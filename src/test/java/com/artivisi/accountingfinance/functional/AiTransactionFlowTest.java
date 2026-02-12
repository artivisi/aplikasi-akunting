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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Functional test for AI-assisted transaction flow with enhanced template metadata.
 * Tests the complete workflow:
 * 1. Device authentication (get access token)
 * 2. Fetch templates with enhanced metadata
 * 3. AI matches template based on metadata
 * 4. Create transaction directly (skip draft)
 * 5. Verify transaction posted
 */
@Slf4j
@DisplayName("AI-Assisted Transaction Flow - Functional Tests")
@Import(ServiceTestDataInitializer.class)
class AiTransactionFlowTest extends PlaywrightTestBase {

    private APIRequestContext apiContext;
    private ObjectMapper objectMapper;
    private String accessToken;
    private Path screenshotDir;

    @BeforeEach
    void setUp() throws Exception {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        // Create API request context
        apiContext = playwright.request().newContext(new APIRequest.NewContextOptions()
                .setBaseURL(baseUrl()));

        // Create screenshots directory for user manual (matches other functional tests)
        screenshotDir = Paths.get("target/screenshots/ai-transaction");
        Files.createDirectories(screenshotDir);

        // Get access token via device flow
        accessToken = authenticateViaDeviceFlow();
    }

    @AfterEach
    void tearDown() {
        if (apiContext != null) {
            apiContext.dispose();
        }
    }

