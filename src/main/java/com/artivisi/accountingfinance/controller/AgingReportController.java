package com.artivisi.accountingfinance.controller;

import com.artivisi.accountingfinance.dto.AgingReport;
import com.artivisi.accountingfinance.service.AgingReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

import static com.artivisi.accountingfinance.controller.ViewConstants.ATTR_CURRENT_PAGE;

@Controller
@RequiredArgsConstructor
public class AgingReportController {

    private final AgingReportService agingReportService;

    @GetMapping("/reports/aging/receivables")
    @PreAuthorize("hasAuthority('REPORT_VIEW')")
    public String receivablesAging(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate,
            Model model) {

        LocalDate effectiveDate = asOfDate != null ? asOfDate : LocalDate.now();
        AgingReport report = agingReportService.generateReceivablesAging(effectiveDate);

        model.addAttribute(ATTR_CURRENT_PAGE, ViewConstants.PAGE_AGING_RECEIVABLES);
        model.addAttribute("report", report);
        model.addAttribute("asOfDate", effectiveDate);

        return "reports/aging-receivables";
    }

    @GetMapping("/reports/aging/payables")
    @PreAuthorize("hasAuthority('REPORT_VIEW')")
    public String payablesAging(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOfDate,
            Model model) {

        LocalDate effectiveDate = asOfDate != null ? asOfDate : LocalDate.now();
        AgingReport report = agingReportService.generatePayablesAging(effectiveDate);

        model.addAttribute(ATTR_CURRENT_PAGE, ViewConstants.PAGE_AGING_PAYABLES);
        model.addAttribute("report", report);
        model.addAttribute("asOfDate", effectiveDate);

        return "reports/aging-payables";
    }
}
