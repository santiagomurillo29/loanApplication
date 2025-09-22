package co.com.bancolombia.reactiveweb;

import co.com.bancolombia.MainApplication;
import co.com.bancolombia.authsecurity.jwt.provider.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@AutoConfigureWebTestClient
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {MainApplication.class, JwtIntegrationsTest.TestRouterConfig.class},
        properties = {
                "jwt.secret=mysuperlongsecretkeythatismorethan32bytes123!",
        }
)
class JwtIntegrationsTest {

    @TestConfiguration
    static class TestRouterConfig {
        @Bean
        public RouterFunction<ServerResponse> testRoutes() {
            return RouterFunctions.route()
                    .GET("/api/test-protected",
                            request -> ServerResponse.ok().bodyValue("Hello, integrationUser"))
                    .build();
        }
    }

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        given(jwtProvider.validate("validToken")).willReturn(Mono.just(true));
        given(jwtProvider.validate("invalidToken")).willReturn(Mono.just(false));
        given(jwtProvider.getSubject(anyString())).willReturn(Mono.just("integrationUser"));
        given(jwtProvider.getClaims(anyString())).willReturn(Mono.empty());
    }


    @Test
    void shouldReturnUnauthorizedWhenNoToken() {
        webTestClient.get()
                .uri("/api/test-protected")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenInvalid() {
        webTestClient.get()
                .uri("/api/test-protected")
                .header(HttpHeaders.AUTHORIZATION, "Bearer invalidToken")
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
