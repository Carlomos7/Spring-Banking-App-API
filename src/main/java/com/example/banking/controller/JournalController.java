package com.example.banking.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.banking.dto.AddEntryRequestDTO;
import com.example.banking.entity.Journal;
import com.example.banking.entity.LedgerEntry;
import com.example.banking.service.JournalService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/journals")
public class JournalController {
    private final JournalService journalService;

    public JournalController(JournalService journalService) {
        this.journalService = journalService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Journal create(@RequestParam(required = false) String description,
            @RequestParam(required = false) String externalRef) {
        return journalService.createJournal(description, externalRef);
    }

    @PostMapping("/{journalId}/entries")
    @ResponseStatus(HttpStatus.CREATED)
    public LedgerEntry addEntry(@PathVariable UUID journalId, @Valid @RequestBody AddEntryRequestDTO req) {
        return journalService.addEntry(journalId, req.accountId(), req.side(), req.currency(), req.amountCents());
    }

    @PostMapping("/{journalId}/post")
    public Journal post(@PathVariable UUID journalId) {
        return journalService.postJournal(journalId);
    }

    @GetMapping("/{journalId}/entries")
    public List<LedgerEntry> listEntries(@PathVariable UUID journalId) {
        return journalService.listEntries(journalId);
    }
}
