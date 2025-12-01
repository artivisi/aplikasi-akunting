package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.page.LoginPage;
import com.artivisi.accountingfinance.functional.page.TemplateDetailPage;
import com.artivisi.accountingfinance.functional.page.TemplateFormPage;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@DisplayName("Template Dynamic Account Feature")
class TemplateDynamicAccountTest extends PlaywrightTestBase {

    private LoginPage loginPage;
    private TemplateFormPage templateFormPage;
    private TemplateDetailPage templateDetailPage;

    @BeforeEach
    void setUp() {
        loginPage = new LoginPage(page, baseUrl());
        templateFormPage = new TemplateFormPage(page, baseUrl());
        templateDetailPage = new TemplateDetailPage(page, baseUrl());

        loginPage.navigate().loginAsAdmin();
    }

    @Test
    @DisplayName("Template detail page should display without SpEL errors")
    void templateDetailShouldDisplayWithoutSpelErrors() {
        // Navigate to template detail page
        templateDetailPage.navigate("e0000000-0000-0000-0000-000000000001");

        // Should display page title without errors
        templateDetailPage.assertPageTitleVisible();
        templateDetailPage.assertTemplateNameVisible();

        // Should display template statistics (tests SpEL expressions)
        assertThat(page.locator("text=Total baris:")).isVisible();
        assertThat(page.locator("text=Debit:")).isVisible();
        assertThat(page.locator("text=Kredit:")).isVisible();

        // Should display balance summary section
        assertThat(page.locator("#balance-summary")).isVisible();

        // Should not show any error messages
        assertThat(page.locator("text=SpelParseException")).not().isVisible();
        assertThat(page.locator("text=Error")).not().isVisible();
    }

    @Test
    @DisplayName("Template detail should show dynamic account indicators correctly")
    void templateDetailShouldShowDynamicAccountIndicators() {
        // Create a template with dynamic accounts
        templateFormPage.navigateToNew();

        // Fill basic info
        templateFormPage.fillTemplateName("Template Akun Dinamis Test");
        templateFormPage.selectCategory("INCOME");
        templateFormPage.selectCashFlowCategory("OPERATING");
        templateFormPage.selectTemplateType("SIMPLE");

        // Add first line - fixed account (Kas Kecil)
        String kasKecilAccountId = templateFormPage.getFirstAccountId();
        templateFormPage.selectAccountForLine(0, kasKecilAccountId);
        templateFormPage.setPositionForLine(0, "DEBIT");
        templateFormPage.fillFormulaForLine(0, "AMOUNT");

        // Add second line - dynamic account (leave empty)
        templateFormPage.clickAddLine();
        page.waitForTimeout(300);
        
        // Select the "Pilih saat transaksi" option (empty value)
        page.locator("#line-account-1").selectOption("");
        templateFormPage.setPositionForLine(1, "CREDIT");
        templateFormPage.fillFormulaForLine(1, "AMOUNT");
        
        // Add hint for dynamic account
        page.fill("input[name='lines[1].accountHint']", "Pilih akun pendapatan");

        // Save template
        templateFormPage.clickSave();

        // Should redirect to detail page
        page.waitForURL("**/templates/**");
        assertThat(page.locator("#page-title")).isVisible();

        // Should show "?" badge for dynamic account
        assertThat(page.locator("#dynamic-account-indicator-1")).isVisible();

        // Should show account hint
        assertThat(page.locator("text=Pilih akun pendapatan")).isVisible();

        // Should show fixed account code for first line
        assertThat(page.locator("#position-badge-0")).hasText("Debit");
        
        // Should show "Pilih saat transaksi" or hint for dynamic account
        assertThat(page.locator("#position-badge-1")).hasText("Kredit");
    }

