package innowise.api_gateway.dto.camunda;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CamundaVariableDto {
    private Object value;
    private String type;
}
