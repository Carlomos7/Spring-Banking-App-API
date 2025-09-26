package com.example.banking.dto;
import jakarta.validation.constraints.*;

public record RegisterRequestDTO(

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username must contain only letters, numbers and underscore")
    String username,

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    String firstName,

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    String lastName,

    @NotBlank(message = "Email is required")
    @Size(max = 320, message = "Email must not exceed 320 characters")
    @Email(message = "Invalid email format")
    String email,

    @NotBlank @Size(min=12, max=60)
    @Pattern(regexp="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s]).{12,}$",
             message="Password must be 12+ chars with upper, lower, digit, and symbol")
    String password
) {
    // Helper to create a new instance
    public static RegisterRequestDTO of(String username, String firstName, String lastName, String email, String password) {
        return new RegisterRequestDTO(username, firstName, lastName, email, password);
    }
}
