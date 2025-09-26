package com.example.banking.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.banking.entity.Journal;
import com.example.banking.model.JournalStatus;

@Repository
public interface JournalRepository extends JpaRepository<Journal, UUID> {
    
    List<Journal> findByStatusOrderByCreatedAtDesc(JournalStatus status);

    Optional<Journal> findByExternalRef(String externalRef);

    boolean existsByExternalRef(String externalRef);
}
