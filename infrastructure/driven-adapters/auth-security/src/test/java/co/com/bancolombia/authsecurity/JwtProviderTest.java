package co.com.bancolombia.authsecurity;

import co.com.bancolombia.authsecurity.jwt.provider.JwtProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.test.StepVerifier;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtProviderTest {

    private JwtProvider jwtProvider;
    private final String secret = "mySecretKey123456789012345678901234";


    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider();

        ReflectionTestUtils.setField(jwtProvider, "secret", secret);
    }

    private String createTestToken(List<String> roles) {
        Key key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setSubject("testUser")
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Test
    void shouldValidateTokenAndGetClaims() {
        String token = createTestToken(List.of("ADMIN"));

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
        String token = createTestToken(List.of("USER"));

        StepVerifier.create(jwtProvider.getSubject(token))
                .assertNext(subject -> assertEquals("testUser", subject))
                .verifyComplete();
    }
}
