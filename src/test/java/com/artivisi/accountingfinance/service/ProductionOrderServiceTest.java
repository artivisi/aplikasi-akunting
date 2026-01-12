package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.TestcontainersConfiguration;
import com.artivisi.accountingfinance.entity.BillOfMaterial;
import com.artivisi.accountingfinance.entity.BillOfMaterialLine;
import com.artivisi.accountingfinance.entity.Product;
import com.artivisi.accountingfinance.entity.ProductionOrder;
import com.artivisi.accountingfinance.entity.ProductionOrderStatus;
import com.artivisi.accountingfinance.repository.BillOfMaterialRepository;
import com.artivisi.accountingfinance.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for ProductionOrderService.
 * Uses test data from V911__inventory_report_test_data.sql
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
@DisplayName("ProductionOrderService Integration Tests")
class ProductionOrderServiceTest {

    @Autowired
    private ProductionOrderService orderService;

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

    private BillOfMaterial testBom;

    @BeforeEach
    @WithMockUser(username = "testuser")
    void setup() {
        // Create a test BOM
        Product finishedProduct = productRepository.findById(PRODUCT_FINISHED_ID)
                .orElseThrow(() -> new IllegalStateException("Test finished product not found"));
        Product component1 = productRepository.findById(PRODUCT_COMPONENT_1_ID)
                .orElseThrow(() -> new IllegalStateException("Test component 1 not found"));

        BillOfMaterial bom = new BillOfMaterial();
        bom.setCode("BOM-PRODORD-TEST");
        bom.setName("Test BOM for Production Order");
        bom.setProduct(finishedProduct);
        bom.setOutputQuantity(BigDecimal.ONE);

        BillOfMaterialLine line = new BillOfMaterialLine();
        line.setComponent(component1);
        line.setQuantity(new BigDecimal("2"));
        bom.addLine(line);

        testBom = bomService.create(bom);
    }

    @Nested
    @DisplayName("Find Operations")
    class FindOperationsTests {

        @Test
        @DisplayName("Should find all production orders")
        void shouldFindAllProductionOrders() {
            List<ProductionOrder> orders = orderService.findAll();
            assertThat(orders).isNotNull();
        }

        @Test
        @DisplayName("Should find production orders by status")
        void shouldFindProductionOrdersByStatus() {
            List<ProductionOrder> draftOrders = orderService.findByStatus(ProductionOrderStatus.DRAFT);
            assertThat(draftOrders).isNotNull();
        }

        @Test
        @DisplayName("Should find production order by ID")
        @WithMockUser(username = "testuser")
        void shouldFindProductionOrderById() {
            // Create a production order first
            ProductionOrder order = createTestOrder();
            ProductionOrder saved = orderService.create(order);

            Optional<ProductionOrder> found = orderService.findById(saved.getId());

            assertThat(found).isPresent();
            assertThat(found.get().getOrderNumber()).isNotEmpty();
        }

        @Test
        @DisplayName("Should return empty for non-existent ID")
        void shouldReturnEmptyForNonExistentId() {
            Optional<ProductionOrder> found = orderService.findById(UUID.randomUUID());
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("Create Operations")
    class CreateOperationsTests {

        @Test
        @WithMockUser(username = "testuser")
        @DisplayName("Should create production order with DRAFT status")
        void shouldCreateProductionOrderWithDraftStatus() {
            ProductionOrder order = createTestOrder();
            ProductionOrder saved = orderService.create(order);

            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getOrderNumber()).isNotEmpty();
            assertThat(saved.getStatus()).isEqualTo(ProductionOrderStatus.DRAFT);
        }

        @Test
        @WithMockUser(username = "testuser")
        @DisplayName("Should generate unique order number")
        void shouldGenerateUniqueOrderNumber() {
            ProductionOrder order1 = orderService.create(createTestOrder());
            ProductionOrder order2 = orderService.create(createTestOrder());

            assertThat(order1.getOrderNumber()).isNotEqualTo(order2.getOrderNumber());
        }

        @Test
        @WithMockUser(username = "testuser")
        @DisplayName("Should reject order without BOM")
        void shouldRejectOrderWithoutBom() {
            ProductionOrder order = new ProductionOrder();
            order.setQuantity(BigDecimal.TEN);
            order.setOrderDate(LocalDate.now());
            order.setBillOfMaterial(null);

            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @WithMockUser(username = "testuser")
        @DisplayName("Should reject order with zero quantity")
        void shouldRejectOrderWithZeroQuantity() {
            ProductionOrder order = createTestOrder();
            order.setQuantity(BigDecimal.ZERO);

            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @WithMockUser(username = "testuser")
        @DisplayName("Should reject order with negative quantity")
        void shouldRejectOrderWithNegativeQuantity() {
            ProductionOrder order = createTestOrder();
            order.setQuantity(new BigDecimal("-1"));

            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Update Operations")
    class UpdateOperationsTests {

        @Test
        @WithMockUser(username = "testuser")
        @DisplayName("Should update draft production order")
        void shouldUpdateDraftProductionOrder() {
            ProductionOrder order = orderService.create(createTestOrder());

            order.setQuantity(new BigDecimal("20"));
            ProductionOrder updated = orderService.update(order.getId(), order);

            assertThat(updated.getQuantity()).isEqualByComparingTo(new BigDecimal("20"));
        }

        @Test
        @WithMockUser(username = "testuser")
        @DisplayName("Should reject update for non-existent order")
        void shouldRejectUpdateForNonExistentOrder() {
            ProductionOrder order = createTestOrder();

            assertThatThrownBy(() -> orderService.update(UUID.randomUUID(), order))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("tidak ditemukan");
        }
    }

    @Nested
    @DisplayName("Status Transitions")
    class StatusTransitionTests {

        @Test
        @WithMockUser(username = "testuser")
        @DisplayName("Should start draft order")
        void shouldStartDraftOrder() {
            ProductionOrder order = orderService.create(createTestOrder());

            ProductionOrder started = orderService.start(order.getId());

            assertThat(started.getStatus()).isEqualTo(ProductionOrderStatus.IN_PROGRESS);
        }

        @Test
        @WithMockUser(username = "testuser")
        @DisplayName("Should cancel draft order")
        void shouldCancelDraftOrder() {
            ProductionOrder order = orderService.create(createTestOrder());

            orderService.cancel(order.getId());

            Optional<ProductionOrder> found = orderService.findById(order.getId());
            assertThat(found).isPresent();
            assertThat(found.get().getStatus()).isEqualTo(ProductionOrderStatus.CANCELLED);
        }
    }

    private ProductionOrder createTestOrder() {
        ProductionOrder order = new ProductionOrder();
        order.setQuantity(BigDecimal.TEN);
        order.setOrderDate(LocalDate.now());
        order.setPlannedCompletionDate(LocalDate.now().plusDays(7));
        order.setBillOfMaterial(testBom);
        order.setNotes("Test production order");
        return order;
    }
}
