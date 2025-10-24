package innowise.api_gateway.exception.camunda;

import innowise.api_gateway.exception.StatusCodeAbstractException;
import org.springframework.http.HttpStatus;

public class ProcessBpmnException extends StatusCodeAbstractException {
    public ProcessBpmnException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
