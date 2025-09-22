package co.com.bancolombia.sqs.sender;

import co.com.bancolombia.model.loanapplication.gateways.SQSSenderPort;
import co.com.bancolombia.model.loanapplication.model.sqs.LoanApplicationPayment;
import co.com.bancolombia.model.loanapplication.model.sqs.LoanApplicationState;
import co.com.bancolombia.sqs.sender.config.SQSSenderProperties;
import co.com.bancolombia.sqs.sender.mapper.LoanApplicationEventMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSender implements SQSSenderPort {
    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;
    private final LoanApplicationEventMapper mapper;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<String> sendState(LoanApplicationState state) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(mapper.toDtoState(state)))
                .subscribeOn(Schedulers.boundedElastic())
                .map(this::buildRequest)
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnError(e -> log.error("Error sending message to SQS", e))
                .doOnNext(response -> log.debug("Message sent to sqs state{}", response.messageId()))
                .map(SendMessageResponse::messageId);

    }

    @Override
    public Mono<String> sendPayment(LoanApplicationPayment event) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(mapper.toDtoPayment(event)))
                .map(this::buildRequest)
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnError(e -> log.error("Error sending message to SQS", e))
                .doOnNext(response -> log.debug("Message sent to sqs payment {}", response.messageId()))
                .map(SendMessageResponse::messageId);
    }

    private SendMessageRequest buildRequest(String message) {
        return SendMessageRequest.builder()
                .queueUrl(properties.queueUrl())
                .messageBody(message)
                .build();
    }
}
