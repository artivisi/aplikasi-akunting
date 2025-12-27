package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Functional tests for Tax Profile feature.
 * Tests the tax profile page for viewing and editing company tax classification info.
 */
@DisplayName("Tax Profile")
public class TaxProfileTest extends PlaywrightTestBase {

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    @Test
    @Order(1)
    @DisplayName("Should navigate to tax profile page")
    void navigateToTaxProfile() {
        navigateTo("/tax-profile");
        waitForPageLoad();

        assertThat(page.title())
            .as("Page title should contain 'Profil Pajak'")
            .contains("Profil Pajak");
    }

    @Test
    @Order(2)
    @DisplayName("Should show empty state when no established date")
    void showEmptyStateWhenNoEstablishedDate() {
        navigateTo("/tax-profile");
        waitForPageLoad();

        // Check that form fields exist
        assertThat(page.getByTestId("established-date").isVisible())
            .as("Established date field should be visible")
            .isTrue();
    }

    @Test
    @Order(3)
    @DisplayName("Should save tax profile with established date")
    void saveTaxProfileWithEstablishedDate() {
        navigateTo("/tax-profile");
        waitForPageLoad();

        // Fill established date (company established 2 years ago - eligible for PPh Final)
        String establishedDate = LocalDate.now().minusYears(2).format(DateTimeFormatter.ISO_DATE);
        page.getByTestId("established-date").fill(establishedDate);

        // Set PKP status to No
        page.getByTestId("pkp-no").click();

        // Save
        page.getByTestId("btn-save").click();
        waitForPageLoad();

        // Verify success message or redirect
        assertThat(page.url())
            .as("Should stay on tax profile page after save")
            .contains("/tax-profile");
    }

    @Test
    @Order(4)
    @DisplayName("Should show PPh Final eligibility for company under 4 years")
    void showPPhFinalEligibilityForYoungCompany() {
        navigateTo("/tax-profile");
        waitForPageLoad();

        // Fill established date (company established 2 years ago)
        String establishedDate = LocalDate.now().minusYears(2).format(DateTimeFormatter.ISO_DATE);
        page.getByTestId("established-date").fill(establishedDate);
        page.getByTestId("btn-save").click();
        waitForPageLoad();

        // Verify PPh regime text shows PPh Final eligibility
        String pphRegime = page.getByTestId("pph-regime").textContent();
        assertThat(pphRegime)
            .as("PPh regime should indicate PPh Final 0.5% eligibility")
            .contains("PPh Final");
    }

    @Test
    @Order(5)
    @DisplayName("Should show PPh Badan regime for company over 4 years")
    void showPPhBadanForOldCompany() {
        navigateTo("/tax-profile");
        waitForPageLoad();

        // Fill established date (company established 5 years ago)
        String establishedDate = LocalDate.now().minusYears(5).format(DateTimeFormatter.ISO_DATE);
        page.getByTestId("established-date").fill(establishedDate);
        page.getByTestId("btn-save").click();
        waitForPageLoad();

        // Verify PPh regime text shows PPh Badan
        String pphRegime = page.getByTestId("pph-regime").textContent();
        assertThat(pphRegime)
            .as("PPh regime should indicate PPh Badan with Pasal 31E")
            .contains("PPh Badan");
    }

    @Test
    @Order(6)
    @DisplayName("Should show PKP since field when PKP is selected")
    void showPkpSinceFieldWhenPkpSelected() {
        navigateTo("/tax-profile");
        waitForPageLoad();

        // Select PKP Yes
        page.getByTestId("pkp-yes").click();

        // Wait for Alpine.js to show the PKP since field
        page.waitForTimeout(300);

        // Verify PKP since field is visible
        assertThat(page.getByTestId("pkp-since").isVisible())
            .as("PKP since field should be visible when PKP is selected")
            .isTrue();
    }

    @Test
    @Order(7)
    @DisplayName("Should save and display PKP status")
    void saveAndDisplayPkpStatus() {
        navigateTo("/tax-profile");
        waitForPageLoad();

        // Fill established date
        String establishedDate = LocalDate.now().minusYears(3).format(DateTimeFormatter.ISO_DATE);
        page.getByTestId("established-date").fill(establishedDate);

        // Select PKP Yes
        page.getByTestId("pkp-yes").click();
        page.waitForTimeout(300);

        // Fill PKP since date
        String pkpSince = LocalDate.now().minusYears(1).format(DateTimeFormatter.ISO_DATE);
        page.getByTestId("pkp-since").fill(pkpSince);

        // Save
        page.getByTestId("btn-save").click();
        waitForPageLoad();

        // Verify PPN status shows PKP
        String ppnStatus = page.getByTestId("ppn-status").textContent();
        assertThat(ppnStatus)
            .as("PPN status should indicate wajib pungut PPN")
            .contains("PPN");
    }

    @Test
    @Order(8)
    @DisplayName("Should display company age correctly")
    void displayCompanyAgeCorrectly() {
        navigateTo("/tax-profile");
        waitForPageLoad();

        // Fill established date (exactly 3 years ago)
        String establishedDate = LocalDate.now().minusYears(3).format(DateTimeFormatter.ISO_DATE);
        page.getByTestId("established-date").fill(establishedDate);
        page.getByTestId("btn-save").click();
        waitForPageLoad();

        // Verify company age is displayed
        String companyAge = page.getByTestId("company-age").textContent();
        assertThat(companyAge)
            .as("Company age should show 3 years")
            .contains("3 tahun");
    }

    @Test
    @Order(9)
    @DisplayName("Navigation menu should have Tax Profile link")
    void navigationMenuHasTaxProfileLink() {
        navigateTo("/dashboard");
        waitForPageLoad();

        // Open Master Data menu (use sidebar-nav to avoid matching mobile menu)
        page.locator("#sidebar-nav details:has-text('Master Data')").click();
        page.waitForTimeout(300);

        // Check that Tax Profile link exists
        assertThat(page.locator("#nav-tax-profile").isVisible())
            .as("Tax Profile link should be visible in navigation")
            .isTrue();
    }
}
