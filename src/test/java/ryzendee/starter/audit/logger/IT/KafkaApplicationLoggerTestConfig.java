package ryzendee.starter.audit.logger.IT;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ryzendee.starter.audit.logger.KafkaApplicationLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@EnableKafka
@TestConfiguration
@ExtendWith(SpringExtension.class)
class KafkaApplicationLoggerTestConfig {

    @Autowired
    private Environment env;

    @Bean
    public KafkaApplicationLogger kafkaLogger(KafkaTemplate<String, Object> kafkaTemplate) {
        return new KafkaApplicationLogger(kafkaTemplate, env.getProperty("spring.audit.kafka.topic.name"));
    }

    @Bean
    public KafkaMessageListenerContainer<String, Object> kafkaMessageListenerContainer(ConsumerFactory<String, Object> consumerFactory,
                                                                                       ContainerProperties containerProperties,
                                                                                       BlockingQueue<ConsumerRecord<String, Object>> records) {
        KafkaMessageListenerContainer<String, Object> container
                = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);

        container.setupMessageListener((MessageListener<String, Object>) records::add);
        return container;
    }

    @Bean
    public BlockingQueue<ConsumerRecord<String, Object>> records() {
        return new LinkedBlockingQueue<>();
    }

    @Bean
    public ContainerProperties containerProperties() {
        return new ContainerProperties(env.getProperty("spring.audit.kafka.topic.name"));
    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactory(EmbeddedKafkaBroker embeddedKafkaBroker) {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, env.getProperty("spring.kafka.consumer.group-id"));
        props.put(JsonDeserializer.TRUSTED_PACKAGES, env.getProperty("spring.kafka.consumer.properties.spring.json.trusted.packages"));
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, env.getProperty("spring.kafka.consumer.auto-offset-reset"));

        return new DefaultKafkaConsumerFactory<>(props);
    }
}

