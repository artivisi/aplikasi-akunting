package com.artivisi.accountingfinance.manual;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Captures printable PDF samples of reports and invoice for user manual.
 * Used during GitHub Actions build to generate downloadable samples.
 */
public class PrintableSamplesCapture {

    private final String baseUrl;
    private final Path samplesDir;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Test data invoice ID from V906
    private static final String INVOICE_ID = "f0600000-0000-0000-0000-000000000001";

    public PrintableSamplesCapture(String baseUrl, Path samplesDir) {
        this.baseUrl = baseUrl;
        this.samplesDir = samplesDir;
    }

    public record PrintableDefinition(
            String id,
            String name,
            String url,
            String description
    ) {}

    public List<PrintableDefinition> getPrintableDefinitions() {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        String todayStr = today.format(DATE_FORMAT);
        String startStr = startOfMonth.format(DATE_FORMAT);

        return List.of(
            new PrintableDefinition(
                "invoice-print",
                "Contoh Invoice",
                "/invoices/" + INVOICE_ID + "/print",
                "Contoh cetakan invoice dengan kop surat perusahaan"
            ),
            new PrintableDefinition(
                "trial-balance-print",
                "Contoh Neraca Saldo",
                "/reports/trial-balance/print?asOfDate=" + todayStr,
                "Contoh cetakan neraca saldo"
            ),
            new PrintableDefinition(
                "balance-sheet-print",
                "Contoh Neraca",
                "/reports/balance-sheet/print?asOfDate=" + todayStr,
                "Contoh cetakan neraca (laporan posisi keuangan)"
            ),
            new PrintableDefinition(
                "income-statement-print",
                "Contoh Laba Rugi",
                "/reports/income-statement/print?startDate=" + startStr + "&endDate=" + todayStr,
                "Contoh cetakan laporan laba rugi"
            )
        );
    }

    /**
     * Captures PDF samples of all printable pages
     */
    public void captureAll() {
        samplesDir.toFile().mkdirs();

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(true)
            );

            BrowserContext context = browser.newContext(
                    new Browser.NewContextOptions()
                            .setViewportSize(1280, 800)
                            .setLocale("id-ID")
            );

            Page page = context.newPage();

            // Login first
            if (login(page)) {
                System.out.println("Login successful");
            } else {
                System.err.println("Login failed!");
                return;
            }

            // Capture each printable
            for (PrintableDefinition printable : getPrintableDefinitions()) {
                capturePdf(page, printable);
            }

            browser.close();
        }

        System.out.println("Printable samples capture complete!");
    }

    private boolean login(Page page) {
        try {
            page.navigate(baseUrl + "/login");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            page.fill("input[name='username']", "admin");
            page.fill("input[name='password']", "admin");
            page.click("button[type='submit']");

            page.waitForURL("**/dashboard", new Page.WaitForURLOptions().setTimeout(10000));
            return true;
        } catch (Exception e) {
            System.err.println("Login failed: " + e.getMessage());
            return false;
        }
    }

    private void capturePdf(Page page, PrintableDefinition printable) {
        System.out.printf("Capturing PDF: %s (%s)%n", printable.name(), printable.url());

        try {
            page.navigate(baseUrl + printable.url());
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Wait for page to stabilize
            page.waitForTimeout(500);

            // Generate PDF
            Path pdfPath = samplesDir.resolve(printable.id() + ".pdf");
            page.pdf(new Page.PdfOptions()
                    .setPath(pdfPath)
                    .setFormat("A4")
                    .setPrintBackground(true));

            System.out.printf("  Saved: %s%n", pdfPath);
        } catch (Exception e) {
            System.err.printf("  Failed to capture %s: %s%n", printable.name(), e.getMessage());
        }
    }

    public static void main(String[] args) {
        String baseUrl = System.getenv().getOrDefault("APP_URL", "http://localhost:8080");
        Path samplesDir = Paths.get("target", "user-manual", "samples");

        PrintableSamplesCapture capture = new PrintableSamplesCapture(baseUrl, samplesDir);
        capture.captureAll();
    }
}
