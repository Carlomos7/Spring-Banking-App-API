package com.example.banking.service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.banking.entity.Account;
import com.example.banking.entity.Customer;
import com.example.banking.repository.AccountRepository;
import com.example.banking.repository.CustomerRepository;
import com.example.banking.repository.LedgerEntryRepository;

import jakarta.validation.ValidationException;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    public AccountService(AccountRepository accountRepository, CustomerRepository customerRepository, LedgerEntryRepository ledgerEntryRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
    }

    @Transactional
    public Account openAccount(UUID customerId, String kind, String currency) {
        Customer owner = customerRepository.findById(customerId)
                .orElseThrow(() -> new ValidationException("customer not found"));

        String k = kind.trim().toLowerCase(Locale.ROOT);
        if (!(k.equals("checking") || k.equals("savings") || k.equals("internal"))) {
            throw new ValidationException("invalid account kind");
        }

        String cur = currency.trim().toUpperCase(Locale.ROOT);
        if (cur.length() != 3) throw new ValidationException("invalid currency (ISO-4217)"); // TODO: More robust validation

        Account a = new Account(owner, k, cur); // defaults isActive=true
        return accountRepository.save(a);
    }

    @Transactional(readOnly = true)
    public List<Account> listCustomerAccounts(UUID customerId) {
        return accountRepository.findByCustomer_Id(customerId);
    }

    @Transactional(readOnly = true)
    public Account getAccount(UUID accountId, UUID customerId) {
        return accountRepository.findByIdAndCustomer_Id(accountId, customerId)
                .orElseThrow(() -> new ValidationException("account not found")); // TODO: Custom exception
    }

    @Transactional
    public void setActive(UUID accountId, boolean active) {
        Account a = accountRepository.findById(accountId)
                .orElseThrow(() -> new ValidationException("account not found")); // TODO: Custom exception
        if (a.isActive() == active) return; // no change
        a.setActive(active);
    }

    @Transactional(readOnly = true)
    public long computeBalance(UUID accountId) {
        return ledgerEntryRepository.netAmountForAccount(accountId);
    }
}
