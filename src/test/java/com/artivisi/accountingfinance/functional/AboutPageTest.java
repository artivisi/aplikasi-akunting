package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.page.AboutPage;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("About Page")
class AboutPageTest extends PlaywrightTestBase {

    private AboutPage aboutPage;

    @BeforeEach
    void setUpTest() {
        this.aboutPage = new AboutPage(page, baseUrl());
        loginAsAdmin();
    }

    @Test
    @DisplayName("Should display about page with version information")
    void shouldDisplayAboutPageWithVersionInfo() {
        // Navigate to about page
        aboutPage.navigate();

        // Verify page loaded
        aboutPage.assertPageLoaded();

        // Verify page title
        aboutPage.assertPageTitle("Tentang Aplikasi");

        // Verify version information section is visible
        aboutPage.assertVersionSectionVisible();

        // Verify git commit ID is displayed
        aboutPage.assertCommitIdVisible();

        // Verify commit ID has expected format (7-40 characters hex)
        String commitId = aboutPage.getCommitId();
        assertTrue(commitId.matches("[0-9a-f]{7,40}"), 
            "Commit ID should be 7-40 hex characters, but was: " + commitId);

        // If git tag is available, verify it's displayed
        if (aboutPage.hasGitTag()) {
            String gitTag = aboutPage.getGitTag();
            assertTrue(!gitTag.isEmpty(), "Git tag should not be empty");
        }
    }

    @Test
    @DisplayName("Should display application name and description")
    void shouldDisplayApplicationInfo() {
        aboutPage.navigate();
        aboutPage.assertPageLoaded();

        // Verify application name is displayed
        assertThat(page.locator(PAGE_CONTENT + " h2")).containsText("Aplikasi Akunting");

        // Verify description is displayed
        assertThat(page.locator(PAGE_CONTENT + " h2 + p")).containsText("Aplikasi pembukuan");
    }
    
    private static final String PAGE_CONTENT = "#about-page-content";
}