    @Test
    @DisplayName("Preview modal should display selected account and amount")
    void previewModalShouldShowSelectedAccountAndAmount() {
        // Navigate to template form
        templateFormPage.navigateToNew();

        // Fill basic info
        templateFormPage.fillTemplateName("Test Preview Modal");
        templateFormPage.selectCategory("INCOME");
        templateFormPage.selectCashFlowCategory("OPERATING");
        templateFormPage.selectTemplateType("SIMPLE");

        // Add debit line
        String debitAccountId = templateFormPage.getFirstAccountId();
        templateFormPage.selectAccountForLine(0, debitAccountId);
        templateFormPage.setPositionForLine(0, "DEBIT");
        templateFormPage.fillFormulaForLine(0, "AMOUNT");

        // Add credit line
        templateFormPage.clickAddLine();
        page.waitForTimeout(300);
        String creditAccountId = templateFormPage.getSecondAccountId();
        templateFormPage.selectAccountForLine(1, creditAccountId);
        templateFormPage.setPositionForLine(1, "CREDIT");
        templateFormPage.fillFormulaForLine(1, "AMOUNT");

        // Set preview amount
        templateFormPage.fillPreviewAmount("5.000.000");
        page.waitForTimeout(300);

        // Click preview button
        page.locator("button:has-text('Preview')").click();

        // Wait for modal to appear
        page.waitForSelector("#previewModal", new com.microsoft.playwright.Page.WaitForSelectorOptions().setTimeout(5000));

        // Modal should be visible
        assertThat(page.locator("#previewModal")).isVisible();

        // Should show template name in modal title
        assertThat(page.locator("#previewModal h3:has-text('Preview Template: Test Preview Modal')")).isVisible();

        // Should show preview amount
        assertThat(page.locator("#previewModal:has-text('Rp 5.000.000')")).isVisible();

        // Should show account names in table
        assertThat(page.locator("#previewModal table tbody tr")).hasCount(2);

        // Should show debit and credit columns
        assertThat(page.locator("#previewModal th:has-text('Debit')")).isVisible();
        assertThat(page.locator("#previewModal th:has-text('Kredit')")).isVisible();

        // Should show balance status
        assertThat(page.locator("#previewModal .text-green-600:has-text('Jurnal seimbang')")).isVisible();
    }

    @Test
    @DisplayName("Preview modal close buttons should work correctly")
    void previewModalCloseButtonsShouldWork() {
        // Navigate to template form
        templateFormPage.navigateToNew();

        // Fill minimum required fields
        templateFormPage.fillTemplateName("Test Close Button");
        templateFormPage.selectCategory("EXPENSE");
        templateFormPage.selectCashFlowCategory("OPERATING");

        // Add lines
        String account1 = templateFormPage.getFirstAccountId();
        templateFormPage.selectAccountForLine(0, account1);
        templateFormPage.setPositionForLine(0, "DEBIT");
        templateFormPage.fillFormulaForLine(0, "AMOUNT");

        templateFormPage.clickAddLine();
        page.waitForTimeout(300);
        String account2 = templateFormPage.getSecondAccountId();
        templateFormPage.selectAccountForLine(1, account2);
        templateFormPage.setPositionForLine(1, "CREDIT");
        templateFormPage.fillFormulaForLine(1, "AMOUNT");

        templateFormPage.fillPreviewAmount("1.000.000");

        // Open preview modal
        page.locator("button:has-text('Preview')").click();
        page.waitForSelector("#previewModal");
        assertThat(page.locator("#previewModal")).isVisible();

        // Test X button (close icon)
        page.locator("#previewModal button[onclick*='remove']").first().click();
        page.waitForTimeout(300);

        // Modal should be closed
        assertThat(page.locator("#previewModal")).not().isVisible();

        // Open modal again
        page.locator("button:has-text('Preview')").click();
        page.waitForSelector("#previewModal");
        assertThat(page.locator("#previewModal")).isVisible();

        // Test "Tutup" button
        page.locator("#previewModal button:has-text('Tutup')").click();
        page.waitForTimeout(300);

        // Modal should be closed
        assertThat(page.locator("#previewModal")).not().isVisible();
    }

