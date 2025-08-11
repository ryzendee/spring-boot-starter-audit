package ryzendee.starter.audit.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import ryzendee.starter.audit.model.HttpAuditLogEntry;
import ryzendee.starter.audit.model.AuditLogEntry;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Форматтер для {@link HttpAuditLogEntry}.
 *
 * @author Dmitry Ryazantsev
 */
@Component
public class HttpAuditLogEntryFormatter implements AuditLogEntryFormatter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean supports(Object logEntry) {
        return logEntry instanceof HttpAuditLogEntry;
    }

    @Override
    public String format(AuditLogEntry entry) {
        HttpAuditLogEntry p = (HttpAuditLogEntry) entry;

        return String.format(
                "%s %s %s %d %s RequestBody = %s ResponseBody = %s",
                formatTimestamp(entry.getTimestamp()),
                p.getDirection(),
                p.getHttpMethod(),
                p.getHttpStatusCode(),
                p.getRequestPath(),
                toJson(p.getRequestBody()),
                toJson(p.getResponseBody())
        );
    }

    private String formatTimestamp(LocalDateTime time) {
        return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
    }

    private String toJson(Object o) {
        if (o == null) return "{}";
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            return "\"<invalid>\"";
        }
    }

}
