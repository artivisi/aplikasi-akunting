package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.TestcontainersConfiguration;
import com.artivisi.accountingfinance.entity.ProductCategory;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for ProductCategoryService.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
@DisplayName("ProductCategoryService Integration Tests")
class ProductCategoryServiceTest {

    @Autowired
    private ProductCategoryService categoryService;

    @Nested
    @DisplayName("Find Operations")
    class FindOperationsTests {

        @Test
        @DisplayName("Should find all active categories")
        void shouldFindAllActiveCategories() {
            List<ProductCategory> categories = categoryService.findAllActive();

            assertThat(categories).isNotNull();
        }

        @Test
        @DisplayName("Should find root categories")
        void shouldFindRootCategories() {
            List<ProductCategory> rootCategories = categoryService.findRootCategories();

            assertThat(rootCategories).isNotNull();
        }

        @Test
        @DisplayName("Should find category by ID")
        void shouldFindCategoryById() {
            // Create a category first
            ProductCategory category = new ProductCategory();
            category.setCode("TCAT" + (System.currentTimeMillis() % 100000));
            category.setName("Test Category");
            category.setActive(true);
            ProductCategory created = categoryService.create(category);

            Optional<ProductCategory> found = categoryService.findById(created.getId());

            assertThat(found).isPresent();
            assertThat(found.get().getCode()).isEqualTo(category.getCode());
        }

        @Test
        @DisplayName("Should find category by code")
        void shouldFindCategoryByCode() {
            String uniqueCode = "TCOD" + (System.currentTimeMillis() % 100000);
            ProductCategory category = new ProductCategory();
            category.setCode(uniqueCode);
            category.setName("Test Category By Code");
            category.setActive(true);
            categoryService.create(category);

            Optional<ProductCategory> found = categoryService.findByCode(uniqueCode);

            assertThat(found).isPresent();
            assertThat(found.get().getName()).isEqualTo("Test Category By Code");
        }

