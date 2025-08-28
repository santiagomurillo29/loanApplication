package co.com.bancolombia.usecase.loanapplication.usecase.api;

import co.com.bancolombia.model.loanapplication.model.LoanApplicationModel;
import reactor.core.publisher.Mono;

public interface LoanApplicationServicePort {
    Mono<LoanApplicationModel> createLoanApplication(LoanApplicationModel loanApplicationModel);
}
