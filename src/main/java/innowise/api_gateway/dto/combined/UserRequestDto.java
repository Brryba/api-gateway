package innowise.api_gateway.dto.combined;

import innowise.api_gateway.dto.auth_service.AuthServiceRequestDto;
import innowise.api_gateway.dto.user_service.UserServiceRequestDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequestDto {
    @Valid
    UserServiceRequestDto user;
    @Valid
    AuthServiceRequestDto auth;
}