    @Test
    @DisplayName("Complete AI workflow: auth → fetch templates → match template → post transaction")
    void testCompleteAiWorkflow() throws Exception {
        log.info("=== AI-Assisted Transaction Flow Test ===");

        // ============================================
        // Step 1: Fetch template catalog with enhanced metadata
        // ============================================
        log.info("Step 1: Fetching template catalog...");

        APIResponse templatesResponse = apiContext.get("/api/templates",
                RequestOptions.create()
                        .setHeader("Authorization", "Bearer " + accessToken));

        if (!templatesResponse.ok()) {
            log.error("Failed to fetch templates - Status: {}, Response: {}",
                    templatesResponse.status(), templatesResponse.text());
        }
        assertThat(templatesResponse.ok()).isTrue();

        String templatesJson = templatesResponse.text();
        saveToFile(screenshotDir.resolve("01-templates-response.json"), templatesJson);

        JsonNode templates = objectMapper.readTree(templatesJson);
        assertThat(templates.isArray()).isTrue();
        assertThat(templates.size()).isGreaterThan(0);

        log.info("✓ Fetched " + templates.size() + " templates");

        // Display first template with enhanced metadata
        if (templates.size() > 0) {
            JsonNode firstTemplate = templates.get(0);
            log.info("\nExample template:");
            log.debug("  Name: " + firstTemplate.get("name").asText());
            log.debug("  Semantic: " + (firstTemplate.has("semanticDescription")
                    ? firstTemplate.get("semanticDescription").asText() : "N/A"));
            log.debug("  Keywords: " + (firstTemplate.has("keywords")
                    ? firstTemplate.get("keywords").toString() : "[]"));
            log.debug("  Examples: " + (firstTemplate.has("exampleMerchants")
                    ? firstTemplate.get("exampleMerchants").toString() : "[]"));
        }

        // ============================================
        // Step 2: AI analyzes receipt and matches template
        // ============================================
        log.info("\nStep 2: AI analyzing receipt...");

        // Simulate AI analysis of a coffee shop receipt
        String merchant = "Starbucks Grand Indonesia";
        int amount = 85000;
        String category = "Food & Beverage";

        log.info("Receipt data:");
        log.debug("  Merchant: " + merchant);
        log.debug("  Amount: Rp " + amount);
        log.debug("  Category: " + category);

        // AI matches template by keywords
        String selectedTemplateId = null;
        String selectedTemplateName = null;
        for (JsonNode template : templates) {
            JsonNode keywords = template.get("keywords");
            if (keywords != null && keywords.isArray()) {
                for (JsonNode keyword : keywords) {
                    String kw = keyword.asText().toLowerCase();
                    if (category.toLowerCase().contains(kw) ||
                        kw.contains("meal") || kw.contains("food") || kw.contains("beverage")) {
                        selectedTemplateId = template.get("id").asText();
                        selectedTemplateName = template.get("name").asText();
                        log.debug("\n✓ AI matched template: " + selectedTemplateName);
                        log.debug("  Template ID: " + selectedTemplateId);
                        log.info("  Match reason: keyword '" + kw + "'");
                        break;
                    }
                }
            }
            if (selectedTemplateId != null) break;
        }

        // Fallback: use first template if no match
        if (selectedTemplateId == null) {
            selectedTemplateId = templates.get(0).get("id").asText();
            selectedTemplateName = templates.get(0).get("name").asText();
            log.debug("\n⚠ No keyword match, using first template: " + selectedTemplateName);
        }

        assertThat(selectedTemplateId).isNotNull();

        // ============================================
        // Step 3: User approves in CLI (simulated)
        // ============================================
        log.info("\nStep 3: User consultation...");
        log.info("AI presents to user:");
        log.info("  📄 Analyzed receipt:");
        log.debug("     • Merchant: " + merchant);
        log.debug("     • Amount: Rp " + amount);
        log.info("     • Template: \"" + selectedTemplateName + "\"");
        log.info("     Options: [Yes] Edit | Different Template | Cancel");
        log.info("\n✓ User selected: Yes (approved)");

        // ============================================
        // Step 4: Post transaction directly
        // ============================================
        log.info("\nStep 4: Posting transaction directly...");

        Map<String, Object> transactionRequest = new HashMap<>();
        transactionRequest.put("templateId", selectedTemplateId);
        transactionRequest.put("merchant", merchant);
        transactionRequest.put("amount", amount);
        transactionRequest.put("transactionDate", "2026-02-12");
        transactionRequest.put("currency", "IDR");
        transactionRequest.put("description", "Team coffee break at Starbucks");
        transactionRequest.put("category", category);
        transactionRequest.put("items", new String[]{"Caffe Latte Grande", "Blueberry Muffin"});
        transactionRequest.put("source", "claude-code");
        transactionRequest.put("userApproved", true);

        String requestJson = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(transactionRequest);
        saveToFile(screenshotDir.resolve("02-transaction-request.json"), requestJson);

        APIResponse transactionResponse = apiContext.post("/api/transactions",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Authorization", "Bearer " + accessToken)
                        .setData(transactionRequest));

        log.debug("Response status: " + transactionResponse.status());

        if (!transactionResponse.ok()) {
            log.info("ERROR: Failed to create transaction");
            log.debug("Status: " + transactionResponse.status());
            log.debug("Response: " + transactionResponse.text());
        }

        assertThat(transactionResponse.status()).isEqualTo(201);

        String transactionJson = transactionResponse.text();
        saveToFile(screenshotDir.resolve("03-transaction-response.json"), transactionJson);

        JsonNode transaction = objectMapper.readTree(transactionJson);

        String transactionNumber = transaction.get("transactionNumber").asText();
        String transactionId = transaction.get("transactionId").asText();
        String status = transaction.get("status").asText();

        assertThat(transactionNumber).isNotNull().isNotEmpty();
        assertThat(status).isEqualTo("POSTED");

        log.info("\n✓ Transaction posted successfully!");
        log.debug("  Transaction Number: " + transactionNumber);
        log.debug("  Transaction ID: " + transactionId);
        log.debug("  Status: " + status);

        // Display journal entries
        JsonNode journalEntries = transaction.get("journalEntries");
        if (journalEntries != null && journalEntries.isArray()) {
            log.info("\n  Journal Entries:");
            for (JsonNode entry : journalEntries) {
                String accountCode = entry.get("accountCode").asText();
                String accountName = entry.get("accountName").asText();
                int debit = entry.get("debitAmount").asInt();
                int credit = entry.get("creditAmount").asInt();

                if (debit > 0) {
                    log.debug("    [DEBIT]  " + accountCode + " - " + accountName + ": Rp " + debit);
                } else {
                    log.debug("    [CREDIT] " + accountCode + " - " + accountName + ": Rp " + credit);
                }
            }
        }

        // ============================================
        // Step 5: Verify transaction in web UI
        // ============================================
        log.info("\nStep 5: Verifying in web UI...");

        loginAsAdmin();
        navigateTo("/transactions");
        waitForPageLoad();

        page.screenshot(new com.microsoft.playwright.Page.ScreenshotOptions()
                .setPath(screenshotDir.resolve("04-transactions-list.png"))
                .setFullPage(true));

        // Search for transaction
        assertThat(page.content()).contains(transactionNumber);
        log.info("✓ Transaction visible in web UI");

        // ============================================
        // Test Summary
        // ============================================
        log.info("\n=== Test Summary ===");
        log.info("✓ Authenticated via device flow");
        log.info("✓ Fetched " + templates.size() + " templates with metadata");
        log.debug("✓ AI matched template: " + selectedTemplateName);
        log.info("✓ User approved transaction");
        log.debug("✓ Posted transaction: " + transactionNumber);
        log.info("✓ Verified in web UI");
        log.info("========================\n");

        log.debug("Screenshots and data saved to: " + screenshotDir.toAbsolutePath());
    }

