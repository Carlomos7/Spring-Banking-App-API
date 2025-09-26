package com.example.banking.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.banking.dto.CustomerResponseDTO;
import com.example.banking.dto.UpdateProfileRequestDTO;
import com.example.banking.entity.Customer;
import com.example.banking.service.CustomerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/customers")
@Tag(name = "Customers", description = "Endpoints for managing customers")
public class CustomerController {
    
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Operation(summary = "Get customer by ID", description = "Retrieves customer details by their unique ID.")
    @GetMapping("/{id}")
    public CustomerResponseDTO getCustomerById(@PathVariable UUID id) {
        Customer c = customerService.requireById(id);
        return CustomerResponseDTO.of(
            c.getId(),
            c.getUsername(),
            c.getEmail(),
            c.getFirstName(),
            c.getLastName(),
            c.getCreatedAt()
        );
    }

    @Operation(summary = "Update customer profile", description = "Updates the profile information of a customer.")
    @PatchMapping("/{id}")
    public CustomerResponseDTO updateCustomerProfile(@PathVariable UUID id, @Valid @RequestBody UpdateProfileRequestDTO req) {
        Customer c = customerService.updateProfile(id, req.firstName(), req.lastName(), req.email());
        return CustomerResponseDTO.of(
            c.getId(),
            c.getUsername(),
            c.getEmail(),
            c.getFirstName(),
            c.getLastName(),
            c.getCreatedAt()
        );
    }
    
}
