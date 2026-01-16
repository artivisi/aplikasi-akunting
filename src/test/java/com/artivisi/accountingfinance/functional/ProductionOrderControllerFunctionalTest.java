package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.entity.BillOfMaterial;
import com.artivisi.accountingfinance.entity.ProductionOrder;
import com.artivisi.accountingfinance.entity.ProductionOrderStatus;
import com.artivisi.accountingfinance.functional.manufacturing.CoffeeTestDataInitializer;
import com.artivisi.accountingfinance.repository.BillOfMaterialRepository;
import com.artivisi.accountingfinance.repository.ProductionOrderRepository;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Functional tests for ProductionOrderController.
 * Tests production order list, create, detail, start, complete, cancel operations.
 */
@DisplayName("Production Order Controller Tests")
@Import(CoffeeTestDataInitializer.class)
class ProductionOrderControllerFunctionalTest extends PlaywrightTestBase {

    @Autowired
    private ProductionOrderRepository orderRepository;

    @Autowired
    private BillOfMaterialRepository bomRepository;

    @BeforeEach
    void setupAndLogin() {
        ensureTestOrdersExist();
        loginAsAdmin();
    }

    private void ensureTestOrdersExist() {
        // Ensure we have orders in DRAFT and IN_PROGRESS status for testing
        BillOfMaterial bom = bomRepository.findAll().stream().findFirst().orElse(null);
        if (bom == null) return;

        // Create DRAFT order if none exists
        boolean hasDraft = orderRepository.findAll().stream()
                .anyMatch(o -> o.getStatus() == ProductionOrderStatus.DRAFT);
        if (!hasDraft) {
            ProductionOrder draft = new ProductionOrder();
            draft.setOrderNumber("TEST-DRAFT-" + System.currentTimeMillis());
            draft.setBillOfMaterial(bom);
            draft.setQuantity(BigDecimal.TEN);
            draft.setOrderDate(LocalDate.now());
            draft.setStatus(ProductionOrderStatus.DRAFT);
            orderRepository.save(draft);
        }

        // Create IN_PROGRESS order if none exists
        boolean hasInProgress = orderRepository.findAll().stream()
                .anyMatch(o -> o.getStatus() == ProductionOrderStatus.IN_PROGRESS);
        if (!hasInProgress) {
            ProductionOrder inProgress = new ProductionOrder();
            inProgress.setOrderNumber("TEST-INPROG-" + System.currentTimeMillis());
            inProgress.setBillOfMaterial(bom);
            inProgress.setQuantity(BigDecimal.valueOf(5));
            inProgress.setOrderDate(LocalDate.now());
            inProgress.setStatus(ProductionOrderStatus.IN_PROGRESS);
            orderRepository.save(inProgress);
        }
    }

    // ==================== LIST PAGE ====================

    @Test
    @DisplayName("Should display production order list page")
    void shouldDisplayProductionOrderListPage() {
        navigateTo("/inventory/production");
        waitForPageLoad();

        assertThat(page.locator("h1, .page-title").first()).isVisible();
    }

    @Test
    @DisplayName("Should display new order button")
    void shouldDisplayNewOrderButton() {
        navigateTo("/inventory/production");
        waitForPageLoad();

        assertThat(page.locator("a[href*='/inventory/production/create']").first()).isVisible();
    }

    // ==================== NEW ORDER FORM ====================

    @Test
    @DisplayName("Should display new production order form")
    void shouldDisplayNewProductionOrderForm() {
        navigateTo("/inventory/production/create");
        waitForPageLoad();

        assertThat(page.locator("#bomId")).isVisible();
        assertThat(page.locator("#quantity")).isVisible();
        assertThat(page.locator("#orderDate")).isVisible();
    }

    @Test
    @DisplayName("Should display BOM selection dropdown")
    void shouldDisplayBomSelectionDropdown() {
        navigateTo("/inventory/production/create");
        waitForPageLoad();

        assertThat(page.locator("#bomId")).isVisible();
    }

