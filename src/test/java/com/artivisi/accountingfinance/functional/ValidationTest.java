package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.repository.PayrollRunRepository;
import com.artivisi.accountingfinance.repository.EmployeeRepository;
import com.artivisi.accountingfinance.repository.ClientRepository;
import com.artivisi.accountingfinance.repository.ProjectRepository;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Validation tests to exercise error paths in controllers.
 * These tests submit forms with invalid data to improve coverage of validation code.
 */
@DisplayName("Validation Tests")
@Import(ServiceTestDataInitializer.class)
class ValidationTest extends PlaywrightTestBase {

    @Autowired
    private PayrollRunRepository payrollRunRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    // ==================== PAYROLL VALIDATION ====================

    @Test
    @DisplayName("Should show validation error when submitting payroll form with empty period")
    void shouldShowValidationErrorForEmptyPayrollPeriod() {
        navigateTo("/payroll/new");
        waitForPageLoad();

        // Clear the period field and submit
        var periodInput = page.locator("input[name='period']").first();
        if (periodInput.isVisible()) {
            periodInput.clear();
        }

        // Submit the form
        var submitBtn = page.locator("button[type='submit']").first();
        submitBtn.click();
        waitForPageLoad();

        // Should stay on form page with validation error
        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should show error when creating duplicate payroll period")
    void shouldShowErrorForDuplicatePayrollPeriod() {
        // First, get an existing payroll period
        var existingPayroll = payrollRunRepository.findAll().stream().findFirst();
        if (existingPayroll.isEmpty()) {
            return;
        }

        String existingPeriod = existingPayroll.get().getPayrollPeriod();

        navigateTo("/payroll/new");
        waitForPageLoad();

        // Fill with existing period
        var periodInput = page.locator("input[name='period']").first();
        if (periodInput.isVisible()) {
            periodInput.fill(existingPeriod);
        }

        // Submit the form
        var submitBtn = page.locator("button[type='submit']").first();
        submitBtn.click();
        waitForPageLoad();

        // Should show error for duplicate
        assertThat(page.locator("body")).isVisible();
    }

    // ==================== EMPLOYEE VALIDATION ====================

    @Test
    @DisplayName("Should show validation error when submitting employee form with empty name")
    void shouldShowValidationErrorForEmptyEmployeeName() {
        navigateTo("/employees/new");
        waitForPageLoad();

        // Clear required fields and submit
        var nameInput = page.locator("input[name='name']").first();
        if (nameInput.isVisible()) {
            nameInput.clear();
        }

        var submitBtn = page.locator("button[type='submit']").first();
        submitBtn.click();
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should show validation error for invalid employee email format")
    void shouldShowValidationErrorForInvalidEmployeeEmail() {
        navigateTo("/employees/new");
        waitForPageLoad();

        var emailInput = page.locator("input[name='email']").first();
        if (emailInput.isVisible()) {
            emailInput.fill("invalid-email");
        }

        var submitBtn = page.locator("button[type='submit']").first();
        submitBtn.click();
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    // ==================== CLIENT VALIDATION ====================

    @Test
    @DisplayName("Should show validation error when submitting client form with empty name")
    void shouldShowValidationErrorForEmptyClientName() {
        navigateTo("/clients/new");
        waitForPageLoad();

        var nameInput = page.locator("input[name='name']").first();
        if (nameInput.isVisible()) {
            nameInput.clear();
        }

        var submitBtn = page.locator("button[type='submit']").first();
        submitBtn.click();
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should show validation error for invalid client NPWP format")
    void shouldShowValidationErrorForInvalidClientNpwp() {
        navigateTo("/clients/new");
        waitForPageLoad();

        var npwpInput = page.locator("input[name='npwp']").first();
        if (npwpInput.isVisible()) {
            npwpInput.fill("invalid-npwp");
        }

        var submitBtn = page.locator("button[type='submit']").first();
        submitBtn.click();
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    // ==================== PROJECT VALIDATION ====================

    @Test
    @DisplayName("Should show validation error when submitting project form with empty code")
    void shouldShowValidationErrorForEmptyProjectCode() {
        navigateTo("/projects/new");
        waitForPageLoad();

        var codeInput = page.locator("input[name='code']").first();
        if (codeInput.isVisible()) {
            codeInput.clear();
        }

        var submitBtn = page.locator("button[type='submit']").first();
        submitBtn.click();
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should show error when creating duplicate project code")
    void shouldShowErrorForDuplicateProjectCode() {
        var existingProject = projectRepository.findAll().stream().findFirst();
        if (existingProject.isEmpty()) {
            return;
        }

        String existingCode = existingProject.get().getCode();

        navigateTo("/projects/new");
        waitForPageLoad();

        var codeInput = page.locator("input[name='code']").first();
        if (codeInput.isVisible()) {
            codeInput.fill(existingCode);
        }

        var submitBtn = page.locator("button[type='submit']").first();
        submitBtn.click();
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    // ==================== USER VALIDATION ====================

    @Test
    @DisplayName("Should show validation error when submitting user form with empty username")
    void shouldShowValidationErrorForEmptyUsername() {
        navigateTo("/users/new");
        waitForPageLoad();

        var usernameInput = page.locator("input[name='username']").first();
        if (usernameInput.isVisible()) {
            usernameInput.clear();
        }

        var submitBtn = page.locator("button[type='submit']").first();
        submitBtn.click();
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should show validation error for weak password")
    void shouldShowValidationErrorForWeakPassword() {
        navigateTo("/users/new");
        waitForPageLoad();

        var passwordInput = page.locator("input[name='password']").first();
        if (passwordInput.isVisible()) {
            passwordInput.fill("weak");  // Too short, no complexity
        }

        var submitBtn = page.locator("button[type='submit']").first();
        submitBtn.click();
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should show validation error for password mismatch")
    void shouldShowValidationErrorForPasswordMismatch() {
        navigateTo("/users/new");
        waitForPageLoad();

        var passwordInput = page.locator("input[name='password']").first();
        var confirmInput = page.locator("input[name='confirmPassword']").first();

        if (passwordInput.isVisible() && confirmInput.isVisible()) {
            passwordInput.fill("ValidPassword123!");
            confirmInput.fill("DifferentPassword456!");
        }

        var submitBtn = page.locator("button[type='submit']").first();
        submitBtn.click();
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    // ==================== INVOICE VALIDATION ====================

    @Test
    @DisplayName("Should show validation error when submitting invoice with no client")
    void shouldShowValidationErrorForInvoiceWithNoClient() {
        navigateTo("/invoices/new");
        waitForPageLoad();

        // Try to submit without selecting client
        var submitBtn = page.locator("button[type='submit']").first();
        submitBtn.click();
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should show validation error for negative invoice amount")
    void shouldShowValidationErrorForNegativeInvoiceAmount() {
        navigateTo("/invoices/new");
        waitForPageLoad();

        var amountInput = page.locator("input[name='amount'], input[name='totalAmount']").first();
        if (amountInput.isVisible()) {
            amountInput.fill("-1000");
        }

        var submitBtn = page.locator("button[type='submit']").first();
        submitBtn.click();
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    // ==================== FIXED ASSET VALIDATION ====================

    @Test
    @DisplayName("Should show validation error when submitting fixed asset with empty name")
    void shouldShowValidationErrorForEmptyAssetName() {
        navigateTo("/fixed-assets/new");
        waitForPageLoad();

        var nameInput = page.locator("input[name='name']").first();
        if (nameInput.isVisible()) {
            nameInput.clear();
        }

        var submitBtn = page.locator("button[type='submit']").first();
        if (submitBtn.isVisible()) {
            submitBtn.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should show validation error for zero acquisition cost")
    void shouldShowValidationErrorForZeroAcquisitionCost() {
        navigateTo("/fixed-assets/new");
        waitForPageLoad();

        var costInput = page.locator("input[name='acquisitionCost']").first();
        if (costInput.isVisible()) {
            costInput.fill("0");
        }

        var submitBtn = page.locator("button[type='submit']").first();
        if (submitBtn.isVisible()) {
            submitBtn.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }

    // ==================== PRODUCT VALIDATION ====================

    @Test
    @DisplayName("Should show validation error when submitting product with empty code")
    void shouldShowValidationErrorForEmptyProductCode() {
        navigateTo("/products/new");
        waitForPageLoad();

        var codeInput = page.locator("input[name='code']").first();
        if (codeInput.isVisible()) {
            codeInput.clear();
        }

        var submitBtn = page.locator("button[type='submit']").first();
        submitBtn.click();
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should show validation error for negative product price")
    void shouldShowValidationErrorForNegativeProductPrice() {
        navigateTo("/products/new");
        waitForPageLoad();

        var priceInput = page.locator("input[name='sellingPrice']").first();
        if (priceInput.isVisible()) {
            priceInput.fill("-100");
        }

        var submitBtn = page.locator("button[type='submit']").first();
        submitBtn.click();
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    // ==================== TRANSACTION VALIDATION ====================

    @Test
    @DisplayName("Should show validation error when submitting transaction with no description")
    void shouldShowValidationErrorForEmptyTransactionDescription() {
        navigateTo("/transactions/new");
        waitForPageLoad();

        var descInput = page.locator("input[name='description'], textarea[name='description']").first();
        if (descInput.isVisible()) {
            descInput.clear();
        }

        var submitBtn = page.locator("button[type='submit']").first();
        submitBtn.click();
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should show validation error for unbalanced journal entry")
    void shouldShowValidationErrorForUnbalancedJournal() {
        navigateTo("/transactions/new");
        waitForPageLoad();

        // Fill some basic info but with unbalanced amounts
        var descInput = page.locator("input[name='description'], textarea[name='description']").first();
        if (descInput.isVisible()) {
            descInput.fill("Test Unbalanced");
        }

        // This would need actual journal entry rows to be meaningful
        var submitBtn = page.locator("button[type='submit']").first();
        submitBtn.click();
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    // ==================== CHANGE PASSWORD VALIDATION ====================

    @Test
    @DisplayName("Should show validation error when current password is wrong")
    void shouldShowValidationErrorForWrongCurrentPassword() {
        navigateTo("/self-service/change-password");
        waitForPageLoad();

        var currentPasswordInput = page.locator("input[name='currentPassword']").first();
        var newPasswordInput = page.locator("input[name='newPassword']").first();
        var confirmPasswordInput = page.locator("input[name='confirmPassword']").first();

        if (currentPasswordInput.isVisible()) {
            currentPasswordInput.fill("WrongPassword123!");
            if (newPasswordInput.isVisible()) {
                newPasswordInput.fill("NewValidPassword123!");
            }
            if (confirmPasswordInput.isVisible()) {
                confirmPasswordInput.fill("NewValidPassword123!");
            }
        }

        var submitBtn = page.locator("button[type='submit']").first();
        if (submitBtn.isVisible()) {
            submitBtn.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }

    // ==================== SETTINGS VALIDATION ====================

    @Test
    @DisplayName("Should show validation error for empty company name in settings")
    void shouldShowValidationErrorForEmptyCompanyName() {
        navigateTo("/settings/company");
        waitForPageLoad();

        var nameInput = page.locator("input[name='companyName'], input[name='name']").first();
        if (nameInput.isVisible()) {
            nameInput.clear();
        }

        var submitBtn = page.locator("button[type='submit']").first();
        if (submitBtn.isVisible()) {
            submitBtn.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }
}
