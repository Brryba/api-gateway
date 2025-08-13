package innowise.api_gateway.exception.service_calls;

import innowise.api_gateway.exception.StatusCodeAbstractException;
import org.springframework.http.HttpStatus;

public class ClientServiceException extends StatusCodeAbstractException {
    public ClientServiceException(HttpStatus status, String message) {
        super(status, message);
    }
}
