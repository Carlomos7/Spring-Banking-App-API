package com.example.banking.dto;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.*;

public record LoginResponseDTO(

    @NotNull(message = "User ID cannot be null")
    UUID userId,

    @NotNull(message = "Username cannot be null")
    String username,

    @NotNull(message = "Email cannot be null")
    String email,

    @NotNull(message = "First name cannot be null")
    String firstName,

    @NotNull(message = "Last name cannot be null")
    String lastName//,

    // TODO: Implement tokens
) {
    // Helper method to create a new instance
    public static LoginResponseDTO of(
            UUID userId,
            String username,
            String email,
            String firstName,
            String lastName
    ) {
        return new LoginResponseDTO(
                userId,
                username,
                email,
                firstName,
                lastName
        );
    }
}