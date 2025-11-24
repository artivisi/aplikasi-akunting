package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.entity.ChartOfAccount;
import com.artivisi.accountingfinance.entity.JournalEntry;
import com.artivisi.accountingfinance.enums.JournalEntryStatus;
import com.artivisi.accountingfinance.enums.NormalBalance;
import com.artivisi.accountingfinance.repository.ChartOfAccountRepository;
import com.artivisi.accountingfinance.repository.JournalEntryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JournalEntryService {

    private final JournalEntryRepository journalEntryRepository;
    private final ChartOfAccountRepository chartOfAccountRepository;

    public JournalEntry findById(UUID id) {
        return journalEntryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Journal entry not found with id: " + id));
    }

    public JournalEntry findByJournalNumber(String journalNumber) {
        return journalEntryRepository.findByJournalNumber(journalNumber)
                .orElseThrow(() -> new EntityNotFoundException("Journal entry not found with number: " + journalNumber));
    }

    public List<JournalEntry> findByTransactionId(UUID transactionId) {
        return journalEntryRepository.findByTransactionIdOrderByJournalNumberAsc(transactionId);
    }

    public Page<JournalEntry> findAllByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return journalEntryRepository.findAllPostedEntriesByDateRange(startDate, endDate, pageable);
    }

    public List<JournalEntry> findByAccountAndDateRange(UUID accountId, LocalDate startDate, LocalDate endDate) {
        return journalEntryRepository.findPostedEntriesByAccountAndDateRange(accountId, startDate, endDate);
    }

    public GeneralLedgerData getGeneralLedger(UUID accountId, LocalDate startDate, LocalDate endDate) {
        ChartOfAccount account = chartOfAccountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        BigDecimal openingDebit = journalEntryRepository.sumDebitBeforeDate(accountId, startDate);
        BigDecimal openingCredit = journalEntryRepository.sumCreditBeforeDate(accountId, startDate);

        BigDecimal openingBalance;
        if (account.getNormalBalance() == NormalBalance.DEBIT) {
            openingBalance = openingDebit.subtract(openingCredit);
        } else {
            openingBalance = openingCredit.subtract(openingDebit);
        }

        List<JournalEntry> entries = journalEntryRepository
                .findPostedEntriesByAccountAndDateRange(accountId, startDate, endDate);

        BigDecimal runningBalance = openingBalance;
        List<LedgerLineItem> lineItems = new ArrayList<>();

        for (JournalEntry entry : entries) {
            if (account.getNormalBalance() == NormalBalance.DEBIT) {
                runningBalance = runningBalance.add(entry.getDebitAmount()).subtract(entry.getCreditAmount());
            } else {
                runningBalance = runningBalance.subtract(entry.getDebitAmount()).add(entry.getCreditAmount());
            }

            lineItems.add(new LedgerLineItem(entry, runningBalance));
        }

        BigDecimal totalDebit = journalEntryRepository.sumDebitByAccountAndDateRange(accountId, startDate, endDate);
        BigDecimal totalCredit = journalEntryRepository.sumCreditByAccountAndDateRange(accountId, startDate, endDate);

        return new GeneralLedgerData(
                account,
                openingBalance,
                totalDebit,
                totalCredit,
                runningBalance,
                lineItems
        );
    }

    public GeneralLedgerPagedData getGeneralLedgerPaged(
            UUID accountId, LocalDate startDate, LocalDate endDate,
            String search, Pageable pageable) {

        ChartOfAccount account = chartOfAccountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        // Calculate opening balance (same as before)
        BigDecimal openingDebit = journalEntryRepository.sumDebitBeforeDate(accountId, startDate);
        BigDecimal openingCredit = journalEntryRepository.sumCreditBeforeDate(accountId, startDate);

        BigDecimal openingBalance;
        if (account.getNormalBalance() == NormalBalance.DEBIT) {
            openingBalance = openingDebit.subtract(openingCredit);
        } else {
            openingBalance = openingCredit.subtract(openingDebit);
        }

        // Get paginated entries
        Page<JournalEntry> entriesPage;
        if (search != null && !search.isBlank()) {
            entriesPage = journalEntryRepository.findPostedEntriesByAccountAndDateRangeAndSearchPaged(
                    accountId, startDate, endDate, search, pageable);
        } else {
            entriesPage = journalEntryRepository.findPostedEntriesByAccountAndDateRangePaged(
                    accountId, startDate, endDate, pageable);
        }

        // For running balance, we need to calculate the balance up to the start of this page
        // Get all entries before the current page to calculate starting balance
        BigDecimal pageStartBalance = openingBalance;
        if (pageable.getPageNumber() > 0) {
            // Get all entries before the current page
            List<JournalEntry> priorEntries = journalEntryRepository
                    .findPostedEntriesByAccountAndDateRange(accountId, startDate, endDate);

            int skipCount = (int) pageable.getOffset();
            for (int i = 0; i < Math.min(skipCount, priorEntries.size()); i++) {
                JournalEntry entry = priorEntries.get(i);
                if (account.getNormalBalance() == NormalBalance.DEBIT) {
                    pageStartBalance = pageStartBalance.add(entry.getDebitAmount()).subtract(entry.getCreditAmount());
                } else {
                    pageStartBalance = pageStartBalance.subtract(entry.getDebitAmount()).add(entry.getCreditAmount());
                }
            }
        }

        // Build line items with running balance
        BigDecimal runningBalance = pageStartBalance;
        List<LedgerLineItem> lineItems = new ArrayList<>();

        for (JournalEntry entry : entriesPage.getContent()) {
            if (account.getNormalBalance() == NormalBalance.DEBIT) {
                runningBalance = runningBalance.add(entry.getDebitAmount()).subtract(entry.getCreditAmount());
            } else {
                runningBalance = runningBalance.subtract(entry.getDebitAmount()).add(entry.getCreditAmount());
            }
            lineItems.add(new LedgerLineItem(entry, runningBalance));
        }

        // Calculate totals for the entire date range (not just current page)
        BigDecimal totalDebit = journalEntryRepository.sumDebitByAccountAndDateRange(accountId, startDate, endDate);
        BigDecimal totalCredit = journalEntryRepository.sumCreditByAccountAndDateRange(accountId, startDate, endDate);

        BigDecimal closingBalance;
        if (account.getNormalBalance() == NormalBalance.DEBIT) {
            closingBalance = openingBalance.add(totalDebit).subtract(totalCredit);
        } else {
            closingBalance = openingBalance.subtract(totalDebit).add(totalCredit);
        }

        return new GeneralLedgerPagedData(
                account,
                openingBalance,
                totalDebit,
                totalCredit,
                closingBalance,
                lineItems,
                entriesPage.getNumber(),
                entriesPage.getTotalPages(),
                entriesPage.getTotalElements(),
                entriesPage.hasNext(),
                entriesPage.hasPrevious()
        );
    }

    public record GeneralLedgerData(
            ChartOfAccount account,
            BigDecimal openingBalance,
            BigDecimal totalDebit,
            BigDecimal totalCredit,
            BigDecimal closingBalance,
            List<LedgerLineItem> entries
    ) {}

    public record GeneralLedgerPagedData(
            ChartOfAccount account,
            BigDecimal openingBalance,
            BigDecimal totalDebit,
            BigDecimal totalCredit,
            BigDecimal closingBalance,
            List<LedgerLineItem> entries,
            int currentPage,
            int totalPages,
            long totalElements,
            boolean hasNext,
            boolean hasPrevious
    ) {}

    public record LedgerLineItem(
            JournalEntry entry,
            BigDecimal runningBalance
    ) {}

    // ========== Manual Journal Entry Operations ==========

    /**
     * Create manual journal entries (multiple lines with same journal number).
     * All entries are created in DRAFT status.
     */
    @Transactional
    public List<JournalEntry> create(List<JournalEntry> entries) {
        if (entries == null || entries.size() < 2) {
            throw new IllegalArgumentException("Journal entry must have at least 2 lines");
        }

        validateBalance(entries);

        String journalNumber = generateJournalNumber();

        for (JournalEntry entry : entries) {
            entry.setJournalNumber(journalNumber);
            entry.setStatus(JournalEntryStatus.DRAFT);
        }

        return journalEntryRepository.saveAll(entries);
    }

    /**
     * Update a draft journal entry group.
     * Only draft entries can be updated.
     */
    @Transactional
    public List<JournalEntry> update(String journalNumber, List<JournalEntry> updatedEntries) {
        List<JournalEntry> existingEntries = journalEntryRepository.findAllByJournalNumberOrderByIdAsc(journalNumber);

        if (existingEntries.isEmpty()) {
            throw new EntityNotFoundException("Journal entry not found with number: " + journalNumber);
        }

        // Check if any entry is not draft
        for (JournalEntry entry : existingEntries) {
            if (!entry.isDraft()) {
                throw new IllegalStateException("Cannot update journal entry with status: " + entry.getStatus());
            }
        }

        if (updatedEntries == null || updatedEntries.size() < 2) {
            throw new IllegalArgumentException("Journal entry must have at least 2 lines");
        }

        validateBalance(updatedEntries);

        // Delete old entries and save new ones
        journalEntryRepository.deleteAll(existingEntries);

        for (JournalEntry entry : updatedEntries) {
            entry.setJournalNumber(journalNumber);
            entry.setStatus(JournalEntryStatus.DRAFT);
        }

        return journalEntryRepository.saveAll(updatedEntries);
    }

    /**
     * Post a draft journal entry group.
     * Validates balance before posting.
     */
    @Transactional
    public List<JournalEntry> post(String journalNumber) {
        List<JournalEntry> entries = journalEntryRepository.findAllByJournalNumberOrderByIdAsc(journalNumber);

        if (entries.isEmpty()) {
            throw new EntityNotFoundException("Journal entry not found with number: " + journalNumber);
        }

        // Check if any entry is not draft
        for (JournalEntry entry : entries) {
            if (!entry.isDraft()) {
                throw new IllegalStateException("Cannot post journal entry with status: " + entry.getStatus());
            }
        }

        validateBalance(entries);

        LocalDateTime now = LocalDateTime.now();
        for (JournalEntry entry : entries) {
            entry.setStatus(JournalEntryStatus.POSTED);
            entry.setPostedAt(now);
        }

        return journalEntryRepository.saveAll(entries);
    }

    /**
     * Void a posted journal entry group.
     * Requires a reason for voiding.
     */
    @Transactional
    public List<JournalEntry> voidEntry(String journalNumber, String reason) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Void reason is required");
        }

        List<JournalEntry> entries = journalEntryRepository.findAllByJournalNumberOrderByIdAsc(journalNumber);

        if (entries.isEmpty()) {
            throw new EntityNotFoundException("Journal entry not found with number: " + journalNumber);
        }

        // Check if any entry is not posted
        for (JournalEntry entry : entries) {
            if (!entry.isPosted()) {
                throw new IllegalStateException("Cannot void journal entry with status: " + entry.getStatus());
            }
        }

        LocalDateTime now = LocalDateTime.now();
        for (JournalEntry entry : entries) {
            entry.setStatus(JournalEntryStatus.VOID);
            entry.setVoidedAt(now);
            entry.setVoidReason(reason);
        }

        return journalEntryRepository.saveAll(entries);
    }

    /**
     * Validate that total debit equals total credit.
     */
    public void validateBalance(List<JournalEntry> entries) {
        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;

        for (JournalEntry entry : entries) {
            totalDebit = totalDebit.add(entry.getDebitAmount());
            totalCredit = totalCredit.add(entry.getCreditAmount());
        }

        if (totalDebit.compareTo(totalCredit) != 0) {
            throw new IllegalArgumentException(
                    String.format("Journal entry is not balanced. Debit: %s, Credit: %s, Difference: %s",
                            totalDebit, totalCredit, totalDebit.subtract(totalCredit).abs()));
        }
    }

    /**
     * Find all entries by journal number.
     */
    public List<JournalEntry> findAllByJournalNumber(String journalNumber) {
        return journalEntryRepository.findAllByJournalNumberOrderByIdAsc(journalNumber);
    }

    /**
     * Generate next journal number in format JE-YYYY-NNNN
     */
    private String generateJournalNumber() {
        String year = String.valueOf(Year.now().getValue());
        String prefix = "JE-" + year + "-%";

        Integer maxSeq = journalEntryRepository.findMaxSequenceByPrefix(prefix);
        int nextSeq = (maxSeq == null) ? 1 : maxSeq + 1;

        return String.format("JE-%s-%04d", year, nextSeq);
    }
}
