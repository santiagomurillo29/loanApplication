package co.com.bancolombia.r2dbc.adapter;

import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationPersistencePort;
import co.com.bancolombia.model.loanapplication.model.LoanApplicationModel;
import co.com.bancolombia.model.loanapplication.model.LoanTypeModel;
import co.com.bancolombia.model.loanapplication.model.StateModel;
import co.com.bancolombia.model.loanapplication.model.page.PageLoanApplicationModel;
import co.com.bancolombia.r2dbc.health.R2dbcSafeExecutor;
import co.com.bancolombia.r2dbc.mapper.LoanApplicationMapperR2dbc;
import co.com.bancolombia.r2dbc.mapper.LoanTypeMapperR2dbc;
import co.com.bancolombia.r2dbc.mapper.StateMapperR2dbc;
import co.com.bancolombia.r2dbc.repository.LoanApplicationRepository;
import co.com.bancolombia.r2dbc.repository.LoanApplicationRepositoryCustom;
import co.com.bancolombia.r2dbc.repository.LoanTypeRepository;
import co.com.bancolombia.r2dbc.repository.StateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class LoanApplicationAdapterR2dbc implements LoanApplicationPersistencePort {

    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanApplicationRepositoryCustom loanApplicationRepositoryCustom;
    private final LoanApplicationMapperR2dbc loanApplicationMapperR2dbc;
    private final R2dbcSafeExecutor r2dbcSafeExecutor;
    private final LoanTypeAdapterR2dbc loanTypeAdapterR2dbc;
    private final StateAdapterR2dbc stateAdapterR2dbc;

    @Override
    public Mono<LoanApplicationModel> saveLoanApplication(LoanApplicationModel loanApplicationModel) {
        return r2dbcSafeExecutor.executeMono(() ->
                loanApplicationRepository.save(loanApplicationMapperR2dbc.toEntityLoanApplication(loanApplicationModel))
                        .doOnSubscribe(sub -> log.info("Saving loan application: {}", loanApplicationModel))
                        .map(loanApplicationMapperR2dbc::toModelLoanApplication)
                        .flatMap(saved ->
                                Mono.zip(
                                                loanTypeAdapterR2dbc.findLoanTypeById(saved.getLoanType().getIdLoanType()),
                                                stateAdapterR2dbc.findStateById(saved.getState().getIdState())
                                        )
                                        .map(tuple -> {
                                            saved.setLoanType(tuple.getT1());
                                            saved.setState(tuple.getT2());
                                            return saved;
                                        })
                        )
                        .doOnSuccess(saved -> log.info("Loan application saved successfully: {}", saved))
                        .doOnError(e -> log.error("Error saving loan application: {}", e.getMessage()))
        );
    }

    @Override
    public Mono<LoanApplicationModel> findLoanApplicationById(Long idLoanApplication) {
        return r2dbcSafeExecutor.executeMono(() ->
                loanApplicationRepository.findById(idLoanApplication)
                        .doOnSubscribe(sub -> log.info("Finding loan application with id loan application"))
                        .map(loanApplicationMapperR2dbc::toModelLoanApplication)
                        .doOnError(e -> log.error("Error finding loan application by id: {}", e.getMessage()))
        );
    }


    @Override
    public Mono<PageLoanApplicationModel<LoanApplicationModel>> findLoanApplicationsByStates(int page, int size, List<String> states) {
        Flux<LoanApplicationModel> rows = loanApplicationRepositoryCustom.findByStatesPaged(states, page, size);
        Mono<Long> totalMono = loanApplicationRepositoryCustom.countByStates(states);

        return rows
                .collectList()
                .zipWith(totalMono)
                .map(tuple -> {
                    List<LoanApplicationModel> content = tuple.getT1();
                    long total = tuple.getT2();
                    int totalPages = (int) Math.ceil((double) total / size);
                    return new PageLoanApplicationModel<>(content, page, size, totalPages, total);
                });
    }
}
