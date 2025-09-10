package co.com.bancolombia.authsecurity.jwt.manager;

import co.com.bancolombia.authsecurity.jwt.provider.JwtProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtProvider jwtProvider;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        Object credentials = authentication.getCredentials();

        if (credentials == null) {
            return Mono.error(new BadCredentialsException("No token provided"));
        }

        final String token = credentials.toString();

        return jwtProvider.getClaims(token)
                .map(claims -> buildAuthenticationFromClaims(claims, token))
                .onErrorMap(e -> new BadCredentialsException("Invalid token"));
    }

    private Authentication buildAuthenticationFromClaims(Claims claims, String token) {
        String username = claims.getSubject();
        Object rolesClaim = claims.get("roles");

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (rolesClaim instanceof Collection<?>) {
            ((Collection<?>) rolesClaim).forEach(r -> authorities.add(new SimpleGrantedAuthority("ROLE_" + r)));
        }

        return new UsernamePasswordAuthenticationToken(username, token, authorities);
    }
}
