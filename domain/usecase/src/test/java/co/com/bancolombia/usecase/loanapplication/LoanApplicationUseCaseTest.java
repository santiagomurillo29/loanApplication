package co.com.bancolombia.usecase.loanapplication;

import co.com.bancolombia.model.loanapplication.gateways.AuthenticationClientPersistencePort;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationPersistencePort;
import co.com.bancolombia.model.loanapplication.globalmessage.GlobalMessage;
import co.com.bancolombia.model.loanapplication.model.LoanApplicationModel;
import co.com.bancolombia.model.loanapplication.model.LoanTypeModel;
import co.com.bancolombia.model.loanapplication.model.StateModel;
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
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class LoanApplicationUseCaseTest {

    @Mock
    LoanApplicationPersistencePort repo;

    @Mock
    AuthenticationClientPersistencePort repoAuthentication;

    @InjectMocks
    LoanApplicationUseCase useCase;

    @Test
    void createLoanApplication_whenExists_thenSave() {
        LoanTypeModel loanTypeModel = new LoanTypeModel(1L, "Personal Loan", BigDecimal.valueOf(5000.00), BigDecimal.valueOf(500000.00), BigDecimal.valueOf(12.50), true);
        StateModel stateModel = new StateModel("Description", "PENDING", 1L);
        LoanApplicationModel model = new LoanApplicationModel(1L, BigDecimal.valueOf(30000.00), 24, "user@gmail.com", null, loanTypeModel);

        given(repoAuthentication.validateEmailExists(model.getEmail())).willReturn(Mono.just(true));
        given(repo.findLoanTypeById(1L)).willReturn(Mono.just(loanTypeModel));
        given(repo.findStateByName(stateModel.getName())).willReturn(Mono.just(stateModel));
        given(repo.saveLoanApplication(model)).willReturn(Mono.just(model));

        StepVerifier.create(useCase.createLoanApplication(model))
                .expectNext(model)
                .verifyComplete();
    }

    @Test
    void createLoanApplication_whenNotExists_thenSave() {
        LoanTypeModel loanTypeModel = new LoanTypeModel(1L, "Personal Loan", BigDecimal.valueOf(5000.00), BigDecimal.valueOf(500000.00), BigDecimal.valueOf(12.50), true);
        LoanApplicationModel model = new LoanApplicationModel(1L, BigDecimal.valueOf(30000.00), 24, "string@gmail.com", null, loanTypeModel);

        given(repoAuthentication.validateEmailExists(model.getEmail())).willReturn(Mono.just(false));

        StepVerifier.create(useCase.createLoanApplication(model))
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(BusinessException.class, error);
                    assertEquals(GlobalMessage.NOT_FOUND_EMAIL.getMessage(), error.getMessage());
                })
                .verify();
    }
}
