package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.page.*;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Fixed Assets (Phase 4)")
class FixedAssetTest extends PlaywrightTestBase {

    private LoginPage loginPage;
    private AssetListPage listPage;
    private AssetFormPage formPage;
    private AssetDetailPage detailPage;
    private AssetCategoryListPage categoryListPage;
    private AssetCategoryFormPage categoryFormPage;

    @BeforeEach
    void setUp() {
        loginPage = new LoginPage(page, baseUrl());
        listPage = new AssetListPage(page, baseUrl());
        formPage = new AssetFormPage(page, baseUrl());
        detailPage = new AssetDetailPage(page, baseUrl());
        categoryListPage = new AssetCategoryListPage(page, baseUrl());
        categoryFormPage = new AssetCategoryFormPage(page, baseUrl());

        loginPage.navigate().loginAsAdmin();
    }

    @Nested
    @DisplayName("4.1 Asset CRUD Operations")
    class AssetCrudTests {

        @Test
        @DisplayName("Should display asset list page")
        void shouldDisplayAssetListPage() {
            listPage.navigate();

            listPage.assertPageTitleVisible();
            listPage.assertPageTitleText("Daftar Aset Tetap");
            listPage.assertTableVisible();
        }

        @Test
        @DisplayName("Should create asset and verify in list by searching")
        void shouldCreateAssetAndVerifyInList() {
            String uniqueCode = "AST-CRUD-" + System.currentTimeMillis();
            String uniqueName = "CRUD Test Asset";

            // Create asset
            formPage.navigateToNew();
            formPage.fillAssetCode(uniqueCode);
            formPage.fillName(uniqueName);
            formPage.selectFirstCategory();
            formPage.fillPurchaseDate("2025-01-01");
            formPage.fillPurchaseCost("10000000");
            formPage.fillDepreciationStartDate("2025-01-01");
            formPage.fillUsefulLifeMonths("48");
            formPage.fillResidualValue("0");
            formPage.clickSubmit();

            // Verify redirect to detail page
            detailPage.assertAssetNameText(uniqueName);
            detailPage.assertStatusText("Aktif");

            // Navigate to list and search for the created asset
            listPage.navigate();
            listPage.search(uniqueCode);

            // Verify asset is in the list
            assertThat(listPage.hasAssetWithCode(uniqueCode)).isTrue();
        }

        @Test
        @DisplayName("Should view asset detail after creation")
        void shouldViewAssetDetailAfterCreation() {
            String uniqueCode = "AST-DETAIL-" + System.currentTimeMillis();
            String uniqueName = "Detail View Asset";

            // Create asset
            formPage.navigateToNew();
            formPage.fillAssetCode(uniqueCode);
            formPage.fillName(uniqueName);
            formPage.selectFirstCategory();
            formPage.fillLocation("Kantor Pusat");
            formPage.fillPurchaseDate("2025-01-01");
            formPage.fillPurchaseCost("8000000");
            formPage.fillDepreciationStartDate("2025-01-01");
            formPage.fillUsefulLifeMonths("24");
            formPage.fillResidualValue("1000000");
            formPage.clickSubmit();

            // Verify detail page shows correct information
            detailPage.assertAssetNameText(uniqueName);
            detailPage.assertAssetCodeText(uniqueCode);
            detailPage.assertStatusText("Aktif");

            // Verify edit button is available
            assertThat(detailPage.isEditButtonVisible()).isTrue();
        }

        @Test
        @DisplayName("Should click from list to view asset detail")
        void shouldClickFromListToViewDetail() {
            String uniqueCode = "AST-LIST-" + System.currentTimeMillis();
            String uniqueName = "List Click Asset";

            // Create asset
            formPage.navigateToNew();
            formPage.fillAssetCode(uniqueCode);
            formPage.fillName(uniqueName);
            formPage.selectFirstCategory();
            formPage.fillPurchaseDate("2025-01-01");
            formPage.fillPurchaseCost("5000000");
            formPage.fillDepreciationStartDate("2025-01-01");
            formPage.fillUsefulLifeMonths("12");
            formPage.fillResidualValue("0");
            formPage.clickSubmit();

            // Navigate to list and search
            listPage.navigate();
            listPage.search(uniqueCode);

            // Click to view detail
            listPage.clickAssetDetail(uniqueCode);

            // Verify we're on the detail page
            detailPage.assertAssetNameText(uniqueName);
        }

