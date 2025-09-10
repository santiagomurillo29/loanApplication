package co.com.bancolombia.consumer.anthenticationclient;

import co.com.bancolombia.consumer.anthenticationclient.mapper.RestConsumerMapper;
import co.com.bancolombia.consumer.anthenticationclient.model.UserResponseDto;
import co.com.bancolombia.consumer.exception.WebClientException;
import co.com.bancolombia.model.loanapplication.gateways.AuthenticationClientPersistencePort;
import co.com.bancolombia.model.loanapplication.globalmessage.GlobalMessage;
import co.com.bancolombia.model.loanapplication.model.restconsumer.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Setter
public class AuthenticationClient implements AuthenticationClientPersistencePort {

    private final WebClient.Builder webClientBuilder;
    private final RestConsumerMapper restConsumerMapper;

    @Value("${services.authentication.url}")
    private String authBaseUrl;

    @Override
    public Mono<UserResponse> getUserByEmail(String email, String token) {
        return webClientBuilder.build()
                .get()
                .uri(authBaseUrl + "/api/v1/usuario/correo?emailUser=" + email)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(status -> status == HttpStatus.NOT_FOUND,
                        response -> Mono.error(new WebClientException(GlobalMessage.NOT_FOUND_EMAIL)))
                .onStatus(status -> status == HttpStatus.INTERNAL_SERVER_ERROR,
                        response -> Mono.error(new WebClientException(GlobalMessage.INTERNAL_ERROR)))
                .bodyToMono(UserResponseDto.class)
                .map(restConsumerMapper::toDomain)
                .onErrorMap(WebClientRequestException.class,
                        ex -> new WebClientException(GlobalMessage.MICROSERVICE_DOWN)
                );
    }

    @Override
    public Mono<Boolean> validateEmailExists(String email, String token) {
        return getUserByEmail(email, token).map(user -> true);
    }
}
