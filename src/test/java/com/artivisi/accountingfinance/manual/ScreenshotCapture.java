package com.artivisi.accountingfinance.manual;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Captures screenshots of application pages using Playwright.
 * Used for generating user manual documentation.
 */
public class ScreenshotCapture {

    private final String baseUrl;
    private final Path screenshotsDir;

    public ScreenshotCapture(String baseUrl, Path screenshotsDir) {
        this.baseUrl = baseUrl;
        this.screenshotsDir = screenshotsDir;
    }

    /**
     * Page definitions for screenshot capture
     */
    public record PageDefinition(
            String id,
            String name,
            String url,
            boolean requiresAuth,
            String description,
            String section
    ) {}

    public static List<PageDefinition> getPageDefinitions() {
        return List.of(
            // Authentication
            new PageDefinition("login", "Halaman Login", "/login", false,
                    "Halaman login untuk masuk ke aplikasi", "login"),

            // Dashboard
            new PageDefinition("dashboard", "Dashboard", "/dashboard", true,
                    "Tampilan utama dengan ringkasan keuangan", "dashboard"),

            // Chart of Accounts
            new PageDefinition("accounts-list", "Daftar Akun", "/accounts", true,
                    "Daftar semua akun dalam bagan akun", "bagan-akun"),
            new PageDefinition("accounts-form", "Form Akun", "/accounts/new", true,
                    "Form untuk menambah atau mengubah akun", "bagan-akun"),

            // Templates
            new PageDefinition("templates-list", "Daftar Template", "/templates", true,
                    "Daftar template jurnal dengan kategori", "template-jurnal"),
            new PageDefinition("templates-detail", "Detail Template", "/templates/TPL-001", true,
                    "Konfigurasi dan formula template", "template-jurnal"),
            new PageDefinition("templates-form", "Form Template", "/templates/new", true,
                    "Form untuk membuat template baru", "template-jurnal"),

            // Transactions
            new PageDefinition("transactions-list", "Daftar Transaksi", "/transactions", true,
                    "Daftar transaksi dengan filter status dan periode", "transaksi"),
            new PageDefinition("transactions-form", "Form Transaksi", "/transactions/new", true,
                    "Form untuk membuat transaksi baru", "transaksi"),
            new PageDefinition("transactions-detail", "Detail Transaksi", "/transactions/TRX-2025-0001", true,
                    "Detail transaksi dengan jurnal dan audit trail", "transaksi"),

            // Journal Entries
            new PageDefinition("journals-list", "Buku Besar", "/journals", true,
                    "Tampilan buku besar dengan saldo berjalan", "buku-besar"),
            new PageDefinition("journals-detail", "Detail Jurnal", "/journals/JE-2025-0001", true,
                    "Detail entri jurnal dengan dampak akun", "buku-besar"),

            // Reports
            new PageDefinition("reports-index", "Halaman Laporan", "/reports", true,
                    "Indeks laporan keuangan yang tersedia", "laporan"),
            new PageDefinition("reports-trial-balance", "Neraca Saldo", "/reports/trial-balance", true,
                    "Laporan neraca saldo untuk verifikasi keseimbangan", "laporan"),
            new PageDefinition("reports-income-statement", "Laporan Laba Rugi", "/reports/income-statement", true,
                    "Laporan laba rugi dengan pendapatan dan beban", "laporan"),
            new PageDefinition("reports-balance-sheet", "Neraca", "/reports/balance-sheet", true,
                    "Laporan posisi keuangan (aset, liabilitas, ekuitas)", "laporan"),
            new PageDefinition("reports-cash-flow", "Laporan Arus Kas", "/reports/cash-flow", true,
                    "Laporan arus kas operasional, investasi, pendanaan", "laporan")
        );
    }

    /**
     * Captures screenshots of all defined pages
     */
    public void captureAll() {
        screenshotsDir.toFile().mkdirs();

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
                System.out.println("Login failed, continuing with unauthenticated pages only");
            }

            // Capture each page
            for (PageDefinition pageDef : getPageDefinitions()) {
                capturePageScreenshot(page, pageDef);
            }

            browser.close();
        }

        System.out.println("Screenshot capture complete!");
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

    private void capturePageScreenshot(Page page, PageDefinition pageDef) {
        System.out.printf("Capturing: %s (%s)%n", pageDef.name(), pageDef.url());

        try {
            page.navigate(baseUrl + pageDef.url());
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Wait for page to stabilize
            page.waitForTimeout(500);

            // Take screenshot
            Path screenshotPath = screenshotsDir.resolve(pageDef.id() + ".png");
            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(screenshotPath)
                    .setFullPage(false));

            System.out.printf("  Saved: %s%n", screenshotPath);
        } catch (Exception e) {
            System.err.printf("  Failed to capture %s: %s%n", pageDef.name(), e.getMessage());
        }
    }

    public static void main(String[] args) {
        String baseUrl = System.getenv().getOrDefault("APP_URL", "http://localhost:8080");
        Path screenshotsDir = Paths.get("target", "user-manual", "screenshots");

        ScreenshotCapture capture = new ScreenshotCapture(baseUrl, screenshotsDir);
        capture.captureAll();
    }
}
