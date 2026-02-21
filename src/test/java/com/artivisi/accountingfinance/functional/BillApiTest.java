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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DisplayName("Bill API - Functional Tests")
@Import(ServiceTestDataInitializer.class)
class BillApiTest extends PlaywrightTestBase {

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

    @Test
    @DisplayName("POST /api/bills - creates bill with existing vendor match")
    void testCreateBillWithExistingVendor() throws Exception {
        // First create a vendor via API
        String vendorName = "PT Vendor Pertama";
        JsonNode created1 = createBill(vendorName, "2026-02-01", "2026-03-01",
                "INV-001", List.of(
                        Map.of("description", "Jasa Konsultasi", "unitPrice", 5000000, "quantity", 1)
                ));

        assertThat(created1.get("vendorName").asText()).isEqualTo(vendorName);
        String vendorId = created1.get("vendorId").asText();

        // Create another bill with same vendor name — should match existing
        JsonNode created2 = createBill(vendorName, "2026-02-15", "2026-03-15",
                "INV-002", List.of(
                        Map.of("description", "Jasa Maintenance", "unitPrice", 3000000, "quantity", 2)
                ));

        assertThat(created2.get("vendorId").asText()).isEqualTo(vendorId);
        assertThat(created2.get("vendorName").asText()).isEqualTo(vendorName);

        log.info("Existing vendor match test passed - vendorId: {}", vendorId);
    }

    @Test
    @DisplayName("POST /api/bills - creates bill with new vendor auto-creation")
    void testCreateBillWithNewVendor() throws Exception {
        String vendorName = "PT Vendor Baru API";
        JsonNode created = createBill(vendorName, "2026-02-10", "2026-03-10",
                null, List.of(
                        Map.of("description", "Pembelian ATK", "unitPrice", 250000, "quantity", 10, "taxRate", 11)
                ));

        assertThat(created.get("id")).isNotNull();
        assertThat(created.get("billNumber").asText()).isNotEmpty();
        assertThat(created.get("vendorName").asText()).isEqualTo(vendorName);
        assertThat(created.get("vendorCode").asText()).startsWith("VND-");
        assertThat(created.get("status").asText()).isEqualTo("DRAFT");
        assertThat(created.get("amount").asDouble()).isEqualTo(2500000.0);
        assertThat(created.get("taxAmount").asDouble()).isEqualTo(275000.0);

        JsonNode lines = created.get("lines");
        assertThat(lines.size()).isEqualTo(1);
        assertThat(lines.get(0).get("description").asText()).isEqualTo("Pembelian ATK");
        assertThat(lines.get(0).get("quantity").asDouble()).isEqualTo(10.0);
        assertThat(lines.get(0).get("unitPrice").asDouble()).isEqualTo(250000.0);
        assertThat(lines.get(0).get("taxRate").asDouble()).isEqualTo(11.0);

        log.info("New vendor auto-creation test passed - bill: {}, vendor: {}",
                created.get("billNumber").asText(), created.get("vendorCode").asText());
    }

    @Test
    @DisplayName("POST /api/bills - creates bill with multiple lines")
    void testCreateBillWithMultipleLines() throws Exception {
        JsonNode created = createBill("PT Multi Line Vendor", "2026-02-05", "2026-03-05",
                "ML-001", List.of(
                        Map.of("description", "Item A", "unitPrice", 1000000, "quantity", 1),
                        Map.of("description", "Item B", "unitPrice", 500000, "quantity", 3),
                        Map.of("description", "Item C", "unitPrice", 200000, "quantity", 5, "taxRate", 11)
                ));

        assertThat(created.get("lines").size()).isEqualTo(3);
        // 1000000 + 1500000 + 1000000 = 3500000
        assertThat(created.get("amount").asDouble()).isEqualTo(3500000.0);
        // tax on Item C only: 1000000 * 11% = 110000
        assertThat(created.get("taxAmount").asDouble()).isEqualTo(110000.0);

        log.info("Multiple lines test passed - amount: {}, tax: {}",
                created.get("amount"), created.get("taxAmount"));
    }

    @Test
    @DisplayName("GET /api/bills - lists bills with pagination")
    void testListBills() throws Exception {
        // Create a bill first
        createBill("PT List Test Vendor", "2026-01-15", "2026-02-15",
                null, List.of(
                        Map.of("description", "Test item", "unitPrice", 100000, "quantity", 1)
                ));

        APIResponse response = get("/api/bills?page=0&size=10");
        assertThat(response.status()).isEqualTo(200);

        JsonNode body = parse(response);
        assertThat(body.has("items")).isTrue();
        assertThat(body.has("totalElements")).isTrue();
        assertThat(body.has("totalPages")).isTrue();
        assertThat(body.has("currentPage")).isTrue();
        assertThat(body.has("pageSize")).isTrue();
        assertThat(body.get("totalElements").asLong()).isGreaterThanOrEqualTo(1);

        JsonNode items = body.get("items");
        assertThat(items.isArray()).isTrue();
        assertThat(items.size()).isGreaterThanOrEqualTo(1);

        JsonNode first = items.get(0);
        assertThat(first.has("id")).isTrue();
        assertThat(first.has("billNumber")).isTrue();
        assertThat(first.has("vendorName")).isTrue();
        assertThat(first.has("status")).isTrue();

        log.info("List bills test passed - totalElements: {}", body.get("totalElements"));
    }

