package innowise.api_gateway.controller;

import innowise.api_gateway.dto.UserRequestDto;
import innowise.api_gateway.dto.UserResponseDto;
import innowise.api_gateway.service.UserGatewayService;
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
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserGatewayController {
    private final UserGatewayService userGatewayService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserResponseDto> signup(@Valid @RequestBody UserRequestDto userRequestDto) {
        return userGatewayService.createUser(Mono.fromSupplier(() -> userRequestDto));
    }
}
