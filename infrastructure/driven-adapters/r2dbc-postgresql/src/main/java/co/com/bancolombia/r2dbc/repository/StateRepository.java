package co.com.bancolombia.r2dbc.repository;

import co.com.bancolombia.r2dbc.entity.StateEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface StateRepository extends ReactiveCrudRepository<StateEntity, Long> {
    Mono<StateEntity> findByName(String name);
}