    @Test
    @DisplayName("GET /api/bills - filters by status")
    void testListBillsFilterByStatus() throws Exception {
        createBill("PT Filter Vendor", "2026-01-20", "2026-02-20",
                null, List.of(
                        Map.of("description", "Filter test", "unitPrice", 100000, "quantity", 1)
                ));

        APIResponse response = get("/api/bills?status=DRAFT");
        assertThat(response.status()).isEqualTo(200);

        JsonNode body = parse(response);
        JsonNode items = body.get("items");
        for (int i = 0; i < items.size(); i++) {
            assertThat(items.get(i).get("status").asText()).isEqualTo("DRAFT");
        }

        log.info("Filter by status test passed - {} DRAFT bills", items.size());
    }

    @Test
    @DisplayName("GET /api/bills/{id} - returns bill detail")
    void testGetBillDetail() throws Exception {
        JsonNode created = createBill("PT Detail Vendor", "2026-02-01", "2026-03-01",
                "DET-001", List.of(
                        Map.of("description", "Detail item", "unitPrice", 750000, "quantity", 2)
                ));

        String billId = created.get("id").asText();

        APIResponse response = get("/api/bills/" + billId);
        assertThat(response.status()).isEqualTo(200);

        JsonNode bill = parse(response);
        assertThat(bill.get("id").asText()).isEqualTo(billId);
        assertThat(bill.get("vendorName").asText()).isEqualTo("PT Detail Vendor");
        assertThat(bill.get("vendorInvoiceNumber").asText()).isEqualTo("DET-001");
        assertThat(bill.get("lines").size()).isEqualTo(1);
        assertThat(bill.get("lines").get(0).get("description").asText()).isEqualTo("Detail item");

        log.info("Bill detail test passed - billNumber: {}", bill.get("billNumber"));
    }

    @Test
    @DisplayName("POST /api/bills/{id}/approve - approves a draft bill")
    void testApproveBill() throws Exception {
        JsonNode created = createBill("PT Approve Vendor", "2026-02-01", "2026-03-01",
                null, List.of(
                        Map.of("description", "Approve item", "unitPrice", 500000, "quantity", 1)
                ));

        String billId = created.get("id").asText();

        APIResponse response = post("/api/bills/" + billId + "/approve", Map.of());
        assertThat(response.status()).isEqualTo(200);

        JsonNode approved = parse(response);
        assertThat(approved.get("status").asText()).isEqualTo("APPROVED");
        assertThat(approved.get("approvedAt")).isNotNull();
        assertThat(approved.get("approvedBy").asText()).isNotEmpty();

        log.info("Approve bill test passed - status: {}", approved.get("status"));
    }

    @Test
    @DisplayName("POST /api/bills/{id}/mark-paid - marks an approved bill as paid")
    void testMarkBillPaid() throws Exception {
        JsonNode created = createBill("PT Paid Vendor", "2026-02-01", "2026-03-01",
                null, List.of(
                        Map.of("description", "Paid item", "unitPrice", 1000000, "quantity", 1)
                ));

        String billId = created.get("id").asText();

        // Approve first
        post("/api/bills/" + billId + "/approve", Map.of());

        // Then mark as paid
        APIResponse response = post("/api/bills/" + billId + "/mark-paid", Map.of());
        assertThat(response.status()).isEqualTo(200);

        JsonNode paid = parse(response);
        assertThat(paid.get("status").asText()).isEqualTo("PAID");
        assertThat(paid.get("paidAt")).isNotNull();

        log.info("Mark paid test passed - status: {}", paid.get("status"));
    }

    @Test
    @DisplayName("API endpoints reject unauthenticated requests with 401")
    void testUnauthenticated() {
        APIResponse listResponse = apiContext.get("/api/bills");
        assertThat(listResponse.status()).isEqualTo(401);

        APIResponse createResponse = apiContext.post("/api/bills",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setData(Map.of("vendorName", "test")));
        assertThat(createResponse.status()).isEqualTo(401);

        log.info("Unauthenticated test passed");
    }

    // --- Helpers ---

    private JsonNode createBill(String vendorName, String billDate, String dueDate,
                                 String vendorInvoiceNumber, List<Map<String, Object>> lines) throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("vendorName", vendorName);
        request.put("billDate", billDate);
        request.put("dueDate", dueDate);
        if (vendorInvoiceNumber != null) {
            request.put("vendorInvoiceNumber", vendorInvoiceNumber);
        }
        request.put("lines", lines);

        APIResponse response = post("/api/bills", request);
        assertThat(response.status()).isEqualTo(201);

        return parse(response);
    }

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

    private JsonNode parse(APIResponse response) throws Exception {
        return objectMapper.readTree(response.text());
    }

    private String authenticateViaDeviceFlow() throws Exception {
        Map<String, String> codeRequest = new HashMap<>();
        codeRequest.put("clientId", "bill-api-test");

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

        page.locator("input[name='deviceName']").fill("Bill API Test Device");
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
