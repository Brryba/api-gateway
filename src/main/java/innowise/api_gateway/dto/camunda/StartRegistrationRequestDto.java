package innowise.api_gateway.dto.camunda;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StartRegistrationRequestDto {
    private Map<String, CamundaVariableDto> variables;
    private String businessKey;
    private boolean withVariablesInReturn;
}
