package ryzendee.starter.audit.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ryzendee.starter.audit.model.AuditLogEntry;
import ryzendee.starter.audit.util.AuditLogEntryFormatter;

import java.util.List;

/**
 * Логгер для записи данных в файл.
 *
 * @author Dmitry Ryazantsev
 */
public class FileApplicationLogger extends AbstractLog4jLogger implements ApplicationLogger {

    private static final Logger LOGGER = LogManager.getLogger("FileLogger");

    public FileApplicationLogger(List<AuditLogEntryFormatter> auditLogEntryFormatters) {
        super(auditLogEntryFormatters);
    }

    @Override
    public void log(AuditLogEntry auditLogEntry) {
        log(LOGGER, auditLogEntry);
    }
}
