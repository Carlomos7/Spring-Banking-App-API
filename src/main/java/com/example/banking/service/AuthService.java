package com.example.banking.service;

import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.banking.dto.CustomerResponseDTO;
import com.example.banking.dto.LoginRequestDTO;
import com.example.banking.dto.LoginResponseDTO;
import com.example.banking.dto.RegisterRequestDTO;
import com.example.banking.entity.Customer;
import com.example.banking.exceptions.BankingExceptions.UsernameAlreadyExistsException;
import com.example.banking.exceptions.BankingExceptions.EmailAlreadyExistsException;
import com.example.banking.exceptions.BankingExceptions.InvalidCredentialsException;
import com.example.banking.exceptions.BankingExceptions.IncorrectPasswordException;
import com.example.banking.repository.CustomerRepository;


@Service
public class AuthService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder encoder;

    public AuthService(CustomerRepository customerRepository, PasswordEncoder encoder) {
        this.customerRepository = customerRepository;
        this.encoder = encoder;
    }

    @Transactional
    public CustomerResponseDTO register(RegisterRequestDTO req) {
        var username = req.username().trim().toLowerCase(Locale.ROOT);
        var email = req.email().trim().toLowerCase(Locale.ROOT);

        if (customerRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException(username);
        }
        if (customerRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        var customer = new Customer(
            username,
            encoder.encode(req.password()), // hash the password
            req.firstName().trim(),
            req.lastName().trim(),
            email
        );
        customerRepository.save(customer);

        return CustomerResponseDTO.of(
            customer.getId(),
            customer.getUsername(),
            customer.getEmail(),
            customer.getFirstName(),
            customer.getLastName(),
            customer.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO req){
        var identifier = req.identifier().trim().toLowerCase(Locale.ROOT);
        var customer = customerRepository.findByUsername(identifier)
            .or(() -> customerRepository.findByEmail(identifier))
            .orElseThrow(() -> new InvalidCredentialsException());
        if(!encoder.matches(req.password(), customer.getPasswordHash())){
            throw new InvalidCredentialsException();
        }

        if (encoder.upgradeEncoding(customer.getPasswordHash())){
            customer.setPasswordHash(encoder.encode(req.password()));
            customerRepository.save(customer);
        }

        return generateLoginResponse(customer);
    }

    @Transactional
    public void changePassword(Customer customer, String currentPassword, String newPassword) {
        if (!encoder.matches(currentPassword, customer.getPasswordHash())) {
            throw new IncorrectPasswordException();
        }
        customer.setPasswordHash(encoder.encode(newPassword)); // hash the new password
        customerRepository.save(customer);
    }

    private LoginResponseDTO generateLoginResponse(Customer customer) {
        return LoginResponseDTO.of(
            customer.getId(),
            customer.getUsername(),
            customer.getEmail(),
            customer.getFirstName(),
            customer.getLastName()
        );
    }
}
