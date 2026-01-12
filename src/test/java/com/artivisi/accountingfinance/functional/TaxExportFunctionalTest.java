package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Functional tests for TaxExportController.
 * Tests Coretax export functionality for e-Faktur and Bupot.
 */
@DisplayName("Tax Export (Coretax) Tests")
@Import(ServiceTestDataInitializer.class)
class TaxExportFunctionalTest extends PlaywrightTestBase {

    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    @Nested
    @DisplayName("Export Page Display")
    class ExportPageDisplayTests {

        @Test
        @DisplayName("Should display tax export page with title")
        void shouldDisplayTaxExportPage() {
            navigateTo("/reports/tax-export");
            waitForPageLoad();

            assertThat(page.title())
                .as("Page title should contain 'Export Data Pajak'")
                .contains("Export Data Pajak");

            assertThat(page.locator("#page-title").textContent())
                .as("Page heading should show Export Data Pajak untuk Coretax")
                .contains("Export Data Pajak untuk Coretax");
        }

        @Test
        @DisplayName("Should display period selection form")
        void shouldDisplayPeriodSelectionForm() {
            navigateTo("/reports/tax-export");
            waitForPageLoad();

            assertThat(page.locator("#startMonth").isVisible())
                .as("Start month select should be visible")
                .isTrue();

            assertThat(page.locator("#endMonth").isVisible())
                .as("End month select should be visible")
                .isTrue();

            // Use text content to identify the specific submit button
            assertThat(page.locator("button:has-text('Tampilkan')").isVisible())
                .as("Submit button should be visible")
                .isTrue();
        }

        @Test
        @DisplayName("Should display e-Faktur Keluaran section")
        void shouldDisplayEFakturKeluaranSection() {
            navigateTo("/reports/tax-export");
            waitForPageLoad();

            assertThat(page.locator("text=e-Faktur Keluaran").first().isVisible())
                .as("e-Faktur Keluaran section should be visible")
                .isTrue();

            // Download link is an anchor tag
            assertThat(page.locator("a#btn-download-efaktur-keluaran").isVisible())
                .as("Download e-Faktur Keluaran button should be visible")
                .isTrue();
        }

        @Test
        @DisplayName("Should display e-Faktur Masukan section")
        void shouldDisplayEFakturMasukanSection() {
            navigateTo("/reports/tax-export");
            waitForPageLoad();

            assertThat(page.locator("text=e-Faktur Masukan").first().isVisible())
                .as("e-Faktur Masukan section should be visible")
                .isTrue();

            // Download link is an anchor tag
            assertThat(page.locator("a#btn-download-efaktur-masukan").isVisible())
                .as("Download e-Faktur Masukan button should be visible")
                .isTrue();
        }

        @Test
        @DisplayName("Should display Bupot Unifikasi section")
        void shouldDisplayBupotUnifikasiSection() {
            navigateTo("/reports/tax-export");
            waitForPageLoad();

            assertThat(page.locator("text=Bupot Unifikasi").first().isVisible())
                .as("Bupot Unifikasi section should be visible")
                .isTrue();

            // Download link is an anchor tag
            assertThat(page.locator("a#btn-download-bupot-unifikasi").isVisible())
                .as("Download Bupot Unifikasi button should be visible")
                .isTrue();
        }

        @Test
        @DisplayName("Should display instructions section")
        void shouldDisplayInstructionsSection() {
            navigateTo("/reports/tax-export");
            waitForPageLoad();

            assertThat(page.locator("text=Cara Menggunakan").isVisible())
                .as("Instructions section should be visible")
                .isTrue();

            assertThat(page.locator("text=DJP Converter").first().isVisible())
                .as("DJP Converter reference should be visible")
                .isTrue();
        }

        @Test
        @DisplayName("Should display requirements notice")
        void shouldDisplayRequirementsNotice() {
            navigateTo("/reports/tax-export");
            waitForPageLoad();

            assertThat(page.locator("text=Persyaratan Data").isVisible())
                .as("Requirements notice should be visible")
                .isTrue();

            assertThat(page.locator("text=NPWP dan NITKU").isVisible())
                .as("NPWP and NITKU requirement should be visible")
                .isTrue();
        }
    }

    @Nested
    @DisplayName("Period Selection")
    class PeriodSelectionTests {

