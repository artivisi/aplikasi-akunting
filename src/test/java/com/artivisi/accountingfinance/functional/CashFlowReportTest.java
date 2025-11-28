package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.page.CashFlowReportPage;
import com.artivisi.accountingfinance.functional.page.LoginPage;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@DisplayName("Cash Flow Statement Report (Phase 2.10)")
class CashFlowReportTest extends PlaywrightTestBase {

    private LoginPage loginPage;
    private CashFlowReportPage cashFlowPage;

    private String startDate;
    private String endDate;

    @BeforeEach
    void setUp() {
        loginPage = new LoginPage(page, baseUrl());
        cashFlowPage = new CashFlowReportPage(page, baseUrl());

        // Use a date range that covers the test data
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysAgo = today.minusDays(30);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        startDate = thirtyDaysAgo.format(formatter);
        endDate = today.format(formatter);

        loginPage.navigate().loginAsAdmin();
    }

    @Test
    @DisplayName("Should display cash flow page title")
    void shouldDisplayCashFlowPageTitle() {
        cashFlowPage.navigate();

        cashFlowPage.assertPageTitleVisible();
        cashFlowPage.assertPageTitleText("Laporan Arus Kas");
    }

    @Test
    @DisplayName("Should display report title 'LAPORAN ARUS KAS'")
    void shouldDisplayReportTitle() {
        cashFlowPage.navigate();

        cashFlowPage.assertReportTitleVisible();
        cashFlowPage.assertReportTitleText("LAPORAN ARUS KAS");
    }

    @Test
    @DisplayName("Should display start date selector")
    void shouldDisplayStartDateSelector() {
        cashFlowPage.navigate();

        cashFlowPage.assertStartDateVisible();
    }

    @Test
    @DisplayName("Should display end date selector")
    void shouldDisplayEndDateSelector() {
        cashFlowPage.navigate();

        cashFlowPage.assertEndDateVisible();
    }

    @Test
    @DisplayName("Should display generate button")
    void shouldDisplayGenerateButton() {
        cashFlowPage.navigate();

        cashFlowPage.assertGenerateButtonVisible();
    }

    @Test
    @DisplayName("Should filter report by date range")
    void shouldFilterReportByDateRange() {
        cashFlowPage.navigate();

        cashFlowPage.setStartDate(startDate);
        cashFlowPage.setEndDate(endDate);
        cashFlowPage.clickGenerate();

        cashFlowPage.assertReportTitleVisible();
    }

    @Test
    @DisplayName("Should display operating activities total")
    void shouldDisplayOperatingTotal() {
        cashFlowPage.navigateWithDates(startDate, endDate);

        cashFlowPage.assertOperatingTotalVisible();
    }

    @Test
    @DisplayName("Should display investing activities total")
    void shouldDisplayInvestingTotal() {
        cashFlowPage.navigateWithDates(startDate, endDate);

        cashFlowPage.assertInvestingTotalVisible();
    }

    @Test
    @DisplayName("Should display financing activities total")
    void shouldDisplayFinancingTotal() {
        cashFlowPage.navigateWithDates(startDate, endDate);

        cashFlowPage.assertFinancingTotalVisible();
    }

    @Test
    @DisplayName("Should display net cash change")
    void shouldDisplayNetCashChange() {
        cashFlowPage.navigateWithDates(startDate, endDate);

        cashFlowPage.assertNetCashChangeVisible();
    }

    @Test
    @DisplayName("Should display beginning cash balance")
    void shouldDisplayBeginningBalance() {
        cashFlowPage.navigateWithDates(startDate, endDate);

        cashFlowPage.assertBeginningBalanceVisible();
    }

    @Test
    @DisplayName("Should display ending cash balance")
    void shouldDisplayEndingBalance() {
        cashFlowPage.navigateWithDates(startDate, endDate);

        cashFlowPage.assertEndingBalanceVisible();
    }

    @Test
    @DisplayName("Should display print button")
    void shouldDisplayPrintButton() {
        cashFlowPage.navigate();

        cashFlowPage.assertPrintButtonVisible();
    }

    @Test
    @DisplayName("Should display PDF export button")
    void shouldDisplayPdfExportButton() {
        cashFlowPage.navigate();

        cashFlowPage.assertExportPdfButtonVisible();
    }

    @Test
    @DisplayName("Should display Excel export button")
    void shouldDisplayExcelExportButton() {
        cashFlowPage.navigate();

        cashFlowPage.assertExportExcelButtonVisible();
    }
}
