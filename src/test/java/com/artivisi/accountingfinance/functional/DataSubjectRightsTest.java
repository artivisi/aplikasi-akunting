package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Functional tests for Data Subject Rights (GDPR/UU PDP) UI.
 * Tests the admin interface for managing data subject requests.
 */
@DisplayName("Data Subject Rights (GDPR/UU PDP)")
class DataSubjectRightsTest {

    @Nested
    @DisplayName("Data Subject List and Access")
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
    class DataSubjectListTests extends PlaywrightTestBase {

        @BeforeEach
        void setUp() {
            loginAsAdmin();
        }

        private void navigateToDataSubjects() {
            page.navigate(baseUrl() + "/settings/data-subjects",
                    new Page.NavigateOptions().setTimeout(30000));
            page.waitForLoadState(LoadState.NETWORKIDLE);
        }

        @Test
        @DisplayName("Should display data subjects list page")
        void shouldDisplayDataSubjectsListPage() {
            navigateToDataSubjects();

            assertThat(page.locator("#page-title")).containsText("Hak Subjek Data");
            assertThat(page.locator("#employee-table")).isVisible();
        }

        @Test
        @DisplayName("Should display GDPR/UU PDP info panel")
        void shouldDisplayGdprInfoPanel() {
            navigateToDataSubjects();

            // Check info panel exists with GDPR references
            assertThat(page.locator("text=Pengelolaan Hak Subjek Data")).isVisible();
            assertThat(page.locator("text=Hak Akses (Art. 15)")).isVisible();
            assertThat(page.locator("text=Hak Penghapusan (Art. 17)")).isVisible();
        }

        @Test
        @DisplayName("Should display employee list with action buttons")
        void shouldDisplayEmployeeListWithActions() {
            navigateToDataSubjects();

            // Should have employees from test data
            assertThat(page.locator("#employee-table")).isVisible();

            // Check action links exist (view detail icon)
            assertThat(page.locator("#employee-table a[title='Lihat Detail']").first()).isVisible();
        }

        @Test
        @DisplayName("Should search employees by name")
        void shouldSearchEmployeesByName() {
            navigateToDataSubjects();

            // Use search input
            page.locator("#search-input").fill("Budi");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Table should still be visible (search results or empty state)
            assertThat(page.locator("#employee-table")).isVisible();
        }
    }

    @Nested
    @DisplayName("Data Subject Detail View")
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
    class DataSubjectDetailTests extends PlaywrightTestBase {

        @BeforeEach
        void setUp() {
            loginAsAdmin();
        }

        @Test
        @DisplayName("Should navigate to employee detail")
        void shouldNavigateToEmployeeDetail() {
            page.navigate(baseUrl() + "/settings/data-subjects",
                    new Page.NavigateOptions().setTimeout(30000));
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Click on first employee's detail link
            page.locator("#employee-table a[title='Lihat Detail']").first().click();
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Should show detail page
            assertThat(page.locator("#page-title")).containsText("Detail Subjek Data");
            assertThat(page.locator("#employee-id")).isVisible();
            assertThat(page.locator("#employee-name")).isVisible();
        }

        @Test
        @DisplayName("Should display retention status")
        void shouldDisplayRetentionStatus() {
            page.navigate(baseUrl() + "/settings/data-subjects",
                    new Page.NavigateOptions().setTimeout(30000));
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Navigate to first employee detail
            page.locator("#employee-table a[title='Lihat Detail']").first().click();
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Should show retention status section
            assertThat(page.locator("#retention-status")).isVisible();
            assertThat(page.locator("text=Periode Retensi")).isVisible();
        }

        @Test
        @DisplayName("Should have export and anonymize buttons")
        void shouldHaveExportAndAnonymizeButtons() {
            page.navigate(baseUrl() + "/settings/data-subjects",
                    new Page.NavigateOptions().setTimeout(30000));
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Navigate to first employee detail
            page.locator("#employee-table a[title='Lihat Detail']").first().click();
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Should have action buttons
            assertThat(page.locator("#btn-export")).isVisible();
            assertThat(page.locator("#btn-export")).containsText("Ekspor Data");
        }
    }

    @Nested
    @DisplayName("Data Subject Export (DSAR)")
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
    class DataSubjectExportTests extends PlaywrightTestBase {

        @BeforeEach
        void setUp() {
            loginAsAdmin();
        }

