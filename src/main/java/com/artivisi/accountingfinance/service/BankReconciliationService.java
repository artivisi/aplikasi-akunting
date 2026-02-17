package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.entity.BankReconciliation;
import com.artivisi.accountingfinance.entity.BankStatement;
import com.artivisi.accountingfinance.entity.BankStatementItem;
import com.artivisi.accountingfinance.entity.ChartOfAccount;
import com.artivisi.accountingfinance.entity.CompanyBankAccount;
import com.artivisi.accountingfinance.entity.JournalEntry;
import com.artivisi.accountingfinance.entity.JournalTemplate;
import com.artivisi.accountingfinance.entity.ReconciliationItem;
import com.artivisi.accountingfinance.entity.Transaction;
import com.artivisi.accountingfinance.enums.MatchType;
import com.artivisi.accountingfinance.enums.ReconciliationStatus;
import com.artivisi.accountingfinance.enums.StatementItemMatchStatus;
import com.artivisi.accountingfinance.security.LogSanitizer;
import com.artivisi.accountingfinance.repository.BankReconciliationRepository;
import com.artivisi.accountingfinance.repository.BankStatementItemRepository;
import com.artivisi.accountingfinance.repository.JournalEntryRepository;
import com.artivisi.accountingfinance.repository.ReconciliationItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BankReconciliationService {

    private final BankReconciliationRepository reconciliationRepository;
    private final ReconciliationItemRepository reconciliationItemRepository;
    private final BankStatementItemRepository statementItemRepository;
    private final BankStatementImportService importService;
    private final JournalEntryRepository journalEntryRepository;
    private final TransactionService transactionService;
    private final JournalTemplateService templateService;

    public List<BankReconciliation> findAll() {
        return reconciliationRepository.findAllWithRelations();
    }

    public BankReconciliation findById(UUID id) {
        return reconciliationRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new EntityNotFoundException("Reconciliation not found with id: " + id));
    }

    public List<ReconciliationItem> findReconciliationItems(UUID reconciliationId) {
        return reconciliationItemRepository.findByReconciliationIdWithRelations(reconciliationId);
    }

    public List<JournalTemplate> findJournalTemplates() {
        return templateService.findAll();
    }

    @Transactional
    public BankReconciliation create(UUID bankStatementId, String notes, String username) {
        BankStatement statement = importService.findStatementById(bankStatementId);
        CompanyBankAccount bankAccount = statement.getBankAccount();

        if (bankAccount.getGlAccount() == null) {
            throw new IllegalArgumentException(
                    "Rekening bank '" + bankAccount.getBankName()
                            + "' belum dihubungkan dengan akun GL. Atur di Pengaturan > Rekening Bank.");
        }

        BankReconciliation recon = new BankReconciliation();
        recon.setBankAccount(bankAccount);
        recon.setBankStatement(statement);
        recon.setPeriodStart(statement.getStatementPeriodStart());
        recon.setPeriodEnd(statement.getStatementPeriodEnd());
        recon.setStatus(ReconciliationStatus.DRAFT);
        recon.setNotes(notes);
        recon.setCreatedBy(username);

        // Compute book balance from GL journal entries
        ChartOfAccount glAccount = bankAccount.getGlAccount();
        BigDecimal bookBalance = computeBookBalance(
                glAccount.getId(), statement.getStatementPeriodStart(), statement.getStatementPeriodEnd());
        recon.setBookBalance(bookBalance);

        // Set bank balance from statement closing balance
        recon.setBankBalance(statement.getClosingBalance());

        // Set counts
        long totalItems = statementItemRepository.findByBankStatementIdOrderByLineNumberAsc(bankStatementId).size();
        recon.setTotalStatementItems((int) totalItems);
        recon.setMatchedCount(0);
        recon.setUnmatchedBankCount((int) totalItems);
        recon.setUnmatchedBookCount(0);

        return reconciliationRepository.save(recon);
    }

    @Transactional
    public int autoMatch(UUID reconciliationId, String username) {
        BankReconciliation recon = findById(reconciliationId);
        if (recon.isCompleted()) {
            throw new IllegalStateException("Rekonsiliasi sudah selesai, tidak dapat melakukan auto-match");
        }

        recon.setStatus(ReconciliationStatus.IN_PROGRESS);

        // Get unmatched statement items
        List<BankStatementItem> unmatchedItems = statementItemRepository
                .findByBankStatementIdAndMatchStatusOrderByLineNumberAsc(
                        recon.getBankStatement().getId(), StatementItemMatchStatus.UNMATCHED);

        // Get book transactions for the period
        ChartOfAccount glAccount = recon.getBankAccount().getGlAccount();
        List<JournalEntry> bookEntries = journalEntryRepository.findPostedEntriesByAccountAndDateRange(
                glAccount.getId(), recon.getPeriodStart(), recon.getPeriodEnd());

        // Track matched transactions to avoid double-matching
        Set<UUID> matchedTransactionIds = new HashSet<>();

        // Load already matched transaction IDs
        List<ReconciliationItem> existingItems = reconciliationItemRepository
                .findByReconciliationIdAndMatchStatus(reconciliationId, StatementItemMatchStatus.MATCHED);
        for (ReconciliationItem item : existingItems) {
            if (item.getTransaction() != null) {
                matchedTransactionIds.add(item.getTransaction().getId());
            }
        }

        int matchCount = 0;

        // Pass 1: Exact match (same date + same amount)
        matchCount += matchPass(recon, unmatchedItems, bookEntries, matchedTransactionIds,
                MatchType.EXACT, new BigDecimal("1.00"), 0, username);

        // Refresh unmatched items
        unmatchedItems = statementItemRepository
                .findByBankStatementIdAndMatchStatusOrderByLineNumberAsc(
                        recon.getBankStatement().getId(), StatementItemMatchStatus.UNMATCHED);

        // Pass 2: Fuzzy date (same amount, date +/-1 day)
        matchCount += matchPass(recon, unmatchedItems, bookEntries, matchedTransactionIds,
                MatchType.FUZZY_DATE, new BigDecimal("0.90"), 1, username);

        // Refresh unmatched items
        unmatchedItems = statementItemRepository
                .findByBankStatementIdAndMatchStatusOrderByLineNumberAsc(
                        recon.getBankStatement().getId(), StatementItemMatchStatus.UNMATCHED);

        // Pass 3: Keyword (same amount, description overlap, date +/-3 days)
        matchCount += keywordMatchPass(recon, unmatchedItems, bookEntries, matchedTransactionIds, username);

        // Update reconciliation counts
        updateReconciliationCounts(recon);
        reconciliationRepository.save(recon);

        log.info("Auto-match completed for reconciliation {}: {} matches",
                LogSanitizer.sanitize(reconciliationId.toString()), matchCount);
        return matchCount;
    }

    private int matchPass(BankReconciliation recon, List<BankStatementItem> unmatchedItems,
                          List<JournalEntry> bookEntries, Set<UUID> matchedTransactionIds,
                          MatchType matchType, BigDecimal confidence, int dateToleranceDays, String username) {
        int matchCount = 0;

        for (BankStatementItem item : unmatchedItems) {
            if (item.getMatchStatus() != StatementItemMatchStatus.UNMATCHED) {
                continue;
            }

            BigDecimal itemAmount = item.getNetAmount();

            for (JournalEntry entry : bookEntries) {
                if (matchedTransactionIds.contains(entry.getTransaction().getId())) {
                    continue;
                }

                // Amount check: bank debit = book credit, bank credit = book debit
                BigDecimal entryAmount = computeEntryNetAmount(entry);
                if (itemAmount.compareTo(entryAmount) != 0) {
                    continue;
                }

                // Date check
                long daysDiff = Math.abs(
                        item.getTransactionDate().toEpochDay() - entry.getJournalDate().toEpochDay());
                if (daysDiff > dateToleranceDays) {
                    continue;
                }

                // Match found
                createMatch(recon, item, entry.getTransaction(), matchType, confidence, username);
                matchedTransactionIds.add(entry.getTransaction().getId());
                matchCount++;
                break;
            }
        }
        return matchCount;
    }

    private int keywordMatchPass(BankReconciliation recon, List<BankStatementItem> unmatchedItems,
                                 List<JournalEntry> bookEntries, Set<UUID> matchedTransactionIds, String username) {
        int matchCount = 0;

        for (BankStatementItem item : unmatchedItems) {
            if (item.getMatchStatus() != StatementItemMatchStatus.UNMATCHED) {
                continue;
            }

            BigDecimal itemAmount = item.getNetAmount();
            String itemDesc = item.getDescription() != null ? item.getDescription().toLowerCase() : "";

            for (JournalEntry entry : bookEntries) {
                if (matchedTransactionIds.contains(entry.getTransaction().getId())) {
                    continue;
                }

                BigDecimal entryAmount = computeEntryNetAmount(entry);
                if (itemAmount.compareTo(entryAmount) != 0) {
                    continue;
                }

                // Date within 3 days
                long daysDiff = Math.abs(
                        item.getTransactionDate().toEpochDay() - entry.getJournalDate().toEpochDay());
                if (daysDiff > 3) {
                    continue;
                }

                // Description keyword overlap
                String entryDesc = entry.getDescription() != null ? entry.getDescription().toLowerCase() : "";
                if (hasKeywordOverlap(itemDesc, entryDesc)) {
                    createMatch(recon, item, entry.getTransaction(),
                            MatchType.KEYWORD, new BigDecimal("0.80"), username);
                    matchedTransactionIds.add(entry.getTransaction().getId());
                    matchCount++;
                    break;
                }
            }
        }
        return matchCount;
    }

    private boolean hasKeywordOverlap(String desc1, String desc2) {
        if (desc1.isEmpty() || desc2.isEmpty()) {
            return false;
        }

        String[] words1 = desc1.split("\\s+");
        for (String word : words1) {
            if (word.length() >= 4 && desc2.contains(word)) {
                return true;
            }
        }
        return false;
    }

    private BigDecimal computeEntryNetAmount(JournalEntry entry) {
        BigDecimal debit = entry.getDebitAmount() != null ? entry.getDebitAmount() : BigDecimal.ZERO;
        BigDecimal credit = entry.getCreditAmount() != null ? entry.getCreditAmount() : BigDecimal.ZERO;
        // For a bank account (asset), debit increases balance, credit decreases
        // Bank statement: credit = money in, debit = money out
        // So: bank credit matches book debit, bank debit matches book credit
        // Net amount convention: positive = money in (credit on bank, debit on books)
        return debit.subtract(credit);
    }

    private void createMatch(BankReconciliation recon, BankStatementItem item, Transaction transaction,
                            MatchType matchType, BigDecimal confidence, String username) {
        // Update statement item
        item.setMatchStatus(StatementItemMatchStatus.MATCHED);
        item.setMatchType(matchType);
        item.setMatchedTransaction(transaction);
        item.setMatchedAt(LocalDateTime.now());
        item.setMatchedBy(username);
        statementItemRepository.save(item);

        // Create reconciliation item
        ReconciliationItem reconItem = new ReconciliationItem();
        reconItem.setReconciliation(recon);
        reconItem.setStatementItem(item);
        reconItem.setTransaction(transaction);
        reconItem.setMatchStatus(StatementItemMatchStatus.MATCHED);
        reconItem.setMatchType(matchType);
        reconItem.setMatchConfidence(confidence);
        reconciliationItemRepository.save(reconItem);
    }

    @Transactional
    public void manualMatch(UUID reconciliationId, UUID statementItemId, UUID transactionId, String username) {
        BankReconciliation recon = findById(reconciliationId);
        if (recon.isCompleted()) {
            throw new IllegalStateException("Rekonsiliasi sudah selesai");
        }

        BankStatementItem item = statementItemRepository.findById(statementItemId)
                .orElseThrow(() -> new EntityNotFoundException("Statement item not found: " + statementItemId));

        Transaction transaction = transactionService.findById(transactionId);

        recon.setStatus(ReconciliationStatus.IN_PROGRESS);
        createMatch(recon, item, transaction, MatchType.MANUAL, new BigDecimal("1.00"), username);
        updateReconciliationCounts(recon);
        reconciliationRepository.save(recon);
    }

    @Transactional
    public void markBankOnly(UUID reconciliationId, UUID statementItemId, String notes, String username) {
        BankReconciliation recon = findById(reconciliationId);
        if (recon.isCompleted()) {
            throw new IllegalStateException("Rekonsiliasi sudah selesai");
        }

        BankStatementItem item = statementItemRepository.findById(statementItemId)
                .orElseThrow(() -> new EntityNotFoundException("Statement item not found: " + statementItemId));

        item.setMatchStatus(StatementItemMatchStatus.BANK_ONLY);
        item.setMatchedAt(LocalDateTime.now());
        item.setMatchedBy(username);
        statementItemRepository.save(item);

        ReconciliationItem reconItem = new ReconciliationItem();
        reconItem.setReconciliation(recon);
        reconItem.setStatementItem(item);
        reconItem.setMatchStatus(StatementItemMatchStatus.BANK_ONLY);
        reconItem.setNotes(notes);
        reconciliationItemRepository.save(reconItem);

        recon.setStatus(ReconciliationStatus.IN_PROGRESS);
        updateReconciliationCounts(recon);
        reconciliationRepository.save(recon);
    }

    @Transactional
    public void markBookOnly(UUID reconciliationId, UUID transactionId, String notes, String username) {
        BankReconciliation recon = findById(reconciliationId);
        if (recon.isCompleted()) {
            throw new IllegalStateException("Rekonsiliasi sudah selesai");
        }

        Transaction transaction = transactionService.findById(transactionId);

        ReconciliationItem reconItem = new ReconciliationItem();
        reconItem.setReconciliation(recon);
        reconItem.setTransaction(transaction);
        reconItem.setMatchStatus(StatementItemMatchStatus.BOOK_ONLY);
        reconItem.setNotes(notes);
        reconciliationItemRepository.save(reconItem);

        recon.setStatus(ReconciliationStatus.IN_PROGRESS);
        updateReconciliationCounts(recon);
        reconciliationRepository.save(recon);
    }

    @Transactional
    public void unmatch(UUID reconciliationId, UUID reconciliationItemId) {
        BankReconciliation recon = findById(reconciliationId);
        if (recon.isCompleted()) {
            throw new IllegalStateException("Rekonsiliasi sudah selesai");
        }

        ReconciliationItem reconItem = reconciliationItemRepository.findById(reconciliationItemId)
                .orElseThrow(() -> new EntityNotFoundException("Reconciliation item not found: " + reconciliationItemId));

        // Reset statement item if it was matched
        if (reconItem.getStatementItem() != null) {
            BankStatementItem stmtItem = reconItem.getStatementItem();
            stmtItem.setMatchStatus(StatementItemMatchStatus.UNMATCHED);
            stmtItem.setMatchType(null);
            stmtItem.setMatchedTransaction(null);
            stmtItem.setMatchedAt(null);
            stmtItem.setMatchedBy(null);
            statementItemRepository.save(stmtItem);
        }

        reconciliationItemRepository.delete(reconItem);
        updateReconciliationCounts(recon);
        reconciliationRepository.save(recon);
    }

    @Transactional
    public void createTransactionFromStatementItem(
            UUID reconciliationId, UUID statementItemId, UUID templateId,
            String description, String username) {

        BankReconciliation recon = findById(reconciliationId);
        if (recon.isCompleted()) {
            throw new IllegalStateException("Rekonsiliasi sudah selesai");
        }

        BankStatementItem item = statementItemRepository.findById(statementItemId)
                .orElseThrow(() -> new EntityNotFoundException("Statement item not found: " + statementItemId));

        BigDecimal amount = item.getDebitAmount() != null && item.getDebitAmount().compareTo(BigDecimal.ZERO) > 0
                ? item.getDebitAmount()
                : item.getCreditAmount();

        String desc = description != null && !description.isBlank() ? description : item.getDescription();

        Transaction transaction = transactionService.createFromReconciliation(
                templateId, item.getTransactionDate(), amount, desc, username);

        // Auto-match the new transaction
        recon.setStatus(ReconciliationStatus.IN_PROGRESS);
        createMatch(recon, item, transaction, MatchType.MANUAL, new BigDecimal("1.00"), username);
        updateReconciliationCounts(recon);
        reconciliationRepository.save(recon);
    }

    @Transactional
    public void complete(UUID reconciliationId, String username) {
        BankReconciliation recon = findById(reconciliationId);

        long unmatchedCount = statementItemRepository
                .countByBankStatementIdAndMatchStatus(
                        recon.getBankStatement().getId(), StatementItemMatchStatus.UNMATCHED);

        if (unmatchedCount > 0) {
            throw new IllegalStateException(
                    "Masih ada " + unmatchedCount + " item yang belum dicocokkan. "
                            + "Cocokkan atau tandai sebagai bank-only/book-only terlebih dahulu.");
        }

        recon.setStatus(ReconciliationStatus.COMPLETED);
        recon.setCompletedAt(LocalDateTime.now());
        recon.setCompletedBy(username);
        updateReconciliationCounts(recon);
        reconciliationRepository.save(recon);
    }

    public List<Transaction> findUnmatchedBookTransactions(UUID reconciliationId) {
        BankReconciliation recon = findById(reconciliationId);
        ChartOfAccount glAccount = recon.getBankAccount().getGlAccount();
        if (glAccount == null) {
            return List.of();
        }

        // Get all posted journal entries for this account in the period
        List<JournalEntry> entries = journalEntryRepository.findPostedEntriesByAccountAndDateRange(
                glAccount.getId(), recon.getPeriodStart(), recon.getPeriodEnd());

        // Get matched transaction IDs
        Set<UUID> matchedTxnIds = new HashSet<>();
        List<ReconciliationItem> reconItems = reconciliationItemRepository
                .findByReconciliationIdOrderByMatchStatusAsc(reconciliationId);
        for (ReconciliationItem ri : reconItems) {
            if (ri.getTransaction() != null) {
                matchedTxnIds.add(ri.getTransaction().getId());
            }
        }

        // Also include transactions matched directly on statement items
        List<BankStatementItem> stmtItems = statementItemRepository
                .findByBankStatementIdOrderByLineNumberAsc(recon.getBankStatement().getId());
        for (BankStatementItem si : stmtItems) {
            if (si.getMatchedTransaction() != null) {
                matchedTxnIds.add(si.getMatchedTransaction().getId());
            }
        }

        // Return unmatched transactions
        List<Transaction> unmatched = new ArrayList<>();
        Set<UUID> seenTxnIds = new HashSet<>();
        for (JournalEntry entry : entries) {
            UUID txnId = entry.getTransaction().getId();
            if (!matchedTxnIds.contains(txnId) && seenTxnIds.add(txnId)) {
                unmatched.add(entry.getTransaction());
            }
        }
        return unmatched;
    }

    private BigDecimal computeBookBalance(UUID accountId, LocalDate periodStart, LocalDate periodEnd) {
        // Balance = sum(debit) - sum(credit) for the period
        // For asset accounts (bank), debit increases balance
        BigDecimal totalDebit = journalEntryRepository.sumDebitByAccountAndDateRange(
                accountId, periodStart, periodEnd);
        BigDecimal totalCredit = journalEntryRepository.sumCreditByAccountAndDateRange(
                accountId, periodStart, periodEnd);

        // Also include prior balance
        BigDecimal priorDebit = journalEntryRepository.sumDebitBeforeDate(accountId, periodStart);
        BigDecimal priorCredit = journalEntryRepository.sumCreditBeforeDate(accountId, periodStart);

        BigDecimal priorBalance = priorDebit.subtract(priorCredit);
        BigDecimal periodNet = totalDebit.subtract(totalCredit);

        return priorBalance.add(periodNet);
    }

    private void updateReconciliationCounts(BankReconciliation recon) {
        UUID statementId = recon.getBankStatement().getId();
        long matched = statementItemRepository.countByBankStatementIdAndMatchStatus(
                statementId, StatementItemMatchStatus.MATCHED);
        long unmatched = statementItemRepository.countByBankStatementIdAndMatchStatus(
                statementId, StatementItemMatchStatus.UNMATCHED);
        long bankOnly = statementItemRepository.countByBankStatementIdAndMatchStatus(
                statementId, StatementItemMatchStatus.BANK_ONLY);
        long bookOnly = reconciliationItemRepository.countByReconciliationIdAndMatchStatus(
                recon.getId(), StatementItemMatchStatus.BOOK_ONLY);

        recon.setMatchedCount((int) matched);
        recon.setUnmatchedBankCount((int) (unmatched + bankOnly));
        recon.setUnmatchedBookCount((int) bookOnly);
    }
}
