package innowise.api_gateway.exception;

public class InvalidJwtTokenException extends RuntimeException {
    public InvalidJwtTokenException(String message) {
        super("Invalid JWT token: " + message);
    }
}
