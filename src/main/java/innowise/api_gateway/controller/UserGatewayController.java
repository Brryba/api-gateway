package innowise.api_gateway.controller;

import innowise.api_gateway.dto.combined.UserRequestDto;
import innowise.api_gateway.dto.combined.UserResponseDto;
import innowise.api_gateway.service.UserRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserGatewayController {
    private final UserRegistrationService userRegistrationService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserResponseDto> signup(@Valid @RequestBody UserRequestDto userRequestDto) {
        return userRegistrationService.createUser(userRequestDto)
                .then(Mono.just(new UserResponseDto()));
    }
}
