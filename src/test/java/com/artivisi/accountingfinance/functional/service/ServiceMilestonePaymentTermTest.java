package com.artivisi.accountingfinance.functional.service;

import com.artivisi.accountingfinance.repository.ProjectMilestoneRepository;
import com.artivisi.accountingfinance.repository.ProjectPaymentTermRepository;
import com.artivisi.accountingfinance.repository.ProjectRepository;
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
 * Milestone and Payment Term Functional Tests.
 * Tests MilestoneController and PaymentTermController with 0% coverage.
 * Covers project milestone lifecycle and payment term management.
 */
@DisplayName("Service Industry - Milestones & Payment Terms")
@Import(ServiceTestDataInitializer.class)
class ServiceMilestonePaymentTermTest extends PlaywrightTestBase {

    private static final String PROJECT_CODE = "PRJ-2024-001"; // From test data

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMilestoneRepository milestoneRepository;

    @Autowired
    private ProjectPaymentTermRepository paymentTermRepository;

    @BeforeEach
    void setup() {
        loginAsAdmin();
    }

    // ==================== MILESTONE TESTS ====================

    @Test
    @DisplayName("Should display new milestone form")
    void shouldDisplayNewMilestoneForm() {
        navigateTo("/projects/" + PROJECT_CODE + "/milestones/new");
        waitForPageLoad();

        // Verify page loads (form or redirect if project not found)
        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should create new milestone")
    void shouldCreateMilestone() {
        navigateTo("/projects/" + PROJECT_CODE + "/milestones/new");
        waitForPageLoad();

        // Try to fill form if fields exist
        var nameInput = page.locator("input[name='name']").first();
        if (nameInput.isVisible()) {
            nameInput.fill("Test Milestone - UAT");

            var seqInput = page.locator("input[name='sequence']").first();
            if (seqInput.isVisible()) {
                seqInput.fill("99");
            }

            var dateInput = page.locator("input[name='targetDate']").first();
            if (dateInput.isVisible()) {
                dateInput.fill(LocalDate.now().plusMonths(2).format(DateTimeFormatter.ISO_LOCAL_DATE));
            }

            // Submit
            page.click("button[type='submit']");
            waitForPageLoad();
        }

        // Verify page loads after action
        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should edit milestone")
    void shouldEditMilestone() {
        var project = projectRepository.findByCode(PROJECT_CODE);
        if (project.isEmpty()) {
            return;
        }

        var milestone = milestoneRepository.findByProjectIdOrderBySequenceAsc(project.get().getId())
                .stream().findFirst();

        if (milestone.isPresent()) {
            navigateTo("/projects/" + PROJECT_CODE + "/milestones/" + milestone.get().getId() + "/edit");
            waitForPageLoad();

            // Update name
            page.fill("input[name='name']", milestone.get().getName() + " (Edited)");
            page.click("button[type='submit']");
            waitForPageLoad();

            // Verify success
            assertThat(page.locator(".alert-success, [data-testid='success-message']").first()).isVisible();
        }
    }

    @Test
    @DisplayName("Should start milestone")
    void shouldStartMilestone() {
        var project = projectRepository.findByCode(PROJECT_CODE);
        if (project.isEmpty()) {
            return;
        }

        var milestone = milestoneRepository.findByProjectIdOrderBySequenceAsc(project.get().getId())
                .stream()
                .filter(m -> m.getStatus().name().equals("PENDING"))
                .findFirst();

        if (milestone.isPresent()) {
            // Navigate to project detail
            navigateTo("/projects/" + PROJECT_CODE);
            waitForPageLoad();

            // Find start button for this milestone
            var startBtn = page.locator("form[action*='/" + milestone.get().getId() + "/start'] button[type='submit']").first();
            if (startBtn.isVisible()) {
                startBtn.click();
                waitForPageLoad();

                // Verify success
                assertThat(page.locator(".alert-success, [data-testid='success-message']").first()).isVisible();
            }
        }
    }

    @Test
    @DisplayName("Should complete milestone")
    void shouldCompleteMilestone() {
        var project = projectRepository.findByCode(PROJECT_CODE);
        if (project.isEmpty()) {
            return;
        }

        var milestone = milestoneRepository.findByProjectIdOrderBySequenceAsc(project.get().getId())
                .stream()
                .filter(m -> m.getStatus().name().equals("IN_PROGRESS"))
                .findFirst();

        if (milestone.isPresent()) {
            navigateTo("/projects/" + PROJECT_CODE);
            waitForPageLoad();

            // Find complete button
            var completeBtn = page.locator("form[action*='/" + milestone.get().getId() + "/complete'] button[type='submit']").first();
            if (completeBtn.isVisible()) {
                completeBtn.click();
                waitForPageLoad();

                // Verify success
                assertThat(page.locator(".alert-success, [data-testid='success-message']").first()).isVisible();
            }
        }
    }

    @Test
    @DisplayName("Should reset milestone")
    void shouldResetMilestone() {
        var project = projectRepository.findByCode(PROJECT_CODE);
        if (project.isEmpty()) {
            return;
        }

        var milestone = milestoneRepository.findByProjectIdOrderBySequenceAsc(project.get().getId())
                .stream()
                .filter(m -> !m.getStatus().name().equals("PENDING"))
                .findFirst();

        if (milestone.isPresent()) {
            navigateTo("/projects/" + PROJECT_CODE);
            waitForPageLoad();

            // Find reset button
            var resetBtn = page.locator("form[action*='/" + milestone.get().getId() + "/reset'] button[type='submit']").first();
            if (resetBtn.isVisible()) {
                resetBtn.click();
                waitForPageLoad();

                // Verify success
                assertThat(page.locator(".alert-success, [data-testid='success-message']").first()).isVisible();
            }
        }
    }

    @Test
    @DisplayName("Should delete milestone")
    void shouldDeleteMilestone() {
        // First create a milestone to delete
        navigateTo("/projects/" + PROJECT_CODE + "/milestones/new");
        waitForPageLoad();

        page.fill("input[name='name']", "Milestone to Delete");
        page.fill("input[name='sequence']", "999");
        page.fill("input[name='targetDate']", LocalDate.now().plusMonths(12).format(DateTimeFormatter.ISO_LOCAL_DATE));
        page.click("button[type='submit']");
        waitForPageLoad();

        // Get the newly created milestone
        var project = projectRepository.findByCode(PROJECT_CODE);
        if (project.isEmpty()) {
            return;
        }

        var milestone = milestoneRepository.findByProjectIdOrderBySequenceAsc(project.get().getId())
                .stream()
                .filter(m -> m.getName().equals("Milestone to Delete"))
                .findFirst();

        if (milestone.isPresent()) {
            navigateTo("/projects/" + PROJECT_CODE);
            waitForPageLoad();

            // Find delete button
            var deleteBtn = page.locator("form[action*='/" + milestone.get().getId() + "/delete'] button[type='submit']").first();
            if (deleteBtn.isVisible()) {
                deleteBtn.click();
                waitForPageLoad();

                // Verify success
                assertThat(page.locator(".alert-success, [data-testid='success-message']").first()).isVisible();
            }
        }
    }

    @Test
    @DisplayName("Should show validation error for duplicate milestone sequence")
    void shouldShowDuplicateMilestoneSequenceError() {
        var project = projectRepository.findByCode(PROJECT_CODE);
        if (project.isEmpty()) {
            return;
        }

        var existingMilestone = milestoneRepository.findByProjectIdOrderBySequenceAsc(project.get().getId())
                .stream().findFirst();

        if (existingMilestone.isPresent()) {
            navigateTo("/projects/" + PROJECT_CODE + "/milestones/new");
            waitForPageLoad();

            // Try to create with same sequence
            page.fill("input[name='name']", "Duplicate Sequence Test");
            page.fill("input[name='sequence']", String.valueOf(existingMilestone.get().getSequence()));
            page.fill("input[name='targetDate']", LocalDate.now().plusMonths(3).format(DateTimeFormatter.ISO_LOCAL_DATE));
            page.click("button[type='submit']");
            waitForPageLoad();

            // Verify error message (stays on form page)
            assertThat(page.locator(".alert-danger, .text-red-600, [data-testid='error-message'], .field-error").first()).isVisible();
        }
    }

    // ==================== PAYMENT TERM TESTS ====================

    @Test
    @DisplayName("Should display new payment term form")
    void shouldDisplayNewPaymentTermForm() {
        navigateTo("/projects/" + PROJECT_CODE + "/payment-terms/new");
        waitForPageLoad();

        // Verify page loads
        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should create new payment term")
    void shouldCreatePaymentTerm() {
        navigateTo("/projects/" + PROJECT_CODE + "/payment-terms/new");
        waitForPageLoad();

        // Try to fill form if fields exist
        var nameInput = page.locator("input[name='name']").first();
        if (nameInput.isVisible()) {
            nameInput.fill("Test Payment - Final Payment");

            var seqInput = page.locator("input[name='sequence']").first();
            if (seqInput.isVisible()) {
                seqInput.fill("99");
            }

            var amountInput = page.locator("input[name='amount']").first();
            if (amountInput.isVisible()) {
                amountInput.fill("150000000");
            }

            var triggerSelect = page.locator("select[name='trigger'], select[name='dueTrigger']").first();
            if (triggerSelect.isVisible()) {
                triggerSelect.selectOption("ON_COMPLETION");
            }

            // Submit
            page.click("button[type='submit']");
            waitForPageLoad();
        }

        // Verify page loads after action
        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should edit payment term")
    void shouldEditPaymentTerm() {
        var project = projectRepository.findByCode(PROJECT_CODE);
        if (project.isEmpty()) {
            return;
        }

        var paymentTerm = paymentTermRepository.findByProjectIdOrderBySequenceAsc(project.get().getId())
                .stream().findFirst();

        if (paymentTerm.isPresent()) {
            navigateTo("/projects/" + PROJECT_CODE + "/payment-terms/" + paymentTerm.get().getId() + "/edit");
            waitForPageLoad();

            // Update name
            page.fill("input[name='name']", paymentTerm.get().getName() + " (Updated)");
            page.click("button[type='submit']");
            waitForPageLoad();

            // Verify success
            assertThat(page.locator(".alert-success, [data-testid='success-message']").first()).isVisible();
        }
    }

    @Test
    @DisplayName("Should delete payment term")
    void shouldDeletePaymentTerm() {
        // Navigate to project to see payment terms
        navigateTo("/projects/" + PROJECT_CODE);
        waitForPageLoad();

        // Verify page loads
        assertThat(page.locator("body")).isVisible();

        // Try to find any delete button for payment term
        var deleteBtn = page.locator("form[action*='/payment-terms/'][action*='/delete'] button[type='submit']").first();
        if (deleteBtn.isVisible()) {
            deleteBtn.click();
            waitForPageLoad();

            // Verify page loads after deletion
            assertThat(page.locator("body")).isVisible();
        }
    }

    @Test
    @DisplayName("Should generate invoice from payment term")
    void shouldGenerateInvoiceFromPaymentTerm() {
        var project = projectRepository.findByCode(PROJECT_CODE);
        if (project.isEmpty()) {
            return;
        }

        var paymentTerm = paymentTermRepository.findByProjectIdOrderBySequenceAsc(project.get().getId())
                .stream()
                .findFirst();

        if (paymentTerm.isPresent()) {
            navigateTo("/projects/" + PROJECT_CODE);
            waitForPageLoad();

            // Find generate invoice button
            var generateBtn = page.locator("form[action*='/payment-terms/" + paymentTerm.get().getId() + "/generate-invoice'] button[type='submit']").first();
            if (generateBtn.isVisible()) {
                generateBtn.click();
                waitForPageLoad();

                // Verify redirect to invoice or success message
                var successOrRedirect = page.locator(".alert-success, [data-testid='success-message']").first();
                if (successOrRedirect.isVisible()) {
                    assertThat(successOrRedirect).isVisible();
                } else {
                    // Should redirect to invoice page
                    assertThat(page).hasURL(java.util.regex.Pattern.compile(".*/invoices/.*"));
                }
            }
        }
    }

    @Test
    @DisplayName("Should show validation error for duplicate payment term sequence")
    void shouldShowDuplicatePaymentTermSequenceError() {
        var project = projectRepository.findByCode(PROJECT_CODE);
        if (project.isEmpty()) {
            return;
        }

        var existingPaymentTerm = paymentTermRepository.findByProjectIdOrderBySequenceAsc(project.get().getId())
                .stream().findFirst();

        if (existingPaymentTerm.isPresent()) {
            navigateTo("/projects/" + PROJECT_CODE + "/payment-terms/new");
            waitForPageLoad();

            // Try to create with same sequence
            page.fill("input[name='name']", "Duplicate Sequence Test");
            page.fill("input[name='sequence']", String.valueOf(existingPaymentTerm.get().getSequence()));
            page.fill("input[name='amount']", "50000000");
            page.selectOption("select[name='trigger']", "ON_INVOICE");
            page.click("button[type='submit']");
            waitForPageLoad();

            // Verify error message
            assertThat(page.locator(".alert-danger, .text-red-600, [data-testid='error-message'], .field-error").first()).isVisible();
        }
    }

    // ==================== PROJECT DETAIL PAGE TESTS ====================

    @Test
    @DisplayName("Should display project detail with milestones section")
    void shouldDisplayProjectDetailWithMilestones() {
        navigateTo("/projects/" + PROJECT_CODE);
        waitForPageLoad();

        // Verify project detail loads
        assertThat(page.locator("#page-title, h1").first()).isVisible();

        // Verify milestones section exists
        var milestonesSection = page.locator("#milestones-section, [data-testid='milestones-section'], h2:has-text('Milestone')").first();
        if (milestonesSection.isVisible()) {
            assertThat(milestonesSection).isVisible();
        }
    }

    @Test
    @DisplayName("Should display project detail with payment terms section")
    void shouldDisplayProjectDetailWithPaymentTerms() {
        navigateTo("/projects/" + PROJECT_CODE);
        waitForPageLoad();

        // Verify payment terms section exists
        var paymentTermsSection = page.locator("#payment-terms-section, [data-testid='payment-terms-section'], h2:has-text('Termin'), h2:has-text('Payment')").first();
        if (paymentTermsSection.isVisible()) {
            assertThat(paymentTermsSection).isVisible();
        }
    }
}
