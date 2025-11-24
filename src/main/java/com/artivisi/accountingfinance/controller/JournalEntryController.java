package com.artivisi.accountingfinance.controller;

import com.artivisi.accountingfinance.entity.JournalEntry;
import com.artivisi.accountingfinance.service.ChartOfAccountService;
import com.artivisi.accountingfinance.service.JournalEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.requireNonNullElse;

@Controller
@RequestMapping("/journals")
@RequiredArgsConstructor
public class JournalEntryController {

    private final JournalEntryService journalEntryService;
    private final ChartOfAccountService chartOfAccountService;

    private static final int DEFAULT_PAGE_SIZE = 20;

    @GetMapping
    public String list(
            @RequestParam(required = false) UUID accountId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size,
            Model model) {

        LocalDate start = requireNonNullElse(startDate, LocalDate.now().withDayOfMonth(1));
        LocalDate end = requireNonNullElse(endDate, LocalDate.now());

        model.addAttribute("currentPage", "journals");
        model.addAttribute("selectedAccount", accountId);
        model.addAttribute("startDate", start);
        model.addAttribute("endDate", end);
        model.addAttribute("searchQuery", search);
        model.addAttribute("accounts", chartOfAccountService.findTransactableAccounts());
        model.addAttribute("pageNumber", page);
        model.addAttribute("pageSize", size);

        if (accountId != null) {
            Pageable pageable = PageRequest.of(page, size);
            model.addAttribute("ledgerData",
                    journalEntryService.getGeneralLedgerPaged(accountId, start, end, search, pageable));
        }

        return "journals/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, Model model) {
        model.addAttribute("currentPage", "journals");
        model.addAttribute("journalEntry", journalEntryService.findById(id));
        return "journals/detail";
    }

    @GetMapping("/ledger/{accountId}")
    public String accountLedger(
            @PathVariable UUID accountId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            Model model) {
        model.addAttribute("currentPage", "journals");
        model.addAttribute("account", chartOfAccountService.findById(accountId));

        LocalDate start = startDate != null ? startDate : LocalDate.now().withDayOfMonth(1);
        LocalDate end = endDate != null ? endDate : LocalDate.now();

        model.addAttribute("startDate", start);
        model.addAttribute("endDate", end);
        model.addAttribute("ledgerData", journalEntryService.getGeneralLedger(accountId, start, end));

        return "journals/ledger";
    }

    // REST API Endpoints

    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<Page<JournalEntry>> apiList(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            Pageable pageable) {
        return ResponseEntity.ok(journalEntryService.findAllByDateRange(startDate, endDate, pageable));
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<JournalEntry> apiGet(@PathVariable UUID id) {
        return ResponseEntity.ok(journalEntryService.findById(id));
    }

    @GetMapping("/api/by-transaction/{transactionId}")
    @ResponseBody
    public ResponseEntity<List<JournalEntry>> apiByTransaction(@PathVariable UUID transactionId) {
        return ResponseEntity.ok(journalEntryService.findByTransactionId(transactionId));
    }

    @GetMapping("/api/ledger/{accountId}")
    @ResponseBody
    public ResponseEntity<JournalEntryService.GeneralLedgerData> apiLedger(
            @PathVariable UUID accountId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        return ResponseEntity.ok(journalEntryService.getGeneralLedger(accountId, startDate, endDate));
    }
}