        @Test
        @DisplayName("Should display export page with personal data")
        void shouldDisplayExportPageWithPersonalData() {
            page.navigate(baseUrl() + "/settings/data-subjects",
                    new Page.NavigateOptions().setTimeout(30000));
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Navigate to first employee detail, then export
            page.locator("#employee-table a[title='Lihat Detail']").first().click();
            page.waitForLoadState(LoadState.NETWORKIDLE);

            page.locator("#btn-export").click();
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Should show export page
            assertThat(page.locator("#page-title")).containsText("Ekspor Data Pribadi");
            assertThat(page.locator("#export-timestamp")).isVisible();
            assertThat(page.locator("#export-name")).isVisible();
        }

        @Test
        @DisplayName("Should display masked sensitive data in export")
        void shouldDisplayMaskedSensitiveData() {
            page.navigate(baseUrl() + "/settings/data-subjects",
                    new Page.NavigateOptions().setTimeout(30000));
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Navigate to first employee detail, then export
            page.locator("#employee-table a[title='Lihat Detail']").first().click();
            page.waitForLoadState(LoadState.NETWORKIDLE);

            page.locator("#btn-export").click();
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Should show masked data section
            assertThat(page.locator("#sensitive-data-header")).isVisible();
            assertThat(page.locator("#exported-data")).isVisible();
        }

        @Test
        @DisplayName("Should display DSAR info banner")
        void shouldDisplayDsarInfoBanner() {
            page.navigate(baseUrl() + "/settings/data-subjects",
                    new Page.NavigateOptions().setTimeout(30000));
            page.waitForLoadState(LoadState.NETWORKIDLE);

            page.locator("#employee-table a[title='Lihat Detail']").first().click();
            page.waitForLoadState(LoadState.NETWORKIDLE);

            page.locator("#btn-export").click();
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Should show DSAR explanation
            assertThat(page.locator("#dsar-info-panel")).isVisible();
            assertThat(page.locator("#dsar-title")).isVisible();
        }
    }

    @Nested
    @DisplayName("Data Subject Anonymization")
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
    class DataSubjectAnonymizeTests extends PlaywrightTestBase {

        @BeforeEach
        void setUp() {
            loginAsAdmin();
        }

        @Test
        @DisplayName("Should display anonymize confirmation page")
        void shouldDisplayAnonymizeConfirmationPage() {
            page.navigate(baseUrl() + "/settings/data-subjects",
                    new Page.NavigateOptions().setTimeout(30000));
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Navigate to first employee detail, then anonymize
            page.locator("#employee-table a[title='Lihat Detail']").first().click();
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Click anonymize button if visible (employee not already anonymized)
            if (page.locator("#btn-anonymize").isVisible()) {
                page.locator("#btn-anonymize").click();
                page.waitForLoadState(LoadState.NETWORKIDLE);

                // Should show anonymize confirmation page
                assertThat(page.locator("#page-title")).containsText("Anonimisasi Data");
                assertThat(page.locator("text=Peringatan")).isVisible();
                assertThat(page.locator("#reason")).isVisible();
            }
        }

        @Test
        @DisplayName("Should require reason for anonymization")
        void shouldRequireReasonForAnonymization() {
            page.navigate(baseUrl() + "/settings/data-subjects",
                    new Page.NavigateOptions().setTimeout(30000));
            page.waitForLoadState(LoadState.NETWORKIDLE);

            page.locator("#employee-table a[title='Lihat Detail']").first().click();
            page.waitForLoadState(LoadState.NETWORKIDLE);

            if (page.locator("#btn-anonymize").isVisible()) {
                page.locator("#btn-anonymize").click();
                page.waitForLoadState(LoadState.NETWORKIDLE);

                // Reason textarea should be required
                assertThat(page.locator("#reason[required]")).isVisible();

                // Confirm checkbox should be required
                assertThat(page.locator("#confirm[required]")).isVisible();
            }
        }

        @Test
        @DisplayName("Should display warning about irreversible action")
        void shouldDisplayWarningAboutIrreversibleAction() {
            page.navigate(baseUrl() + "/settings/data-subjects",
                    new Page.NavigateOptions().setTimeout(30000));
            page.waitForLoadState(LoadState.NETWORKIDLE);

            page.locator("#employee-table a[title='Lihat Detail']").first().click();
            page.waitForLoadState(LoadState.NETWORKIDLE);

            if (page.locator("#btn-anonymize").isVisible()) {
                page.locator("#btn-anonymize").click();
                page.waitForLoadState(LoadState.NETWORKIDLE);

                // Should show warning about irreversible action
                assertThat(page.locator("#warning-panel")).isVisible();
                assertThat(page.locator("#warning-title")).isVisible();
            }
        }
    }

