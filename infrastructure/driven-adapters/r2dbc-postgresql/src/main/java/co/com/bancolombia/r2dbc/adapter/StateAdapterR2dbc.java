package co.com.bancolombia.r2dbc.adapter;

import co.com.bancolombia.model.loanapplication.gateways.StatePersistencePort;
import co.com.bancolombia.model.loanapplication.model.StateModel;
import co.com.bancolombia.r2dbc.health.R2dbcSafeExecutor;
import co.com.bancolombia.r2dbc.mapper.StateMapperR2dbc;
import co.com.bancolombia.r2dbc.repository.StateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Repository
public class StateAdapterR2dbc implements StatePersistencePort {

    private final StateRepository stateRepository;
    private final StateMapperR2dbc stateMapperR2dbc;
    private final R2dbcSafeExecutor r2dbcSafeExecutor;

    @Override
    public Mono<StateModel> findStateByName(String name) {
        return  r2dbcSafeExecutor.executeMono(() ->
                stateRepository.findByName(name)
                        .doOnSubscribe(sub -> log.info("Checking existence of name: {}", name))
                        .map(stateMapperR2dbc::toModelState)
                        .doOnSuccess(found -> log.info("The name does exist: {}", found))
                        .doOnError(e -> log.error("Error checking if the state exists by name {}: {}", name, e.getMessage()))
        );
    }

    @Override
    public Mono<StateModel> findStateById(Long id) {
        return  r2dbcSafeExecutor.executeMono(() ->
                stateRepository.findById(id)
                        .doOnSubscribe(sub -> log.info("Checking existence of id: {}", id))
                        .map(stateMapperR2dbc::toModelState)
                        .doOnSuccess(found -> log.info("The id does exist: {}", found))
                        .doOnError(e -> log.error("Error checking if the state exists by id {}: {}", id, e.getMessage()))
        );
    }
}
