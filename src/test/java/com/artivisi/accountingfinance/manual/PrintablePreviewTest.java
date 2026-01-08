package com.artivisi.accountingfinance.manual;

import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import com.microsoft.playwright.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Generate PDF previews of all printable templates for user manual.
 * Output is saved to target/user-manual/samples/
 */
@DisplayName("Generate Printable PDF Previews")
class PrintablePreviewTest extends PlaywrightTestBase {

    private static final Path OUTPUT_DIR = Paths.get("target", "user-manual", "samples");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @BeforeEach
    void setUp() throws Exception {
        Files.createDirectories(OUTPUT_DIR);
        loginAsAdmin();
    }

    @Test
    @DisplayName("Generate Invoice Print Preview")
    void generateInvoicePrintPreview() throws Exception {
        // Use the known invoice number from V906 test data
        String invoiceNumber = "INV-2024-001";
        navigateTo("/invoices/" + invoiceNumber + "/print");
        waitForPageLoad();
        page.waitForTimeout(500);

        Path pdfPath = generatePdf("invoice-print.pdf");
        Path screenshotPath = savePreviewScreenshot("invoice-print.png");

        assertThat(pdfPath).exists().isNotEmptyFile();
        assertThat(screenshotPath).exists().isNotEmptyFile();
    }

    @Test
    @DisplayName("Generate Balance Sheet Print Preview")
    void generateBalanceSheetPrintPreview() throws Exception {
        String today = LocalDate.now().format(DATE_FORMAT);
        navigateTo("/reports/balance-sheet/print?asOfDate=" + today);
        waitForPageLoad();
        page.waitForTimeout(500);

        Path pdfPath = generatePdf("balance-sheet-print.pdf");
        Path screenshotPath = savePreviewScreenshot("balance-sheet-print.png");

        assertThat(pdfPath).exists().isNotEmptyFile();
        assertThat(screenshotPath).exists().isNotEmptyFile();
    }

    @Test
    @DisplayName("Generate Income Statement Print Preview")
    void generateIncomeStatementPrintPreview() throws Exception {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        String startDate = startOfMonth.format(DATE_FORMAT);
        String endDate = today.format(DATE_FORMAT);

        navigateTo("/reports/income-statement/print?startDate=" + startDate + "&endDate=" + endDate);
        waitForPageLoad();
        page.waitForTimeout(500);

        Path pdfPath = generatePdf("income-statement-print.pdf");
        Path screenshotPath = savePreviewScreenshot("income-statement-print.png");

        assertThat(pdfPath).exists().isNotEmptyFile();
        assertThat(screenshotPath).exists().isNotEmptyFile();
    }

    @Test
    @DisplayName("Generate Trial Balance Print Preview")
    void generateTrialBalancePrintPreview() throws Exception {
        String today = LocalDate.now().format(DATE_FORMAT);
        navigateTo("/reports/trial-balance/print?asOfDate=" + today);
        waitForPageLoad();
        page.waitForTimeout(500);

        Path pdfPath = generatePdf("trial-balance-print.pdf");
        Path screenshotPath = savePreviewScreenshot("trial-balance-print.png");

        assertThat(pdfPath).exists().isNotEmptyFile();
        assertThat(screenshotPath).exists().isNotEmptyFile();
    }

    private Path generatePdf(String filename) {
        Path pdfPath = OUTPUT_DIR.resolve(filename);
        page.pdf(new Page.PdfOptions()
                .setPath(pdfPath)
                .setFormat("A4")
                .setPrintBackground(true));
        System.out.println("Generated PDF: " + pdfPath);
        return pdfPath;
    }

    private Path savePreviewScreenshot(String filename) {
        Path screenshotPath = OUTPUT_DIR.resolve(filename);
        page.screenshot(new Page.ScreenshotOptions()
                .setPath(screenshotPath)
                .setFullPage(true));
        System.out.println("Generated Screenshot: " + screenshotPath);
        return screenshotPath;
    }
}
