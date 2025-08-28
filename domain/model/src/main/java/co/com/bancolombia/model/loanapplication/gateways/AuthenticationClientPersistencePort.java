package co.com.bancolombia.model.loanapplication.gateways;

import reactor.core.publisher.Mono;

public interface AuthenticationClientPersistencePort {
    Mono<Boolean> validateEmailExists (String email);
}
