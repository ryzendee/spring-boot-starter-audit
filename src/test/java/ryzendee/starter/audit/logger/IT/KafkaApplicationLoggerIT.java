package ryzendee.starter.audit.logger.IT;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ryzendee.starter.audit.enums.Direction;
import ryzendee.starter.audit.enums.EventType;
import ryzendee.starter.audit.enums.LogLevel;
import ryzendee.starter.audit.config.AuditStarterAutoConfiguration;
import ryzendee.starter.audit.logger.KafkaApplicationLogger;
import ryzendee.starter.audit.model.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@EnableAutoConfiguration(exclude = {AuditStarterAutoConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@EmbeddedKafka(
        controlledShutdown = true,
        bootstrapServersProperty = "spring.kafka.bootstrap-servers",
        partitions = 1
)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = {KafkaApplicationLoggerTestConfig.class, KafkaAutoConfiguration.class}
)
public class KafkaApplicationLoggerIT {

    private static final String HEADER_MESSAGE_ID = "messageId";
    private static final int TIMEOUT = 15000;

    @Autowired
    private KafkaApplicationLogger kafkaApplicationLogger;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;
    @Autowired
    private KafkaMessageListenerContainer<String, Object> container;
    @Autowired
    private BlockingQueue<ConsumerRecord<String, Object>> records;

    @BeforeEach
    void start() {
        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
    }

    @Autowired
    private Environment env;

    @Test
    void log_httpLogEntry_shouldSend() throws Exception {
        HttpAuditLogEntry entry = HttpAuditLogEntry.builder()
                .direction(Direction.INCOMING)
                .httpMethod("GET")
                .httpStatusCode(200)
                .requestPath("/test")
                .requestBody("{request}")
                .responseBody("response")
                .durationMs(System.currentTimeMillis())
                .build();

        kafkaApplicationLogger.log(entry);

        ConsumerRecord<String, Object> record = records.poll(TIMEOUT, TimeUnit.MILLISECONDS);
        assertThat(record).isNotNull();
        assertThat(record.value()).isNotNull();
        assertThat(record.value()).isInstanceOf(HttpAuditLogEntry.class);
        assertThat(record.headers()).anyMatch(header -> header.key().equals(HEADER_MESSAGE_ID));
    }

    @Test
    void log_MethodLogEntry_shouldSend() throws Exception {
        MethodAuditLogEntry entry = MethodAuditLogEntry.builder()
                .logLevel(LogLevel.INFO)
                .eventType(EventType.END)
                .methodName("methodName")
                .args(new String[]{"arg1"})
                .result("result")
                .build();

        kafkaApplicationLogger.log(entry);

        ConsumerRecord<String, Object> record = records.poll(TIMEOUT, TimeUnit.MILLISECONDS);
        assertThat(record).isNotNull();
        assertThat(record.value()).isNotNull();
        assertThat(record.value()).isInstanceOf(MethodAuditLogEntry.class);
        assertThat(record.headers()).anyMatch(header -> header.key().equals(HEADER_MESSAGE_ID));
    }
}
