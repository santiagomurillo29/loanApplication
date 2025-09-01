package co.com.bancolombia.r2dbc.adapter;

import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationPersistencePort;
import co.com.bancolombia.model.loanapplication.model.LoanApplicationModel;
import co.com.bancolombia.model.loanapplication.model.LoanTypeModel;
import co.com.bancolombia.model.loanapplication.model.StateModel;
import co.com.bancolombia.r2dbc.health.R2dbcSafeExecutor;
import co.com.bancolombia.r2dbc.mapper.LoanApplicationMapperR2dbc;
import co.com.bancolombia.r2dbc.mapper.LoanTypeMapperR2dbc;
import co.com.bancolombia.r2dbc.mapper.StateMapperR2dbc;
import co.com.bancolombia.r2dbc.repository.LoanApplicationRepository;
import co.com.bancolombia.r2dbc.repository.LoanTypeRepository;
import co.com.bancolombia.r2dbc.repository.StateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Repository
public class LoanApplicationAdapterR2dbc implements LoanApplicationPersistencePort {

    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final StateRepository stateRepository;
    private final LoanApplicationMapperR2dbc loanApplicationMapperR2dbc;
    private final LoanTypeMapperR2dbc loanTypeMapperR2dbc;
    private final StateMapperR2dbc stateMapperR2dbc;
    private final R2dbcSafeExecutor r2dbcSafeExecutor;

    @Override
    public Mono<LoanApplicationModel> saveLoanApplication(LoanApplicationModel loanApplicationModel) {
        return r2dbcSafeExecutor.executeMono(() ->
                loanApplicationRepository.save(loanApplicationMapperR2dbc.toEntityLoanApplication(loanApplicationModel))
                        .doOnSubscribe(sub -> log.info("Saving loan application: {}", loanApplicationModel))
                        .map(loanApplicationMapperR2dbc::toModelLoanApplication)
                        .flatMap(saved ->
                                Mono.zip(
                                                findLoanTypeById(saved.getLoanType().getIdLoanType()),
                                                findStateByName(saved.getState().getName() != null ? saved.getState().getName() : "PENDING")
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
    public Mono<LoanTypeModel> findLoanTypeById(Long idLoanType) {
        return r2dbcSafeExecutor.executeMono(() ->
                loanTypeRepository.findById(idLoanType)
                        .doOnSubscribe(sub -> log.info("Checking find of id: {}", idLoanType))
                        .map(loanTypeMapperR2dbc::toModelLoanType)
                        .doOnSuccess(found -> log.info("The loan type exists: {}", found))
                        .doOnError(e -> log.error("Error finding loan type by id {}: {}", idLoanType, e.getMessage()))
        );
    }


    @Override
    public Mono<StateModel> findStateByName(String name) {
        return r2dbcSafeExecutor.executeMono(() ->
                stateRepository.findByName(name)
                        .doOnSubscribe(sub -> log.info("Checking existence of name: {}", name))
                        .map(stateMapperR2dbc::toModelState)
                        .doOnSuccess(found -> log.info("The name does exist: {}", found))
                        .doOnError(e -> log.error("Error checking if the state exists by name {}: {}", name, e.getMessage()))
        );
    }
}
