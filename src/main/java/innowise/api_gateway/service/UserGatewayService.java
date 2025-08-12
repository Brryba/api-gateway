package innowise.api_gateway.service;

import innowise.api_gateway.dto.auth_service.AuthServiceResponseDto;
import innowise.api_gateway.dto.combined.UserRequestDto;
import innowise.api_gateway.dto.combined.UserResponseDto;
import innowise.api_gateway.dto.user_service.UserServiceResponseDto;
import innowise.api_gateway.exception.service_calls.RollbackFailedException;
import innowise.api_gateway.exception.service_calls.UserCreationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserGatewayService {

    private final UserGatewayClient gatewayClient;

    public Mono<UserResponseDto> createUser(UserRequestDto userRequestDto) {
        return gatewayClient.createUserInAuthService(userRequestDto)
                .flatMap(authResponse ->
                        gatewayClient.createUserInUserService(userRequestDto, authResponse.getId())
                                .map(userResponse -> buildUserResponse(authResponse, userResponse))
                                .onErrorResume(e -> handleUserServiceFailure(authResponse, e))
                );
    }

    private UserResponseDto buildUserResponse(AuthServiceResponseDto auth, UserServiceResponseDto user) {
        return UserResponseDto.builder()
                .auth(auth)
                .user(user)
                .build();
    }

    private Mono<UserResponseDto> handleUserServiceFailure(AuthServiceResponseDto authResponse, Throwable e) {
        log.error("UserService failed for authId={}. Rolling back...", authResponse.getId(), e);
        return gatewayClient.rollbackUserInAuthService(authResponse.getId())
                .onErrorResume(rollbackEx -> {
                    log.error("CRITICAL: Rollback failed for authId={}", authResponse.getId(), rollbackEx);
                    return Mono.error(new RollbackFailedException("User creation failed. Try again later."));
                })
                .then(Mono.error(new UserCreationException("User creation failed. Try again later.")));
    }
}
