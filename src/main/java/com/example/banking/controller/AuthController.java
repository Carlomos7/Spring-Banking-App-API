package com.example.banking.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.banking.dto.CustomerResponseDTO;
import com.example.banking.dto.LoginRequestDTO;
import com.example.banking.dto.LoginResponseDTO;
import com.example.banking.dto.RegisterRequestDTO;
import com.example.banking.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for user registration and login")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Register a new user", description = "Creates a new user account with the provided details.")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponseDTO register(@Valid @RequestBody RegisterRequestDTO req) {
        return authService.register(req);
    }

    @Operation(summary = "User login", description = "Authenticates a user")
    @PostMapping("/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO req) {
        return authService.login(req);
    }
    
}
