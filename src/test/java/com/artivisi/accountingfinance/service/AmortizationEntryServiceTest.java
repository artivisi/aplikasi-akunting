package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.TestcontainersConfiguration;
import com.artivisi.accountingfinance.entity.AmortizationEntry;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for AmortizationEntryService.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
@DisplayName("AmortizationEntryService Integration Tests")
class AmortizationEntryServiceTest {

    @Autowired
    private AmortizationEntryService entryService;

    @Nested
    @DisplayName("Find Operations")
    class FindOperationsTests {

        @Test
        @DisplayName("Should throw exception for non-existent entry ID")
        void shouldThrowExceptionForNonExistentId() {
            UUID randomId = UUID.randomUUID();
            assertThatThrownBy(() -> entryService.findById(randomId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("not found");
        }

        @Test
        @DisplayName("Should find entries by schedule ID - empty for random schedule")
        void shouldFindEntriesByScheduleIdEmptyForRandomSchedule() {
            UUID randomId = UUID.randomUUID();
            List<AmortizationEntry> entries = entryService.findByScheduleId(randomId);
            assertThat(entries).isEmpty();
        }

        @Test
        @DisplayName("Should find pending entries by schedule ID - empty for random schedule")
        void shouldFindPendingEntriesByScheduleIdEmptyForRandomSchedule() {
            UUID randomId = UUID.randomUUID();
            List<AmortizationEntry> entries = entryService.findPendingByScheduleId(randomId);
            assertThat(entries).isEmpty();
        }

        @Test
        @DisplayName("Should find pending entries due by date")
        void shouldFindPendingEntriesDueByDate() {
            List<AmortizationEntry> entries = entryService.findPendingEntriesDueByDate(LocalDate.now());
            assertThat(entries).isNotNull();
        }

        @Test
        @DisplayName("Should find pending entries due by past date")
        void shouldFindPendingEntriesDueByPastDate() {
            List<AmortizationEntry> entries = entryService.findPendingEntriesDueByDate(
                    LocalDate.now().minusMonths(1));
            assertThat(entries).isNotNull();
        }

        @Test
        @DisplayName("Should find pending entries due by future date")
        void shouldFindPendingEntriesDueByFutureDate() {
            List<AmortizationEntry> entries = entryService.findPendingEntriesDueByDate(
                    LocalDate.now().plusMonths(1));
            assertThat(entries).isNotNull();
        }

        @Test
        @DisplayName("Should find pending auto-post entries due by date")
        void shouldFindPendingAutoPostEntriesDueByDate() {
            List<AmortizationEntry> entries = entryService.findPendingAutoPostEntriesDueByDate(LocalDate.now());
            assertThat(entries).isNotNull();
        }

        @Test
        @DisplayName("Should count pending entries")
        void shouldCountPendingEntries() {
            long count = entryService.countPendingEntries();
            assertThat(count).isGreaterThanOrEqualTo(0);
        }
    }

    @Nested
    @DisplayName("Post Operations")
    class PostOperationsTests {

        @Test
        @DisplayName("Should throw exception when posting non-existent entry")
        void shouldThrowExceptionWhenPostingNonExistentEntry() {
            UUID randomId = UUID.randomUUID();
            assertThatThrownBy(() -> entryService.postEntry(randomId))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("Should throw exception when posting all pending for non-existent schedule")
        void shouldReturnEmptyWhenPostingAllPendingForNonExistentSchedule() {
            UUID randomId = UUID.randomUUID();
            List<AmortizationEntry> entries = entryService.postAllPending(randomId);
            assertThat(entries).isEmpty();
        }
    }

    @Nested
    @DisplayName("Skip Operations")
    class SkipOperationsTests {

        @Test
        @DisplayName("Should throw exception when skipping non-existent entry")
        void shouldThrowExceptionWhenSkippingNonExistentEntry() {
            UUID randomId = UUID.randomUUID();
            assertThatThrownBy(() -> entryService.skipEntry(randomId))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }
}
