package co.com.bancolombia.consumer.anthenticationclient;

import co.com.bancolombia.consumer.anthenticationclient.model.UserResponse;
import co.com.bancolombia.consumer.exception.WebClientException;
import co.com.bancolombia.model.loanapplication.gateways.AuthenticationClientPersistencePort;
import co.com.bancolombia.model.loanapplication.globalmessage.GlobalMessage;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Setter
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
                        response -> Mono.error(new WebClientException(GlobalMessage.NOT_FOUND_EMAIL)))
                .onStatus(status -> status == HttpStatus.INTERNAL_SERVER_ERROR,
                        response -> Mono.error(new WebClientException(GlobalMessage.INTERNAL_ERROR)))
                .bodyToMono(UserResponse.class)
                .map(user -> true)
                .onErrorMap(WebClientRequestException.class,
                        ex -> new WebClientException(GlobalMessage.MICROSERVICE_DOWN)
                );
    }

}
