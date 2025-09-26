package com.example.banking.service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.Currency;
import java.util.Set;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.banking.entity.Account;
import com.example.banking.entity.Journal;
import com.example.banking.entity.LedgerEntry;
import com.example.banking.exceptions.BankingExceptions.ExternalReferenceAlreadyExistsException;
import com.example.banking.exceptions.BankingExceptions.JournalNotFoundException;
import com.example.banking.exceptions.BankingExceptions.JournalNotPendingException;
import com.example.banking.exceptions.BankingExceptions.AccountNotFoundException;
import com.example.banking.exceptions.BankingExceptions.InactiveAccountException;
import com.example.banking.exceptions.BankingExceptions.InvalidTransactionSideException;
import com.example.banking.exceptions.BankingExceptions.InvalidAmountException;
import com.example.banking.exceptions.BankingExceptions.InvalidCurrencyCodeException;
import com.example.banking.exceptions.BankingExceptions.UnbalancedJournalException;
import com.example.banking.model.JournalStatus;
import com.example.banking.repository.AccountRepository;
import com.example.banking.repository.JournalRepository;
import com.example.banking.repository.LedgerEntryRepository;

@Service
public class JournalService {

    private final JournalRepository journalRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final AccountRepository accountRepository;

    private static final Set<String> ISO_CURRENCY_CODES = Currency.getAvailableCurrencies().stream()
        .map(Currency::getCurrencyCode).collect(Collectors.toUnmodifiableSet());

    public JournalService(JournalRepository journalRepository, LedgerEntryRepository ledgerEntryRepository,
            AccountRepository accountRepository) {
        this.journalRepository = journalRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Journal createJournal(String description, String externalRef) {
        String desc = (description == null || description.isBlank()) ? null : description.trim();
        String ext = (externalRef == null || externalRef.isBlank()) ? null : externalRef.trim();
        if (ext != null && journalRepository.existsByExternalRef(ext)) {
            throw new ExternalReferenceAlreadyExistsException(ext);
        }
        Journal journal = new Journal(desc, ext);
        journal.setStatus(JournalStatus.PENDING);
        return journalRepository.save(journal);
    }

    @Transactional
    public LedgerEntry addEntry(UUID journalId, UUID accountId, String side, String currency, long amountCents) {
        Journal journal = journalRepository.findById(journalId)
                .orElseThrow(() -> new JournalNotFoundException(journalId.toString()));
        if (journal.getStatus() != JournalStatus.PENDING) {
            throw new JournalNotPendingException(journalId.toString());
        }
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId.toString()));
        if (!account.isActive()) {
            throw new InactiveAccountException(accountId.toString());
        }
        String s = side.trim().toLowerCase(Locale.ROOT);
        if (!(s.equals("debit") || s.equals("credit"))) {
            throw new InvalidTransactionSideException(side);
        }

        if (amountCents <= 0) {
            throw new InvalidAmountException(amountCents);
        }

        String cur = currency.trim().toUpperCase(Locale.ROOT);
        if (cur.length() != 3 || !ISO_CURRENCY_CODES.contains(cur)) {
            throw new InvalidCurrencyCodeException(currency);
        }

        // Enforce single currency: if journal already has entries, ensure same currency
        List<String> currencies = ledgerEntryRepository.distinctCurrenciesForJournal(journalId);
        if (!currencies.isEmpty() && !currencies.contains(cur)) {
            throw new InvalidCurrencyCodeException(cur + " (single-currency journal mismatch, existing=" + currencies.get(0) + ")");
        }

        LedgerEntry entry = new LedgerEntry(journal, account, s, amountCents, cur);
        return ledgerEntryRepository.save(entry);

    }

    @Transactional(readOnly = true)
    public boolean isBalanced(UUID journalId) {
        long net = ledgerEntryRepository.netAmountForJournal(journalId);
        return net == 0;
    }

    @Transactional
    public Journal postJournal(UUID journalId) {
        Journal journal = journalRepository.findById(journalId)
                .orElseThrow(() -> new JournalNotFoundException(journalId.toString()));
        if (journal.getStatus() == JournalStatus.POSTED) {
            return journal; // already posted, no-op
        }
        long net = ledgerEntryRepository.netAmountForJournal(journalId);
        long debits = ledgerEntryRepository.totalDebits(journalId);
        long credits = ledgerEntryRepository.totalCredits(journalId);
        List<String> currencies = ledgerEntryRepository.distinctCurrenciesForJournal(journalId);
        String currency = currencies.isEmpty() ? null : currencies.get(0);
        if (net != 0) {
            throw new UnbalancedJournalException("Cannot post an unbalanced journal", Map.of(
                "journalId", journalId.toString(),
                "currency", currency,
                "debitTotalCents", debits,
                "creditTotalCents", credits,
                "netCents", net
            ));
        }
        journal.setStatus(JournalStatus.POSTED);
        journal.setPostedAt(java.time.Instant.now());
        return journalRepository.save(journal);
    }

    @Transactional(readOnly = true)
    public List<LedgerEntry> listEntries(UUID journalId) {
        return ledgerEntryRepository.findByJournal_Id(journalId);
    }

    @Transactional(readOnly = true)
    public Journal getJournal(UUID journalId) {
        return journalRepository.findById(journalId)
            .orElseThrow(() -> new JournalNotFoundException(journalId.toString()));
    }

    @Transactional(readOnly = true)
    public JournalDiagnostics diagnostics(UUID journalId) {
        long net = ledgerEntryRepository.netAmountForJournal(journalId);
        long debits = ledgerEntryRepository.totalDebits(journalId);
        long credits = ledgerEntryRepository.totalCredits(journalId);
        List<String> currencies = ledgerEntryRepository.distinctCurrenciesForJournal(journalId);
        String currency = currencies.isEmpty() ? null : currencies.get(0);
        return new JournalDiagnostics(currency, debits, credits, net, net == 0);
    }

    public static record JournalDiagnostics(String currency, long debitTotalCents, long creditTotalCents, long netCents, boolean balanced) {}
}