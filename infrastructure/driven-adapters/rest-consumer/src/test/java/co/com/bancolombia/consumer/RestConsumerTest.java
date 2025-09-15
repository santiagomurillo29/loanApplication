package co.com.bancolombia.consumer;

import co.com.bancolombia.consumer.anthenticationclient.AuthenticationClient;
import co.com.bancolombia.consumer.anthenticationclient.mapper.RestConsumerMapper;
import co.com.bancolombia.consumer.anthenticationclient.model.UserResponseDto;
import co.com.bancolombia.consumer.exception.WebClientException;
import co.com.bancolombia.model.loanapplication.globalmessage.GlobalMessage;
import co.com.bancolombia.model.loanapplication.model.restconsumer.UserResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;
import java.io.IOException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@Execution(ExecutionMode.SAME_THREAD)
class RestConsumerTest {

    private  MockWebServer mockBackEnd;
    private AuthenticationClient authenticationClient;

    @BeforeEach
    void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();

        RestConsumerMapper restConsumerMapper = Mockito.mock(RestConsumerMapper.class);
        Mockito.when(restConsumerMapper.toDomain(any(UserResponseDto.class)))
                .thenReturn(new UserResponse(1L, "Juan", "test@mail.com", null, null, null, null, null, null));

        authenticationClient = new AuthenticationClient(WebClient.builder(), restConsumerMapper);
        authenticationClient.setAuthBaseUrl(mockBackEnd.url("/").toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    @DisplayName("Debe retornar true cuando el email existe (200)")
    void validateEmailExistsTrue() {
        String body = "{ \"idUser\": 1, \"nameUser\": \"Juan\", \"emailUser\": \"test@mail.com\" }";

        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(body));

        StepVerifier.create(authenticationClient.validateEmailExists("user@gmail.com", "token"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe lanzar excepción NOT_FOUND_EMAIL cuando el email no existe (404)")
    void validateEmailExistsNotFound() {
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.NOT_FOUND.value()));

        StepVerifier.create(authenticationClient.validateEmailExists("notfound@gmail.com", "token"))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(WebClientException.class);
                    assertThat(((WebClientException) error).getMessage())
                            .isEqualTo(GlobalMessage.NOT_FOUND_EMAIL.getMessage());
                })
                .verify();
    }

    @Test
    @DisplayName("Debe lanzar excepción INTERNAL_ERROR cuando el servidor responde con 500")
    void validateEmailExistsInternalError() {
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));

        StepVerifier.create(authenticationClient.validateEmailExists("error@gmail.com", "token"))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(WebClientException.class);
                    assertThat(((WebClientException) error).getMessage())
                            .isEqualTo(GlobalMessage.INTERNAL_ERROR.getMessage());
                })
                .verify();
    }

    @Test
    @DisplayName("Debe lanzar excepción MICROSERVICE_DOWN cuando el microservicio no está disponible")
    void validateEmailExistsMicroserviceDown() throws IOException {
        mockBackEnd.shutdown();

        StepVerifier.create(authenticationClient.validateEmailExists("down@gmail.com", "token"))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(WebClientException.class);
                    assertThat(((WebClientException) error).getMessage())
                            .isEqualTo(GlobalMessage.MICROSERVICE_DOWN.getMessage());
                })
                .verify();
    }
}