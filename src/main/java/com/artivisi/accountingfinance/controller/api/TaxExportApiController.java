package com.artivisi.accountingfinance.controller.api;

import com.artivisi.accountingfinance.controller.api.FinancialAnalysisApiController.AnalysisResponse;
import com.artivisi.accountingfinance.entity.FiscalAdjustment;
import com.artivisi.accountingfinance.entity.TaxTransactionDetail;
import com.artivisi.accountingfinance.enums.AuditEventType;
import com.artivisi.accountingfinance.service.CoretaxExportService;
import com.artivisi.accountingfinance.service.ReportExportService;
import com.artivisi.accountingfinance.service.SecurityAuditService;
import com.artivisi.accountingfinance.service.TaxReportDetailService;
import com.artivisi.accountingfinance.service.TaxReportDetailService.PPhBadanCalculation;
import com.artivisi.accountingfinance.service.TaxReportDetailService.PPNDetailReport;
import com.artivisi.accountingfinance.service.TaxReportDetailService.PPh23DetailReport;
import com.artivisi.accountingfinance.service.TaxReportDetailService.RekonsiliasiFiskalReport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tax-export")
@Tag(name = "Tax Export", description = "Export tax data for SPT preparation (e-Faktur, e-Bupot, PPN/PPh reports)")
@PreAuthorize("hasAuthority('SCOPE_tax-export:read')")
@RequiredArgsConstructor
@Slf4j
public class TaxExportApiController {

    private static final String XLSX_CONTENT_TYPE =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private final CoretaxExportService coretaxExportService;
    private final TaxReportDetailService taxReportDetailService;
    private final ReportExportService reportExportService;
    private final SecurityAuditService securityAuditService;

    // ==================== EXCEL EXPORT ENDPOINTS ====================

    @GetMapping("/efaktur-keluaran")
    @Operation(summary = "Export e-Faktur Keluaran (output VAT) to Coretax-compatible Excel")
    public ResponseEntity<byte[]> exportEfakturKeluaran(
            @Parameter(description = "Start month (yyyy-MM)") @RequestParam String startMonth,
            @Parameter(description = "End month (yyyy-MM)") @RequestParam String endMonth) throws IOException {

        LocalDate[] range = parseMonthRange(startMonth, endMonth);

        byte[] excel = coretaxExportService.exportEFakturKeluaran(range[0], range[1]);

        auditAccess("efaktur-keluaran", Map.of("startMonth", startMonth, "endMonth", endMonth));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=efaktur-keluaran-" + startMonth + "-" + endMonth + ".xlsx")
                .contentType(MediaType.parseMediaType(XLSX_CONTENT_TYPE))
                .body(excel);
    }

    @GetMapping("/efaktur-masukan")
    @Operation(summary = "Export e-Faktur Masukan (input VAT) to Coretax-compatible Excel")
    public ResponseEntity<byte[]> exportEfakturMasukan(
            @Parameter(description = "Start month (yyyy-MM)") @RequestParam String startMonth,
            @Parameter(description = "End month (yyyy-MM)") @RequestParam String endMonth) throws IOException {

        LocalDate[] range = parseMonthRange(startMonth, endMonth);

        byte[] excel = coretaxExportService.exportEFakturMasukan(range[0], range[1]);

        auditAccess("efaktur-masukan", Map.of("startMonth", startMonth, "endMonth", endMonth));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=efaktur-masukan-" + startMonth + "-" + endMonth + ".xlsx")
                .contentType(MediaType.parseMediaType(XLSX_CONTENT_TYPE))
                .body(excel);
    }