        @Test
        @DisplayName("Should show no matching assets in search results")
        void shouldShowNoMatchingAssetsInSearch() {
            listPage.navigate();
            String nonExistentCode = "NONEXISTENT-" + System.currentTimeMillis();
            listPage.search(nonExistentCode);

            // Verify the nonexistent asset is not in the list
            assertThat(listPage.hasAssetWithCode(nonExistentCode)).isFalse();
        }
    }

    @Nested
    @DisplayName("4.2 Asset Category CRUD Operations")
    class AssetCategoryCrudTests {

        @Test
        @DisplayName("Should display category list with seed data")
        void shouldDisplayCategoryListWithSeedData() {
            categoryListPage.navigate();

            categoryListPage.assertPageTitleVisible();
            categoryListPage.assertPageTitleText("Kategori Aset");
            categoryListPage.assertTableVisible();

            // Seed data should have KOMPUTER category
            assertThat(categoryListPage.hasCategoryWithCode("KOMPUTER")).isTrue();
        }

        @Test
        @DisplayName("Should navigate to new category form")
        void shouldNavigateToNewCategoryForm() {
            categoryListPage.navigate();
            categoryListPage.clickNewCategoryButton();

            categoryFormPage.assertPageTitleText("Kategori Aset Baru");
        }

        @Test
        @DisplayName("Should create category and verify in list")
        void shouldCreateCategoryAndVerifyInList() {
            String uniqueCode = "CAT-" + System.currentTimeMillis();
            String uniqueName = "Test Category " + System.currentTimeMillis();

            // Create category
            categoryFormPage.navigateToNew();
            categoryFormPage.fillCode(uniqueCode);
            categoryFormPage.fillName(uniqueName);
            categoryFormPage.fillDescription("Test category description");
            categoryFormPage.selectDepreciationMethod("STRAIGHT_LINE");
            categoryFormPage.fillUsefulLifeMonths("36");
            categoryFormPage.selectFirstAssetAccount();
            categoryFormPage.selectFirstAccumulatedDepreciationAccount();
            categoryFormPage.selectFirstDepreciationExpenseAccount();
            categoryFormPage.clickSubmit();

            // Verify redirect to list page
            categoryListPage.assertPageTitleText("Kategori Aset");

            // Search and verify the created category is in the list
            categoryListPage.search(uniqueCode);
            assertThat(categoryListPage.hasCategoryWithCode(uniqueCode)).isTrue();
        }

        @Test
        @DisplayName("Should search categories")
        void shouldSearchCategories() {
            categoryListPage.navigate();
            categoryListPage.search("KOMPUTER");

            assertThat(categoryListPage.hasCategoryWithCode("KOMPUTER")).isTrue();
        }
    }

    @Nested
    @DisplayName("4.3 Depreciation Methods")
    class DepreciationMethodTests {

        @Test
        @DisplayName("Should create asset with straight-line depreciation")
        void shouldCreateAssetWithStraightLineDepreciation() {
            String uniqueCode = "AST-SL-" + System.currentTimeMillis();
            String uniqueName = "Straight Line Asset";

            formPage.navigateToNew();
            formPage.fillAssetCode(uniqueCode);
            formPage.fillName(uniqueName);
            formPage.selectFirstCategory();
            formPage.fillPurchaseDate("2025-01-01");
            formPage.fillPurchaseCost("12000000");
            formPage.fillDepreciationStartDate("2025-01-01");
            formPage.selectDepreciationMethod("STRAIGHT_LINE");
            formPage.fillUsefulLifeMonths("12");
            formPage.fillResidualValue("0");
            formPage.clickSubmit();

            // Verify creation - monthly depreciation should be 1,000,000
            detailPage.assertAssetNameText(uniqueName);
            detailPage.assertStatusText("Aktif");
        }

        @Test
        @DisplayName("Should create asset with declining balance depreciation")
        void shouldCreateAssetWithDecliningBalanceDepreciation() {
            String uniqueCode = "AST-DB-" + System.currentTimeMillis();
            String uniqueName = "Declining Balance Asset";

            formPage.navigateToNew();
            formPage.fillAssetCode(uniqueCode);
            formPage.fillName(uniqueName);
            formPage.selectFirstCategory();
            formPage.fillPurchaseDate("2025-01-01");
            formPage.fillPurchaseCost("100000000");
            formPage.fillDepreciationStartDate("2025-01-01");
            formPage.selectDepreciationMethod("DECLINING_BALANCE");
            formPage.fillUsefulLifeMonths("48");
            formPage.fillResidualValue("10000000");
            formPage.fillDepreciationRate("25");
            formPage.clickSubmit();

            // Verify creation
            detailPage.assertAssetNameText(uniqueName);
            detailPage.assertStatusText("Aktif");
        }
    }
}
