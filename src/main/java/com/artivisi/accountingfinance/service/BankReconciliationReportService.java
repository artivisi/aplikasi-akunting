package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.entity.BankReconciliation;
import com.artivisi.accountingfinance.entity.BankStatementItem;
import com.artivisi.accountingfinance.entity.ReconciliationItem;
import com.artivisi.accountingfinance.enums.StatementItemMatchStatus;
import com.artivisi.accountingfinance.repository.BankStatementItemRepository;
import com.artivisi.accountingfinance.repository.ReconciliationItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BankReconciliationReportService {

    private final BankReconciliationService reconciliationService;
    private final BankStatementItemRepository statementItemRepository;
    private final ReconciliationItemRepository reconciliationItemRepository;

    public Map<String, Object> getSummary(UUID reconciliationId) {
        BankReconciliation recon = reconciliationService.findById(reconciliationId);
        List<ReconciliationItem> items = reconciliationItemRepository
                .findByReconciliationIdWithRelations(reconciliationId);

        long matched = items.stream()
                .filter(i -> i.getMatchStatus() == StatementItemMatchStatus.MATCHED).count();
        long bankOnly = items.stream()
                .filter(i -> i.getMatchStatus() == StatementItemMatchStatus.BANK_ONLY).count();
        long bookOnly = items.stream()
                .filter(i -> i.getMatchStatus() == StatementItemMatchStatus.BOOK_ONLY).count();

        BigDecimal matchedAmount = items.stream()
                .filter(i -> i.getMatchStatus() == StatementItemMatchStatus.MATCHED && i.getStatementItem() != null)
                .map(i -> i.getStatementItem().getNetAmount().abs())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal bankOnlyAmount = items.stream()
                .filter(i -> i.getMatchStatus() == StatementItemMatchStatus.BANK_ONLY && i.getStatementItem() != null)
                .map(i -> i.getStatementItem().getNetAmount().abs())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal bookOnlyAmount = items.stream()
                .filter(i -> i.getMatchStatus() == StatementItemMatchStatus.BOOK_ONLY && i.getTransaction() != null)
                .map(i -> i.getTransaction().getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return Map.of(
                "matchedCount", matched,
                "bankOnlyCount", bankOnly,
                "bookOnlyCount", bookOnly,
                "totalItems", recon.getTotalStatementItems() != null ? recon.getTotalStatementItems() : 0,
                "matchedAmount", matchedAmount,
                "bankOnlyAmount", bankOnlyAmount,
                "bookOnlyAmount", bookOnlyAmount,
                "bookBalance", recon.getBookBalance() != null ? recon.getBookBalance() : BigDecimal.ZERO,
                "bankBalance", recon.getBankBalance() != null ? recon.getBankBalance() : BigDecimal.ZERO
        );
    }

    public Map<String, Object> getReconciliationStatement(UUID reconciliationId) {
        BankReconciliation recon = reconciliationService.findById(reconciliationId);
        List<ReconciliationItem> items = reconciliationItemRepository
                .findByReconciliationIdWithRelations(reconciliationId);

        BigDecimal bookBalance = recon.getBookBalance() != null ? recon.getBookBalance() : BigDecimal.ZERO;
        BigDecimal bankBalance = recon.getBankBalance() != null ? recon.getBankBalance() : BigDecimal.ZERO;

        // Bank-only items: in bank but not in books (need to add/subtract from book balance)
        BigDecimal bankOnlyDebitTotal = BigDecimal.ZERO;
        BigDecimal bankOnlyCreditTotal = BigDecimal.ZERO;
        for (ReconciliationItem ri : items) {
            if (ri.getMatchStatus() == StatementItemMatchStatus.BANK_ONLY && ri.getStatementItem() != null) {
                BankStatementItem si = ri.getStatementItem();
                if (si.getDebitAmount() != null && si.getDebitAmount().compareTo(BigDecimal.ZERO) > 0) {
                    bankOnlyDebitTotal = bankOnlyDebitTotal.add(si.getDebitAmount());
                }
                if (si.getCreditAmount() != null && si.getCreditAmount().compareTo(BigDecimal.ZERO) > 0) {
                    bankOnlyCreditTotal = bankOnlyCreditTotal.add(si.getCreditAmount());
                }
            }
        }

        // Book-only items: in books but not in bank (need to add/subtract from bank balance)
        BigDecimal bookOnlyTotal = BigDecimal.ZERO;
        for (ReconciliationItem ri : items) {
            if (ri.getMatchStatus() == StatementItemMatchStatus.BOOK_ONLY && ri.getTransaction() != null) {
                bookOnlyTotal = bookOnlyTotal.add(ri.getTransaction().getAmount());
            }
        }

        BigDecimal adjustedBookBalance = bookBalance.add(bankOnlyCreditTotal).subtract(bankOnlyDebitTotal);
        BigDecimal adjustedBankBalance = bankBalance.add(bookOnlyTotal);
        BigDecimal difference = adjustedBookBalance.subtract(adjustedBankBalance);

        return Map.of(
                "bookBalance", bookBalance,
                "bankBalance", bankBalance,
                "bankOnlyDebitTotal", bankOnlyDebitTotal,
                "bankOnlyCreditTotal", bankOnlyCreditTotal,
                "bookOnlyTotal", bookOnlyTotal,
                "adjustedBookBalance", adjustedBookBalance,
                "adjustedBankBalance", adjustedBankBalance,
                "difference", difference
        );
    }

    public List<ReconciliationItem> getOutstandingItems(UUID reconciliationId) {
        List<ReconciliationItem> items = reconciliationItemRepository
                .findByReconciliationIdWithRelations(reconciliationId);

        return items.stream()
                .filter(i -> i.getMatchStatus() == StatementItemMatchStatus.BANK_ONLY
                        || i.getMatchStatus() == StatementItemMatchStatus.BOOK_ONLY)
                .toList();
    }
}
