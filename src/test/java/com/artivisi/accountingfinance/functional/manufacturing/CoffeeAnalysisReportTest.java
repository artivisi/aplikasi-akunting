package com.artivisi.accountingfinance.functional.manufacturing;

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
 * Coffee Shop (F&B) industry analysis report test.
 * Reads real financial data from API, computes F&B-specific KPIs, publishes report.
 * KPIs: Food Cost %, Labor Cost %, Prime Cost, Break-even analysis.
 */
@Slf4j
@DisplayName("Analysis Report - Coffee Shop")
@Import(CoffeeAnalysisTestDataInitializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CoffeeAnalysisReportTest extends PlaywrightTestBase {

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
    @DisplayName("Read financial data, compute F&B KPIs, publish and verify")
    void testPublishAndViewReportFromRealData() throws Exception {
        // Step 1: Read company info
        JsonNode companyBody = getApi("/api/analysis/company");
        JsonNode companyData = companyBody.get("data");
        String companyName = companyData.get("companyName").asText();
        String industry = companyData.get("industry").asText();
        assertThat(industry).isEqualTo("coffee-shop");
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

        // Step 3: Read income statement for food cost breakdown
        JsonNode incomeBody = getApi("/api/analysis/income-statement?startDate=2024-01-01&endDate=2024-01-31");
        JsonNode incomeData = incomeBody.get("data");
        BigDecimal totalRevenue = incomeData.get("totalRevenue").decimalValue();
        BigDecimal totalExpense = incomeData.get("totalExpense").decimalValue();
        JsonNode expenseItems = incomeData.get("expenseItems");

        // Categorize expenses for F&B analysis
        BigDecimal foodCost = BigDecimal.ZERO;  // HPP (5.x)
        BigDecimal laborCost = BigDecimal.ZERO;  // Gaji (6.1.x)
        BigDecimal rentCost = BigDecimal.ZERO;   // Sewa (6.2.01)
        BigDecimal otherOpex = BigDecimal.ZERO;  // Other operational

        for (JsonNode item : expenseItems) {
            String code = item.get("accountCode").asText();
            BigDecimal balance = item.get("balance").decimalValue();
            if (code.startsWith("5.")) {
                foodCost = foodCost.add(balance);
            } else if (code.startsWith("6.1")) {
                laborCost = laborCost.add(balance);
            } else if (code.equals("6.2.01")) {
                rentCost = rentCost.add(balance);
            } else {
                otherOpex = otherOpex.add(balance);
            }
        }

        BigDecimal foodCostPct = totalRevenue.compareTo(BigDecimal.ZERO) > 0
                ? foodCost.multiply(BigDecimal.valueOf(100)).divide(totalRevenue, 1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal laborCostPct = totalRevenue.compareTo(BigDecimal.ZERO) > 0
                ? laborCost.multiply(BigDecimal.valueOf(100)).divide(totalRevenue, 1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal primeCost = foodCost.add(laborCost);
        BigDecimal primeCostPct = totalRevenue.compareTo(BigDecimal.ZERO) > 0
                ? primeCost.multiply(BigDecimal.valueOf(100)).divide(totalRevenue, 1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal netMargin = totalRevenue.compareTo(BigDecimal.ZERO) > 0
                ? netProfit.multiply(BigDecimal.valueOf(100)).divide(totalRevenue, 1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Step 4: Compute F&B KPIs
        List<Map<String, String>> metrics = List.of(
                Map.of("name", "Pendapatan", "value", formatCurrency(totalRevenue), "status", "info"),
                Map.of("name", "Food Cost %", "value", foodCostPct + "%",
                        "status", foodCostPct.compareTo(BigDecimal.valueOf(35)) <= 0 ? "positive" : "warning"),
                Map.of("name", "Labor Cost %", "value", laborCostPct + "%",
                        "status", laborCostPct.compareTo(BigDecimal.valueOf(30)) <= 0 ? "positive" : "warning"),
                Map.of("name", "Prime Cost %", "value", primeCostPct + "%",
                        "status", primeCostPct.compareTo(BigDecimal.valueOf(65)) <= 0 ? "positive" : "warning"),
                Map.of("name", "Laba Bersih", "value", formatCurrency(netProfit),
                        "status", netProfit.compareTo(BigDecimal.ZERO) > 0 ? "positive" : "negative"),
                Map.of("name", "Net Margin", "value", netMargin + "%",
                        "status", netMargin.compareTo(BigDecimal.valueOf(10)) >= 0 ? "positive" : "warning")
        );

        // Step 5: Findings
        List<Map<String, String>> findings = new ArrayList<>();
        findings.add(Map.of("category", "food-cost", "severity",
                foodCostPct.compareTo(BigDecimal.valueOf(35)) <= 0 ? "info" : "warning",
                "description", "Food cost " + foodCostPct + "% dari pendapatan (" + formatCurrency(foodCost)
                        + "). Benchmark F&B: 28-35%. "
                        + (foodCostPct.compareTo(BigDecimal.valueOf(35)) <= 0
                        ? "Dalam batas sehat." : "Di atas benchmark, perlu evaluasi harga atau porsi.")));
        findings.add(Map.of("category", "labor-cost", "severity",
                laborCostPct.compareTo(BigDecimal.valueOf(30)) <= 0 ? "info" : "warning",
                "description", "Labor cost " + laborCostPct + "% dari pendapatan (" + formatCurrency(laborCost)
                        + "). Benchmark F&B: 25-30%."));
        findings.add(Map.of("category", "prime-cost", "severity",
                primeCostPct.compareTo(BigDecimal.valueOf(65)) <= 0 ? "info" : "warning",
                "description", "Prime cost (food + labor) " + primeCostPct + "% (" + formatCurrency(primeCost)
                        + "). Benchmark: <65% untuk profitabilitas berkelanjutan."));

        // Step 6: Recommendations
        List<Map<String, String>> recommendations = new ArrayList<>();
        if (foodCostPct.compareTo(BigDecimal.valueOf(35)) > 0) {
            recommendations.add(Map.of("priority", "high",
                    "description", "Turunkan food cost dari " + foodCostPct + "% ke target 30-35%. "
                            + "Evaluasi supplier bahan baku, porsi, dan waste management.",
                    "impact", "Peningkatan gross margin " + foodCostPct.subtract(BigDecimal.valueOf(35)).setScale(1, RoundingMode.HALF_UP) + "pp"));
        }
        recommendations.add(Map.of("priority", "medium",
                "description", "Biaya sewa " + formatCurrency(rentCost) + "/bulan. "
                        + "Pastikan occupancy rate tinggi terutama di jam sibuk (07:00-10:00 dan 15:00-17:00).",
                "impact", "Optimalisasi revenue per seat"));
        recommendations.add(Map.of("priority", "medium",
                "description", "Dengan saldo kas " + formatCurrency(cashBalance)
                        + ", pertimbangkan investasi mesin espresso berkualitas untuk meningkatkan konsistensi produk.",
                "impact", "Peningkatan kualitas dan customer retention"));

        // Step 7: Risks
        List<Map<String, String>> risks = new ArrayList<>();
        risks.add(Map.of("severity", "high",
                "description", "Kenaikan harga bahan baku kopi dan susu dapat langsung menekan margin. "
                        + "Food cost saat ini " + foodCostPct + "%.",
                "mitigation", "Kontrak harga tetap dengan roaster lokal, diversifikasi menu non-kopi."));
        risks.add(Map.of("severity", "medium",
                "description", "Ketergantungan pada satu lokasi. Gangguan di area sekitar "
                        + "(renovasi jalan, kompetitor baru) berdampak langsung pada traffic.",
                "mitigation", "Bangun loyalitas pelanggan melalui membership program dan delivery service."));

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
                + "Pendapatan " + formatCurrency(totalRevenue) + " dengan food cost " + foodCostPct + "% "
                + "dan labor cost " + laborCostPct + "%. "
                + "Prime cost " + primeCostPct + "%. "
                + "Laba bersih " + formatCurrency(netProfit) + " (margin " + netMargin + "%).");
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
        assertThat(reportData.get("industry").asText()).isEqualTo("coffee-shop");
        assertThat(reportData.get("metrics").size()).isEqualTo(6);
        log.info("Published: {} (id: {})", reportData.get("title").asText(), reportData.get("id").asText());

        // Step 9: Verify in web UI
        loginAsAdmin();
        navigateTo("/analysis-reports");
        waitForPageLoad();
        assertThat(page.locator("[data-testid='report-list']")).isVisible();
        assertThat(page.locator("[data-testid='report-industry']").first()).containsText("Kedai Kopi");
        takeManualScreenshot("analysis-reports/coffee-list");

        page.locator("[data-testid='report-title']").first().click();
        waitForPageLoad();
        assertThat(page.locator("[data-testid='detail-industry']")).containsText("Kedai Kopi");
        assertThat(page.locator("[data-testid='metrics-section']")).isVisible();
        takeManualScreenshot("analysis-reports/coffee-detail");

        log.info("Coffee Shop analysis report test passed");
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
        codeRequest.put("clientId", "coffee-analysis-test");

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

        page.locator("input[name='deviceName']").fill("Coffee Analysis Test");
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
