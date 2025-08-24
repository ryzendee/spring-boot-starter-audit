package ryzendee.starter.audit.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ryzendee.starter.audit.enums.LogLevel;

import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.UUID.randomUUID;

/**
 * Базовый класс для всех записей лога.
 *
 * @author Dmitry Ryazantsev
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        property = "@class"
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class AuditLogEntry {

    @Builder.Default
    protected UUID traceId = randomUUID();
    @Builder.Default
    protected LocalDateTime timestamp = LocalDateTime.now();

    protected LogLevel logLevel;
}
