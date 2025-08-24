package ryzendee.starter.audit.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ryzendee.starter.audit.annotation.AuditLog;
import ryzendee.starter.audit.enums.LogLevel;
import ryzendee.starter.audit.logger.ApplicationLogger;
import ryzendee.starter.audit.model.AuditLogEntry;
import ryzendee.starter.audit.enums.EventType;
import ryzendee.starter.audit.model.MethodAuditLogEntry;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MethodLoggingAspectTest {

    private static final String TEST_METHOD_NAME = "TestClass.testMethod()";
    private static final Object[] TEST_ARGS = {"arg1", 123};
    private static final Object TEST_RESULT = "test result";
    private static final String TEST_ERROR_MSG = "Test error";

    @Mock
    private ProceedingJoinPoint joinPoint;
    @Mock
    private Signature signature;
    @Mock
    private AuditLog auditLog;
    @Mock
    private ApplicationLogger logger;

    @InjectMocks
    private MethodLoggingAspect aspect;

    @BeforeEach
    void setUp() {
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn(TEST_METHOD_NAME);
    }

    @Test
    void logMethod_shouldLogStartAndEndOnSuccess() throws Throwable {
        // Arrange
        when(auditLog.logLevel()).thenReturn(LogLevel.INFO);
        when(joinPoint.getArgs()).thenReturn(TEST_ARGS);
        when(joinPoint.proceed()).thenReturn(TEST_RESULT);

        // Act
        Object result = aspect.logMethod(joinPoint, auditLog);

        // Assert
        assertThat(result).isEqualTo(TEST_RESULT);

        ArgumentCaptor<AuditLogEntry> entryCaptor = ArgumentCaptor.forClass(AuditLogEntry.class);
        verify(logger, times(2)).log(entryCaptor.capture());

        List<AuditLogEntry> loggedEntries = entryCaptor.getAllValues();

        MethodAuditLogEntry startEntry = (MethodAuditLogEntry) loggedEntries.getFirst();
        assertThat(startEntry.getLogLevel()).isEqualTo(LogLevel.INFO);
        assertThat(startEntry.getEventType()).isEqualTo(EventType.START);
        assertThat(startEntry.getMethodName()).isEqualTo(TEST_METHOD_NAME);
        assertThat(startEntry.getArgs()).isEqualTo(TEST_ARGS);

        MethodAuditLogEntry endEntry = (MethodAuditLogEntry) loggedEntries.get(1);
        assertThat(endEntry.getLogLevel()).isEqualTo(LogLevel.INFO);
        assertThat(endEntry.getEventType()).isEqualTo(EventType.END);
        assertThat(endEntry.getMethodName()).isEqualTo(TEST_METHOD_NAME);
        assertThat(endEntry.getResult()).isEqualTo(TEST_RESULT);
    }

    @Test
    void logMethod_shouldLogStartAndErrorOnException() throws Throwable {
        // Arrange
        when(auditLog.logLevel()).thenReturn(LogLevel.DEBUG);
        when(joinPoint.getArgs()).thenReturn(TEST_ARGS);
        when(joinPoint.proceed()).thenThrow(new RuntimeException(TEST_ERROR_MSG));

        // Act & Assert
        assertThatThrownBy(() -> aspect.logMethod(joinPoint, auditLog))
                .isInstanceOf(RuntimeException.class);

        ArgumentCaptor<AuditLogEntry> entryCaptor = ArgumentCaptor.forClass(AuditLogEntry.class);
        verify(logger, times(2)).log(entryCaptor.capture());

        List<AuditLogEntry> loggedEntries = entryCaptor.getAllValues();

        MethodAuditLogEntry startEntry = (MethodAuditLogEntry) loggedEntries.getFirst();
        assertThat(startEntry.getLogLevel()).isEqualTo(LogLevel.DEBUG);

        MethodAuditLogEntry errorEntry = (MethodAuditLogEntry) loggedEntries.get(1);
        assertThat(errorEntry.getLogLevel()).isEqualTo(LogLevel.ERROR);
        assertThat(errorEntry.getEventType()).isEqualTo(EventType.ERROR);
        assertThat(errorEntry.getMethodName()).isEqualTo(TEST_METHOD_NAME);
        assertThat(errorEntry.getErrorMessage()).isEqualTo(TEST_ERROR_MSG);
    }
}