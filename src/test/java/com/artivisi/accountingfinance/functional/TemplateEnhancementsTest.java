package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.page.LoginPage;
import com.artivisi.accountingfinance.functional.page.TemplateDetailPage;
import com.artivisi.accountingfinance.functional.page.TemplateListPage;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Template Enhancements (Section 1.7)")
class TemplateEnhancementsTest extends PlaywrightTestBase {

    private LoginPage loginPage;
    private TemplateListPage templateListPage;
    private TemplateDetailPage templateDetailPage;

    // Template IDs from seed data
    private static final String INCOME_CONSULTING_TEMPLATE_ID = "e0000000-0000-0000-0000-000000000001";
    private static final String EXPENSE_SALARY_TEMPLATE_ID = "e0000000-0000-0000-0000-000000000004";
    // Test editable template from V902 migration
    private static final String TEST_EDITABLE_TEMPLATE_ID = "f0000000-0000-0000-0000-000000000001";

    @BeforeEach
    void setUp() {
        loginPage = new LoginPage(page, baseUrl());
        templateListPage = new TemplateListPage(page, baseUrl());
        templateDetailPage = new TemplateDetailPage(page, baseUrl());

        loginPage.navigate().loginAsAdmin();
    }

    @Nested
    @DisplayName("1.7.1 Search Functionality")
    class SearchTests {

        @Test
        @DisplayName("Should display search input on template list page")
        void shouldDisplaySearchInput() {
            templateListPage.navigate();

            templateListPage.assertSearchInputVisible();
        }

        @Test
        @DisplayName("Should filter templates by search query")
        void shouldFilterTemplatesBySearchQuery() {
            templateListPage.navigate();

            templateListPage.searchTemplates("Konsultasi");

            // Should show templates matching "Konsultasi"
            templateListPage.assertTemplateVisible("Pendapatan Jasa Konsultasi");
        }

        @Test
        @DisplayName("Should show no results for non-matching search")
        void shouldShowNoResultsForNonMatchingSearch() {
            templateListPage.navigate();

            templateListPage.searchTemplates("NonExistentTemplate12345");

            // Should show no templates
            assertThat(templateListPage.getTemplateCount()).isEqualTo(0);
        }

        @Test
        @DisplayName("Should search by description")
        void shouldSearchByDescription() {
            templateListPage.navigate();

            // Search for a word in the template description
            templateListPage.searchTemplates("gaji");

            templateListPage.assertTemplateVisible("Bayar Beban Gaji");
        }
    }

    @Nested
    @DisplayName("1.7.2 Tag Management")
    class TagTests {

        @Test
        @DisplayName("Should display tag input and add button on detail page")
        void shouldDisplayTagInputOnDetailPage() {
            templateDetailPage.navigate(TEST_EDITABLE_TEMPLATE_ID);

            templateDetailPage.assertAddTagButtonVisible();
        }

        @Test
        @DisplayName("Should display tag input field on detail page")
        void shouldDisplayTagInputField() {
            templateDetailPage.navigate(TEST_EDITABLE_TEMPLATE_ID);

            // Tag input should be visible (it's now always visible as a form)
            templateDetailPage.assertTagInputVisible();
        }
    }

    @Nested
    @DisplayName("1.7.3 User Favorites")
    class FavoritesTests {

        @Test
        @DisplayName("Should display favorite button on template cards")
        void shouldDisplayFavoriteButton() {
            templateListPage.navigate();

            // Favorite button should be visible on template cards
            templateListPage.assertFavoriteButtonVisible();
        }

        @Test
        @DisplayName("Should display favorites filter button")
        void shouldDisplayFavoritesFilter() {
            templateListPage.navigate();

            // Favorites filter button should be visible
            templateListPage.assertFavoritesFilterVisible();
        }
    }

    @Nested
    @DisplayName("1.7.4 Category Filter")
    class CategoryFilterTests {

        @Test
        @DisplayName("Should filter by category")
        void shouldFilterByCategory() {
            // Navigate with category filter
            page.navigate(baseUrl() + "/templates?category=INCOME");
            page.waitForLoadState();

            // Should show income templates
            templateListPage.assertTemplateVisible("Pendapatan Jasa Konsultasi");

            // Get count - should be filtered
            int incomeCount = templateListPage.getTemplateCount();

            // Navigate to all templates
            templateListPage.navigate();

            // Count should be higher or equal
            int allCount = templateListPage.getTemplateCount();
            assertThat(allCount).isGreaterThanOrEqualTo(incomeCount);
        }
    }
}
