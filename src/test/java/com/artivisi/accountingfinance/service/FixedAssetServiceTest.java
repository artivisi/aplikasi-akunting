package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.TestcontainersConfiguration;
import com.artivisi.accountingfinance.entity.AssetStatus;
import com.artivisi.accountingfinance.entity.FixedAsset;
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
 * Integration tests for FixedAssetService.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
@DisplayName("FixedAssetService Integration Tests")
class FixedAssetServiceTest {

    @Autowired
    private FixedAssetService fixedAssetService;

    @Nested
    @DisplayName("Find Operations")
    class FindOperationsTests {

        @Test
        @DisplayName("Should find all assets")
        void shouldFindAllAssets() {
            List<FixedAsset> assets = fixedAssetService.findAll();
            assertThat(assets).isNotNull();
        }

        @Test
        @DisplayName("Should find all active assets")
        void shouldFindAllActiveAssets() {
            List<FixedAsset> activeAssets = fixedAssetService.findAllActive();
            assertThat(activeAssets).isNotNull();
        }

        @Test
        @DisplayName("Should find assets by filters with null values")
        void shouldFindAssetsByFiltersWithNullValues() {
            Page<FixedAsset> result = fixedAssetService.findByFilters(
                    null, null, null, PageRequest.of(0, 10));
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should find assets by status ACTIVE")
        void shouldFindAssetsByStatusActive() {
            Page<FixedAsset> result = fixedAssetService.findByFilters(
                    null, AssetStatus.ACTIVE, null, PageRequest.of(0, 10));
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should find assets by status DISPOSED")
        void shouldFindAssetsByStatusDisposed() {
            Page<FixedAsset> result = fixedAssetService.findByFilters(
                    null, AssetStatus.DISPOSED, null, PageRequest.of(0, 10));
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should find assets by search")
        void shouldFindAssetsBySearch() {
            Page<FixedAsset> result = fixedAssetService.findByFilters(
                    "test", null, null, PageRequest.of(0, 10));
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should find assets by search with status")
        void shouldFindAssetsBySearchWithStatus() {
            Page<FixedAsset> result = fixedAssetService.findByFilters(
                    "laptop", AssetStatus.ACTIVE, null, PageRequest.of(0, 10));
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should find assets by search with category")
        void shouldFindAssetsBySearchWithCategory() {
            UUID randomCategoryId = UUID.randomUUID();
            Page<FixedAsset> result = fixedAssetService.findByFilters(
                    null, null, randomCategoryId, PageRequest.of(0, 10));
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should throw exception for non-existent ID")
        void shouldThrowExceptionForNonExistentId() {
            UUID randomId = UUID.randomUUID();
            assertThatThrownBy(() -> fixedAssetService.findById(randomId))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("Should paginate results correctly")
        void shouldPaginateResultsCorrectly() {
            Page<FixedAsset> page1 = fixedAssetService.findByFilters(
                    null, null, null, PageRequest.of(0, 5));
            Page<FixedAsset> page2 = fixedAssetService.findByFilters(
                    null, null, null, PageRequest.of(1, 5));

            assertThat(page1).isNotNull();
            assertThat(page2).isNotNull();
            assertThat(page1.getNumber()).isEqualTo(0);
            assertThat(page2.getNumber()).isEqualTo(1);
        }
    }
}
