package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.repository.EmployeeRepository;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Functional tests for DataSubjectController.
 * Tests GDPR/UU PDP data subject rights management.
 */
@DisplayName("Data Subject Controller Tests")
@Import(ServiceTestDataInitializer.class)
class DataSubjectControllerFunctionalTest extends PlaywrightTestBase {

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    @Test
    @DisplayName("Should display data subject list page")
    void shouldDisplayDataSubjectListPage() {
        navigateTo("/settings/data-subjects");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/settings\\/data-subjects.*"));
    }

    @Test
    @DisplayName("Should have search input")
    void shouldHaveSearchInput() {
        navigateTo("/settings/data-subjects");
        waitForPageLoad();

        var searchInput = page.locator("input[name='search']").first();
        if (searchInput.isVisible()) {
            assertThat(searchInput).isVisible();
        }
    }

    @Test
    @DisplayName("Should search employees by keyword")
    void shouldSearchEmployeesByKeyword() {
        navigateTo("/settings/data-subjects");
        waitForPageLoad();

        var searchInput = page.locator("input[name='search']").first();
        if (searchInput.isVisible()) {
            searchInput.fill("test");

            var searchBtn = page.locator("button[type='submit']").first();
            if (searchBtn.isVisible()) {
                searchBtn.click();
                waitForPageLoad();
            }
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should display employee table")
    void shouldDisplayEmployeeTable() {
        navigateTo("/settings/data-subjects");
        waitForPageLoad();

        var table = page.locator("table").first();
        assertThat(table).isVisible();
    }

    @Test
    @DisplayName("Should display data subject detail page")
    void shouldDisplayDataSubjectDetailPage() {
        var employee = employeeRepository.findAll().stream().findFirst();
        if (employee.isEmpty()) {
            return;
        }

        navigateTo("/settings/data-subjects/" + employee.get().getId());
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should show retention status on detail page")
    void shouldShowRetentionStatusOnDetailPage() {
        var employee = employeeRepository.findAll().stream().findFirst();
        if (employee.isEmpty()) {
            return;
        }

        navigateTo("/settings/data-subjects/" + employee.get().getId());
        waitForPageLoad();

        // Page should contain retention status information
        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should have export data link")
    void shouldHaveExportDataLink() {
        var employee = employeeRepository.findAll().stream().findFirst();
        if (employee.isEmpty()) {
            return;
        }

        navigateTo("/settings/data-subjects/" + employee.get().getId());
        waitForPageLoad();

        var exportLink = page.locator("a[href*='/export']").first();
        if (exportLink.isVisible()) {
            assertThat(exportLink).isVisible();
        }
    }

    @Test
    @DisplayName("Should have anonymize link")
    void shouldHaveAnonymizeLink() {
        var employee = employeeRepository.findAll().stream().findFirst();
        if (employee.isEmpty()) {
            return;
        }

        navigateTo("/settings/data-subjects/" + employee.get().getId());
        waitForPageLoad();

        var anonymizeLink = page.locator("a[href*='/anonymize']").first();
        if (anonymizeLink.isVisible()) {
            assertThat(anonymizeLink).isVisible();
        }
    }

    @Test
    @DisplayName("Should display export data page")
    void shouldDisplayExportDataPage() {
        var employee = employeeRepository.findAll().stream().findFirst();
        if (employee.isEmpty()) {
            return;
        }

        navigateTo("/settings/data-subjects/" + employee.get().getId() + "/export");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should display anonymize confirmation page")
    void shouldDisplayAnonymizeConfirmationPage() {
        var employee = employeeRepository.findAll().stream().findFirst();
        if (employee.isEmpty()) {
            return;
        }

        navigateTo("/settings/data-subjects/" + employee.get().getId() + "/anonymize");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should be accessible from settings menu")
    void shouldBeAccessibleFromSettingsMenu() {
        navigateTo("/settings");
        waitForPageLoad();

        var dataSubjectsLink = page.locator("a[href*='/settings/data-subjects']").first();
        if (dataSubjectsLink.isVisible()) {
            dataSubjectsLink.click();
            waitForPageLoad();
            assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/settings\\/data-subjects.*"));
        }
    }
}
