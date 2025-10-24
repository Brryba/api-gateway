package innowise.api_gateway.controller;

import innowise.api_gateway.dto.error.ErrorDto;
import innowise.api_gateway.dto.error.ValidationErrorDto;
import innowise.api_gateway.exception.StatusCodeAbstractException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class ExceptionControllerAdvice {
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ValidationErrorDto>> handleValidationExceptions(
            WebExchangeBindException ex, ServerWebExchange exchange) {
        List<String> errors = new ArrayList<>();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            errors.add(error.getDefaultMessage());
        }

        ValidationErrorDto validationError = ValidationErrorDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.value() + " " + HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Validation Error")
                .validationErrors(errors)
                .path(exchange.getRequest().getPath().toString())
                .requestType(exchange.getRequest().getMethod().toString())
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationError));
    }

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

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto));
    }
}
