package innowise.api_gateway.dto.camunda;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegistrationProcessResponseDto {
    private Long processId;
}
