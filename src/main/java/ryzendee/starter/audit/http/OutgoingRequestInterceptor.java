package ryzendee.starter.audit.http;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import ryzendee.starter.audit.logger.ApplicationLogger;
import ryzendee.starter.audit.enums.Direction;
import ryzendee.starter.audit.model.HttpAuditLogEntry;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;


/**
 * Перехватчик для логирования исходящих HTTP-запросов через RestTemplate или WebClient.
 *
 * @author Dmitry Ryazantsev
 */
@RequiredArgsConstructor
public class OutgoingRequestInterceptor implements ClientHttpRequestInterceptor {

    private final ApplicationLogger logger;

    /**
     * Перехватывает и логирует исходящий HTTP-запрос и ответ.
     *
     * @param request исходящий HTTP-запрос
     * @param body тело запроса в байтах
     * @param execution объект для продолжения выполнения запроса
     * @return HTTP-ответ
     * @throws IOException при ошибках ввода-вывода
     */
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {

        long startTime = System.currentTimeMillis();

        ClientHttpResponse response = execution.execute(request, body);

        long duration = System.currentTimeMillis() - startTime;
        String requestBody = new String(body, UTF_8);
        String responseBody = StreamUtils.copyToString(response.getBody(), UTF_8);
        HttpAuditLogEntry logEntry = HttpAuditLogEntry.builder()
                .direction(Direction.OUTGOING)
                .httpMethod(request.getMethod().name())
                .httpStatusCode(response.getStatusCode().value())
                .requestPath(request.getURI().toString())
                .requestBody(requestBody)
                .responseBody(responseBody)
                .durationMs(duration)
                .build();

        logger.log(logEntry);

        return response;
    }

}
