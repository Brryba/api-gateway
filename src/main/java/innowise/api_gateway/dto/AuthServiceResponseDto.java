package innowise.api_gateway.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthServiceResponseDto {
    private Long id;
    private String login;
}
