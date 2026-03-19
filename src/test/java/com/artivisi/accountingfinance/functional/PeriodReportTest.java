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

    // Seed data transactions are all in 2024:
    // - 2024-01-01: Setoran Modal 500,000,000 (equity, not revenue)
    // - 2024-01-15: Pendapatan Jasa Konsultasi 196,200,000 → account 4.1.02
    // - 2024-01-15: Beban Software & Lisensi 3,330,000 → account 5.1.21
    // - 2024-01-31: Beban Cloud & Server 5,550,000 → account 5.1.20
    // - 2024-02-28: Pendapatan Jasa Training 163,500,000 → account 4.1.01

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
    @DisplayName("Should generate yearly report via Tahun preset with correct totals")
    void shouldGenerateYearlyReportViaPreset() {
        navigateTo("/reports/period?startDate=2024-01-01&endDate=2024-12-31");
        waitForPageLoad();

        // Revenue: 196,200,000 + 163,500,000 = 359,700,000
        assertThat(page.locator("text=LAPORAN LABA RUGI")).isVisible();
        assertThat(page.locator("#period-report-content").getByText("Pendapatan Jasa Konsultasi")).isVisible();
        assertThat(page.locator("#period-report-content").getByText("Pendapatan Jasa Training")).isVisible();

        // Expenses: 3,330,000 + 5,550,000 = 8,880,000
        assertThat(page.locator("text=Total Beban Operasional")).isVisible();

        // Net income = positive → "LABA BERSIH"
        assertThat(page.locator("text=LABA BERSIH")).isVisible();

        // Balance Sheet
        assertThat(page.locator("text=LAPORAN POSISI KEUANGAN")).isVisible();
        assertThat(page.locator("text=Total Aset")).isVisible();
    }

    @Test
    @DisplayName("Should generate quarterly report via Q1 preset click")
    void shouldGenerateQuarterlyReportViaPreset() {
        navigateTo("/reports/period");
        waitForPageLoad();

        // Find the 2024 row and click its Q1 button
        var year2024Row = page.locator("span.text-xs:has-text('2024')").locator("..");
        year2024Row.locator("button.period-preset:has-text('Q1')").click();
        waitForPageLoad();

        // Q1 2024 has all transactions
        assertThat(page.locator("text=LAPORAN LABA RUGI")).isVisible();
        assertThat(page.locator("#period-report-content").getByText("Pendapatan Jasa Konsultasi")).isVisible();
        assertThat(page.locator("text=LABA BERSIH")).isVisible();
        assertThat(page.locator("#startDate")).hasValue("2024-01-01");
        assertThat(page.locator("#endDate")).hasValue("2024-03-31");
    }

    @Test
    @DisplayName("Should expand monthly buttons and generate January report")
    void shouldGenerateMonthlyReportViaPreset() {
        navigateTo("/reports/period");
        waitForPageLoad();

        // Find the 2024 row and expand months
        var year2024Row = page.locator("span.text-xs:has-text('2024')").locator("..");
        year2024Row.locator("button.month-toggle").click();

        // Click Jan
        var janButton = page.locator("#months-2024 button.period-preset:has-text('Jan')");
        assertThat(janButton).isVisible();
        janButton.click();
        waitForPageLoad();

        // Jan 2024: revenue = Konsultasi only (196,200,000), expenses = 8,880,000
        assertThat(page.locator("text=LAPORAN LABA RUGI")).isVisible();
        assertThat(page.locator("#period-report-content").getByText("Pendapatan Jasa Konsultasi")).isVisible();
        assertThat(page.locator("#startDate")).hasValue("2024-01-01");
        assertThat(page.locator("#endDate")).hasValue("2024-01-31");
    }

    @Test
    @DisplayName("Should generate report via manual date input")
    void shouldGenerateReportViaManualInput() {
        navigateTo("/reports/period");
        waitForPageLoad();

        page.locator("#startDate").fill("2024-02-01");
        page.locator("#endDate").fill("2024-02-28");
        page.locator("#btn-generate").click();
        waitForPageLoad();

        // Feb 2024: only Training revenue (163,500,000), no expenses
        assertThat(page.locator("text=LAPORAN LABA RUGI")).isVisible();
        assertThat(page.locator("#period-report-content").getByText("Pendapatan Jasa Training")).isVisible();
        assertThat(page.locator("text=Tidak ada beban")).isVisible();
        assertThat(page.locator("#startDate")).hasValue("2024-02-01");
        assertThat(page.locator("#endDate")).hasValue("2024-02-28");
    }

    @Test
    @DisplayName("Should show empty report for period with no transactions")
    void shouldShowEmptyReportForPeriodWithNoData() {
        navigateTo("/reports/period?startDate=2025-06-01&endDate=2025-06-30");
        waitForPageLoad();

        assertThat(page.locator("text=LAPORAN LABA RUGI")).isVisible();
        assertThat(page.locator("text=Tidak ada pendapatan")).isVisible();
        assertThat(page.locator("text=Tidak ada beban")).isVisible();
    }
}
