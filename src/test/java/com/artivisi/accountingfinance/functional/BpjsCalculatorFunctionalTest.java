package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Functional tests for BpjsCalculatorController.
 * Tests BPJS contribution calculator functionality.
 */
@DisplayName("BPJS Calculator Tests")
@Import(ServiceTestDataInitializer.class)
class BpjsCalculatorFunctionalTest extends PlaywrightTestBase {

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    @Nested
    @DisplayName("Calculator Form Display")
    class CalculatorFormDisplayTests {

        @Test
        @DisplayName("Should display calculator page with title")
        void shouldDisplayCalculatorPage() {
            navigateTo("/bpjs-calculator");
            waitForPageLoad();

            assertThat(page.title())
                .as("Page title should contain 'Kalkulator BPJS'")
                .contains("Kalkulator BPJS");

            assertThat(page.locator("#page-title").textContent())
                .as("Page heading should show Kalkulator BPJS")
                .contains("Kalkulator BPJS");
        }

        @Test
        @DisplayName("Should display salary input field")
        void shouldDisplaySalaryInput() {
            navigateTo("/bpjs-calculator");
            waitForPageLoad();

            assertThat(page.locator("#salary").isVisible())
                .as("Salary input should be visible")
                .isTrue();
        }

        @Test
        @DisplayName("Should display risk class select")
        void shouldDisplayRiskClassSelect() {
            navigateTo("/bpjs-calculator");
            waitForPageLoad();

            assertThat(page.locator("#riskClass").isVisible())
                .as("Risk class select should be visible")
                .isTrue();
        }

        @Test
        @DisplayName("Should display calculate button")
        void shouldDisplayCalculateButton() {
            navigateTo("/bpjs-calculator");
            waitForPageLoad();

            assertThat(page.locator("#btn-calculate").isVisible())
                .as("Calculate button should be visible")
                .isTrue();
        }

        @Test
        @DisplayName("Should have all 5 risk class options")
        void shouldHaveAllRiskClassOptions() {
            navigateTo("/bpjs-calculator");
            waitForPageLoad();

            var options = page.locator("#riskClass option").all();

            assertThat(options)
                .as("Should have 5 risk class options")
                .hasSize(5);
        }
    }

    @Nested
    @DisplayName("BPJS Calculation")
    class BpjsCalculationTests {

        @Test
        @DisplayName("Should calculate BPJS for basic salary")
        void shouldCalculateBpjsForBasicSalary() {
            navigateTo("/bpjs-calculator");
            waitForPageLoad();

            page.locator("#salary").fill("10000000");
            page.locator("#riskClass").selectOption("1");
            page.locator("#btn-calculate").click();
            waitForPageLoad();

            // Verify results are displayed
            assertThat(page.getByTestId("total-company").isVisible())
                .as("Total company contribution should be displayed")
                .isTrue();

            assertThat(page.getByTestId("total-employee").isVisible())
                .as("Total employee contribution should be displayed")
                .isTrue();

            assertThat(page.getByTestId("grand-total").isVisible())
                .as("Grand total should be displayed")
                .isTrue();
        }

        @Test
        @DisplayName("Should display detailed breakdown")
        void shouldDisplayDetailedBreakdown() {
            navigateTo("/bpjs-calculator");
            waitForPageLoad();

            page.locator("#salary").fill("10000000");
            page.locator("#btn-calculate").click();
            waitForPageLoad();

            // Verify individual BPJS components are displayed
            assertThat(page.getByTestId("kesehatan-company").isVisible())
                .as("BPJS Kesehatan company should be displayed")
                .isTrue();

            assertThat(page.getByTestId("kesehatan-employee").isVisible())
                .as("BPJS Kesehatan employee should be displayed")
                .isTrue();

            assertThat(page.getByTestId("jkk").isVisible())
                .as("BPJS JKK should be displayed")
                .isTrue();

            assertThat(page.getByTestId("jkm").isVisible())
                .as("BPJS JKM should be displayed")
                .isTrue();

            assertThat(page.getByTestId("jht-company").isVisible())
                .as("BPJS JHT company should be displayed")
                .isTrue();

            assertThat(page.getByTestId("jht-employee").isVisible())
                .as("BPJS JHT employee should be displayed")
                .isTrue();

            assertThat(page.getByTestId("jp-company").isVisible())
                .as("BPJS JP company should be displayed")
                .isTrue();

            assertThat(page.getByTestId("jp-employee").isVisible())
                .as("BPJS JP employee should be displayed")
                .isTrue();
        }