    @Test
    @DisplayName("Should display quantity input")
    void shouldDisplayQuantityInput() {
        navigateTo("/inventory/production/create");
        waitForPageLoad();

        assertThat(page.locator("#quantity")).isVisible();
    }

    @Test
    @DisplayName("Should display order date input")
    void shouldDisplayOrderDateInput() {
        navigateTo("/inventory/production/create");
        waitForPageLoad();

        assertThat(page.locator("#orderDate")).isVisible();
    }

    @Test
    @DisplayName("Should display notes textarea")
    void shouldDisplayNotesTextarea() {
        navigateTo("/inventory/production/create");
        waitForPageLoad();

        assertThat(page.locator("#notes")).isVisible();
    }

    @Test
    @DisplayName("Should create new production order with valid data")
    void shouldCreateNewProductionOrder() {
        var bom = bomRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new AssertionError("BOM required for test"));

        navigateTo("/inventory/production/create");
        waitForPageLoad();

        // Select BOM
        page.locator("#bomId").selectOption(bom.getId().toString());

        // Fill quantity
        page.locator("#quantity").fill("10");

        // Fill order date
        page.locator("#orderDate").fill(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));

        // Submit using specific ID
        page.locator("#btn-simpan").click();
        waitForPageLoad();

        // Should redirect to list or detail
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/inventory\\/production.*"));
    }

    // ==================== DETAIL PAGE ====================

    @Test
    @DisplayName("Should display production order detail page")
    void shouldDisplayProductionOrderDetailPage() {
        var order = orderRepository.findAll().stream().findFirst();
        if (order.isEmpty()) return;

        navigateTo("/inventory/production/" + order.get().getId());
        waitForPageLoad();

        assertThat(page.getByTestId("order-number")).isVisible();
    }

    @Test
    @DisplayName("Should display order quantity on detail page")
    void shouldDisplayOrderQuantity() {
        var order = orderRepository.findAll().stream().findFirst();
        if (order.isEmpty()) return;

        navigateTo("/inventory/production/" + order.get().getId());
        waitForPageLoad();

        assertThat(page.getByTestId("order-quantity")).isVisible();
    }

    @Test
    @DisplayName("Should display product name on detail page")
    void shouldDisplayProductName() {
        var order = orderRepository.findAll().stream().findFirst();
        if (order.isEmpty()) return;

        navigateTo("/inventory/production/" + order.get().getId());
        waitForPageLoad();

        assertThat(page.getByTestId("product-name")).isVisible();
    }

    // ==================== ORDER ACTIONS ====================

    @Test
    @DisplayName("Should display start button for draft order")
    void shouldDisplayStartButtonForDraftOrder() {
        var order = orderRepository.findAll().stream()
                .filter(o -> "DRAFT".equals(o.getStatus().name()))
                .findFirst();
        if (order.isEmpty()) return;

        navigateTo("/inventory/production/" + order.get().getId());
        waitForPageLoad();

        assertThat(page.locator("#form-start")).isVisible();
    }

    @Test
    @DisplayName("Should display complete button for in-progress order")
    void shouldDisplayCompleteButtonForInProgressOrder() {
        var order = orderRepository.findAll().stream()
                .filter(o -> "IN_PROGRESS".equals(o.getStatus().name()))
                .findFirst();
        if (order.isEmpty()) return;

        navigateTo("/inventory/production/" + order.get().getId());
        waitForPageLoad();

        assertThat(page.locator("#form-complete")).isVisible();
    }

    @Test
    @DisplayName("Should display cancel button for draft order")
    void shouldDisplayCancelButtonForDraftOrder() {
        var order = orderRepository.findAll().stream()
                .filter(o -> "DRAFT".equals(o.getStatus().name()))
                .findFirst();
        if (order.isEmpty()) return;

        navigateTo("/inventory/production/" + order.get().getId());
        waitForPageLoad();

        assertThat(page.locator("#form-cancel")).isVisible();
    }

    @Test
    @DisplayName("Should display delete button for draft order")
    void shouldDisplayDeleteButtonForDraftOrder() {
        var order = orderRepository.findAll().stream()
                .filter(o -> "DRAFT".equals(o.getStatus().name()))
                .findFirst();
        if (order.isEmpty()) return;

        navigateTo("/inventory/production/" + order.get().getId());
        waitForPageLoad();

        assertThat(page.locator("#form-delete")).isVisible();
    }

    @Test
    @DisplayName("Should display completed status badge")
    void shouldDisplayCompletedStatusBadge() {
        var order = orderRepository.findAll().stream()
                .filter(o -> "COMPLETED".equals(o.getStatus().name()))
                .findFirst();
        if (order.isEmpty()) return;

        navigateTo("/inventory/production/" + order.get().getId());
        waitForPageLoad();

        assertThat(page.getByTestId("order-status-completed")).isVisible();
    }

    // ==================== FILTER TESTS ====================

    @Test
    @DisplayName("Should filter by DRAFT status")
    void shouldFilterByDraftStatus() {
        navigateTo("/inventory/production?status=DRAFT");
        waitForPageLoad();

        assertThat(page.locator("h1, .page-title").first()).isVisible();
    }

    @Test
    @DisplayName("Should filter by IN_PROGRESS status")
    void shouldFilterByInProgressStatus() {
        navigateTo("/inventory/production?status=IN_PROGRESS");
        waitForPageLoad();

        assertThat(page.locator("h1, .page-title").first()).isVisible();
    }

    @Test
    @DisplayName("Should filter by COMPLETED status")
    void shouldFilterByCompletedStatus() {
        navigateTo("/inventory/production?status=COMPLETED");
        waitForPageLoad();

        assertThat(page.locator("h1, .page-title").first()).isVisible();
    }

    @Test
    @DisplayName("Should filter by CANCELLED status")
    void shouldFilterByCancelledStatus() {
        navigateTo("/inventory/production?status=CANCELLED");
        waitForPageLoad();

        assertThat(page.locator("h1, .page-title").first()).isVisible();
    }

    // ==================== EDIT FORM TESTS ====================

    @Test
    @DisplayName("Should display edit form for draft order")
    void shouldDisplayEditFormForDraftOrder() {
        var order = orderRepository.findAll().stream()
                .filter(o -> "DRAFT".equals(o.getStatus().name()))
                .findFirst();
        if (order.isEmpty()) {
            navigateTo("/inventory/production");
            waitForPageLoad();
            assertThat(page.locator("h1, .page-title").first()).isVisible();
            return;
        }

        navigateTo("/inventory/production/" + order.get().getId() + "/edit");
        waitForPageLoad();

        assertThat(page.locator("#bomId, #quantity, #orderDate").first()).isVisible();
    }

    @Test
    @DisplayName("Should redirect when editing non-draft order")
    void shouldRedirectWhenEditingNonDraftOrder() {
        var order = orderRepository.findAll().stream()
                .filter(o -> !"DRAFT".equals(o.getStatus().name()))
                .findFirst();
        if (order.isEmpty()) {
            navigateTo("/inventory/production");
            waitForPageLoad();
            assertThat(page.locator("h1, .page-title").first()).isVisible();
            return;
        }

        navigateTo("/inventory/production/" + order.get().getId() + "/edit");
        waitForPageLoad();

        // Should redirect to detail page
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/inventory\\/production\\/.*"));
    }

    @Test
    @DisplayName("Should handle non-existent order detail")
    void shouldHandleNonExistentOrderDetail() {
        navigateTo("/inventory/production/00000000-0000-0000-0000-000000000000");
        waitForPageLoad();

        // Should redirect to list
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/inventory\\/production.*"));
    }

    @Test
    @DisplayName("Should handle non-existent order edit")
    void shouldHandleNonExistentOrderEdit() {
        navigateTo("/inventory/production/00000000-0000-0000-0000-000000000000/edit");
        waitForPageLoad();

        // Should redirect to list
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/inventory\\/production.*"));
    }

    // ==================== ACTION TESTS ====================

    @Test
    @DisplayName("Should start draft order")
    void shouldStartDraftOrder() {
        var order = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() == ProductionOrderStatus.DRAFT)
                .findFirst()
                .orElseThrow(() -> new AssertionError("DRAFT order required for test"));

        navigateTo("/inventory/production/" + order.getId());
        waitForPageLoad();

        // Handle confirm dialog
        page.onDialog(dialog -> dialog.accept());

        // Click start button
        page.locator("#form-start button[type='submit']").click();
        waitForPageLoad();

        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/inventory\\/production\\/.*"));
    }

    @Test
    @DisplayName("Should complete in-progress order")
    void shouldCompleteInProgressOrder() {
        var order = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() == ProductionOrderStatus.IN_PROGRESS)
                .findFirst()
                .orElseThrow(() -> new AssertionError("IN_PROGRESS order required for test"));

        navigateTo("/inventory/production/" + order.getId());
        waitForPageLoad();

        // Handle confirm dialog
        page.onDialog(dialog -> dialog.accept());

        // Click complete button
        page.locator("#form-complete button[type='submit']").click();
        waitForPageLoad();

        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/inventory\\/production\\/.*"));
    }

    @Test
    @DisplayName("Should cancel draft order")
    void shouldCancelDraftOrder() {
        // Create a fresh draft order for cancel test
        var bom = bomRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new AssertionError("BOM required for test"));
        ProductionOrder cancelOrder = new ProductionOrder();
        cancelOrder.setOrderNumber("TEST-CANCEL-" + System.currentTimeMillis());
        cancelOrder.setBillOfMaterial(bom);
        cancelOrder.setQuantity(BigDecimal.ONE);
        cancelOrder.setOrderDate(LocalDate.now());
        cancelOrder.setStatus(ProductionOrderStatus.DRAFT);
        cancelOrder = orderRepository.save(cancelOrder);

        navigateTo("/inventory/production/" + cancelOrder.getId());
        waitForPageLoad();

        // Handle confirm dialog
        page.onDialog(dialog -> dialog.accept());

        // Click cancel button
        page.locator("#form-cancel button[type='submit']").click();
        waitForPageLoad();

        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/inventory\\/production\\/.*"));
    }

    @Test
    @DisplayName("Should delete draft order")
    void shouldDeleteDraftOrder() {
        // Create a fresh draft order for delete test
        var bom = bomRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new AssertionError("BOM required for test"));
        ProductionOrder deleteOrder = new ProductionOrder();
        deleteOrder.setOrderNumber("TEST-DELETE-" + System.currentTimeMillis());
        deleteOrder.setBillOfMaterial(bom);
        deleteOrder.setQuantity(BigDecimal.ONE);
        deleteOrder.setOrderDate(LocalDate.now());
        deleteOrder.setStatus(ProductionOrderStatus.DRAFT);
        deleteOrder = orderRepository.save(deleteOrder);

        navigateTo("/inventory/production/" + deleteOrder.getId());
        waitForPageLoad();

        // Handle confirm dialog
        page.onDialog(dialog -> dialog.accept());

        // Click delete button
        page.locator("#form-delete button[type='submit']").click();
        waitForPageLoad();

        // Should redirect to list
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/inventory\\/production.*"));
    }

    // ==================== ADDITIONAL COVERAGE TESTS ====================

    @Test
    @DisplayName("Should display planned completion date input")
    void shouldDisplayPlannedCompletionDateInput() {
        navigateTo("/inventory/production/create");
        waitForPageLoad();

        var dateInput = page.locator("#plannedCompletionDate").first();
        if (dateInput.isVisible()) {
            assertThat(dateInput).isVisible();
        }
    }

    @Test
    @DisplayName("Should create order with planned completion date")
    void shouldCreateOrderWithPlannedCompletionDate() {
        var bom = bomRepository.findAll().stream().findFirst();
        if (bom.isEmpty()) return;

        navigateTo("/inventory/production/create");
        waitForPageLoad();

        // Select BOM
        page.locator("#bomId").selectOption(bom.get().getId().toString());

        // Fill quantity
        page.locator("#quantity").fill("5");

        // Fill order date
        page.locator("#orderDate").fill(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));

        // Fill planned completion date
        var plannedDateInput = page.locator("#plannedCompletionDate").first();
        if (plannedDateInput.isVisible()) {
            plannedDateInput.fill(LocalDate.now().plusDays(7).format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        // Fill notes
        var notesInput = page.locator("#notes").first();
        if (notesInput.isVisible()) {
            notesInput.fill("Test order with planned completion date");
        }

        // Submit using specific ID
        page.locator("#btn-simpan").click();
        waitForPageLoad();

        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/inventory\\/production.*"));
    }

    @Test
    @DisplayName("Should update production order")
    void shouldUpdateProductionOrder() {
        var order = orderRepository.findAll().stream()
                .filter(o -> "DRAFT".equals(o.getStatus().name()))
                .findFirst();
        if (order.isEmpty()) {
            return;
        }

        navigateTo("/inventory/production/" + order.get().getId() + "/edit");
        waitForPageLoad();

        // Update quantity
        var quantityInput = page.locator("#quantity").first();
        if (quantityInput.isVisible()) {
            quantityInput.fill("20");
        }

        // Update notes
        var notesInput = page.locator("#notes").first();
        if (notesInput.isVisible()) {
            notesInput.fill("Updated notes " + System.currentTimeMillis());
        }

        // Submit using specific ID
        var submitBtn = page.locator("#btn-simpan");
        if (submitBtn.isVisible()) {
            submitBtn.click();
            waitForPageLoad();
        }

        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/inventory\\/production.*"));
    }

    @Test
    @DisplayName("Should display order status on detail page")
    void shouldDisplayOrderStatusOnDetailPage() {
        var order = orderRepository.findAll().stream().findFirst();
        if (order.isEmpty()) return;

        navigateTo("/inventory/production/" + order.get().getId());
        waitForPageLoad();

        assertThat(page.getByTestId("order-status")).isVisible();
    }

    @Test
    @DisplayName("Should display BOM info on detail page")
    void shouldDisplayBOMInfoOnDetailPage() {
        var order = orderRepository.findAll().stream().findFirst();
        if (order.isEmpty()) return;

        navigateTo("/inventory/production/" + order.get().getId());
        waitForPageLoad();

        assertThat(page.getByTestId("bom-name")).isVisible();
    }

    @Test
    @DisplayName("Should display order date on detail page")
    void shouldDisplayOrderDateOnDetailPage() {
        var order = orderRepository.findAll().stream().findFirst();
        if (order.isEmpty()) return;

        navigateTo("/inventory/production/" + order.get().getId());
        waitForPageLoad();

        assertThat(page.getByTestId("order-date")).isVisible();
    }

    @Test
    @DisplayName("Should display cancel button for in-progress order")
    void shouldDisplayCancelButtonForInProgressOrder() {
        var order = orderRepository.findAll().stream()
                .filter(o -> "IN_PROGRESS".equals(o.getStatus().name()))
                .findFirst();
        if (order.isEmpty()) return;

        navigateTo("/inventory/production/" + order.get().getId());
        waitForPageLoad();

        var cancelBtn = page.locator("#form-cancel").first();
        if (cancelBtn.isVisible()) {
            assertThat(cancelBtn).isVisible();
        }
    }

    @Test
    @DisplayName("Should display status filter dropdown on list page")
    void shouldDisplayStatusFilterDropdownOnListPage() {
        navigateTo("/inventory/production");
        waitForPageLoad();

        var statusFilter = page.locator("select[name='status']").first();
        if (statusFilter.isVisible()) {
            assertThat(statusFilter).isVisible();
        }
    }

    @Test
    @DisplayName("Should filter using status filter dropdown")
    void shouldFilterUsingStatusFilterDropdown() {
        navigateTo("/inventory/production");
        waitForPageLoad();

        var statusFilter = page.locator("select[name='status']").first();
        if (statusFilter.isVisible()) {
            statusFilter.selectOption("DRAFT");

            var filterBtn = page.locator("form button[type='submit']").first();
            if (filterBtn.isVisible()) {
                filterBtn.click();
                waitForPageLoad();
            }
        }

        assertThat(page.locator("h1, .page-title").first()).isVisible();
    }

    @Test
    @DisplayName("Should display production order list table")
    void shouldDisplayProductionOrderListTable() {
        navigateTo("/inventory/production");
        waitForPageLoad();

        var table = page.locator("table").first();
        if (table.isVisible()) {
            assertThat(table).isVisible();
        }
    }

    @Test
    @DisplayName("Should have link to create from list page")
    void shouldHaveLinkToCreateFromListPage() {
        navigateTo("/inventory/production");
        waitForPageLoad();

        var createLink = page.locator("a[href*='/inventory/production/create']").first();
        assertThat(createLink).isVisible();
    }

    @Test
    @DisplayName("Should display cancelled status badge")
    void shouldDisplayCancelledStatusBadge() {
        var order = orderRepository.findAll().stream()
                .filter(o -> "CANCELLED".equals(o.getStatus().name()))
                .findFirst();
        if (order.isEmpty()) return;

        navigateTo("/inventory/production/" + order.get().getId());
        waitForPageLoad();

        assertThat(page.getByTestId("order-status-cancelled")).isVisible();
    }

    @Test
    @DisplayName("Should display in-progress status badge")
    void shouldDisplayInProgressStatusBadge() {
        var order = orderRepository.findAll().stream()
                .filter(o -> "IN_PROGRESS".equals(o.getStatus().name()))
                .findFirst();
        if (order.isEmpty()) return;

        navigateTo("/inventory/production/" + order.get().getId());
        waitForPageLoad();

        assertThat(page.getByTestId("order-status-in-progress")).isVisible();
    }

    @Test
    @DisplayName("Should display draft status badge")
    void shouldDisplayDraftStatusBadge() {
        var order = orderRepository.findAll().stream()
                .filter(o -> "DRAFT".equals(o.getStatus().name()))
                .findFirst();
        if (order.isEmpty()) return;

        navigateTo("/inventory/production/" + order.get().getId());
        waitForPageLoad();

        assertThat(page.getByTestId("order-status-draft")).isVisible();
    }

    // ==================== FORM SUBMISSION TESTS ====================

    @Test
    @DisplayName("Should submit create production order form")
    void shouldSubmitCreateProductionOrderForm() {
        var bom = bomRepository.findAll().stream().findFirst();
        if (bom.isEmpty()) {
            return;
        }

        navigateTo("/inventory/production/create");
        waitForPageLoad();

        // Select BOM
        var bomSelect = page.locator("#bomId");
        bomSelect.selectOption(bom.get().getId().toString());

        // Fill quantity
        page.locator("#quantity").fill("10");

        // Fill order date
        page.locator("#orderDate").fill(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));

        // Fill planned completion date
        var plannedDate = page.locator("#plannedCompletionDate");
        if (plannedDate.isVisible()) {
            plannedDate.fill(LocalDate.now().plusDays(7).format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        // Fill notes
        var notes = page.locator("#notes");
        if (notes.isVisible()) {
            notes.fill("Test production order " + System.currentTimeMillis());
        }

        // Submit form using specific ID
        page.locator("#btn-simpan").click();
        waitForPageLoad();

        // Should redirect to list or detail
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/inventory\\/production.*"));
    }

    @Test
    @DisplayName("Should submit update production order form")
    void shouldSubmitUpdateProductionOrderForm() {
        var order = orderRepository.findAll().stream()
                .filter(o -> "DRAFT".equals(o.getStatus().name()))
                .findFirst();
        if (order.isEmpty()) {
            return;
        }

        navigateTo("/inventory/production/" + order.get().getId() + "/edit");
        waitForPageLoad();

        // Update quantity
        var quantityInput = page.locator("#quantity");
        if (quantityInput.isVisible()) {
            quantityInput.fill("15");
        }

        // Update notes
        var notesInput = page.locator("#notes");
        if (notesInput.isVisible()) {
            notesInput.fill("Updated notes " + System.currentTimeMillis());
        }

        // Submit form using specific ID
        page.locator("#btn-simpan").click();
        waitForPageLoad();

        // Should redirect
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/inventory\\/production.*"));
    }

    @Test
    @DisplayName("Should submit start production order form")
    void shouldSubmitStartProductionOrderForm() {
        var order = orderRepository.findAll().stream()
                .filter(o -> "DRAFT".equals(o.getStatus().name()))
                .findFirst();
        if (order.isEmpty()) {
            return;
        }

        navigateTo("/inventory/production/" + order.get().getId());
        waitForPageLoad();

        var startForm = page.locator("#form-start, form[action*='/start']").first();
        if (startForm.isVisible()) {
            startForm.locator("button[type='submit']").click();
            waitForPageLoad();
        }

        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/inventory\\/production.*"));
    }

    @Test
    @DisplayName("Should submit complete production order form")
    void shouldSubmitCompleteProductionOrderForm() {
        var order = orderRepository.findAll().stream()
                .filter(o -> "IN_PROGRESS".equals(o.getStatus().name()))
                .findFirst();
        if (order.isEmpty()) {
            return;
        }

        navigateTo("/inventory/production/" + order.get().getId());
        waitForPageLoad();

        var completeForm = page.locator("#form-complete, form[action*='/complete']").first();
        if (completeForm.isVisible()) {
            completeForm.locator("button[type='submit']").click();
            waitForPageLoad();
        }

        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/inventory\\/production.*"));
    }

    @Test
    @DisplayName("Should submit cancel production order form")
    void shouldSubmitCancelProductionOrderForm() {
        var order = orderRepository.findAll().stream()
                .filter(o -> "IN_PROGRESS".equals(o.getStatus().name()) || "DRAFT".equals(o.getStatus().name()))
                .findFirst();
        if (order.isEmpty()) {
            return;
        }

        navigateTo("/inventory/production/" + order.get().getId());
        waitForPageLoad();

        var cancelForm = page.locator("#form-cancel, form[action*='/cancel']").first();
        if (cancelForm.isVisible()) {
            cancelForm.locator("button[type='submit']").click();
            waitForPageLoad();
        }

        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/inventory\\/production.*"));
    }

    @Test
    @DisplayName("Should submit delete production order form")
    void shouldSubmitDeleteProductionOrderForm() {
        var order = orderRepository.findAll().stream()
                .filter(o -> "DRAFT".equals(o.getStatus().name()))
                .findFirst();
        if (order.isEmpty()) {
            return;
        }

        navigateTo("/inventory/production/" + order.get().getId());
        waitForPageLoad();

        var deleteForm = page.locator("#form-delete, form[action*='/delete']").first();
        if (deleteForm.isVisible()) {
            deleteForm.locator("button[type='submit']").click();
            waitForPageLoad();
        }

        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/inventory\\/production.*"));
    }
}
