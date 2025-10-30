package innowise.api_gateway.exception.camunda;

import innowise.api_gateway.exception.StatusCodeAbstractException;
import org.springframework.http.HttpStatus;

public class ProcessNotStartedException extends StatusCodeAbstractException {
    public ProcessNotStartedException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
