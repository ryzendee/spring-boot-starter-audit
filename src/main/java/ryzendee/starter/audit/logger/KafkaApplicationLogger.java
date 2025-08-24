package ryzendee.starter.audit.logger;


import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import ryzendee.starter.audit.model.AuditLogEntry;

import static java.util.UUID.randomUUID;

/**
 * Логгер для отправки данных в Kafka-топик.
 *
 * @author Dmitry Ryazantsev
 */
public class KafkaApplicationLogger implements ApplicationLogger {

    private static final String HEADER_MESSAGE_ID = "messageId";

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public KafkaApplicationLogger(KafkaTemplate<String, Object> kafkaTemplate,
                                  String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Override
    public void log(AuditLogEntry auditLogEntry) {
        ProducerRecord<String, Object> record = new ProducerRecord<>(topic, auditLogEntry);
        record.headers().add(HEADER_MESSAGE_ID, randomUUID().toString().getBytes());
        kafkaTemplate.send(record);
    }

}
