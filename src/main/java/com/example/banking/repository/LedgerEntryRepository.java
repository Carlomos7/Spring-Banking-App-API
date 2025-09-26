package com.example.banking.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.banking.entity.LedgerEntry;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, UUID> {
    
    // journal-centric
    List<LedgerEntry> findByJournal_Id(UUID journalId);

    List<LedgerEntry> findByJournal_IdOrderByCreatedAtDesc(UUID journalId);

    // Account History
    List<LedgerEntry> findByAccount_IdOrderByCreatedAtDesc(UUID accountId, Pageable pageable);


    // Following Double Entry Accounting principles
    // the sum of debits and credits in a journal must be zero
    // therefore we can calculate the net amount for a journal
    @Query("""
              select coalesce(sum(
                case when e.side = 'debit' then e.amountCents
                     when e.side = 'credit' then -e.amountCents
                     else 0 end), 0)
              from LedgerEntry e
              where e.journal.id = :journalId
            """)
    Long netAmountForJournal(UUID journalId);

    @Query("""
              select e.currency from LedgerEntry e
              where e.journal.id = :journalId
              group by e.currency
            """)
    List<String> distinctCurrenciesForJournal(UUID journalId);

    @Query("""
              select coalesce(sum(e.amountCents),0) from LedgerEntry e
              where e.journal.id = :journalId and e.side = 'debit'
            """)
    Long totalDebits(UUID journalId);

    @Query("""
              select coalesce(sum(e.amountCents),0) from LedgerEntry e
              where e.journal.id = :journalId and e.side = 'credit'
            """)
    Long totalCredits(UUID journalId);

    // similarly we can calculate the net amount for an account
    // credits increase the balance, debits decrease it
    @Query("""
              select coalesce(sum(
                case when e.side = 'credit' then e.amountCents
                     when e.side = 'debit'  then -e.amountCents
                     else 0 end), 0)
              from LedgerEntry e
              where e.account.id = :accountId
            """)
    Long netAmountForAccount(UUID accountId);
}
