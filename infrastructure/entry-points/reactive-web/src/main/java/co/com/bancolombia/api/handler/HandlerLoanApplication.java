package co.com.bancolombia.api.handler;

import co.com.bancolombia.api.dto.request.LoanApplicationRequestDto;
import co.com.bancolombia.api.dto.request.UpdateLoanApplicationRequestDto;
import co.com.bancolombia.api.dto.request.validation.RequestValidator;
import co.com.bancolombia.api.mapper.LoanApplicationMapper;
import co.com.bancolombia.r2dbc.health.R2dbcHealthChecker;
import co.com.bancolombia.usecase.loanapplication.usecase.api.LoanApplicationServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class HandlerLoanApplication {

    private final LoanApplicationServicePort loanApplicationServicePort;
    private final LoanApplicationMapper loanApplicationMapper;
    private final RequestValidator validator;
    private final R2dbcHealthChecker healthChecker;

    public Mono<ServerResponse> CreateLoanApplication(ServerRequest serverRequest) {
        String token = serverRequest.exchange().getAttribute("token");
        return serverRequest.bodyToMono(LoanApplicationRequestDto.class)
                .flatMap(validator::validate)
                .map(loanApplicationMapper::toModelLoanApplication)
                .flatMap(model -> loanApplicationServicePort.createLoanApplication(model, token))
                .map(loanApplicationMapper::toDtoLoanApplication)
                .flatMap(response -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response));
    }

    public Mono<ServerResponse> GetLoanApplication(ServerRequest serverRequest) {
        String token = serverRequest.exchange().getAttribute("token");
        return loanApplicationServicePort.findLoanApplicationPending(
                serverRequest.queryParam("page")
                        .map(Integer::parseInt)
                        .orElse(0),
                serverRequest.queryParam("size")
                        .map(Integer::parseInt)
                        .orElse(10),
                serverRequest.queryParams().getOrDefault(
                        "state",
                        List.of("PENDING", "REJECTED")),
                token)
                .map(loanApplicationMapper::toPageDto)
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response));
    }

    public Mono<ServerResponse> UpdateLoanApplication(ServerRequest serverRequest) {
        String token = serverRequest.exchange().getAttribute("token");
        return serverRequest.bodyToMono(UpdateLoanApplicationRequestDto.class)
                .flatMap(validator::validate)
                .flatMap(dto -> loanApplicationServicePort.updateLoanApplication(
                        Long.parseLong(serverRequest.pathVariable("idLoanApplication")),
                        dto.getState(),
                        token))
                .map(loanApplicationMapper::toDtoLoanApplication)
                .flatMap(response -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response));
    }

    public Mono<ServerResponse> CalculateCapacityLoanApplication(ServerRequest serverRequest) {
        String token = serverRequest.exchange().getAttribute("token");
        return loanApplicationServicePort.calculateCapacityLoanApplication(
                        Long.parseLong(serverRequest.pathVariable("idLoanApplication")),
                        token)
                .map(loanApplicationMapper::toDtoLoanApplication)
                .flatMap(response -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response));
    }

    public Mono<ServerResponse> GetHealth(ServerRequest ignored) {
        return healthChecker.isDatabaseUp()
                .flatMap(isUp -> {
                    if (isUp) {
                        return ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("status", "UP", "database", "UP"));
                    } else {
                        return ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("status", "DOWN", "database", "DOWN"));
                    }
                });
    }

    public Mono<ServerResponse> GetLiveness(ServerRequest ignoredServerRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue("OK");
    }
}
