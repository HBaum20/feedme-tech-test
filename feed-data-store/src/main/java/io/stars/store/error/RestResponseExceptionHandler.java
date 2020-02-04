package io.stars.store.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Collections;
import java.util.UUID;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@ControllerAdvice
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler
{

    private static final String MALFORMED_EVENT_MESSAGE = "Provided request is incorrectly formed: %s";

    @ExceptionHandler(MalformedEventException.class)
    protected ResponseEntity<Object> handleMalformedEventException(final Exception ex)
    {
        final UUID errorId = UUID.randomUUID();
        final ApplicationErrors msg = getErrors(errorId, ex, BAD_REQUEST, format(MALFORMED_EVENT_MESSAGE, ex.getMessage()));
        final HttpHeaders headers = getHeaders(format(MALFORMED_EVENT_MESSAGE, errorId));

        log.debug(format(MALFORMED_EVENT_MESSAGE, errorId), ex);
        return handleExceptionInternal(ex, msg, headers, BAD_REQUEST, null);
    }

    private static HttpHeaders getHeaders()
    {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        return headers;
    }

    private static HttpHeaders getHeaders(final String message)
    {
        final HttpHeaders headers = getHeaders();
        headers.add("X-Message", message);
        return headers;
    }

    private static ApplicationErrors getErrors(final UUID errorId, final Exception ex, final HttpStatus httpStatus, final String message)
    {
        return ApplicationErrors.builder()
                                .errors(Collections.singletonList(
                                        ApplicationError.builder()
                                                        .status(httpStatus.value())
                                                        .errorId(errorId.toString())
                                                        .errorType(ex.getClass().getSimpleName())
                                                        .errorMessage(message)
                                                        .build()
                                ))
                                .build();
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            final Exception ex,
            final Object body,
            final HttpHeaders headers,
            final HttpStatus status,
            final WebRequest request
    )
    {
        return new ResponseEntity<>(body, headers, status);
    }
}