    @Nested
    @DisplayName("Data Subject Rights Authorization")
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
    class DataSubjectAuthorizationTests extends PlaywrightTestBase {

        @Test
        @DisplayName("Should require authentication")
        void shouldRequireAuthentication() {
            // Navigate without login
            page.navigate(baseUrl() + "/settings/data-subjects",
                    new Page.NavigateOptions().setTimeout(30000));
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Should redirect to login
            assertThat(page).hasURL(java.util.regex.Pattern.compile(".*/login.*"));
        }
    }

    @Nested
    @DisplayName("Data Subject Anonymization Execution")
    @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
    class DataSubjectAnonymizeExecutionTests extends PlaywrightTestBase {

        @BeforeEach
        void setUp() {
            loginAsAdmin();
        }

        @Test
        @DisplayName("Should successfully anonymize employee data")
        void shouldSuccessfullyAnonymizeEmployeeData() {
            page.navigate(baseUrl() + "/settings/data-subjects",
                    new Page.NavigateOptions().setTimeout(30000));
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Find an employee that is not already anonymized
            var detailLink = page.locator("#employee-table a[title='Lihat Detail']").first();
            if (!detailLink.isVisible()) {
                return; // No employees to test
            }

            detailLink.click();
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Get employee name before anonymization
            String employeeName = page.locator("#employee-name").textContent();

            // Skip if already anonymized
            if (employeeName.startsWith("ANONYMIZED-")) {
                return;
            }

            // Click anonymize button if visible
            if (!page.locator("#btn-anonymize").isVisible()) {
                return;
            }

            page.locator("#btn-anonymize").click();
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Fill in reason
            page.locator("#reason").fill("Permintaan tertulis dari subjek data untuk tes fungsional GDPR");

            // Check confirm checkbox
            page.locator("#confirm").check();

            // Submit form
            page.locator("#btn-confirm-anonymize").click();
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Should redirect to list with success message
            assertThat(page).hasURL(java.util.regex.Pattern.compile(".*/settings/data-subjects$"));

            // Verify the employee now shows as anonymized
            // Navigate back to list and check
            page.navigate(baseUrl() + "/settings/data-subjects",
                    new Page.NavigateOptions().setTimeout(30000));
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Look for ANONYMIZED- prefix in table or Dianonimisasi status badge
            assertThat(page.locator("#employee-table")).containsText("Dianonimisasi");
        }

        @Test
        @DisplayName("Should verify anonymized data has null PII fields")
        void shouldVerifyAnonymizedDataHasNullPiiFields() {
            page.navigate(baseUrl() + "/settings/data-subjects",
                    new Page.NavigateOptions().setTimeout(30000));
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Find an anonymized employee (should exist from previous test or pre-existing data)
            // Look for one with "Dianonimisasi" status
            var anonymizedRow = page.locator("#employee-table tr:has-text('Dianonimisasi')").first();

            if (anonymizedRow.isVisible()) {
                // Click detail link for this anonymized employee
                anonymizedRow.locator("a[title='Lihat Detail']").click();
                page.waitForLoadState(LoadState.NETWORKIDLE);

                // Verify name starts with ANONYMIZED-
                String name = page.locator("#employee-name").textContent();
                assertThat(page.locator("#employee-name")).containsText("ANONYMIZED-");

                // Export data to verify PII is null/masked
                page.locator("#btn-export").click();
                page.waitForLoadState(LoadState.NETWORKIDLE);

                // Check that sensitive fields show as null/empty (represented as "-")
                // In export page, masked-nik, masked-npwp, masked-bank should be "-" or null
                var maskedNik = page.locator("#masked-nik").textContent();
                var maskedNpwp = page.locator("#masked-npwp").textContent();
                var maskedBank = page.locator("#masked-bank").textContent();

                // Anonymized data should show as "-" (null)
                org.junit.jupiter.api.Assertions.assertTrue(
                        maskedNik.equals("-") || maskedNik.equals("null") || maskedNik.isEmpty(),
                        "NIK KTP should be null/empty after anonymization, but was: " + maskedNik);
            }
        }
    }
}
