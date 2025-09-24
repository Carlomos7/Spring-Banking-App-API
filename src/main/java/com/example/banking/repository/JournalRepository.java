package com.example.banking.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.banking.entity.Journal;

@Repository
public interface JournalRepository extends JpaRepository<Journal, UUID> {

    // Find by external reference (idempotency for transactions)
    Optional<Journal> findByExternalRef(String externalRef);

}
