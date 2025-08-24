package ryzendee.starter.audit.logger;

import ryzendee.starter.audit.model.AuditLogEntry;

/**
 * Интерфейс логгера для записи аудиторских событий.
 *
 * Определяет метод для логирования {@link AuditLogEntry}.
 *
 * @author Dmitry Ryazantsev
 */
public interface ApplicationLogger {

    /**
     * Логирует указанную запись.
     *
     * @param auditLogEntry запись журнала для логирования
     */
    void log(AuditLogEntry auditLogEntry);
}