package innowise.api_gateway.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import innowise.api_gateway.dto.camunda.StartRegistrationRequestDto;
import innowise.api_gateway.dto.camunda.CamundaVariableDto;
import innowise.api_gateway.dto.combined.UserRequestDto;
import innowise.api_gateway.exception.service_calls.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationService {
    private final WebClient camundaClient;
    private final ObjectMapper objectMapper;

    public Mono<Void> createUser(UserRequestDto userRequestDto) {
        String serializedUser;
        try {
            serializedUser = objectMapper.writeValueAsString(userRequestDto);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Unable to create user. Bad request");
        }

        CamundaVariableDto userRequestVariable = CamundaVariableDto.builder()
                .value(serializedUser)
                .type("Json")
                .build();

        StartRegistrationRequestDto camundaRequestDto = StartRegistrationRequestDto.builder()
                .variables(Map.of("userRequest", userRequestVariable))
                .businessKey("user-registration")
                .withVariablesInReturn(true)
                .build();

        return camundaClient.post()
                .uri("/engine-rest/process-definition/key/registration_process/start")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(camundaRequestDto)
                .retrieve()
                .toBodilessEntity()
                .then();
    }
}
