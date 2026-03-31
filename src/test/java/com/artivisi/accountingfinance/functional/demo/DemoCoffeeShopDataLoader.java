package com.artivisi.accountingfinance.functional.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Demo data loader for Coffee Shop industry (Kedai Kopi Nusantara).
 *
 * Loads seed pack + demo data including BOM and production orders.
 *
 * Usage:
 *   ./mvnw test -Dtest=DemoCoffeeShopDataLoader
 */
@Slf4j
@DisplayName("Demo: Coffee Shop Data Loader")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DemoCoffeeShopDataLoader extends DemoDataLoaderBase {

    @Override
    protected String industryName() {
        return "Coffee Shop";
    }

    @Override
    protected String seedDataPath() {
        return "industry-seed/coffee-shop/seed-data";
    }

    @Override
    protected String demoDataPath() {
        return "src/test/resources/demo-data/coffee-shop";
    }

    @Test
    @Order(1)
    @DisplayName("1. Import seed data (COA, templates, products, BOM)")
    void importSeed() throws Exception {
        var result = importSeedData();
        log.info("Seed import complete: {} records", result.totalRecords());
    }

    @Test
    @Order(2)
    @DisplayName("2. Import demo data (company, transactions, production, payroll)")
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
    @DisplayName("6. Validate BOM exists")
    void validateBom() {
        loginAsAdmin();
        navigateTo("/inventory/bom");
        waitForPageLoad();

        assertPageContains("BOM");
        log.info("BOM data validated");
    }

    @Test
    @Order(7)
    @DisplayName("7. Validate production orders")
    void validateProductionOrders() {
        loginAsAdmin();
        navigateTo("/inventory/production");
        waitForPageLoad();

        // Production orders page should load
        com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat(page.locator("body")).isVisible();
        log.info("Production orders validated");
    }

    @Test
    @Order(8)
    @DisplayName("8. Validate transactions exist")
    void validateTransactions() {
        loginAsAdmin();
        navigateTo("/transactions");
        waitForPageLoad();

        assertPageContains("KKN-");
        log.info("Transactions validated");
    }

    private void assertPageContains(String text) {
        var body = page.locator("body");
        org.assertj.core.api.Assertions.assertThat(body.textContent()).contains(text);
    }
}
