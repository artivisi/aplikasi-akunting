package com.artivisi.accountingfinance.controller;

import com.artivisi.accountingfinance.service.ReportExportService;
import com.artivisi.accountingfinance.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final ReportExportService reportExportService;

    private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    @GetMapping
    public String index(Model model) {
        model.addAttribute("currentPage", "reports");
        return "reports/index";
    }

    @GetMapping("/trial-balance")
    public String trialBalance(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) LocalDate asOfDate,
            Model model) {
        model.addAttribute("currentPage", "reports");
        model.addAttribute("reportType", "trial-balance");
        model.addAttribute("period", period);

        LocalDate reportDate = asOfDate != null ? asOfDate : LocalDate.now();
        model.addAttribute("asOfDate", reportDate);
        model.addAttribute("report", reportService.generateTrialBalance(reportDate));

        return "reports/trial-balance";
    }

    @GetMapping("/income-statement")
    public String incomeStatement(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String compareWith,
            Model model) {
        model.addAttribute("currentPage", "reports");
        model.addAttribute("reportType", "income-statement");
        model.addAttribute("compareWith", compareWith);

        LocalDate start = startDate != null ? startDate : LocalDate.now().withDayOfMonth(1);
        LocalDate end = endDate != null ? endDate : LocalDate.now();

        model.addAttribute("startDate", start);
        model.addAttribute("endDate", end);
        model.addAttribute("report", reportService.generateIncomeStatement(start, end));

        return "reports/income-statement";
    }

    @GetMapping("/balance-sheet")
    public String balanceSheet(
            @RequestParam(required = false) LocalDate asOfDate,
            @RequestParam(required = false) String compareWith,
            Model model) {
        model.addAttribute("currentPage", "reports");
        model.addAttribute("reportType", "balance-sheet");
        model.addAttribute("compareWith", compareWith);

        LocalDate reportDate = asOfDate != null ? asOfDate : LocalDate.now();
        model.addAttribute("asOfDate", reportDate);
        model.addAttribute("report", reportService.generateBalanceSheet(reportDate));

        return "reports/balance-sheet";
    }

    @GetMapping("/cash-flow")
    public String cashFlow(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            Model model) {
        model.addAttribute("currentPage", "reports");
        model.addAttribute("reportType", "cash-flow");

        LocalDate start = startDate != null ? startDate : LocalDate.now().withDayOfMonth(1);
        LocalDate end = endDate != null ? endDate : LocalDate.now();

        model.addAttribute("startDate", start);
        model.addAttribute("endDate", end);

        return "reports/cash-flow";
    }

    // REST API Endpoints

    @GetMapping("/api/trial-balance")
    @ResponseBody
    public ResponseEntity<ReportService.TrialBalanceReport> apiTrialBalance(
            @RequestParam(required = false) LocalDate asOfDate) {
        LocalDate reportDate = asOfDate != null ? asOfDate : LocalDate.now();
        return ResponseEntity.ok(reportService.generateTrialBalance(reportDate));
    }

    @GetMapping("/api/income-statement")
    @ResponseBody
    public ResponseEntity<ReportService.IncomeStatementReport> apiIncomeStatement(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        return ResponseEntity.ok(reportService.generateIncomeStatement(startDate, endDate));
    }

    @GetMapping("/api/balance-sheet")
    @ResponseBody
    public ResponseEntity<ReportService.BalanceSheetReport> apiBalanceSheet(
            @RequestParam(required = false) LocalDate asOfDate) {
        LocalDate reportDate = asOfDate != null ? asOfDate : LocalDate.now();
        return ResponseEntity.ok(reportService.generateBalanceSheet(reportDate));
    }

    // ==================== EXPORT ENDPOINTS ====================

    // Trial Balance Exports
    @GetMapping("/trial-balance/export/pdf")
    public ResponseEntity<byte[]> exportTrialBalanceToPdf(
            @RequestParam(required = false) LocalDate asOfDate) {
        LocalDate reportDate = asOfDate != null ? asOfDate : LocalDate.now();
        ReportService.TrialBalanceReport report = reportService.generateTrialBalance(reportDate);
        byte[] pdfBytes = reportExportService.exportTrialBalanceToPdf(report);

        String filename = "neraca-saldo-" + reportDate.format(FILE_DATE_FORMAT) + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/trial-balance/export/excel")
    public ResponseEntity<byte[]> exportTrialBalanceToExcel(
            @RequestParam(required = false) LocalDate asOfDate) {
        LocalDate reportDate = asOfDate != null ? asOfDate : LocalDate.now();
        ReportService.TrialBalanceReport report = reportService.generateTrialBalance(reportDate);
        byte[] excelBytes = reportExportService.exportTrialBalanceToExcel(report);

        String filename = "neraca-saldo-" + reportDate.format(FILE_DATE_FORMAT) + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }

    // Balance Sheet Exports
    @GetMapping("/balance-sheet/export/pdf")
    public ResponseEntity<byte[]> exportBalanceSheetToPdf(
            @RequestParam(required = false) LocalDate asOfDate) {
        LocalDate reportDate = asOfDate != null ? asOfDate : LocalDate.now();
        ReportService.BalanceSheetReport report = reportService.generateBalanceSheet(reportDate);
        byte[] pdfBytes = reportExportService.exportBalanceSheetToPdf(report);

        String filename = "laporan-posisi-keuangan-" + reportDate.format(FILE_DATE_FORMAT) + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/balance-sheet/export/excel")
    public ResponseEntity<byte[]> exportBalanceSheetToExcel(
            @RequestParam(required = false) LocalDate asOfDate) {
        LocalDate reportDate = asOfDate != null ? asOfDate : LocalDate.now();
        ReportService.BalanceSheetReport report = reportService.generateBalanceSheet(reportDate);
        byte[] excelBytes = reportExportService.exportBalanceSheetToExcel(report);

        String filename = "laporan-posisi-keuangan-" + reportDate.format(FILE_DATE_FORMAT) + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }

    // Income Statement Exports
    @GetMapping("/income-statement/export/pdf")
    public ResponseEntity<byte[]> exportIncomeStatementToPdf(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        LocalDate start = startDate != null ? startDate : LocalDate.now().withDayOfMonth(1);
        LocalDate end = endDate != null ? endDate : LocalDate.now();
        ReportService.IncomeStatementReport report = reportService.generateIncomeStatement(start, end);
        byte[] pdfBytes = reportExportService.exportIncomeStatementToPdf(report);

        String filename = "laporan-laba-rugi-" + start.format(FILE_DATE_FORMAT) + "-" + end.format(FILE_DATE_FORMAT) + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/income-statement/export/excel")
    public ResponseEntity<byte[]> exportIncomeStatementToExcel(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        LocalDate start = startDate != null ? startDate : LocalDate.now().withDayOfMonth(1);
        LocalDate end = endDate != null ? endDate : LocalDate.now();
        ReportService.IncomeStatementReport report = reportService.generateIncomeStatement(start, end);
        byte[] excelBytes = reportExportService.exportIncomeStatementToExcel(report);

        String filename = "laporan-laba-rugi-" + start.format(FILE_DATE_FORMAT) + "-" + end.format(FILE_DATE_FORMAT) + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }
}
