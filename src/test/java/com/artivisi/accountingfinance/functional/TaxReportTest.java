package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.page.LoginPage;
import com.artivisi.accountingfinance.functional.page.PPNSummaryPage;
import com.artivisi.accountingfinance.functional.page.PPh23WithholdingPage;
import com.artivisi.accountingfinance.functional.page.TaxSummaryPage;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tax Reports (Phase 2.6)")
class TaxReportTest extends PlaywrightTestBase {

    private LoginPage loginPage;
    private PPNSummaryPage ppnSummaryPage;
    private PPh23WithholdingPage pph23Page;
    private TaxSummaryPage taxSummaryPage;

    // Test data uses CURRENT_DATE - INTERVAL, so we need dynamic dates
    private String startDate;
    private String endDate;

    @BeforeEach
    void setUp() {
        loginPage = new LoginPage(page, baseUrl());
        ppnSummaryPage = new PPNSummaryPage(page, baseUrl());
        pph23Page = new PPh23WithholdingPage(page, baseUrl());
        taxSummaryPage = new TaxSummaryPage(page, baseUrl());

        // Test data is inserted relative to CURRENT_DATE
        // Use a date range that covers the test data (last 10 days)
        LocalDate today = LocalDate.now();
        LocalDate tenDaysAgo = today.minusDays(10);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        startDate = tenDaysAgo.format(formatter);
        endDate = today.format(formatter);

        loginPage.navigate().loginAsAdmin();
    }

    @Nested
    @DisplayName("6.1 PPN Summary Report")
    class PPNSummaryTests {

        @Nested
        @DisplayName("6.1.1 Navigation")
        class NavigationTests {

            @Test
            @DisplayName("Should display PPN summary page title")
            void shouldDisplayPPNSummaryPageTitle() {
                ppnSummaryPage.navigate();

                ppnSummaryPage.assertPageTitleVisible();
                ppnSummaryPage.assertPageTitleText("Ringkasan PPN");
            }

            @Test
            @DisplayName("Should display report title 'RINGKASAN PPN'")
            void shouldDisplayReportTitle() {
                ppnSummaryPage.navigate();

                ppnSummaryPage.assertReportTitleVisible();
                ppnSummaryPage.assertReportTitleText("RINGKASAN PPN");
            }
        }

        @Nested
        @DisplayName("6.1.2 Filter Controls")
        class FilterControlsTests {

            @Test
            @DisplayName("Should display start date selector")
            void shouldDisplayStartDateSelector() {
                ppnSummaryPage.navigate();

                ppnSummaryPage.assertStartDateVisible();
            }

            @Test
            @DisplayName("Should display end date selector")
            void shouldDisplayEndDateSelector() {
                ppnSummaryPage.navigate();

                ppnSummaryPage.assertEndDateVisible();
            }

            @Test
            @DisplayName("Should display generate button")
            void shouldDisplayGenerateButton() {
                ppnSummaryPage.navigate();

                ppnSummaryPage.assertGenerateButtonVisible();
            }

            @Test
            @DisplayName("Should display print button")
            void shouldDisplayPrintButton() {
                ppnSummaryPage.navigate();

                ppnSummaryPage.assertPrintButtonVisible();
            }
        }

        @Nested
        @DisplayName("6.1.3 PPN Report Structure")
        class PPNReportStructureTests {

            @Test
            @DisplayName("Should display PPN Keluaran section")
            void shouldDisplayPPNKeluaranSection() {
                ppnSummaryPage.navigateWithDates(startDate, endDate);

                ppnSummaryPage.assertPPNKeluaranVisible();
            }

            @Test
            @DisplayName("Should display PPN Masukan section")
            void shouldDisplayPPNMasukanSection() {
                ppnSummaryPage.navigateWithDates(startDate, endDate);

                ppnSummaryPage.assertPPNMasukanVisible();
            }

            @Test
            @DisplayName("Should display Net PPN")
            void shouldDisplayNetPPN() {
                ppnSummaryPage.navigateWithDates(startDate, endDate);

                ppnSummaryPage.assertNetPPNVisible();
            }

            @Test
            @DisplayName("Should display PPN status")
            void shouldDisplayPPNStatus() {
                ppnSummaryPage.navigateWithDates(startDate, endDate);

                ppnSummaryPage.assertPPNStatusVisible();
            }
        }

        @Nested
        @DisplayName("6.1.4 PPN Calculation")
        class PPNCalculationTests {

