package co.com.bancolombia.r2dbc.anthenticationclient;

import co.com.bancolombia.model.loanapplication.gateways.AuthenticationClientPersistencePort;
import co.com.bancolombia.model.loanapplication.globalmessage.GlobalMessage;
import co.com.bancolombia.r2dbc.anthenticationclient.model.UserResponse;
import co.com.bancolombia.usecase.loanapplication.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthenticationClient implements AuthenticationClientPersistencePort {

    private final WebClient.Builder webClientBuilder;

    @Value("${services.authentication.url}")
    private String authBaseUrl;

    public Mono<Boolean> validateEmailExists(String email) {
        return webClientBuilder.build()
                .get()
                .uri(authBaseUrl + "/api/v1/usuario/correo?emailUser=" + email)
                .retrieve()
                .onStatus(status -> status == HttpStatus.NOT_FOUND,
                        response -> Mono.error(new BusinessException(GlobalMessage.NOT_FOUND_EMAIL))
                )
                .bodyToMono(UserResponse.class)
                .map(user -> true)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                        return Mono.just(false);
                    }
                    return Mono.error(ex);
                });
    }
}
