package innowise.api_gateway.service;

import innowise.api_gateway.dto.combined.UserRequestDto;
import innowise.api_gateway.dto.combined.UserResponseDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserGatewayService {
    public Mono<UserResponseDto> createUser(Mono<UserRequestDto> userRequestDto) {
        return null;
    }
}
