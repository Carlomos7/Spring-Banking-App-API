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
import com.example.banking.dto.JournalResponseDTO;
import com.example.banking.dto.LedgerEntryResponseDTO;
import com.example.banking.entity.Journal;
import com.example.banking.entity.LedgerEntry;
import com.example.banking.service.JournalService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/journals")
@Tag(name = "Journals", description = "Endpoints for managing financial journals")
public class JournalController {
    private final JournalService journalService;

    public JournalController(JournalService journalService) {
        this.journalService = journalService;
    }

    @Operation(summary = "Create a new journal", description = "Creates a new financial journal with optional description and external reference.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JournalResponseDTO create(@RequestParam(required = false) String description,
            @RequestParam(required = false) String externalRef) {
        Journal j = journalService.createJournal(description, externalRef);
        var diag = journalService.diagnostics(j.getId());
        return JournalResponseDTO.of(j, diag.balanced(), diag.currency(), diag.debitTotalCents(), diag.creditTotalCents(), diag.netCents());
    }

    @Operation(summary = "Add an entry to a journal", description = "Adds a ledger entry to a specified journal.")
    @PostMapping("/{journalId}/entries")
    @ResponseStatus(HttpStatus.CREATED)
    public LedgerEntryResponseDTO addEntry(@PathVariable UUID journalId, @Valid @RequestBody AddEntryRequestDTO req) {
        LedgerEntry e = journalService.addEntry(journalId, req.accountId(), req.side(), req.currency(), req.amountCents());
        return LedgerEntryResponseDTO.of(e);
    }

    @PostMapping("/{journalId}/post")
    public JournalResponseDTO post(@PathVariable UUID journalId) {
        Journal j = journalService.postJournal(journalId);
        var diag = journalService.diagnostics(journalId);
        return JournalResponseDTO.of(j, diag.balanced(), diag.currency(), diag.debitTotalCents(), diag.creditTotalCents(), diag.netCents());
    }

    @GetMapping("/{journalId}/entries")
    public List<LedgerEntryResponseDTO> listEntries(@PathVariable UUID journalId) {
        return journalService.listEntries(journalId).stream().map(LedgerEntryResponseDTO::of).toList();
    }

    @GetMapping("/{journalId}")
    public JournalResponseDTO get(@PathVariable UUID journalId) {
        Journal j = journalService.getJournal(journalId);
        var diag = journalService.diagnostics(journalId);
        return JournalResponseDTO.of(j, diag.balanced(), diag.currency(), diag.debitTotalCents(), diag.creditTotalCents(), diag.netCents());
    }
}
