package innowise.api_gateway.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public abstract class StatusCodeAbstractException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String message;
}

