package innowise.api_gateway.exception.camunda;

import innowise.api_gateway.exception.StatusCodeAbstractException;
import org.springframework.http.HttpStatus;

public class ProcessFailedStateException extends StatusCodeAbstractException {
    public ProcessFailedStateException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
