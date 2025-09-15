package co.com.bancolombia.r2dbc.adapter;

import co.com.bancolombia.model.loanapplication.gateways.LoanTypePersistencePort;
import co.com.bancolombia.model.loanapplication.model.LoanTypeModel;
import co.com.bancolombia.r2dbc.health.R2dbcSafeExecutor;
import co.com.bancolombia.r2dbc.mapper.LoanTypeMapperR2dbc;
import co.com.bancolombia.r2dbc.repository.LoanTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Repository
public class LoanTypeAdapterR2dbc implements LoanTypePersistencePort {

    private final LoanTypeRepository loanTypeRepository;
    private final LoanTypeMapperR2dbc loanTypeMapperR2dbc;
    private final R2dbcSafeExecutor r2dbcSafeExecutor;

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
}
