package co.com.bancolombia.model.loanapplication.gateways;

import co.com.bancolombia.model.loanapplication.model.LoanApplicationModel;
import co.com.bancolombia.model.loanapplication.model.LoanTypeModel;
import co.com.bancolombia.model.loanapplication.model.StateModel;
import reactor.core.publisher.Mono;

public interface LoanApplicationPersistencePort {
    Mono<LoanApplicationModel> saveLoanApplication(LoanApplicationModel loanApplicationModel);
    Mono<LoanTypeModel> findLoanTypeById(Long idLoanType);
    Mono<StateModel> findStateByName(String name);
}
