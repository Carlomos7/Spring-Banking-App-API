package com.example.banking.dto;

import java.time.Instant;
import java.util.UUID;

import com.example.banking.entity.LedgerEntry;

public record LedgerEntryResponseDTO(
    UUID id,
    UUID journalId,
    UUID accountId,
    String side,
    long amountCents,
    String currency,
    Instant createdAt
) {
    public static LedgerEntryResponseDTO of(LedgerEntry e) {
        return new LedgerEntryResponseDTO(
            e.getId(),
            e.getJournal().getId(),
            e.getAccount().getId(),
            e.getSide(),
            e.getAmountCents(),
            e.getCurrency(),
            e.getCreatedAt()
        );
    }
}
