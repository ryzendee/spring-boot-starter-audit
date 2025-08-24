package ryzendee.starter.audit.annotation;

import ryzendee.starter.audit.enums.LogLevel;

import java.lang.annotation.*;

/**
 * Аннотация для включения аудита (логирования) вызовов метода.
 *
 * Используется аспектом {@link ryzendee.starter.audit.aspect.MethodLoggingAspect}
 * для перехвата вызовов и записи логов.
 *
 * @author Dmitry Ryazantsev
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuditLog {

    /**
     * Уровень логирования для аудита метода.
     *
     * @return уровень логирования
     */
    LogLevel logLevel() default LogLevel.INFO;
}
