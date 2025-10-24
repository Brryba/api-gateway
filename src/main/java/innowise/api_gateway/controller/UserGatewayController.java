package innowise.api_gateway.controller;

import innowise.api_gateway.dto.camunda.ProcessStartDto;
import innowise.api_gateway.dto.camunda.RegistrationProcessResponseDto;
import innowise.api_gateway.dto.combined.UserRequestDto;
import innowise.api_gateway.service.UserRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserGatewayController {
    private final UserRegistrationService userRegistrationService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<ProcessStartDto> signup(@Valid @RequestBody UserRequestDto userRequestDto) {
        return userRegistrationService.createUser(userRequestDto);
    }

    @GetMapping("/registration/{processId}/status")
    public Mono<RegistrationProcessResponseDto> registration(@PathVariable UUID processId) {
        return userRegistrationService.checkCreationProcessStatus(processId);
    }
}
