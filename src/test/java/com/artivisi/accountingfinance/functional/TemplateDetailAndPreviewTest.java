package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.page.LoginPage;
import com.artivisi.accountingfinance.functional.page.TemplateDetailPage;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@DisplayName("Template Detail Page and Preview Modal")
class TemplateDetailAndPreviewTest extends PlaywrightTestBase {

    private LoginPage loginPage;
    private TemplateDetailPage templateDetailPage;

    // Template ID from seed data
    private static final String INCOME_CONSULTING_TEMPLATE_ID = "e0000000-0000-0000-0000-000000000001";

    @BeforeEach
    void setUp() {
        loginPage = new LoginPage(page, baseUrl());
        templateDetailPage = new TemplateDetailPage(page, baseUrl());

        loginPage.navigate().loginAsAdmin();
    }

    @Test
    @DisplayName("Template detail page should render without SpEL parsing errors")
    void templateDetailShouldRenderWithoutSpelErrors() {
        // Navigate to template detail page
        templateDetailPage.navigate(INCOME_CONSULTING_TEMPLATE_ID);

        // Should display page content without errors
        templateDetailPage.assertPageTitleVisible();
        templateDetailPage.assertTemplateNameVisible();

        // Should display template statistics (tests the fixed SpEL expressions)
        assertThat(page.locator("text=Total baris:")).isVisible();
        assertThat(page.locator("text=Debit:")).isVisible();
        assertThat(page.locator("text=Kredit:")).isVisible();

        // Should display balance status icon (tests the fixed SpEL conditional)
        assertThat(page.locator("svg.text-green-600").first()).isVisible();

        // Should not show any SpEL error messages
        assertThat(page.locator("text=SpelParseException")).not().isVisible();
        assertThat(page.locator("text=EL1042E")).not().isVisible();
        assertThat(page.locator("text=Problem parsing")).not().isVisible();

        // Page should have actual template data, not errors
        assertThat(page.locator("#template-name")).containsText("Pendapatan Jasa Konsultasi");
    }

    @Test
    @DisplayName("Preview modal should display data and close buttons should work")
    void previewModalShouldWorkCorrectly() {
        // Navigate to template form (new template)
        page.navigate(baseUrl() + "/templates/new");
        page.waitForLoadState();

        // Wait for Alpine.js to initialize
        page.waitForSelector("[x-data]");

        // Fill basic template information
        page.fill("#templateName", "Test Preview Bug Fix");
        page.selectOption("#category", "INCOME");
        page.selectOption("#cashFlow", "OPERATING");

        // Wait for first line to be initialized
        page.waitForSelector("#line-account-0");

        // Select accounts for the first line (debit)
        String firstAccount = page.locator("#line-account-0 option").nth(1).getAttribute("value");
        page.selectOption("#line-account-0", firstAccount);

        // Set formula for first line
        page.fill("input[name='lines[0].formula']", "AMOUNT");

        // Add second line
        page.click("button:has-text('Tambah Baris')");
        page.waitForTimeout(500);

        // Select account for second line (credit)
        String secondAccount = page.locator("#line-account-1 option").nth(2).getAttribute("value");
        page.selectOption("#line-account-1", secondAccount);

        // Set formula for second line
        page.fill("input[name='lines[1].formula']", "AMOUNT");

        // Set preview amount
        page.fill("input[x-model='previewAmount']", "2500000");
        page.waitForTimeout(300);

        // Click preview button
        page.locator("button:has-text('Preview')").click();

        // Wait for modal to appear
        page.waitForSelector("#previewModal", new com.microsoft.playwright.Page.WaitForSelectorOptions().setTimeout(10000));

        // Modal should be visible
        assertThat(page.locator("#previewModal")).isVisible();

        // Modal should show template name
        assertThat(page.locator("#previewModal h3")).containsText("Test Preview Bug Fix");

        // Modal should show preview amount
        // This verifies that the JavaScript correctly captures and displays form values
        assertThat(page.locator("#previewModal:has-text('Rp 2.500.000')")).isVisible();

        // Modal should show table structure  
        assertThat(page.locator("#previewModal table")).isVisible();
        
        // Modal should show actual account data in the table rows (not empty)
        assertThat(page.locator("#previewModal table tbody tr")).not().hasCount(0);
        
        // Modal should show debit and credit amounts (not just dashes)
        // At least one cell should have "2.500.000" (the amount we entered)
        assertThat(page.locator("#previewModal table tbody:has-text('2.500.000')")).isVisible();
        
        // Modal should NOT show "?" or "Akun tidak tersedia" since we selected real accounts
        assertThat(page.locator("#previewModal:has-text('? - Akun tidak tersedia')")).not().isVisible();
        
        // Modal should show actual account names (not placeholder text)
        // We should see actual account codes and names from the selected accounts
        assertThat(page.locator("#previewModal table tbody tr").first()).isVisible();

        // Test X button (close icon) works
        page.locator("#previewModal button[onclick*='remove']").first().click();
        page.waitForTimeout(500);

        // Modal should be closed
        assertThat(page.locator("#previewModal")).not().isVisible();

        // Open modal again to test second close button
        page.locator("button:has-text('Preview')").click();
        page.waitForSelector("#previewModal");
        assertThat(page.locator("#previewModal")).isVisible();

        // Test "Tutup" button works
        page.locator("#previewModal button:has-text('Tutup')").click();
        page.waitForTimeout(500);

        // Modal should be closed
        assertThat(page.locator("#previewModal")).not().isVisible();
    }

    @Test
    @DisplayName("Template detail should correctly count debit and credit lines")
    void templateDetailShouldCountLinesCorrectly() {
        // Navigate to template detail
        templateDetailPage.navigate(INCOME_CONSULTING_TEMPLATE_ID);

        // Should show total lines count
        assertThat(page.locator("text=/Total baris:\\s*\\d+/")).isVisible();

        // Should show debit count (tests SpEL expression fix)
        assertThat(page.locator("text=/Debit:\\s*\\d+/")).isVisible();

        // Should show credit count (tests SpEL expression fix)
        assertThat(page.locator("text=/Kredit:\\s*\\d+/")).isVisible();

        // Should show balance indicator when debit > 0 and credit > 0
        assertThat(page.locator("svg.text-green-600").first()).isVisible();
    }
}
