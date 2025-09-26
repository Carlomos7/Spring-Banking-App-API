package com.example.banking.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.banking.dto.LedgerEntryResponseDTO;
import com.example.banking.service.LedgerEntryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/accounts/{accountId}/entries")
@Tag(name = "Ledger Entries", description = "Endpoints for retrieving ledger entries for accounts")
public class LedgerEntryController {
    private final LedgerEntryService ledgerEntryService;

    public LedgerEntryController(LedgerEntryService ledgerEntryService) {
        this.ledgerEntryService = ledgerEntryService;
    }

    @Operation(summary = "Get account ledger history", description = "Retrieves a paginated list of ledger entries for a specified account.")
    @GetMapping
    public List<LedgerEntryResponseDTO> history(@PathVariable UUID accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ledgerEntryService.accountHistory(accountId, page, size).stream()
            .map(LedgerEntryResponseDTO::of)
            .toList();
    }
}
