package ryzendee.starter.audit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import ryzendee.starter.audit.aspect.MethodLoggingAspect;
import ryzendee.starter.audit.http.IncomingRequestLoggingFilter;
import ryzendee.starter.audit.http.OutgoingRequestInterceptor;
import ryzendee.starter.audit.logger.*;
import ryzendee.starter.audit.util.AuditLogEntryFormatter;

import java.util.List;

@Configuration
@EnableConfigurationProperties(AuditProperties.class)
public class AuditStarterAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "spring.audit.console", name = "enabled", havingValue = "true")
    public ConsoleApplicationLogger consoleLogger(List<AuditLogEntryFormatter> formatters) {
        return new ConsoleApplicationLogger(formatters);
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.audit.file", name = "enabled", havingValue = "true")
    public FileApplicationLogger fileLogger(List<AuditLogEntryFormatter> formatters) {
        return new FileApplicationLogger(formatters);
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.audit.kafka", name = "enabled", havingValue = "true")
    public KafkaApplicationLogger kafkaLogger(AuditProperties auditProperties, KafkaTemplate<String, Object> kafkaTemplate) {
        String topic = auditProperties.getKafka().getTopic().getName();
        return new KafkaApplicationLogger(kafkaTemplate, topic);
    }

    @Bean
    @Primary
    @ConditionalOnBean(ApplicationLogger.class) // чтоб создвался если есть хоть один логгер
    public ApplicationLogger compositeLogger(List<ApplicationLogger> loggers) {
        return new CompositeApplicationLogger(loggers);
    }

    @Bean
    @ConditionalOnBean(ApplicationLogger.class)
    public MethodLoggingAspect methodLoggingAspect(ApplicationLogger logger) {
        return new MethodLoggingAspect(logger);
    }

    @Bean
    @ConditionalOnBean(ApplicationLogger.class)
    public IncomingRequestLoggingFilter incomingRequestLoggingFilter(ApplicationLogger logger) {
        return new IncomingRequestLoggingFilter(logger);
    }

    @Bean
    @ConditionalOnBean(ApplicationLogger.class)
    public OutgoingRequestInterceptor outgoingRequestInterceptor(ApplicationLogger logger) {
        return new OutgoingRequestInterceptor(logger);
    }
}
