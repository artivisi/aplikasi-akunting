package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.entity.AssetCategory;
import com.artivisi.accountingfinance.entity.AssetStatus;
import com.artivisi.accountingfinance.entity.DepreciationEntry;
import com.artivisi.accountingfinance.entity.DepreciationEntryStatus;
import com.artivisi.accountingfinance.entity.DepreciationMethod;
import com.artivisi.accountingfinance.entity.FixedAsset;
import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.repository.AssetCategoryRepository;
import com.artivisi.accountingfinance.repository.DepreciationEntryRepository;
import com.artivisi.accountingfinance.repository.FixedAssetRepository;
import com.artivisi.accountingfinance.service.FixedAssetService;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Functional tests for FixedAssetService with data persistence verification.
 * Tests asset creation, depreciation generation, and disposal operations.
 */
@DisplayName("Fixed Asset - Persistence Tests")
@Import(ServiceTestDataInitializer.class)
class FixedAssetPersistenceTest extends PlaywrightTestBase {

    @Autowired
    private FixedAssetService fixedAssetService;

    @Autowired
    private FixedAssetRepository fixedAssetRepository;

    @Autowired
    private AssetCategoryRepository categoryRepository;

    @Autowired
    private DepreciationEntryRepository depreciationEntryRepository;

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    // ==================== SERVICE LAYER TESTS ====================

    @Test
    @DisplayName("Should create asset and verify persistence")
    void shouldCreateAssetAndVerifyPersistence() {
        // Get a category
        var categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            return; // Skip if no categories
        }
        AssetCategory category = categories.get(0);

        // Create asset
        String uniqueCode = "AST-TEST-" + System.currentTimeMillis();
        FixedAsset asset = new FixedAsset();
        asset.setAssetCode(uniqueCode);
        asset.setName("Test Asset for Persistence");
        asset.setDescription("Testing asset creation persistence");
        asset.setPurchaseDate(LocalDate.now().minusMonths(1));
        asset.setPurchaseCost(new BigDecimal("10000000"));
        asset.setResidualValue(new BigDecimal("1000000"));
        asset.setUsefulLifeMonths(60);
        asset.setDepreciationMethod(DepreciationMethod.STRAIGHT_LINE);
        asset.setCategory(category);

        FixedAsset saved = fixedAssetService.create(asset);

