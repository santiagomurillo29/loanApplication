package co.com.bancolombia.sqs.sender.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

public class SQSSenderPropertiesTest {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withUserConfiguration(TestConfig.class)
                    .withPropertyValues(
                            "adapter.sqs.region=us-east-1",
                            "adapter.sqs.queue-url=http://fake-queue-url",
                            "adapter.sqs.endpoint=http://localhost:4566"
                    );

    @EnableConfigurationProperties(SQSSenderProperties.class)
    static class TestConfig {}

    @Test
    void shouldBindPropertiesCorrectly() {
        contextRunner.run(context -> {
            SQSSenderProperties props = context.getBean(SQSSenderProperties.class);
            assertThat(props.region()).isEqualTo("us-east-1");
            assertThat(props.queueUrl()).isEqualTo("http://fake-queue-url");
            assertThat(props.endpoint()).isEqualTo("http://localhost:4566");
        });
    }
}
