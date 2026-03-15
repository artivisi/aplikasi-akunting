package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.taxdetail.TaxDetailTestDataInitializer;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Functional tests for SPT Tahunan Badan API endpoints.
 * Tests L1, L4, L9, Transkrip 8A, BPA1 exports and loss carryforward CRUD.
 */
@Slf4j
@DisplayName("SPT Tahunan Badan API Tests")
@Import(TaxDetailTestDataInitializer.class)
class SptTahunanApiTest extends PlaywrightTestBase {

    private APIRequestContext apiContext;
    private ObjectMapper objectMapper;
    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

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

    // ==================== L1 REKONSILIASI FISKAL ====================

    @Nested
    @DisplayName("L1 Rekonsiliasi Fiskal")
    class L1Tests {

        @Test
        @DisplayName("GET /api/tax-export/spt-tahunan/l1 - returns JSON")
        void testL1Json() throws Exception {
            APIResponse response = get("/api/tax-export/spt-tahunan/l1?year=2025");
            assertThat(response.status()).isEqualTo(200);

            JsonNode body = parse(response);
            assertThat(body.get("reportType").asText()).isEqualTo("spt-tahunan-l1");

            JsonNode data = body.get("data");
            assertThat(data.has("year")).isTrue();
            assertThat(data.get("year").asInt()).isEqualTo(2025);
            assertThat(data.has("operatingRevenue")).isTrue();
            assertThat(data.has("operatingExpenses")).isTrue();
            assertThat(data.has("adjustments")).isTrue();
            assertThat(data.has("pkp")).isTrue();
            assertThat(data.has("pkpBeforeLoss")).isTrue();
            assertThat(data.has("lossCarryforwards")).isTrue();

            log.info("L1 JSON test passed - pkp={}", data.get("pkp").asText());
        }

        @Test
        @DisplayName("GET /api/tax-export/spt-tahunan/l1?format=excel - returns XLSX")
        void testL1Excel() {
            APIResponse response = get("/api/tax-export/spt-tahunan/l1?year=2025&format=excel");
            assertThat(response.status()).isEqualTo(200);
            assertThat(response.headers().get("content-type")).contains("spreadsheetml.sheet");
            assertThat(response.headers().get("content-disposition")).contains("attachment");
            assertThat(response.body()).hasSizeGreaterThan(0);

            log.info("L1 Excel test passed - size={}", response.body().length);
        }
    }

    // ==================== L4 PENGHASILAN FINAL ====================

    @Nested
    @DisplayName("L4 Penghasilan Final")
    class L4Tests {

        @Test
        @DisplayName("GET /api/tax-export/spt-tahunan/l4 - returns JSON")
        void testL4Json() throws Exception {
            APIResponse response = get("/api/tax-export/spt-tahunan/l4?year=2025");
            assertThat(response.status()).isEqualTo(200);

            JsonNode body = parse(response);
            assertThat(body.get("reportType").asText()).isEqualTo("spt-tahunan-l4");

            JsonNode data = body.get("data");
            assertThat(data.has("year")).isTrue();
            assertThat(data.has("items")).isTrue();
            assertThat(data.has("totalBruto")).isTrue();
            assertThat(data.has("totalPph")).isTrue();

            log.info("L4 JSON test passed - items={}", data.get("items").size());
        }

        @Test
        @DisplayName("GET /api/tax-export/spt-tahunan/l4?format=excel - returns XLSX")
        void testL4Excel() {
            APIResponse response = get("/api/tax-export/spt-tahunan/l4?year=2025&format=excel");
            assertThat(response.status()).isEqualTo(200);
            assertThat(response.headers().get("content-type")).contains("spreadsheetml.sheet");
            assertThat(response.headers().get("content-disposition")).contains("attachment");
            assertThat(response.body()).hasSizeGreaterThan(0);

            log.info("L4 Excel test passed - size={}", response.body().length);
        }
    }

    // ==================== TRANSKRIP 8A ====================

    @Nested
    @DisplayName("Transkrip 8A Laporan Keuangan")
    class Transkrip8ATests {

        @Test
        @DisplayName("GET /api/tax-export/spt-tahunan/transkrip-8a - returns JSON")
        void testTranskrip8AJson() throws Exception {
            APIResponse response = get("/api/tax-export/spt-tahunan/transkrip-8a?year=2025");
            assertThat(response.status()).isEqualTo(200);

            JsonNode body = parse(response);
            assertThat(body.get("reportType").asText()).isEqualTo("spt-tahunan-transkrip-8a");

            JsonNode data = body.get("data");
            assertThat(data.has("year")).isTrue();
            assertThat(data.has("neracaItems")).isTrue();
            assertThat(data.has("labaRugiItems")).isTrue();

            log.info("Transkrip 8A JSON test passed - neraca={}, labaRugi={}",
                    data.get("neracaItems").size(), data.get("labaRugiItems").size());
        }

