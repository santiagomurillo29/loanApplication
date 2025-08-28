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
        return Mono.from(connectionFactory.create())
                .flatMap(connection ->
                        Mono.from(connection.createStatement("SELECT 1").execute())
                                .flatMap(result -> Mono.from(result.map((row, metadata) -> true)))
                                .doFinally(signal -> connection.close())
                )
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(e -> {
                    log.error("Ping to R2DBC DB failed: {}", e.getMessage());
                    return Mono.just(false);
                });
    }
}
