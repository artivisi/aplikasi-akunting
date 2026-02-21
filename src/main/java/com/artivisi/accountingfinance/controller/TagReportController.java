package com.artivisi.accountingfinance.controller;

import com.artivisi.accountingfinance.security.Permission;
import com.artivisi.accountingfinance.service.TagReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

import static com.artivisi.accountingfinance.controller.ViewConstants.*;

@Controller
@RequestMapping("/reports/tag-summary")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('" + Permission.REPORT_VIEW + "') and hasAuthority('" + Permission.TAG_VIEW + "')")
public class TagReportController {

    private final TagReportService tagReportService;

    @GetMapping
    public String tagSummary(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            Model model) {

        model.addAttribute(ATTR_CURRENT_PAGE, PAGE_REPORTS);

        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        List<TagReportService.TagTypeSummary> summaries = tagReportService.generateReport(startDate, endDate);
        model.addAttribute("summaries", summaries);

        return "reports/tag-summary";
    }
}