    @Test
    @DisplayName("AI workflow with template metadata enhancement")
    void testTemplateMetadataEnhancement() throws Exception {
        log.info("\n=== Template Metadata Enhancement Test ===\n");

        // Fetch single template with full metadata
        APIResponse templatesResponse = apiContext.get("/api/templates",
                RequestOptions.create()
                        .setHeader("Authorization", "Bearer " + accessToken));

        if (!templatesResponse.ok()) {
            log.error("Failed to fetch templates - Status: {}, Response: {}",
                    templatesResponse.status(), templatesResponse.text());
        }
        assertThat(templatesResponse.ok()).isTrue();

        JsonNode templates = objectMapper.readTree(templatesResponse.text());
        assertThat(templates.size()).isGreaterThan(0);

        JsonNode firstTemplate = templates.get(0);
        String templateId = firstTemplate.get("id").asText();

        // Get single template
        APIResponse templateResponse = apiContext.get("/api/templates/" + templateId,
                RequestOptions.create()
                        .setHeader("Authorization", "Bearer " + accessToken));

        assertThat(templateResponse.ok()).isTrue();

        String templateJson = templateResponse.text();
        saveToFile(screenshotDir.resolve("05-template-detail.json"), templateJson);

        JsonNode template = objectMapper.readTree(templateJson);

        log.info("Template Details:");
        log.debug("  ID: " + template.get("id").asText());
        log.debug("  Name: " + template.get("name").asText());
        log.debug("  Category: " + template.get("category").asText());
        log.debug("  Description: " + (template.has("description") ? template.get("description").asText() : "N/A"));
        log.debug("  Semantic: " + (template.has("semanticDescription") ? template.get("semanticDescription").asText() : "N/A"));
        log.debug("  Keywords: " + (template.has("keywords") ? template.get("keywords").toString() : "[]"));
        log.debug("  Examples: " + (template.has("exampleMerchants") ? template.get("exampleMerchants").toString() : "[]"));
        log.debug("  Amount Range: " +
                (template.has("typicalAmountMin") ? template.get("typicalAmountMin").asInt() : "N/A") + " - " +
                (template.has("typicalAmountMax") ? template.get("typicalAmountMax").asInt() : "N/A"));

        // Verify enhanced metadata fields exist
        assertThat(template.has("id")).isTrue();
        assertThat(template.has("name")).isTrue();
        assertThat(template.has("category")).isTrue();
        assertThat(template.has("keywords")).isTrue();
        assertThat(template.has("exampleMerchants")).isTrue();

        log.info("\n✓ Template metadata verified");
        log.info("========================\n");
    }

