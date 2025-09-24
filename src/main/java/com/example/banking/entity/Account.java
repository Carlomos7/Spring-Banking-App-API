package com.example.banking.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.*;

@Entity
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false) // FK constraint
    private Customer customer;

    @Column(name = "kind", nullable = false, length = 20)
    private String kind;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "opened_at", nullable = false, updatable = false, insertable = false)
    private Instant openedAt;

    public Account() {
    }

    public Account(Customer customer, String kind, String currency) {
        this.customer = customer;
        this.kind = kind;
        this.currency = currency;
        this.isActive = true; // default to active
    }

    public Account(UUID id, Customer customer, String kind, String currency, boolean isActive, Instant openedAt) {
        this.id = id;
        this.customer = customer;
        this.kind = kind;
        this.currency = currency;
        this.isActive = isActive;
        this.openedAt = openedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public Instant getOpenedAt() {
        return openedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Account))
            return false;
        Account account = (Account) o;
        return id != null && id.equals(account.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
