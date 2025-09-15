package co.com.bancolombia.sqs.sender;

import co.com.bancolombia.sqs.sender.config.SQSSenderProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;
import java.util.concurrent.CompletableFuture;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SQSSenderTest {

    private SQSSender sender;
    private SqsAsyncClient client;

    @BeforeEach
    void setup() {
        SQSSenderProperties props = new SQSSenderProperties(
                "us-east-1",
                "http://fake-queue-url",
                null
        );
        client = mock(SqsAsyncClient.class);
        sender = new SQSSender(props, client);
    }

    @Test
    void shouldSendMessageAndReturnMessageId() {
        SendMessageResponse response = SendMessageResponse.builder()
                .messageId("12345")
                .build();

        when(client.sendMessage(any(SendMessageRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        Mono<String> result = sender.send("hello world");

        StepVerifier.create(result)
                .expectNext("12345")
                .verifyComplete();

        verify(client).sendMessage(any(SendMessageRequest.class));
    }
}
