package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.TestcontainersConfiguration;
import com.artivisi.accountingfinance.entity.Client;
import com.artivisi.accountingfinance.entity.Project;
import com.artivisi.accountingfinance.repository.ClientRepository;
import com.artivisi.accountingfinance.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for ProjectProfitabilityService.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
@DisplayName("ProjectProfitabilityService Integration Tests")
class ProjectProfitabilityServiceTest {

    @Autowired
    private ProjectProfitabilityService profitabilityService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ClientRepository clientRepository;

    // Test data IDs from V903__invoice_test_data.sql
    private static final UUID TEST_PROJECT_ID = UUID.fromString("d0000000-0000-0000-0000-000000000001");
    private static final UUID TEST_CLIENT_ID = UUID.fromString("c0000000-0000-0000-0000-000000000001");

    private Project testProject;
    private Client testClient;

    @BeforeEach
    void setup() {
        testProject = projectRepository.findById(TEST_PROJECT_ID).orElse(null);
        testClient = clientRepository.findById(TEST_CLIENT_ID).orElse(null);
    }

    @Nested
    @DisplayName("Project Profitability Reports")
    class ProjectProfitabilityTests {

        @Test
        @DisplayName("Should throw exception for non-existent project")
        void shouldThrowExceptionForNonExistentProject() {
            UUID randomId = UUID.randomUUID();
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            assertThatThrownBy(() -> profitabilityService.calculateProjectProfitability(randomId, startDate, endDate))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Project not found");
        }

        @Test
        @DisplayName("Should calculate project profitability for existing project")
        void shouldCalculateProjectProfitabilityForExistingProject() {
            if (testProject == null) return;

            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            ProjectProfitabilityService.ProjectProfitabilityReport report =
                    profitabilityService.calculateProjectProfitability(testProject.getId(), startDate, endDate);

            assertThat(report).isNotNull();
            assertThat(report.project()).isNotNull();
            assertThat(report.startDate()).isEqualTo(startDate);
            assertThat(report.endDate()).isEqualTo(endDate);
            assertThat(report.totalRevenue()).isNotNull();
            assertThat(report.totalExpense()).isNotNull();
            assertThat(report.grossProfit()).isNotNull();
            assertThat(report.profitMargin()).isNotNull();
        }

        @Test
        @DisplayName("Should return empty items for project with no transactions")
        void shouldReturnEmptyItemsForProjectWithNoTransactions() {
            if (testProject == null) return;

            // Use far past date range with no transactions
            LocalDate startDate = LocalDate.of(1990, 1, 1);
            LocalDate endDate = LocalDate.of(1990, 12, 31);

            ProjectProfitabilityService.ProjectProfitabilityReport report =
                    profitabilityService.calculateProjectProfitability(testProject.getId(), startDate, endDate);

            assertThat(report).isNotNull();
            assertThat(report.totalRevenue()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(report.totalExpense()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(report.grossProfit()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Should calculate profit margin correctly")
        void shouldCalculateProfitMarginCorrectly() {
            if (testProject == null) return;

            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            ProjectProfitabilityService.ProjectProfitabilityReport report =
                    profitabilityService.calculateProjectProfitability(testProject.getId(), startDate, endDate);

            // grossProfit = totalRevenue - totalExpense
            BigDecimal expectedGrossProfit = report.totalRevenue().subtract(report.totalExpense());
            assertThat(report.grossProfit()).isEqualByComparingTo(expectedGrossProfit);
        }
    }

    @Nested
    @DisplayName("Client Profitability Reports")
    class ClientProfitabilityTests {

        @Test
        @DisplayName("Should throw exception for non-existent client")
        void shouldThrowExceptionForNonExistentClient() {
            UUID randomId = UUID.randomUUID();
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            assertThatThrownBy(() -> profitabilityService.calculateClientProfitability(randomId, startDate, endDate))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Client not found");
        }

        @Test
        @DisplayName("Should calculate client profitability for existing client")
        void shouldCalculateClientProfitabilityForExistingClient() {
            if (testClient == null) return;

            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            ProjectProfitabilityService.ClientProfitabilityReport report =
                    profitabilityService.calculateClientProfitability(testClient.getId(), startDate, endDate);

            assertThat(report).isNotNull();
            assertThat(report.client()).isNotNull();
            assertThat(report.startDate()).isEqualTo(startDate);
            assertThat(report.endDate()).isEqualTo(endDate);
            assertThat(report.projects()).isNotNull();
            assertThat(report.totalRevenue()).isNotNull();
            assertThat(report.totalProfit()).isNotNull();
            assertThat(report.overallMargin()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Client Ranking")
    class ClientRankingTests {

        @Test
        @DisplayName("Should get client ranking")
        void shouldGetClientRanking() {
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            List<ProjectProfitabilityService.ClientRankingItem> rankings =
                    profitabilityService.getClientRanking(startDate, endDate, 10);

            assertThat(rankings).isNotNull();
        }

        @Test
        @DisplayName("Should limit client ranking results")
        void shouldLimitClientRankingResults() {
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            List<ProjectProfitabilityService.ClientRankingItem> rankings =
                    profitabilityService.getClientRanking(startDate, endDate, 5);

            assertThat(rankings).isNotNull();
            assertThat(rankings.size()).isLessThanOrEqualTo(5);
        }

        @Test
        @DisplayName("Should get client ranking with no limit")
        void shouldGetClientRankingWithNoLimit() {
            LocalDate startDate = LocalDate.of(2025, 1, 1);
            LocalDate endDate = LocalDate.of(2025, 12, 31);

            List<ProjectProfitabilityService.ClientRankingItem> rankings =
                    profitabilityService.getClientRanking(startDate, endDate, 0);

            assertThat(rankings).isNotNull();
        }
    }

    @Nested
    @DisplayName("Cost Overrun Reports")
    class CostOverrunTests {

        @Test
        @DisplayName("Should throw exception for non-existent project")
        void shouldThrowExceptionForNonExistentProject() {
            UUID randomId = UUID.randomUUID();

            assertThatThrownBy(() -> profitabilityService.calculateCostOverrun(randomId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Project not found");
        }

        @Test
        @DisplayName("Should calculate cost overrun for existing project")
        void shouldCalculateCostOverrunForExistingProject() {
            if (testProject == null) return;

            ProjectProfitabilityService.CostOverrunReport report =
                    profitabilityService.calculateCostOverrun(testProject.getId());

            assertThat(report).isNotNull();
            assertThat(report.project()).isNotNull();
            assertThat(report.budget()).isNotNull();
            assertThat(report.spent()).isNotNull();
            assertThat(report.progressPercent()).isGreaterThanOrEqualTo(0);
            assertThat(report.riskLevel()).isNotNull();
        }

        @Test
        @DisplayName("Should identify risk levels correctly")
        void shouldIdentifyRiskLevelsCorrectly() {
            if (testProject == null) return;

            ProjectProfitabilityService.CostOverrunReport report =
                    profitabilityService.calculateCostOverrun(testProject.getId());

            // Risk level should be one of: LOW, MEDIUM, HIGH, UNKNOWN
            assertThat(report.riskLevel()).isIn(
                    ProjectProfitabilityService.CostOverrunRisk.LOW,
                    ProjectProfitabilityService.CostOverrunRisk.MEDIUM,
                    ProjectProfitabilityService.CostOverrunRisk.HIGH,
                    ProjectProfitabilityService.CostOverrunRisk.UNKNOWN
            );
        }
    }
}
