package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.TestcontainersConfiguration;
import com.artivisi.accountingfinance.entity.BillOfMaterial;
import com.artivisi.accountingfinance.entity.BillOfMaterialLine;
import com.artivisi.accountingfinance.entity.Product;
import com.artivisi.accountingfinance.repository.BillOfMaterialRepository;
import com.artivisi.accountingfinance.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for BillOfMaterialService.
 * Uses test data from V911__inventory_report_test_data.sql
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
@DisplayName("BillOfMaterialService Integration Tests")
class BillOfMaterialServiceTest {

    @Autowired
    private BillOfMaterialService bomService;

    @Autowired
    private BillOfMaterialRepository bomRepository;

    @Autowired
    private ProductRepository productRepository;

    // Test data IDs from V911
    private static final UUID PRODUCT_FINISHED_ID = UUID.fromString("d0911002-0000-0000-0000-000000000003");
    private static final UUID PRODUCT_COMPONENT_1_ID = UUID.fromString("d0911002-0000-0000-0000-000000000001");
    private static final UUID PRODUCT_COMPONENT_2_ID = UUID.fromString("d0911002-0000-0000-0000-000000000002");

    private Product finishedProduct;
    private Product component1;
    private Product component2;

    @BeforeEach
    void setup() {
        finishedProduct = productRepository.findById(PRODUCT_FINISHED_ID)
                .orElseThrow(() -> new IllegalStateException("Test finished product not found"));
        component1 = productRepository.findById(PRODUCT_COMPONENT_1_ID)
                .orElseThrow(() -> new IllegalStateException("Test component 1 not found"));
        component2 = productRepository.findById(PRODUCT_COMPONENT_2_ID)
                .orElseThrow(() -> new IllegalStateException("Test component 2 not found"));
    }

    @Nested
    @DisplayName("Find Operations")
    class FindOperationsTests {

        @Test
        @DisplayName("Should find all BOMs")
        void shouldFindAllBoms() {
            List<BillOfMaterial> boms = bomService.findAll();
            assertThat(boms).isNotNull();
        }

        @Test
        @DisplayName("Should search BOMs by name")
        void shouldSearchBomsByName() {
            List<BillOfMaterial> boms = bomService.search("test");
            assertThat(boms).isNotNull();
        }

        @Test
        @DisplayName("Should return all when search is empty")
        void shouldReturnAllWhenSearchIsEmpty() {
            List<BillOfMaterial> all = bomService.findAll();
            List<BillOfMaterial> searched = bomService.search("");
            assertThat(searched).hasSameSizeAs(all);
        }

        @Test
        @DisplayName("Should return all when search is null")
        void shouldReturnAllWhenSearchIsNull() {
            List<BillOfMaterial> all = bomService.findAll();
            List<BillOfMaterial> searched = bomService.search(null);
            assertThat(searched).hasSameSizeAs(all);
        }
    }

    @Nested
    @DisplayName("Create Operations")
    class CreateOperationsTests {

        @Test
        @DisplayName("Should create BOM with lines")
        void shouldCreateBomWithLines() {
            BillOfMaterial bom = new BillOfMaterial();
            bom.setCode("BOM-TEST-001");
            bom.setName("Test BOM");
            bom.setProduct(finishedProduct);
            bom.setOutputQuantity(BigDecimal.ONE);

            BillOfMaterialLine line1 = new BillOfMaterialLine();
            line1.setComponent(component1);
            line1.setQuantity(new BigDecimal("2"));
            bom.addLine(line1);

            BillOfMaterialLine line2 = new BillOfMaterialLine();
            line2.setComponent(component2);
            line2.setQuantity(new BigDecimal("0.5"));
            bom.addLine(line2);

            BillOfMaterial saved = bomService.create(bom);

            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getCode()).isEqualTo("BOM-TEST-001");
            assertThat(saved.getLines()).hasSize(2);
        }

        @Test
        @DisplayName("Should reject duplicate BOM code")
        void shouldRejectDuplicateBomCode() {
            // Create first BOM
            BillOfMaterial bom1 = new BillOfMaterial();
            bom1.setCode("BOM-DUPLICATE");
            bom1.setName("First BOM");
            bom1.setProduct(finishedProduct);
            bom1.setOutputQuantity(BigDecimal.ONE);

            BillOfMaterialLine line = new BillOfMaterialLine();
            line.setComponent(component1);
            line.setQuantity(BigDecimal.ONE);
            bom1.addLine(line);

            bomService.create(bom1);

            // Try to create second with same code
            BillOfMaterial bom2 = new BillOfMaterial();
            bom2.setCode("BOM-DUPLICATE");
            bom2.setName("Second BOM");
            bom2.setProduct(finishedProduct);
            bom2.setOutputQuantity(BigDecimal.ONE);

            BillOfMaterialLine line2 = new BillOfMaterialLine();
            line2.setComponent(component2);
            line2.setQuantity(BigDecimal.ONE);
            bom2.addLine(line2);

            assertThatThrownBy(() -> bomService.create(bom2))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("sudah digunakan");
        }