            @Test
            @DisplayName("Should show expected PPN Keluaran")
            void shouldShowExpectedPPNKeluaran() {
                // Test data: PPN Keluaran = 1,100,000 + 1,650,000 = 2,750,000
                ppnSummaryPage.navigateWithDates(startDate, endDate);

                String ppnKeluaran = ppnSummaryPage.getPPNKeluaranText();
                assertThat(ppnKeluaran).isEqualTo("2.750.000");
            }

            @Test
            @DisplayName("Should show expected PPN Masukan")
            void shouldShowExpectedPPNMasukan() {
                // Test data: PPN Masukan = 550,000
                ppnSummaryPage.navigateWithDates(startDate, endDate);

                String ppnMasukan = ppnSummaryPage.getPPNMasukanText();
                assertThat(ppnMasukan).isEqualTo("(550.000)");
            }

            @Test
            @DisplayName("Should show expected Net PPN (Kurang Bayar)")
            void shouldShowExpectedNetPPN() {
                // Test data: Net PPN = 2,750,000 - 550,000 = 2,200,000
                ppnSummaryPage.navigateWithDates(startDate, endDate);

                String netPPN = ppnSummaryPage.getNetPPNText();
                assertThat(netPPN).isEqualTo("2.200.000");
            }

            @Test
            @DisplayName("Should show kurang bayar message for positive net PPN")
            void shouldShowKurangBayarMessage() {
                ppnSummaryPage.navigateWithDates(startDate, endDate);

                ppnSummaryPage.assertPPNMessageContains("disetor");
            }
        }
    }

    @Nested
    @DisplayName("6.2 PPh 23 Withholding Report")
    class PPh23WithholdingTests {

        @Nested
        @DisplayName("6.2.1 Navigation")
        class NavigationTests {

            @Test
            @DisplayName("Should display PPh 23 page title")
            void shouldDisplayPPh23PageTitle() {
                pph23Page.navigate();

                pph23Page.assertPageTitleVisible();
                pph23Page.assertPageTitleText("Pemotongan PPh 23");
            }

            @Test
            @DisplayName("Should display report title")
            void shouldDisplayReportTitle() {
                pph23Page.navigate();

                pph23Page.assertReportTitleVisible();
                pph23Page.assertReportTitleText("LAPORAN PEMOTONGAN PPh 23");
            }
        }

        @Nested
        @DisplayName("6.2.2 Filter Controls")
        class FilterControlsTests {

            @Test
            @DisplayName("Should display start date selector")
            void shouldDisplayStartDateSelector() {
                pph23Page.navigate();

                pph23Page.assertStartDateVisible();
            }

            @Test
            @DisplayName("Should display end date selector")
            void shouldDisplayEndDateSelector() {
                pph23Page.navigate();

                pph23Page.assertEndDateVisible();
            }

            @Test
            @DisplayName("Should display generate button")
            void shouldDisplayGenerateButton() {
                pph23Page.navigate();

                pph23Page.assertGenerateButtonVisible();
            }

            @Test
            @DisplayName("Should display print button")
            void shouldDisplayPrintButton() {
                pph23Page.navigate();

                pph23Page.assertPrintButtonVisible();
            }
        }

        @Nested
        @DisplayName("6.2.3 PPh 23 Report Structure")
        class PPh23ReportStructureTests {

            @Test
            @DisplayName("Should display total withheld section")
            void shouldDisplayTotalWithheldSection() {
                pph23Page.navigateWithDates(startDate, endDate);

                pph23Page.assertTotalWithheldVisible();
            }

            @Test
            @DisplayName("Should display total deposited section")
            void shouldDisplayTotalDepositedSection() {
                pph23Page.navigateWithDates(startDate, endDate);

                pph23Page.assertTotalDepositedVisible();
            }

            @Test
            @DisplayName("Should display balance")
            void shouldDisplayBalance() {
                pph23Page.navigateWithDates(startDate, endDate);

                pph23Page.assertBalanceVisible();
            }

            @Test
            @DisplayName("Should display PPh 23 status")
            void shouldDisplayPPh23Status() {
                pph23Page.navigateWithDates(startDate, endDate);

                pph23Page.assertPPh23StatusVisible();
            }
        }

        @Nested
        @DisplayName("6.2.4 PPh 23 Calculation")
        class PPh23CalculationTests {

            @Test
            @DisplayName("Should show expected total withheld")
            void shouldShowExpectedTotalWithheld() {
                // Test data: PPh 23 Withheld = 40,000
                pph23Page.navigateWithDates(startDate, endDate);

                String totalWithheld = pph23Page.getTotalWithheldText();
                assertThat(totalWithheld).isEqualTo("40.000");
            }

