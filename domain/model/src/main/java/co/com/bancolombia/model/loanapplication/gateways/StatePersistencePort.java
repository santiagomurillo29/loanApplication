package co.com.bancolombia.model.loanapplication.gateways;

import co.com.bancolombia.model.loanapplication.model.StateModel;
import reactor.core.publisher.Mono;

public interface StatePersistencePort {
    Mono<StateModel> findStateByName(String name);
    Mono<StateModel> findStateById(Long id);
}
