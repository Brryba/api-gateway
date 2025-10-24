package innowise.api_gateway.exception.camunda;

import innowise.api_gateway.exception.StatusCodeAbstractException;
import org.springframework.http.HttpStatus;

public class ProcessNotFoundException extends StatusCodeAbstractException {
    public ProcessNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