        @Test
        @DisplayName("Should calculate with different risk classes")
        void shouldCalculateWithDifferentRiskClasses() {
            navigateTo("/bpjs-calculator");
            waitForPageLoad();

            page.locator("#salary").fill("15000000");
            page.locator("#riskClass").selectOption("3"); // Risk class 3 - 0.89%
            page.locator("#btn-calculate").click();
            waitForPageLoad();

            // Verify results displayed
            assertThat(page.getByTestId("jkk").isVisible())
                .as("JKK should be displayed with risk class 3")
                .isTrue();
        }

        @Test
        @DisplayName("Should show ceiling warning for high salary")
        void shouldShowCeilingWarningForHighSalary() {
            navigateTo("/bpjs-calculator");
            waitForPageLoad();

            // Use salary above BPJS Kesehatan ceiling (12 million)
            page.locator("#salary").fill("15000000");
            page.locator("#btn-calculate").click();
            waitForPageLoad();

            // Should show ceiling warning
            assertThat(page.locator("text=Catatan Batas Upah").isVisible())
                .as("Ceiling warning should be displayed")
                .isTrue();
        }

        @Test
        @DisplayName("Should preserve form values after calculation")
        void shouldPreserveFormValuesAfterCalculation() {
            navigateTo("/bpjs-calculator");
            waitForPageLoad();

            page.locator("#salary").fill("20000000");
            page.locator("#riskClass").selectOption("2");
            page.locator("#btn-calculate").click();
            waitForPageLoad();

            // Verify form values are preserved
            assertThat(page.locator("#salary").inputValue())
                .as("Salary should be preserved")
                .isEqualTo("20000000");

            assertThat(page.locator("#riskClass").inputValue())
                .as("Risk class should be preserved")
                .isEqualTo("2");
        }
    }

    @Nested
    @DisplayName("Risk Class Selection")
    class RiskClassSelectionTests {

        @Test
        @DisplayName("Should select risk class 1 - IT Services")
        void shouldSelectRiskClass1() {
            navigateTo("/bpjs-calculator");
            waitForPageLoad();

            page.locator("#riskClass").selectOption("1");

            assertThat(page.locator("#riskClass").inputValue())
                .isEqualTo("1");
        }

        @Test
        @DisplayName("Should select risk class 5 - Mining")
        void shouldSelectRiskClass5() {
            navigateTo("/bpjs-calculator");
            waitForPageLoad();

            page.locator("#riskClass").selectOption("5");

            assertThat(page.locator("#riskClass").inputValue())
                .isEqualTo("5");
        }

        @Test
        @DisplayName("Risk class should affect JKK calculation")
        void riskClassShouldAffectJkkCalculation() {
            navigateTo("/bpjs-calculator");
            waitForPageLoad();

            // Calculate with risk class 1
            page.locator("#salary").fill("10000000");
            page.locator("#riskClass").selectOption("1");
            page.locator("#btn-calculate").click();
            waitForPageLoad();

            String jkkClass1 = page.getByTestId("jkk").textContent();

            // Calculate with risk class 5
            page.locator("#riskClass").selectOption("5");
            page.locator("#btn-calculate").click();
            waitForPageLoad();

            String jkkClass5 = page.getByTestId("jkk").textContent();

            // JKK should be different for different risk classes
            assertThat(jkkClass1)
                .as("JKK for risk class 1 should differ from risk class 5")
                .isNotEqualTo(jkkClass5);
        }
    }
}
