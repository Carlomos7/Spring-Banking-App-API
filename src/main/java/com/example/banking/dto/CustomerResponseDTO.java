package com.example.banking.dto;

import java.time.Instant;
import java.util.UUID;

public record CustomerResponseDTO(UUID id, String username, String email, String firstName, String lastName, Instant createdAt) {
    // Helper to create a new instance
    public static CustomerResponseDTO of(UUID id, String username, String email, String firstName, String lastName, Instant createdAt) {
        return new CustomerResponseDTO(id, username, email, firstName, lastName, createdAt);
    }
}
