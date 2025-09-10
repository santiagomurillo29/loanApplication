package co.com.bancolombia.authsecurity.jwt.provider;

import co.com.bancolombia.model.loanapplication.gateways.TokenAuthSecurityPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.logging.Logger;

@Component
public class JwtProvider implements TokenAuthSecurityPort {

    private static final Logger LOGGER =  Logger.getLogger(JwtProvider.class.getName());

    @Value("${jwt.secret}")
    private String secret;

    public Mono<Claims> getClaims(String token) {
        return Mono.fromCallable(() ->
                        Jwts.parserBuilder()
                                .setSigningKey(getKey(secret))
                                .build()
                                .parseClaimsJws(token)
                                .getBody()
                )
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<String> getSubject(String token) {
        return getClaims(token).map(Claims::getSubject);
    }

    public Mono<Boolean> validate(String token) {
        return Mono.fromCallable(() -> {
                    try {
                        Jwts.parserBuilder()
                                .setSigningKey(getKey(secret))
                                .build()
                                .parseClaimsJws(token);
                        return true;
                    } catch (ExpiredJwtException e) {
                        LOGGER.severe("token expired");
                    } catch (UnsupportedJwtException e) {
                        LOGGER.severe("token unsupported");
                    } catch (MalformedJwtException e) {
                        LOGGER.severe("token malformed");
                    } catch (SignatureException e) {
                        LOGGER.severe("bad signature");
                    } catch (IllegalArgumentException e) {
                        LOGGER.severe("illegal args");
                    }
                    return false;
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    private SecretKey getKey(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
