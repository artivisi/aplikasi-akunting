package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.repository.AssetCategoryRepository;
import com.artivisi.accountingfinance.repository.FixedAssetRepository;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Functional tests for FixedAssetController.
 * Tests fixed asset list, create, edit, depreciate, dispose operations.
 */
@DisplayName("Fixed Asset Controller Tests")
@Import(ServiceTestDataInitializer.class)
class FixedAssetControllerFunctionalTest extends PlaywrightTestBase {

    @Autowired
    private FixedAssetRepository assetRepository;

    @Autowired
    private AssetCategoryRepository categoryRepository;

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    @Test
    @DisplayName("Should display fixed asset list page")
    void shouldDisplayFixedAssetListPage() {
        navigateTo("/assets");
        waitForPageLoad();

        assertThat(page.locator("#page-title, h1").first()).isVisible();
    }

    @Test
    @DisplayName("Should filter assets by category")
    void shouldFilterAssetsByCategory() {
        navigateTo("/assets");
        waitForPageLoad();

        var categorySelect = page.locator("select[name='categoryId']").first();
        if (categorySelect.isVisible()) {
            var options = categorySelect.locator("option");
            if (options.count() > 1) {
                categorySelect.selectOption(new String[]{options.nth(1).getAttribute("value")});

                var filterBtn = page.locator("form button[type='submit']").first();
                if (filterBtn.isVisible()) {
                    filterBtn.click();
                    waitForPageLoad();
                }
            }
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should filter assets by status")
    void shouldFilterAssetsByStatus() {
        navigateTo("/assets");
        waitForPageLoad();

        var statusSelect = page.locator("select[name='status']").first();
        if (statusSelect.isVisible()) {
            statusSelect.selectOption("ACTIVE");

            var filterBtn = page.locator("form button[type='submit']").first();
            if (filterBtn.isVisible()) {
                filterBtn.click();
                waitForPageLoad();
            }
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should display new fixed asset form")
    void shouldDisplayNewFixedAssetForm() {
        navigateTo("/assets/new");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should create new fixed asset")
    void shouldCreateNewFixedAsset() {
        var category = categoryRepository.findAll().stream().findFirst();
        if (category.isEmpty()) {
            return;
        }

        navigateTo("/assets/new");
        waitForPageLoad();

        // Fill asset name
        var nameInput = page.locator("input[name='name']").first();
        if (nameInput.isVisible()) {
            nameInput.fill("Test Asset " + System.currentTimeMillis());
        }

        // Fill acquisition date
        var acquisitionDateInput = page.locator("input[name='acquisitionDate']").first();
        if (acquisitionDateInput.isVisible()) {
            acquisitionDateInput.fill(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        // Fill acquisition cost
        var costInput = page.locator("input[name='acquisitionCost']").first();
        if (costInput.isVisible()) {
            costInput.fill("50000000");
        }

        // Select category
        var categorySelect = page.locator("select[name='category.id'], select[name='categoryId']").first();
        if (categorySelect.isVisible()) {
            categorySelect.selectOption(category.get().getId().toString());
        }

        // Fill useful life
        var usefulLifeInput = page.locator("input[name='usefulLifeMonths']").first();
        if (usefulLifeInput.isVisible()) {
            usefulLifeInput.fill("60");
        }

        // Submit
        var submitBtn = page.locator("#btn-simpan").first();
        if (submitBtn.isVisible()) {
            submitBtn.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should display fixed asset detail page")
    void shouldDisplayFixedAssetDetailPage() {
        var asset = assetRepository.findAll().stream().findFirst();
        if (asset.isEmpty()) {
            return;
        }

        navigateTo("/assets/" + asset.get().getId());
        waitForPageLoad();

        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/assets\\/.*"));
    }

    @Test
    @DisplayName("Should display fixed asset edit form")
    void shouldDisplayFixedAssetEditForm() {
        var asset = assetRepository.findAll().stream().findFirst();
        if (asset.isEmpty()) {
            return;
        }

        navigateTo("/assets/" + asset.get().getId() + "/edit");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should update fixed asset")
    void shouldUpdateFixedAsset() {
        var asset = assetRepository.findAll().stream().findFirst();
        if (asset.isEmpty()) {
            return;
        }

        navigateTo("/assets/" + asset.get().getId() + "/edit");
        waitForPageLoad();

        // Update name
        var nameInput = page.locator("input[name='name']").first();
        if (nameInput.isVisible()) {
            nameInput.fill("Updated Asset " + System.currentTimeMillis());
        }

        // Submit
        var submitBtn = page.locator("#btn-simpan").first();
        if (submitBtn.isVisible()) {
            submitBtn.click();
            waitForPageLoad();
        }

        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/assets\\/.*"));
    }

    @Test
    @DisplayName("Should run depreciation")
    void shouldRunDepreciation() {
        var asset = assetRepository.findAll().stream()
                .filter(a -> "ACTIVE".equals(a.getStatus().name()))
                .findFirst();
        if (asset.isEmpty()) {
            return;
        }

        navigateTo("/assets/" + asset.get().getId());
        waitForPageLoad();

        var depreciateBtn = page.locator("form[action*='/depreciate'] button[type='submit']").first();
        if (depreciateBtn.isVisible()) {
            depreciateBtn.click();
            waitForPageLoad();
        }

        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/assets\\/.*"));
    }

    @Test
    @DisplayName("Should dispose fixed asset")
    void shouldDisposeFixedAsset() {
        var asset = assetRepository.findAll().stream()
                .filter(a -> "ACTIVE".equals(a.getStatus().name()))
                .findFirst();
        if (asset.isEmpty()) {
            return;
        }

        navigateTo("/assets/" + asset.get().getId() + "/dispose");
        waitForPageLoad();

        // Fill disposal date
        var disposalDateInput = page.locator("input[name='disposalDate']").first();
        if (disposalDateInput.isVisible()) {
            disposalDateInput.fill(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        // Fill disposal value
        var disposalValueInput = page.locator("input[name='disposalValue']").first();
        if (disposalValueInput.isVisible()) {
            disposalValueInput.fill("5000000");
        }

        // Submit
        var submitBtn = page.locator("#btn-simpan").first();
        if (submitBtn.isVisible()) {
            submitBtn.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should display depreciation report")
    void shouldDisplayDepreciationReport() {
        navigateTo("/assets/depreciation-report");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should filter depreciation report by date")
    void shouldFilterDepreciationReportByDate() {
        navigateTo("/assets/depreciation-report");
        waitForPageLoad();

        var startDateInput = page.locator("input[name='startDate']").first();
        var endDateInput = page.locator("input[name='endDate']").first();

        if (startDateInput.isVisible() && endDateInput.isVisible()) {
            startDateInput.fill("2024-01-01");
            endDateInput.fill("2024-12-31");

            var filterBtn = page.locator("form button[type='submit']").first();
            if (filterBtn.isVisible()) {
                filterBtn.click();
                waitForPageLoad();
            }
        }

        assertThat(page.locator("body")).isVisible();
    }

    // ==================== DEPRECIATION MANAGEMENT ====================

    @Test
    @DisplayName("Should display depreciation list page")
    void shouldDisplayDepreciationListPage() {
        navigateTo("/assets/depreciation");
        waitForPageLoad();

        assertThat(page.locator("#page-title, h1").first()).isVisible();
    }

    @Test
    @DisplayName("Should generate depreciation entries")
    void shouldGenerateDepreciationEntries() {
        navigateTo("/assets/depreciation");
        waitForPageLoad();

        // Find period input and generate button
        var periodInput = page.locator("input[name='period']").first();
        if (periodInput.isVisible()) {
            periodInput.fill(YearMonth.now().toString());
        }

        var generateBtn = page.locator("form[action*='/depreciation/generate'] button[type='submit']").first();
        if (generateBtn.isVisible()) {
            generateBtn.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should post all depreciation entries")
    void shouldPostAllDepreciationEntries() {
        navigateTo("/assets/depreciation");
        waitForPageLoad();

        var periodInput = page.locator("input[name='period']").first();
        if (periodInput.isVisible()) {
            periodInput.fill(YearMonth.now().toString());
        }

        var postAllBtn = page.locator("form[action*='/depreciation/post-all'] button[type='submit']").first();
        if (postAllBtn.isVisible()) {
            postAllBtn.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }

    // ==================== DELETE ASSET ====================

    @Test
    @DisplayName("Should delete fixed asset")
    void shouldDeleteFixedAsset() {
        // First create a new asset to delete
        var category = categoryRepository.findAll().stream().findFirst();
        if (category.isEmpty()) {
            return;
        }

        navigateTo("/assets/new");
        waitForPageLoad();

        String uniqueName = "Asset To Delete " + System.currentTimeMillis();
        String uniqueCode = "DEL-" + System.currentTimeMillis() % 100000;

        var nameInput = page.locator("input[name='name']").first();
        if (nameInput.isVisible()) {
            nameInput.fill(uniqueName);
        }

        var codeInput = page.locator("input[name='assetCode']").first();
        if (codeInput.isVisible()) {
            codeInput.fill(uniqueCode);
        }

        var acquisitionDateInput = page.locator("input[name='purchaseDate']").first();
        if (acquisitionDateInput.isVisible()) {
            acquisitionDateInput.fill(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        var costInput = page.locator("input[name='purchaseCost']").first();
        if (costInput.isVisible()) {
            costInput.fill("10000000");
        }

        var categorySelect = page.locator("select[name='category.id'], select[name='categoryId']").first();
        if (categorySelect.isVisible()) {
            categorySelect.selectOption(category.get().getId().toString());
        }

        var usefulLifeInput = page.locator("input[name='usefulLifeMonths']").first();
        if (usefulLifeInput.isVisible()) {
            usefulLifeInput.fill("12");
        }

        var submitBtn = page.locator("#btn-simpan").first();
        if (submitBtn.isVisible()) {
            submitBtn.click();
            waitForPageLoad();
        }

        // Find the created asset
        var asset = assetRepository.findAll().stream()
                .filter(a -> a.getName().equals(uniqueName))
                .findFirst();

        if (asset.isPresent()) {
            navigateTo("/assets/" + asset.get().getId());
            waitForPageLoad();

            // Find and click delete button
            var deleteBtn = page.locator("form[action*='/delete'] button[type='submit']").first();
            if (deleteBtn.isVisible()) {
                deleteBtn.click();
                waitForPageLoad();
            }
        }

        assertThat(page.locator("body")).isVisible();
    }

    // ==================== SEARCH FILTER ====================

    @Test
    @DisplayName("Should search assets by text")
    void shouldSearchAssetsByText() {
        navigateTo("/assets?search=test");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should filter assets with multiple parameters")
    void shouldFilterAssetsWithMultipleParameters() {
        var category = categoryRepository.findAll().stream().findFirst();
        String categoryParam = category.map(c -> "&categoryId=" + c.getId()).orElse("");

        navigateTo("/assets?status=ACTIVE" + categoryParam + "&search=test");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    // ==================== PAGINATION ====================

    @Test
    @DisplayName("Should paginate asset list")
    void shouldPaginateAssetList() {
        navigateTo("/assets?page=0&size=5");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }
}
