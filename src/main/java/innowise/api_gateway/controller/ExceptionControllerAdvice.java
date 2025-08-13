package innowise.api_gateway.controller;

import innowise.api_gateway.dto.error.ErrorDto;
import innowise.api_gateway.exception.StatusCodeAbstractException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@ControllerAdvice
public class ExceptionControllerAdvice {
    @ExceptionHandler(StatusCodeAbstractException.class)
    public Mono<ResponseEntity<ErrorDto>> handleHttpStatusCodeException(StatusCodeAbstractException ex, ServerWebExchange exchange) {
        ErrorDto errorDto = ErrorDto.builder()
                .timestamp(LocalDateTime.now())
                .status(ex.getHttpStatus().value())
                .error(ex.getHttpStatus().value() + " " + ex.getHttpStatus().getReasonPhrase())
                .message(ex.getMessage())
                .path(exchange.getRequest().getPath().toString())
                .requestType(exchange.getRequest().getMethod().toString())
                .build();

        return Mono.just(ResponseEntity.status(ex.getHttpStatus()).body(errorDto));
    }
}
