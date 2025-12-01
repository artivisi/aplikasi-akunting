package com.artivisi.accountingfinance.functional.page;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class TaxExportPage {

    private final Page page;
    private final String baseUrl;

    // Locators
    private static final String PAGE_TITLE = "#page-title";
    private static final String START_MONTH = "#startMonth";
    private static final String END_MONTH = "#endMonth";
    private static final String BTN_SUBMIT = "button:has-text('Tampilkan')";

    // Card locators for statistics
    private static final String FAKTUR_KELUARAN_COUNT = ".bg-green-100";
    private static final String FAKTUR_MASUKAN_COUNT = ".bg-blue-100";
    private static final String BUPOT_COUNT = ".bg-yellow-100";

    // Download buttons
    private static final String BTN_DOWNLOAD_FAKTUR_KELUARAN = "#btn-download-efaktur-keluaran";
    private static final String BTN_DOWNLOAD_FAKTUR_MASUKAN = "#btn-download-efaktur-masukan";
    private static final String BTN_DOWNLOAD_BUPOT = "#btn-download-bupot-unifikasi";

    public TaxExportPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public TaxExportPage navigate() {
        page.navigate(baseUrl + "/reports/tax-export",
            new Page.NavigateOptions().setTimeout(30000));
        page.waitForLoadState();
        return this;
    }

    public TaxExportPage navigateWithMonth(YearMonth month) {
        String monthStr = month.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        page.navigate(baseUrl + "/reports/tax-export?startMonth=" + monthStr + "&endMonth=" + monthStr,
            new Page.NavigateOptions().setTimeout(30000));
        page.waitForLoadState();
        return this;
    }

    // Actions
    public void selectStartMonth(String month) {
        page.selectOption(START_MONTH, month);
    }

    public void selectEndMonth(String month) {
        page.selectOption(END_MONTH, month);
    }

    public void clickSubmit() {
        page.click(BTN_SUBMIT);
        page.waitForLoadState();
    }

    // Assertions - Page Elements
    public void assertPageTitleVisible() {
        assertThat(page.locator(PAGE_TITLE)).isVisible();
    }

    public void assertPageTitleText(String expected) {
        assertThat(page.locator(PAGE_TITLE)).hasText(expected);
    }

    public void assertStartMonthVisible() {
        assertThat(page.locator(START_MONTH)).isVisible();
    }

    public void assertEndMonthVisible() {
        assertThat(page.locator(END_MONTH)).isVisible();
    }

    public void assertSubmitButtonVisible() {
        assertThat(page.locator(BTN_SUBMIT)).isVisible();
    }

    // Statistics assertions
    public String getFakturKeluaranCountText() {
        return page.locator(FAKTUR_KELUARAN_COUNT).textContent().trim();
    }

    public String getFakturMasukanCountText() {
        return page.locator(FAKTUR_MASUKAN_COUNT).textContent().trim();
    }

    public String getBupotCountText() {
        return page.locator(BUPOT_COUNT).textContent().trim();
    }

    public void assertFakturKeluaranCount(int expected) {
        assertThat(page.locator(FAKTUR_KELUARAN_COUNT)).hasText(expected + " faktur");
    }

    public void assertFakturMasukanCount(int expected) {
        assertThat(page.locator(FAKTUR_MASUKAN_COUNT)).hasText(expected + " faktur");
    }

    public void assertBupotCount(int expected) {
        assertThat(page.locator(BUPOT_COUNT)).hasText(expected + " bupot");
    }

    // Download button assertions
    public void assertDownloadFakturKeluaranEnabled() {
        assertThat(page.locator(BTN_DOWNLOAD_FAKTUR_KELUARAN)).not().hasClass(".*pointer-events-none.*");
    }

    public void assertDownloadFakturMasukanEnabled() {
        assertThat(page.locator(BTN_DOWNLOAD_FAKTUR_MASUKAN)).not().hasClass(".*pointer-events-none.*");
    }

    public void assertDownloadBupotEnabled() {
        assertThat(page.locator(BTN_DOWNLOAD_BUPOT)).not().hasClass(".*pointer-events-none.*");
    }

    public void assertDownloadFakturKeluaranDisabled() {
        assertThat(page.locator(BTN_DOWNLOAD_FAKTUR_KELUARAN)).hasClass(".*pointer-events-none.*");
    }

    public void assertDownloadFakturMasukanDisabled() {
        assertThat(page.locator(BTN_DOWNLOAD_FAKTUR_MASUKAN)).hasClass(".*pointer-events-none.*");
    }

    public void assertDownloadBupotDisabled() {
        assertThat(page.locator(BTN_DOWNLOAD_BUPOT)).hasClass(".*pointer-events-none.*");
    }

    // Get download URLs
    public String getFakturKeluaranDownloadUrl() {
        return page.locator(BTN_DOWNLOAD_FAKTUR_KELUARAN).getAttribute("href");
    }

    public String getFakturMasukanDownloadUrl() {
        return page.locator(BTN_DOWNLOAD_FAKTUR_MASUKAN).getAttribute("href");
    }

    public String getBupotDownloadUrl() {
        return page.locator(BTN_DOWNLOAD_BUPOT).getAttribute("href");
    }

    // Download actions - returns response bytes
    public byte[] downloadFakturKeluaran() {
        var download = page.waitForDownload(() -> {
            page.click(BTN_DOWNLOAD_FAKTUR_KELUARAN);
        });
        try {
            return java.nio.file.Files.readAllBytes(download.path());
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to read download", e);
        }
    }

    public byte[] downloadFakturMasukan() {
        var download = page.waitForDownload(() -> {
            page.click(BTN_DOWNLOAD_FAKTUR_MASUKAN);
        });
        try {
            return java.nio.file.Files.readAllBytes(download.path());
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to read download", e);
        }
    }

    public byte[] downloadBupot() {
        var download = page.waitForDownload(() -> {
            page.click(BTN_DOWNLOAD_BUPOT);
        });
        try {
            return java.nio.file.Files.readAllBytes(download.path());
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to read download", e);
        }
    }
}
