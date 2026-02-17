package com.artivisi.accountingfinance.functional.seller;

import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Online Seller industry analysis report test.
 * Reads real financial data from API, computes e-commerce-specific KPIs, publishes report.
 * KPIs: Gross Margin, COGS ratio, Marketplace fee ratio, Inventory turnover.
 */
@Slf4j
@DisplayName("Analysis Report - Online Seller")
@Import(SellerTestDataInitializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SellerAnalysisReportTest extends PlaywrightTestBase {

    private APIRequestContext apiContext;
    private ObjectMapper objectMapper;
    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        // Device flow auth requires browser login during setUp — increase navigation timeout
        page.setDefaultNavigationTimeout(15000);

        apiContext = playwright.request().newContext(new APIRequest.NewContextOptions()
                .setBaseURL(baseUrl()));

        accessToken = authenticateViaDeviceFlow();
    }

    @AfterEach
    void tearDown() {
        if (apiContext != null) {
            apiContext.dispose();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Read financial data, compute e-commerce KPIs, publish and verify")
    void testPublishAndViewReportFromRealData() throws Exception {
        // Step 1: Read company info
        JsonNode companyBody = getApi("/api/analysis/company");
        JsonNode companyData = companyBody.get("data");
        String companyName = companyData.get("companyName").asText();
        String industry = companyData.get("industry").asText();
        assertThat(industry).isEqualTo("online-seller");
        log.info("Company: {} (industry: {})", companyName, industry);

        // Step 2: Read financial snapshot for January 2024
        JsonNode snapshotBody = getApi("/api/analysis/snapshot?month=2024-01");
        JsonNode snapshot = snapshotBody.get("data");
        BigDecimal revenue = snapshot.get("revenue").decimalValue();
        BigDecimal expense = snapshot.get("expense").decimalValue();
        BigDecimal netProfit = snapshot.get("netProfit").decimalValue();
        BigDecimal cashBalance = snapshot.get("cashBalance").decimalValue();
        log.info("Snapshot - revenue: {}, expense: {}, netProfit: {}, cashBalance: {}",
                revenue, expense, netProfit, cashBalance);

        assertThat(revenue).isGreaterThan(BigDecimal.ZERO);

        // Step 3: Read income statement for COGS and marketplace fee breakdown
        JsonNode incomeBody = getApi("/api/analysis/income-statement?startDate=2024-01-01&endDate=2024-01-31");
        JsonNode incomeData = incomeBody.get("data");
        BigDecimal totalRevenue = incomeData.get("totalRevenue").decimalValue();
        BigDecimal totalExpense = incomeData.get("totalExpense").decimalValue();
        JsonNode expenseItems = incomeData.get("expenseItems");

        // Separate COGS from operating expenses
        BigDecimal cogs = BigDecimal.ZERO;
        BigDecimal marketplaceFees = BigDecimal.ZERO;
        BigDecimal shippingCost = BigDecimal.ZERO;
        for (JsonNode item : expenseItems) {
            String code = item.get("accountCode").asText();
            BigDecimal balance = item.get("balance").decimalValue();
            if (code.startsWith("5.1")) {
                cogs = cogs.add(balance);
            } else if (code.startsWith("5.2")) {
                marketplaceFees = marketplaceFees.add(balance);
            } else if (code.startsWith("5.3")) {
                shippingCost = shippingCost.add(balance);
            }
        }

        BigDecimal grossProfit = totalRevenue.subtract(cogs);
        BigDecimal grossMargin = totalRevenue.compareTo(BigDecimal.ZERO) > 0
                ? grossProfit.multiply(BigDecimal.valueOf(100)).divide(totalRevenue, 1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal cogsRatio = totalRevenue.compareTo(BigDecimal.ZERO) > 0
                ? cogs.multiply(BigDecimal.valueOf(100)).divide(totalRevenue, 1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal feeRatio = totalRevenue.compareTo(BigDecimal.ZERO) > 0
                ? marketplaceFees.multiply(BigDecimal.valueOf(100)).divide(totalRevenue, 1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Step 4: Compute e-commerce KPIs
        List<Map<String, String>> metrics = List.of(
                Map.of("name", "Penjualan Bersih", "value", formatCurrency(totalRevenue), "status", "info"),
                Map.of("name", "HPP (Harga Pokok)", "value", formatCurrency(cogs), "status", "info"),
                Map.of("name", "Laba Kotor", "value", formatCurrency(grossProfit),
                        "status", grossProfit.compareTo(BigDecimal.ZERO) > 0 ? "positive" : "negative"),
                Map.of("name", "Gross Margin", "value", grossMargin + "%",
                        "status", grossMargin.compareTo(BigDecimal.valueOf(15)) >= 0 ? "positive" : "warning"),
                Map.of("name", "Biaya Marketplace", "value", formatCurrency(marketplaceFees),
                        "status", feeRatio.compareTo(BigDecimal.valueOf(5)) <= 0 ? "positive" : "warning"),
                Map.of("name", "Biaya Pengiriman", "value", formatCurrency(shippingCost), "status", "info")
        );

        // Step 5: Findings
        List<Map<String, String>> findings = new ArrayList<>();
        findings.add(Map.of("category", "profitabilitas", "severity",
                grossMargin.compareTo(BigDecimal.valueOf(15)) >= 0 ? "info" : "warning",
                "description", "Gross margin " + grossMargin + "% (HPP " + formatCurrency(cogs)
                        + " dari penjualan " + formatCurrency(totalRevenue) + "). "
                        + "Benchmark e-commerce: 15-30%."));
        findings.add(Map.of("category", "biaya-marketplace", "severity",
                feeRatio.compareTo(BigDecimal.valueOf(5)) <= 0 ? "info" : "warning",
                "description", "Total biaya marketplace " + formatCurrency(marketplaceFees)
                        + " (" + feeRatio + "% dari penjualan). Termasuk admin fee per platform."));
        findings.add(Map.of("category", "logistik", "severity", "info",
                "description", "Biaya pengiriman subsidi penjual " + formatCurrency(shippingCost)
                        + ". Evaluasi apakah free ongkir meningkatkan konversi secara proporsional."));

        // Step 6: Recommendations
        List<Map<String, String>> recommendations = new ArrayList<>();
        recommendations.add(Map.of("priority", "high",
                "description", "Optimalkan HPP melalui negosiasi volume dengan supplier. "
                        + "COGS ratio saat ini " + cogsRatio + "%. Target: di bawah 80%.",
                "impact", "Peningkatan gross margin"));
        recommendations.add(Map.of("priority", "medium",
                "description", "Evaluasi performa per platform marketplace. "
                        + "Bandingkan fee vs volume penjualan untuk alokasi budget iklan.",
                "impact", "Efisiensi biaya marketplace"));
        recommendations.add(Map.of("priority", "medium",
                "description", "Pertimbangkan bundling produk untuk meningkatkan average order value "
                        + "dan mengurangi rasio biaya pengiriman per transaksi.",
                "impact", "Peningkatan margin per order"));

        // Step 7: Risks
        List<Map<String, String>> risks = new ArrayList<>();
        risks.add(Map.of("severity", "high",
                "description", "Ketergantungan pada platform marketplace. Perubahan kebijakan fee atau algoritma "
                        + "dapat langsung berdampak pada profitabilitas.",
                "mitigation", "Bangun channel penjualan sendiri (website/social commerce) sebagai diversifikasi."));
        risks.add(Map.of("severity", "medium",
                "description", "Fluktuasi harga dari supplier dapat menekan margin. "
                        + "COGS ratio " + cogsRatio + "% relatif tinggi untuk e-commerce.",
                "mitigation", "Kontrak harga tetap dengan supplier utama, diversifikasi supplier."));

        // Step 8: Publish report
        Map<String, Object> reportRequest = new HashMap<>();
        reportRequest.put("title", "Review Keuangan Januari 2024 - " + companyName);
        reportRequest.put("reportType", "monthly-review");
        reportRequest.put("industry", industry);
        reportRequest.put("periodStart", "2024-01-01");
        reportRequest.put("periodEnd", "2024-01-31");
        reportRequest.put("aiSource", "claude-code");
        reportRequest.put("aiModel", "claude-opus-4-6");
        reportRequest.put("executiveSummary",
                "Review keuangan " + companyName + " periode Januari 2024. "
                + "Penjualan " + formatCurrency(totalRevenue) + " dengan HPP " + formatCurrency(cogs)
                + " menghasilkan gross margin " + grossMargin + "%. "
                + "Biaya marketplace " + formatCurrency(marketplaceFees) + " (" + feeRatio + "%). "
                + "Laba bersih " + formatCurrency(netProfit) + ".");
        reportRequest.put("metrics", metrics);
        reportRequest.put("findings", findings);
        reportRequest.put("recommendations", recommendations);
        reportRequest.put("risks", risks);

        APIResponse publishResponse = apiContext.post("/api/analysis/reports",
                RequestOptions.create()
                        .setHeader("Authorization", "Bearer " + accessToken)
                        .setHeader("Content-Type", "application/json")
                        .setData(reportRequest));

        assertThat(publishResponse.status()).isEqualTo(201);
        JsonNode reportData = objectMapper.readTree(publishResponse.text()).get("data");
        assertThat(reportData.get("industry").asText()).isEqualTo("online-seller");
        assertThat(reportData.get("metrics").size()).isEqualTo(6);
        log.info("Published: {} (id: {})", reportData.get("title").asText(), reportData.get("id").asText());

        // Step 9: Verify in web UI
        loginAsAdmin();
        navigateTo("/analysis-reports");
        waitForPageLoad();
        assertThat(page.locator("[data-testid='report-list']")).isVisible();
        assertThat(page.locator("[data-testid='report-industry']").first()).containsText("Toko Online");
        takeManualScreenshot("analysis-reports/seller-list");

        page.locator("[data-testid='report-title']").first().click();
        waitForPageLoad();
        assertThat(page.locator("[data-testid='detail-industry']")).containsText("Toko Online");
        assertThat(page.locator("[data-testid='metrics-section']")).isVisible();
        takeManualScreenshot("analysis-reports/seller-detail");

        log.info("Online Seller analysis report test passed");
    }

    // --- Helpers ---

    private JsonNode getApi(String path) throws Exception {
        APIResponse response = apiContext.get(path,
                RequestOptions.create().setHeader("Authorization", "Bearer " + accessToken));
        assertThat(response.status()).isEqualTo(200);
        return objectMapper.readTree(response.text());
    }

    private String formatCurrency(BigDecimal amount) {
        return String.format(new Locale("id", "ID"), "Rp %,.0f", amount);
    }

    private String authenticateViaDeviceFlow() throws Exception {
        Map<String, String> codeRequest = new HashMap<>();
        codeRequest.put("clientId", "seller-analysis-test");

        APIResponse codeResponse = apiContext.post("/api/device/code",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setData(codeRequest));
        assertThat(codeResponse.ok()).isTrue();

        JsonNode codeData = objectMapper.readTree(codeResponse.text());
        String deviceCode = codeData.get("deviceCode").asText();
        String userCode = codeData.get("userCode").asText();

        loginAsAdmin();
        navigateTo("/device?code=" + userCode);
        waitForPageLoad();

        page.locator("input[name='deviceName']").fill("Seller Analysis Test");
        page.locator("button[type='submit']:has-text('Otorisasi Perangkat')").click();
        waitForPageLoad();

        Map<String, String> tokenRequest = new HashMap<>();
        tokenRequest.put("deviceCode", deviceCode);

        for (int i = 0; i < 10; i++) {
            Thread.sleep(2000);
            APIResponse tokenResponse = apiContext.post("/api/device/token",
                    RequestOptions.create()
                            .setHeader("Content-Type", "application/json")
                            .setData(tokenRequest));
            if (tokenResponse.ok()) {
                return objectMapper.readTree(tokenResponse.text()).get("accessToken").asText();
            }
        }
        throw new RuntimeException("Failed to get access token");
    }
}
