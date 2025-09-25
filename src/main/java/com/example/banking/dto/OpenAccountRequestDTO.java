package com.example.banking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OpenAccountRequestDTO(
        @NotBlank @Size(max = 32) String kind,
        @NotBlank @Size(min = 3, max = 3) String currency) {
}
