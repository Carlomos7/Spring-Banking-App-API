package com.example.banking.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.banking.dto.CustomerResponseDTO;
import com.example.banking.dto.LoginRequestDTO;
import com.example.banking.dto.LoginResponseDTO;
import com.example.banking.dto.RegisterRequestDTO;
import com.example.banking.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public CustomerResponseDTO register(@Valid @RequestBody RegisterRequestDTO req) {
        return authService.register(req);
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO req) {
        return authService.login(req);
    }
    
}
