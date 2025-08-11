package ryzendee.starter.audit.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import ryzendee.starter.audit.annotation.AuditLog;
import ryzendee.starter.audit.enums.LogLevel;
import ryzendee.starter.audit.model.AuditLogEntry;
import ryzendee.starter.audit.enums.EventType;
import ryzendee.starter.audit.logger.ApplicationLogger;
import ryzendee.starter.audit.model.MethodAuditLogEntry;

/**
 * Аспект для логирования вызовов методов, аннотированных {@link AuditLog}.
 *
 * @author Dmitry Ryazantsev
 */
@Aspect
@Slf4j
@RequiredArgsConstructor
public class MethodLoggingAspect {

    private final ApplicationLogger logger;

    /**
     * Окружает вызов метода логированием начала, окончания и ошибок.
     *
     * @param joinPoint точка соединения, представляющая метод
     * @param auditLog аннотация с настройками логирования
     * @return результат выполнения метода
     * @throws Throwable исключение, проброшенное вызываемым методом
     */
    @Around("@annotation(auditLog)")
    public Object logMethod(ProceedingJoinPoint joinPoint, AuditLog auditLog) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();

        AuditLogEntry auditLogEntry
                = buildStart(auditLog.logLevel(), methodName, joinPoint.getArgs());
        logger.log(auditLogEntry);

        try {
            Object result = joinPoint.proceed();

            auditLogEntry
                    = buildEnd(auditLog.logLevel(), methodName, result);
            logger.log(auditLogEntry);

            return result;
        } catch (Exception ex) {
            auditLogEntry = buildError(methodName, ex.getMessage());
            logger.log(auditLogEntry);

            throw ex;
        }
    }

    private MethodAuditLogEntry buildStart(LogLevel logLevel, String methodName, Object[] args) {
        return MethodAuditLogEntry.builder()
                .logLevel(logLevel)
                .eventType(EventType.START)
                .methodName(methodName)
                .args(args)
                .build();
    }

    private MethodAuditLogEntry buildEnd(LogLevel logLevel, String methodName, Object result) {
        return MethodAuditLogEntry.builder()
                .logLevel(logLevel)
                .eventType(EventType.END)
                .methodName(methodName)
                .result(result)
                .build();
    }

    private MethodAuditLogEntry buildError(String methodName, String errorMessage) {
        return MethodAuditLogEntry.builder()
                .logLevel(LogLevel.ERROR)
                .eventType(EventType.ERROR)
                .methodName(methodName)
                .errorMessage(errorMessage)
                .build();
    }
}