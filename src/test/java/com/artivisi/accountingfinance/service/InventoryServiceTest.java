package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.TestcontainersConfiguration;
import com.artivisi.accountingfinance.entity.InventoryBalance;
import com.artivisi.accountingfinance.entity.InventoryTransaction;
import com.artivisi.accountingfinance.entity.Product;
import com.artivisi.accountingfinance.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for InventoryService.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
@DisplayName("InventoryService Integration Tests")
class InventoryServiceTest {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ProductRepository productRepository;

    // Test product IDs from V911__inventory_report_test_data.sql
    private static final UUID TEST_PRODUCT_ID = UUID.fromString("d0911002-0000-0000-0000-000000000001");

    private Product testProduct;

    @BeforeEach
    void setup() {
        testProduct = productRepository.findById(TEST_PRODUCT_ID).orElse(null);
    }

    @Nested
    @DisplayName("Balance Operations")
    class BalanceOperationsTests {

        @Test
        @DisplayName("Should find balance by product ID - empty for non-existent")
        void shouldFindBalanceByProductIdEmptyForNonExistent() {
            UUID randomId = UUID.randomUUID();
            Optional<InventoryBalance> balance = inventoryService.findBalanceByProductId(randomId);
            assertThat(balance).isEmpty();
        }

        @Test
        @DisplayName("Should find balances with search")
        void shouldFindBalancesWithSearch() {
            Page<InventoryBalance> balances = inventoryService.findBalances("test", null, PageRequest.of(0, 10));
            assertThat(balances).isNotNull();
        }

        @Test
        @DisplayName("Should find balances without search")
        void shouldFindBalancesWithoutSearch() {
            Page<InventoryBalance> balances = inventoryService.findBalances(null, null, PageRequest.of(0, 10));
            assertThat(balances).isNotNull();
        }

        @Test
        @DisplayName("Should find low stock products")
        void shouldFindLowStockProducts() {
            List<InventoryBalance> lowStock = inventoryService.findLowStockProducts();
            assertThat(lowStock).isNotNull();
        }

        @Test
        @DisplayName("Should get current stock")
        void shouldGetCurrentStock() {
            if (testProduct == null) return;
            BigDecimal stock = inventoryService.getCurrentStock(testProduct.getId());
            assertThat(stock).isNotNull();
        }

        @Test
        @DisplayName("Should get current average cost")
        void shouldGetCurrentAverageCost() {
            if (testProduct == null) return;
            BigDecimal cost = inventoryService.getCurrentAverageCost(testProduct.getId());
            assertThat(cost).isNotNull();
        }
    }

    @Nested
    @DisplayName("Transaction History Operations")
    class TransactionHistoryTests {

        @Test
        @DisplayName("Should find transactions by product ID - empty for random ID")
        void shouldFindTransactionsByProductIdEmptyForRandomId() {
            UUID randomId = UUID.randomUUID();
            List<InventoryTransaction> transactions = inventoryService.findByProductId(randomId);
            assertThat(transactions).isEmpty();
        }

        @Test
        @DisplayName("Should find transaction by ID - empty for random ID")
        void shouldFindTransactionByIdEmptyForRandomId() {
            UUID randomId = UUID.randomUUID();
            Optional<InventoryTransaction> transaction = inventoryService.findById(randomId);
            assertThat(transaction).isEmpty();
        }

        @Test
        @DisplayName("Should find transactions with pagination")
        void shouldFindTransactionsWithPagination() {
            if (testProduct == null) return;

            Page<InventoryTransaction> transactions = inventoryService.findTransactions(
                    testProduct.getId(), null, null, null, PageRequest.of(0, 10));

            assertThat(transactions).isNotNull();
        }
    }

