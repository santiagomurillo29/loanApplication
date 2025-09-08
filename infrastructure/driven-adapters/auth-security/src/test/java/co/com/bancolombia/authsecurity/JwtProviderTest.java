package co.com.bancolombia.authsecurity;

import co.com.bancolombia.authsecurity.jwt.provider.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.test.StepVerifier;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JwtProviderTest {

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider();

        ReflectionTestUtils.setField(jwtProvider, "secret", "mySecretKey123456789012345678901234");
        ReflectionTestUtils.setField(jwtProvider, "expiration", 1000 * 60 * 5);
    }

    @Test
    void shouldGenerateAndValidateToken() {
        UserDetails userDetails = new User(
                "testUser",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        String token = jwtProvider.generateToken(userDetails);
        assertNotNull(token);

        StepVerifier.create(jwtProvider.validate(token))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(jwtProvider.getClaims(token))
                .assertNext(claims -> {
                    assertEquals("testUser", claims.getSubject());
                    assertEquals(List.of("ADMIN"), claims.get("roles"));
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnFalseWhenTokenInvalid() {
        StepVerifier.create(jwtProvider.validate("bad-token"))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void shouldGetSubjectFromToken() {
        UserDetails userDetails = new User(
                "testUser",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        String token = jwtProvider.generateToken(userDetails);

        StepVerifier.create(jwtProvider.getClaims(token))
                .assertNext(claims -> assertEquals("testUser", claims.getSubject()))
                .verifyComplete();
    }
}
