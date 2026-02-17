package com.artivisi.accountingfinance.functional.campus;

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
 * Campus (Education) industry analysis report test.
 * Reads real financial data from API, computes education-specific KPIs, publishes report.
 * KPIs: Revenue per Student, Faculty Cost Ratio, Scholarship Ratio, Operating Efficiency.
 */
@Slf4j
@DisplayName("Analysis Report - Campus")
@Import(CampusTestDataInitializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CampusAnalysisReportTest extends PlaywrightTestBase {

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
    @DisplayName("Read financial data, compute education KPIs, publish and verify")
    void testPublishAndViewReportFromRealData() throws Exception {
        // Step 1: Read company info
        JsonNode companyBody = getApi("/api/analysis/company");
        JsonNode companyData = companyBody.get("data");
        String companyName = companyData.get("companyName").asText();
        String industry = companyData.get("industry").asText();
        assertThat(industry).isEqualTo("campus");
        log.info("Company: {} (industry: {})", companyName, industry);

        // Step 2: Read financial snapshot for July 2024 (campus fiscal year starts July)
        JsonNode snapshotBody = getApi("/api/analysis/snapshot?month=2024-07");
        JsonNode snapshot = snapshotBody.get("data");
        BigDecimal revenue = snapshot.get("revenue").decimalValue();
        BigDecimal expense = snapshot.get("expense").decimalValue();
        BigDecimal netProfit = snapshot.get("netProfit").decimalValue();
        BigDecimal cashBalance = snapshot.get("cashBalance").decimalValue();
        log.info("Snapshot - revenue: {}, expense: {}, netProfit: {}, cashBalance: {}",
                revenue, expense, netProfit, cashBalance);

        assertThat(revenue).isGreaterThan(BigDecimal.ZERO);

        // Step 3: Read income statement for expense breakdown
        JsonNode incomeBody = getApi("/api/analysis/income-statement?startDate=2024-07-01&endDate=2024-07-31");
        JsonNode incomeData = incomeBody.get("data");
        BigDecimal totalRevenue = incomeData.get("totalRevenue").decimalValue();
        BigDecimal totalExpense = incomeData.get("totalExpense").decimalValue();
        JsonNode revenueItems = incomeData.get("revenueItems");
        JsonNode expenseItems = incomeData.get("expenseItems");

        // Categorize revenue and expenses for education analysis
        BigDecimal sppRevenue = BigDecimal.ZERO;
        BigDecimal otherRevenue = BigDecimal.ZERO;
        for (JsonNode item : revenueItems) {
            String code = item.get("accountCode").asText();
            BigDecimal balance = item.get("balance").decimalValue();
            if (code.equals("4.1.01")) {
                sppRevenue = balance;
            } else {
                otherRevenue = otherRevenue.add(balance);
            }
        }

        BigDecimal facultyCost = BigDecimal.ZERO;   // 5.1.x (academic)
        BigDecimal adminCost = BigDecimal.ZERO;      // 5.2.x (administrative)
        BigDecimal scholarshipCost = BigDecimal.ZERO; // 5.3.x (student affairs)
        for (JsonNode item : expenseItems) {
            String code = item.get("accountCode").asText();
            BigDecimal balance = item.get("balance").decimalValue();
            if (code.startsWith("5.1")) {
                facultyCost = facultyCost.add(balance);
            } else if (code.startsWith("5.2")) {
                adminCost = adminCost.add(balance);
            } else if (code.startsWith("5.3")) {
                scholarshipCost = scholarshipCost.add(balance);
            }
        }

        BigDecimal facultyCostRatio = totalRevenue.compareTo(BigDecimal.ZERO) > 0
                ? facultyCost.multiply(BigDecimal.valueOf(100)).divide(totalRevenue, 1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal scholarshipRatio = totalRevenue.compareTo(BigDecimal.ZERO) > 0
                ? scholarshipCost.multiply(BigDecimal.valueOf(100)).divide(totalRevenue, 1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal sppConcentration = totalRevenue.compareTo(BigDecimal.ZERO) > 0
                ? sppRevenue.multiply(BigDecimal.valueOf(100)).divide(totalRevenue, 1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal operatingMargin = totalRevenue.compareTo(BigDecimal.ZERO) > 0
                ? netProfit.multiply(BigDecimal.valueOf(100)).divide(totalRevenue, 1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Step 4: Compute education KPIs
        List<Map<String, String>> metrics = List.of(
                Map.of("name", "Pendapatan Total", "value", formatCurrency(totalRevenue), "status", "info"),
                Map.of("name", "Pendapatan SPP", "value", formatCurrency(sppRevenue),
                        "status", sppConcentration.compareTo(BigDecimal.valueOf(80)) < 0 ? "positive" : "warning"),
                Map.of("name", "Biaya Dosen & Akademik", "value", formatCurrency(facultyCost),
                        "status", facultyCostRatio.compareTo(BigDecimal.valueOf(40)) <= 0 ? "positive" : "warning"),
                Map.of("name", "Rasio Beasiswa", "value", scholarshipRatio + "%",
                        "status", scholarshipRatio.compareTo(BigDecimal.valueOf(5)) >= 0 ? "positive" : "info"),
                Map.of("name", "Surplus Operasional", "value", formatCurrency(netProfit),
                        "status", netProfit.compareTo(BigDecimal.ZERO) > 0 ? "positive" : "negative"),
                Map.of("name", "Margin Operasional", "value", operatingMargin + "%",
                        "status", operatingMargin.compareTo(BigDecimal.valueOf(10)) >= 0 ? "positive" : "warning")
        );

        // Step 5: Findings
        List<Map<String, String>> findings = new ArrayList<>();
        findings.add(Map.of("category", "pendapatan", "severity",
                sppConcentration.compareTo(BigDecimal.valueOf(80)) < 0 ? "info" : "warning",
                "description", "Pendapatan SPP " + formatCurrency(sppRevenue) + " (" + sppConcentration + "% dari total). "
                        + "Pendapatan lain " + formatCurrency(otherRevenue) + ". "
                        + (sppConcentration.compareTo(BigDecimal.valueOf(80)) >= 0
                        ? "Konsentrasi SPP tinggi, diversifikasi pendapatan diperlukan."
                        : "Diversifikasi pendapatan cukup baik.")));
        findings.add(Map.of("category", "biaya-akademik", "severity", "info",
                "description", "Biaya dosen & akademik " + formatCurrency(facultyCost) + " (" + facultyCostRatio + "% pendapatan). "
                        + "Biaya administrasi " + formatCurrency(adminCost) + ". "
                        + "Benchmark: rasio dosen 30-40% dari pendapatan."));
        findings.add(Map.of("category", "beasiswa", "severity", "info",
                "description", "Dana beasiswa " + formatCurrency(scholarshipCost)
                        + " (" + scholarshipRatio + "% dari pendapatan). "
                        + "Alokasi beasiswa menunjukkan komitmen terhadap aksesibilitas pendidikan."));

        // Step 6: Recommendations
        List<Map<String, String>> recommendations = new ArrayList<>();
        if (sppConcentration.compareTo(BigDecimal.valueOf(80)) >= 0) {
            recommendations.add(Map.of("priority", "high",
                    "description", "Diversifikasi pendapatan selain SPP. "
                            + "Potensi: program sertifikasi profesional, pelatihan korporat, riset kolaborasi industri.",
                    "impact", "Mengurangi ketergantungan pada SPP dan meningkatkan resiliensi keuangan"));
        }
        recommendations.add(Map.of("priority", "medium",
                "description", "Optimalisasi rasio dosen tetap vs tidak tetap. "
                        + "Biaya akademik saat ini " + formatCurrency(facultyCost) + " (" + facultyCostRatio + "%).",
                "impact", "Efisiensi biaya akademik tanpa mengurangi kualitas"));
        recommendations.add(Map.of("priority", "medium",
                "description", "Dengan surplus " + formatCurrency(netProfit)
                        + ", alokasikan untuk pengembangan fasilitas lab atau akreditasi program studi.",
                "impact", "Peningkatan daya saing dan akreditasi"));

        // Step 7: Risks
        List<Map<String, String>> risks = new ArrayList<>();
        risks.add(Map.of("severity", "high",
                "description", "Penurunan jumlah mahasiswa baru akibat kompetisi dengan perguruan tinggi lain "
                        + "dan tren penurunan minat ke STMIK. SPP berkontribusi " + sppConcentration + "% pendapatan.",
                "mitigation", "Perkuat program studi unggulan, kerjasama industri untuk penyerapan lulusan, "
                        + "dan beasiswa menarik untuk mahasiswa berprestasi."));
        risks.add(Map.of("severity", "medium",
                "description", "Regulasi pendidikan (akreditasi, rasio dosen) dapat memaksa peningkatan biaya. "
                        + "Rasio biaya dosen saat ini " + facultyCostRatio + "%.",
                "mitigation", "Rencana rekrutmen dosen tetap bertahap, program studi lanjut bagi dosen."));

        // Step 8: Publish report
        Map<String, Object> reportRequest = new HashMap<>();
        reportRequest.put("title", "Review Keuangan Juli 2024 - " + companyName);
        reportRequest.put("reportType", "semester-review");
        reportRequest.put("industry", industry);
        reportRequest.put("periodStart", "2024-07-01");
        reportRequest.put("periodEnd", "2024-07-31");
        reportRequest.put("aiSource", "claude-code");
        reportRequest.put("aiModel", "claude-opus-4-6");
        reportRequest.put("executiveSummary",
                "Review keuangan " + companyName + " periode Juli 2024 (awal semester ganjil). "
                + "Pendapatan " + formatCurrency(totalRevenue) + " (SPP " + sppConcentration + "%). "
                + "Biaya dosen & akademik " + formatCurrency(facultyCost) + " (" + facultyCostRatio + "%). "
                + "Dana beasiswa " + formatCurrency(scholarshipCost) + ". "
                + "Surplus operasional " + formatCurrency(netProfit) + " (margin " + operatingMargin + "%).");
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
        assertThat(reportData.get("industry").asText()).isEqualTo("campus");
        assertThat(reportData.get("metrics").size()).isEqualTo(6);
        log.info("Published: {} (id: {})", reportData.get("title").asText(), reportData.get("id").asText());

        // Step 9: Verify in web UI
        loginAsAdmin();
        navigateTo("/analysis-reports");
        waitForPageLoad();
        assertThat(page.locator("[data-testid='report-list']")).isVisible();
        assertThat(page.locator("[data-testid='report-industry']").first()).containsText("Kampus");
        takeManualScreenshot("analysis-reports/campus-list");

        page.locator("[data-testid='report-title']").first().click();
        waitForPageLoad();
        assertThat(page.locator("[data-testid='detail-industry']")).containsText("Kampus");
        assertThat(page.locator("[data-testid='metrics-section']")).isVisible();
        takeManualScreenshot("analysis-reports/campus-detail");

        log.info("Campus analysis report test passed");
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
        codeRequest.put("clientId", "campus-analysis-test");

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

        page.locator("input[name='deviceName']").fill("Campus Analysis Test");
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
