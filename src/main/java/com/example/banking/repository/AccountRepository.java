package com.example.banking.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.banking.entity.Account;
// import com.example.banking.entity.Customer;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    // Find all accounts for a specific customer
    // List<Account> findByCustomer(Customer customer);

    // Find all accounts by customerId
    List<Account> findByCustomer_Id(UUID customerId);

    // Find account by id and customerId (to ensure ownership)
    Optional<Account> findByIdAndCustomer_Id(UUID accountId, UUID customerId);

    // Find account by id and customerId, ordered by openedAt descending
    List<Account> findByCustomer_IdOrderByOpenedAtDesc(UUID customerId);

    // Find all active accounts
    List<Account> findByIsActiveTrue();
}
