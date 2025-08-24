package ryzendee.starter.audit.http;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import ryzendee.starter.audit.logger.ApplicationLogger;
import ryzendee.starter.audit.enums.Direction;
import ryzendee.starter.audit.model.HttpAuditLogEntry;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Фильтр для логирования входящих HTTP-запросов и ответов.
 *
 * @author Dmitry Ryazantsev
 */
@RequiredArgsConstructor
public class IncomingRequestLoggingFilter extends OncePerRequestFilter {

    private static final String EMPTY_BODY = "{}";

    private final ApplicationLogger logger;

    /**
     * Фильтр для логирования входящих HTTP-запросов и ответов.
     * Использует {@link ContentCachingRequestWrapper} и {@link ContentCachingResponseWrapper} для
     * кеширования тела запросов и ответов.
     *
     * Логирует метод, путь, тело запроса и ответа, HTTP статус и время обработки.
     *
     * @author Dmitry Ryazantsev
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            String requestBody = getBody(wrappedRequest.getContentAsByteArray());
            String responseBody = getBody(wrappedResponse.getContentAsByteArray());
            HttpAuditLogEntry logEntry = buildLogEntry(wrappedRequest, wrappedResponse, requestBody, responseBody, duration);

            logger.log(logEntry);

            wrappedResponse.copyBodyToResponse();
        }
    }

    private String getBody(byte[] content) {
        return content.length == 0 ? EMPTY_BODY : new String(content, UTF_8);
    }

    private HttpAuditLogEntry buildLogEntry(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response,
                                            String requestBody, String responseBody, long duration) {

        return HttpAuditLogEntry.builder()
                .direction(Direction.INCOMING)
                .httpMethod(request.getMethod())
                .httpStatusCode(response.getStatus())
                .requestPath(getFullRequestPath(request))
                .requestBody(requestBody)
                .responseBody(responseBody)
                .durationMs(duration)
                .build();
    }

    private String getFullRequestPath(HttpServletRequest request) {
        String queryString = request.getQueryString();
        return queryString == null ? request.getRequestURI() : request.getRequestURI() + "?" + queryString;
    }

}
