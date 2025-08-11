package ryzendee.starter.audit.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ryzendee.starter.audit.enums.EventType;
import ryzendee.starter.audit.enums.LogLevel;

/**
 * Класс для записей лога метода.
 *
 * @author Dmitry Ryazantsev
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MethodAuditLogEntry extends AuditLogEntry {

    private LogLevel logLevel;
    private EventType eventType;
    private String methodName;
    private String errorMessage;
    private Object[] args;
    private Object result;
}