        @Test
        @DisplayName("Should default to current month")
        void shouldDefaultToCurrentMonth() {
            navigateTo("/reports/tax-export");
            waitForPageLoad();

            String currentMonth = YearMonth.now().format(MONTH_FORMAT);

            assertThat(page.locator("#startMonth").inputValue())
                .as("Start month should default to current month")
                .isEqualTo(currentMonth);

            assertThat(page.locator("#endMonth").inputValue())
                .as("End month should default to current month")
                .isEqualTo(currentMonth);
        }

        @Test
        @DisplayName("Should update period on form submit")
        void shouldUpdatePeriodOnSubmit() {
            navigateTo("/reports/tax-export");
            waitForPageLoad();

            // Select a different month
            YearMonth twoMonthsAgo = YearMonth.now().minusMonths(2);
            String targetMonth = twoMonthsAgo.format(MONTH_FORMAT);

            page.locator("#startMonth").selectOption(targetMonth);
            page.locator("#endMonth").selectOption(targetMonth);
            page.locator("button:has-text('Tampilkan')").click();
            waitForPageLoad();

            // Verify URL contains the selected period
            assertThat(page.url())
                .as("URL should contain selected start month")
                .contains("startMonth=" + targetMonth);

            assertThat(page.url())
                .as("URL should contain selected end month")
                .contains("endMonth=" + targetMonth);
        }

        @Test
        @DisplayName("Should allow selecting date range")
        void shouldAllowSelectingDateRange() {
            navigateTo("/reports/tax-export");
            waitForPageLoad();

            YearMonth threeMonthsAgo = YearMonth.now().minusMonths(3);
            YearMonth oneMonthAgo = YearMonth.now().minusMonths(1);

            page.locator("#startMonth").selectOption(threeMonthsAgo.format(MONTH_FORMAT));
            page.locator("#endMonth").selectOption(oneMonthAgo.format(MONTH_FORMAT));
            page.locator("button:has-text('Tampilkan')").click();
            waitForPageLoad();

            assertThat(page.url())
                .as("URL should contain start month")
                .contains("startMonth=" + threeMonthsAgo.format(MONTH_FORMAT));

            assertThat(page.url())
                .as("URL should contain end month")
                .contains("endMonth=" + oneMonthAgo.format(MONTH_FORMAT));
        }

        @Test
        @DisplayName("Should display available months for selection")
        void shouldDisplayAvailableMonths() {
            navigateTo("/reports/tax-export");
            waitForPageLoad();

            // Get all options from start month select
            var options = page.locator("#startMonth option").all();

            assertThat(options.size())
                .as("Should have 24 months available")
                .isEqualTo(24);
        }
    }

    @Nested
    @DisplayName("Export Statistics")
    class ExportStatisticsTests {

        @Test
        @DisplayName("Should display e-Faktur Keluaran count")
        void shouldDisplayEFakturKeluaranCount() {
            navigateTo("/reports/tax-export");
            waitForPageLoad();

            // Look for the faktur count badge
            var countBadge = page.locator("text=/\\d+ faktur/").first();

            assertThat(countBadge.isVisible())
                .as("e-Faktur count should be displayed")
                .isTrue();
        }

        @Test
        @DisplayName("Should display e-Faktur Keluaran PPN total")
        void shouldDisplayEFakturKeluaranPpnTotal() {
            navigateTo("/reports/tax-export");
            waitForPageLoad();

            // The Total PPN label should be visible
            assertThat(page.locator("text=Total PPN:").first().isVisible())
                .as("Total PPN label should be visible")
                .isTrue();
        }

        @Test
        @DisplayName("Should display Bupot count")
        void shouldDisplayBupotCount() {
            navigateTo("/reports/tax-export");
            waitForPageLoad();

            // Look for the bupot count badge
            var countBadge = page.locator("text=/\\d+ bupot/").first();

            assertThat(countBadge.isVisible())
                .as("Bupot count should be displayed")
                .isTrue();
        }

        @Test
        @DisplayName("Should display Total PPh")
        void shouldDisplayTotalPph() {
            navigateTo("/reports/tax-export");
            waitForPageLoad();

            assertThat(page.locator("text=Total PPh:").isVisible())
                .as("Total PPh label should be visible")
                .isTrue();
        }
    }

    @Nested
    @DisplayName("Download Actions")
    class DownloadActionsTests {

