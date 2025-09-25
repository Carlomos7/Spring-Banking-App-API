package com.example.banking.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
    @NotBlank(message = "Username or Email is required")
    String identifier,   // username OR email
    @NotBlank(message = "Password is required")
    String password
) {

    // Helper to create a new instance
    public static LoginRequestDTO of(String identifier, String password) {
        return new LoginRequestDTO(identifier, password);
    }
}