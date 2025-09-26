package com.example.banking.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.banking.entity.LedgerEntry;
import com.example.banking.repository.LedgerEntryRepository;

@Service
public class LedgerEntryService {
    
    private final LedgerEntryRepository ledgerEntryRepository;

    public LedgerEntryService(LedgerEntryRepository ledgerEntryRepository) {
        this.ledgerEntryRepository = ledgerEntryRepository;
    }

    @Transactional(readOnly = true)
    public List<LedgerEntry> accountHistory(UUID accountId, int page, int size) {
        return ledgerEntryRepository.findByAccount_IdOrderByCreatedAtDesc(accountId, PageRequest.of(page, size));
    }
}
