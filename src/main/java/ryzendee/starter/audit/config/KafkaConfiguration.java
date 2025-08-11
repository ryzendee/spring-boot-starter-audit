//package ryzendee.starter.audit.config;
//
//import org.apache.kafka.clients.admin.NewTopic;
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.apache.kafka.common.serialization.StringSerializer;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.env.Environment;
//import org.springframework.kafka.annotation.EnableKafka;
//import org.springframework.kafka.config.TopicBuilder;
//import org.springframework.kafka.core.DefaultKafkaProducerFactory;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.core.ProducerFactory;
//import org.springframework.kafka.support.serializer.JsonSerializer;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import static java.lang.Integer.parseInt;
//
//@EnableKafka
//@Configuration
//public class KafkaConfiguration {
//
//    @Autowired
//    private Environment env;
//
//    @Bean
//    public NewTopic newTopic() {
//        return TopicBuilder.name(env.getProperty("spring.audit.kafka.topic.name"))
//                .partitions(parseInt(env.getProperty("spring.audit.kafka.topic.partitions")))
//                .replicas(parseInt(env.getProperty("spring.audit.kafka.topic.replicas")))
//                .build();
//    }
//}
