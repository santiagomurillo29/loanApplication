package co.com.bancolombia.usecase.loanapplication;

import co.com.bancolombia.model.loanapplication.gateways.AuthenticationClientPersistencePort;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationPersistencePort;
import co.com.bancolombia.model.loanapplication.gateways.TokenAuthSecurityPort;
import co.com.bancolombia.model.loanapplication.globalmessage.GlobalMessage;
import co.com.bancolombia.model.loanapplication.model.LoanApplicationModel;
import co.com.bancolombia.model.loanapplication.model.LoanTypeModel;
import co.com.bancolombia.model.loanapplication.model.StateModel;
import co.com.bancolombia.model.loanapplication.model.page.PageLoanApplicationModel;
import co.com.bancolombia.model.loanapplication.model.restconsumer.UserResponse;
import co.com.bancolombia.usecase.loanapplication.exception.BusinessException;
import co.com.bancolombia.usecase.loanapplication.usecase.LoanApplicationUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class LoanApplicationUseCaseTest {

    @Mock
    LoanApplicationPersistencePort repo;

    @Mock
    AuthenticationClientPersistencePort repoAuthentication;

    @Mock
    TokenAuthSecurityPort tokenAuthSecurityPort;

    @InjectMocks
    LoanApplicationUseCase useCase;

    private final String token = "anyToken";

    @Test
    void createLoanApplication_whenExists_thenSave() {
        LoanTypeModel loanTypeModel = new LoanTypeModel(1L, "Personal Loan", BigDecimal.valueOf(5000.00), BigDecimal.valueOf(500000.00), BigDecimal.valueOf(12.50), true);
        StateModel stateModel = new StateModel("Description", "PENDING", 1L);
        LoanApplicationModel model = new LoanApplicationModel(1L, BigDecimal.valueOf(30000.00), 24, "user@gmail.com", null, loanTypeModel);

        given(tokenAuthSecurityPort.getSubject(token)).willReturn(Mono.just("user@gmail.com"));
        given(repoAuthentication.validateEmailExists(model.getEmail(), token)).willReturn(Mono.just(true));
        given(repo.findLoanTypeById(1L)).willReturn(Mono.just(loanTypeModel));
        given(repo.findStateByName("PENDING")).willReturn(Mono.just(stateModel));
        given(repo.saveLoanApplication(model)).willReturn(Mono.just(model));

        StepVerifier.create(useCase.createLoanApplication(model, token))
                .expectNext(model)
                .verifyComplete();
    }

    @Test
    void createLoanApplication_whenNotExists_thenSave() {
        LoanTypeModel loanTypeModel = new LoanTypeModel(1L, "Personal Loan", BigDecimal.valueOf(5000.00), BigDecimal.valueOf(500000.00), BigDecimal.valueOf(12.50), true);
        LoanApplicationModel model = new LoanApplicationModel(1L, BigDecimal.valueOf(30000.00), 24, "user@gmail.com", null, loanTypeModel);

        given(tokenAuthSecurityPort.getSubject(token)).willReturn(Mono.just("user@gmail.com"));
        given(repoAuthentication.validateEmailExists(model.getEmail(), token)).willReturn(Mono.just(false));

        StepVerifier.create(useCase.createLoanApplication(model, token))
                .expectErrorSatisfies(error -> { assertInstanceOf(BusinessException.class, error);
                    assertEquals(GlobalMessage.NOT_FOUND_EMAIL.getMessage(), error.getMessage());
                })
                .verify();
    }

    @Test
    void createLoanApplication_whenEmailMismatch_thenThrow() {
        LoanTypeModel loanTypeModel = new LoanTypeModel(1L, "Personal Loan", BigDecimal.valueOf(5000.00), BigDecimal.valueOf(500000.00), BigDecimal.valueOf(12.50), true);
        LoanApplicationModel model = new LoanApplicationModel(1L, BigDecimal.valueOf(30000.00), 24, "otheruser@gmail.com", null, loanTypeModel);

        given(tokenAuthSecurityPort.getSubject(token)).willReturn(Mono.just("user@gmail.com"));

        StepVerifier.create(useCase.createLoanApplication(model, token))
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(BusinessException.class, error);
                    assertEquals(GlobalMessage.EMAIL_NOT_MATCH.getMessage(), error.getMessage());
                })
                .verify();
    }

    @Test
    void findLoanApplicationPending_whenExists_thenReturnPage() {
        LoanTypeModel loanTypeModel = new LoanTypeModel(
                1L, "Personal Loan", BigDecimal.valueOf(5000.00),
                BigDecimal.valueOf(500000.00), BigDecimal.valueOf(12.50), true
        );
        StateModel stateModel = new StateModel("Description", "PENDING", 1L);
        LoanApplicationModel loanApp = new LoanApplicationModel(
                1L, BigDecimal.valueOf(24000.00), 12, "user@gmail.com", stateModel, loanTypeModel
        );

        PageLoanApplicationModel<LoanApplicationModel> pageLoanApplicationModel =
                new PageLoanApplicationModel<>(List.of(loanApp), 0, 10, 1, 1);

        UserResponse userResponse = new UserResponse(
                1L, "John", "Doe", "123456", null,
                "Street 1", "123456789", "user@gmail.com",
                BigDecimal.valueOf(5000)
        );

        given(tokenAuthSecurityPort.getSubject(token)).willReturn(Mono.just("user@gmail.com"));
        given(repo.findLoanApplicationsByStates(0, 10, List.of("PENDING")))
                .willReturn(Mono.just(pageLoanApplicationModel));
        given(repoAuthentication.getUserByEmail("user@gmail.com", token))
                .willReturn(Mono.just(userResponse));

        StepVerifier.create(useCase.findLoanApplicationPending(0, 10, List.of("PENDING"), token))
                .assertNext(result -> {
                    assertEquals(1, result.getContent().size());
                    assertEquals(0, result.getCurrentPage());
                    assertEquals(10, result.getPageSize());
                    assertEquals(1, result.getTotalPages());
                    assertEquals(1, result.getTotalElements());

                    var pending = result.getContent().get(0);
                    assertEquals("John", pending.getUserName());
                    assertEquals(0, pending.getMonthlyAmount().compareTo(BigDecimal.valueOf(2000)));
                    assertEquals(BigDecimal.valueOf(5000), pending.getBaseSalaryUser());
                    assertEquals("user@gmail.com", pending.getLoanApplication().getEmail());

                })
                .verifyComplete();
    }
}
