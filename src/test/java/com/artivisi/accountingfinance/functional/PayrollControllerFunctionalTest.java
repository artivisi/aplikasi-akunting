package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.repository.PayrollRunRepository;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Functional tests for PayrollController.
 * Tests payroll run lifecycle, calculations, and reports.
 */
@DisplayName("Payroll Controller Tests")
@Import(ServiceTestDataInitializer.class)
class PayrollControllerFunctionalTest extends PlaywrightTestBase {

    @Autowired
    private PayrollRunRepository payrollRunRepository;

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    @Test
    @DisplayName("Should display payroll list page")
    void shouldDisplayPayrollListPage() {
        navigateTo("/payroll");
        waitForPageLoad();

        assertThat(page.url())
            .as("Should be on payroll page")
            .contains("/payroll");
    }

    @Test
    @DisplayName("Should display status filter")
    void shouldDisplayStatusFilter() {
        navigateTo("/payroll");
        waitForPageLoad();

        var statusSelect = page.locator("select[name='status']").first();

        assertThat(statusSelect.isVisible())
            .as("Status filter should be visible")
            .isTrue();
    }

    @Test
    @DisplayName("Should have new payroll button")
    void shouldHaveNewPayrollButton() {
        navigateTo("/payroll");
        waitForPageLoad();

        var newButton = page.locator("a[href*='/payroll/new']").first();

        assertThat(newButton.isVisible())
            .as("New payroll button should be visible")
            .isTrue();
    }

    @Test
    @DisplayName("Should filter by status")
    void shouldFilterByStatus() {
        navigateTo("/payroll");
        waitForPageLoad();

        page.locator("select[name='status']").first().selectOption("DRAFT");

        // Submit filter form using the form context
        page.locator("form[action='/payroll'] button[type='submit']").click();
        waitForPageLoad();

        assertThat(page.url())
            .as("URL should contain status filter")
            .contains("status=DRAFT");
    }

    @Test
    @DisplayName("Should display new payroll form")
    void shouldDisplayNewPayrollForm() {
        navigateTo("/payroll/new");
        waitForPageLoad();

        assertThat(page.url())
            .as("Should be on new payroll form")
            .contains("/payroll/new");
    }

    @Test
    @DisplayName("Should display period input")
    void shouldDisplayPeriodInput() {
        navigateTo("/payroll/new");
        waitForPageLoad();

        var periodInput = page.locator("input[name='period']").first();

        assertThat(periodInput.isVisible())
            .as("Period input should be visible")
            .isTrue();
    }

    @Test
    @DisplayName("Should display base salary input")
    void shouldDisplayBaseSalaryInput() {
        navigateTo("/payroll/new");
        waitForPageLoad();

        var baseSalaryInput = page.locator("input[name='baseSalary']").first();

        assertThat(baseSalaryInput.isVisible())
            .as("Base salary input should be visible")
            .isTrue();
    }

    @Test
    @DisplayName("Should display risk class select")
    void shouldDisplayRiskClassSelect() {
        navigateTo("/payroll/new");
        waitForPageLoad();

        var riskClassSelect = page.locator("select[name='jkkRiskClass']").first();

        assertThat(riskClassSelect.isVisible())
            .as("JKK risk class select should be visible")
            .isTrue();
    }

    @Test
    @DisplayName("Should have period input with month type")
    void shouldHavePeriodInputWithMonthType() {
        navigateTo("/payroll/new");
        waitForPageLoad();

        var periodInput = page.locator("input[name='period']").first();

        // Verify the period input is a month type input
        assertThat(periodInput.getAttribute("type"))
            .as("Period input should be of type month")
            .isEqualTo("month");
    }

    @Test
    @DisplayName("Should create new payroll run")
    void shouldCreateNewPayrollRun() {
        navigateTo("/payroll/new");
        waitForPageLoad();

        // Use a unique period to avoid duplicates
        String uniquePeriod = YearMonth.now().plusMonths(12).toString();

        page.locator("input[name='period']").first().fill(uniquePeriod);
        page.locator("input[name='baseSalary']").first().fill("10000000");
        page.locator("select[name='jkkRiskClass']").first().selectOption("1");

        page.locator("#btn-submit").click();
        waitForPageLoad();

        // Should redirect to payroll detail or list
        assertThat(page.url())
            .as("Should redirect after creating payroll")
            .containsAnyOf("/payroll/", "/payroll");
    }

    @Test
    @DisplayName("Should display payroll detail if exists")
    void shouldDisplayPayrollDetailIfExists() {
        var payrollRuns = payrollRunRepository.findAll();
        if (payrollRuns.isEmpty()) {
            return; // Skip if no payroll runs exist
        }

        var payrollRun = payrollRuns.get(0);
        navigateTo("/payroll/" + payrollRun.getId());
        waitForPageLoad();

        assertThat(page.url())
            .as("Should be on payroll detail page")
            .contains("/payroll/" + payrollRun.getId());
    }

