package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.TestcontainersConfiguration;
import com.artivisi.accountingfinance.entity.AssetCategory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for AssetCategoryService.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
@DisplayName("AssetCategoryService Integration Tests")
class AssetCategoryServiceTest {

    @Autowired
    private AssetCategoryService assetCategoryService;

    @Nested
    @DisplayName("Find Operations")
    class FindOperationsTests {

        @Test
        @DisplayName("Should find all asset categories")
        void shouldFindAllAssetCategories() {
            List<AssetCategory> categories = assetCategoryService.findAll();
            assertThat(categories).isNotNull();
        }

        @Test
        @DisplayName("Should find all active asset categories")
        void shouldFindAllActiveAssetCategories() {
            List<AssetCategory> activeCategories = assetCategoryService.findAllActive();
            assertThat(activeCategories).isNotNull();
        }

        @Test
        @DisplayName("Should throw exception for non-existent ID")
        void shouldThrowExceptionForNonExistentId() {
            UUID randomId = UUID.randomUUID();
            assertThatThrownBy(() -> assetCategoryService.findById(randomId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("tidak ditemukan");
        }

        @Test
        @DisplayName("Should throw exception for non-existent code")
        void shouldThrowExceptionForNonExistentCode() {
            assertThatThrownBy(() -> assetCategoryService.findByCode("NON-EXISTENT-CODE"))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("tidak ditemukan");
        }

        @Test
        @DisplayName("Should find categories by filters with null values")
        void shouldFindCategoriesByFiltersWithNullValues() {
            Page<AssetCategory> result = assetCategoryService.findByFilters(
                    null, null, PageRequest.of(0, 10));
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should find categories by filters with search")
        void shouldFindCategoriesByFiltersWithSearch() {
            Page<AssetCategory> result = assetCategoryService.findByFilters(
                    "computer", null, PageRequest.of(0, 10));
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should find categories by filters with active flag")
        void shouldFindCategoriesByFiltersWithActiveFlag() {
            Page<AssetCategory> result = assetCategoryService.findByFilters(
                    null, true, PageRequest.of(0, 10));
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should find categories by filters with inactive flag")
        void shouldFindCategoriesByFiltersWithInactiveFlag() {
            Page<AssetCategory> result = assetCategoryService.findByFilters(
                    null, false, PageRequest.of(0, 10));
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should find categories by filters with search and active flag")
        void shouldFindCategoriesByFiltersWithSearchAndActiveFlag() {
            Page<AssetCategory> result = assetCategoryService.findByFilters(
                    "office", true, PageRequest.of(0, 10));
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should check if code exists - false for random code")
        void shouldCheckIfCodeExistsFalseForRandomCode() {
            boolean exists = assetCategoryService.existsByCode("RANDOM-CODE-" + UUID.randomUUID());
            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("Activate/Deactivate Operations")
    class ActivateDeactivateTests {

        @Test
        @DisplayName("Should throw exception when activating non-existent category")
        void shouldThrowExceptionWhenActivatingNonExistentCategory() {
            UUID randomId = UUID.randomUUID();
            assertThatThrownBy(() -> assetCategoryService.activate(randomId))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("Should throw exception when deactivating non-existent category")
        void shouldThrowExceptionWhenDeactivatingNonExistentCategory() {
            UUID randomId = UUID.randomUUID();
            assertThatThrownBy(() -> assetCategoryService.deactivate(randomId))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Delete Operations")
    class DeleteOperationsTests {

        @Test
        @DisplayName("Should throw exception when deleting non-existent category")
        void shouldThrowExceptionWhenDeletingNonExistentCategory() {
            UUID randomId = UUID.randomUUID();
            assertThatThrownBy(() -> assetCategoryService.delete(randomId))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }
}
