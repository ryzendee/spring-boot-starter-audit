package ryzendee.starter.audit.logger;

import lombok.RequiredArgsConstructor;
import ryzendee.starter.audit.model.AuditLogEntry;

import java.util.List;

@RequiredArgsConstructor
public class CompositeApplicationLogger implements ApplicationLogger {

    private final List<ApplicationLogger> loggers;

    @Override
    public void log(AuditLogEntry auditLogEntry) {
        loggers.forEach(logger -> logger.log(auditLogEntry));
    }
}
