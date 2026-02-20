package com.artivisi.accountingfinance.controller;

import com.artivisi.accountingfinance.entity.BankReconciliation;
import com.artivisi.accountingfinance.entity.BankStatement;
import com.artivisi.accountingfinance.entity.BankStatementItem;
import com.artivisi.accountingfinance.entity.BankStatementParserConfig;
import com.artivisi.accountingfinance.entity.CompanyBankAccount;
import com.artivisi.accountingfinance.entity.ReconciliationItem;
import com.artivisi.accountingfinance.enums.AuditEventType;
import com.artivisi.accountingfinance.enums.BankStatementParserType;
import com.artivisi.accountingfinance.enums.ReconciliationStatus;
import com.artivisi.accountingfinance.enums.StatementItemMatchStatus;
import com.artivisi.accountingfinance.repository.BankStatementItemRepository;
import com.artivisi.accountingfinance.security.Permission;
import com.artivisi.accountingfinance.service.BankReconciliationReportService;
import com.artivisi.accountingfinance.service.BankReconciliationService;
import com.artivisi.accountingfinance.service.BankStatementImportService;
import com.artivisi.accountingfinance.service.BankStatementParserConfigService;
import com.artivisi.accountingfinance.service.CompanyBankAccountService;
import com.artivisi.accountingfinance.service.SecurityAuditService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.artivisi.accountingfinance.controller.ViewConstants.*;

@Controller
@RequestMapping("/bank-reconciliation")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('" + Permission.BANK_RECONCILIATION_VIEW + "')")
public class BankReconciliationController {

    private static final String ATTR_SUCCESS = "successMessage";
    private static final String ATTR_ERROR = "errorMessage";

    private final BankStatementParserConfigService parserConfigService;
    private final BankStatementImportService importService;
    private final BankReconciliationService reconciliationService;
    private final BankReconciliationReportService reportService;
    private final CompanyBankAccountService bankAccountService;
    private final BankStatementItemRepository statementItemRepository;
    private final SecurityAuditService securityAuditService;

    // ==================== Landing Page ====================

    @GetMapping
    public String index(Model model) {
        model.addAttribute(ATTR_CURRENT_PAGE, PAGE_BANK_RECONCILIATION);
        model.addAttribute("recentStatements", importService.findAllStatements());
        model.addAttribute("recentReconciliations", reconciliationService.findAll());
        return "bank-reconciliation/index";
    }

    // ==================== Parser Configs ====================

    @GetMapping("/parser-configs")
    public String parserConfigList(Model model) {
        model.addAttribute(ATTR_CURRENT_PAGE, PAGE_BANK_RECON_PARSER_CONFIGS);
        model.addAttribute("configs", parserConfigService.findAll());
        return "bank-reconciliation/parser-configs/list";
    }

    @GetMapping("/parser-configs/new")
    @PreAuthorize("hasAuthority('" + Permission.BANK_RECONCILIATION_CONFIG + "')")
    public String newParserConfigForm(Model model) {
        model.addAttribute(ATTR_CURRENT_PAGE, PAGE_BANK_RECON_PARSER_CONFIGS);
        model.addAttribute("config", new BankStatementParserConfig());
        model.addAttribute("bankTypes", BankStatementParserType.values());
        model.addAttribute(ATTR_IS_EDIT, false);
        return "bank-reconciliation/parser-configs/form";
    }

    @PostMapping("/parser-configs/new")
    @PreAuthorize("hasAuthority('" + Permission.BANK_RECONCILIATION_CONFIG + "')")
    public String createParserConfig(
            @Valid @ModelAttribute("config") BankStatementParserConfig config,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute(ATTR_CURRENT_PAGE, PAGE_BANK_RECON_PARSER_CONFIGS);
            model.addAttribute("bankTypes", BankStatementParserType.values());
            model.addAttribute(ATTR_IS_EDIT, false);
            return "bank-reconciliation/parser-configs/form";
        }

