package com.example.banking.service;

import com.example.banking.entity.Customer;
import com.example.banking.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Customer createCustomer(Customer customer) {
        // basic null checks
        if (customer == null) {
            throw new IllegalArgumentException("Customer is required");
        }
        String fullName = safeTrim(customer.getFullName());
        String email    = safeTrim(customer.getEmail());

        if (fullName == null || fullName.isEmpty()) {
            throw new IllegalArgumentException("Full name is required");
        }
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        // normalize email
        email = email.toLowerCase();
        customer.setFullName(fullName);
        customer.setEmail(email);

        // uniqueness check
        if (customerRepository.findByEmail(email).isPresent()) {
            throw new IllegalStateException("Email already registered");
        }

        return customerRepository.save(customer);
    }

    private String safeTrim(String s) {
        return s == null ? null : s.trim();
    }
}
