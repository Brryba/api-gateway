package innowise.api_gateway.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import innowise.api_gateway.dto.camunda.RegistrationProcessResponseDto;
import innowise.api_gateway.dto.combined.UserRequestDto;
import innowise.api_gateway.exception.service_calls.BadRequestException;
import io.camunda.client.CamundaClient;
import io.camunda.client.api.response.ProcessInstanceEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationService {
    @Value("${camunda.process.registration.maxAttempts}")
    private Integer maxAttempts;

    @Value("${camunda.process.registration.pullInterval}")
    private Integer pullInterval;

    private final ObjectMapper objectMapper;
    private final CamundaClient camundaClient;

    public Mono<ResponseEntity<RegistrationProcessResponseDto>> createUser(UserRequestDto userRequestDto) {
        String serializedUser, serializedAuth;
        try {
            serializedUser = objectMapper.writeValueAsString(userRequestDto.getUser());
            serializedAuth = objectMapper.writeValueAsString(userRequestDto.getAuth());
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Unable to create user. Bad request");
        }

        log.info("User {} creation request received", userRequestDto.getUser().getName());

        ProcessInstanceEvent processInstance = camundaClient
                .newCreateInstanceCommand()
                .bpmnProcessId("registration_process")
                .latestVersion()
                .variables(Map.of("userRequest", serializedUser,
                        "authRequest", serializedAuth))
                .send()
                .join();

        log.info("Started Registration Process {}", processInstance.getBpmnProcessId());

        return null;
    }

//        CamundaVariableDto userRequestVariable = CamundaVariableDto.builder()
//                .value(serializedUser)
//                .type("Json")
//                .build();
//
//        CamundaVariableDto authRequestVariable = CamundaVariableDto.builder()
//                .value(serializedAuth)
//                .type("Json")
//                .build();
//
//        StartRegistrationRequestDto camundaRequestDto = StartRegistrationRequestDto.builder()
//                .variables(Map.of(
//                        "userRequest", userRequestVariable,
//                        "authRequest", authRequestVariable))
//                .businessKey("user-registration")
//                .withVariablesInReturn(true)
//                .build();
//
//        return camundaClient.post()
//                .uri("/engine-rest/process-definition/key/registration_process/start")
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(camundaRequestDto)
//                .retrieve()
//                .bodyToMono(ProcessStartDto.class)
//                .flatMap(process -> {
//                    log.info("User creation process started with {} id", process.getId());
//                    return pullRegistrationResultUntilCreated(process.getId());
//                });
//    }
//
//    public Mono<ResponseEntity<RegistrationProcessResponseDto>> pullRegistrationResultUntilCreated(UUID processId) {
//        return Flux.interval(Duration.ofSeconds(pullInterval))
//                .take(maxAttempts)
//                .flatMap(i -> checkCreationProcessStatus(processId))
//                .filter(dto -> "COMPLETED".equalsIgnoreCase(dto.getState()))
//                .next()
//                .map(ResponseEntity::ok)
//                .switchIfEmpty(
//                        Mono.just(
//                                ResponseEntity.accepted().body(
//                                        RegistrationProcessResponseDto.builder()
//                                                .state("IN_PROGRESS")
//                                                .processId(processId)
//                                                .build()
//                                )
//                        )
//                );
//    }
//
//
//    public Mono<RegistrationProcessResponseDto> checkCreationProcessStatus(UUID processId) {
//        return camundaClient.get()
//                .uri("/engine-rest/history/process-instance/" + processId)
//                .retrieve()
//                .bodyToMono(JsonNode.class)
//                .switchIfEmpty(Mono.error(new ProcessNotFoundException(processId + " process was not found")))
//                .flatMap(responseNode -> {
//                    String state = responseNode.get("state").asText();
//                    log.info("Polling process {}: current state is {}", processId, state);
//
//                    return switch (state) {
//                        case "COMPLETED" -> camundaClient.get()
//                                .uri("/engine-rest/history/variable-instance?processInstanceId=" +
//                                        processId + "&variableNames=error,authResponse,userResponse")
//                                .retrieve()
//                                .bodyToMono(JsonNode.class)
//                                .map(this::parseCompletedVariables);
//                        case "RUNNING", "ACTIVE" -> Mono.just(RegistrationProcessResponseDto.builder()
//                                .state("ACTIVE")
//                                .build());
//                        default -> Mono.error(new ProcessFailedStateException("Process ended with state: " + state));
//                    };
//                });
//    }
//
//    private RegistrationProcessResponseDto parseCompletedVariables(JsonNode variables) {
//        String error = readVariable(variables, "error");
//        if (error != null && !error.isEmpty()) {
//            throw new ProcessBpmnException(error);
//        }
//
//        String authResponse = readVariable(variables, "authResponse");
//        String userResponse = readVariable(variables, "userResponse");
//
//        try {
//            return RegistrationProcessResponseDto.builder()
//                    .state("COMPLETED")
//                    .user(UserResponseDto.builder()
//                            .auth(objectMapper.readValue(authResponse, AuthServiceResponseDto.class))
//                            .user(objectMapper.readValue(userResponse, UserServiceResponseDto.class))
//                            .build())
//                    .build();
//        } catch (JsonProcessingException e) {
//            throw new JsonParsingException("Unable to parse completed variables. Try again later.");
//        }
//    }
//
//    private String readVariable(JsonNode variables, String name) {
//        for (JsonNode node : variables) {
//            if (name.equals(node.path("name").asText(null))) {
//                return node.path("value").asText(null);
//            }
//        }
//        return null;
//    }
}
