package innowise.api_gateway.dto.combined;

import innowise.api_gateway.dto.auth_service.AuthServiceResponseDto;
import innowise.api_gateway.dto.user_service.UserServiceResponseDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDto {
    @Valid
    UserServiceResponseDto user;
    @Valid
    AuthServiceResponseDto auth;
}
