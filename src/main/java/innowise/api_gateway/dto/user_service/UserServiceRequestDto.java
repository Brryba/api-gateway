package innowise.api_gateway.dto.user_service;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class UserServiceRequestDto {
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
}
