package io.stars.transform.error;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestResponseExceptionHandler
    extends ResponseEntityExceptionHandler
{
    @ExceptionHandler({TCPClientInitException.class, MessageCreationException.class})
    protected ResponseEntity<ApiError> handleInternalServerError(final Exception ex)
    {
        if(ex instanceof TCPClientInitException) {
            return buildResponseEntity(new ApiError(INTERNAL_SERVER_ERROR, "Error setting up TCP Client", ex));
        } else if(ex instanceof MessageCreationException) {
            return buildResponseEntity(new ApiError(INTERNAL_SERVER_ERROR, "Error parsing data to json", ex));
        } else {
            return buildResponseEntity(new ApiError(INTERNAL_SERVER_ERROR, "An unexpected error occurred", ex));
        }
    }

    private ResponseEntity<ApiError> buildResponseEntity(ApiError apiError)
    {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
