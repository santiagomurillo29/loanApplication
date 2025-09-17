package co.com.bancolombia.sqs.sender.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Component
@RequiredArgsConstructor
@Log4j2
public class SqsWarmup {

    private final SqsAsyncClient client;

    @EventListener(ApplicationReadyEvent.class)
    public void warmUp() {
        log.info("Warming up SQS client...");
        client.listQueues().thenAccept(r -> log.info("SQS warmup complete, found {} queues", r.queueUrls().size()))
                .exceptionally(e -> {
                    log.error("SQS warmup failed", e);
                    return null;
                });
    }
}
