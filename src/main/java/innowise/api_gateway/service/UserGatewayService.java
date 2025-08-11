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

@Service
@RequiredArgsConstructor
@Slf4j
public class UserGatewayService {
    private final WebClient userServiceClient;
    private final WebClient authServiceClient;

    public Mono<UserResponseDto> createUser(UserRequestDto userRequestDto) {
        return createUserInAuthService(userRequestDto)
                .flatMap(authResponse -> {
                            log.info("User with id {} created in user authentication service", authResponse.getId());

                            return createUserInUserService(userRequestDto, authResponse.getId())
                                    .map(userResponse -> {
                                        log.info("User with id {} created in user authentication service", authResponse.getId());

                                        return UserResponseDto.builder()
                                                .auth(authResponse)
                                                .user(userResponse)
                                                .build();
                                    });
                        }
                );
    }

    private Mono<AuthServiceResponseDto> createUserInAuthService(UserRequestDto userRequestDto) {
        log.info("Requesting user creation in user service");

        try {
            return authServiceClient.post()
                    .uri("/auth/signup")
                    .bodyValue(userRequestDto.getAuth())
                    .retrieve()
                    .bodyToMono(AuthServiceResponseDto.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return Mono.error(e);
        }
    }

    private Mono<UserServiceResponseDto> createUserInUserService(UserRequestDto userRequestDto, Long userId) {
        log.info("Requesting user creation in authentication service");

        return userServiceClient.post()
                .uri("/auth/user/" + userId)
                .bodyValue(userRequestDto.getAuth())
                .retrieve()
                .bodyToMono(UserServiceResponseDto.class);
    }
}
