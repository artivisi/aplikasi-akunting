package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.TestcontainersConfiguration;
import com.artivisi.accountingfinance.entity.Client;
import com.artivisi.accountingfinance.entity.Project;
import com.artivisi.accountingfinance.enums.ProjectStatus;
import com.artivisi.accountingfinance.repository.ClientRepository;
import com.artivisi.accountingfinance.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for ProjectService.
 * Tests actual database queries and business logic.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
@DisplayName("ProjectService Integration Tests")
class ProjectServiceTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Nested
    @DisplayName("Find Operations")
    class FindOperationsTests {

        @Test
        @DisplayName("findById should return project with correct data")
        void findByIdShouldReturnProject() {
            Project project = createTestProject();

            Project found = projectService.findById(project.getId());

            assertThat(found).isNotNull();
            assertThat(found.getId()).isEqualTo(project.getId());
            assertThat(found.getCode()).isEqualTo(project.getCode());
            assertThat(found.getName()).isEqualTo(project.getName());
        }

        @Test
        @DisplayName("findById should throw EntityNotFoundException for invalid ID")
        void findByIdShouldThrowForInvalidId() {
            UUID invalidId = UUID.randomUUID();

            assertThatThrownBy(() -> projectService.findById(invalidId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Project not found with id");
        }

        @Test
        @DisplayName("findByCode should return correct project")
        void findByCodeShouldReturnProject() {
            Project project = createTestProject();

            Project found = projectService.findByCode(project.getCode());

            assertThat(found).isNotNull();
            assertThat(found.getCode()).isEqualTo(project.getCode());
        }

        @Test
        @DisplayName("findByCode should throw for invalid code")
        void findByCodeShouldThrowForInvalidCode() {
            assertThatThrownBy(() -> projectService.findByCode("INVALID-CODE"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Project not found with code");
        }

        @Test
        @DisplayName("findAll should return paginated results")
        void findAllShouldReturnPaginatedResults() {
            createTestProject();
            createTestProject();

            Page<Project> page = projectService.findAll(PageRequest.of(0, 10));

            assertThat(page.getContent()).isNotEmpty();
            assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(2);
        }

        @Test
        @DisplayName("findByFilters should filter by status")
        void findByFiltersShouldFilterByStatus() {
            createTestProject(); // ACTIVE by default
            Project completed = createTestProject();
            projectService.complete(completed.getId());

            Page<Project> activePage = projectService.findByFilters(
                ProjectStatus.ACTIVE, null, null, PageRequest.of(0, 10));

            assertThat(activePage.getContent()).isNotEmpty().allMatch(p -> p.getStatus() == ProjectStatus.ACTIVE);
        }

        @Test
        @DisplayName("findByFilters should filter by client")
        void findByFiltersShouldFilterByClient() {
            Client client = createTestClient();
            createTestProjectWithClient(client);
            createTestProject(); // Without client

            Page<Project> page = projectService.findByFilters(
                null, client.getId(), null, PageRequest.of(0, 10));

            assertThat(page.getContent()).isNotEmpty().allMatch(p ->
                p.getClient() != null && p.getClient().getId().equals(client.getId()));
        }

        @Test
        @DisplayName("findByFilters should search by name")
        void findByFiltersShouldSearchByName() {
            Project project = buildTestProject();
            project.setName("Unique Project Name XYZ");
            projectService.create(project, null);

            Page<Project> page = projectService.findByFilters(
                null, null, "Unique", PageRequest.of(0, 10));

            assertThat(page.getContent()).isNotEmpty();
            assertThat(page.getContent()).anyMatch(p -> p.getName().contains("Unique"));
        }

        @Test
        @DisplayName("findByClientId should return projects for client")
        void findByClientIdShouldReturnProjectsForClient() {
            Client client = createTestClient();
            createTestProjectWithClient(client);
            createTestProjectWithClient(client);

            List<Project> projects = projectService.findByClientId(client.getId());

            assertThat(projects).hasSizeGreaterThanOrEqualTo(2).allMatch(p -> p.getClient().getId().equals(client.getId()));
        }

        @Test
        @DisplayName("findActiveProjects should return only active projects")
        void findActiveProjectsShouldReturnOnlyActive() {
            createTestProject(); // ACTIVE
            Project completed = createTestProject();
            projectService.complete(completed.getId());
            Project archived = createTestProject();
            projectService.archive(archived.getId());

            List<Project> activeProjects = projectService.findActiveProjects();

            assertThat(activeProjects).isNotEmpty().allMatch(p -> p.getStatus() == ProjectStatus.ACTIVE);
        }
    }

    @Nested
    @DisplayName("Create Project")
    class CreateProjectTests {

        @Test
        @DisplayName("create should save project with ACTIVE status")
        void createShouldSaveWithActiveStatus() {
            Project project = buildTestProject();

            Project saved = projectService.create(project, null);

            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getStatus()).isEqualTo(ProjectStatus.ACTIVE);
        }

        @Test
        @DisplayName("create should persist all fields correctly")
        void createShouldPersistAllFields() {
            Project project = buildTestProject();
            project.setDescription("Test description");
            project.setContractValue(new BigDecimal("100000000"));
            project.setBudgetAmount(new BigDecimal("80000000"));
            project.setStartDate(LocalDate.now());
            project.setEndDate(LocalDate.now().plusMonths(6));

            Project saved = projectService.create(project, null);

            Project retrieved = projectRepository.findById(saved.getId()).orElseThrow();
            assertThat(retrieved.getDescription()).isEqualTo("Test description");
            assertThat(retrieved.getContractValue()).isEqualByComparingTo("100000000");
            assertThat(retrieved.getBudgetAmount()).isEqualByComparingTo("80000000");
            assertThat(retrieved.getStartDate()).isEqualTo(LocalDate.now());
        }

        @Test
        @DisplayName("create should associate project with client")
        void createShouldAssociateWithClient() {
            Client client = createTestClient();
            Project project = buildTestProject();

            Project saved = projectService.create(project, client.getId());

            assertThat(saved.getClient()).isNotNull();
            assertThat(saved.getClient().getId()).isEqualTo(client.getId());
        }

        @Test
        @DisplayName("create should throw for duplicate code")
        void createShouldThrowForDuplicateCode() {
            Project first = createTestProject();

            Project second = buildTestProject();
            second.setCode(first.getCode());

            assertThatThrownBy(() -> projectService.create(second, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Project code already exists");
        }

        @Test
        @DisplayName("create should throw for non-existent client")
        void createShouldThrowForNonExistentClient() {
            Project project = buildTestProject();
            UUID invalidClientId = UUID.randomUUID();

            assertThatThrownBy(() -> projectService.create(project, invalidClientId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Client not found");
        }
    }

    @Nested
    @DisplayName("Update Project")
    class UpdateProjectTests {

        @Test
        @DisplayName("update should modify project fields")
        void updateShouldModifyFields() {
            Project project = createTestProject();

            Project updateData = buildUpdateData(project);
            updateData.setName("Updated Name");
            updateData.setDescription("Updated description");
            updateData.setContractValue(new BigDecimal("200000000"));

            Project updated = projectService.update(project.getId(), updateData, null);

            assertThat(updated.getName()).isEqualTo("Updated Name");
            assertThat(updated.getDescription()).isEqualTo("Updated description");
            assertThat(updated.getContractValue()).isEqualByComparingTo("200000000");
        }

        @Test
        @DisplayName("update should allow changing code if not duplicate")
        void updateShouldAllowChangingCode() {
            Project project = createTestProject();
            String newCode = "NEW-" + System.nanoTime();

            Project updateData = buildUpdateData(project);
            updateData.setCode(newCode);

            Project updated = projectService.update(project.getId(), updateData, null);

            assertThat(updated.getCode()).isEqualTo(newCode);
        }

        @Test
        @DisplayName("update should throw for duplicate code")
        void updateShouldThrowForDuplicateCode() {
            Project first = createTestProject();
            Project second = createTestProject();

            Project updateData = buildUpdateData(second);
            updateData.setCode(first.getCode());

            assertThatThrownBy(() -> projectService.update(second.getId(), updateData, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Project code already exists");
        }

        @Test
        @DisplayName("update should change client")
        void updateShouldChangeClient() {
            Client client1 = createTestClient();
            Client client2 = createTestClient();
            Project project = createTestProjectWithClient(client1);

            Project updateData = buildUpdateData(project);
            Project updated = projectService.update(project.getId(), updateData, client2.getId());

            assertThat(updated.getClient().getId()).isEqualTo(client2.getId());
        }

        @Test
        @DisplayName("update should remove client when null")
        void updateShouldRemoveClientWhenNull() {
            Client client = createTestClient();
            Project project = createTestProjectWithClient(client);

            Project updateData = buildUpdateData(project);
            Project updated = projectService.update(project.getId(), updateData, null);

            assertThat(updated.getClient()).isNull();
        }

        @Test
        @DisplayName("update should throw for non-existent project")
        void updateShouldThrowForNonExistentProject() {
            UUID invalidId = UUID.randomUUID();
            Project updateData = buildTestProject();

            assertThatThrownBy(() -> projectService.update(invalidId, updateData, null))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Project not found");
        }
    }

    @Nested
    @DisplayName("Status Operations")
    class StatusOperationsTests {

        @Test
        @DisplayName("updateStatus should change project status")
        void updateStatusShouldChangeStatus() {
            Project project = createTestProject();
            assertThat(project.getStatus()).isEqualTo(ProjectStatus.ACTIVE);

            projectService.updateStatus(project.getId(), ProjectStatus.COMPLETED);

            Project updated = projectRepository.findById(project.getId()).orElseThrow();
            assertThat(updated.getStatus()).isEqualTo(ProjectStatus.COMPLETED);
        }

        @Test
        @DisplayName("archive should set status to ARCHIVED")
        void archiveShouldSetStatusToArchived() {
            Project project = createTestProject();

            projectService.archive(project.getId());

            Project archived = projectRepository.findById(project.getId()).orElseThrow();
            assertThat(archived.getStatus()).isEqualTo(ProjectStatus.ARCHIVED);
            assertThat(archived.isArchived()).isTrue();
        }

        @Test
        @DisplayName("complete should set status to COMPLETED")
        void completeShouldSetStatusToCompleted() {
            Project project = createTestProject();

            projectService.complete(project.getId());

            Project completed = projectRepository.findById(project.getId()).orElseThrow();
            assertThat(completed.getStatus()).isEqualTo(ProjectStatus.COMPLETED);
            assertThat(completed.isCompleted()).isTrue();
        }

        @Test
        @DisplayName("reactivate should set status to ACTIVE")
        void reactivateShouldSetStatusToActive() {
            Project project = createTestProject();
            projectService.archive(project.getId());

            projectService.reactivate(project.getId());

            Project reactivated = projectRepository.findById(project.getId()).orElseThrow();
            assertThat(reactivated.getStatus()).isEqualTo(ProjectStatus.ACTIVE);
            assertThat(reactivated.isActive()).isTrue();
        }

        @Test
        @DisplayName("updateStatus should throw for non-existent project")
        void updateStatusShouldThrowForNonExistent() {
            UUID invalidId = UUID.randomUUID();

            assertThatThrownBy(() -> projectService.updateStatus(invalidId, ProjectStatus.COMPLETED))
                .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Count Operations")
    class CountOperationsTests {

        @Test
        @DisplayName("countByStatus should return correct count")
        void countByStatusShouldReturnCorrectCount() {
            createTestProject(); // ACTIVE
            createTestProject(); // ACTIVE
            Project completed = createTestProject();
            projectService.complete(completed.getId());

            long activeCount = projectService.countByStatus(ProjectStatus.ACTIVE);
            long completedCount = projectService.countByStatus(ProjectStatus.COMPLETED);

            assertThat(activeCount).isGreaterThanOrEqualTo(2);
            assertThat(completedCount).isGreaterThanOrEqualTo(1);
        }

        @Test
        @DisplayName("countActiveProjects should return correct count")
        void countActiveProjectsShouldReturnCorrectCount() {
            createTestProject(); // ACTIVE
            createTestProject(); // ACTIVE
            Project archived = createTestProject();
            projectService.archive(archived.getId());

            long count = projectService.countActiveProjects();

            assertThat(count).isGreaterThanOrEqualTo(2);
        }
    }

    // Helper methods

    private Project createTestProject() {
        Project project = buildTestProject();
        return projectService.create(project, null);
    }

    private Project createTestProjectWithClient(Client client) {
        Project project = buildTestProject();
        return projectService.create(project, client.getId());
    }

    private Client createTestClient() {
        Client client = new Client();
        client.setCode("CLI-" + System.nanoTime());
        client.setName("Test Client " + System.currentTimeMillis());
        return clientRepository.save(client);
    }

    private Project buildTestProject() {
        Project project = new Project();
        project.setCode("PRJ-" + System.nanoTime());
        project.setName("Test Project " + System.currentTimeMillis());
        project.setContractValue(new BigDecimal("50000000"));
        project.setBudgetAmount(new BigDecimal("40000000"));
        project.setStartDate(LocalDate.now());
        project.setEndDate(LocalDate.now().plusMonths(3));
        return project;
    }

    private Project buildUpdateData(Project existing) {
        Project updateData = new Project();
        updateData.setCode(existing.getCode());
        updateData.setName(existing.getName());
        updateData.setDescription(existing.getDescription());
        updateData.setContractValue(existing.getContractValue());
        updateData.setBudgetAmount(existing.getBudgetAmount());
        updateData.setStartDate(existing.getStartDate());
        updateData.setEndDate(existing.getEndDate());
        return updateData;
    }
}
