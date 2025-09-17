package co.com.bancolombia.r2dbc.health;

import co.com.bancolombia.model.loanapplication.globalmessage.GlobalMessage;
import co.com.bancolombia.r2dbc.exception.DataBaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class R2dbcSafeExecutor {
    public <T> Mono<T> executeMono(Supplier<Mono<T>> operation) {
        return operation.get()
                .onErrorMap(e -> new DataBaseException(GlobalMessage.DATABASE_ERROR, e));
    }

    public <T> Flux<T> executeFlux(Supplier<Flux<T>> operation) {
        return operation.get()
                .onErrorMap(e -> new DataBaseException(GlobalMessage.DATABASE_ERROR, e));
    }
}

