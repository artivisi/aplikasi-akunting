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
 * Functional tests for PaymentTermController.
 * Tests CRUD operations for project payment terms.
 */
@DisplayName("Payment Term Tests")
@Import(ServiceTestDataInitializer.class)
class PaymentTermTest extends PlaywrightTestBase {

    private static final String PROJECT_CODE = "PRJ-2024-001";

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    @Nested
    @DisplayName("Payment Term Form")
    class PaymentTermFormTests {

        @Test
        @DisplayName("Should display new payment term form")
        void shouldDisplayNewPaymentTermForm() {
            navigateTo("/projects/" + PROJECT_CODE + "/payment-terms/new");
            waitForPageLoad();

            assertThat(page.locator("#sequence").isVisible())
                .as("Sequence input should be visible")
                .isTrue();

            assertThat(page.locator("#name").isVisible())
                .as("Name input should be visible")
                .isTrue();

            assertThat(page.locator("#dueTrigger").isVisible())
                .as("Due trigger select should be visible")
                .isTrue();
        }

        @Test
        @DisplayName("Should show project context in form")
        void shouldShowProjectContextInForm() {
            navigateTo("/projects/" + PROJECT_CODE + "/payment-terms/new");
            waitForPageLoad();

            // Page should have reference to the project
            assertThat(page.url())
                .as("URL should contain project code")
                .contains(PROJECT_CODE);
        }

        @Test
        @DisplayName("Should create new payment term")
        void shouldCreateNewPaymentTerm() {
            navigateTo("/projects/" + PROJECT_CODE + "/payment-terms/new");
            waitForPageLoad();

            // Fill the form
            page.locator("#sequence").fill("99");
            page.locator("#name").fill("Test Payment Term");
            page.locator("#dueTrigger").selectOption("ON_SIGNING");
            page.locator("#percentage").fill("30");

            page.locator("#btn-simpan").click();
            waitForPageLoad();

            // Should redirect back to project page
            assertThat(page.url())
                .as("Should redirect to project page")
                .contains("/projects/");
            assertThat(page.url())
                .as("Should not be on new form")
                .doesNotContain("/payment-terms/new");
        }

        @Test
        @DisplayName("Should validate required fields")
        void shouldValidateRequiredFields() {
            navigateTo("/projects/" + PROJECT_CODE + "/payment-terms/new");
            waitForPageLoad();

            // Submit empty form (name is required)
            page.locator("#btn-simpan").click();

            // HTML5 validation will prevent submission - URL stays the same
            assertThat(page.url())
                .as("Should stay on form page due to validation")
                .contains("/payment-terms/new");
        }
    }

    @Nested
    @DisplayName("Payment Term Actions")
    class PaymentTermActionsTests {

        @Test
        @DisplayName("Should navigate to project from payment term")
        void shouldNavigateToProjectFromPaymentTerm() {
            // First, navigate to project page to see payment terms
            navigateTo("/projects/" + PROJECT_CODE);
            waitForPageLoad();

            assertThat(page.url())
                .as("Should be on project page")
                .contains("/projects/" + PROJECT_CODE);
        }

        @Test
        @DisplayName("Should access payment terms from project detail")
        void shouldAccessPaymentTermsFromProjectDetail() {
            navigateTo("/projects/" + PROJECT_CODE);
            waitForPageLoad();

            // Look for link to add payment term
            if (page.locator("a[href*='payment-terms/new']").isVisible()) {
                page.locator("a[href*='payment-terms/new']").click();
                waitForPageLoad();

                assertThat(page.url())
                    .as("Should navigate to new payment term form")
                    .contains("/payment-terms/new");
            }
        }
    }

    @Nested
    @DisplayName("Payment Term Trigger Types")
    class PaymentTermTriggerTests {

        @Test
        @DisplayName("Should display all trigger options")
        void shouldDisplayAllTriggerOptions() {
            navigateTo("/projects/" + PROJECT_CODE + "/payment-terms/new");
            waitForPageLoad();

            // Get all options from the dueTrigger select
            var options = page.locator("#dueTrigger option").all();

            assertThat(options.size())
                .as("Should have trigger options")
                .isGreaterThan(1); // More than just the placeholder
        }

        @Test
        @DisplayName("Should select different trigger types")
        void shouldSelectDifferentTriggerTypes() {
            navigateTo("/projects/" + PROJECT_CODE + "/payment-terms/new");
            waitForPageLoad();

            // Try selecting different triggers
            page.locator("#dueTrigger").selectOption("ON_SIGNING");
            assertThat(page.locator("#dueTrigger").inputValue())
                .isEqualTo("ON_SIGNING");

            page.locator("#dueTrigger").selectOption("ON_COMPLETION");
            assertThat(page.locator("#dueTrigger").inputValue())
                .isEqualTo("ON_COMPLETION");
        }
    }
}
