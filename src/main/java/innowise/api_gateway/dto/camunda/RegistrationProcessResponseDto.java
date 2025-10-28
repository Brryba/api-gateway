package innowise.api_gateway.dto.camunda;

import com.fasterxml.jackson.annotation.JsonInclude;
import innowise.api_gateway.dto.combined.UserResponseDto;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegistrationProcessResponseDto {
    private String state;
    private UserResponseDto user;
    private UUID processId;
}
