package com.example.banking.service;

import com.example.banking.entity.Customer;
import com.example.banking.exceptions.BankingExceptions.CustomerNotFoundException;
import com.example.banking.exceptions.BankingExceptions.EmailAlreadyExistsException;
import com.example.banking.repository.CustomerRepository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Customer> getById(UUID id) {
        return customerRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Customer> getByUsername(String username) {
        return customerRepository.findByUsername(username);
    }

    public Customer requireById(UUID id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id.toString()));
    }

    @Transactional(readOnly = true)
    public Optional<Customer> getByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    @Transactional
    public Customer updateProfile(UUID id, String firstName, String lastName, String email) {
        var c = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id.toString()));
        
        var normalizedEmail = email.trim().toLowerCase();
        if (!c.getEmail().equals(normalizedEmail) && customerRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyExistsException(normalizedEmail);
        }

        c.setFirstName(firstName);
        c.setLastName(lastName);
        c.setEmail(normalizedEmail);

        return customerRepository.save(c);
    }

    // TODO: Add method for deactivation
}
