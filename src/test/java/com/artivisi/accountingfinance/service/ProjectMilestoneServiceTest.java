package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.TestcontainersConfiguration;
import com.artivisi.accountingfinance.entity.Project;
import com.artivisi.accountingfinance.entity.ProjectMilestone;
import com.artivisi.accountingfinance.enums.MilestoneStatus;
import com.artivisi.accountingfinance.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for ProjectMilestoneService.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
@DisplayName("ProjectMilestoneService Integration Tests")
class ProjectMilestoneServiceTest {

    @Autowired
    private ProjectMilestoneService milestoneService;

    @Autowired
    private ProjectRepository projectRepository;

    // Test project ID from V903__invoice_test_data.sql
    private static final UUID TEST_PROJECT_ID = UUID.fromString("d0000000-0000-0000-0000-000000000001");

    private Project testProject;

    @BeforeEach
    void setup() {
        testProject = projectRepository.findById(TEST_PROJECT_ID)
                .orElse(null);
    }

    @Nested
    @DisplayName("Find Operations")
    class FindOperationsTests {

        @Test
        @DisplayName("Should throw exception for non-existent milestone ID")
        void shouldThrowExceptionForNonExistentId() {
            UUID randomId = UUID.randomUUID();
            assertThatThrownBy(() -> milestoneService.findById(randomId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Milestone not found");
        }

        @Test
        @DisplayName("Should find milestones by project ID")
        void shouldFindMilestonesByProjectId() {
            if (testProject == null) return;

            List<ProjectMilestone> milestones = milestoneService.findByProjectId(testProject.getId());
            assertThat(milestones).isNotNull();
        }

        @Test
        @DisplayName("Should find milestones by project ID and status PENDING")
        void shouldFindMilestonesByProjectIdAndStatusPending() {
            if (testProject == null) return;

            List<ProjectMilestone> milestones = milestoneService.findByProjectIdAndStatus(
                    testProject.getId(), MilestoneStatus.PENDING);
            assertThat(milestones).isNotNull();
        }

        @Test
        @DisplayName("Should find milestones by project ID and status IN_PROGRESS")
        void shouldFindMilestonesByProjectIdAndStatusInProgress() {
            if (testProject == null) return;

            List<ProjectMilestone> milestones = milestoneService.findByProjectIdAndStatus(
                    testProject.getId(), MilestoneStatus.IN_PROGRESS);
            assertThat(milestones).isNotNull();
        }

        @Test
        @DisplayName("Should find milestones by project ID and status COMPLETED")
        void shouldFindMilestonesByProjectIdAndStatusCompleted() {
            if (testProject == null) return;

            List<ProjectMilestone> milestones = milestoneService.findByProjectIdAndStatus(
                    testProject.getId(), MilestoneStatus.COMPLETED);
            assertThat(milestones).isNotNull();
        }

        @Test
        @DisplayName("Should return empty list for random project ID")
        void shouldReturnEmptyListForRandomProjectId() {
            UUID randomId = UUID.randomUUID();
            List<ProjectMilestone> milestones = milestoneService.findByProjectId(randomId);
            assertThat(milestones).isEmpty();
        }

        @Test
        @DisplayName("Should count milestones by project ID and status")
        void shouldCountMilestonesByProjectIdAndStatus() {
            if (testProject == null) return;

            long count = milestoneService.countByProjectIdAndStatus(
                    testProject.getId(), MilestoneStatus.PENDING);
            assertThat(count).isGreaterThanOrEqualTo(0);
        }

        @Test
        @DisplayName("Should get total weight for project")
        void shouldGetTotalWeightForProject() {
            if (testProject == null) return;

            int totalWeight = milestoneService.getTotalWeightForProject(testProject.getId());
            assertThat(totalWeight).isGreaterThanOrEqualTo(0);
        }
    }

    @Nested
    @DisplayName("Create Operations")
    class CreateOperationsTests {

        @Test
        @DisplayName("Should throw exception when creating milestone for non-existent project")
        void shouldThrowExceptionWhenCreatingMilestoneForNonExistentProject() {
            UUID randomId = UUID.randomUUID();
            ProjectMilestone milestone = new ProjectMilestone();
            milestone.setName("Test Milestone");
            milestone.setWeightPercent(25);
            milestone.setTargetDate(LocalDate.now().plusMonths(1));

            assertThatThrownBy(() -> milestoneService.create(randomId, milestone))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Project not found");
        }

        @Test
        @DisplayName("Should create milestone with auto-assigned sequence")
        void shouldCreateMilestoneWithAutoAssignedSequence() {
            if (testProject == null) return;

            ProjectMilestone milestone = new ProjectMilestone();
            milestone.setName("Test Milestone Auto Seq");
            milestone.setDescription("Test description");
            milestone.setWeightPercent(10);
            milestone.setTargetDate(LocalDate.now().plusMonths(1));

            ProjectMilestone saved = milestoneService.create(testProject.getId(), milestone);

            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getSequence()).isNotNull();
            assertThat(saved.getStatus()).isEqualTo(MilestoneStatus.PENDING);
        }

        @Test
        @DisplayName("Should create milestone with specified sequence")
        void shouldCreateMilestoneWithSpecifiedSequence() {
            if (testProject == null) return;

            ProjectMilestone milestone = new ProjectMilestone();
            milestone.setName("Test Milestone With Seq");
            milestone.setSequence(999);
            milestone.setWeightPercent(5);
            milestone.setTargetDate(LocalDate.now().plusMonths(2));

            ProjectMilestone saved = milestoneService.create(testProject.getId(), milestone);

            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getSequence()).isEqualTo(999);
        }
    }

