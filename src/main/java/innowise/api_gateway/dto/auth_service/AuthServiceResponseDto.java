package innowise.api_gateway.dto.auth_service;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthServiceResponseDto {
    private Long id;
    private String login;
}
