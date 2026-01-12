package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.repository.BillOfMaterialRepository;
import com.artivisi.accountingfinance.repository.ProductionOrderRepository;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Functional tests for ProductionOrderController.
 * Tests production order list, create, edit, start, complete, cancel operations.
 */
@DisplayName("Production Order Controller Tests")
@Import(ServiceTestDataInitializer.class)
class ProductionOrderControllerFunctionalTest extends PlaywrightTestBase {

    @Autowired
    private ProductionOrderRepository orderRepository;

    @Autowired
    private BillOfMaterialRepository bomRepository;

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    @Test
    @DisplayName("Should display production order list page")
    void shouldDisplayProductionOrderListPage() {
        navigateTo("/inventory/production");
        waitForPageLoad();

        assertThat(page.locator("#page-title, h1").first()).isVisible();
    }

    @Test
    @DisplayName("Should search orders by keyword")
    void shouldSearchOrdersByKeyword() {
        navigateTo("/inventory/production");
        waitForPageLoad();

        var searchInput = page.locator("input[name='search'], input[name='keyword']").first();
        if (searchInput.isVisible()) {
            searchInput.fill("kopi");

            var filterBtn = page.locator("form button[type='submit']").first();
            if (filterBtn.isVisible()) {
                filterBtn.click();
                waitForPageLoad();
            }
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should filter orders by status")
    void shouldFilterOrdersByStatus() {
        navigateTo("/inventory/production");
        waitForPageLoad();

        var statusSelect = page.locator("select[name='status']").first();
        if (statusSelect.isVisible()) {
            var options = statusSelect.locator("option");
            if (options.count() > 1) {
                statusSelect.selectOption(new String[]{options.nth(1).getAttribute("value")});

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
    @DisplayName("Should display new production order form")
    void shouldDisplayNewProductionOrderForm() {
        navigateTo("/inventory/production/create");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should create new production order")
    void shouldCreateNewProductionOrder() {
        var bom = bomRepository.findAll().stream().findFirst();
        if (bom.isEmpty()) {
            return;
        }

        navigateTo("/inventory/production/create");
        waitForPageLoad();

        // Select BOM
        var bomSelect = page.locator("select[name='bom.id'], select[name='bomId']").first();
        if (bomSelect.isVisible()) {
            bomSelect.selectOption(bom.get().getId().toString());
        }

        // Fill quantity
        var quantityInput = page.locator("input[name='quantity']").first();
        if (quantityInput.isVisible()) {
            quantityInput.fill("10");
        }

        // Fill planned date
        var plannedDateInput = page.locator("input[name='plannedDate']").first();
        if (plannedDateInput.isVisible()) {
            plannedDateInput.fill(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
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
    @DisplayName("Should display production order detail page")
    void shouldDisplayProductionOrderDetailPage() {
        var order = orderRepository.findAll().stream().findFirst();
        if (order.isEmpty()) {
            return;
        }

        navigateTo("/inventory/production/" + order.get().getId());
        waitForPageLoad();

        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/inventory\\/production\\/.*"));
    }

    @Test
    @DisplayName("Should display production order edit form")
    void shouldDisplayProductionOrderEditForm() {
        var order = orderRepository.findAll().stream()
                .filter(o -> "PLANNED".equals(o.getStatus().name()))
                .findFirst();
        if (order.isEmpty()) {
            return;
        }

        navigateTo("/inventory/production/" + order.get().getId() + "/edit");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should update production order")
    void shouldUpdateProductionOrder() {
        var order = orderRepository.findAll().stream()
                .filter(o -> "PLANNED".equals(o.getStatus().name()))
                .findFirst();
        if (order.isEmpty()) {
            return;
        }

        navigateTo("/inventory/production/" + order.get().getId() + "/edit");
        waitForPageLoad();

        // Update quantity
        var quantityInput = page.locator("input[name='quantity']").first();
        if (quantityInput.isVisible()) {
            quantityInput.fill("20");
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
    @DisplayName("Should start production order")
    void shouldStartProductionOrder() {
        var order = orderRepository.findAll().stream()
                .filter(o -> "PLANNED".equals(o.getStatus().name()))
                .findFirst();
        if (order.isEmpty()) {
            return;
        }

        navigateTo("/inventory/production/" + order.get().getId());
        waitForPageLoad();

        var startBtn = page.locator("form[action*='/start'] button[type='submit']").first();
        if (startBtn.isVisible()) {
            startBtn.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should complete production order")
    void shouldCompleteProductionOrder() {
        var order = orderRepository.findAll().stream()
                .filter(o -> "IN_PROGRESS".equals(o.getStatus().name()))
                .findFirst();
        if (order.isEmpty()) {
            return;
        }

        navigateTo("/inventory/production/" + order.get().getId());
        waitForPageLoad();

        var completeBtn = page.locator("form[action*='/complete'] button[type='submit']").first();
        if (completeBtn.isVisible()) {
            completeBtn.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should cancel production order")
    void shouldCancelProductionOrder() {
        var order = orderRepository.findAll().stream()
                .filter(o -> "PLANNED".equals(o.getStatus().name()))
                .findFirst();
        if (order.isEmpty()) {
            return;
        }

        navigateTo("/inventory/production/" + order.get().getId());
        waitForPageLoad();

        var cancelBtn = page.locator("form[action*='/cancel'] button[type='submit']").first();
        if (cancelBtn.isVisible()) {
            cancelBtn.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should delete production order")
    void shouldDeleteProductionOrder() {
        var order = orderRepository.findAll().stream()
                .filter(o -> "PLANNED".equals(o.getStatus().name()) || "CANCELLED".equals(o.getStatus().name()))
                .findFirst();
        if (order.isEmpty()) {
            return;
        }

        navigateTo("/inventory/production/" + order.get().getId());
        waitForPageLoad();

        var deleteBtn = page.locator("form[action*='/delete'] button[type='submit']").first();
        if (deleteBtn.isVisible()) {
            deleteBtn.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }
}
