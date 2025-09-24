package com.example.banking.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.banking.model.JournalStatus;

import jakarta.persistence.*;

@Entity
@Table(name = "journal")
public class Journal {
    
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private JournalStatus status;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "external_ref", length = 120, unique = true)
    private String externalRef;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private Instant createdAt;

    @Column(name = "posted_at")
    private Instant postedAt;

    @OneToMany(mappedBy = "journal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LedgerEntry> ledgerEntries = new ArrayList<>();

    public Journal() {}

    public Journal(JournalStatus status, String description, String externalRef) {
        this.status = status;
        this.description = description;
        this.externalRef = externalRef;
    }

    public Journal(UUID id, JournalStatus status, String description, String externalRef, Instant createdAt, Instant postedAt) {
        this.id = id;
        this.status = status;
        this.description = description;
        this.externalRef = externalRef;
        this.createdAt = createdAt;
        this.postedAt = postedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public JournalStatus getStatus() {
        return status;
    }

    public void setStatus(JournalStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExternalRef() {
        return externalRef;
    }

    public void setExternalRef(String externalRef) {
        this.externalRef = externalRef;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(Instant postedAt) {
        this.postedAt = postedAt;
    }

    public List<LedgerEntry> getLedgerEntries() {
        return ledgerEntries;
    }
    
    public void addLedgerEntry(LedgerEntry entry) {
        ledgerEntries.add(entry);
        entry.setJournal(this);
    }

    public void removeLedgerEntry(LedgerEntry entry) {
        ledgerEntries.remove(entry);
        entry.setJournal(null);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Journal)) return false;
        Journal that = (Journal) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() { return getClass().hashCode(); }

}
