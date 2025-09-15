package co.com.bancolombia.model.loanapplication.gateways;

import co.com.bancolombia.model.loanapplication.model.LoanTypeModel;
import reactor.core.publisher.Mono;

public interface LoanTypePersistencePort {
    Mono<LoanTypeModel> findLoanTypeById(Long idLoanType);
}
