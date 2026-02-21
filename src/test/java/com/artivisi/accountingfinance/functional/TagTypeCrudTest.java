package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.repository.TagTypeRepository;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@DisplayName("Tag Type CRUD Tests")
@Import(ServiceTestDataInitializer.class)
class TagTypeCrudTest extends PlaywrightTestBase {

    @Autowired
    private TagTypeRepository tagTypeRepository;

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    @Test
    @DisplayName("Should display tag type list page")
    void shouldDisplayTagTypeListPage() {
        navigateTo("/tags/types");
        waitForPageLoad();

        assertThat(page.locator("#page-title, h1").first()).containsText("Label Transaksi");
    }

    @Test
    @DisplayName("Should navigate to tag types from sidebar")
    void shouldNavigateFromSidebar() {
        navigateTo("/");
        waitForPageLoad();

        // Open Master Data collapsible section first
        page.locator("#nav-group-master").click();
        page.waitForTimeout(300);

        var sidebarLink = page.locator("#nav-tags");
        assertThat(sidebarLink).isVisible();
        sidebarLink.click();
        waitForPageLoad();

        assertThat(page.locator("#page-title, h1").first()).containsText("Label Transaksi");
    }

    @Test
    @DisplayName("Should create new tag type")
    void shouldCreateNewTagType() {
        navigateTo("/tags/types/new");
        waitForPageLoad();

        page.locator("#code").fill("NEWT");
        page.locator("#name").fill("Tipe Baru");
        page.locator("#description").fill("Tipe label baru untuk test");
        page.locator("#btn-save").click();
        waitForPageLoad();

        // Should redirect to list with success message
        assertThat(page.locator("body")).containsText("berhasil ditambahkan");
        assertThat(page.locator("[data-testid='tag-type-row-NEWT']")).isVisible();
    }

    @Test
    @DisplayName("Should show validation error for empty code")
    void shouldShowValidationErrorForEmptyCode() {
        navigateTo("/tags/types/new");
        waitForPageLoad();

        page.locator("#name").fill("Test");
        page.locator("#btn-save").click();
        waitForPageLoad();

        // Should show validation error
        assertThat(page.locator(".text-red-600").first()).isVisible();
    }

    @Test
    @DisplayName("Should show validation error for duplicate code")
    void shouldShowValidationErrorForDuplicateCode() {
        // Create first tag type
        navigateTo("/tags/types/new");
        waitForPageLoad();
        page.locator("#code").fill("DUP");
        page.locator("#name").fill("Duplikat Test");
        page.locator("#btn-save").click();
        waitForPageLoad();

        // Try creating same code
        navigateTo("/tags/types/new");
        waitForPageLoad();
        page.locator("#code").fill("DUP");
        page.locator("#name").fill("Duplikat Lain");
        page.locator("#btn-save").click();
        waitForPageLoad();

        assertThat(page.locator(".text-red-600").first()).isVisible();
    }

    @Test
    @DisplayName("Should edit tag type")
    void shouldEditTagType() {
        // Create tag type first
        navigateTo("/tags/types/new");
        waitForPageLoad();
        page.locator("#code").fill("EDIT");
        page.locator("#name").fill("Untuk Edit");
        page.locator("#btn-save").click();
        waitForPageLoad();

        // Click edit
        var editLink = page.locator("[data-testid='tag-type-row-EDIT'] a:has-text('Edit')");
        editLink.click();
        waitForPageLoad();

        // Update name
        page.locator("#name").fill("Sudah Diedit");
        page.locator("#btn-save").click();
        waitForPageLoad();

        assertThat(page.locator("body")).containsText("berhasil diubah");
    }

    @Test
    @DisplayName("Should delete tag type without tags")
    void shouldDeleteTagTypeWithoutTags() {
        // Create tag type
        navigateTo("/tags/types/new");
        waitForPageLoad();
        page.locator("#code").fill("DEL");
        page.locator("#name").fill("Untuk Hapus");
        page.locator("#btn-save").click();
        waitForPageLoad();

        // Delete
        page.onDialog(dialog -> dialog.accept());
        page.locator("[data-testid='tag-type-row-DEL'] button:has-text('Hapus')").click();
        waitForPageLoad();

        assertThat(page.locator("body")).containsText("berhasil dihapus");
    }

    @Test
    @DisplayName("Should search tag types")
    void shouldSearchTagTypes() {
        // Create tag types
        navigateTo("/tags/types/new");
        waitForPageLoad();
        page.locator("#code").fill("SRCH");
        page.locator("#name").fill("Pencarian Test");
        page.locator("#btn-save").click();
        waitForPageLoad();

        // Search
        page.locator("#search-input").fill("SRCH");
        page.waitForTimeout(500);

        assertThat(page.locator("[data-testid='tag-type-row-SRCH']")).isVisible();
    }
}
