package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
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
 * IT Service industry analysis report test.
 * Reads real financial data from API, computes IT-specific KPIs, publishes report.
 */
@Slf4j
@DisplayName("Analysis Report - IT Service")
@Import(ServiceTestDataInitializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AnalysisReportTest extends PlaywrightTestBase {

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
    @DisplayName("Read financial data, compute IT-service KPIs, publish and verify in web UI")
    void testPublishAndViewReportFromRealData() throws Exception {
        // Step 1: Read company info
        JsonNode companyBody = getApi("/api/analysis/company");
        JsonNode companyData = companyBody.get("data");
        String companyName = companyData.get("companyName").asText();
        String industry = companyData.get("industry").asText();
        assertThat(industry).isEqualTo("it-service");
        log.info("Company: {} (industry: {})", companyName, industry);

        // Step 2: Read financial snapshot for January 2024
        JsonNode snapshotBody = getApi("/api/analysis/snapshot?month=2024-01");
        JsonNode snapshot = snapshotBody.get("data");
        BigDecimal revenue = snapshot.get("revenue").decimalValue();
        BigDecimal expense = snapshot.get("expense").decimalValue();
        BigDecimal netProfit = snapshot.get("netProfit").decimalValue();
        BigDecimal profitMargin = snapshot.get("profitMargin").decimalValue();
        BigDecimal cashBalance = snapshot.get("cashBalance").decimalValue();
        log.info("Snapshot - revenue: {}, expense: {}, netProfit: {}, cashBalance: {}",
                revenue, expense, netProfit, cashBalance);

        // Verify real data is present (not zeros)
        assertThat(revenue).isGreaterThan(BigDecimal.ZERO);
        assertThat(expense).isGreaterThan(BigDecimal.ZERO);

        // Step 3: Read income statement for detailed breakdown
        JsonNode incomeBody = getApi("/api/analysis/income-statement?startDate=2024-01-01&endDate=2024-01-31");
        JsonNode incomeData = incomeBody.get("data");
        JsonNode revenueItems = incomeData.get("revenueItems");
        JsonNode expenseItems = incomeData.get("expenseItems");

        // Step 4: Compute IT-service-specific metrics from real data
        BigDecimal expenseRatio = revenue.compareTo(BigDecimal.ZERO) > 0
                ? expense.multiply(BigDecimal.valueOf(100)).divide(revenue, 1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        List<Map<String, String>> metrics = List.of(
                Map.of("name", "Pendapatan Bulanan", "value", formatCurrency(revenue), "status", "info"),
                Map.of("name", "Beban Operasional", "value", formatCurrency(expense), "status", "info"),
                Map.of("name", "Laba Bersih", "value", formatCurrency(netProfit),
                        "status", netProfit.compareTo(BigDecimal.ZERO) > 0 ? "positive" : "negative"),
                Map.of("name", "Margin Laba", "value", profitMargin.setScale(1, RoundingMode.HALF_UP) + "%",
                        "status", profitMargin.compareTo(BigDecimal.valueOf(15)) >= 0 ? "positive" : "warning"),
                Map.of("name", "Saldo Kas", "value", formatCurrency(cashBalance), "status", "info"),
                Map.of("name", "Rasio Beban/Pendapatan", "value", expenseRatio + "%",
                        "status", expenseRatio.compareTo(BigDecimal.valueOf(70)) <= 0 ? "positive" : "warning")
        );

        // Step 5: Generate findings from real income statement data
        List<Map<String, String>> findings = new ArrayList<>();
        if (revenueItems.size() > 0) {
            StringBuilder revDetail = new StringBuilder();
            for (JsonNode item : revenueItems) {
                revDetail.append(item.get("accountName").asText())
                        .append(": ").append(formatCurrency(item.get("balance").decimalValue())).append(". ");
            }
            findings.add(Map.of("category", "pendapatan", "severity", revenueItems.size() <= 2 ? "warning" : "info",
                    "description", "Sumber pendapatan: " + revDetail.toString().trim()
                            + " Diversifikasi " + (revenueItems.size() <= 2 ? "masih rendah." : "cukup baik.")));
        }
        if (expenseItems.size() > 0) {
            StringBuilder expDetail = new StringBuilder();
            for (JsonNode item : expenseItems) {
                expDetail.append(item.get("accountName").asText())
                        .append(": ").append(formatCurrency(item.get("balance").decimalValue())).append(". ");
            }
            findings.add(Map.of("category", "beban-operasional", "severity", "info",
                    "description", "Rincian beban operasional: " + expDetail.toString().trim()
                            + " Rasio beban terhadap pendapatan: " + expenseRatio + "%."));
        }
        findings.add(Map.of("category", "profitabilitas", "severity", "info",
                "description", "Margin laba bersih " + profitMargin.setScale(1, RoundingMode.HALF_UP) + "%. "
                        + (profitMargin.compareTo(BigDecimal.valueOf(20)) >= 0
                        ? "Di atas benchmark industri jasa IT (15-25%)."
                        : "Perlu perhatian, benchmark industri jasa IT 15-25%.")));

        // Step 6: Generate recommendations
        List<Map<String, String>> recommendations = new ArrayList<>();
        if (revenueItems.size() <= 2) {
            recommendations.add(Map.of("priority", "high",
                    "description", "Diversifikasi sumber pendapatan. Saat ini hanya " + revenueItems.size()
                            + " jenis pendapatan aktif. Target: minimal 3 sumber pendapatan berbeda.",
                    "impact", "Mengurangi risiko konsentrasi pendapatan"));
        }
        recommendations.add(Map.of("priority", "medium",
                "description", "Pertahankan rasio beban operasional di bawah 70%. "
                        + "Rasio saat ini: " + expenseRatio + "% (" + formatCurrency(expense) + " dari " + formatCurrency(revenue) + ").",
                "impact", "Menjaga margin laba tetap sehat"));
        recommendations.add(Map.of("priority", "medium",
                "description", "Dengan saldo kas " + formatCurrency(cashBalance)
                        + ", pertimbangkan investasi untuk pengembangan kapasitas atau pelatihan tim.",
                "impact", "Utilisasi kas idle untuk pertumbuhan bisnis"));

        // Step 7: Identify risks
        List<Map<String, String>> risks = new ArrayList<>();
        if (revenueItems.size() <= 2) {
            risks.add(Map.of("severity", "high",
                    "description", "Konsentrasi pendapatan tinggi: hanya " + revenueItems.size()
                            + " sumber pendapatan aktif. Kehilangan satu klien/proyek berdampak signifikan.",
                    "mitigation", "Perluas basis klien dan jenis layanan. Target 3+ sumber pendapatan aktif."));
        }
        risks.add(Map.of("severity", "medium",
                "description", "Beban operasional didominasi biaya infrastruktur IT (" + formatCurrency(expense)
                        + "). Kenaikan harga cloud services dapat menekan margin.",
                "mitigation", "Evaluasi kontrak cloud services tahunan, pertimbangkan reserved instances."));

        // Step 8: Publish the report
        Map<String, Object> request = new HashMap<>();
        request.put("title", "Review Keuangan Januari 2024 - " + companyName);
        request.put("reportType", "monthly-review");
        request.put("industry", industry);
        request.put("periodStart", "2024-01-01");
        request.put("periodEnd", "2024-01-31");
        request.put("aiSource", "claude-code");
        request.put("aiModel", "claude-opus-4-6");
        request.put("executiveSummary",
                "Review keuangan bulanan " + companyName + " periode Januari 2024. "
                + "Pendapatan " + formatCurrency(revenue) + " dengan laba bersih " + formatCurrency(netProfit)
                + " (margin " + profitMargin.setScale(1, RoundingMode.HALF_UP) + "%). "
                + "Beban operasional " + formatCurrency(expense) + " (rasio " + expenseRatio + "% terhadap pendapatan). "
                + "Saldo kas akhir bulan " + formatCurrency(cashBalance) + ".");
        request.put("metrics", metrics);
        request.put("findings", findings);
        request.put("recommendations", recommendations);
        request.put("risks", risks);

        APIResponse publishResponse = apiContext.post("/api/analysis/reports",
                RequestOptions.create()
                        .setHeader("Authorization", "Bearer " + accessToken)
                        .setHeader("Content-Type", "application/json")
                        .setData(request));

        assertThat(publishResponse.status()).isEqualTo(201);
        JsonNode publishBody = objectMapper.readTree(publishResponse.text());
        assertThat(publishBody.get("reportType").asText()).isEqualTo("analysis-report");
        JsonNode reportData = publishBody.get("data");
        assertThat(reportData.get("id").asText()).isNotEmpty();
        assertThat(reportData.get("industry").asText()).isEqualTo("it-service");
        assertThat(reportData.get("metrics").size()).isEqualTo(6);
        assertThat(reportData.get("findings").size()).isGreaterThanOrEqualTo(2);
        assertThat(reportData.get("recommendations").size()).isGreaterThanOrEqualTo(2);
        assertThat(reportData.get("risks").size()).isGreaterThanOrEqualTo(1);
        log.info("Published report: {} (id: {})", reportData.get("title").asText(), reportData.get("id").asText());

        // Step 9: Verify report in API list
        APIResponse listResponse = apiContext.get("/api/analysis/reports",
                RequestOptions.create().setHeader("Authorization", "Bearer " + accessToken));
        assertThat(listResponse.status()).isEqualTo(200);
        JsonNode listBody = objectMapper.readTree(listResponse.text());
        JsonNode reports = listBody.get("data").get("reports");
        assertThat(reports.isArray()).isTrue();
        assertThat(reports.size()).isGreaterThanOrEqualTo(1);

        // Step 10: Verify in web UI - list page
        loginAsAdmin();
        navigateTo("/analysis-reports");
        waitForPageLoad();
        assertThat(page.locator("[data-testid='report-list']")).isVisible();
        assertThat(page.locator("[data-testid='report-title']").first()).containsText(companyName);
        assertThat(page.locator("[data-testid='report-industry']").first()).containsText("Jasa IT");
        takeManualScreenshot("analysis-reports/list");

        // Step 11: Verify in web UI - detail page
        page.locator("[data-testid='report-title']").first().click();
        waitForPageLoad();
        assertThat(page.locator("[data-testid='detail-title']")).containsText(companyName);
        assertThat(page.locator("[data-testid='detail-industry']")).containsText("Jasa IT");
        assertThat(page.locator("[data-testid='detail-ai-source']")).containsText("claude-code");
        assertThat(page.locator("[data-testid='executive-summary']")).isVisible();
        assertThat(page.locator("[data-testid='metrics-section']")).isVisible();
        assertThat(page.locator("[data-testid='findings-section']")).isVisible();
        assertThat(page.locator("[data-testid='recommendations-section']")).isVisible();
        assertThat(page.locator("[data-testid='risks-section']")).isVisible();
        takeManualScreenshot("analysis-reports/detail-top");

        page.locator("[data-testid='risks-section']").scrollIntoViewIfNeeded();
        page.waitForTimeout(300);
        takeManualScreenshot("analysis-reports/detail-bottom");

        log.info("IT Service analysis report test passed");
    }

    @Test
    @Order(2)
    @DisplayName("Web UI - delete report removes it from list")
    void testDeleteReport() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("title", "Laporan untuk Dihapus");
        request.put("reportType", "monthly-review");
        request.put("industry", "it-service");

        APIResponse publishResponse = apiContext.post("/api/analysis/reports",
                RequestOptions.create()
                        .setHeader("Authorization", "Bearer " + accessToken)
                        .setHeader("Content-Type", "application/json")
                        .setData(request));
        assertThat(publishResponse.status()).isEqualTo(201);

        loginAsAdmin();
        navigateTo("/analysis-reports");
        waitForPageLoad();

        page.locator("text=Laporan untuk Dihapus").click();
        waitForPageLoad();

        page.onDialog(dialog -> dialog.accept());
        page.locator("[data-testid='btn-delete']").click();
        waitForPageLoad();

        assertThat(page.url()).contains("/analysis-reports");
        assertThat(page.locator("text=Laporan untuk Dihapus")).not().isVisible();

        log.info("Delete report test passed");
    }

    @Test
    @Order(3)
    @DisplayName("POST /api/analysis/reports without auth returns 401")
    void testPublishWithoutAuth() {
        Map<String, Object> request = new HashMap<>();
        request.put("title", "Test");
        request.put("reportType", "monthly-review");

        APIResponse response = apiContext.post("/api/analysis/reports",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setData(request));

        assertThat(response.status()).isEqualTo(401);
        log.info("Unauthenticated publish test passed");
    }

    @Test
    @Order(4)
    @DisplayName("POST /api/analysis/reports without required title returns 400")
    void testPublishMissingTitle() {
        Map<String, Object> request = new HashMap<>();
        request.put("reportType", "monthly-review");

        APIResponse response = apiContext.post("/api/analysis/reports",
                RequestOptions.create()
                        .setHeader("Authorization", "Bearer " + accessToken)
                        .setHeader("Content-Type", "application/json")
                        .setData(request));

        assertThat(response.status()).isEqualTo(400);
        log.info("Missing title test passed");
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
        codeRequest.put("clientId", "analysis-report-test");

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

        page.locator("input[name='deviceName']").fill("Analysis Report Test");
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
                JsonNode tokenData = objectMapper.readTree(tokenResponse.text());
                return tokenData.get("accessToken").asText();
            }
        }

        throw new RuntimeException("Failed to get access token");
    }
}
