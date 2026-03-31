package com.artivisi.accountingfinance.functional.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Demo data loader for Online Seller industry (Toko Gadget Sejahtera).
 *
 * Loads seed pack + demo data including inventory transactions.
 *
 * Usage:
 *   ./mvnw test -Dtest=DemoOnlineSellerDataLoader
 */
@Slf4j
@DisplayName("Demo: Online Seller Data Loader")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DemoOnlineSellerDataLoader extends DemoDataLoaderBase {

    @Override
    protected String industryName() {
        return "Online Seller";
    }

    @Override
    protected String seedDataPath() {
        return "industry-seed/online-seller/seed-data";
    }

    @Override
    protected String demoDataPath() {
        return "src/test/resources/demo-data/online-seller";
    }

    @Test
    @Order(1)
    @DisplayName("1. Import seed data (COA, templates, products)")
    void importSeed() throws Exception {
        var result = importSeedData();
        log.info("Seed import complete: {} records", result.totalRecords());
    }

    @Test
    @Order(2)
    @DisplayName("2. Import demo data (company, transactions, inventory, payroll)")
    void importDemo() throws Exception {
        var result = importMasterData();
        log.info("Demo import complete: {} records", result.totalRecords());
    }

    @Test
    @Order(3)
    @DisplayName("3. Create demo users")
    void createUsers() {
        createDemoUsers();
    }

    @Test
    @Order(4)
    @DisplayName("4. Validate dashboard loads")
    void validateDashboardLoads() {
        validateDashboard();
    }

    @Test
    @Order(5)
    @DisplayName("5. Validate trial balance")
    void validateTrialBalanceLoads() {
        validateTrialBalance(java.time.LocalDate.of(2025, 12, 31));
    }

    @Test
    @Order(6)
    @DisplayName("6. Validate inventory stock")
    void validateInventory() {
        loginAsAdmin();
        navigateTo("/inventory/stock");
        waitForPageLoad();

        assertPageContains("Persediaan");
        log.info("Inventory stock validated");
    }

    @Test
    @Order(7)
    @DisplayName("7. Validate transactions exist")
    void validateTransactions() {
        loginAsAdmin();
        navigateTo("/transactions");
        waitForPageLoad();

        assertPageContains("TGS-");
        log.info("Transactions validated");
    }

    @Test
    @Order(8)
    @DisplayName("8. Validate payroll data")
    void validatePayroll() {
        loginAsAdmin();
        navigateTo("/payroll");
        waitForPageLoad();

        assertPageContains("2025");
        log.info("Payroll data validated");
    }

    private void assertPageContains(String text) {
        var body = page.locator("body");
        org.assertj.core.api.Assertions.assertThat(body.textContent()).contains(text);
    }
}
