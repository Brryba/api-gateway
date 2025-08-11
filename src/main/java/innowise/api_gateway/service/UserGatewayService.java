package innowise.api_gateway.service;

import innowise.api_gateway.dto.auth_service.AuthServiceResponseDto;
import innowise.api_gateway.dto.combined.UserRequestDto;
import innowise.api_gateway.dto.combined.UserResponseDto;
import innowise.api_gateway.dto.user_service.UserServiceResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserGatewayService {
    private final WebClient userServiceClient;
    private final WebClient authServiceClient;

    public Mono<UserResponseDto> createUser(UserRequestDto userRequestDto) {
        return createUserInAuthService(userRequestDto)
                .flatMap(authResponse -> createUserInUserService(userRequestDto, authResponse.getId())
                        .flatMap(userResponse ->
                                confirmUserInAuthService(authResponse.getId())
                                        .map(authConfirmationResponse -> UserResponseDto.builder()
                                                .auth(authResponse)
                                                .user(userResponse)
                                                .build())
                        ).onErrorResume(
                                e -> {
                                    log.error("User creation failed");
                                    return rollbackUserInAuthService(authResponse.getId()).
                                            then(Mono.error(e));
                                }
                        )
                );
    }

    private Mono<AuthServiceResponseDto> createUserInAuthService(UserRequestDto userRequestDto) {
        log.info("Requesting user creation in user service");

        return authServiceClient.post()
                .uri("/api/auth/signup")
                .bodyValue(userRequestDto.getAuth())
                .retrieve()
                .bodyToMono(AuthServiceResponseDto.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
                .doOnError(error -> log.error("Auth service call failed: {}", error.getMessage()))
                .doOnSuccess(authServiceResponse ->
                        log.info("User with id {} created in authentication service", authServiceResponse.getId()));

    }

    private Mono<AuthServiceResponseDto> confirmUserInAuthService(Long userId) {
        log.info("Requesting user confirmation in user service");

        return authServiceClient.patch()
                .uri("/api/auth/" + userId + "/confirm")
                .retrieve()
                .bodyToMono(AuthServiceResponseDto.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
                .doOnError(error -> log.error("Auth service call failed: {}", error.getMessage()))
                .doOnSuccess(authServiceResponse -> {
                    log.info("User {} creation confirmed in authentication service", authServiceResponse.getId());
                });
    }

    private Mono<Void> rollbackUserInAuthService(Long userId) {
        log.info("Requesting user deletion in user service");

        return authServiceClient.patch()
                .uri("/api/auth/" + userId + "/rollback")
                .retrieve()
                .bodyToMono(Void.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
                .doOnError(error -> log.error("Auth service call failed: {}", error.getMessage()))
                .doOnSuccess(authServiceResponse -> {
                    log.info("User {} rollbacked in authentication service", userId);
                });
    }

    private Mono<UserServiceResponseDto> createUserInUserService(UserRequestDto userRequestDto, Long userId) {
        log.info("Requesting user creation in authentication service");

        return userServiceClient.post()
                .uri("/api/user/" + userId)
                .bodyValue(userRequestDto.getUser())
                .retrieve()
                .bodyToMono(UserServiceResponseDto.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
                .doOnError(error -> log.error("User service call failed: {}", error.getMessage()))
                .doOnSuccess(userServiceResponse ->
                        log.info("User with id {} created in user service", userServiceResponse.getId()));
    }
}