    @GetMapping("/bupot-unifikasi")
    @Operation(summary = "Export e-Bupot Unifikasi (PPh withholding) to Coretax-compatible Excel")
    public ResponseEntity<byte[]> exportBupotUnifikasi(
            @Parameter(description = "Start month (yyyy-MM)") @RequestParam String startMonth,
            @Parameter(description = "End month (yyyy-MM)") @RequestParam String endMonth) throws IOException {

        LocalDate[] range = parseMonthRange(startMonth, endMonth);

        byte[] excel = coretaxExportService.exportBupotUnifikasi(range[0], range[1]);

        auditAccess("bupot-unifikasi", Map.of("startMonth", startMonth, "endMonth", endMonth));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=bupot-unifikasi-" + startMonth + "-" + endMonth + ".xlsx")
                .contentType(MediaType.parseMediaType(XLSX_CONTENT_TYPE))
                .body(excel);
    }

    // ==================== JSON / EXCEL ENDPOINTS ====================

    @GetMapping("/ppn-detail")
    @Transactional(readOnly = true)
    @Operation(summary = "PPN detail report (JSON or Excel)",
            description = "PPN detail with Faktur Keluaran/Masukan breakdown. Add ?format=excel for XLSX.")
    @ApiResponse(responseCode = "200", description = "JSON report",
            content = @Content(schema = @Schema(implementation = PPNDetailData.class)))
    @ApiResponse(responseCode = "200", description = "Excel download (when format=excel)",
            content = @Content(mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
    public ResponseEntity<?> getPpnDetail(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @Parameter(description = "Set to 'excel' for XLSX download") @RequestParam(required = false) String format) {

        LocalDate start = parseDate(startDate);
        LocalDate end = parseDate(endDate);

        PPNDetailReport report = taxReportDetailService.generatePPNDetailReport(start, end);

        auditAccess("ppn-detail", Map.of("startDate", startDate, "endDate", endDate));

        if ("excel".equals(format)) {
            byte[] excel = reportExportService.exportPpnDetailToExcel(report);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=ppn-detail-" + startDate + "-" + endDate + ".xlsx")
                    .contentType(MediaType.parseMediaType(XLSX_CONTENT_TYPE))
                    .body(excel);
        }

        PPNDetailData data = toPPNDetailData(report);

        return ResponseEntity.ok(new AnalysisResponse<>(
                "ppn-detail", LocalDateTime.now(),
                Map.of("startDate", startDate, "endDate", endDate),
                data,
                Map.of("description", "PPN detail report with Faktur Keluaran and Masukan breakdown",
                        "currency", "IDR")));
    }

    @GetMapping("/pph23-detail")
    @Transactional(readOnly = true)
    @Operation(summary = "PPh 23 withholding tax detail report (JSON or Excel)",
            description = "PPh 23 detail report. Add ?format=excel for XLSX.")
    @ApiResponse(responseCode = "200", description = "JSON report",
            content = @Content(schema = @Schema(implementation = PPh23DetailData.class)))
    @ApiResponse(responseCode = "200", description = "Excel download (when format=excel)",
            content = @Content(mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
    public ResponseEntity<?> getPph23Detail(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @Parameter(description = "Set to 'excel' for XLSX download") @RequestParam(required = false) String format) {

        LocalDate start = parseDate(startDate);
        LocalDate end = parseDate(endDate);

        PPh23DetailReport report = taxReportDetailService.generatePPh23DetailReport(start, end);

        auditAccess("pph23-detail", Map.of("startDate", startDate, "endDate", endDate));

        if ("excel".equals(format)) {
            byte[] excel = reportExportService.exportPph23DetailToExcel(report);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=pph23-detail-" + startDate + "-" + endDate + ".xlsx")
                    .contentType(MediaType.parseMediaType(XLSX_CONTENT_TYPE))
                    .body(excel);
        }

        PPh23DetailData data = toPPh23DetailData(report);

        return ResponseEntity.ok(new AnalysisResponse<>(
                "pph23-detail", LocalDateTime.now(),
                Map.of("startDate", startDate, "endDate", endDate),
                data,
                Map.of("description", "PPh 23 withholding tax detail report",
                        "currency", "IDR")));
    }

    @GetMapping("/rekonsiliasi-fiskal")
    @Transactional(readOnly = true)
    @Operation(summary = "Fiscal reconciliation: commercial income to taxable income (JSON or Excel)",
            description = "Fiscal reconciliation report. Add ?format=excel for XLSX.")
    @ApiResponse(responseCode = "200", description = "JSON report",
            content = @Content(schema = @Schema(implementation = RekonsiliasiFiskalData.class)))
    @ApiResponse(responseCode = "200", description = "Excel download (when format=excel)",
            content = @Content(mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
    public ResponseEntity<?> getRekonsiliasiFiskal(
            @RequestParam int year,
            @Parameter(description = "Set to 'excel' for XLSX download") @RequestParam(required = false) String format) {

        RekonsiliasiFiskalReport report = taxReportDetailService.generateRekonsiliasiFiskal(year);

        auditAccess("rekonsiliasi-fiskal", Map.of("year", String.valueOf(year)));

        if ("excel".equals(format)) {
            byte[] excel = reportExportService.exportRekonsiliasiFiskalToExcel(report);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=rekonsiliasi-fiskal-" + year + ".xlsx")
                    .contentType(MediaType.parseMediaType(XLSX_CONTENT_TYPE))
                    .body(excel);
        }

        RekonsiliasiFiskalData data = toRekonsiliasiFiskalData(report);

        return ResponseEntity.ok(new AnalysisResponse<>(
                "rekonsiliasi-fiskal", LocalDateTime.now(),
                Map.of("year", String.valueOf(year)),
                data,
                Map.of("description", "Fiscal reconciliation: commercial income → taxable income (PKP)",
                        "currency", "IDR")));
    }

    @GetMapping("/pph-badan")
    @Transactional(readOnly = true)
    public ResponseEntity<AnalysisResponse<PPhBadanData>> getPphBadan(
            @RequestParam int year) {

        RekonsiliasiFiskalReport rekonsiliasi = taxReportDetailService.generateRekonsiliasiFiskal(year);
        PPhBadanCalculation calc = rekonsiliasi.pphBadan();

        auditAccess("pph-badan", Map.of("year", String.valueOf(year)));

        PPhBadanData data = new PPhBadanData(
                calc.pkp(), calc.totalRevenue(), calc.pphTerutang(),
                calc.calculationMethod(),
                calc.kreditPajakPPh23(), calc.kreditPajakPPh25(),
                calc.totalKreditPajak(), calc.pph29());

        return ResponseEntity.ok(new AnalysisResponse<>(
                "pph-badan", LocalDateTime.now(),
                Map.of("year", String.valueOf(year)),
                data,
                Map.of("description", "PPh Badan corporate income tax calculation with Pasal 31E facility",
                        "currency", "IDR")));
    }

    // ==================== PARSING HELPERS ====================

    private LocalDate[] parseMonthRange(String startMonth, String endMonth) {
        try {
            YearMonth start = YearMonth.parse(startMonth);
            YearMonth end = YearMonth.parse(endMonth);
            return new LocalDate[]{start.atDay(1), end.atEndOfMonth()};
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Invalid month format. Expected yyyy-MM, got: " + startMonth + ", " + endMonth);
        }
    }

    private LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Invalid date format. Expected yyyy-MM-dd, got: " + date);
        }
    }

    // ==================== MAPPING HELPERS ====================

    private PPNDetailData toPPNDetailData(PPNDetailReport report) {
        List<TaxDetailItem> keluaranItems = report.keluaranItems().stream()
                .map(this::toTaxDetailItem)
                .toList();
        List<TaxDetailItem> masukanItems = report.masukanItems().stream()
                .map(this::toTaxDetailItem)
                .toList();

        PPNTotals totals = new PPNTotals(
                report.totalDppKeluaran(), report.totalPpnKeluaran(),
                report.totalDppMasukan(), report.totalPpnMasukan());

        return new PPNDetailData(keluaranItems, masukanItems, totals);
    }

    private PPh23DetailData toPPh23DetailData(PPh23DetailReport report) {
        List<TaxDetailItem> items = report.items().stream()
                .map(this::toTaxDetailItem)
                .toList();

        PPh23Totals totals = new PPh23Totals(report.totalGross(), report.totalTax());

        return new PPh23DetailData(items, totals);
    }

    private RekonsiliasiFiskalData toRekonsiliasiFiskalData(RekonsiliasiFiskalReport report) {
        List<FiscalAdjustmentItem> adjustmentItems = report.adjustments().stream()
                .map(a -> new FiscalAdjustmentItem(
                        a.getDescription(),
                        a.getAdjustmentCategory().name(),
                        a.getAdjustmentDirection().name(),
                        a.getAmount()))
                .toList();

        PPhBadanCalculation calc = report.pphBadan();
        PPhBadanData pphBadan = new PPhBadanData(
                calc.pkp(), calc.totalRevenue(), calc.pphTerutang(),
                calc.calculationMethod(),
                calc.kreditPajakPPh23(), calc.kreditPajakPPh25(),
                calc.totalKreditPajak(), calc.pph29());

        return new RekonsiliasiFiskalData(
                report.year(),
                report.commercialNetIncome(),
                report.totalPositiveAdjustment(),
                report.totalNegativeAdjustment(),
                report.netAdjustment(),
                adjustmentItems,
                report.pkp(),
                pphBadan);
    }

    private TaxDetailItem toTaxDetailItem(TaxTransactionDetail d) {
        return new TaxDetailItem(
                d.getTransaction().getTransactionNumber(),
                d.getTransaction().getTransactionDate(),
                d.getCounterpartyName(),
                d.getCounterpartyNpwp(),
                d.getTaxType().name(),
                d.getFakturNumber(),
                d.getTransactionCode(),
                d.getDpp(),
                d.getPpn(),
                d.getBupotNumber(),
                d.getTaxObjectCode(),
                d.getGrossAmount(),
                d.getTaxRate(),
                d.getTaxAmount());
    }

    // ==================== AUDIT ====================

    private void auditAccess(String endpoint, Map<String, String> params) {
        securityAuditService.logAsync(AuditEventType.API_CALL,
                "Tax export API: " + endpoint + " " + params);
    }

    // ==================== DTOs ====================

    public record TaxDetailItem(
            String transactionNumber,
            LocalDate transactionDate,
            String counterpartyName,
            String counterpartyNpwp,
            String taxType,
            String fakturNumber,
            String transactionCode,
            BigDecimal dpp,
            BigDecimal ppn,
            String bupotNumber,
            String taxObjectCode,
            BigDecimal grossAmount,
            BigDecimal taxRate,
            BigDecimal taxAmount
    ) {}

    public record PPNTotals(
            BigDecimal totalDppKeluaran,
            BigDecimal totalPpnKeluaran,
            BigDecimal totalDppMasukan,
            BigDecimal totalPpnMasukan
    ) {}

    public record PPNDetailData(
            List<TaxDetailItem> keluaranItems,
            List<TaxDetailItem> masukanItems,
            PPNTotals totals
    ) {}

    public record PPh23Totals(
            BigDecimal totalGross,
            BigDecimal totalTax
    ) {}

    public record PPh23DetailData(
            List<TaxDetailItem> items,
            PPh23Totals totals
    ) {}

    public record FiscalAdjustmentItem(
            String description,
            String category,
            String direction,
            BigDecimal amount
    ) {}

    public record RekonsiliasiFiskalData(
            int year,
            BigDecimal commercialNetIncome,
            BigDecimal totalPositiveAdjustment,
            BigDecimal totalNegativeAdjustment,
            BigDecimal netAdjustment,
            List<FiscalAdjustmentItem> adjustments,
            BigDecimal pkp,
            PPhBadanData pphBadan
    ) {}

    public record PPhBadanData(
            BigDecimal pkp,
            BigDecimal totalRevenue,
            BigDecimal pphTerutang,
            String calculationMethod,
            BigDecimal kreditPajakPPh23,
            BigDecimal kreditPajakPPh25,
            BigDecimal totalKreditPajak,
            BigDecimal pph29
    ) {}
}
