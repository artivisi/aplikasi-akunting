package com.artivisi.accountingfinance.ui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

@DisplayName("Debug Account Form")
class DebugAccountFormTest extends PlaywrightTestBase {

    @Test
    @DisplayName("Debug Panduan Pengisian dropdown")
    void debugPanduanDropdown() {
        List<String> consoleErrors = new ArrayList<>();
        List<String> consoleWarnings = new ArrayList<>();
        List<String> allConsoleMessages = new ArrayList<>();

        // Capture all console messages
        page.onConsoleMessage(msg -> {
            String text = msg.type() + ": " + msg.text();
            allConsoleMessages.add(text);
            if ("error".equals(msg.type())) {
                consoleErrors.add(msg.text());
            } else if ("warning".equals(msg.type())) {
                consoleWarnings.add(msg.text());
            }
        });

        // Capture page errors
        page.onPageError(error -> {
            System.err.println("PAGE ERROR: " + error);
            consoleErrors.add("PAGE ERROR: " + error);
        });

        loginAsAdmin();
        navigateTo("/accounts/new");
        waitForPageLoad();

        // Print all console messages
        System.out.println("\n=== ALL CONSOLE MESSAGES ===");
        allConsoleMessages.forEach(System.out::println);

        System.out.println("\n=== CONSOLE ERRORS ===");
        consoleErrors.forEach(System.out::println);

        System.out.println("\n=== CONSOLE WARNINGS ===");
        consoleWarnings.forEach(System.out::println);

        // Take screenshot before clicking
        takeScreenshot("debug-01-before-click");

        // Try to click the Panduan Pengisian button
        System.out.println("\n=== ATTEMPTING TO CLICK PANDUAN PENGISIAN ===");

        // Check if Alpine.js is loaded
        Object alpineLoaded = page.evaluate("typeof Alpine !== 'undefined'");
        System.out.println("Alpine.js loaded: " + alpineLoaded);

        // Check the button state
        var button = page.locator("button:has-text('Panduan Pengisian')");
        System.out.println("Button visible: " + button.isVisible());
        System.out.println("Button HTML: " + button.evaluate("el => el.outerHTML"));

        // Try clicking
        button.click();

        // Wait a bit for any animations
        page.waitForTimeout(500);

        // Take screenshot after clicking
        takeScreenshot("debug-02-after-click");

        // Check the content panel - should be HIDDEN after first click (was open by default)
        var contentPanel = page.locator("text=Format Kode Akun");
        System.out.println("Content panel visible after 1st click (should be false): " + contentPanel.isVisible());

        // Take screenshot
        takeScreenshot("debug-03-collapsed");

        // Click again to expand
        button.click();
        page.waitForTimeout(500);

        System.out.println("Content panel visible after 2nd click (should be true): " + contentPanel.isVisible());
        takeScreenshot("debug-04-expanded-again");

        // Print any new console errors
        System.out.println("\n=== CONSOLE ERRORS AFTER CLICKS ===");
        consoleErrors.forEach(System.out::println);

        // Get page HTML for debugging
        System.out.println("\n=== PAGE TITLE ===");
        System.out.println(page.title());

        // Verify the toggle works
        if (consoleErrors.isEmpty()) {
            System.out.println("\n=== SUCCESS: No JavaScript errors! ===");
        }
    }
}
