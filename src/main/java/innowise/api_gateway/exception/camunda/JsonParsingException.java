package innowise.api_gateway.exception.camunda;

import innowise.api_gateway.exception.StatusCodeAbstractException;
import org.springframework.http.HttpStatus;

public class JsonParsingException extends StatusCodeAbstractException {
    public JsonParsingException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
