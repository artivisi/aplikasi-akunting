package com.artivisi.accountingfinance.controller;

import com.artivisi.accountingfinance.security.Permission;
import com.artivisi.accountingfinance.service.InventoryReportService;
import com.artivisi.accountingfinance.service.ProductCategoryService;
import com.artivisi.accountingfinance.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.UUID;

@Controller
@RequestMapping("/inventory/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('" + Permission.INVENTORY_VIEW + "')")
public class InventoryReportController {

    private final InventoryReportService reportService;
    private final ProductCategoryService categoryService;
    private final ProductService productService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("currentPage", "inventory-reports");
        return "inventory/reports/index";
    }

    @GetMapping("/stock-balance")
    public String stockBalance(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) String search,
            Model model) {

        model.addAttribute("currentPage", "inventory-reports");
        model.addAttribute("reportType", "stock-balance");
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("search", search);
        model.addAttribute("categories", categoryService.findAllActive());
        model.addAttribute("report", reportService.generateStockBalanceReport(categoryId, search));
        model.addAttribute("asOfDate", LocalDate.now());

        return "inventory/reports/stock-balance";
    }

    @GetMapping("/stock-balance/print")
    public String stockBalancePrint(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) String search,
            Model model) {

        model.addAttribute("categoryId", categoryId);
        model.addAttribute("search", search);
        model.addAttribute("report", reportService.generateStockBalanceReport(categoryId, search));
        model.addAttribute("asOfDate", LocalDate.now());

        return "inventory/reports/stock-balance-print";
    }

    @GetMapping("/stock-movement")
    public String stockMovement(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID productId,
            Model model) {

        LocalDate start = startDate != null ? startDate : LocalDate.now().withDayOfMonth(1);
        LocalDate end = endDate != null ? endDate : LocalDate.now();

        model.addAttribute("currentPage", "inventory-reports");
        model.addAttribute("reportType", "stock-movement");
        model.addAttribute("startDate", start);
        model.addAttribute("endDate", end);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("productId", productId);
        model.addAttribute("categories", categoryService.findAllActive());
        model.addAttribute("products", productService.findAllActive());
        model.addAttribute("report", reportService.generateStockMovementReport(start, end, categoryId, productId));

        return "inventory/reports/stock-movement";
    }

    @GetMapping("/stock-movement/print")
    public String stockMovementPrint(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID productId,
            Model model) {

        LocalDate start = startDate != null ? startDate : LocalDate.now().withDayOfMonth(1);
        LocalDate end = endDate != null ? endDate : LocalDate.now();

        model.addAttribute("startDate", start);
        model.addAttribute("endDate", end);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("productId", productId);
        model.addAttribute("report", reportService.generateStockMovementReport(start, end, categoryId, productId));

        return "inventory/reports/stock-movement-print";
    }

    @GetMapping("/valuation")
    public String inventoryValuation(
            @RequestParam(required = false) UUID categoryId,
            Model model) {

        model.addAttribute("currentPage", "inventory-reports");
        model.addAttribute("reportType", "valuation");
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("categories", categoryService.findAllActive());
        model.addAttribute("report", reportService.generateValuationReport(categoryId));
        model.addAttribute("asOfDate", LocalDate.now());

        return "inventory/reports/valuation";
    }

    @GetMapping("/valuation/print")
    public String inventoryValuationPrint(
            @RequestParam(required = false) UUID categoryId,
            Model model) {

        model.addAttribute("categoryId", categoryId);
        model.addAttribute("report", reportService.generateValuationReport(categoryId));
        model.addAttribute("asOfDate", LocalDate.now());

        return "inventory/reports/valuation-print";
    }
}