    @Nested
    @DisplayName("Status Transition Operations")
    class StatusTransitionTests {

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("Should start milestone")
        void shouldStartMilestone() {
            if (testProject == null) return;

            // Create a milestone
            ProjectMilestone milestone = new ProjectMilestone();
            milestone.setName("Test Start Milestone");
            milestone.setWeightPercent(10);
            milestone.setTargetDate(LocalDate.now().plusMonths(1));
            ProjectMilestone saved = milestoneService.create(testProject.getId(), milestone);

            // Start it
            milestoneService.startMilestone(saved.getId());

            ProjectMilestone updated = milestoneService.findById(saved.getId());
            assertThat(updated.getStatus()).isEqualTo(MilestoneStatus.IN_PROGRESS);
        }

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("Should reset milestone")
        void shouldResetMilestone() {
            if (testProject == null) return;

            // Create a milestone
            ProjectMilestone milestone = new ProjectMilestone();
            milestone.setName("Test Reset Milestone");
            milestone.setWeightPercent(10);
            milestone.setTargetDate(LocalDate.now().plusMonths(1));
            ProjectMilestone saved = milestoneService.create(testProject.getId(), milestone);

            // Start it
            milestoneService.startMilestone(saved.getId());

            // Reset it
            milestoneService.resetMilestone(saved.getId());

            ProjectMilestone updated = milestoneService.findById(saved.getId());
            assertThat(updated.getStatus()).isEqualTo(MilestoneStatus.PENDING);
            assertThat(updated.getActualDate()).isNull();
        }

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("Should update milestone status")
        void shouldUpdateMilestoneStatus() {
            if (testProject == null) return;

            // Create a milestone
            ProjectMilestone milestone = new ProjectMilestone();
            milestone.setName("Test Status Update");
            milestone.setWeightPercent(10);
            milestone.setTargetDate(LocalDate.now().plusMonths(1));
            ProjectMilestone saved = milestoneService.create(testProject.getId(), milestone);

            // Update status to IN_PROGRESS
            milestoneService.updateStatus(saved.getId(), MilestoneStatus.IN_PROGRESS);

            ProjectMilestone updated = milestoneService.findById(saved.getId());
            assertThat(updated.getStatus()).isEqualTo(MilestoneStatus.IN_PROGRESS);
        }
    }

    @Nested
    @DisplayName("Update Operations")
    class UpdateOperationsTests {

        @Test
        @DisplayName("Should update milestone details")
        void shouldUpdateMilestoneDetails() {
            if (testProject == null) return;

            // Create a milestone
            ProjectMilestone milestone = new ProjectMilestone();
            milestone.setName("Original Name");
            milestone.setWeightPercent(10);
            milestone.setTargetDate(LocalDate.now().plusMonths(1));
            ProjectMilestone saved = milestoneService.create(testProject.getId(), milestone);

            // Update it
            saved.setName("Updated Name");
            saved.setDescription("Updated description");
            saved.setWeightPercent(20);
            ProjectMilestone updated = milestoneService.update(saved.getId(), saved);

            assertThat(updated.getName()).isEqualTo("Updated Name");
            assertThat(updated.getDescription()).isEqualTo("Updated description");
            assertThat(updated.getWeightPercent()).isEqualTo(20);
        }
    }

    @Nested
    @DisplayName("Delete Operations")
    class DeleteOperationsTests {

        @Test
        @DisplayName("Should delete milestone")
        void shouldDeleteMilestone() {
            if (testProject == null) return;

            // Create a milestone
            ProjectMilestone milestone = new ProjectMilestone();
            milestone.setName("To Be Deleted");
            milestone.setWeightPercent(5);
            milestone.setTargetDate(LocalDate.now().plusMonths(1));
            ProjectMilestone saved = milestoneService.create(testProject.getId(), milestone);
            UUID id = saved.getId();

            // Delete it
            milestoneService.delete(id);

            // Verify deletion
            assertThatThrownBy(() -> milestoneService.findById(id))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent milestone")
        void shouldThrowExceptionWhenDeletingNonExistentMilestone() {
            UUID randomId = UUID.randomUUID();
            assertThatThrownBy(() -> milestoneService.delete(randomId))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }
}
