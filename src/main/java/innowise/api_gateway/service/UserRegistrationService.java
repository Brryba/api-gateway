package innowise.api_gateway.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import innowise.api_gateway.dto.camunda.RegistrationProcessResponseDto;
import innowise.api_gateway.dto.combined.UserRequestDto;
import innowise.api_gateway.exception.camunda.ProcessNotStartedException;
import innowise.api_gateway.exception.service_calls.BadRequestException;
import io.camunda.client.CamundaClient;
import io.camunda.client.api.response.ProcessInstanceEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationService {

    private final ObjectMapper objectMapper;
    private final CamundaClient camundaClient;

    public Mono<RegistrationProcessResponseDto> createUser(UserRequestDto userRequestDto) {
        String serializedUser, serializedAuth;
        try {
            serializedUser = objectMapper.writeValueAsString(userRequestDto.getUser());
            serializedAuth = objectMapper.writeValueAsString(userRequestDto.getAuth());
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Unable to create user. Bad request");
        }

        log.info("User {} creation request received", userRequestDto.getAuth().getLogin());

        CompletableFuture<ProcessInstanceEvent> startFuture = camundaClient
                .newCreateInstanceCommand()
                .bpmnProcessId("registration_process")
                .latestVersion()
                .variables(Map.of(
                        "userRequest", serializedUser,
                        "authRequest", serializedAuth))
                .send()
                .toCompletableFuture();

        return Mono.fromFuture(startFuture)
                .map(processInstance -> {
                    log.info("Started process {} to create user {}", processInstance.getProcessInstanceKey(), userRequestDto.getAuth().getLogin());

                    return RegistrationProcessResponseDto.builder()
                            .processId(processInstance.getProcessInstanceKey())
                            .build();
                })
                .doOnError(e -> {
                    log.error("Failed to start process for user {} ", userRequestDto.getAuth().getLogin(), e);
                    throw new ProcessNotStartedException("Failed to start process");
                });
    }
}
