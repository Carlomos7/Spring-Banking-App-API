package com.example.banking.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.banking.entity.Account;
import com.example.banking.entity.Customer;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    // Find all accounts for a specific customer
    List<Account> findByCustomer(Customer customer);

    // Find all accounts by customerId
    List<Account> findByCustomerId(UUID customerId);

    //Find all active accounts
    List<Account> findByIsActiveTrue();
}
