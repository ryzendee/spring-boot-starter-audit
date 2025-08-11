package ryzendee.starter.audit.logger;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import ryzendee.starter.audit.enums.LogLevel;
import ryzendee.starter.audit.model.AuditLogEntry;
import ryzendee.starter.audit.util.AuditLogEntryFormatter;

import java.util.List;

/**
 * Абстрактный базовый класс для логгеров на основе Log4j2.
 *
 * Использует список {@link AuditLogEntryFormatter} для форматирования записей перед логированием.
 * Поддерживает преобразование уровня логирования в формат Log4j2.
 *
 * @author Dmitry Ryazantsev
 */
@RequiredArgsConstructor
public abstract class AbstractLog4jLogger {

    private static final Level FALLBACK_LEVEL = Level.INFO;

    private final List<AuditLogEntryFormatter> auditLogEntryFormatters;

    /**
     * Выполняет логирование записи {@link AuditLogEntry} с использованием переданного логгера Log4j.
     *
     * @param logger логгер Log4j для вывода сообщения
     * @param auditLogEntry запись для логирования
     */
    protected void log(Logger logger, AuditLogEntry auditLogEntry) {
        Level level = toLog4jLevel(auditLogEntry.getLogLevel());
        logger.log(level, formatLogEntry(auditLogEntry));
    }

    /**
     * Преобразует уровень логирования {@link LogLevel} в соответствующий уровень Log4j2.
     * Если уровень не распознан, возвращается {@link #FALLBACK_LEVEL}.
     *
     * @param logLevel уровень логирования из доменной модели
     * @return уровень логирования Log4j2
     */
    protected Level toLog4jLevel(LogLevel logLevel) {
        String logLevelName = logLevel != null ? logLevel.name() : FALLBACK_LEVEL.name();
        return Level.toLevel(logLevelName);
    }

    /**
     * Форматирует запись журнала с помощью подходящего {@link AuditLogEntryFormatter} из списка.
     *
     * @param entry запись журнала для форматирования
     * @return строковое представление записи журнала
     * @throws IllegalArgumentException если не найден подходящий форматтер
     */
    protected String formatLogEntry(AuditLogEntry entry) {
        return auditLogEntryFormatters.stream()
                .filter(formatter -> formatter.supports(entry))
                .findFirst()
                .map(formatter -> formatter.format(entry))
                .orElseThrow(() ->
                        new IllegalArgumentException("No formatter found for payload type: " + entry.getClass())
                );
    }
}
