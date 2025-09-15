package co.com.bancolombia.sqs.sender.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import software.amazon.awssdk.metrics.MetricCollection;
import software.amazon.awssdk.metrics.MetricPublisher;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import static org.assertj.core.api.Assertions.assertThat;

public class SQSSenderConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(SQSSenderConfig.class)
            .withBean(MetricPublisher.class, () -> new MetricPublisher() {
                @Override
                public void publish(MetricCollection metricCollection) {}
                @Override
                public void close() {}
            })
            .withConfiguration(AutoConfigurations.of(
                    org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration.class
            ))
            .withPropertyValues(
                    "adapter.sqs.region=us-east-1",
                    "adapter.sqs.queue-url=http://fake-queue-url",
                    "adapter.sqs.endpoint=http://localhost:4566"
            )
            .withBean(SQSSenderProperties.class, () ->
                    new SQSSenderProperties("us-east-1", "http://fake-queue-url", "http://localhost:4566")
            );

    @Test
    void shouldCreateSqsAsyncClientBean() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(SqsAsyncClient.class);

            SqsAsyncClient client = context.getBean(SqsAsyncClient.class);
            assertThat(client).isNotNull();
            assertThat(client.serviceClientConfiguration().region().id()).isEqualTo("us-east-1");
        });
    }

}
