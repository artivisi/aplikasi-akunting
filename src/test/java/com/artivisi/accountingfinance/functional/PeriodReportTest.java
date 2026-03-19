package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@DisplayName("Period Report Tests")
@Import(ServiceTestDataInitializer.class)
class PeriodReportTest extends PlaywrightTestBase {

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    @Test
    @DisplayName("Should display period selector with quick presets")
    void shouldDisplayPeriodSelector() {
        navigateTo("/reports/period");
        waitForPageLoad();

        assertThat(page.locator("#page-title")).hasText("Laporan Periode");
        assertThat(page.locator("#startDate")).isVisible();
        assertThat(page.locator("#endDate")).isVisible();
        assertThat(page.locator("#btn-generate")).isVisible();
        assertThat(page.locator("button.period-preset:has-text('Tahun')").first()).isVisible();
        assertThat(page.locator("text=Pilih periode untuk menampilkan laporan keuangan")).isVisible();
    }

    @Test
    @DisplayName("Should generate yearly report when clicking Tahun preset")
    void shouldGenerateYearlyReportViaPreset() {
        navigateTo("/reports/period");
        waitForPageLoad();

        page.locator("button.period-preset:has-text('Tahun')").first().click();
        waitForPageLoad();

        assertThat(page.locator("text=LAPORAN LABA RUGI")).isVisible();
        assertThat(page.locator("text=LAPORAN POSISI KEUANGAN")).isVisible();
        assertThat(page.locator("#startDate")).not().hasValue("");
        assertThat(page.locator("#endDate")).not().hasValue("");
    }

    @Test
    @DisplayName("Should generate quarterly report when clicking Q1 preset")
    void shouldGenerateQuarterlyReportViaPreset() {
        navigateTo("/reports/period");
        waitForPageLoad();

        page.locator("button.period-preset:has-text('Q1')").first().click();
        waitForPageLoad();

        assertThat(page.locator("text=LAPORAN LABA RUGI")).isVisible();
        assertThat(page.locator("text=LAPORAN POSISI KEUANGAN")).isVisible();
    }

    @Test
    @DisplayName("Should expand monthly buttons and generate monthly report")
    void shouldGenerateMonthlyReportViaPreset() {
        navigateTo("/reports/period");
        waitForPageLoad();

        page.locator("button.month-toggle").first().click();

        var janButton = page.locator("button.period-preset:has-text('Jan')").first();
        assertThat(janButton).isVisible();

        janButton.click();
        waitForPageLoad();

        assertThat(page.locator("text=LAPORAN LABA RUGI")).isVisible();
        assertThat(page.locator("text=LAPORAN POSISI KEUANGAN")).isVisible();
    }

    @Test
    @DisplayName("Should generate report via manual date input")
    void shouldGenerateReportViaManualInput() {
        navigateTo("/reports/period");
        waitForPageLoad();

        page.locator("#startDate").fill("2025-01-01");
        page.locator("#endDate").fill("2025-12-31");
        page.locator("#btn-generate").click();
        waitForPageLoad();

        assertThat(page.locator("text=LAPORAN LABA RUGI")).isVisible();
        assertThat(page.locator("text=LAPORAN POSISI KEUANGAN")).isVisible();
        assertThat(page.locator("#startDate")).hasValue("2025-01-01");
        assertThat(page.locator("#endDate")).hasValue("2025-12-31");
    }
}