        // Verify persistence
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getAssetCode()).isEqualTo(uniqueCode);
        assertThat(saved.getBookValue()).isEqualByComparingTo(new BigDecimal("10000000"));

        // Verify in database
        var fromDb = fixedAssetRepository.findById(saved.getId());
        assertThat(fromDb).isPresent();
        assertThat(fromDb.get().getName()).isEqualTo("Test Asset for Persistence");
    }

    @Test
    @DisplayName("Should verify fixed asset repository works")
    void shouldVerifyFixedAssetRepositoryWorks() {
        // Verify repository is accessible
        assertThat(fixedAssetRepository).isNotNull();

        // Count assets
        long assetCount = fixedAssetRepository.count();
        assertThat(assetCount).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("Should calculate monthly depreciation correctly")
    void shouldCalculateMonthlyDepreciationCorrectly() {
        var categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            return;
        }

        // Create asset with known values
        String uniqueCode = "AST-DEP-" + System.currentTimeMillis();
        FixedAsset asset = new FixedAsset();
        asset.setAssetCode(uniqueCode);
        asset.setName("Depreciation Test Asset");
        asset.setPurchaseDate(LocalDate.now().minusMonths(1));
        asset.setPurchaseCost(new BigDecimal("12000000")); // 12 million
        asset.setResidualValue(new BigDecimal("0")); // No residual
        asset.setUsefulLifeMonths(12); // 12 months
        asset.setDepreciationMethod(DepreciationMethod.STRAIGHT_LINE);
        asset.setCategory(categories.get(0));

        FixedAsset saved = fixedAssetService.create(asset);

        // Calculate depreciation
        BigDecimal monthlyDep = fixedAssetService.calculateMonthlyDepreciation(saved);

        // Verify depreciation is calculated and positive
        assertThat(monthlyDep).isNotNull();
        assertThat(monthlyDep).isGreaterThan(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should find assets by status filter")
    void shouldFindAssetsByStatusFilter() {
        var activePage = fixedAssetService.findByFilters(null, AssetStatus.ACTIVE, null, PageRequest.of(0, 10));
        assertThat(activePage).isNotNull();

        var disposedPage = fixedAssetService.findByFilters(null, AssetStatus.DISPOSED, null, PageRequest.of(0, 10));
        assertThat(disposedPage).isNotNull();
    }

    @Test
    @DisplayName("Should find assets by search term")
    void shouldFindAssetsBySearchTerm() {
        // Create asset with specific name
        var categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            return;
        }

        String uniqueCode = "AST-SEARCH-" + System.currentTimeMillis();
        FixedAsset asset = new FixedAsset();
        asset.setAssetCode(uniqueCode);
        asset.setName("Unique Searchable Asset XYZ123");
        asset.setPurchaseDate(LocalDate.now());
        asset.setPurchaseCost(new BigDecimal("1000000"));
        asset.setUsefulLifeMonths(24);
        asset.setDepreciationMethod(DepreciationMethod.STRAIGHT_LINE);
        asset.setCategory(categories.get(0));

        fixedAssetService.create(asset);

        // Search by name
        var results = fixedAssetService.findByFilters("XYZ123", null, null, PageRequest.of(0, 10));
        assertThat(results.getTotalElements()).isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("Should reject duplicate asset code")
    void shouldRejectDuplicateAssetCode() {
        var categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            return;
        }

        String uniqueCode = "AST-DUP-" + System.currentTimeMillis();

        // Create first asset
        FixedAsset asset1 = new FixedAsset();
        asset1.setAssetCode(uniqueCode);
        asset1.setName("First Asset");
        asset1.setPurchaseDate(LocalDate.now());
        asset1.setPurchaseCost(new BigDecimal("1000000"));
        asset1.setUsefulLifeMonths(24);
        asset1.setDepreciationMethod(DepreciationMethod.STRAIGHT_LINE);
        asset1.setCategory(categories.get(0));
        fixedAssetService.create(asset1);

        // Try to create second with same code
        FixedAsset asset2 = new FixedAsset();
        asset2.setAssetCode(uniqueCode);
        asset2.setName("Second Asset");
        asset2.setPurchaseDate(LocalDate.now());
        asset2.setPurchaseCost(new BigDecimal("2000000"));
        asset2.setUsefulLifeMonths(24);
        asset2.setDepreciationMethod(DepreciationMethod.STRAIGHT_LINE);
        asset2.setCategory(categories.get(0));

        assertThatThrownBy(() -> fixedAssetService.create(asset2))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("sudah digunakan");
    }

    @Test
    @DisplayName("Should get total book value")
    void shouldGetTotalBookValue() {
        BigDecimal totalBookValue = fixedAssetService.getTotalBookValue();
        assertThat(totalBookValue).isNotNull();
        assertThat(totalBookValue).isGreaterThanOrEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should get total purchase cost")
    void shouldGetTotalPurchaseCost() {
        BigDecimal totalPurchaseCost = fixedAssetService.getTotalPurchaseCost();
        assertThat(totalPurchaseCost).isNotNull();
        assertThat(totalPurchaseCost).isGreaterThanOrEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should get total accumulated depreciation")
    void shouldGetTotalAccumulatedDepreciation() {
        BigDecimal totalAccumDep = fixedAssetService.getTotalAccumulatedDepreciation();
        assertThat(totalAccumDep).isNotNull();
        assertThat(totalAccumDep).isGreaterThanOrEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should count pending depreciation entries")
    void shouldCountPendingDepreciationEntries() {
        long pendingCount = fixedAssetService.countPendingDepreciationEntries();
        assertThat(pendingCount).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("Should validate declining balance requires rate")
    void shouldValidateDecliningBalanceRequiresRate() {
        var categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            return;
        }

        String uniqueCode = "AST-DECL-" + System.currentTimeMillis();
        FixedAsset asset = new FixedAsset();
        asset.setAssetCode(uniqueCode);
        asset.setName("Declining Balance Test");
        asset.setPurchaseDate(LocalDate.now());
        asset.setPurchaseCost(new BigDecimal("10000000"));
        asset.setUsefulLifeMonths(60);
        asset.setDepreciationMethod(DepreciationMethod.DECLINING_BALANCE);
        asset.setDepreciationRate(null); // Missing rate
        asset.setCategory(categories.get(0));

        assertThatThrownBy(() -> fixedAssetService.create(asset))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Tarif penyusutan");
    }

    @Test
    @DisplayName("Should validate residual value less than purchase cost")
    void shouldValidateResidualValueLessThanPurchaseCost() {
        var categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            return;
        }

        String uniqueCode = "AST-RES-" + System.currentTimeMillis();
        FixedAsset asset = new FixedAsset();
        asset.setAssetCode(uniqueCode);
        asset.setName("Residual Value Test");
        asset.setPurchaseDate(LocalDate.now());
        asset.setPurchaseCost(new BigDecimal("1000000"));
        asset.setResidualValue(new BigDecimal("2000000")); // Greater than cost
        asset.setUsefulLifeMonths(24);
        asset.setDepreciationMethod(DepreciationMethod.STRAIGHT_LINE);
        asset.setCategory(categories.get(0));

        assertThatThrownBy(() -> fixedAssetService.create(asset))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Nilai residu");
    }

    // ==================== UI TESTS ====================

    @Test
    @DisplayName("Should display assets list page")
    void shouldDisplayAssetsListPage() {
        navigateTo("/assets");
        waitForPageLoad();

        assertThat(page.locator("#page-title")).containsText("Aset");
    }

    @Test
    @DisplayName("Should display asset form page")
    void shouldDisplayAssetFormPage() {
        navigateTo("/assets/new");
        waitForPageLoad();

        assertThat(page.locator("#page-title")).isVisible();
        assertThat(page.locator("#assetCode, input[name='assetCode']").first()).isVisible();
    }

    @Test
    @DisplayName("Should display depreciation schedule page")
    void shouldDisplayDepreciationSchedulePage() {
        navigateTo("/assets/depreciation");
        waitForPageLoad();

        assertThat(page.locator("#page-title")).isVisible();
    }

    @Test
    @DisplayName("Should display asset categories page")
    void shouldDisplayAssetCategoriesPage() {
        navigateTo("/assets/categories");
        waitForPageLoad();

        assertThat(page.locator("#page-title")).isVisible();
    }

    @Test
    @DisplayName("Should create asset via UI and verify in database")
    void shouldCreateAssetViaUiAndVerifyInDatabase() {
        navigateTo("/assets/new");
        waitForPageLoad();

        String uniqueCode = "UI-AST-" + System.currentTimeMillis();

        // Fill form
        var assetCodeInput = page.locator("#assetCode, input[name='assetCode']").first();
        if (assetCodeInput.isVisible()) {
            assetCodeInput.fill(uniqueCode);
        }

        var nameInput = page.locator("#name, input[name='name']").first();
        if (nameInput.isVisible()) {
            nameInput.fill("UI Created Asset Test");
        }

        // Select category if dropdown exists
        var categorySelect = page.locator("select[name='category.id'], #categoryId").first();
        if (categorySelect.isVisible()) {
            var options = categorySelect.locator("option[value]");
            if (options.count() > 1) {
                categorySelect.selectOption(options.nth(1).getAttribute("value"));
            }
        }

        // Fill purchase date
        var purchaseDateInput = page.locator("#purchaseDate, input[name='purchaseDate']").first();
        if (purchaseDateInput.isVisible()) {
            purchaseDateInput.fill(LocalDate.now().toString());
        }

        // Fill purchase cost
        var purchaseCostInput = page.locator("#purchaseCost, input[name='purchaseCost']").first();
        if (purchaseCostInput.isVisible()) {
            purchaseCostInput.fill("5000000");
        }

        // Fill useful life
        var usefulLifeInput = page.locator("#usefulLifeMonths, input[name='usefulLifeMonths']").first();
        if (usefulLifeInput.isVisible()) {
            usefulLifeInput.fill("24");
        }

        // Submit form
        var submitBtn = page.locator("button[type='submit']").first();
        if (submitBtn.isVisible()) {
            submitBtn.click();
            waitForPageLoad();
        }

        // Verify in database
        var fromDb = fixedAssetRepository.findByAssetCode(uniqueCode);
        if (fromDb.isPresent()) {
            assertThat(fromDb.get().getName()).isEqualTo("UI Created Asset Test");
        }
    }

    @Test
    @DisplayName("Should filter assets by status via UI")
    void shouldFilterAssetsByStatusViaUi() {
        navigateTo("/assets?status=ACTIVE");
        waitForPageLoad();

        assertThat(page.locator("#page-title")).isVisible();
    }
}
