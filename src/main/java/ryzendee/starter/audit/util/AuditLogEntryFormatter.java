package ryzendee.starter.audit.util;

import ryzendee.starter.audit.model.AuditLogEntry;

/**
 * Интерфейс форматтера записей аудита.
 *
 * Определяет контракт для преобразования {@link AuditLogEntry} в строковое представление
 * и проверки поддержки форматирования указанного объекта лога.
 *
 * @author Dmitry Ryazantsev
 */
public interface AuditLogEntryFormatter {

    /**
     * Форматирует указанный лог {@link AuditLogEntry} в строковое представление.
     *
     * @param auditLogEntry лог, который необходимо преобразовать
     * @return строковое представление лога;
     */
    String format(AuditLogEntry auditLogEntry);

    /**
     * Проверяет, поддерживает ли текущая реализация форматирование указанного лога.
     *
     * Используется для выбора подходящего форматтера в случаях,
     * когда есть несколько различных типов логов.
     *
     * @param logEntry лог, который необходимо проверить на поддержку
     * @return {@code true}, если данный форматтер может обработать переданный объект; иначе {@code false}
     */

    boolean supports(Object logEntry);
}