    @Test
    @DisplayName("Draft workflow still works (v1 API)")
    void testDraftWorkflowCompatibility() throws Exception {
        log.info("\n=== Draft Workflow Compatibility Test ===\n");

        // Create draft from receipt (v1 API)
        Map<String, Object> draftRequest = new HashMap<>();
        draftRequest.put("merchant", "McDonald's Plaza Senayan");
        draftRequest.put("amount", 65000);
        draftRequest.put("transactionDate", "2026-02-12");
        draftRequest.put("currency", "IDR");
        draftRequest.put("category", "Food & Beverage");
        draftRequest.put("items", new String[]{"Big Mac Meal", "McFlurry"});
        draftRequest.put("confidence", 0.95);
        draftRequest.put("source", "claude-code");

        String requestJson = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(draftRequest);
        saveToFile(screenshotDir.resolve("06-draft-request.json"), requestJson);

        APIResponse draftResponse = apiContext.post("/api/drafts/from-receipt",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Authorization", "Bearer " + accessToken)
                        .setData(draftRequest));

        assertThat(draftResponse.status()).isEqualTo(201);

        String draftJson = draftResponse.text();
        saveToFile(screenshotDir.resolve("07-draft-response.json"), draftJson);

        JsonNode draft = objectMapper.readTree(draftJson);

        String draftId = draft.get("draftId").asText();
        String draftStatus = draft.get("status").asText();

        assertThat(draftId).isNotEmpty();
        assertThat(draftStatus).isEqualTo("PENDING");

        log.debug("✓ Created draft: " + draftId);
        log.debug("  Status: " + draftStatus);
        log.debug("  Merchant: " + draft.get("merchant").asText());

        // Get suggested template
        JsonNode suggestedTemplate = draft.get("suggestedTemplate");
        if (suggestedTemplate != null && !suggestedTemplate.isNull()) {
            log.debug("  Suggested template: " + suggestedTemplate.get("name").asText());
        } else {
            log.info("  Suggested template: (none - will be selected during approval)");
        }

        log.info("\n✓ Draft workflow (v1) still works");
        log.info("========================\n");
    }

    /**
     * Authenticate via device flow and return access token.
     */
    private String authenticateViaDeviceFlow() throws Exception {
        log.info("Authenticating via device flow...");

        // Request device code
        Map<String, String> codeRequest = new HashMap<>();
        codeRequest.put("clientId", "playwright-test");

        APIResponse codeResponse = apiContext.post("/api/device/code",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setData(codeRequest));

        assertThat(codeResponse.ok()).isTrue();

        JsonNode codeData = objectMapper.readTree(codeResponse.text());
        String deviceCode = codeData.get("deviceCode").asText();
        String userCode = codeData.get("userCode").asText();

        log.debug("Device code: " + deviceCode);
        log.debug("User code: " + userCode);

        // User authorizes via browser
        loginAsAdmin();
        navigateTo("/device?code=" + userCode);
        waitForPageLoad();

        page.screenshot(new com.microsoft.playwright.Page.ScreenshotOptions()
                .setPath(screenshotDir.resolve("00-device-authorization.png"))
                .setFullPage(true));

        page.locator("input[name='deviceName']").fill("AI Test Device");
        page.locator("button[type='submit']:has-text('Otorisasi Perangkat')").click();
        waitForPageLoad();

        // Poll for token
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
                String token = tokenData.get("accessToken").asText();
                log.info("✓ Access token received");
                return token;
            }
        }

        throw new RuntimeException("Failed to get access token");
    }

    /**
     * Save content to file for user manual.
     */
    private void saveToFile(Path path, String content) throws Exception {
        Files.writeString(path, content);
    }
}
