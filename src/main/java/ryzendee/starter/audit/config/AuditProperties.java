package ryzendee.starter.audit.config;


import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Getter
@NoArgsConstructor
@ConfigurationProperties(prefix = "spring.audit")
public class AuditProperties {

    @NestedConfigurationProperty
    private Console console = new Console();

    @NestedConfigurationProperty
    private File file = new File();

    @NestedConfigurationProperty
    private Kafka kafka = new Kafka();

    @Data
    @NoArgsConstructor
    public static class Console {
        /**
         * Включение логгирования в консоль.
         */
        private boolean enabled = false;
    }

    @Data
    @NoArgsConstructor
    public static class File {
        /**
         * Включение логгирования в файл.
         */
        private boolean enabled = false;
    }

    @Data
    @NoArgsConstructor
    public static class Kafka {
        /**
         * Включение логгирования в Kafka.
         */
        private boolean enabled = false;

        /**
         * Настройки топика Kafka.
         */
        @NestedConfigurationProperty
        private Topic topic = new Topic();
    }

    @Data
    @NoArgsConstructor
    public static class Topic {
        /**
         * Имя топика Kafka для отправки логов.
         */
        private String name = "application-logs-topic";

    }
}

