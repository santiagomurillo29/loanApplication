package co.com.bancolombia.model.loanapplication.gateways;

import co.com.bancolombia.model.loanapplication.model.LoanApplicationModel;
import co.com.bancolombia.model.loanapplication.model.LoanTypeModel;
import co.com.bancolombia.model.loanapplication.model.page.PageLoanApplicationModel;
import co.com.bancolombia.model.loanapplication.model.StateModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface LoanApplicationPersistencePort {
    Mono<LoanApplicationModel> saveLoanApplication(LoanApplicationModel loanApplicationModel);
    Mono<LoanApplicationModel> findLoanApplicationById(Long idLoanApplication);
    Mono<PageLoanApplicationModel<LoanApplicationModel>> findLoanApplicationsByStates(int page, int size, List<String> states);
    Flux<LoanApplicationModel> findApprovedLoansByEmail(String emailUser);
}
