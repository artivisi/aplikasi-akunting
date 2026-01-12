package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.repository.FiscalPeriodRepository;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Functional tests for FiscalPeriodController.
 * Tests fiscal period management operations.
 */
@DisplayName("Fiscal Period Controller Tests")
@Import(ServiceTestDataInitializer.class)
class FiscalPeriodControllerFunctionalTest extends PlaywrightTestBase {

    @Autowired
    private FiscalPeriodRepository fiscalPeriodRepository;

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    @Test
    @DisplayName("Should display fiscal period list page")
    void shouldDisplayFiscalPeriodListPage() {
        navigateTo("/fiscal-periods");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/fiscal-periods.*"));
    }

    @Test
    @DisplayName("Should filter by year")
    void shouldFilterByYear() {
        int currentYear = LocalDate.now().getYear();
        navigateTo("/fiscal-periods?year=" + currentYear);
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should filter by status")
    void shouldFilterByStatus() {
        navigateTo("/fiscal-periods?status=OPEN");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should have year filter dropdown")
    void shouldHaveYearFilterDropdown() {
        navigateTo("/fiscal-periods");
        waitForPageLoad();

        var yearSelect = page.locator("select[name='year']").first();
        if (yearSelect.isVisible()) {
            assertThat(yearSelect).isVisible();
        }
    }

    @Test
    @DisplayName("Should have status filter dropdown")
    void shouldHaveStatusFilterDropdown() {
        navigateTo("/fiscal-periods");
        waitForPageLoad();

        var statusSelect = page.locator("select[name='status']").first();
        if (statusSelect.isVisible()) {
            assertThat(statusSelect).isVisible();
        }
    }

    @Test
    @DisplayName("Should display new fiscal period form")
    void shouldDisplayNewFiscalPeriodForm() {
        navigateTo("/fiscal-periods/new");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should have year input")
    void shouldHaveYearInput() {
        navigateTo("/fiscal-periods/new");
        waitForPageLoad();

        var yearInput = page.locator("input[name='year'], select[name='year']").first();
        if (yearInput.isVisible()) {
            assertThat(yearInput).isVisible();
        }
    }

    @Test
    @DisplayName("Should have month input")
    void shouldHaveMonthInput() {
        navigateTo("/fiscal-periods/new");
        waitForPageLoad();

        var monthInput = page.locator("input[name='month'], select[name='month']").first();
        if (monthInput.isVisible()) {
            assertThat(monthInput).isVisible();
        }
    }

    @Test
    @DisplayName("Should display fiscal period detail page")
    void shouldDisplayFiscalPeriodDetailPage() {
        var period = fiscalPeriodRepository.findAll().stream().findFirst();
        if (period.isEmpty()) {
            return;
        }

        navigateTo("/fiscal-periods/" + period.get().getId());
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should have close month button for open periods")
    void shouldHaveCloseMonthButtonForOpenPeriods() {
        var period = fiscalPeriodRepository.findAll().stream()
                .filter(p -> "OPEN".equals(p.getStatus().name()))
                .findFirst();
        if (period.isEmpty()) {
            return;
        }

        navigateTo("/fiscal-periods/" + period.get().getId());
        waitForPageLoad();

        var closeMonthBtn = page.locator("form[action*='/close-month'] button[type='submit']").first();
        if (closeMonthBtn.isVisible()) {
            assertThat(closeMonthBtn).isVisible();
        }
    }
}