        @Test
        @DisplayName("Should reject BOM without components")
        void shouldRejectBomWithoutComponents() {
            BillOfMaterial bom = new BillOfMaterial();
            bom.setCode("BOM-NO-COMPONENTS");
            bom.setName("No Components BOM");
            bom.setProduct(finishedProduct);
            bom.setOutputQuantity(BigDecimal.ONE);
            // No lines added

            assertThatThrownBy(() -> bomService.create(bom))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("minimal 1 komponen");
        }

        @Test
        @DisplayName("Should reject self-referencing BOM")
        void shouldRejectSelfReferencingBom() {
            BillOfMaterial bom = new BillOfMaterial();
            bom.setCode("BOM-SELF-REF");
            bom.setName("Self Referencing BOM");
            bom.setProduct(finishedProduct);
            bom.setOutputQuantity(BigDecimal.ONE);

            // Use the finished product as its own component
            BillOfMaterialLine line = new BillOfMaterialLine();
            line.setComponent(finishedProduct);
            line.setQuantity(BigDecimal.ONE);
            bom.addLine(line);

            assertThatThrownBy(() -> bomService.create(bom))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("tidak boleh menjadi komponen dari dirinya sendiri");
        }

        @Test
        @DisplayName("Should reject empty BOM code")
        void shouldRejectEmptyBomCode() {
            BillOfMaterial bom = new BillOfMaterial();
            bom.setCode("");
            bom.setName("Empty Code BOM");
            bom.setProduct(finishedProduct);
            bom.setOutputQuantity(BigDecimal.ONE);

            BillOfMaterialLine line = new BillOfMaterialLine();
            line.setComponent(component1);
            line.setQuantity(BigDecimal.ONE);
            bom.addLine(line);

            assertThatThrownBy(() -> bomService.create(bom))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Update Operations")
    class UpdateOperationsTests {

        @Test
        @DisplayName("Should update BOM name")
        void shouldUpdateBomName() {
            // Create a BOM first
            BillOfMaterial bom = createTestBom("BOM-UPDATE-001");
            BillOfMaterial saved = bomService.create(bom);

            // Update it
            saved.setName("Updated Name");
            BillOfMaterial updated = bomService.update(saved.getId(), saved);

            assertThat(updated.getName()).isEqualTo("Updated Name");
        }

        @Test
        @DisplayName("Should reject update for non-existent BOM")
        void shouldRejectUpdateForNonExistentBom() {
            BillOfMaterial bom = new BillOfMaterial();
            bom.setCode("BOM-NONEXISTENT");
            bom.setName("Non-existent BOM");

            UUID nonExistentId = UUID.randomUUID();

            assertThatThrownBy(() -> bomService.update(nonExistentId, bom))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("tidak ditemukan");
        }
    }

    @Nested
    @DisplayName("Delete Operations")
    class DeleteOperationsTests {

        @Test
        @DisplayName("Should soft delete BOM by setting active to false")
        void shouldSoftDeleteBom() {
            BillOfMaterial bom = createTestBom("BOM-DELETE-001");
            BillOfMaterial saved = bomService.create(bom);

            bomService.delete(saved.getId());

            // After soft delete, findById still returns but active = false
            Optional<BillOfMaterial> found = bomService.findById(saved.getId());
            assertThat(found).isPresent();
            assertThat(found.get().isActive()).isFalse();
        }

        @Test
        @DisplayName("Should reject delete for non-existent BOM")
        void shouldRejectDeleteForNonExistentBom() {
            UUID nonExistentId = UUID.randomUUID();

            assertThatThrownBy(() -> bomService.delete(nonExistentId))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    private BillOfMaterial createTestBom(String code) {
        BillOfMaterial bom = new BillOfMaterial();
        bom.setCode(code);
        bom.setName("Test BOM " + code);
        bom.setProduct(finishedProduct);
        bom.setOutputQuantity(BigDecimal.ONE);

        BillOfMaterialLine line = new BillOfMaterialLine();
        line.setComponent(component1);
        line.setQuantity(new BigDecimal("2"));
        bom.addLine(line);

        return bom;
    }
}
