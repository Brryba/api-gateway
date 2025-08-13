package innowise.api_gateway.service;

import innowise.api_gateway.dto.auth_service.AuthServiceResponseDto;
import innowise.api_gateway.dto.combined.UserRequestDto;
import innowise.api_gateway.dto.user_service.UserServiceResponseDto;
import innowise.api_gateway.exception.service_calls.ClientServiceException;
import innowise.api_gateway.exception.service_calls.InternalServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserGatewayClient {

    private final WebClient userServiceClient;
    private final WebClient authServiceClient;

    public Mono<AuthServiceResponseDto> createUserInAuthService(UserRequestDto userRequestDto) {
        log.info("Requesting user creation in authentication service...");

        return authServiceClient.post()
                .uri("/api/auth/signup")
                .bodyValue(userRequestDto.getAuth())
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, this::handle5xxError)
                .onStatus(HttpStatusCode::is4xxClientError, this::handle4xxError)
                .bodyToMono(AuthServiceResponseDto.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)).filter(this::shouldRetry))

                .doOnError(error ->
                        log.error("Authentication service call failed: {}", error.getMessage(), error))
                .doOnSuccess(authServiceResponse ->
                        log.info("User with id {} created in authentication service", authServiceResponse.getId()));
    }

    public Mono<Void> rollbackUserInAuthService(Long userId) {
        log.info("Requesting user rollback(deletion) in authentication service...");

        return authServiceClient.delete()
                .uri("/api/auth/" + userId)
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, this::handle5xxError)
                .onStatus(HttpStatusCode::is4xxClientError, this::handle4xxError)
                .bodyToMono(Void.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)).filter(this::shouldRetry))

                .doOnError(error ->
                        log.error("Authentication service call failed: {}", error.getMessage()))
                .doOnSuccess(unused ->
                        log.info("User {} deleted in authentication service", userId));
    }

    public Mono<UserServiceResponseDto> createUserInUserService(UserRequestDto userRequestDto, Long userId) {
        log.info("Requesting user creation in user service");

        return userServiceClient.post()
                .uri("/api/user/" + userId)
                .bodyValue(userRequestDto.getUser())
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, this::handle5xxError)
                .onStatus(HttpStatusCode::is4xxClientError, this::handle4xxError)
                .bodyToMono(UserServiceResponseDto.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)).filter(this::shouldRetry))

                .doOnError(error ->
                        log.error("User service call failed: {}", error.getMessage()))
                .doOnSuccess(userServiceResponse ->
                        log.info("User with id {} created in user service", userServiceResponse.getId()));
    }

    private Mono<Throwable> handle5xxError(ClientResponse clientResponse) {
        return Mono.error(new InternalServiceException("Server error"));
    }

    private Mono<Throwable> handle4xxError(ClientResponse clientResponse) {
        return clientResponse.bodyToMono(String.class).flatMap(body -> Mono.error(
                new ClientServiceException(HttpStatus.valueOf(clientResponse.statusCode().value()), body)));
    }

    private boolean shouldRetry(Throwable ex) {
        return ex instanceof WebClientRequestException || ex instanceof InternalServiceException;
    }
}