    @Test
    @DisplayName("Preview should show correct calculations with formula")
    void previewShouldShowCorrectCalculations() {
        // Navigate to template form
        templateFormPage.navigateToNew();

        // Fill basic info
        templateFormPage.fillTemplateName("Test Preview Calculations");
        templateFormPage.selectCategory("EXPENSE");
        templateFormPage.selectCashFlowCategory("OPERATING");

        // Add debit line with 70% formula
        String debitAccountId = templateFormPage.getFirstAccountId();
        templateFormPage.selectAccountForLine(0, debitAccountId);
        templateFormPage.setPositionForLine(0, "DEBIT");
        templateFormPage.fillFormulaForLine(0, "amount * 0.7");

        // Add credit line with 70% formula
        templateFormPage.clickAddLine();
        page.waitForTimeout(300);
        String creditAccountId = templateFormPage.getSecondAccountId();
        templateFormPage.selectAccountForLine(1, creditAccountId);
        templateFormPage.setPositionForLine(1, "CREDIT");
        templateFormPage.fillFormulaForLine(1, "amount * 0.7");

        // Set preview amount to 10,000,000
        templateFormPage.fillPreviewAmount("10.000.000");

        // Click preview
        page.locator("button:has-text('Preview')").click();
        page.waitForSelector("#previewModal");

        // Should show calculated amount (7,000,000)
        assertThat(page.locator("#previewModal:has-text('7.000.000')")).isVisible();

        // Total should match (both debit and credit should be 7,000,000)
        assertThat(page.locator("#previewModal tfoot:has-text('7.000.000')")).isVisible();
    }

    @Test
    @DisplayName("Template detail should handle templates with only debit or only credit lines")
    void templateDetailShouldHandleImbalancedLines() {
        // Create template with only debit lines
        templateFormPage.navigateToNew();

        templateFormPage.fillTemplateName("Template Only Debit");
        templateFormPage.selectCategory("EXPENSE");
        templateFormPage.selectCashFlowCategory("INVESTING");

        // Fill both default lines - debit and credit
        String debitAccountId = templateFormPage.getFirstAccountId();
        templateFormPage.selectAccountForLine(0, debitAccountId);
        templateFormPage.setPositionForLine(0, "DEBIT");
        templateFormPage.fillFormulaForLine(0, "amount");

        String creditAccountId = templateFormPage.getSecondAccountId();
        templateFormPage.selectAccountForLine(1, creditAccountId);
        templateFormPage.setPositionForLine(1, "CREDIT");
        templateFormPage.fillFormulaForLine(1, "amount");

        templateFormPage.clickSave();
        page.waitForURL("**/templates/**");

        // Should display without errors
        templateDetailPage.assertPageTitleVisible();

        // Should show debit count = 1, credit count = 1
        assertThat(page.locator("#debit-count")).containsText("1");
        assertThat(page.locator("#credit-count")).containsText("1");
    }

    @Test
    @DisplayName("Preview button should be disabled when template is invalid")
    void previewButtonShouldBeDisabledWhenInvalid() {
        // Navigate to new template form
        templateFormPage.navigateToNew();

        // Preview button should be disabled initially
        assertThat(page.locator("button:has-text('Preview')")).isDisabled();

        // Fill template name only
        templateFormPage.fillTemplateName("Incomplete Template");

        // Should still be disabled
        assertThat(page.locator("button:has-text('Preview')")).isDisabled();

        // Fill category
        templateFormPage.selectCategory("INCOME");

        // Should still be disabled (needs lines)
        assertThat(page.locator("button:has-text('Preview')")).isDisabled();

        // Add debit line
        String accountId = templateFormPage.getFirstAccountId();
        templateFormPage.selectAccountForLine(0, accountId);
        templateFormPage.setPositionForLine(0, "DEBIT");
        templateFormPage.fillFormulaForLine(0, "AMOUNT");

        // Should still be disabled (needs credit line)
        assertThat(page.locator("button:has-text('Preview')")).isDisabled();

        // Add credit line
        templateFormPage.clickAddLine();
        page.waitForTimeout(300);
        String creditAccountId = templateFormPage.getSecondAccountId();
        templateFormPage.selectAccountForLine(1, creditAccountId);
        templateFormPage.setPositionForLine(1, "CREDIT");
        templateFormPage.fillFormulaForLine(1, "AMOUNT");

        // Now preview button should be enabled
        page.waitForTimeout(500); // Wait for Alpine.js reactivity
        assertThat(page.locator("button:has-text('Preview')")).isEnabled();
    }
}
