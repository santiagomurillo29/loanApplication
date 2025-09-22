package co.com.bancolombia.model.loanapplication.gateways;

import co.com.bancolombia.model.loanapplication.model.restconsumer.UserResponse;
import reactor.core.publisher.Mono;

public interface AuthenticationClientPort {
    Mono<UserResponse> getUserByEmail(String email, String token);
    Mono<Boolean> validateEmailExists (String email, String token);
}
