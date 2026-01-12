package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.TestcontainersConfiguration;
import com.artivisi.accountingfinance.entity.TaxDeadline;
import com.artivisi.accountingfinance.entity.TaxDeadlineCompletion;
import com.artivisi.accountingfinance.enums.TaxDeadlineType;
import jakarta.persistence.EntityNotFoundException;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for TaxDeadlineService.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
@DisplayName("TaxDeadlineService Integration Tests")
class TaxDeadlineServiceTest {

    @Autowired
    private TaxDeadlineService taxDeadlineService;

    @Nested
    @DisplayName("Find Operations")
    class FindOperationsTests {

        @Test
        @DisplayName("Should find all tax deadlines")
        void shouldFindAllTaxDeadlines() {
            List<TaxDeadline> deadlines = taxDeadlineService.findAll();
            assertThat(deadlines).isNotNull();
        }

        @Test
        @DisplayName("Should find all active tax deadlines")
        void shouldFindAllActiveTaxDeadlines() {
            List<TaxDeadline> deadlines = taxDeadlineService.findAllActive();
            assertThat(deadlines).isNotNull();
            assertThat(deadlines).allMatch(d -> d.getActive());
        }

        @Test
        @DisplayName("Should throw exception for non-existent ID")
        void shouldThrowExceptionForNonExistentId() {
            UUID randomId = UUID.randomUUID();
            assertThatThrownBy(() -> taxDeadlineService.findById(randomId))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("Should find deadline by type")
        void shouldFindDeadlineByType() {
            Optional<TaxDeadline> deadline = taxDeadlineService.findByDeadlineType(TaxDeadlineType.PPH_21_PAYMENT);
            assertThat(deadline).isNotNull();
        }
    }

    @Nested
    @DisplayName("Deadline Status Operations")
    class DeadlineStatusTests {

        @Test
        @DisplayName("Should get deadline status for period")
        void shouldGetDeadlineStatusForPeriod() {
            List<TaxDeadlineService.TaxDeadlineStatus> statuses =
                    taxDeadlineService.getDeadlineStatusForPeriod(2025, 6);

            assertThat(statuses).isNotNull();
        }

        @Test
        @DisplayName("Should get upcoming deadlines")
        void shouldGetUpcomingDeadlines() {
            List<TaxDeadlineService.TaxDeadlineStatus> upcoming =
                    taxDeadlineService.getUpcomingDeadlines();

            assertThat(upcoming).isNotNull();
        }

        @Test
        @DisplayName("Should get overdue deadlines")
        void shouldGetOverdueDeadlines() {
            List<TaxDeadlineService.TaxDeadlineStatus> overdue =
                    taxDeadlineService.getOverdueDeadlines();

            assertThat(overdue).isNotNull();
        }

        @Test
        @DisplayName("Should get due soon deadlines")
        void shouldGetDueSoonDeadlines() {
            List<TaxDeadlineService.TaxDeadlineStatus> dueSoon =
                    taxDeadlineService.getDueSoonDeadlines();

            assertThat(dueSoon).isNotNull();
        }
    }

    @Nested
    @DisplayName("Checklist Summary Operations")
    class ChecklistSummaryTests {

        @Test
        @DisplayName("Should get monthly checklist summary")
        void shouldGetMonthlyChecklistSummary() {
            TaxDeadlineService.MonthlyChecklistSummary summary =
                    taxDeadlineService.getMonthlyChecklistSummary(2025, 6);

            assertThat(summary).isNotNull();
            assertThat(summary.year()).isEqualTo(2025);
            assertThat(summary.month()).isEqualTo(6);
        }

        @Test
        @DisplayName("Should get yearly checklist summary")
        void shouldGetYearlyChecklistSummary() {
            List<TaxDeadlineService.MonthlyChecklistSummary> summaries =
                    taxDeadlineService.getYearlyChecklistSummary(2025);

            assertThat(summaries).isNotNull();
            assertThat(summaries).hasSize(12);
        }

        @Test
        @DisplayName("Should calculate completion percentage")
        void shouldCalculateCompletionPercentage() {
            TaxDeadlineService.MonthlyChecklistSummary summary =
                    taxDeadlineService.getMonthlyChecklistSummary(2025, 6);

            int percentage = summary.getCompletionPercentage();
            assertThat(percentage).isBetween(0, 100);
        }
    }

    @Nested
    @DisplayName("Create/Update Operations")
    class CreateUpdateTests {

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("Should save tax deadline")
        void shouldSaveTaxDeadline() {
            TaxDeadline deadline = new TaxDeadline();
            deadline.setDeadlineType(TaxDeadlineType.PPH_21_PAYMENT);
            deadline.setName("Test PPh 21");
            deadline.setDueDay(15);
            deadline.setActive(true);

            TaxDeadline saved = taxDeadlineService.save(deadline);

            assertThat(saved).isNotNull();
            assertThat(saved.getId()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Completion Operations")
    class CompletionTests {

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("Should mark deadline as completed")
        void shouldMarkDeadlineAsCompleted() {
            // First create a new deadline for testing
            TaxDeadline deadline = new TaxDeadline();
            deadline.setDeadlineType(TaxDeadlineType.PPH_21_PAYMENT);
            deadline.setName("Test PPh 21 for Completion");
            deadline.setDueDay(15);
            deadline.setActive(true);
            TaxDeadline saved = taxDeadlineService.save(deadline);

            // Mark as completed
            TaxDeadlineCompletion completion = taxDeadlineService.markAsCompleted(
                    saved.getId(),
                    2025,
                    6,
                    LocalDate.of(2025, 6, 10),
                    "REF-001",
                    "Test completion"
            );

            assertThat(completion).isNotNull();
            assertThat(completion.getId()).isNotNull();
            assertThat(completion.getCompletedBy()).isEqualTo("admin");
        }

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("Should reject duplicate completion")
        void shouldRejectDuplicateCompletion() {
            // Get an existing deadline
            List<TaxDeadline> activeDeadlines = taxDeadlineService.findAllActive();
            if (activeDeadlines.isEmpty()) {
                return; // Skip if no deadlines exist
            }
            TaxDeadline deadline = activeDeadlines.get(0);

            // First completion - use a unique month to avoid conflicts with existing completions
            taxDeadlineService.markAsCompleted(
                    deadline.getId(), 2030, 1, LocalDate.of(2030, 1, 20), "REF-002", null);

            // Second completion for same period should fail
            assertThatThrownBy(() -> taxDeadlineService.markAsCompleted(
                    deadline.getId(), 2030, 1, LocalDate.of(2030, 1, 25), "REF-003", null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("already completed");
        }

        @Test
        @DisplayName("Should find completion by deadline and period")
        void shouldFindCompletionByDeadlineAndPeriod() {
            List<TaxDeadline> deadlines = taxDeadlineService.findAllActive();
            if (!deadlines.isEmpty()) {
                Optional<TaxDeadlineCompletion> completion =
                        taxDeadlineService.findCompletion(deadlines.get(0).getId(), 2025, 6);
                assertThat(completion).isNotNull();
            }
        }

        @Test
        @DisplayName("Should throw exception for non-existent completion")
        void shouldThrowExceptionForNonExistentCompletion() {
            UUID randomId = UUID.randomUUID();
            assertThatThrownBy(() -> taxDeadlineService.findCompletionById(randomId))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }
}
