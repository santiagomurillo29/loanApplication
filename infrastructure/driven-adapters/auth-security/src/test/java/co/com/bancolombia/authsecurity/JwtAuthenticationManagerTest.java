package co.com.bancolombia.authsecurity;

import co.com.bancolombia.authsecurity.jwt.manager.JwtAuthenticationManager;
import co.com.bancolombia.authsecurity.jwt.provider.JwtProvider;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

public class JwtAuthenticationManagerTest {

    private JwtProvider jwtProvider;
    private JwtAuthenticationManager jwtAuthenticationManager;

    @BeforeEach
    void setUp() {
        jwtProvider = Mockito.mock(JwtProvider.class);
        jwtAuthenticationManager = new JwtAuthenticationManager(jwtProvider);
    }

    @Test
    void shouldAuthenticateWithValidToken() {
        String token = "validToken";

        Claims claims = Mockito.mock(Claims.class);
        Mockito.when(claims.getSubject()).thenReturn("testUser");
        Mockito.when(claims.get("roles")).thenReturn(List.of("ADMIN", "USER"));

        Mockito.when(jwtProvider.getClaims(token))
                .thenReturn(Mono.just(claims));

        Authentication auth = new UsernamePasswordAuthenticationToken(null, token);

        StepVerifier.create(jwtAuthenticationManager.authenticate(auth))
                .expectNextMatches(result ->
                        result.getName().equals("testUser") &&
                                result.getAuthorities().size() == 2
                )
                .verifyComplete();
    }


    @Test
    void shouldFailWithInvalidToken() {
        String token = "badToken";

        Mockito.when(jwtProvider.getClaims(token))
                .thenReturn(Mono.error(new BadCredentialsException("Invalid token")));

        Authentication auth = new UsernamePasswordAuthenticationToken(null, token);

        StepVerifier.create(jwtAuthenticationManager.authenticate(auth))
                .expectError(BadCredentialsException.class)
                .verify();
    }
}
