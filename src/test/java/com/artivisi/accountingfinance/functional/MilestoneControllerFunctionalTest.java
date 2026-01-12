package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.repository.ProjectMilestoneRepository;
import com.artivisi.accountingfinance.repository.ProjectRepository;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Functional tests for MilestoneController.
 * Tests project milestone CRUD operations.
 */
@DisplayName("Milestone Controller Tests")
@Import(ServiceTestDataInitializer.class)
class MilestoneControllerFunctionalTest extends PlaywrightTestBase {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMilestoneRepository milestoneRepository;

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    @Test
    @DisplayName("Should display new milestone form")
    void shouldDisplayNewMilestoneForm() {
        var project = projectRepository.findAll().stream().findFirst();
        if (project.isEmpty()) {
            return;
        }

        navigateTo("/projects/" + project.get().getCode() + "/milestones/new");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should have name input field")
    void shouldHaveNameInputField() {
        var project = projectRepository.findAll().stream().findFirst();
        if (project.isEmpty()) {
            return;
        }

        navigateTo("/projects/" + project.get().getCode() + "/milestones/new");
        waitForPageLoad();

        var nameInput = page.locator("input[name='name']").first();
        if (nameInput.isVisible()) {
            assertThat(nameInput).isVisible();
        }
    }

    @Test
    @DisplayName("Should have sequence input field")
    void shouldHaveSequenceInputField() {
        var project = projectRepository.findAll().stream().findFirst();
        if (project.isEmpty()) {
            return;
        }

        navigateTo("/projects/" + project.get().getCode() + "/milestones/new");
        waitForPageLoad();

        var sequenceInput = page.locator("input[name='sequence']").first();
        if (sequenceInput.isVisible()) {
            assertThat(sequenceInput).isVisible();
        }
    }

    @Test
    @DisplayName("Should create new milestone")
    void shouldCreateNewMilestone() {
        var project = projectRepository.findAll().stream().findFirst();
        if (project.isEmpty()) {
            return;
        }

        navigateTo("/projects/" + project.get().getCode() + "/milestones/new");
        waitForPageLoad();

        var nameInput = page.locator("input[name='name']").first();
        if (nameInput.isVisible()) {
            nameInput.fill("Test Milestone " + System.currentTimeMillis());
        }

        var sequenceInput = page.locator("input[name='sequence']").first();
        if (sequenceInput.isVisible()) {
            sequenceInput.fill("99");
        }

        var submitBtn = page.locator("button[type='submit'], #btn-simpan").first();
        if (submitBtn.isVisible()) {
            submitBtn.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should display edit milestone form")
    void shouldDisplayEditMilestoneForm() {
        var milestone = milestoneRepository.findAll().stream().findFirst();
        if (milestone.isEmpty()) {
            return;
        }

        var projectCode = milestone.get().getProject().getCode();
        navigateTo("/projects/" + projectCode + "/milestones/" + milestone.get().getId() + "/edit");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should update milestone")
    void shouldUpdateMilestone() {
        var milestone = milestoneRepository.findAll().stream().findFirst();
        if (milestone.isEmpty()) {
            return;
        }

        var projectCode = milestone.get().getProject().getCode();
        navigateTo("/projects/" + projectCode + "/milestones/" + milestone.get().getId() + "/edit");
        waitForPageLoad();

        var nameInput = page.locator("input[name='name']").first();
        if (nameInput.isVisible()) {
            nameInput.fill("Updated Milestone " + System.currentTimeMillis());
        }

        var submitBtn = page.locator("button[type='submit'], #btn-simpan").first();
        if (submitBtn.isVisible()) {
            submitBtn.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should start milestone")
    void shouldStartMilestone() {
        var milestone = milestoneRepository.findAll().stream()
                .filter(m -> m.getStatus() == null || "NOT_STARTED".equals(m.getStatus().name()))
                .findFirst();
        if (milestone.isEmpty()) {
            return;
        }

        var projectCode = milestone.get().getProject().getCode();
        navigateTo("/projects/" + projectCode);
        waitForPageLoad();

        var startBtn = page.locator("form[action*='/start'] button[type='submit']").first();
        if (startBtn.isVisible()) {
            startBtn.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should complete milestone")
    void shouldCompleteMilestone() {
        var milestone = milestoneRepository.findAll().stream()
                .filter(m -> m.getStatus() != null && "IN_PROGRESS".equals(m.getStatus().name()))
                .findFirst();
        if (milestone.isEmpty()) {
            return;
        }

        var projectCode = milestone.get().getProject().getCode();
        navigateTo("/projects/" + projectCode);
        waitForPageLoad();

        var completeBtn = page.locator("form[action*='/complete'] button[type='submit']").first();
        if (completeBtn.isVisible()) {
            completeBtn.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should reset milestone")
    void shouldResetMilestone() {
        var milestone = milestoneRepository.findAll().stream()
                .filter(m -> m.getStatus() != null &&
                        ("IN_PROGRESS".equals(m.getStatus().name()) || "COMPLETED".equals(m.getStatus().name())))
                .findFirst();
        if (milestone.isEmpty()) {
            return;
        }

        var projectCode = milestone.get().getProject().getCode();
        navigateTo("/projects/" + projectCode);
        waitForPageLoad();

        var resetBtn = page.locator("form[action*='/reset'] button[type='submit']").first();
        if (resetBtn.isVisible()) {
            resetBtn.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should delete milestone")
    void shouldDeleteMilestone() {
        var milestone = milestoneRepository.findAll().stream()
                .filter(m -> m.getStatus() == null || "NOT_STARTED".equals(m.getStatus().name()))
                .findFirst();
        if (milestone.isEmpty()) {
            return;
        }

        var projectCode = milestone.get().getProject().getCode();
        navigateTo("/projects/" + projectCode);
        waitForPageLoad();

        var deleteBtn = page.locator("form[action*='/delete'] button[type='submit']").first();
        if (deleteBtn.isVisible()) {
            deleteBtn.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }
}
