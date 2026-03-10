package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.entity.AlertEvent;
import com.artivisi.accountingfinance.entity.AlertRule;
import com.artivisi.accountingfinance.enums.AlertSeverity;
import com.artivisi.accountingfinance.enums.AlertType;
import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.repository.AlertEventRepository;
import com.artivisi.accountingfinance.repository.AlertRuleRepository;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@DisplayName("Alert Config Tests")
@Import(ServiceTestDataInitializer.class)
class AlertConfigTest extends PlaywrightTestBase {

    @Autowired
    private AlertRuleRepository alertRuleRepository;

    @Autowired
    private AlertEventRepository alertEventRepository;

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    @Test
    @DisplayName("Should display alert config page with 7 rules")
    void shouldDisplayAlertConfigPage() {
        navigateTo("/alerts/config");
        waitForPageLoad();

        assertThat(page.locator("#page-title, h1").first()).containsText("Konfigurasi Peringatan");

        // Verify all 7 alert types are listed (use exact text match on heading elements)
        assertThat(page.locator(".font-medium:text-is('Kas Rendah')")).isVisible();
        assertThat(page.locator(".font-medium:text-is('Piutang Jatuh Tempo')")).isVisible();
        assertThat(page.locator(".font-medium:text-is('Lonjakan Biaya')")).isVisible();
        assertThat(page.locator(".font-medium:text-is('Proyek Melebihi Anggaran')")).isVisible();
        assertThat(page.locator(".font-medium:text-is('Margin Proyek Turun')")).isVisible();
        assertThat(page.locator(".font-medium:text-is('Penagihan Melambat')")).isVisible();
        assertThat(page.locator(".font-medium:text-is('Konsentrasi Klien')")).isVisible();

        takeManualScreenshot("alerts/config");
    }

    @Test
    @DisplayName("Should navigate to alerts from sidebar")
    void shouldNavigateFromSidebar() {
        navigateTo("/");
        waitForPageLoad();

        // Open Laporan collapsible section
        page.locator("#nav-group-laporan").click();
        page.waitForTimeout(300);

        var sidebarLink = page.locator("#nav-alerts");
        assertThat(sidebarLink).isVisible();
        sidebarLink.click();
        waitForPageLoad();

        assertThat(page.locator("#page-title, h1").first()).containsText("Peringatan Aktif");
    }

    @Test
    @DisplayName("Should update alert threshold")
    void shouldUpdateAlertThreshold() {
        navigateTo("/alerts/config");
        waitForPageLoad();

        // Find the first threshold input (CASH_LOW) and change value
        var thresholdInputs = page.locator("input[name='threshold']");
        thresholdInputs.first().fill("20000000");

        // Click first save button
        page.locator("button:has-text('Simpan')").first().click();
        waitForPageLoad();

        // Verify success message
        assertThat(page.locator("body")).containsText("berhasil diperbarui");
    }

    @Test
    @DisplayName("Should toggle alert disabled")
    void shouldToggleAlertDisabled() {
        navigateTo("/alerts/config");
        waitForPageLoad();

        // Uncheck the first enabled checkbox
        var checkboxes = page.locator("input[name='enabled']");
        checkboxes.first().uncheck();

        // Save
        page.locator("button:has-text('Simpan')").first().click();
        waitForPageLoad();

        assertThat(page.locator("body")).containsText("berhasil diperbarui");
    }

    @Test
    @DisplayName("Should display alert history page")
    void shouldDisplayAlertHistory() {
        navigateTo("/alerts/history");
        waitForPageLoad();

        assertThat(page.locator("#page-title, h1").first()).containsText("Riwayat Peringatan");

        takeManualScreenshot("alerts/history");
    }

    @Test
    @DisplayName("Should filter alert history by alert type")
    void shouldFilterAlertHistoryByType() {
        // Create a test event for filtering
        AlertRule cashLowRule = alertRuleRepository.findByAlertType(AlertType.CASH_LOW)
                .orElseThrow();
        AlertEvent event = new AlertEvent();
        event.setAlertRule(cashLowRule);
        event.setSeverity(AlertSeverity.WARNING);
        event.setMessage("Filter test: kas rendah");
        alertEventRepository.save(event);

        navigateTo("/alerts/history?alertType=CASH_LOW");
        waitForPageLoad();

        assertThat(page.locator("#page-title, h1").first()).containsText("Riwayat Peringatan");
    }

    @Test
    @DisplayName("Should filter alert history by severity")
    void shouldFilterAlertHistoryBySeverity() {
        navigateTo("/alerts/history?severity=WARNING");
        waitForPageLoad();

        assertThat(page.locator("#page-title, h1").first()).containsText("Riwayat Peringatan");
    }

    @Test
    @DisplayName("Should filter alert history by acknowledged status")
    void shouldFilterAlertHistoryByAcknowledged() {
        navigateTo("/alerts/history?acknowledged=false");
        waitForPageLoad();

        assertThat(page.locator("#page-title, h1").first()).containsText("Riwayat Peringatan");
    }

    @Test
    @DisplayName("Should filter alert history with combined filters")
    void shouldFilterAlertHistoryWithCombinedFilters() {
        navigateTo("/alerts/history?alertType=CASH_LOW&severity=WARNING&acknowledged=false");
        waitForPageLoad();

        assertThat(page.locator("#page-title, h1").first()).containsText("Riwayat Peringatan");
    }

    @Test
    @DisplayName("Should paginate alert history")
    void shouldPaginateAlertHistory() {
        navigateTo("/alerts/history?page=0&size=5");
        waitForPageLoad();

        assertThat(page.locator("#page-title, h1").first()).containsText("Riwayat Peringatan");
    }

    @Test
    @DisplayName("Should display active alerts page")
    void shouldDisplayActiveAlertsPage() {
        navigateTo("/alerts");
        waitForPageLoad();

        assertThat(page.locator("#page-title, h1").first()).containsText("Peringatan Aktif");
    }
}
