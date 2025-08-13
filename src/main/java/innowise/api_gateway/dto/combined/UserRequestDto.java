package innowise.api_gateway.dto.combined;

import innowise.api_gateway.dto.auth_service.AuthServiceRequestDto;
import innowise.api_gateway.dto.user_service.UserServiceRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "User is missing")
    UserServiceRequestDto user;
    @Valid
    @NotNull(message = "Auth is missing")
    AuthServiceRequestDto auth;
}
