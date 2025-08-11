package innowise.api_gateway.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequestDto {
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not be longer than 100 letters")
    private String name;
    @NotBlank(message = "Surname is required")
    @Size(max = 100, message = "Surname must not be longer than 100 letters")
    private String surname;
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;
    @NotBlank(message = "Email is required")
    @Size(max = 100, message = "Email must be shorter than 100 symbols")
    @Email(message = "Incorrect email format")
    private String email;

    @Size(min = 2, message = "Login must not be empty")
    @Size(max = 100, message = "Login must not be longer than 100 symbols")
    private String login;
    @NotEmpty(message = "Password is required")
    @Size(min = 8, message = "Password not secure. Must contain at least 8 symbols")
    private String password;
}