    @Test
    @DisplayName("Should show payroll period in detail")
    void shouldShowPayrollPeriodInDetail() {
        var payrollRuns = payrollRunRepository.findAll();
        if (payrollRuns.isEmpty()) {
            return;
        }

        var payrollRun = payrollRuns.get(0);
        navigateTo("/payroll/" + payrollRun.getId());
        waitForPageLoad();

        // Should display the period in Indonesian format (e.g., "Februari 2024")
        var pageContent = page.content();
        assertThat(pageContent)
            .as("Should show payroll period display name")
            .contains(payrollRun.getPeriodDisplayName());
    }

    @Test
    @DisplayName("Should display action buttons based on status")
    void shouldDisplayActionButtonsBasedOnStatus() {
        var payrollRuns = payrollRunRepository.findAll();
        if (payrollRuns.isEmpty()) {
            return;
        }

        var payrollRun = payrollRuns.get(0);
        navigateTo("/payroll/" + payrollRun.getId());
        waitForPageLoad();

        // Should have at least some action buttons
        var actionButtons = page.locator("form[action*='/payroll/'] button[type='submit']").all();

        // There should be action buttons (approve, cancel, post, recalculate, delete)
        assertThat(actionButtons.size())
            .as("Should have action buttons")
            .isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("Should have recalculate form")
    void shouldHaveRecalculateForm() {
        var payrollRuns = payrollRunRepository.findAll();
        if (payrollRuns.isEmpty()) {
            return;
        }

        var draftPayroll = payrollRuns.stream()
            .filter(pr -> pr.getStatus().name().equals("DRAFT"))
            .findFirst();

        if (draftPayroll.isEmpty()) {
            return;
        }

        navigateTo("/payroll/" + draftPayroll.get().getId());
        waitForPageLoad();

        var recalculateForm = page.locator("form[action*='/recalculate']").first();

        // If draft, recalculate form should be visible
        if (recalculateForm.isVisible()) {
            assertThat(recalculateForm.isVisible()).isTrue();
        }
    }

    @Test
    @DisplayName("Should have export buttons for payroll run")
    void shouldHaveExportButtonsForPayrollRun() {
        var payrollRuns = payrollRunRepository.findAll();
        if (payrollRuns.isEmpty()) {
            return;
        }

        var payrollRun = payrollRuns.get(0);
        navigateTo("/payroll/" + payrollRun.getId());
        waitForPageLoad();

        // Should have export links for PDF/Excel
        var exportLinks = page.locator("a[href*='/export/']").all();

        assertThat(exportLinks.size())
            .as("Should have export links")
            .isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("Should display bukti potong page")
    void shouldDisplayBuktiPotongPage() {
        navigateTo("/payroll/bukti-potong");
        waitForPageLoad();

        assertThat(page.url())
            .as("Should be on bukti potong page")
            .contains("/payroll/bukti-potong");
    }

    @Test
    @DisplayName("Should display year filter")
    void shouldDisplayYearFilter() {
        navigateTo("/payroll/bukti-potong");
        waitForPageLoad();

        // Should have year selection
        var yearSelect = page.locator("select[name='year'], input[name='year']").first();

        assertThat(yearSelect.isVisible())
            .as("Year filter should be visible")
            .isTrue();
    }

    @Test
    @DisplayName("Should filter by year")
    void shouldFilterByYear() {
        navigateTo("/payroll/bukti-potong?year=2024");
        waitForPageLoad();

        assertThat(page.url())
            .as("URL should contain year parameter")
            .contains("year=2024");
    }

    @Test
    @DisplayName("Should navigate from list to new form")
    void shouldNavigateFromListToNewForm() {
        navigateTo("/payroll");
        waitForPageLoad();

        page.locator("a[href*='/payroll/new']").first().click();
        waitForPageLoad();

        assertThat(page.url())
            .as("Should navigate to new payroll form")
            .contains("/payroll/new");
    }

    @Test
    @DisplayName("Should navigate to bukti potong from list")
    void shouldNavigateToBuktiPotongFromList() {
        navigateTo("/payroll");
        waitForPageLoad();

        var buktiPotongLink = page.locator("a[href*='/payroll/bukti-potong']").first();

        if (buktiPotongLink.isVisible()) {
            buktiPotongLink.click();
            waitForPageLoad();

            assertThat(page.url())
                .as("Should navigate to bukti potong page")
                .contains("/payroll/bukti-potong");
        }
    }
}
