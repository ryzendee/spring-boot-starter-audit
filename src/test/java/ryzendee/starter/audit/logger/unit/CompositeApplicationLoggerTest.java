package ryzendee.starter.audit.logger.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ryzendee.starter.audit.logger.ApplicationLogger;
import ryzendee.starter.audit.logger.CompositeApplicationLogger;
import ryzendee.starter.audit.model.AuditLogEntry;

import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CompositeApplicationLoggerTest {

    @InjectMocks
    private CompositeApplicationLogger compositeApplicationLogger;

    @Mock
    private ApplicationLogger mockApplicationLogger;
    @Mock
    private AuditLogEntry auditLogEntry;

    @BeforeEach
    void setUp() {
        compositeApplicationLogger = new CompositeApplicationLogger(List.of(mockApplicationLogger));
    }

    @Test
    void log_shouldCallLogger() {
        compositeApplicationLogger.log(auditLogEntry);

        verify(mockApplicationLogger).log(auditLogEntry);
    }
}
