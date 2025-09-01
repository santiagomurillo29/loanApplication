package co.com.bancolombia.usecase.loanapplication.usecase;

import co.com.bancolombia.model.loanapplication.gateways.AuthenticationClientPersistencePort;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationPersistencePort;
import co.com.bancolombia.model.loanapplication.globalmessage.GlobalMessage;
import co.com.bancolombia.model.loanapplication.model.LoanApplicationModel;
import co.com.bancolombia.model.loanapplication.model.StateModel;
import co.com.bancolombia.usecase.loanapplication.exception.BusinessException;
import co.com.bancolombia.usecase.loanapplication.usecase.api.LoanApplicationServicePort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoanApplicationUseCase implements LoanApplicationServicePort {

    private final LoanApplicationPersistencePort loanApplicationPersistencePort;
    private final AuthenticationClientPersistencePort authenticationClientPersistencePort;

    @Override
    public Mono<LoanApplicationModel> createLoanApplication(LoanApplicationModel loanApplicationModel) {
        return authenticationClientPersistencePort.validateEmailExists(loanApplicationModel.getEmail())
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new BusinessException(GlobalMessage.NOT_FOUND_EMAIL));
                    }
                    return loanApplicationPersistencePort.findLoanTypeById(loanApplicationModel.getLoanType().getIdLoanType());
                })
                .flatMap(loanTypeModel -> {
                    loanApplicationModel.setLoanType(loanTypeModel);
                    return loanApplicationPersistencePort.findStateByName("PENDING")
                            .switchIfEmpty(Mono.error(new BusinessException(GlobalMessage.NOT_FOUND_STATE)));
                })
                .flatMap(state -> {
                    loanApplicationModel.setState(state);
                    return loanApplicationPersistencePort.saveLoanApplication(loanApplicationModel);
                });
    }
}
