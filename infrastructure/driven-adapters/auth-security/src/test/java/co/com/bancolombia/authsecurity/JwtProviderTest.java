package co.com.bancolombia.authsecurity;

import io.jsonwebtoken.Jwts;
import co.com.bancolombia.authsecurity.jwt.provider.JwtProvider;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.test.StepVerifier;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JwtProviderTest {

    private JwtProvider jwtProvider;
    private final String secret = "mySecretKey123456789012345678901234";


    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider();

        ReflectionTestUtils.setField(jwtProvider, "secret", secret);
    }

    private String createTestToken(String username, List<String> roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(SignatureAlgorithm.HS256, secret.getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    @Test
    void shouldValidateTokenAndGetClaims() {
        String token = createTestToken("testUser", List.of("ADMIN"));

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
        String token = createTestToken("testUser", List.of("USER"));

        StepVerifier.create(jwtProvider.getSubject(token))
                .assertNext(subject -> assertEquals("testUser", subject))
                .verifyComplete();
    }
}
