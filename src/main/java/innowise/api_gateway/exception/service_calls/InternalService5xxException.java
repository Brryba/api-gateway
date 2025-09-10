package innowise.api_gateway.exception.service_calls;

import innowise.api_gateway.exception.StatusCodeAbstractException;
import org.springframework.http.HttpStatus;

public class InternalService5xxException extends StatusCodeAbstractException {
    public InternalService5xxException(String message) {
        super(HttpStatus.BAD_GATEWAY, message);
    }
}
