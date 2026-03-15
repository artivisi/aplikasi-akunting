package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.entity.ChartOfAccount;
import com.artivisi.accountingfinance.entity.Employee;
import com.artivisi.accountingfinance.entity.FiscalAdjustment;
import com.artivisi.accountingfinance.entity.FiscalLossCarryforward;
import com.artivisi.accountingfinance.entity.PtkpStatus;
import com.artivisi.accountingfinance.entity.TaxTransactionDetail;
import com.artivisi.accountingfinance.enums.FiscalAdjustmentDirection;
import com.artivisi.accountingfinance.enums.TaxType;
import com.artivisi.accountingfinance.repository.FiscalLossCarryforwardRepository;
import com.artivisi.accountingfinance.repository.TaxTransactionDetailRepository;
import com.artivisi.accountingfinance.service.PayrollService.YearlyPayrollSummary;
import com.artivisi.accountingfinance.service.ReportService.BalanceSheetReport;
import com.artivisi.accountingfinance.service.ReportService.IncomeStatementItem;
import com.artivisi.accountingfinance.service.ReportService.IncomeStatementReport;
import com.artivisi.accountingfinance.service.TaxReportDetailService.PPhBadanCalculation;
import com.artivisi.accountingfinance.service.TaxReportDetailService.RekonsiliasiFiskalReport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for generating SPT Tahunan PPh Badan lampiran data.
 * Produces structured data matching Coretax form layouts.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SptTahunanExportService {

    private final TaxReportDetailService taxReportDetailService;
    private final ReportService reportService;
    private final DepreciationReportService depreciationReportService;
    private final TaxTransactionDetailRepository taxTransactionDetailRepository;
    private final PayrollService payrollService;
    private final Pph21CalculationService pph21CalculationService;
    private final FiscalLossCarryforwardRepository fiscalLossCarryforwardRepository;

    // Account code prefixes for categorization
    private static final String OPERATING_REVENUE_PREFIX = "4.1";
    private static final String OTHER_INCOME_PREFIX = "4.2";
    private static final String OPERATING_EXPENSE_PREFIX = "5.1";
    private static final String OTHER_EXPENSE_PREFIX = "5.2";
    private static final String TAX_EXPENSE_PREFIX = "5.9";

    // ==================== L1: REKONSILIASI FISKAL ====================

    /**
     * Generate L1 (Rekonsiliasi Fiskal) in Coretax structure.
     * Maps the app's income statement and fiscal adjustments to the
     * standard L1 form sections (I-III).
     */
    public L1Report generateL1(int year) {
        RekonsiliasiFiskalReport rekon = taxReportDetailService.generateRekonsiliasiFiskal(year);
        IncomeStatementReport incomeStatement = rekon.incomeStatement();

        // Categorize revenue items
        List<L1LineItem> operatingRevenue = categorizeItems(incomeStatement.revenueItems(), OPERATING_REVENUE_PREFIX);
        List<L1LineItem> otherIncome = categorizeItems(incomeStatement.revenueItems(), OTHER_INCOME_PREFIX);

        BigDecimal totalOperatingRevenue = sumLineItems(operatingRevenue);
        BigDecimal totalOtherIncome = sumLineItems(otherIncome);

        // Categorize expense items (exclude tax expenses from L1)
        List<L1LineItem> operatingExpenses = categorizeItems(incomeStatement.expenseItems(), OPERATING_EXPENSE_PREFIX);
        List<L1LineItem> otherExpenses = categorizeItems(incomeStatement.expenseItems(), OTHER_EXPENSE_PREFIX);

        BigDecimal totalOperatingExpenses = sumLineItems(operatingExpenses);
        BigDecimal totalOtherExpenses = sumLineItems(otherExpenses);

        // Gross profit (for service companies, COGS = 0)
        BigDecimal grossProfit = totalOperatingRevenue;

        // Net operating income
        BigDecimal netOperatingIncome = grossProfit.subtract(totalOperatingExpenses);

        // Net other income
        BigDecimal netOtherIncome = totalOtherIncome.subtract(totalOtherExpenses);

        // Commercial net income
        BigDecimal commercialNetIncome = netOperatingIncome.add(netOtherIncome);

        // Fiscal adjustments grouped by direction
        List<L1AdjustmentItem> positiveAdjustments = rekon.adjustments().stream()
                .filter(a -> a.getAdjustmentDirection() == FiscalAdjustmentDirection.POSITIVE)
                .map(this::toAdjustmentItem)
                .toList();

        List<L1AdjustmentItem> negativeAdjustments = rekon.adjustments().stream()
                .filter(a -> a.getAdjustmentDirection() == FiscalAdjustmentDirection.NEGATIVE)
                .map(this::toAdjustmentItem)
                .toList();

        // Loss carryforward (kompensasi kerugian fiskal)
        List<FiscalLossCarryforward> activeLosses = fiscalLossCarryforwardRepository.findActiveLossesForYear(year);
        BigDecimal totalLossCompensation = activeLosses.stream()
                .map(FiscalLossCarryforward::getRemainingAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<L1LossItem> lossItems = activeLosses.stream()
                .map(loss -> new L1LossItem(
                        loss.getOriginYear(), loss.getRemainingAmount(), loss.getExpiryYear()))
                .toList();

        // Adjusted PKP after loss compensation
        BigDecimal pkpAfterLoss = rekon.pkp().subtract(totalLossCompensation).max(BigDecimal.ZERO);

        return new L1Report(
                year,
                // Section I: Penghasilan Neto Fiskal
                operatingRevenue, totalOperatingRevenue,
                BigDecimal.ZERO, // COGS (service company)
                grossProfit,
                operatingExpenses, totalOperatingExpenses,
                netOperatingIncome,
                otherIncome, totalOtherIncome,
                otherExpenses, totalOtherExpenses,
                netOtherIncome,
                commercialNetIncome,
                positiveAdjustments, rekon.totalPositiveAdjustment(),
                negativeAdjustments, rekon.totalNegativeAdjustment(),
                rekon.pkp(),
                // Loss carryforward
                lossItems, totalLossCompensation,
                pkpAfterLoss,
                // Section II: PPh Terutang
                rekon.pphBadan(),
                // Section III: Kredit Pajak
                rekon.pphBadan().kreditPajakPPh23(),
                rekon.pphBadan().kreditPajakPPh25(),
                rekon.pphBadan().totalKreditPajak(),
                rekon.pphBadan().pph29()
        );
    }

    // ==================== L4: PENGHASILAN FINAL ====================

    /**
     * Generate L4 (Penghasilan Final) — PPh 4(2) summary by tax object code.
     */
    public L4Report generateL4(int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        List<TaxTransactionDetail> pph42Details = taxTransactionDetailRepository
                .findByTaxTypeAndDateRange(TaxType.PPH_42, startDate, endDate);

        // Group by tax object code and aggregate
        Map<String, List<TaxTransactionDetail>> byObjectCode = pph42Details.stream()
                .collect(Collectors.groupingBy(d -> d.getTaxObjectCode() != null ? d.getTaxObjectCode() : "UNKNOWN"));

        List<L4LineItem> items = new ArrayList<>();
        BigDecimal totalGross = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;

        for (Map.Entry<String, List<TaxTransactionDetail>> entry : byObjectCode.entrySet()) {
            String objectCode = entry.getKey();
            List<TaxTransactionDetail> details = entry.getValue();

            BigDecimal gross = details.stream()
                    .map(TaxTransactionDetail::getGrossAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal tax = details.stream()
                    .map(TaxTransactionDetail::getTaxAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal rate = details.getFirst().getTaxRate();

            items.add(new L4LineItem(objectCode, describeObjectCode(objectCode), gross, rate, tax));
            totalGross = totalGross.add(gross);
            totalTax = totalTax.add(tax);
        }

        // Sort by object code
        items.sort((a, b) -> a.taxObjectCode().compareTo(b.taxObjectCode()));

        return new L4Report(year, items, totalGross, totalTax);
    }

    // ==================== TRANSKRIP 8A: LAPORAN KEUANGAN ====================

    /**
     * Generate Transkrip 8A (Laporan Keuangan) — structured balance sheet
     * and income statement matching Coretax 8A-Jasa layout.
     */
    public Transkrip8AReport generateTranskrip8A(int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        IncomeStatementReport incomeStatement = reportService.generateIncomeStatement(startDate, endDate);
        BalanceSheetReport balanceSheet = reportService.generateBalanceSheet(endDate);

        // Map income statement items to transcript lines
        List<Transkrip8ALineItem> revenueLines = incomeStatement.revenueItems().stream()
                .map(item -> new Transkrip8ALineItem(
                        item.account().getAccountCode(),
                        item.account().getAccountName(),
                        item.balance()))
                .toList();

        List<Transkrip8ALineItem> expenseLines = incomeStatement.expenseItems().stream()
                .map(item -> new Transkrip8ALineItem(
                        item.account().getAccountCode(),
                        item.account().getAccountName(),
                        item.balance()))
                .toList();

        // Map balance sheet items
        List<Transkrip8ALineItem> assetLines = balanceSheet.assetItems().stream()
                .map(item -> new Transkrip8ALineItem(
                        item.account().getAccountCode(),
                        item.account().getAccountName(),
                        item.balance()))
                .toList();

        List<Transkrip8ALineItem> liabilityLines = balanceSheet.liabilityItems().stream()
                .map(item -> new Transkrip8ALineItem(
                        item.account().getAccountCode(),
                        item.account().getAccountName(),
                        item.balance()))
                .toList();

        List<Transkrip8ALineItem> equityLines = balanceSheet.equityItems().stream()
                .map(item -> new Transkrip8ALineItem(
                        item.account().getAccountCode(),
                        item.account().getAccountName(),
                        item.balance()))
                .toList();

        return new Transkrip8AReport(
                year,
                // Neraca (Balance Sheet)
                assetLines, balanceSheet.totalAssets(),
                liabilityLines, balanceSheet.totalLiabilities(),
                equityLines, balanceSheet.totalEquity(),
                balanceSheet.currentYearEarnings(),
                // Laba Rugi (Income Statement)
                revenueLines, incomeStatement.totalRevenue(),
                expenseLines, incomeStatement.totalExpense(),
                incomeStatement.netIncome()
        );
    }

    // ==================== L9: PENYUSUTAN & AMORTISASI ====================

    /**
     * Generate L9 (Penyusutan & Amortisasi) data matching DJP converter template.
     */
    public L9Report generateL9(int year) {
        DepreciationReportService.DepreciationReport depReport =
                depreciationReportService.generateReport(year);

        List<L9LineItem> items = depReport.items().stream()
                .map(item -> new L9LineItem(
                        item.assetName(),
                        mapToFiscalGroup(item.categoryName(), item.usefulLifeYears()),
                        item.purchaseDate(),
                        item.purchaseCost(),
                        mapDepreciationMethod(item.depreciationMethod()),
                        item.usefulLifeYears(),
                        item.depreciationThisYear(),
                        item.accumulatedDepreciation(),
                        item.bookValue()))
                .toList();

        return new L9Report(
                year, items,
                depReport.totalPurchaseCost(),
                depReport.totalDepreciationThisYear(),
                depReport.totalAccumulatedDepreciation(),
                depReport.totalBookValue());
    }

    // ==================== BPA1: e-BUPOT PPh 21 ANNUAL ====================

    /**
     * Generate bulk 1721-A1 data for all employees with payroll in the given year.
     * Matches DJP BPA1 converter template for Coretax XML import.
     */
    public Bpa1Report generateBpa1(int year) {
        List<UUID> employeeIds = payrollService.getEmployeesWithPayrollInYear(year);

        List<Bpa1LineItem> items = new ArrayList<>();
        BigDecimal totalGross = BigDecimal.ZERO;
        BigDecimal totalPph21Terutang = BigDecimal.ZERO;
        BigDecimal totalPph21Dipotong = BigDecimal.ZERO;

        for (UUID employeeId : employeeIds) {
            YearlyPayrollSummary summary = payrollService.getYearlyPayrollSummary(employeeId, year);
            Employee employee = summary.employee();

            BigDecimal penghasilanBruto = summary.totalGross();
            BigDecimal biayaJabatan = penghasilanBruto.multiply(new BigDecimal("5"))
                    .divide(new BigDecimal("100"), 0, RoundingMode.HALF_UP)
                    .min(Pph21CalculationService.BIAYA_JABATAN_ANNUAL_MAX);
            BigDecimal bpjsDeduction = summary.totalBpjsEmployee();
            BigDecimal penghasilanNeto = penghasilanBruto.subtract(biayaJabatan).subtract(bpjsDeduction);
            BigDecimal ptkp = employee.getPtkpStatus().getAnnualAmount();
            BigDecimal pkpRaw = penghasilanNeto.subtract(ptkp).max(BigDecimal.ZERO);
            BigDecimal pkp = pkpRaw.divide(new BigDecimal("1000"), 0, RoundingMode.FLOOR)
                    .multiply(new BigDecimal("1000"));
            BigDecimal pph21Terutang = pph21CalculationService.calculateProgressiveTax(pkp);
            BigDecimal pph21Dipotong = summary.totalPph21();

            items.add(new Bpa1LineItem(
                    employee.getNpwp(),
                    employee.getNikKtp(),
                    employee.getName(),
                    employee.getPtkpStatus(),
                    summary.monthCount(),
                    penghasilanBruto,
                    biayaJabatan,
                    bpjsDeduction,
                    penghasilanNeto,
                    ptkp,
                    pkp,
                    pph21Terutang,
                    pph21Dipotong,
                    pph21Terutang.subtract(pph21Dipotong)
            ));

            totalGross = totalGross.add(penghasilanBruto);
            totalPph21Terutang = totalPph21Terutang.add(pph21Terutang);
            totalPph21Dipotong = totalPph21Dipotong.add(pph21Dipotong);
        }

        return new Bpa1Report(year, items, totalGross, totalPph21Terutang, totalPph21Dipotong);
    }

    // ==================== L7: FISCAL LOSS CARRYFORWARD ====================

    /**
     * Get active fiscal losses that can be deducted from PKP for the given year.
     * Per UU PPh Pasal 6 ayat 2, losses can be carried forward for 5 years.
     */
    public LossCarryforwardReport generateLossCarryforward(int year) {
        List<FiscalLossCarryforward> allLosses = fiscalLossCarryforwardRepository.findAllByOrderByOriginYearDesc();
        List<FiscalLossCarryforward> activeLosses = fiscalLossCarryforwardRepository.findActiveLossesForYear(year);

        BigDecimal totalRemaining = activeLosses.stream()
                .map(FiscalLossCarryforward::getRemainingAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<LossCarryforwardItem> items = allLosses.stream()
                .map(loss -> new LossCarryforwardItem(
                        loss.getId(),
                        loss.getOriginYear(),
                        loss.getOriginalAmount(),
                        loss.getUsedAmount(),
                        loss.getRemainingAmount(),
                        loss.getExpiryYear(),
                        loss.isExpired(year),
                        loss.getNotes()))
                .toList();

        return new LossCarryforwardReport(year, items, totalRemaining);
    }

    // ==================== LOSS CARRYFORWARD CRUD ====================

    @Transactional
    public FiscalLossCarryforward saveLossCarryforward(FiscalLossCarryforward loss) {
        return fiscalLossCarryforwardRepository.save(loss);
    }

    @Transactional
    public void deleteLossCarryforward(UUID id) {
        fiscalLossCarryforwardRepository.deleteById(id);
    }

    public List<FiscalLossCarryforward> findAllLossCarryforwards() {
        return fiscalLossCarryforwardRepository.findAllByOrderByOriginYearDesc();
    }

    public FiscalLossCarryforward findLossCarryforwardById(UUID id) {
        return fiscalLossCarryforwardRepository.findById(id).orElse(null);
    }

    // ==================== HELPERS ====================

    private List<L1LineItem> categorizeItems(List<IncomeStatementItem> items, String prefix) {
        return items.stream()
                .filter(item -> item.account().getAccountCode().startsWith(prefix))
                .filter(item -> item.balance().compareTo(BigDecimal.ZERO) != 0)
                .map(item -> new L1LineItem(
                        item.account().getAccountCode(),
                        item.account().getAccountName(),
                        item.balance()))
                .toList();
    }

    private BigDecimal sumLineItems(List<L1LineItem> items) {
        return items.stream()
                .map(L1LineItem::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private L1AdjustmentItem toAdjustmentItem(FiscalAdjustment adj) {
        return new L1AdjustmentItem(
                adj.getDescription(),
                adj.getAdjustmentCategory().name(),
                adj.getAdjustmentDirection().name(),
                adj.getAmount(),
                adj.getAccountCode());
    }

    private String describeObjectCode(String code) {
        return switch (code) {
            case "28-409-01" -> "Sewa Tanah dan/atau Bangunan";
            case "28-409-07" -> "Jasa Konstruksi - Pelaksana Kecil";
            case "28-409-08" -> "Jasa Konstruksi - Pelaksana Menengah/Besar";
            case "28-423-01" -> "PPh Final UMKM (PP 55)";
            default -> "PPh Final - " + code;
        };
    }

    /**
     * Map asset category and useful life to fiscal asset group (Kelompok I-IV or Bangunan).
     * Per UU PPh Pasal 11, fiscal groups determine max useful life for tax depreciation.
     */
    private String mapToFiscalGroup(String categoryName, int usefulLifeYears) {
        // Map based on useful life (fiscal rules):
        // Kelompok I: 4 years, Kelompok II: 8 years, Kelompok III: 16 years, Kelompok IV: 20 years
        // Bangunan Permanen: 20 years, Bangunan Non-Permanen: 10 years
        if (categoryName != null && categoryName.toLowerCase().contains("bangunan")) {
            return usefulLifeYears <= 10 ? "Bangunan Non-Permanen" : "Bangunan Permanen";
        }
        if (usefulLifeYears <= 4) return "Kelompok I";
        if (usefulLifeYears <= 8) return "Kelompok II";
        if (usefulLifeYears <= 16) return "Kelompok III";
        return "Kelompok IV";
    }

    private String mapDepreciationMethod(String method) {
        if (method == null) return "Garis Lurus";
        return switch (method) {
            case "Saldo Menurun" -> "Saldo Menurun";
            default -> "Garis Lurus";
        };
    }

    // ==================== DTOs ====================

    public record L1Report(
            int year,
            // Section I: Penghasilan Neto Fiskal
            List<L1LineItem> operatingRevenue,
            BigDecimal totalOperatingRevenue,
            BigDecimal cogs,
            BigDecimal grossProfit,
            List<L1LineItem> operatingExpenses,
            BigDecimal totalOperatingExpenses,
            BigDecimal netOperatingIncome,
            List<L1LineItem> otherIncome,
            BigDecimal totalOtherIncome,
            List<L1LineItem> otherExpenses,
            BigDecimal totalOtherExpenses,
            BigDecimal netOtherIncome,
            BigDecimal commercialNetIncome,
            List<L1AdjustmentItem> positiveAdjustments,
            BigDecimal totalPositiveAdjustment,
            List<L1AdjustmentItem> negativeAdjustments,
            BigDecimal totalNegativeAdjustment,
            BigDecimal pkpBeforeLoss,
            // Loss carryforward (kompensasi kerugian)
            List<L1LossItem> lossCarryforwards,
            BigDecimal totalLossCompensation,
            BigDecimal pkp,
            // Section II: PPh Terutang
            PPhBadanCalculation pphBadan,
            // Section III: Kredit Pajak
            BigDecimal kreditPPh23,
            BigDecimal kreditPPh25,
            BigDecimal totalKreditPajak,
            BigDecimal pph29
    ) {}

    public record L1LineItem(
            String accountCode,
            String accountName,
            BigDecimal amount
    ) {}

    public record L1LossItem(
            int originYear,
            BigDecimal remainingAmount,
            int expiryYear
    ) {}

    public record L1AdjustmentItem(
            String description,
            String category,
            String direction,
            BigDecimal amount,
            String accountCode
    ) {}

    public record L4Report(
            int year,
            List<L4LineItem> items,
            BigDecimal totalGross,
            BigDecimal totalTax
    ) {}

    public record L4LineItem(
            String taxObjectCode,
            String description,
            BigDecimal grossAmount,
            BigDecimal taxRate,
            BigDecimal taxAmount
    ) {}

    public record Transkrip8AReport(
            int year,
            // Neraca (Balance Sheet)
            List<Transkrip8ALineItem> assetItems,
            BigDecimal totalAssets,
            List<Transkrip8ALineItem> liabilityItems,
            BigDecimal totalLiabilities,
            List<Transkrip8ALineItem> equityItems,
            BigDecimal totalEquity,
            BigDecimal currentYearEarnings,
            // Laba Rugi (Income Statement)
            List<Transkrip8ALineItem> revenueItems,
            BigDecimal totalRevenue,
            List<Transkrip8ALineItem> expenseItems,
            BigDecimal totalExpense,
            BigDecimal netIncome
    ) {}

    public record Transkrip8ALineItem(
            String accountCode,
            String accountName,
            BigDecimal amount
    ) {}

    public record L9Report(
            int year,
            List<L9LineItem> items,
            BigDecimal totalPurchaseCost,
            BigDecimal totalDepreciationThisYear,
            BigDecimal totalAccumulatedDepreciation,
            BigDecimal totalBookValue
    ) {}

    public record L9LineItem(
            String assetName,
            String fiscalGroup,
            LocalDate acquisitionDate,
            BigDecimal acquisitionCost,
            String depreciationMethod,
            int usefulLifeYears,
            BigDecimal depreciationThisYear,
            BigDecimal accumulatedDepreciation,
            BigDecimal bookValue
    ) {}

    public record Bpa1Report(
            int year,
            List<Bpa1LineItem> items,
            BigDecimal totalGross,
            BigDecimal totalPph21Terutang,
            BigDecimal totalPph21Dipotong
    ) {}

    public record LossCarryforwardReport(
            int year,
            List<LossCarryforwardItem> items,
            BigDecimal totalActiveRemaining
    ) {}

    public record LossCarryforwardItem(
            UUID id,
            int originYear,
            BigDecimal originalAmount,
            BigDecimal usedAmount,
            BigDecimal remainingAmount,
            int expiryYear,
            boolean expired,
            String notes
    ) {}

    public record Bpa1LineItem(
            String npwp,
            String nik,
            String name,
            PtkpStatus ptkpStatus,
            int monthCount,
            BigDecimal penghasilanBruto,
            BigDecimal biayaJabatan,
            BigDecimal bpjsDeduction,
            BigDecimal penghasilanNeto,
            BigDecimal ptkp,
            BigDecimal pkp,
            BigDecimal pph21Terutang,
            BigDecimal pph21Dipotong,
            BigDecimal pph21KurangBayar
    ) {}
}
