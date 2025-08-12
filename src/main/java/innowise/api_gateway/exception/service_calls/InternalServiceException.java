package innowise.api_gateway.exception.service_calls;

import innowise.api_gateway.exception.StatusCodeAbstractException;
import org.springframework.http.HttpStatus;

public class InternalServiceException extends StatusCodeAbstractException {
    public InternalServiceException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
