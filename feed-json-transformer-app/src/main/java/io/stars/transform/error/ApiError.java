package io.stars.transform.error;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class ApiError
{
    private HttpStatus status;
    private LocalDateTime timestamp;
    private String message;
    private String debugMessage;

    private ApiError()
    {
        timestamp = LocalDateTime.now();
    }

    public ApiError(final HttpStatus status)
    {
        this();
        this.status = status;
    }

    public ApiError(final HttpStatus status, Throwable ex)
    {
        this();
        this.status = status;
        this.message = "API experienced an error";
        this.debugMessage = ex.getLocalizedMessage();
    }

    public ApiError(HttpStatus status, String message, Throwable ex)
    {
        this();
        this.status = status;
        this.message = message;
        this.debugMessage = ex.getLocalizedMessage();
    }
}
