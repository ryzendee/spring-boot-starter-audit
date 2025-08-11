package ryzendee.starter.audit.http;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import ryzendee.starter.audit.logger.ApplicationLogger;
import ryzendee.starter.audit.enums.Direction;
import ryzendee.starter.audit.model.HttpAuditLogEntry;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutgoingRequestInterceptorTest {

    private static final String TEST_URL = "http://example.com/api";
    private static final String TEST_REQUEST_BODY = "request body";
    private static final String TEST_RESPONSE_BODY = "response body";
    private static final int TEST_STATUS_CODE = 200;
    private static final HttpMethod TEST_METHOD = HttpMethod.GET;

    @Mock
    private ApplicationLogger logger;
    @Mock
    private HttpRequest request;
    @Mock
    private ClientHttpRequestExecution execution;
    @Mock
    private ClientHttpResponse response;

    @InjectMocks
    private OutgoingRequestInterceptor interceptor;

    @Test
    void intercept_shouldLogRequestAndResponse() throws IOException {
        // Arrange
        when(request.getMethod()).thenReturn(TEST_METHOD);
        when(request.getURI()).thenReturn(URI.create(TEST_URL));

        byte[] requestBody = TEST_REQUEST_BODY.getBytes();
        when(response.getStatusCode()).thenReturn(org.springframework.http.HttpStatus.valueOf(TEST_STATUS_CODE));
        when(response.getBody()).thenReturn(new ByteArrayInputStream(TEST_RESPONSE_BODY.getBytes()));

        when(execution.execute(request, requestBody)).thenReturn(response);

        // Act
        ClientHttpResponse result = interceptor.intercept(request, requestBody, execution);

        // Assert
        assertThat(result).isSameAs(response);

        ArgumentCaptor<HttpAuditLogEntry> entryCaptor = ArgumentCaptor.forClass(HttpAuditLogEntry.class);
        verify(logger).log(entryCaptor.capture());

        HttpAuditLogEntry loggedEntry = entryCaptor.getValue();
        assertThat(loggedEntry.getDirection()).isEqualTo(Direction.OUTGOING);
        assertThat(loggedEntry.getHttpMethod()).isEqualTo(TEST_METHOD.name());
        assertThat(loggedEntry.getHttpStatusCode()).isEqualTo(TEST_STATUS_CODE);
        assertThat(loggedEntry.getRequestPath()).isEqualTo(TEST_URL);
        assertThat(loggedEntry.getRequestBody()).isEqualTo(TEST_REQUEST_BODY);
        assertThat(loggedEntry.getResponseBody()).isEqualTo(TEST_RESPONSE_BODY);
        assertThat(loggedEntry.getDurationMs()).isGreaterThanOrEqualTo(0);
    }
}