package ryzendee.starter.audit.logger.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ryzendee.starter.audit.enums.LogLevel;
import ryzendee.starter.audit.logger.FileApplicationLogger;
import ryzendee.starter.audit.model.AuditLogEntry;
import ryzendee.starter.audit.util.AuditLogEntryFormatter;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileApplicationLoggerTest {

    private FileApplicationLogger fileApplicationLogger;

    @Mock
    private AuditLogEntryFormatter mockAuditLogEntryFormatter;
    @Mock
    private AuditLogEntry mockAuditLogEntry;

    @BeforeEach
    void setUp() {
        fileApplicationLogger = new FileApplicationLogger(List.of(mockAuditLogEntryFormatter));
    }

    @Test
    void format_shouldFormat() {
        when(mockAuditLogEntry.getLogLevel()).thenReturn(LogLevel.INFO);
        when(mockAuditLogEntryFormatter.supports(mockAuditLogEntry)).thenReturn(true);
        when(mockAuditLogEntryFormatter.format(mockAuditLogEntry)).thenReturn("test-log");

        fileApplicationLogger.log(mockAuditLogEntry);

        verify(mockAuditLogEntry).getLogLevel();
        verify(mockAuditLogEntryFormatter).supports(mockAuditLogEntry);
        verify(mockAuditLogEntryFormatter).format(mockAuditLogEntry);
    }
}
