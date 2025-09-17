package co.com.bancolombia.usecase.loanapplication;

import co.com.bancolombia.model.loanapplication.gateways.AuthenticationClientPersistencePort;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationPersistencePort;
import co.com.bancolombia.model.loanapplication.gateways.LoanTypePersistencePort;
import co.com.bancolombia.model.loanapplication.gateways.SQSSenderPersistencePort;
import co.com.bancolombia.model.loanapplication.gateways.StatePersistencePort;
import co.com.bancolombia.model.loanapplication.gateways.TokenAuthSecurityPort;
import co.com.bancolombia.model.loanapplication.globalmessage.GlobalMessage;
import co.com.bancolombia.model.loanapplication.model.LoanApplicationModel;
import co.com.bancolombia.model.loanapplication.model.LoanApplicationPendingModel;
import co.com.bancolombia.model.loanapplication.model.LoanTypeModel;
import co.com.bancolombia.model.loanapplication.model.StateModel;
import co.com.bancolombia.model.loanapplication.model.page.PageLoanApplicationModel;
import co.com.bancolombia.model.loanapplication.model.restconsumer.UserResponse;
import co.com.bancolombia.model.loanapplication.model.sqs.LoanApplicationPayment;
import co.com.bancolombia.model.loanapplication.model.sqs.LoanApplicationState;
import co.com.bancolombia.usecase.loanapplication.exception.BusinessException;
import co.com.bancolombia.usecase.loanapplication.usecase.LoanApplicationUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class LoanApplicationUseCaseTest {

    @Mock
    LoanApplicationPersistencePort loanApplicationPersistencePort;

    @Mock
    AuthenticationClientPersistencePort authenticationClientPersistencePort;

    @Mock
    TokenAuthSecurityPort tokenAuthSecurityPort;

    @Mock
    StatePersistencePort statePersistencePort;

    @Mock
    LoanTypePersistencePort loanTypePersistencePort;

    @Mock
    SQSSenderPersistencePort sqsSenderPersistencePort;

    @InjectMocks
    LoanApplicationUseCase useCase;

    private final String token = "anyToken";

    @Test
    void createLoanApplication_whenExists_thenSave() {
        LoanTypeModel loanTypeModel = new LoanTypeModel(1L, "Personal Loan", BigDecimal.valueOf(5000.00), BigDecimal.valueOf(500000.00), BigDecimal.valueOf(12.50), true);
        StateModel stateModel = new StateModel("Description", "PENDING", 1L);
        LoanApplicationModel model = new LoanApplicationModel(1L, BigDecimal.valueOf(30000.00), 24, "user@gmail.com", null, loanTypeModel);

        given(tokenAuthSecurityPort.getSubject(token)).willReturn(Mono.just("user@gmail.com"));
        given(authenticationClientPersistencePort.validateEmailExists(model.getEmail(), token)).willReturn(Mono.just(true));
        given(loanTypePersistencePort.findLoanTypeById(1L)).willReturn(Mono.just(loanTypeModel));
        given(statePersistencePort.findStateByName("PENDING")).willReturn(Mono.just(stateModel));
        given(loanApplicationPersistencePort.saveLoanApplication(model)).willReturn(Mono.just(model));

        StepVerifier.create(useCase.createLoanApplication(model, token))
                .expectNext(model)
                .verifyComplete();
    }

    @Test
    void createLoanApplication_whenNotExists_thenSave() {
        LoanTypeModel loanTypeModel = new LoanTypeModel(1L, "Personal Loan", BigDecimal.valueOf(5000.00), BigDecimal.valueOf(500000.00), BigDecimal.valueOf(12.50), true);
        LoanApplicationModel model = new LoanApplicationModel(1L, BigDecimal.valueOf(30000.00), 24, "user@gmail.com", null, loanTypeModel);

        given(tokenAuthSecurityPort.getSubject(token)).willReturn(Mono.just("user@gmail.com"));
        given(authenticationClientPersistencePort.validateEmailExists(model.getEmail(), token)).willReturn(Mono.just(false));

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
        LoanTypeModel loanTypeModel = new LoanTypeModel(1L, "Personal Loan", BigDecimal.valueOf(5000.00), BigDecimal.valueOf(500000.00), BigDecimal.valueOf(12.50), true);
        StateModel stateModel = new StateModel("Description", "PENDING", 1L);
        LoanApplicationModel loanApp = new LoanApplicationModel(1L, BigDecimal.valueOf(24000.00), 12, "user@gmail.com", stateModel, loanTypeModel);

        PageLoanApplicationModel<LoanApplicationModel> pageLoanApplicationModel = new PageLoanApplicationModel<>(List.of(loanApp), 0, 10, 1, 1);
        UserResponse userResponse = new UserResponse(1L, "John", "Doe", "123456", null, "Street 1", "123456789", "user@gmail.com", BigDecimal.valueOf(5000));

        given(tokenAuthSecurityPort.getSubject(token)).willReturn(Mono.just("user@gmail.com"));
        given(loanApplicationPersistencePort.findLoanApplicationsByStates(0, 10, List.of("PENDING"))).willReturn(Mono.just(pageLoanApplicationModel));
        given(authenticationClientPersistencePort.getUserByEmail("user@gmail.com", token)).willReturn(Mono.just(userResponse));

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

    @Test
    void updateLoanApplication_whenFound_thenUpdateAndSendSqs() {
        Long idLoanApplication = 1L;
        String stateName = "APPROVED";

        StateModel newState = new StateModel("desc", stateName, 2L);
        StateModel oldState = new StateModel("desc", "PENDING", 1L);
        LoanApplicationModel existing = new LoanApplicationModel(idLoanApplication, BigDecimal.valueOf(20000.00), 12, "user@gmail.com", oldState, new LoanTypeModel(1L, "Personal Loan", BigDecimal.ONE, BigDecimal.TEN, BigDecimal.ONE, true));
        LoanApplicationModel updated = new LoanApplicationModel(idLoanApplication, existing.getAmount(), existing.getTerm(), existing.getEmail(), newState, existing.getLoanType());

        given(tokenAuthSecurityPort.getSubject(token)).willReturn(Mono.just("user@gmail.com"));
        given(statePersistencePort.findStateByName(stateName)).willReturn(Mono.just(newState));
        given(loanApplicationPersistencePort.findLoanApplicationById(idLoanApplication)).willReturn(Mono.just(existing));
        given(loanApplicationPersistencePort.saveLoanApplication(any())).willReturn(Mono.just(updated));
        given(sqsSenderPersistencePort.sendState(any(LoanApplicationState.class))).willReturn(Mono.empty());

        StepVerifier.create(useCase.updateLoanApplication(idLoanApplication, stateName, token))
                .expectNextMatches(saved -> saved.getState().getName().equals("APPROVED"))
                .verifyComplete();
    }

    @Test
    void findLoanApplicationPending_whenFound_thenReturnPageWithPendingModels() {
        int page = 0, size = 2;
        List<String> states = List.of("PENDING");
        String token = "anyToken";

        PageLoanApplicationModel<LoanApplicationModel> pageModel = getLoanApplicationModelPageLoanApplicationModel(page, size);

        UserResponse user1 = new UserResponse(1L, "User One", "Last", "123", null, "addr", "phone", "user1@gmail.com", BigDecimal.valueOf(3000));
        UserResponse user2 = new UserResponse(2L, "User Two", "Last", "456", null, "addr", "phone", "user2@gmail.com", BigDecimal.valueOf(2000));

        given(tokenAuthSecurityPort.getSubject(token)).willReturn(Mono.just("user@gmail.com"));
        given(loanApplicationPersistencePort.findLoanApplicationsByStates(page, size, states)).willReturn(Mono.just(pageModel));
        given(authenticationClientPersistencePort.getUserByEmail("user1@gmail.com", token)).willReturn(Mono.just(user1));
        given(authenticationClientPersistencePort.getUserByEmail("user2@gmail.com", token)).willReturn(Mono.just(user2));

        StepVerifier.create(useCase.findLoanApplicationPending(page, size, states, token))
                .assertNext(result -> {
                    assertEquals(2, result.getContent().size());

                    LoanApplicationPendingModel pending1 = result.getContent().get(0);
                    LoanApplicationPendingModel pending2 = result.getContent().get(1);

                    assertEquals("User One", pending1.getUserName());
                    assertEquals("User Two", pending2.getUserName());
                    assertEquals(BigDecimal.valueOf(1000), pending1.getMonthlyAmount());
                    assertEquals(BigDecimal.valueOf(1000), pending2.getMonthlyAmount());
                })
                .verifyComplete();
    }

    @Test
    void calculateCapacityLoanApplication_whenValid_thenSaveAndSend() {
        Long idLoan = 1L;
        String email = "user@gmail.com";

        LoanTypeModel loanType = new LoanTypeModel(1L, "Personal Loan", BigDecimal.ONE, BigDecimal.TEN, BigDecimal.valueOf(12.5), true);
        StateModel stateModel = new StateModel("desc", "APPROVED", 2L);
        LoanApplicationModel loanApp = new LoanApplicationModel(idLoan, BigDecimal.valueOf(10000), 12, email, null, loanType);
        UserResponse user = new UserResponse(1L, "John", "Doe", "doc", null, "addr", "phone", email, BigDecimal.valueOf(5000));
        LoanApplicationModel savedLoan = new LoanApplicationModel(idLoan, loanApp.getAmount(), loanApp.getTerm(), email, stateModel, loanType);

        given(tokenAuthSecurityPort.getSubject(token)).willReturn(Mono.just(email));
        given(loanApplicationPersistencePort.findLoanApplicationById(idLoan)).willReturn(Mono.just(loanApp));
        given(loanTypePersistencePort.findLoanTypeById(loanType.getIdLoanType())).willReturn(Mono.just(loanType));
        given(authenticationClientPersistencePort.getUserByEmail(email, token)).willReturn(Mono.just(user));
        given(loanApplicationPersistencePort.findApprovedLoansByEmail(email)).willReturn(Flux.empty());
        given(statePersistencePort.findStateByName(anyString())).willReturn(Mono.just(stateModel));
        given(loanApplicationPersistencePort.saveLoanApplication(any())).willReturn(Mono.just(savedLoan));
        given(sqsSenderPersistencePort.sendPayment(any(LoanApplicationPayment.class))).willReturn(Mono.empty());

        StepVerifier.create(useCase.calculateCapacityLoanApplication(idLoan, token))
                .expectNextMatches(result -> result.getState().getName().equals("APPROVED"))
                .verifyComplete();
    }

    private static PageLoanApplicationModel<LoanApplicationModel> getLoanApplicationModelPageLoanApplicationModel(int page, int size) {
        LoanApplicationModel app1 = new LoanApplicationModel(1L, BigDecimal.valueOf(12000), 12, "user1@gmail.com", null, new LoanTypeModel(1L, "LoanType", BigDecimal.ONE, BigDecimal.TEN, BigDecimal.ONE, true));
        LoanApplicationModel app2 = new LoanApplicationModel(2L, BigDecimal.valueOf(6000), 6, "user2@gmail.com", null, new LoanTypeModel(2L, "LoanType2", BigDecimal.ONE, BigDecimal.TEN, BigDecimal.ONE, true));
        return new PageLoanApplicationModel<>(List.of(app1, app2), page, size, 1, 2L);
    }
}
