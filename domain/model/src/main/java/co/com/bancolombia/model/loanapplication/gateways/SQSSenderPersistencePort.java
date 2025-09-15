package co.com.bancolombia.model.loanapplication.gateways;

import reactor.core.publisher.Mono;

public interface SQSSenderPersistencePort {
    Mono<String> send(String message);
}
