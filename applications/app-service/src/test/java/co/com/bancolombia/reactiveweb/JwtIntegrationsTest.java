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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import java.util.List;

@AutoConfigureWebTestClient
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {MainApplication.class, JwtIntegrationsTest.TestRouterConfig.class},
        properties = {
                "jwt.expiration=360000",
                "jwt.secret=mysuperlongsecretkeythatismorethan32bytes123!",
        }
)
public class JwtIntegrationsTest {

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

    @Autowired
    private JwtProvider jwtProvider;

    private String validToken;

    @BeforeEach
    void setUp() {
        UserDetails user = new User(
                "integrationUser",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        validToken = jwtProvider.generateToken(user);
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

    @Test
    void shouldReturnOkWhenTokenValid() {
        webTestClient.get()
                .uri("/api/test-protected")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Hello, integrationUser");
    }
}
