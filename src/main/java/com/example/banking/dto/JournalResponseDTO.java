package com.example.banking.dto;

import java.time.Instant;
import java.util.UUID;

import com.example.banking.entity.Journal;
import com.example.banking.model.JournalStatus;

public record JournalResponseDTO(
    UUID id,
    JournalStatus status,
    String description,
    String externalRef,
    Instant createdAt,
    Instant postedAt,
    boolean balanced,
    String currency,
    long debitTotalCents,
    long creditTotalCents,
    long netCents
) {
    public static JournalResponseDTO of(Journal j, boolean balanced, String currency,
            long debitTotal, long creditTotal, long net) {
        return new JournalResponseDTO(
            j.getId(),
            j.getStatus(),
            j.getDescription(),
            j.getExternalRef(),
            j.getCreatedAt(),
            j.getPostedAt(),
            balanced,
            currency,
            debitTotal,
            creditTotal,
            net
        );
    }
}
