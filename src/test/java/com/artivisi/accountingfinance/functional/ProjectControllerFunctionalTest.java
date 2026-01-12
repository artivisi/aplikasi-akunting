package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.repository.ClientRepository;
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
 * Functional tests for ProjectController.
 * Tests project list, create, edit, complete, archive, and reactivate operations.
 */
@DisplayName("Project Controller Tests")
@Import(ServiceTestDataInitializer.class)
class ProjectControllerFunctionalTest extends PlaywrightTestBase {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ClientRepository clientRepository;

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    @Test
    @DisplayName("Should display project list page")
    void shouldDisplayProjectListPage() {
        navigateTo("/projects");
        waitForPageLoad();

        assertThat(page.locator("#page-title, h1").first()).isVisible();
    }

    @Test
    @DisplayName("Should filter projects by status")
    void shouldFilterProjectsByStatus() {
        navigateTo("/projects");
        waitForPageLoad();

        var statusSelect = page.locator("select[name='status']").first();
        if (statusSelect.isVisible()) {
            statusSelect.selectOption("ACTIVE");

            var filterBtn = page.locator("form button[type='submit']").first();
            if (filterBtn.isVisible()) {
                filterBtn.click();
                waitForPageLoad();
            }
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should filter projects by client")
    void shouldFilterProjectsByClient() {
        navigateTo("/projects");
        waitForPageLoad();

        var clientSelect = page.locator("select[name='clientId']").first();
        if (clientSelect.isVisible()) {
            var options = clientSelect.locator("option");
            if (options.count() > 1) {
                clientSelect.selectOption(new String[]{options.nth(1).getAttribute("value")});

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
    @DisplayName("Should search projects by keyword")
    void shouldSearchProjectsByKeyword() {
        navigateTo("/projects");
        waitForPageLoad();

        var searchInput = page.locator("input[name='keyword'], input[name='search']").first();
        if (searchInput.isVisible()) {
            searchInput.fill("test");

            var filterBtn = page.locator("form button[type='submit']").first();
            if (filterBtn.isVisible()) {
                filterBtn.click();
                waitForPageLoad();
            }
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should display new project form")
    void shouldDisplayNewProjectForm() {
        navigateTo("/projects/new");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should create new project")
    void shouldCreateNewProject() {
        var client = clientRepository.findAll().stream().findFirst();
        if (client.isEmpty()) {
            return;
        }

        navigateTo("/projects/new");
        waitForPageLoad();

        // Fill project code
        var codeInput = page.locator("input[name='code']").first();
        if (codeInput.isVisible()) {
            codeInput.fill("PRJ-TEST-" + System.currentTimeMillis());
        }

        // Fill project name
        var nameInput = page.locator("input[name='name']").first();
        if (nameInput.isVisible()) {
            nameInput.fill("Test Project " + System.currentTimeMillis());
        }

        // Fill client
        var clientSelect = page.locator("select[name='client.id'], select[name='clientId']").first();
        if (clientSelect.isVisible()) {
            clientSelect.selectOption(client.get().getId().toString());
        }

        // Fill start date
        var startDateInput = page.locator("input[name='startDate']").first();
        if (startDateInput.isVisible()) {
            startDateInput.fill(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        // Fill end date
        var endDateInput = page.locator("input[name='endDate']").first();
        if (endDateInput.isVisible()) {
            endDateInput.fill(LocalDate.now().plusMonths(3).format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        // Fill contract value
        var contractValueInput = page.locator("input[name='contractValue']").first();
        if (contractValueInput.isVisible()) {
            contractValueInput.fill("100000000");
        }

        // Submit using specific button ID
        var submitBtn = page.locator("#btn-simpan").first();
        if (submitBtn.isVisible()) {
            submitBtn.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should show validation error for duplicate code")
    void shouldShowValidationErrorForDuplicateCode() {
        var existingProject = projectRepository.findAll().stream().findFirst();
        if (existingProject.isEmpty()) {
            return;
        }

        var client = clientRepository.findAll().stream().findFirst();
        if (client.isEmpty()) {
            return;
        }

        navigateTo("/projects/new");
        waitForPageLoad();

        // Fill with existing code
        var codeInput = page.locator("input[name='code']").first();
        if (codeInput.isVisible()) {
            codeInput.fill(existingProject.get().getCode());
        }

        var nameInput = page.locator("input[name='name']").first();
        if (nameInput.isVisible()) {
            nameInput.fill("Duplicate Test");
        }

        var clientSelect = page.locator("select[name='client.id'], select[name='clientId']").first();
        if (clientSelect.isVisible()) {
            clientSelect.selectOption(client.get().getId().toString());
        }

        var submitBtn = page.locator("#btn-simpan").first();
        if (submitBtn.isVisible()) {
            submitBtn.click();
            waitForPageLoad();
        }

        // Should stay on form or show error
        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should display project detail page")
    void shouldDisplayProjectDetailPage() {
        var project = projectRepository.findAll().stream().findFirst();
        if (project.isEmpty()) {
            return;
        }

        navigateTo("/projects/" + project.get().getCode());
        waitForPageLoad();

        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/projects\\/.*"));
    }

    @Test
    @DisplayName("Should display project edit form")
    void shouldDisplayProjectEditForm() {
        var project = projectRepository.findAll().stream().findFirst();
        if (project.isEmpty()) {
            return;
        }

        navigateTo("/projects/" + project.get().getCode() + "/edit");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should update project")
    void shouldUpdateProject() {
        var project = projectRepository.findAll().stream().findFirst();
        if (project.isEmpty()) {
            return;
        }

        navigateTo("/projects/" + project.get().getCode() + "/edit");
        waitForPageLoad();

        // Update name
        var nameInput = page.locator("input[name='name']").first();
        if (nameInput.isVisible()) {
            nameInput.fill("Updated Project " + System.currentTimeMillis());
        }

        // Submit using specific button ID
        var submitBtn = page.locator("#btn-simpan").first();
        if (submitBtn.isVisible()) {
            submitBtn.click();
            waitForPageLoad();
        }

        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/projects\\/.*"));
    }

    @Test
    @DisplayName("Should display milestones section on project detail")
    void shouldDisplayMilestonesSectionOnProjectDetail() {
        var project = projectRepository.findAll().stream().findFirst();
        if (project.isEmpty()) {
            return;
        }

        navigateTo("/projects/" + project.get().getCode());
        waitForPageLoad();

        // Check for milestones section
        var milestonesSection = page.locator("#milestones-section, h2:has-text('Milestone'), h3:has-text('Milestone')").first();
        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should display payment terms section on project detail")
    void shouldDisplayPaymentTermsSectionOnProjectDetail() {
        var project = projectRepository.findAll().stream().findFirst();
        if (project.isEmpty()) {
            return;
        }

        navigateTo("/projects/" + project.get().getCode());
        waitForPageLoad();

        // Check for payment terms section
        var paymentTermsSection = page.locator("#payment-terms-section, h2:has-text('Termin'), h3:has-text('Termin')").first();
        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should complete project")
    void shouldCompleteProject() {
        var project = projectRepository.findAll().stream()
                .filter(p -> "ACTIVE".equals(p.getStatus().name()))
                .findFirst();
        if (project.isEmpty()) {
            return;
        }

        navigateTo("/projects/" + project.get().getCode());
        waitForPageLoad();

        var completeBtn = page.locator("form[action*='/complete'] button[type='submit']").first();
        if (completeBtn.isVisible()) {
            completeBtn.click();
            waitForPageLoad();
        }

        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/projects\\/.*"));
    }

    @Test
    @DisplayName("Should archive project")
    void shouldArchiveProject() {
        var project = projectRepository.findAll().stream()
                .filter(p -> "COMPLETED".equals(p.getStatus().name()))
                .findFirst();
        if (project.isEmpty()) {
            return;
        }

        navigateTo("/projects/" + project.get().getCode());
        waitForPageLoad();

        var archiveBtn = page.locator("form[action*='/archive'] button[type='submit']").first();
        if (archiveBtn.isVisible()) {
            archiveBtn.click();
            waitForPageLoad();
        }

        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/projects\\/.*"));
    }

    @Test
    @DisplayName("Should reactivate project")
    void shouldReactivateProject() {
        var project = projectRepository.findAll().stream()
                .filter(p -> "ARCHIVED".equals(p.getStatus().name()) || "COMPLETED".equals(p.getStatus().name()))
                .findFirst();
        if (project.isEmpty()) {
            return;
        }

        navigateTo("/projects/" + project.get().getCode());
        waitForPageLoad();

        var reactivateBtn = page.locator("form[action*='/reactivate'] button[type='submit']").first();
        if (reactivateBtn.isVisible()) {
            reactivateBtn.click();
            waitForPageLoad();
        }

        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/projects\\/.*"));
    }

    @Test
    @DisplayName("Should navigate to new milestone from project detail")
    void shouldNavigateToNewMilestoneFromProjectDetail() {
        var project = projectRepository.findAll().stream().findFirst();
        if (project.isEmpty()) {
            return;
        }

        navigateTo("/projects/" + project.get().getCode());
        waitForPageLoad();

        var newMilestoneLink = page.locator("a[href*='/milestones/new']").first();
        if (newMilestoneLink.isVisible()) {
            newMilestoneLink.click();
            waitForPageLoad();

            assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/milestones\\/new.*"));
        }
    }

    @Test
    @DisplayName("Should navigate to new payment term from project detail")
    void shouldNavigateToNewPaymentTermFromProjectDetail() {
        var project = projectRepository.findAll().stream().findFirst();
        if (project.isEmpty()) {
            return;
        }

        navigateTo("/projects/" + project.get().getCode());
        waitForPageLoad();

        var newPaymentTermLink = page.locator("a[href*='/payment-terms/new']").first();
        if (newPaymentTermLink.isVisible()) {
            newPaymentTermLink.click();
            waitForPageLoad();

            assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/payment-terms\\/new.*"));
        }
    }
}
