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
import com.example.banking.dto.ToggleActiveRequestDTO;
import com.example.banking.entity.Account;
import com.example.banking.service.AccountService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/customers/{customerId}/accounts")
    @ResponseStatus(HttpStatus.CREATED)
    public Account open(@PathVariable UUID customerId, @Valid @RequestBody OpenAccountRequestDTO req) {
        return accountService.openAccount(customerId, req.kind(), req.currency());
    }

    @GetMapping("/customers/{customerId}/accounts")
    public List<Account> list(@PathVariable UUID customerId) {
        return accountService.listCustomerAccounts(customerId);
    }

    @GetMapping("/accounts/{accountId}")
    public Account get(@PathVariable UUID accountId, @RequestParam(required = false) UUID customerId) {
        // could also strictly require customerId and check ownership
        if (customerId != null) {
            return accountService.getAccount(accountId, customerId);
        }
        return accountService
                .listCustomerAccounts(customerId) // TODO: optimize
                .stream().filter(a -> a.getId().equals(accountId)).findFirst()
                .orElseGet(() -> accountService.getAccount(accountId, customerId)); // fallback
    }

    @PatchMapping("/accounts/{accountId}/active")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void toggleActive(@PathVariable UUID accountId, @Valid @RequestBody ToggleActiveRequestDTO req) {
        accountService.setActive(accountId, req.active());
    }

}