        try {
            parserConfigService.create(config);
            securityAuditService.log(AuditEventType.SETTINGS_CHANGE,
                    "Bank parser config created: " + config.getConfigName());
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS, "Konfigurasi parser berhasil dibuat");
            return REDIRECT_BANK_RECON_PARSER_CONFIGS;
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("configName", "duplicate", e.getMessage());
            model.addAttribute(ATTR_CURRENT_PAGE, PAGE_BANK_RECON_PARSER_CONFIGS);
            model.addAttribute("bankTypes", BankStatementParserType.values());
            model.addAttribute(ATTR_IS_EDIT, false);
            return "bank-reconciliation/parser-configs/form";
        }
    }

    @GetMapping("/parser-configs/{id}/edit")
    @PreAuthorize("hasAuthority('" + Permission.BANK_RECONCILIATION_CONFIG + "')")
    public String editParserConfigForm(@PathVariable UUID id, Model model) {
        model.addAttribute(ATTR_CURRENT_PAGE, PAGE_BANK_RECON_PARSER_CONFIGS);
        model.addAttribute("config", parserConfigService.findById(id));
        model.addAttribute("bankTypes", BankStatementParserType.values());
        model.addAttribute(ATTR_IS_EDIT, true);
        return "bank-reconciliation/parser-configs/form";
    }

    @PostMapping("/parser-configs/{id}")
    @PreAuthorize("hasAuthority('" + Permission.BANK_RECONCILIATION_CONFIG + "')")
    public String updateParserConfig(
            @PathVariable UUID id,
            @Valid @ModelAttribute("config") BankStatementParserConfig config,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            config.setId(id);
            model.addAttribute(ATTR_CURRENT_PAGE, PAGE_BANK_RECON_PARSER_CONFIGS);
            model.addAttribute("bankTypes", BankStatementParserType.values());
            model.addAttribute(ATTR_IS_EDIT, true);
            return "bank-reconciliation/parser-configs/form";
        }

        try {
            parserConfigService.update(id, config);
            securityAuditService.log(AuditEventType.SETTINGS_CHANGE,
                    "Bank parser config updated: " + config.getConfigName());
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS, "Konfigurasi parser berhasil diperbarui");
            return REDIRECT_BANK_RECON_PARSER_CONFIGS;
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("configName", "duplicate", e.getMessage());
            config.setId(id);
            model.addAttribute(ATTR_CURRENT_PAGE, PAGE_BANK_RECON_PARSER_CONFIGS);
            model.addAttribute("bankTypes", BankStatementParserType.values());
            model.addAttribute(ATTR_IS_EDIT, true);
            return "bank-reconciliation/parser-configs/form";
        }
    }

    @PostMapping("/parser-configs/{id}/deactivate")
    @PreAuthorize("hasAuthority('" + Permission.BANK_RECONCILIATION_CONFIG + "')")
    public String deactivateParserConfig(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        parserConfigService.deactivate(id);
        redirectAttributes.addFlashAttribute(ATTR_SUCCESS, "Konfigurasi parser berhasil dinonaktifkan");
        return REDIRECT_BANK_RECON_PARSER_CONFIGS;
    }

    @PostMapping("/parser-configs/{id}/activate")
    @PreAuthorize("hasAuthority('" + Permission.BANK_RECONCILIATION_CONFIG + "')")
    public String activateParserConfig(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        parserConfigService.activate(id);
        redirectAttributes.addFlashAttribute(ATTR_SUCCESS, "Konfigurasi parser berhasil diaktifkan");
        return REDIRECT_BANK_RECON_PARSER_CONFIGS;
    }

    @PostMapping("/parser-configs/{id}/delete")
    @PreAuthorize("hasAuthority('" + Permission.BANK_RECONCILIATION_CONFIG + "')")
    public String deleteParserConfig(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            parserConfigService.delete(id);
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS, "Konfigurasi parser berhasil dihapus");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(ATTR_ERROR, e.getMessage());
        }
        return REDIRECT_BANK_RECON_PARSER_CONFIGS;
    }

    // ==================== Statement Import ====================

    @GetMapping("/import")
    @PreAuthorize("hasAuthority('" + Permission.BANK_RECONCILIATION_IMPORT + "')")
    public String importForm(Model model) {
        model.addAttribute(ATTR_CURRENT_PAGE, PAGE_BANK_RECON_STATEMENTS);
        model.addAttribute("bankAccounts", bankAccountService.findActive());
        model.addAttribute("parserConfigs", parserConfigService.findActive());
        return "bank-reconciliation/import";
    }

    @PostMapping("/import")
    @PreAuthorize("hasAuthority('" + Permission.BANK_RECONCILIATION_IMPORT + "')")
    public String importStatement(
            @RequestParam("bankAccountId") UUID bankAccountId,
            @RequestParam("parserConfigId") UUID parserConfigId,
            @RequestParam("periodStart") LocalDate periodStart,
            @RequestParam("periodEnd") LocalDate periodEnd,
            @RequestParam(value = "openingBalance", required = false) java.math.BigDecimal openingBalance,
            @RequestParam(value = "closingBalance", required = false) java.math.BigDecimal closingBalance,
            @RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes) {

        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            BankStatement statement = importService.importStatement(
                    bankAccountId, parserConfigId, periodStart, periodEnd,
                    openingBalance, closingBalance, file, username);

            securityAuditService.log(AuditEventType.DATA_IMPORT,
                    "Bank statement imported: " + file.getOriginalFilename()
                            + " (" + statement.getTotalItems() + " items)");

            redirectAttributes.addFlashAttribute(ATTR_SUCCESS,
                    "Berhasil mengimpor " + statement.getTotalItems() + " transaksi dari " + file.getOriginalFilename());
            return "redirect:/bank-reconciliation/statements/" + statement.getId();
        } catch (Exception e) {
            log.error("Failed to import bank statement", e);
            redirectAttributes.addFlashAttribute(ATTR_ERROR, "Gagal mengimpor: " + e.getMessage());
            return "redirect:/bank-reconciliation/import";
        }
    }

    @GetMapping("/statements")
    public String statementList(Model model) {
        model.addAttribute(ATTR_CURRENT_PAGE, PAGE_BANK_RECON_STATEMENTS);
        model.addAttribute("statements", importService.findAllStatements());
        return "bank-reconciliation/statements/list";
    }

    @GetMapping("/statements/{id}")
    public String statementDetail(@PathVariable UUID id,
                                  @RequestParam(value = "matchStatus", required = false) StatementItemMatchStatus matchStatus,
                                  Model model) {
        BankStatement statement = importService.findStatementById(id);
        List<BankStatementItem> items;
        if (matchStatus != null) {
            items = statementItemRepository.findByBankStatementIdAndMatchStatusOrderByLineNumberAsc(id, matchStatus);
        } else {
            items = statementItemRepository.findByBankStatementIdWithTransaction(id);
        }

        model.addAttribute(ATTR_CURRENT_PAGE, PAGE_BANK_RECON_STATEMENTS);
        model.addAttribute("statement", statement);
        model.addAttribute("items", items);
        model.addAttribute("matchStatuses", StatementItemMatchStatus.values());
        model.addAttribute("selectedMatchStatus", matchStatus);
        return "bank-reconciliation/statements/detail";
    }

    // ==================== Reconciliations ====================

    @GetMapping("/reconciliations")
    public String reconciliationList(Model model) {
        model.addAttribute(ATTR_CURRENT_PAGE, PAGE_BANK_RECON_RECONCILIATIONS);
        model.addAttribute("reconciliations", reconciliationService.findAll());
        return "bank-reconciliation/reconciliations/list";
    }

    @GetMapping("/reconciliations/new")
    @PreAuthorize("hasAuthority('" + Permission.BANK_RECONCILIATION_MATCH + "')")
    public String newReconciliationForm(Model model) {
        model.addAttribute(ATTR_CURRENT_PAGE, PAGE_BANK_RECON_RECONCILIATIONS);
        model.addAttribute("bankAccounts", bankAccountService.findActive());
        model.addAttribute("statements", importService.findAllStatements());
        return "bank-reconciliation/reconciliations/form";
    }

    @PostMapping("/reconciliations/new")
    @PreAuthorize("hasAuthority('" + Permission.BANK_RECONCILIATION_MATCH + "')")
    public String createReconciliation(
            @RequestParam("bankStatementId") UUID bankStatementId,
            @RequestParam(value = "notes", required = false) String notes,
            RedirectAttributes redirectAttributes) {

        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            BankReconciliation recon = reconciliationService.create(bankStatementId, notes, username);
            securityAuditService.log(AuditEventType.SETTINGS_CHANGE,
                    "Bank reconciliation created for statement: " + bankStatementId);
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS, "Rekonsiliasi berhasil dibuat");
            return "redirect:/bank-reconciliation/reconciliations/" + recon.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ATTR_ERROR, "Gagal membuat rekonsiliasi: " + e.getMessage());
            return "redirect:/bank-reconciliation/reconciliations/new";
        }
    }

    @GetMapping("/reconciliations/{id}")
    public String reconciliationDetail(@PathVariable UUID id,
                                       @RequestHeader(value = "HX-Request", required = false) String hxRequest,
                                       Model model) {
        BankReconciliation recon = reconciliationService.findById(id);
        List<BankStatementItem> bankItems = statementItemRepository
                .findByBankStatementIdWithTransaction(recon.getBankStatement().getId());
        List<ReconciliationItem> reconItems = reconciliationService.findReconciliationItems(id);

        // Separate bank items by match status
        List<BankStatementItem> unmatchedBankItems = bankItems.stream()
                .filter(i -> i.getMatchStatus() == StatementItemMatchStatus.UNMATCHED)
                .toList();
        List<BankStatementItem> matchedBankItems = bankItems.stream()
                .filter(i -> i.getMatchStatus() == StatementItemMatchStatus.MATCHED)
                .toList();

        // Get unmatched book transactions
        var unmatchedBookTxns = reconciliationService.findUnmatchedBookTransactions(id);

        model.addAttribute(ATTR_CURRENT_PAGE, PAGE_BANK_RECON_RECONCILIATIONS);
        model.addAttribute("recon", recon);
        model.addAttribute("bankItems", bankItems);
        model.addAttribute("unmatchedBankItems", unmatchedBankItems);
        model.addAttribute("matchedBankItems", matchedBankItems);
        model.addAttribute("reconItems", reconItems);
        model.addAttribute("unmatchedBookTxns", unmatchedBookTxns);
        model.addAttribute("templates", reconciliationService.findJournalTemplates());

        if ("true".equals(hxRequest)) {
            return "bank-reconciliation/reconciliations/detail :: reconContent";
        }
        return "bank-reconciliation/reconciliations/detail";
    }

    @PostMapping("/reconciliations/{id}/auto-match")
    @PreAuthorize("hasAuthority('" + Permission.BANK_RECONCILIATION_MATCH + "')")
    public String autoMatch(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            int matchCount = reconciliationService.autoMatch(id, username);
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS,
                    "Auto-match selesai: " + matchCount + " transaksi berhasil dicocokkan");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ATTR_ERROR, "Gagal auto-match: " + e.getMessage());
        }
        return "redirect:/bank-reconciliation/reconciliations/" + id;
    }

    @PostMapping("/reconciliations/{id}/match")
    @PreAuthorize("hasAuthority('" + Permission.BANK_RECONCILIATION_MATCH + "')")
    public String manualMatch(
            @PathVariable UUID id,
            @RequestParam("statementItemId") UUID statementItemId,
            @RequestParam("transactionId") UUID transactionId,
            RedirectAttributes redirectAttributes) {

        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            reconciliationService.manualMatch(id, statementItemId, transactionId, username);
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS, "Transaksi berhasil dicocokkan");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ATTR_ERROR, "Gagal mencocokkan: " + e.getMessage());
        }
        return "redirect:/bank-reconciliation/reconciliations/" + id;
    }

    @PostMapping("/reconciliations/{id}/mark-bank-only")
    @PreAuthorize("hasAuthority('" + Permission.BANK_RECONCILIATION_MATCH + "')")
    public String markBankOnly(
            @PathVariable UUID id,
            @RequestParam("statementItemId") UUID statementItemId,
            @RequestParam(value = "notes", required = false) String notes,
            RedirectAttributes redirectAttributes) {

        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            reconciliationService.markBankOnly(id, statementItemId, notes, username);
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS, "Item ditandai sebagai hanya di bank");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ATTR_ERROR, e.getMessage());
        }
        return "redirect:/bank-reconciliation/reconciliations/" + id;
    }

    @PostMapping("/reconciliations/{id}/mark-book-only")
    @PreAuthorize("hasAuthority('" + Permission.BANK_RECONCILIATION_MATCH + "')")
    public String markBookOnly(
            @PathVariable UUID id,
            @RequestParam("transactionId") UUID transactionId,
            @RequestParam(value = "notes", required = false) String notes,
            RedirectAttributes redirectAttributes) {

        try {
            reconciliationService.markBookOnly(id, transactionId, notes);
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS, "Transaksi ditandai sebagai hanya di buku");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ATTR_ERROR, e.getMessage());
        }
        return "redirect:/bank-reconciliation/reconciliations/" + id;
    }

    @PostMapping("/reconciliations/{id}/unmatch/{itemId}")
    @PreAuthorize("hasAuthority('" + Permission.BANK_RECONCILIATION_MATCH + "')")
    public String unmatch(
            @PathVariable UUID id,
            @PathVariable UUID itemId,
            RedirectAttributes redirectAttributes) {

        try {
            reconciliationService.unmatch(id, itemId);
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS, "Pencocokan berhasil dibatalkan");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ATTR_ERROR, e.getMessage());
        }
        return "redirect:/bank-reconciliation/reconciliations/" + id;
    }

    @PostMapping("/reconciliations/{id}/create-transaction")
    @PreAuthorize("hasAuthority('" + Permission.BANK_RECONCILIATION_MATCH + "')")
    public String createTransactionFromItem(
            @PathVariable UUID id,
            @RequestParam("statementItemId") UUID statementItemId,
            @RequestParam("templateId") UUID templateId,
            @RequestParam(value = "description", required = false) String description,
            RedirectAttributes redirectAttributes) {

        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            reconciliationService.createTransactionFromStatementItem(
                    id, statementItemId, templateId, description, username);
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS, "Transaksi berhasil dibuat dan dicocokkan");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ATTR_ERROR, "Gagal membuat transaksi: " + e.getMessage());
        }
        return "redirect:/bank-reconciliation/reconciliations/" + id;
    }

    @PostMapping("/reconciliations/{id}/complete")
    @PreAuthorize("hasAuthority('" + Permission.BANK_RECONCILIATION_COMPLETE + "')")
    public String completeReconciliation(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            reconciliationService.complete(id, username);
            securityAuditService.log(AuditEventType.SETTINGS_CHANGE,
                    "Bank reconciliation completed: " + id);
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS, "Rekonsiliasi berhasil diselesaikan");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ATTR_ERROR, "Gagal menyelesaikan: " + e.getMessage());
        }
        return "redirect:/bank-reconciliation/reconciliations/" + id;
    }

    // ==================== Reports ====================

    @GetMapping("/reconciliations/{id}/report")
    public String reconciliationReport(@PathVariable UUID id, Model model) {
        BankReconciliation recon = reconciliationService.findById(id);
        model.addAttribute(ATTR_CURRENT_PAGE, PAGE_BANK_RECON_RECONCILIATIONS);
        model.addAttribute("recon", recon);
        model.addAttribute("summary", reportService.getSummary(id));
        model.addAttribute("statement", reportService.getReconciliationStatement(id));
        model.addAttribute("outstanding", reportService.getOutstandingItems(id));
        return "bank-reconciliation/reports/report";
    }

    @GetMapping("/reconciliations/{id}/report/print")
    public String reconciliationReportPrint(@PathVariable UUID id, Model model) {
        BankReconciliation recon = reconciliationService.findById(id);
        model.addAttribute("recon", recon);
        model.addAttribute("summary", reportService.getSummary(id));
        model.addAttribute("statement", reportService.getReconciliationStatement(id));
        model.addAttribute("outstanding", reportService.getOutstandingItems(id));
        return "bank-reconciliation/reports/print";
    }
}
