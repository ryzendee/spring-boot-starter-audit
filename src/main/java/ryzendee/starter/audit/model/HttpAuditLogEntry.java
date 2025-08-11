package ryzendee.starter.audit.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ryzendee.starter.audit.enums.Direction;

/**
 * Класс для записей лога HTTP.
 *
 * @author Dmitry Ryazantsev
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class HttpAuditLogEntry extends AuditLogEntry {

    private Direction direction;
    private String httpMethod;
    private int httpStatusCode;
    private String requestPath;
    private long durationMs;
    private Object requestBody;
    private Object responseBody;
}