        @Test
        @DisplayName("Should return empty for non-existent ID")
        void shouldReturnEmptyForNonExistentId() {
            Optional<ProductCategory> found = categoryService.findById(UUID.randomUUID());

            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("Should find by search with pagination")
        void shouldFindBySearchWithPagination() {
            Page<ProductCategory> result = categoryService.findBySearch("test", PageRequest.of(0, 10));

            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("Create Operations")
    class CreateOperationsTests {

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("Should create new category")
        void shouldCreateNewCategory() {
            ProductCategory category = new ProductCategory();
            category.setCode("NCAT" + (System.currentTimeMillis() % 100000));
            category.setName("New Test Category");
            category.setDescription("Test Description");
            category.setActive(true);

            ProductCategory created = categoryService.create(category);

            assertThat(created).isNotNull();
            assertThat(created.getId()).isNotNull();
            assertThat(created.getName()).isEqualTo("New Test Category");
        }

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("Should reject duplicate category code")
        void shouldRejectDuplicateCategoryCode() {
            String duplicateCode = "DUP" + (System.currentTimeMillis() % 10000);

            ProductCategory first = new ProductCategory();
            first.setCode(duplicateCode);
            first.setName("First Category");
            first.setActive(true);
            categoryService.create(first);

            ProductCategory second = new ProductCategory();
            second.setCode(duplicateCode);
            second.setName("Second Category");
            second.setActive(true);

            assertThatThrownBy(() -> categoryService.create(second))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("sudah digunakan");
        }

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("Should create child category")
        void shouldCreateChildCategory() {
            long ts = System.currentTimeMillis() % 10000;
            // Create parent
            ProductCategory parent = new ProductCategory();
            parent.setCode("PAR" + ts);
            parent.setName("Parent Category");
            parent.setActive(true);
            ProductCategory createdParent = categoryService.create(parent);

            // Create child
            ProductCategory child = new ProductCategory();
            child.setCode("CHD" + ts);
            child.setName("Child Category");
            child.setParent(createdParent);
            child.setActive(true);

            ProductCategory createdChild = categoryService.create(child);

            assertThat(createdChild).isNotNull();
            assertThat(createdChild.getParent()).isNotNull();
            assertThat(createdChild.getParent().getId()).isEqualTo(createdParent.getId());
        }
    }

    @Nested
    @DisplayName("Update Operations")
    class UpdateOperationsTests {

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("Should update category")
        void shouldUpdateCategory() {
            ProductCategory category = new ProductCategory();
            category.setCode("UPD" + (System.currentTimeMillis() % 10000));
            category.setName("Original Name");
            category.setActive(true);
            ProductCategory created = categoryService.create(category);

            ProductCategory updated = new ProductCategory();
            updated.setCode(created.getCode());
            updated.setName("Updated Name");
            updated.setDescription("New description");
            updated.setActive(true);

            ProductCategory result = categoryService.update(created.getId(), updated);

            assertThat(result.getName()).isEqualTo("Updated Name");
            assertThat(result.getDescription()).isEqualTo("New description");
        }

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("Should throw exception for non-existent category update")
        void shouldThrowExceptionForNonExistentCategoryUpdate() {
            UUID randomId = UUID.randomUUID();
            ProductCategory updated = new ProductCategory();
            updated.setCode("NON-EXIST");
            updated.setName("Test");

            assertThatThrownBy(() -> categoryService.update(randomId, updated))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("tidak ditemukan");
        }
    }

    @Nested
    @DisplayName("Delete Operations")
    class DeleteOperationsTests {

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("Should delete category without products")
        void shouldDeleteCategoryWithoutProducts() {
            ProductCategory category = new ProductCategory();
            category.setCode("DEL" + (System.currentTimeMillis() % 10000));
            category.setName("Category to Delete");
            category.setActive(true);
            ProductCategory created = categoryService.create(category);

            categoryService.delete(created.getId());

            Optional<ProductCategory> found = categoryService.findById(created.getId());
            assertThat(found).isEmpty();
        }

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("Should throw exception for non-existent category delete")
        void shouldThrowExceptionForNonExistentCategoryDelete() {
            UUID randomId = UUID.randomUUID();

            assertThatThrownBy(() -> categoryService.delete(randomId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("tidak ditemukan");
        }

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("Should throw exception when deleting category with children")
        void shouldThrowExceptionWhenDeletingCategoryWithChildren() {
            long ts = System.currentTimeMillis() % 10000;
            // Create parent
            ProductCategory parent = new ProductCategory();
            parent.setCode("PD" + ts);
            parent.setName("Parent with Child");
            parent.setActive(true);
            ProductCategory createdParent = categoryService.create(parent);

            // Create child
            ProductCategory child = new ProductCategory();
            child.setCode("CD" + ts);
            child.setName("Child Category");
            child.setParent(createdParent);
            child.setActive(true);
            categoryService.create(child);

            // Try to delete parent
            assertThatThrownBy(() -> categoryService.delete(createdParent.getId()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("sub-kategori");
        }
    }

    @Nested
    @DisplayName("Hierarchy Operations")
    class HierarchyOperationsTests {

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("Should find children by parent ID")
        void shouldFindChildrenByParentId() {
            long ts = System.currentTimeMillis() % 10000;
            // Create parent
            ProductCategory parent = new ProductCategory();
            parent.setCode("HP" + ts);
            parent.setName("Hierarchy Parent");
            parent.setActive(true);
            ProductCategory createdParent = categoryService.create(parent);

            // Create children
            for (int i = 0; i < 3; i++) {
                ProductCategory child = new ProductCategory();
                child.setCode("HC" + ts + i);
                child.setName("Child " + i);
                child.setParent(createdParent);
                child.setActive(true);
                categoryService.create(child);
            }

            List<ProductCategory> children = categoryService.findByParentId(createdParent.getId());

            assertThat(children).hasSize(3);
        }

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("Should prevent circular reference")
        void shouldPreventCircularReference() {
            // Create category
            ProductCategory category = new ProductCategory();
            category.setCode("CIRC" + (System.currentTimeMillis() % 10000));
            category.setName("Circular Category");
            category.setActive(true);
            ProductCategory created = categoryService.create(category);

            // Try to set itself as parent
            ProductCategory selfRef = new ProductCategory();
            selfRef.setCode(created.getCode());
            selfRef.setName(created.getName());
            selfRef.setParent(created);
            selfRef.setActive(true);

            assertThatThrownBy(() -> categoryService.update(created.getId(), selfRef))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("sirkuler");
        }
    }
}
