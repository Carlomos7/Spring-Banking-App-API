package com.example.banking.dto;

import java.time.Instant;
import java.util.UUID;

import com.example.banking.entity.Account;

public record AccountResponseDTO(
    UUID id,
    UUID customerId,
    String kind,
    String currency,
    boolean active,
    Instant openedAt
) {
    public static AccountResponseDTO of(Account a) {
        return new AccountResponseDTO(
            a.getId(),
            a.getCustomer().getId(),
            a.getKind(),
            a.getCurrency(),
            a.isActive(),
            a.getOpenedAt()
        );
    }
}
