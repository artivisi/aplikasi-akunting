package com.artivisi.accountingfinance.functional.bankrecon;

import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@DisplayName("Bank Reconciliation")
@Import(BankReconTestDataInitializer.class)
class BankReconciliationTest extends PlaywrightTestBase {

    // ==================== Landing Page ====================

    @Test
    @DisplayName("Should display bank reconciliation landing page")
    void shouldDisplayLandingPage() {
        loginAsAdmin();
        navigateTo("/bank-reconciliation");
        waitForPageLoad();

        assertThat(page.locator("#page-title")).containsText("Rekonsiliasi Bank");
        assertThat(page.locator("[data-testid='btn-import-statement']")).isVisible();
        assertThat(page.locator("[data-testid='btn-new-reconciliation']")).isVisible();
        assertThat(page.locator("[data-testid='btn-parser-configs']")).isVisible();

        takeManualScreenshot("bank-recon/landing-page");
    }

    @Test
    @DisplayName("Should display recent statements on landing page")
    void shouldDisplayRecentStatements() {
        loginAsAdmin();
        navigateTo("/bank-reconciliation");
        waitForPageLoad();

        assertThat(page.locator("[data-testid='recent-statements-table']")).isVisible();
        assertThat(page.locator("[data-testid='recent-statements-table']")).containsText("BCA");
    }

    // ==================== Navigation ====================

    @Test
    @DisplayName("Should navigate via sidebar")
    void shouldNavigateViaSidebar() {
        loginAsAdmin();
        navigateTo("/dashboard");
        waitForPageLoad();

        page.locator("#nav-bank-recon").click();
        waitForPageLoad();

        assertThat(page.locator("#page-title")).containsText("Rekonsiliasi Bank");
    }

    // ==================== Parser Configs ====================

    @Test
    @DisplayName("Should display preloaded parser configs")
    void shouldDisplayParserConfigs() {
        loginAsAdmin();
        navigateTo("/bank-reconciliation/parser-configs");
        waitForPageLoad();

        assertThat(page.locator("#page-title")).containsText("Konfigurasi Parser Bank");
        assertThat(page.locator("[data-testid='parser-config-table']")).isVisible();
        assertThat(page.locator("[data-testid='parser-config-table']")).containsText("BCA");
        assertThat(page.locator("[data-testid='parser-config-table']")).containsText("Mandiri");
        assertThat(page.locator("[data-testid='parser-config-table']")).containsText("BNI");

        takeManualScreenshot("bank-recon/parser-configs");
    }

    @Test
    @DisplayName("Should create custom parser config")
    void shouldCreateCustomParserConfig() {
        loginAsAdmin();
        navigateTo("/bank-reconciliation/parser-configs/new");
        waitForPageLoad();

        page.locator("[data-testid='select-bank-type']").selectOption("CUSTOM");
        page.locator("[data-testid='input-config-name']").fill("Test Custom Parser");
        page.locator("[data-testid='input-date-column']").fill("0");
        page.locator("[data-testid='input-desc-column']").fill("1");
        page.locator("[data-testid='input-date-format']").fill("dd/MM/yyyy");

        takeManualScreenshot("bank-recon/parser-config-form");

        page.locator("[data-testid='btn-save-config']").click();
        waitForPageLoad();

        assertThat(page.locator("[data-testid='parser-config-table']")).containsText("Test Custom Parser");
    }

    // ==================== Statement Import ====================

    @Test
    @DisplayName("Should display import form with bank accounts and parser configs")
    void shouldDisplayImportForm() {
        loginAsAdmin();
        navigateTo("/bank-reconciliation/import");
        waitForPageLoad();

        assertThat(page.locator("#page-title")).containsText("Import Mutasi Bank");
        assertThat(page.locator("[data-testid='select-bank-account']")).isVisible();
        assertThat(page.locator("[data-testid='select-parser-config']")).isVisible();
        assertThat(page.locator("[data-testid='input-period-start']")).isVisible();
        assertThat(page.locator("[data-testid='input-period-end']")).isVisible();
        assertThat(page.locator("[data-testid='input-file']")).isVisible();
        assertThat(page.locator("[data-testid='btn-import']")).isVisible();

        takeManualScreenshot("bank-recon/import-form");
    }

    @Test
    @DisplayName("Should display statements list with imported statement")
    void shouldDisplayStatementsList() {
        loginAsAdmin();
        navigateTo("/bank-reconciliation/statements");
        waitForPageLoad();

        assertThat(page.locator("#page-title")).containsText("Daftar Mutasi Bank");
        assertThat(page.locator("[data-testid='statements-table']")).isVisible();
        assertThat(page.locator("[data-testid='statements-table']")).containsText("BCA");

        takeManualScreenshot("bank-recon/statements-list");
    }

