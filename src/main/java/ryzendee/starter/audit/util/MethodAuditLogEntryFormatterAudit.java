package ryzendee.starter.audit.util;

import org.springframework.stereotype.Component;
import ryzendee.starter.audit.model.AuditLogEntry;
import ryzendee.starter.audit.model.MethodAuditLogEntry;

import java.util.Arrays;

/**
 * Форматтер для {@link MethodAuditLogEntry}.
 *
 * @author Dmitry Ryazantsev
 */
@Component
public class MethodAuditLogEntryFormatterAudit implements AuditLogEntryFormatter {

    @Override
    public boolean supports(Object logEntry) {
        return logEntry instanceof MethodAuditLogEntry;
    }

    @Override
    public String format(AuditLogEntry entry) {
        MethodAuditLogEntry p = (MethodAuditLogEntry) entry;

        StringBuilder sb = new StringBuilder();
        sb.append(p.getEventType()).append(" ")
                .append(entry.getTraceId()).append(" ")
                .append(p.getMethodName()).append(" ");

        switch (p.getEventType()) {
            case START -> sb.append("args = ").append(Arrays.toString(p.getArgs()));
            case END -> sb.append("result = ").append(p.getResult());
            case ERROR -> sb.append("errorMessage = ").append(p.getErrorMessage());
        }

        return sb.toString();
    }

}
