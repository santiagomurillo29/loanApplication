package co.com.bancolombia.r2dbc.health;

import io.r2dbc.spi.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class R2dbcHealthChecker {

    private final ConnectionFactory connectionFactory;

    public Mono<Boolean> isDatabaseUp() {
        return Mono.usingWhen(
                        Mono.from(connectionFactory.create()),
                        connection -> Mono.from(connection.createStatement("SELECT 1").execute())
                                .flatMap(result -> Mono.from(result.map((row, meta) -> true)))
                                .onErrorResume(e -> {
                                    log.error("Query test failed: {}", e.getMessage());
                                    return Mono.just(false);
                                }),
                        connection -> Mono.from(connection.close())
                                .doOnError(e -> log.warn("Error closing connection: {}", e.getMessage()))
                )
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(e -> {
                    log.error("Ping to R2DBC DB failed: {}", e.toString());
                    return Mono.just(false);
                });
    }
}