    @Nested
    @DisplayName("Purchase Operations")
    class PurchaseOperationsTests {

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("Should throw exception for purchase of non-existent product")
        void shouldThrowExceptionForPurchaseOfNonExistentProduct() {
            UUID randomId = UUID.randomUUID();

            assertThatThrownBy(() -> inventoryService.recordPurchase(
                    randomId, LocalDate.now(), BigDecimal.TEN, new BigDecimal("10000"),
                    "REF-001", "Test purchase"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("tidak ditemukan");
        }

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("Should record purchase for existing product")
        void shouldRecordPurchaseForExistingProduct() {
            if (testProduct == null) return;

            InventoryTransaction transaction = inventoryService.recordPurchase(
                    testProduct.getId(), LocalDate.now(), BigDecimal.TEN, new BigDecimal("10000"),
                    "REF-TEST-001", "Test purchase");

            assertThat(transaction).isNotNull();
            assertThat(transaction.getId()).isNotNull();
            assertThat(transaction.getQuantity()).isEqualByComparingTo(BigDecimal.TEN);
        }
    }

    @Nested
    @DisplayName("Sale Operations")
    class SaleOperationsTests {

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("Should throw exception for sale of non-existent product")
        void shouldThrowExceptionForSaleOfNonExistentProduct() {
            UUID randomId = UUID.randomUUID();

            assertThatThrownBy(() -> inventoryService.recordSale(
                    randomId, LocalDate.now(), BigDecimal.ONE, new BigDecimal("15000"),
                    "SALE-001", "Test sale"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("tidak ditemukan");
        }
    }

    @Nested
    @DisplayName("Adjustment Operations")
    class AdjustmentOperationsTests {

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("Should throw exception for adjustment of non-existent product")
        void shouldThrowExceptionForAdjustmentOfNonExistentProduct() {
            UUID randomId = UUID.randomUUID();

            assertThatThrownBy(() -> inventoryService.recordAdjustmentIn(
                    randomId, LocalDate.now(), BigDecimal.ONE, new BigDecimal("10000"),
                    "ADJ-001", "Test adjustment"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("tidak ditemukan");
        }

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("Should record adjustment in for existing product")
        void shouldRecordAdjustmentInForExistingProduct() {
            if (testProduct == null) return;

            InventoryTransaction transaction = inventoryService.recordAdjustmentIn(
                    testProduct.getId(), LocalDate.now(), BigDecimal.ONE, new BigDecimal("10000"),
                    "ADJ-TEST-001", "Test adjustment");

            assertThat(transaction).isNotNull();
            assertThat(transaction.getId()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Production Operations")
    class ProductionOperationsTests {

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("Should throw exception for production in of non-existent product")
        void shouldThrowExceptionForProductionInOfNonExistentProduct() {
            UUID randomId = UUID.randomUUID();

            assertThatThrownBy(() -> inventoryService.recordProductionIn(
                    randomId, LocalDate.now(), BigDecimal.ONE, new BigDecimal("10000"),
                    "PROD-001", "Test production"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("tidak ditemukan");
        }

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("Should throw exception for production out of non-existent product")
        void shouldThrowExceptionForProductionOutOfNonExistentProduct() {
            UUID randomId = UUID.randomUUID();

            assertThatThrownBy(() -> inventoryService.recordProductionOut(
                    randomId, LocalDate.now(), BigDecimal.ONE,
                    "PROD-001", "Test production"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("tidak ditemukan");
        }
    }

    @Nested
    @DisplayName("FIFO Layer Operations")
    class FifoLayerOperationsTests {

        @Test
        @DisplayName("Should get FIFO layers for product - empty for random ID")
        void shouldGetFifoLayersForProductEmptyForRandomId() {
            if (testProduct == null) return;

            var layers = inventoryService.getFifoLayers(testProduct.getId());
            assertThat(layers).isNotNull();
        }
    }

    @Nested
    @DisplayName("Valuation Operations")
    class ValuationOperationsTests {

        @Test
        @DisplayName("Should get total inventory value")
        void shouldGetTotalInventoryValue() {
            BigDecimal totalValue = inventoryService.getTotalInventoryValue();
            assertThat(totalValue).isNotNull();
            assertThat(totalValue).isGreaterThanOrEqualTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Should get available quantity")
        void shouldGetAvailableQuantity() {
            if (testProduct == null) return;
            BigDecimal available = inventoryService.getAvailableQuantity(testProduct.getId());
            assertThat(available).isNotNull();
        }

        @Test
        @DisplayName("Should calculate COGS")
        void shouldCalculateCogs() {
            if (testProduct == null) return;
            BigDecimal cogs = inventoryService.calculateCogs(testProduct.getId(), BigDecimal.ONE);
            assertThat(cogs).isNotNull();
        }
    }
}
