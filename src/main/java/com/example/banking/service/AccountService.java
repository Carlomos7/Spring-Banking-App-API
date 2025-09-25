package com.example.banking.service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.banking.entity.Account;
import com.example.banking.entity.Customer;
import com.example.banking.exceptions.BankingExceptions.CustomerNotFoundException;
import com.example.banking.exceptions.BankingExceptions.AccountNotFoundException;
import com.example.banking.exceptions.BankingExceptions.InvalidAccountKindException;
import com.example.banking.exceptions.BankingExceptions.InvalidCurrencyCodeException;
import com.example.banking.repository.AccountRepository;
import com.example.banking.repository.CustomerRepository;
import com.example.banking.repository.LedgerEntryRepository;

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
                .orElseThrow(() -> new CustomerNotFoundException(customerId.toString()));

        String k = kind.trim().toLowerCase(Locale.ROOT);
        if (!(k.equals("checking") || k.equals("savings") || k.equals("internal"))) {
            throw new InvalidAccountKindException(kind);
        }

        String cur = currency.trim().toUpperCase(Locale.ROOT);
        if (cur.length() != 3) throw new InvalidCurrencyCodeException(currency);

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
                .orElseThrow(() -> new AccountNotFoundException(accountId.toString()));
    }

    @Transactional
    public void setActive(UUID accountId, boolean active) {
        Account a = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId.toString()));
        if (a.isActive() == active) return; // no change
        a.setActive(active);
    }

    @Transactional(readOnly = true)
    public long computeBalance(UUID accountId) {
        return ledgerEntryRepository.netAmountForAccount(accountId);
    }
}
