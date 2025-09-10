package co.com.bancolombia.authsecurity.jwt.filter;

import co.com.bancolombia.authsecurity.jwt.provider.JwtProvider;
import co.com.bancolombia.model.loanapplication.globalmessage.GlobalMessage;
import co.com.bancolombia.usecase.loanapplication.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter implements WebFilter {

    private final JwtProvider jwtProvider;
    private final ReactiveAuthenticationManager authenticationManager;
    private final ServerSecurityContextRepository securityContextRepository;
    private final ServerAuthenticationEntryPoint authenticationEntryPoint;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        if (path.startsWith("/auth") ||
                path.equals("/api/v1/login") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/webjars") ||
                path.equals("/favicon.ico")) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return authenticationEntryPoint.commence(exchange, new BadCredentialsException("no token provided or bad header"));
        }

        String token = authHeader.substring(7);

        exchange.getAttributes().put("token", token);

        return jwtProvider.validate(token)
                .flatMap(valid -> {
                    if (!valid) {
                        return authenticationEntryPoint.commence(exchange, new BadCredentialsException("invalid or expired token"));
                    }

                    Authentication authToken = new UsernamePasswordAuthenticationToken(token, token);
                    return authenticationManager.authenticate(authToken)
                            .flatMap(authentication -> {
                                SecurityContextImpl securityContext = new SecurityContextImpl(authentication);
                                return securityContextRepository.save(exchange, securityContext)
                                        .then(chain.filter(exchange));
                            });
                });
    }
}
