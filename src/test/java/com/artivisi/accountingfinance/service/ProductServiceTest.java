package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.TestcontainersConfiguration;
import com.artivisi.accountingfinance.entity.CostingMethod;
import com.artivisi.accountingfinance.entity.Product;
import com.artivisi.accountingfinance.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for ProductService.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
@DisplayName("ProductService Integration Tests")
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    private Product testProduct;

    @BeforeEach
    void setup() {
        testProduct = productRepository.findByCode("PRD-TEST-001").orElse(null);
    }

    @Nested
    @DisplayName("Find Operations")
    class FindOperationsTests {

        @Test
        @DisplayName("Should find product by ID")
        void shouldFindProductById() {
            if (testProduct == null) return;
            Product found = productRepository.findById(testProduct.getId()).orElse(null);
            assertThat(found).isNotNull();
        }

        @Test
        @DisplayName("Should find products with pagination")
        void shouldFindProductsWithPagination() {
            Page<Product> products = productRepository.findAll(PageRequest.of(0, 10));
            assertThat(products).isNotNull();
        }
    }

    @Nested
    @DisplayName("Create Operations")
    class CreateOperationsTests {

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("Should create new product")
        void shouldCreateNewProduct() {
            Product product = new Product();
            product.setCode("TST-" + System.currentTimeMillis());
            product.setName("Test Product");
            product.setUnit("pcs");
            product.setActive(true);
            product.setCostingMethod(CostingMethod.WEIGHTED_AVERAGE);

            Product created = productService.create(product);

            assertThat(created).isNotNull();
            assertThat(created.getId()).isNotNull();
        }

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("Should reject duplicate product code")
        void shouldRejectDuplicateProductCode() {
            if (testProduct == null) return;

            Product product = new Product();
            product.setCode(testProduct.getCode());
            product.setName("Duplicate");
            product.setUnit("pcs");
            product.setActive(true);
            product.setCostingMethod(CostingMethod.WEIGHTED_AVERAGE);

            assertThatThrownBy(() -> productService.create(product))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Update Operations")
    class UpdateOperationsTests {

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("Should update product")
        void shouldUpdateProduct() {
            if (testProduct == null) return;

            Product updated = new Product();
            updated.setCode(testProduct.getCode());
            updated.setName("Updated Name");
            updated.setUnit(testProduct.getUnit());
            updated.setActive(testProduct.isActive());
            updated.setCostingMethod(testProduct.getCostingMethod());

            Product result = productService.update(testProduct.getId(), updated);

            assertThat(result.getName()).isEqualTo("Updated Name");
        }

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("Should throw exception for non-existent product")
        void shouldThrowExceptionForNonExistentProduct() {
            UUID randomId = UUID.randomUUID();
            Product updated = new Product();
            updated.setCode("TST-UPD");
            updated.setName("Test");
            updated.setUnit("pcs");

            assertThatThrownBy(() -> productService.update(randomId, updated))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("tidak ditemukan");
        }
    }

    @Nested
    @DisplayName("Activate/Deactivate Operations")
    class ActivateDeactivateOperationsTests {

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("Should deactivate product")
        void shouldDeactivateProduct() {
            if (testProduct == null) return;
            productService.deactivate(testProduct.getId());
            Product found = productRepository.findById(testProduct.getId()).orElse(null);
            assertThat(found).isNotNull();
            assertThat(found.isActive()).isFalse();
        }

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("Should activate product")
        void shouldActivateProduct() {
            if (testProduct == null) return;
            productService.deactivate(testProduct.getId());
            productService.activate(testProduct.getId());
            Product found = productRepository.findById(testProduct.getId()).orElse(null);
            assertThat(found).isNotNull();
            assertThat(found.isActive()).isTrue();
        }

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("Should throw exception when activating non-existent product")
        void shouldThrowExceptionWhenActivatingNonExistentProduct() {
            UUID randomId = UUID.randomUUID();
            assertThatThrownBy(() -> productService.activate(randomId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("tidak ditemukan");
        }

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("Should throw exception when deactivating non-existent product")
        void shouldThrowExceptionWhenDeactivatingNonExistentProduct() {
            UUID randomId = UUID.randomUUID();
            assertThatThrownBy(() -> productService.deactivate(randomId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("tidak ditemukan");
        }
    }

    @Nested
    @DisplayName("Delete Operations")
    class DeleteOperationsTests {

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("Should throw exception when deleting non-existent product")
        void shouldThrowExceptionWhenDeletingNonExistentProduct() {
            UUID randomId = UUID.randomUUID();
            assertThatThrownBy(() -> productService.delete(randomId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("tidak ditemukan");
        }
    }
}
