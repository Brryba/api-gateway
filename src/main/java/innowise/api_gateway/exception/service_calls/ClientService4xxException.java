package innowise.api_gateway.exception.service_calls;

import innowise.api_gateway.exception.StatusCodeAbstractException;
import org.springframework.http.HttpStatus;

public class ClientService4xxException extends StatusCodeAbstractException {
    public ClientService4xxException(HttpStatus status, String message) {
        super(status, message);
    }
}