        @Test
        @DisplayName("GET /api/tax-export/spt-tahunan/transkrip-8a?format=excel - returns XLSX")
        void testTranskrip8AExcel() {
            APIResponse response = get("/api/tax-export/spt-tahunan/transkrip-8a?year=2025&format=excel");
            assertThat(response.status()).isEqualTo(200);
            assertThat(response.headers().get("content-type")).contains("spreadsheetml.sheet");
            assertThat(response.headers().get("content-disposition")).contains("attachment");
            assertThat(response.body()).hasSizeGreaterThan(0);

            log.info("Transkrip 8A Excel test passed - size={}", response.body().length);
        }
    }

    // ==================== L9 PENYUSUTAN ====================

    @Nested
    @DisplayName("L9 Penyusutan & Amortisasi")
    class L9Tests {

        @Test
        @DisplayName("GET /api/tax-export/spt-tahunan/l9 - returns JSON")
        void testL9Json() throws Exception {
            APIResponse response = get("/api/tax-export/spt-tahunan/l9?year=2025");
            assertThat(response.status()).isEqualTo(200);

            JsonNode body = parse(response);
            assertThat(body.get("reportType").asText()).isEqualTo("spt-tahunan-l9");

            JsonNode data = body.get("data");
            assertThat(data.has("year")).isTrue();
            assertThat(data.has("items")).isTrue();
            assertThat(data.has("totalAcquisitionCost")).isTrue();
            assertThat(data.has("totalFiscalDepreciation")).isTrue();

            log.info("L9 JSON test passed - items={}", data.get("items").size());
        }

        @Test
        @DisplayName("GET /api/tax-export/spt-tahunan/l9?format=excel - returns XLSX")
        void testL9Excel() {
            APIResponse response = get("/api/tax-export/spt-tahunan/l9?year=2025&format=excel");
            assertThat(response.status()).isEqualTo(200);
            assertThat(response.headers().get("content-type")).contains("spreadsheetml.sheet");
            assertThat(response.headers().get("content-disposition")).contains("attachment");
            assertThat(response.body()).hasSizeGreaterThan(0);

            log.info("L9 Excel test passed - size={}", response.body().length);
        }
    }

    // ==================== BPA1 E-BUPOT PPH 21 ====================

    @Nested
    @DisplayName("BPA1 e-Bupot PPh 21")
    class Bpa1Tests {

        @Test
        @DisplayName("GET /api/tax-export/ebupot-pph21 - returns JSON")
        void testBpa1Json() throws Exception {
            APIResponse response = get("/api/tax-export/ebupot-pph21?year=2025");
            assertThat(response.status()).isEqualTo(200);

            JsonNode body = parse(response);
            assertThat(body.get("reportType").asText()).isEqualTo("ebupot-pph21");

            JsonNode data = body.get("data");
            assertThat(data.has("year")).isTrue();
            assertThat(data.has("items")).isTrue();

            log.info("BPA1 JSON test passed - employees={}", data.get("items").size());
        }

        @Test
        @DisplayName("GET /api/tax-export/ebupot-pph21?format=excel - returns XLSX")
        void testBpa1Excel() {
            APIResponse response = get("/api/tax-export/ebupot-pph21?year=2025&format=excel");
            assertThat(response.status()).isEqualTo(200);
            assertThat(response.headers().get("content-type")).contains("spreadsheetml.sheet");
            assertThat(response.headers().get("content-disposition")).contains("attachment");
            assertThat(response.body()).hasSizeGreaterThan(0);

            log.info("BPA1 Excel test passed - size={}", response.body().length);
        }
    }

    // ==================== LOSS CARRYFORWARD CRUD ====================

    @Nested
    @DisplayName("Loss Carryforward CRUD")
    class LossCarryforwardTests {