            @Test
            @DisplayName("Should show expected total deposited (zero)")
            void shouldShowExpectedTotalDeposited() {
                // Test data: PPh 23 Deposited = 0
                pph23Page.navigateWithDates(startDate, endDate);

                String totalDeposited = pph23Page.getTotalDepositedText();
                assertThat(totalDeposited).isEqualTo("(0)");
            }

            @Test
            @DisplayName("Should show expected balance")
            void shouldShowExpectedBalance() {
                // Test data: PPh 23 Balance = 40,000
                pph23Page.navigateWithDates(startDate, endDate);

                String balance = pph23Page.getBalanceText();
                assertThat(balance).isEqualTo("40.000");
            }

            @Test
            @DisplayName("Should show outstanding message for positive balance")
            void shouldShowOutstandingMessage() {
                pph23Page.navigateWithDates(startDate, endDate);

                pph23Page.assertPPh23MessageContains("disetor");
            }
        }
    }

    @Nested
    @DisplayName("6.3 Tax Summary Report")
    class TaxSummaryTests {

        @Nested
        @DisplayName("6.3.1 Navigation")
        class NavigationTests {

            @Test
            @DisplayName("Should display tax summary page title")
            void shouldDisplayTaxSummaryPageTitle() {
                taxSummaryPage.navigate();

                taxSummaryPage.assertPageTitleVisible();
                taxSummaryPage.assertPageTitleText("Ringkasan Pajak");
            }

            @Test
            @DisplayName("Should display report title 'RINGKASAN PAJAK'")
            void shouldDisplayReportTitle() {
                taxSummaryPage.navigate();

                taxSummaryPage.assertReportTitleVisible();
                taxSummaryPage.assertReportTitleText("RINGKASAN PAJAK");
            }
        }

        @Nested
        @DisplayName("6.3.2 Filter Controls")
        class FilterControlsTests {

            @Test
            @DisplayName("Should display start date selector")
            void shouldDisplayStartDateSelector() {
                taxSummaryPage.navigate();

                taxSummaryPage.assertStartDateVisible();
            }

            @Test
            @DisplayName("Should display end date selector")
            void shouldDisplayEndDateSelector() {
                taxSummaryPage.navigate();

                taxSummaryPage.assertEndDateVisible();
            }

            @Test
            @DisplayName("Should display generate button")
            void shouldDisplayGenerateButton() {
                taxSummaryPage.navigate();

                taxSummaryPage.assertGenerateButtonVisible();
            }
        }

        @Nested
        @DisplayName("6.3.3 Tax Summary Structure")
        class TaxSummaryStructureTests {

            @Test
            @DisplayName("Should display tax table")
            void shouldDisplayTaxTable() {
                taxSummaryPage.navigateWithDates(startDate, endDate);

                taxSummaryPage.assertTaxTableVisible();
            }

            @Test
            @DisplayName("Should show tax account rows")
            void shouldShowTaxAccountRows() {
                taxSummaryPage.navigateWithDates(startDate, endDate);

                int rowCount = taxSummaryPage.getTaxRowCount();
                assertThat(rowCount).isGreaterThan(0);
            }

            @Test
            @DisplayName("Should show PPN Masukan account")
            void shouldShowPPNMasukanAccount() {
                taxSummaryPage.navigateWithDates(startDate, endDate);

                taxSummaryPage.assertTaxAccountExists("1.1.25");
            }

            @Test
            @DisplayName("Should show Hutang PPN account")
            void shouldShowHutangPPNAccount() {
                taxSummaryPage.navigateWithDates(startDate, endDate);

                taxSummaryPage.assertTaxAccountExists("2.1.03");
            }

            @Test
            @DisplayName("Should show Hutang PPh 23 account")
            void shouldShowHutangPPh23Account() {
                taxSummaryPage.navigateWithDates(startDate, endDate);

                taxSummaryPage.assertTaxAccountExists("2.1.21");
            }
        }

        @Nested
        @DisplayName("6.3.4 Quick Links")
        class QuickLinksTests {

            @Test
            @DisplayName("Should navigate to PPN Summary from quick link")
            void shouldNavigateToPPNSummary() {
                taxSummaryPage.navigateWithDates(startDate, endDate);
                taxSummaryPage.clickPPNSummaryLink();

                ppnSummaryPage.assertPageTitleText("Ringkasan PPN");
            }

            @Test
            @DisplayName("Should navigate to PPh 23 from quick link")
            void shouldNavigateToPPh23() {
                taxSummaryPage.navigateWithDates(startDate, endDate);
                taxSummaryPage.clickPPh23Link();

                pph23Page.assertPageTitleText("Pemotongan PPh 23");
            }
        }
    }
}
