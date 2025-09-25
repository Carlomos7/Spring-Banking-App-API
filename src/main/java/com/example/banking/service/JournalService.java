package com.example.banking.service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

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

    public JournalService(JournalRepository journalRepository, LedgerEntryRepository ledgerEntryRepository,
            AccountRepository accountRepository) {
        this.journalRepository = journalRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Journal createJournal(String description, String externalRef) {
        if (externalRef != null && journalRepository.existsByExternalRef(externalRef)) {
            throw new ExternalReferenceAlreadyExistsException(externalRef);
        }
        Journal journal = new Journal(description, externalRef);
        journal.setStatus(JournalStatus.PENDING);
        journal.setDescription(description);
        journal.setExternalRef(externalRef);
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
        if (cur.length() != 3){
            throw new InvalidCurrencyCodeException(currency);
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
        if (!isBalanced(journalId)) {
            throw new UnbalancedJournalException(journalId.toString());
        }
        journal.setStatus(JournalStatus.POSTED);

        return journalRepository.save(journal);
    }

    @Transactional(readOnly = true)
    public List<LedgerEntry> listEntries(UUID journalId) {
        return ledgerEntryRepository.findByJournal_Id(journalId);
    }
}