        @Test
        @DisplayName("CRUD lifecycle: create, list, delete loss carryforward")
        void crudLifecycle() throws Exception {
            // CREATE
            Map<String, Object> request = new HashMap<>();
            request.put("originYear", 2022);
            request.put("originalAmount", 50000000);
            request.put("notes", "Rugi fiskal tahun 2022");

            APIResponse createResponse = post("/api/fiscal-adjustments/loss-carryforward", request);
            assertThat(createResponse.status()).isEqualTo(201);

            JsonNode created = parse(createResponse);
            String id = created.get("id").asText();
            assertThat(id).isNotBlank();
            assertThat(created.get("originYear").asInt()).isEqualTo(2022);
            assertThat(created.get("originalAmount").asDouble()).isEqualTo(50000000.0);
            assertThat(created.get("remainingAmount").asDouble()).isEqualTo(50000000.0);
            assertThat(created.get("expiryYear").asInt()).isEqualTo(2027);
            log.info("Created loss carryforward: id={}", id);

            // LIST
            APIResponse listResponse = get("/api/fiscal-adjustments/loss-carryforward?year=2025");
            assertThat(listResponse.status()).isEqualTo(200);

            JsonNode listBody = parse(listResponse);
            assertThat(listBody.has("items")).isTrue();
            assertThat(listBody.has("totalActiveRemaining")).isTrue();

            JsonNode items = listBody.get("items");
            assertThat(items.size()).isGreaterThanOrEqualTo(1);

            boolean found = false;
            for (JsonNode item : items) {
                if (item.get("originYear").asInt() == 2022) {
                    found = true;
                    break;
                }
            }
            assertThat(found).as("Created loss should appear in list").isTrue();
            log.info("Listed loss carryforwards: {} items", items.size());

            // DELETE
            APIResponse deleteResponse = delete("/api/fiscal-adjustments/loss-carryforward/" + id);
            assertThat(deleteResponse.status()).isEqualTo(204);
            log.info("Deleted loss carryforward: id={}", id);

            // VERIFY DELETED
            APIResponse listAfter = get("/api/fiscal-adjustments/loss-carryforward?year=2025");
            JsonNode listAfterBody = parse(listAfter);
            boolean stillExists = false;
            for (JsonNode item : listAfterBody.get("items")) {
                if (item.get("originYear").asInt() == 2022) {
                    stillExists = true;
                    break;
                }
            }
            assertThat(stillExists).as("Deleted loss should not appear in list").isFalse();
        }

        @Test
        @DisplayName("POST with invalid data returns 400")
        void createWithInvalidData() {
            Map<String, Object> request = new HashMap<>();
            request.put("originYear", null);
            request.put("originalAmount", -1000);

            APIResponse response = post("/api/fiscal-adjustments/loss-carryforward", request);
            assertThat(response.status()).isEqualTo(400);
        }
    }

    // ==================== ERROR HANDLING ====================

    @Nested
    @DisplayName("Error Handling")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Unauthenticated requests return 401")
        void testUnauthenticated() {
            APIResponse response = apiContext.get("/api/tax-export/spt-tahunan/l1?year=2025");
            assertThat(response.status()).isEqualTo(401);
        }
    }

    // ==================== HELPERS ====================

    private APIResponse get(String path) {
        return apiContext.get(path,
                RequestOptions.create()
                        .setHeader("Authorization", "Bearer " + accessToken));
    }

    private APIResponse post(String path, Object data) {
        return apiContext.post(path,
                RequestOptions.create()
                        .setHeader("Authorization", "Bearer " + accessToken)
                        .setHeader("Content-Type", "application/json")
                        .setData(data));
    }

    private APIResponse delete(String path) {
        return apiContext.delete(path,
                RequestOptions.create()
                        .setHeader("Authorization", "Bearer " + accessToken));
    }

    private JsonNode parse(APIResponse response) throws Exception {
        return objectMapper.readTree(response.text());
    }

    private String authenticateViaDeviceFlow() throws Exception {
        Map<String, String> codeRequest = new HashMap<>();
        codeRequest.put("clientId", "spt-tahunan-api-test");

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

        page.locator("input[name='deviceName']").fill("SPT Tahunan API Test Device");
        page.locator("button[type='submit']:has-text('Otorisasi Perangkat')").click();
        waitForPageLoad();

        Map<String, String> tokenRequest = new HashMap<>();
        tokenRequest.put("deviceCode", deviceCode);

        AtomicReference<String> tokenRef = new AtomicReference<>();
        await().atMost(Duration.ofSeconds(20)).pollInterval(Duration.ofSeconds(2)).until(() -> {
            APIResponse tokenResponse = apiContext.post("/api/device/token",
                    RequestOptions.create()
                            .setHeader("Content-Type", "application/json")
                            .setData(tokenRequest));
            if (tokenResponse.ok()) {
                JsonNode tokenData = objectMapper.readTree(tokenResponse.text());
                tokenRef.set(tokenData.get("accessToken").asText());
                return true;
            }
            return false;
        });

        return tokenRef.get();
    }
}
