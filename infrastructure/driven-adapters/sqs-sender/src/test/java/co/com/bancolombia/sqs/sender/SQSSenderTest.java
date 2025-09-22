package co.com.bancolombia.sqs.sender;

import co.com.bancolombia.model.loanapplication.model.sqs.LoanApplicationPayment;
import co.com.bancolombia.model.loanapplication.model.sqs.LoanApplicationState;
import co.com.bancolombia.sqs.sender.config.SQSSenderProperties;
import co.com.bancolombia.sqs.sender.dto.LoanApplicationPaymentDto;
import co.com.bancolombia.sqs.sender.dto.LoanApplicationStateDto;
import co.com.bancolombia.sqs.sender.mapper.LoanApplicationEventMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SQSSenderTest {

    private SQSSender sender;
    private SqsAsyncClient client;
    private LoanApplicationEventMapper mapper;

     @BeforeEach
    void setup() {
        SQSSenderProperties props = new SQSSenderProperties(
                "us-east-1",
                "http://fake-queue-url",
                null
        );
        client = mock(SqsAsyncClient.class);
        mapper = mock(LoanApplicationEventMapper.class);
        ObjectMapper objectMapper = new ObjectMapper();

        sender = new SQSSender(props, client, mapper, objectMapper);
    }

    @Test
    void shouldSendStateMessageAndReturnMessageId() {
        LoanApplicationState state = new LoanApplicationState(1L, "APPROVED", "example@example.com", "STATUS", BigDecimal.valueOf(5000.00));
        LoanApplicationStateDto stateDto = new LoanApplicationStateDto(1L, "APPROVED", "example@example.com", "STATUS", BigDecimal.valueOf(5000.00));

        when(mapper.toDtoState(state)).thenReturn(stateDto);

        SendMessageResponse response = SendMessageResponse.builder()
                .messageId("1L")
                .build();

        when(client.sendMessage(any(SendMessageRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        Mono<String> result = sender.sendState(state);

        StepVerifier.create(result)
                .expectNext("1L")
                .verifyComplete();

        verify(client).sendMessage(any(SendMessageRequest.class));
    }

    @Test
    void shouldSendPaymentMessageAndReturnMessageId() {
        LoanApplicationPayment payment = new LoanApplicationPayment(1L, "APPROVED", "example@example.com", BigDecimal.valueOf(5000.00), BigDecimal.valueOf(12.50), 24, "PLAN");
        LoanApplicationPaymentDto paymentDto = new LoanApplicationPaymentDto(1L, "APPROVED", "example@example.com", BigDecimal.valueOf(5000.00), BigDecimal.valueOf(12.50), 24, "PLAN");

        when(mapper.toDtoPayment(payment)).thenReturn(paymentDto);

        SendMessageResponse response = SendMessageResponse.builder()
                .messageId("1L")
                .build();

        when(client.sendMessage(any(SendMessageRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        Mono<String> result = sender.sendPayment(payment);

        StepVerifier.create(result)
                .expectNext("1L")
                .verifyComplete();

        verify(client).sendMessage(any(SendMessageRequest.class));
    }

}
