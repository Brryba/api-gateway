package innowise.api_gateway.exception.security;

import innowise.api_gateway.exception.StatusCodeAbstractException;
import org.springframework.http.HttpStatus;

public class InvalidJwtTokenException extends StatusCodeAbstractException {
    public InvalidJwtTokenException(String message) {
        super(HttpStatus.UNAUTHORIZED,
                "Invalid JWT token: " + message);
    }
}
