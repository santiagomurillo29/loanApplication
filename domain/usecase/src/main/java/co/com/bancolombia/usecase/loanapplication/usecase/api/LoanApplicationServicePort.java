package co.com.bancolombia.usecase.loanapplication.usecase.api;

import co.com.bancolombia.model.loanapplication.model.LoanApplicationModel;
import co.com.bancolombia.model.loanapplication.model.LoanApplicationPendingModel;
import co.com.bancolombia.model.loanapplication.model.page.PageLoanApplicationModel;
import reactor.core.publisher.Mono;

import java.util.List;

public interface LoanApplicationServicePort {
    Mono<LoanApplicationModel> createLoanApplication(LoanApplicationModel loanApplicationModel, String token);
    Mono<LoanApplicationModel> updateLoanApplication(Long idLoanApplication, String stateName, String token);
    Mono<LoanApplicationModel> calculateCapacityLoanApplication(Long idLoanApplication, String token);
    Mono<PageLoanApplicationModel<LoanApplicationPendingModel>> findLoanApplicationPending(int page, int size, List<String> state, String token);
}
