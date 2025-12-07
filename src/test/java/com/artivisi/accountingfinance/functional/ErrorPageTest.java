package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import com.microsoft.playwright.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DisplayName("Error Pages")
class ErrorPageTest extends PlaywrightTestBase {

    @BeforeEach
    void setUpTest() {
        loginAsAdmin();
    }

    @Test
    @DisplayName("404 page should return HTML with error-404-page ID, not JSON")
    void shouldShow404HtmlPageNotJson() {
        Response response = page.navigate(baseUrl() + "/non-existent-page-xyz");

        assertEquals(404, response.status());

        // Verify HTML page is returned (not JSON)
        assertThat(page.locator("#error-404-page")).isVisible();

        // Verify it's not JSON response
        String content = page.content();
        assertFalse(content.trim().startsWith("{"), "Should return HTML, not JSON");
    }

    @Test
    @DisplayName("404 page for entity not found should return HTML with error-404-page ID")
    void shouldShow404HtmlForEntityNotFound() {
        Response response = page.navigate(baseUrl() + "/invoices/NON-EXISTENT-INVOICE");

        assertEquals(404, response.status());
        assertThat(page.locator("#error-404-page")).isVisible();

        String content = page.content();
        assertFalse(content.trim().startsWith("{"), "Should return HTML, not JSON");
    }

    @Test
    @DisplayName("403 page should return HTML with access-denied-page ID, not JSON")
    void shouldShow403HtmlPageNotJson() {
        // Logout and login as employee (no dashboard access)
        page.navigate(baseUrl() + "/logout");
        waitForPageLoad();

        navigateTo("/login");
        waitForPageLoad();
        page.fill("input[name='username']", "employee");
        page.fill("input[name='password']", "admin");
        page.click("button[type='submit']");
        waitForPageLoad();

        // Try to access admin-only page
        Response response = page.navigate(baseUrl() + "/users");

        assertEquals(403, response.status());
        assertThat(page.locator("#access-denied-page")).isVisible();

        String content = page.content();
        assertFalse(content.trim().startsWith("{"), "Should return HTML, not JSON");
    }
}
