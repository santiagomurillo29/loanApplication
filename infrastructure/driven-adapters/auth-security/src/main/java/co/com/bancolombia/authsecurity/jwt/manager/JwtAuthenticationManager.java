package co.com.bancolombia.authsecurity.jwt.manager;

import co.com.bancolombia.authsecurity.jwt.provider.JwtProvider;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtProvider jwtProvider;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.just(authentication)
                .flatMap(auth -> {
                    try {
                        Claims claims = jwtProvider.getClaims(auth.getCredentials().toString());
                        return Mono.just(claims);
                    } catch (Exception e) {
                        return Mono.error(new BadCredentialsException("Invalid token"));
                    }
                })
                .map(claims -> {
                    String username = claims.getSubject();

                    Object rolesClaim = claims.get("roles");
                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();

                    if (rolesClaim instanceof List<?>) {
                        ((List<?>) rolesClaim).forEach(r -> {
                            authorities.add(new SimpleGrantedAuthority("ROLE_" + r));
                        });
                    } else if (rolesClaim instanceof Collection<?>) {
                        ((Collection<?>) rolesClaim).forEach(r -> {
                            authorities.add(new SimpleGrantedAuthority("ROLE_" + r));
                        });
                    }
                    return new UsernamePasswordAuthenticationToken(username, null, authorities);
                });
    }
}
