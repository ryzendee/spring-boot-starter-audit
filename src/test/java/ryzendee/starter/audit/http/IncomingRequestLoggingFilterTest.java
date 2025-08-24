package ryzendee.starter.audit.http;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import ryzendee.starter.audit.logger.ApplicationLogger;
import ryzendee.starter.audit.enums.Direction;
import ryzendee.starter.audit.model.HttpAuditLogEntry;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncomingRequestLoggingFilterTest {

    private static final String TEST_PATH = "/api/test";
    private static final String TEST_METHOD = "GET";
    private static final int TEST_STATUS = 200;
    private static final String TEST_QUERY = "param=value";
    private static final String EMPTY_BODY = "{}";

    @InjectMocks
    private IncomingRequestLoggingFilter filter;

    @Mock
    private ApplicationLogger logger;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @Test
    void doFilterInternal_shouldLogRequestAndResponse() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn(TEST_PATH);
        when(request.getMethod()).thenReturn(TEST_METHOD);
        when(response.getStatus()).thenReturn(TEST_STATUS);

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        // Act
        filter.doFilterInternal(wrappedRequest, wrappedResponse, filterChain);

        // Assert
        verify(filterChain).doFilter(any(ContentCachingRequestWrapper.class), any(ContentCachingResponseWrapper.class));

        ArgumentCaptor<HttpAuditLogEntry> entryCaptor = ArgumentCaptor.forClass(HttpAuditLogEntry.class);
        verify(logger).log(entryCaptor.capture());

        HttpAuditLogEntry capturedEntry = entryCaptor.getValue();
        assertThat(capturedEntry.getDirection()).isEqualTo(Direction.INCOMING);
        assertThat(capturedEntry.getHttpMethod()).isEqualTo(TEST_METHOD);
        assertThat(capturedEntry.getHttpStatusCode()).isEqualTo(TEST_STATUS);
        assertThat(capturedEntry.getRequestPath()).isEqualTo(TEST_PATH);
        assertThat(capturedEntry.getRequestBody()).isEqualTo(EMPTY_BODY);
        assertThat(capturedEntry.getResponseBody()).isEqualTo(EMPTY_BODY);
        assertThat(capturedEntry.getDurationMs()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void doFilterInternal_withQueryParams_shouldIncludeInPath() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn(TEST_PATH);
        when(request.getQueryString()).thenReturn(TEST_QUERY);

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        // Act
        filter.doFilterInternal(wrappedRequest, wrappedResponse, filterChain);

        // Assert
        verify(filterChain).doFilter(any(ContentCachingRequestWrapper.class), any(ContentCachingResponseWrapper.class));

        ArgumentCaptor<HttpAuditLogEntry> entryCaptor = ArgumentCaptor.forClass(HttpAuditLogEntry.class);
        verify(logger).log(entryCaptor.capture());

        assertThat(entryCaptor.getValue().getRequestPath())
                .isEqualTo(TEST_PATH + "?" + TEST_QUERY);
    }
}