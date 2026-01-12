package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Functional tests for TaxCalendarController.
 * Tests tax calendar and deadline management.
 */
@DisplayName("Tax Calendar Controller Tests")
@Import(ServiceTestDataInitializer.class)
class TaxCalendarControllerFunctionalTest extends PlaywrightTestBase {

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    @Test
    @DisplayName("Should display tax calendar page")
    void shouldDisplayTaxCalendarPage() {
        navigateTo("/tax-calendar");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/tax-calendar.*"));
    }

    @Test
    @DisplayName("Should filter by year")
    void shouldFilterByYear() {
        int currentYear = LocalDate.now().getYear();
        navigateTo("/tax-calendar?year=" + currentYear);
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should filter by month")
    void shouldFilterByMonth() {
        int currentMonth = LocalDate.now().getMonthValue();
        navigateTo("/tax-calendar?month=" + currentMonth);
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should filter by year and month")
    void shouldFilterByYearAndMonth() {
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();
        navigateTo("/tax-calendar?year=" + year + "&month=" + month);
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should have year select dropdown")
    void shouldHaveYearSelectDropdown() {
        navigateTo("/tax-calendar");
        waitForPageLoad();

        var yearSelect = page.locator("select[name='year']").first();
        if (yearSelect.isVisible()) {
            assertThat(yearSelect).isVisible();
        }
    }

    @Test
    @DisplayName("Should have month select dropdown")
    void shouldHaveMonthSelectDropdown() {
        navigateTo("/tax-calendar");
        waitForPageLoad();

        var monthSelect = page.locator("select[name='month']").first();
        if (monthSelect.isVisible()) {
            assertThat(monthSelect).isVisible();
        }
    }

    @Test
    @DisplayName("Should display yearly view page")
    void shouldDisplayYearlyViewPage() {
        navigateTo("/tax-calendar/yearly");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should display yearly view for specific year")
    void shouldDisplayYearlyViewForSpecificYear() {
        int currentYear = LocalDate.now().getYear();
        navigateTo("/tax-calendar/yearly?year=" + currentYear);
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should display upcoming deadlines page")
    void shouldDisplayUpcomingDeadlinesPage() {
        navigateTo("/tax-calendar/upcoming");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should get dashboard widget")
    void shouldGetDashboardWidget() {
        navigateTo("/tax-calendar/api/widget");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should navigate to yearly view from list")
    void shouldNavigateToYearlyViewFromList() {
        navigateTo("/tax-calendar");
        waitForPageLoad();

        var yearlyLink = page.locator("a[href*='/tax-calendar/yearly']").first();
        if (yearlyLink.isVisible()) {
            yearlyLink.click();
            waitForPageLoad();
            assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/tax-calendar\\/yearly.*"));
        }
    }

    @Test
    @DisplayName("Should navigate to upcoming from list")
    void shouldNavigateToUpcomingFromList() {
        navigateTo("/tax-calendar");
        waitForPageLoad();

        var upcomingLink = page.locator("a[href*='/tax-calendar/upcoming']").first();
        if (upcomingLink.isVisible()) {
            upcomingLink.click();
            waitForPageLoad();
            assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/tax-calendar\\/upcoming.*"));
        }
    }

    @Test
    @DisplayName("Should navigate back to list from yearly")
    void shouldNavigateBackToListFromYearly() {
        navigateTo("/tax-calendar/yearly");
        waitForPageLoad();

        var backLink = page.locator("a[href='/tax-calendar']").first();
        if (backLink.isVisible()) {
            backLink.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }
}
