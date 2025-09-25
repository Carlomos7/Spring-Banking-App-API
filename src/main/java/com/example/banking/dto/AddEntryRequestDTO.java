package com.example.banking.dto;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AddEntryRequestDTO(
    @NotNull UUID accountId,
    @NotBlank String side, // "debit" or "credit"
    @Min(1) long amountCents,
    @NotBlank @Size(min=3, max=3) String currency
) {}