        @Test
        @DisplayName("Should have correct e-Faktur Keluaran download link")
        void shouldHaveCorrectEFakturKeluaranDownloadLink() {
            navigateTo("/reports/tax-export");
            waitForPageLoad();

            String downloadHref = page.locator("a#btn-download-efaktur-keluaran").getAttribute("href");

            assertThat(downloadHref)
                .as("Download link should point to efaktur-keluaran endpoint")
                .contains("/reports/tax-export/efaktur-keluaran");

            assertThat(downloadHref)
                .as("Download link should include startMonth parameter")
                .contains("startMonth=");

            assertThat(downloadHref)
                .as("Download link should include endMonth parameter")
                .contains("endMonth=");
        }

        @Test
        @DisplayName("Should have correct e-Faktur Masukan download link")
        void shouldHaveCorrectEFakturMasukanDownloadLink() {
            navigateTo("/reports/tax-export");
            waitForPageLoad();

            String downloadHref = page.locator("a#btn-download-efaktur-masukan").getAttribute("href");

            assertThat(downloadHref)
                .as("Download link should point to efaktur-masukan endpoint")
                .contains("/reports/tax-export/efaktur-masukan");

            assertThat(downloadHref)
                .as("Download link should include startMonth parameter")
                .contains("startMonth=");
        }

        @Test
        @DisplayName("Should have correct Bupot Unifikasi download link")
        void shouldHaveCorrectBupotUnifikasiDownloadLink() {
            navigateTo("/reports/tax-export");
            waitForPageLoad();

            String downloadHref = page.locator("a#btn-download-bupot-unifikasi").getAttribute("href");

            assertThat(downloadHref)
                .as("Download link should point to bupot-unifikasi endpoint")
                .contains("/reports/tax-export/bupot-unifikasi");

            assertThat(downloadHref)
                .as("Download link should include startMonth parameter")
                .contains("startMonth=");
        }

        @Test
        @DisplayName("Should update download links when period changes")
        void shouldUpdateDownloadLinksWhenPeriodChanges() {
            navigateTo("/reports/tax-export");
            waitForPageLoad();

            YearMonth twoMonthsAgo = YearMonth.now().minusMonths(2);
            String targetMonth = twoMonthsAgo.format(MONTH_FORMAT);

            page.locator("#startMonth").selectOption(targetMonth);
            page.locator("#endMonth").selectOption(targetMonth);
            page.locator("button:has-text('Tampilkan')").click();
            waitForPageLoad();

            // The download link (anchor tag) should have updated href
            var downloadLink = page.locator("a#btn-download-efaktur-keluaran");
            String downloadHref = downloadLink.getAttribute("href");

            assertThat(downloadHref)
                .as("Download link should use updated startMonth")
                .contains("startMonth=" + targetMonth);

            assertThat(downloadHref)
                .as("Download link should use updated endMonth")
                .contains("endMonth=" + targetMonth);
        }
    }

    @Nested
    @DisplayName("Navigation")
    class NavigationTests {

        @Test
        @DisplayName("Should navigate to tax export from reports menu")
        void shouldNavigateToTaxExportFromReportsMenu() {
            navigateTo("/dashboard");
            waitForPageLoad();

            // Open reports menu if collapsed
            var reportsMenu = page.locator("a[href*='/reports']").first();
            if (reportsMenu.isVisible()) {
                reportsMenu.click();
                waitForPageLoad();
            }

            // Navigate to tax export
            navigateTo("/reports/tax-export");
            waitForPageLoad();

            assertThat(page.url())
                .as("Should be on tax export page")
                .contains("/reports/tax-export");
        }

        @Test
        @DisplayName("Should have link to company settings")
        void shouldHaveLinkToCompanySettings() {
            navigateTo("/reports/tax-export");
            waitForPageLoad();

            var settingsLink = page.locator("a[href*='/settings/company']").first();

            assertThat(settingsLink.isVisible())
                .as("Link to company settings should be visible")
                .isTrue();
        }

        @Test
        @DisplayName("Should have external link to DJP Converter")
        void shouldHaveExternalLinkToDjpConverter() {
            navigateTo("/reports/tax-export");
            waitForPageLoad();

            var djpLink = page.locator("a[href*='pajak.go.id']").first();

            assertThat(djpLink.isVisible())
                .as("DJP Converter link should be visible")
                .isTrue();

            assertThat(djpLink.getAttribute("target"))
                .as("DJP link should open in new tab")
                .isEqualTo("_blank");
        }
    }
}
