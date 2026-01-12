package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.TestcontainersConfiguration;
import com.artivisi.accountingfinance.entity.AmortizationSchedule;
import com.artivisi.accountingfinance.enums.ScheduleStatus;
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

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for AmortizationScheduleService.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
@DisplayName("AmortizationScheduleService Integration Tests")
class AmortizationScheduleServiceTest {

    @Autowired
    private AmortizationScheduleService scheduleService;

    @Nested
    @DisplayName("Find Operations")
    class FindOperationsTests {

        @Test
        @DisplayName("Should find all schedules with pagination")
        void shouldFindAllSchedulesWithPagination() {
            Page<AmortizationSchedule> result = scheduleService.findAll(PageRequest.of(0, 10));
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should find schedules by status ACTIVE")
        void shouldFindSchedulesByStatusActive() {
            List<AmortizationSchedule> activeSchedules = scheduleService.findByStatus(ScheduleStatus.ACTIVE);
            assertThat(activeSchedules).isNotNull();
        }

        @Test
        @DisplayName("Should find schedules by status COMPLETED")
        void shouldFindSchedulesByStatusCompleted() {
            List<AmortizationSchedule> completedSchedules = scheduleService.findByStatus(ScheduleStatus.COMPLETED);
            assertThat(completedSchedules).isNotNull();
        }

        @Test
        @DisplayName("Should find schedules by status CANCELLED")
        void shouldFindSchedulesByStatusCancelled() {
            List<AmortizationSchedule> cancelledSchedules = scheduleService.findByStatus(ScheduleStatus.CANCELLED);
            assertThat(cancelledSchedules).isNotNull();
        }

        @Test
        @DisplayName("Should find schedules by filters with null status")
        void shouldFindSchedulesByFiltersWithNullStatus() {
            Page<AmortizationSchedule> result = scheduleService.findByFilters(
                    null, null, null, PageRequest.of(0, 10));
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should find schedules by filters with status")
        void shouldFindSchedulesByFiltersWithStatus() {
            Page<AmortizationSchedule> result = scheduleService.findByFilters(
                    ScheduleStatus.ACTIVE, null, null, PageRequest.of(0, 10));
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should find schedules by filters with search")
        void shouldFindSchedulesByFiltersWithSearch() {
            Page<AmortizationSchedule> result = scheduleService.findByFilters(
                    null, null, "test", PageRequest.of(0, 10));
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should find schedules by filters with empty search")
        void shouldFindSchedulesByFiltersWithEmptySearch() {
            Page<AmortizationSchedule> result = scheduleService.findByFilters(
                    null, null, "", PageRequest.of(0, 10));
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should throw exception for non-existent ID")
        void shouldThrowExceptionForNonExistentId() {
            UUID randomId = UUID.randomUUID();
            assertThatThrownBy(() -> scheduleService.findById(randomId))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("Should throw exception for non-existent code")
        void shouldThrowExceptionForNonExistentCode() {
            assertThatThrownBy(() -> scheduleService.findByCode("NON-EXISTENT-CODE"))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("Should find active schedules for today")
        void shouldFindActiveSchedulesForToday() {
            List<AmortizationSchedule> schedules = scheduleService.findActiveSchedulesForDate(LocalDate.now());
            assertThat(schedules).isNotNull();
        }

        @Test
        @DisplayName("Should find active schedules for past date")
        void shouldFindActiveSchedulesForPastDate() {
            List<AmortizationSchedule> schedules = scheduleService.findActiveSchedulesForDate(
                    LocalDate.now().minusMonths(1));
            assertThat(schedules).isNotNull();
        }

        @Test
        @DisplayName("Should find active schedules for future date")
        void shouldFindActiveSchedulesForFutureDate() {
            List<AmortizationSchedule> schedules = scheduleService.findActiveSchedulesForDate(
                    LocalDate.now().plusMonths(1));
            assertThat(schedules).isNotNull();
        }
    }
}