    @Test
    @DisplayName("Should display statement detail with parsed items")
    void shouldDisplayStatementDetail() {
        loginAsAdmin();
        navigateTo("/bank-reconciliation/statements");
        waitForPageLoad();

        page.locator("[data-testid='statements-table'] tbody tr a").first().click();
        waitForPageLoad();

        assertThat(page.locator("#page-title")).containsText("Detail Mutasi Bank");
        assertThat(page.locator("[data-testid='stmt-bank']")).containsText("BCA");
        assertThat(page.locator("[data-testid='stmt-total-items']")).containsText("5");
        assertThat(page.locator("[data-testid='statement-items-table']")).isVisible();

        takeManualScreenshot("bank-recon/statement-detail");
    }

    // ==================== Reconciliation ====================

    @Test
    @DisplayName("Should display reconciliation create form with statement dropdown")
    void shouldDisplayReconciliationForm() {
        loginAsAdmin();
        navigateTo("/bank-reconciliation/reconciliations/new");
        waitForPageLoad();

        assertThat(page.locator("#page-title")).containsText("Rekonsiliasi Baru");
        assertThat(page.locator("[data-testid='select-statement']")).isVisible();
        assertThat(page.locator("[data-testid='btn-create-recon']")).isVisible();

        takeManualScreenshot("bank-recon/recon-form");
    }

    @Test
    @DisplayName("Should create reconciliation and show detail")
    void shouldCreateReconciliation() {
        loginAsAdmin();
        createReconciliationViaUI();

        assertThat(page.locator("[data-testid='recon-status']")).isVisible();
        assertThat(page.locator("[data-testid='recon-bank-balance']")).isVisible();
        assertThat(page.locator("[data-testid='recon-book-balance']")).isVisible();
        assertThat(page.locator("[data-testid='recon-matched-count']")).isVisible();
        assertThat(page.locator("[data-testid='recon-difference']")).isVisible();

        takeManualScreenshot("bank-recon/recon-detail");
    }

    @Test
    @DisplayName("Should display reconciliation detail with action buttons")
    void shouldDisplayReconciliationDetailWithActions() {
        loginAsAdmin();
        createReconciliationViaUI();

        assertThat(page.locator("[data-testid='recon-status']")).isVisible();
        assertThat(page.locator("[data-testid='btn-auto-match']")).isVisible();
        assertThat(page.locator("[data-testid='unmatched-bank-items']")).isVisible();
    }

    @Test
    @DisplayName("Should run auto-match on reconciliation")
    void shouldRunAutoMatch() {
        loginAsAdmin();
        createReconciliationViaUI();

        page.locator("[data-testid='btn-auto-match']").click();
        waitForPageLoad();

        assertThat(page.locator("[data-testid='recon-matched-count']")).isVisible();
        assertThat(page.locator("[data-testid='recon-status']")).isVisible();

        takeManualScreenshot("bank-recon/recon-auto-match");
    }

    @Test
    @DisplayName("Should display reconciliation report")
    void shouldDisplayReconciliationReport() {
        loginAsAdmin();
        createReconciliationViaUI();

        // Click the "Laporan" link in the detail page (not the sidebar)
        page.locator("a[href*='reconciliations'][href*='report']").click();
        waitForPageLoad();

        assertThat(page.locator("[data-testid='reconciliation-statement']")).isVisible();
        assertThat(page.locator("[data-testid='btn-print']")).isVisible();

        takeManualScreenshot("bank-recon/recon-report");
    }

    // ==================== Reconciliation List ====================

    @Test
    @DisplayName("Should display reconciliations list")
    void shouldDisplayReconciliationsList() {
        loginAsAdmin();
        navigateTo("/bank-reconciliation/reconciliations");
        waitForPageLoad();

        assertThat(page.locator("#page-title")).containsText("Daftar Rekonsiliasi");
        assertThat(page.locator("[data-testid='reconciliations-table']")).isVisible();
        assertThat(page.locator("[data-testid='btn-new-recon']")).isVisible();
    }

    // ==================== RBAC ====================

    @Test
    @DisplayName("Staff should have view-only access to bank reconciliation")
    void staffShouldHaveViewOnlyAccess() {
        login("staff", "password");
        navigateTo("/bank-reconciliation");
        waitForPageLoad();

        assertThat(page.locator("#page-title")).containsText("Rekonsiliasi Bank");
    }

    @Test
    @DisplayName("Staff should be able to view parser configs")
    void staffShouldViewParserConfigs() {
        login("staff", "password");
        navigateTo("/bank-reconciliation/parser-configs");
        waitForPageLoad();

        assertThat(page.locator("[data-testid='parser-config-table']")).isVisible();
    }

    // ==================== Helper Methods ====================

    private void createReconciliationViaUI() {
        navigateTo("/bank-reconciliation/reconciliations/new");
        waitForPageLoad();

        String optionValue = page.locator("[data-testid='select-statement'] option:not([value=''])")
                .first().evaluate("el => el.value").toString();
        page.locator("[data-testid='select-statement']").selectOption(optionValue);

        page.locator("[data-testid='btn-create-recon']").click();
        waitForPageLoad();
    }
}
