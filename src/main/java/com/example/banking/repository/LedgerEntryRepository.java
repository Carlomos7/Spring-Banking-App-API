package com.example.banking.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.banking.entity.LedgerEntry;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, UUID> {
    
    // all entriers for a given account
    List<LedgerEntry> findByAccountId(UUID accountId);

    // all entries for a given journal
    List<LedgerEntry> findByJournalId(UUID journalId);
}
