package com.example.banking.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.banking.dto.OpenAccountRequestDTO;
import com.example.banking.dto.AccountResponseDTO;
import com.example.banking.dto.ToggleActiveRequestDTO;
import com.example.banking.service.AccountService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping
@Tag(name = "Accounts", description = "Endpoints for managing bank accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(summary = "Open a new account", description = "Opens a new bank account for a specified customer.")
    @PostMapping("/customers/{customerId}/accounts")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponseDTO open(@PathVariable UUID customerId,
                                   @Valid @RequestBody OpenAccountRequestDTO req) {
        var acc = accountService.openAccount(customerId, req.kind(), req.currency());
        return AccountResponseDTO.of(acc);
    }

    @Operation(summary = "List all accounts for a customer", description = "Retrieves a list of all bank accounts for a specified customer.")
    @GetMapping("/customers/{customerId}/accounts")
    public List<AccountResponseDTO> list(@PathVariable UUID customerId) {
        return accountService.listCustomerAccounts(customerId).stream().map(AccountResponseDTO::of).toList();
    }

    @Operation(summary = "Get account details", description = "Retrieves details of a specific bank account for a customer.")
    @GetMapping("/accounts/{accountId}")
    public AccountResponseDTO get(@PathVariable UUID accountId, @RequestParam UUID customerId) {
        return AccountResponseDTO.of(accountService.getAccount(accountId, customerId));
    }

    @Operation(summary = "Activate or deactivate an account", description = "Sets the active status of a specified bank account.")
    @PatchMapping("/accounts/{accountId}/active")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void toggleActive(@PathVariable UUID accountId, @Valid @RequestBody ToggleActiveRequestDTO req) {
        accountService.setActive(accountId, req.active());
    }

}