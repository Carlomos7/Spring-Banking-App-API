package com.example.banking.dto;

import java.time.Instant;
import java.util.UUID;

public record CustomerResponseDTO(UUID id, String username, String email, String firstName, String lastName, Instant createdAt) {}
