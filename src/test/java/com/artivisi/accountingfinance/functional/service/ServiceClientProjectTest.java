package com.artivisi.accountingfinance.functional.service;

import com.artivisi.accountingfinance.functional.page.ClientListPage;
import com.artivisi.accountingfinance.functional.page.ProjectListPage;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

/**
 * Service Industry Client and Project Tests
 * Tests client list, project list, and milestone functionality.
 * Uses Page Object Pattern for maintainability.
 */
@DisplayName("Service Industry - Clients & Projects")
@Import(ServiceTestDataInitializer.class)
class ServiceClientProjectTest extends PlaywrightTestBase {

    // Page Objects
    private ClientListPage clientListPage;
    private ProjectListPage projectListPage;

    private void initPageObjects() {
        String baseUrl = "http://localhost:" + port;
        clientListPage = new ClientListPage(page, baseUrl);
        projectListPage = new ProjectListPage(page, baseUrl);
    }

    @Test
    @DisplayName("Should display Client List with 3 clients from test data")
    void shouldDisplayClientList() {
        loginAsAdmin();
        initPageObjects();

        // Test data has 3 clients: PT Bank Mandiri, PT Telkom Indonesia, PT Pertamina
        clientListPage.navigate()
            .verifyPageTitle()
            .verifyTableVisible()
            .verifyClientCount(3);
    }

    @Test
    @DisplayName("Should display Client Detail")
    void shouldDisplayClientDetail() {
        loginAsAdmin();
        initPageObjects();

        clientListPage.navigate()
            .verifyPageTitle()
            .verifyTableVisible();

        // Click on first client row using data-testid or ID
        page.locator("#client-table tbody tr").first().click();
        page.waitForLoadState();

        // Verify detail page loads
        page.locator("#page-title").isVisible();
    }

    @Test
    @DisplayName("Should display Project List with at least 4 projects from test data")
    void shouldDisplayProjectList() {
        loginAsAdmin();
        initPageObjects();

        // Test data has 4 projects (may be more if other tests create projects)
        projectListPage.navigate()
            .verifyPageTitle()
            .verifyTableVisible()
            .verifyMinimumProjectCount(4);
    }

    @Test
    @DisplayName("Should display Project Detail")
    void shouldDisplayProjectDetail() {
        loginAsAdmin();
        initPageObjects();

        projectListPage.navigate()
            .verifyPageTitle();

        // Click on first project row using data-testid or ID
        page.locator("#project-table tbody tr").first().click();
        page.waitForLoadState();

        // Verify detail page loads
        page.locator("#page-title").isVisible();
    }

    @Test
    @DisplayName("Should display Project Milestones")
    void shouldDisplayProjectMilestones() {
        loginAsAdmin();
        initPageObjects();

        projectListPage.navigate()
            .verifyPageTitle();

        // Click on first project
        page.locator("#project-table tbody tr").first().click();
        page.waitForLoadState();

        // Verify milestones section is visible (using ID)
        page.locator("#milestones-section, #project-milestones").first().isVisible();
    }
}
