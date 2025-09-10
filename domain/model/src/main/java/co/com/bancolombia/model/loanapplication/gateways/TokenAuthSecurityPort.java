package co.com.bancolombia.model.loanapplication.gateways;

import reactor.core.publisher.Mono;

public interface TokenAuthSecurityPort {
    Mono<String> getSubject(String token);
}
