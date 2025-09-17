package co.com.bancolombia.api.handlerexception;

import co.com.bancolombia.api.dto.response.exception.ErrorResponseBodyDto;
import co.com.bancolombia.consumer.exception.WebClientException;
import co.com.bancolombia.model.loanapplication.globalmessage.GlobalMessage;
import co.com.bancolombia.r2dbc.exception.DataBaseException;
import co.com.bancolombia.usecase.loanapplication.exception.BusinessException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Order(-2)
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public GlobalExceptionHandler(ErrorAttributes errorAttributes,
                                  ApplicationContext applicationContext,
                                  ServerCodecConfigurer serverCodecConfigurer) {
        super(errorAttributes, new WebProperties.Resources(), applicationContext);
        this.setMessageWriters(serverCodecConfigurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::buildErrorResponse);
    }

    private Mono<ServerResponse> buildErrorResponse(ServerRequest request) {
        return Mono.just(request)
                .map(this::getError)
                .doOnNext(error -> log.error("Handling error: {}", error.getClass().getName(), error))
                .flatMap(Mono::error)
                .onErrorResume(BusinessException.class, ex -> handleBusinessException(ex, request))
                .onErrorResume(DataBaseException.class, ex -> handleDBException(ex, request))
                .onErrorResume(WebClientException.class, ex -> handleWebClient(ex, request))
                .onErrorResume(ConstraintViolationException.class, ex -> handleConstraintViolationException(ex, request))
                .onErrorResume(ex -> handleUnknownError(request))
                .cast(Tuple2.class)
                .flatMap(tuple -> this.buildResponse(
                        (ErrorResponseBodyDto) tuple.getT1(),
                        (HttpStatus) tuple.getT2()));
    }

    private Mono<Tuple2<ErrorResponseBodyDto, HttpStatus>> handleBusinessException(BusinessException ex, ServerRequest request){
        GlobalMessage error = ex.getError();

        ErrorResponseBodyDto body = ErrorResponseBodyDto.builder()
                .message(error.getMessage())
                .code(error.getStatusCode())
                .timestamp(LocalDateTime.now().toString())
                .path(request.path())
                .build();
        return Mono.just(body).zipWith(Mono.just(HttpStatus.valueOf(Integer.parseInt(error.getStatusCode()))));
    }

    private Mono<Tuple2<ErrorResponseBodyDto, HttpStatus>> handleDBException(DataBaseException ex, ServerRequest request) {
        GlobalMessage error = ex.getError();

        ErrorResponseBodyDto body = ErrorResponseBodyDto.builder()
                .message(error.getMessage())
                .code(error.getStatusCode())
                .timestamp(LocalDateTime.now().toString())
                .path(request.path())
                .build();

        return Mono.just(body)
                .zipWith(Mono.just(HttpStatus.valueOf(Integer.parseInt(error.getStatusCode()))));
    }

    private Mono<Tuple2<ErrorResponseBodyDto, HttpStatus>> handleWebClient(WebClientException ex, ServerRequest request) {
        GlobalMessage error = ex.getError();

        ErrorResponseBodyDto body = ErrorResponseBodyDto.builder()
                .message(error.getMessage())
                .code(error.getStatusCode())
                .timestamp(LocalDateTime.now().toString())
                .path(request.path())
                .build();

        return Mono.just(body)
                .zipWith(Mono.just(HttpStatus.valueOf(Integer.parseInt(error.getStatusCode()))));
    }

    private  Mono<Tuple2<ErrorResponseBodyDto, HttpStatus>> handleConstraintViolationException(ConstraintViolationException ex, ServerRequest request) {
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        String errors = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(" "));

        ErrorResponseBodyDto body = ErrorResponseBodyDto.builder()
                .message(errors)
                .code(GlobalMessage.STATUS_CODE_400)
                .timestamp(LocalDateTime.now().toString())
                .path(request.path())
                .build();

        return Mono.just(body).zipWith(Mono.just(HttpStatus.BAD_REQUEST));
    }

    private Mono<Tuple2<ErrorResponseBodyDto, HttpStatus>> handleUnknownError(ServerRequest request) {
        ErrorResponseBodyDto body = ErrorResponseBodyDto.builder()
                .message("Unexpected error")
                .code(GlobalMessage.STATUS_CODE_500)
                .timestamp(LocalDateTime.now().toString())
                .path(request.path())
                .build();

        return Mono.just(body).zipWith(Mono.just(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    private Mono<ServerResponse> buildResponse(ErrorResponseBodyDto body, HttpStatus httpStatus) {
        return ServerResponse.status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body);
    }
}
