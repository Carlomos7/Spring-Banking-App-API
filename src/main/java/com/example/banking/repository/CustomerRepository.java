package com.example.banking.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.banking.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID>{

    Optional<Customer> findByEmail(String email);
    boolean existsByEmail(String email);

}
