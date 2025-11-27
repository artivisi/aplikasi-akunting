package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.entity.ChartOfAccount;
import com.artivisi.accountingfinance.enums.NormalBalance;
import com.artivisi.accountingfinance.repository.ChartOfAccountRepository;
import com.artivisi.accountingfinance.repository.JournalEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class TaxReportService {

    private final ChartOfAccountRepository chartOfAccountRepository;
    private final JournalEntryRepository journalEntryRepository;

    // Tax account codes (Indonesian standard)
    private static final String PPN_MASUKAN_CODE = "1.1.25";
    private static final String HUTANG_PPN_CODE = "2.1.03";
    private static final String HUTANG_PPH_21_CODE = "2.1.20";
    private static final String HUTANG_PPH_23_CODE = "2.1.21";
    private static final String HUTANG_PPH_42_CODE = "2.1.22";
    private static final String HUTANG_PPH_25_CODE = "2.1.23";
    private static final String HUTANG_PPH_29_CODE = "2.1.24";

    public PPNSummaryReport generatePPNSummary(LocalDate startDate, LocalDate endDate) {
        ChartOfAccount ppnMasukanAccount = chartOfAccountRepository.findByAccountCode(PPN_MASUKAN_CODE)
                .orElseThrow(() -> new IllegalStateException("PPN Masukan account not found: " + PPN_MASUKAN_CODE));
        ChartOfAccount hutangPPNAccount = chartOfAccountRepository.findByAccountCode(HUTANG_PPN_CODE)
                .orElseThrow(() -> new IllegalStateException("Hutang PPN account not found: " + HUTANG_PPN_CODE));

        // PPN Masukan (Input VAT) - ASSET account, DEBIT normal balance
        // Debit increases the balance (VAT from purchases)
        BigDecimal ppnMasukanDebit = journalEntryRepository.sumDebitByAccountAndDateRange(
                ppnMasukanAccount.getId(), startDate, endDate);
        BigDecimal ppnMasukanCredit = journalEntryRepository.sumCreditByAccountAndDateRange(
                ppnMasukanAccount.getId(), startDate, endDate);
        BigDecimal ppnMasukan = ppnMasukanDebit.subtract(ppnMasukanCredit);

        // Hutang PPN (Output VAT) - LIABILITY account, CREDIT normal balance
        // Credit increases the balance (VAT from sales)
        BigDecimal hutangPPNDebit = journalEntryRepository.sumDebitByAccountAndDateRange(
                hutangPPNAccount.getId(), startDate, endDate);
        BigDecimal hutangPPNCredit = journalEntryRepository.sumCreditByAccountAndDateRange(
                hutangPPNAccount.getId(), startDate, endDate);
        BigDecimal ppnKeluaran = hutangPPNCredit.subtract(hutangPPNDebit);

        // Net PPN = Output VAT - Input VAT
        // Positive = amount to pay to government
        // Negative = VAT refund (lebih bayar)
        BigDecimal netPPN = ppnKeluaran.subtract(ppnMasukan);

        return new PPNSummaryReport(startDate, endDate, ppnKeluaran, ppnMasukan, netPPN);
    }

    public PPh23WithholdingReport generatePPh23Withholding(LocalDate startDate, LocalDate endDate) {
        ChartOfAccount hutangPPh23Account = chartOfAccountRepository.findByAccountCode(HUTANG_PPH_23_CODE)
                .orElseThrow(() -> new IllegalStateException("Hutang PPh 23 account not found: " + HUTANG_PPH_23_CODE));

        // Hutang PPh 23 - LIABILITY account, CREDIT normal balance
        // Credit increases (withholding from payments to vendors)
        // Debit decreases (when deposited to government)
        BigDecimal debit = journalEntryRepository.sumDebitByAccountAndDateRange(
                hutangPPh23Account.getId(), startDate, endDate);
        BigDecimal credit = journalEntryRepository.sumCreditByAccountAndDateRange(
                hutangPPh23Account.getId(), startDate, endDate);

        BigDecimal totalWithheld = credit;
        BigDecimal totalDeposited = debit;
        BigDecimal balance = credit.subtract(debit);

        return new PPh23WithholdingReport(startDate, endDate, totalWithheld, totalDeposited, balance);
    }

    public TaxSummaryReport generateTaxSummary(LocalDate startDate, LocalDate endDate) {
        List<TaxAccountItem> taxItems = new ArrayList<>();

        // PPN
        addTaxItem(taxItems, PPN_MASUKAN_CODE, "PPN Masukan", startDate, endDate);
        addTaxItem(taxItems, HUTANG_PPN_CODE, "Hutang PPN", startDate, endDate);

        // PPh
        addTaxItem(taxItems, HUTANG_PPH_21_CODE, "Hutang PPh 21", startDate, endDate);
        addTaxItem(taxItems, HUTANG_PPH_23_CODE, "Hutang PPh 23", startDate, endDate);
        addTaxItem(taxItems, HUTANG_PPH_42_CODE, "Hutang PPh 4(2)", startDate, endDate);
        addTaxItem(taxItems, HUTANG_PPH_25_CODE, "Hutang PPh 25", startDate, endDate);
        addTaxItem(taxItems, HUTANG_PPH_29_CODE, "Hutang PPh 29", startDate, endDate);

        BigDecimal totalBalance = taxItems.stream()
                .map(TaxAccountItem::balance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new TaxSummaryReport(startDate, endDate, taxItems, totalBalance);
    }

    private void addTaxItem(List<TaxAccountItem> items, String accountCode, String label,
                           LocalDate startDate, LocalDate endDate) {
        chartOfAccountRepository.findByAccountCode(accountCode).ifPresent(account -> {
            BigDecimal debit = journalEntryRepository.sumDebitByAccountAndDateRange(
                    account.getId(), startDate, endDate);
            BigDecimal credit = journalEntryRepository.sumCreditByAccountAndDateRange(
                    account.getId(), startDate, endDate);

            BigDecimal balance;
            if (account.getNormalBalance() == NormalBalance.DEBIT) {
                balance = debit.subtract(credit);
            } else {
                balance = credit.subtract(debit);
            }

            items.add(new TaxAccountItem(account, label, debit, credit, balance));
        });
    }

    // DTOs
    public record PPNSummaryReport(
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal ppnKeluaran,  // Output VAT (from sales)
            BigDecimal ppnMasukan,   // Input VAT (from purchases)
            BigDecimal netPPN        // Amount to pay (positive) or refund (negative)
    ) {}

    public record PPh23WithholdingReport(
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal totalWithheld,   // Total PPh 23 withheld from vendors
            BigDecimal totalDeposited,  // Total deposited to government
            BigDecimal balance          // Outstanding balance to deposit
    ) {}

    public record TaxSummaryReport(
            LocalDate startDate,
            LocalDate endDate,
            List<TaxAccountItem> items,
            BigDecimal totalBalance
    ) {}

    public record TaxAccountItem(
            ChartOfAccount account,
            String label,
            BigDecimal debit,
            BigDecimal credit,
            BigDecimal balance
    ) {}
}
