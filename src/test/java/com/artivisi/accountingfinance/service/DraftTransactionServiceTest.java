package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.TestcontainersConfiguration;
import com.artivisi.accountingfinance.entity.DraftTransaction;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for DraftTransactionService.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
@DisplayName("DraftTransactionService Integration Tests")
class DraftTransactionServiceTest {

    @Autowired
    private DraftTransactionService draftTransactionService;

    @Nested
    @DisplayName("Find Operations")
    class FindOperationsTests {

        @Test
        @DisplayName("Should throw exception for non-existent draft ID")
        void shouldThrowExceptionForNonExistentId() {
            UUID randomId = UUID.randomUUID();
            assertThatThrownBy(() -> draftTransactionService.findById(randomId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Draft not found");
        }

        @Test
        @DisplayName("Should find pending drafts")
        void shouldFindPendingDrafts() {
            Page<DraftTransaction> result = draftTransactionService.findPending(PageRequest.of(0, 10));
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should find drafts by filters with null status")
        void shouldFindDraftsByFiltersWithNullStatus() {
            Page<DraftTransaction> result = draftTransactionService.findByFilters(
                    null, PageRequest.of(0, 10));
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should find drafts by filters with PENDING status")
        void shouldFindDraftsByFiltersWithPendingStatus() {
            Page<DraftTransaction> result = draftTransactionService.findByFilters(
                    DraftTransaction.Status.PENDING, PageRequest.of(0, 10));
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should find drafts by filters with APPROVED status")
        void shouldFindDraftsByFiltersWithApprovedStatus() {
            Page<DraftTransaction> result = draftTransactionService.findByFilters(
                    DraftTransaction.Status.APPROVED, PageRequest.of(0, 10));
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should find drafts by filters with REJECTED status")
        void shouldFindDraftsByFiltersWithRejectedStatus() {
            Page<DraftTransaction> result = draftTransactionService.findByFilters(
                    DraftTransaction.Status.REJECTED, PageRequest.of(0, 10));
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should find drafts by user")
        void shouldFindDraftsByUser() {
            Page<DraftTransaction> result = draftTransactionService.findByUser("admin", PageRequest.of(0, 10));
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should find pending drafts by user")
        void shouldFindPendingDraftsByUser() {
            Page<DraftTransaction> result = draftTransactionService.findPendingByUser("admin", PageRequest.of(0, 10));
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should count pending drafts")
        void shouldCountPendingDrafts() {
            long count = draftTransactionService.countPending();
            assertThat(count).isGreaterThanOrEqualTo(0);
        }

        @Test
        @DisplayName("Should count pending drafts by user")
        void shouldCountPendingDraftsByUser() {
            long count = draftTransactionService.countPendingByUser("admin");
            assertThat(count).isGreaterThanOrEqualTo(0);
        }
    }

    @Nested
    @DisplayName("Delete Operations")
    class DeleteOperationsTests {

        @Test
        @DisplayName("Should throw exception when deleting non-existent draft")
        void shouldThrowExceptionWhenDeletingNonExistentDraft() {
            UUID randomId = UUID.randomUUID();
            assertThatThrownBy(() -> draftTransactionService.delete(randomId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Draft not found");
        }
    }

    @Nested
    @DisplayName("Approve/Reject Operations")
    class ApproveRejectTests {

        @Test
        @DisplayName("Should throw exception when approving non-existent draft")
        void shouldThrowExceptionWhenApprovingNonExistentDraft() {
            UUID randomId = UUID.randomUUID();
            assertThatThrownBy(() -> draftTransactionService.approve(
                    randomId, UUID.randomUUID(), "test", null, "admin"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Draft not found");
        }

        @Test
        @DisplayName("Should throw exception when rejecting non-existent draft")
        void shouldThrowExceptionWhenRejectingNonExistentDraft() {
            UUID randomId = UUID.randomUUID();
            assertThatThrownBy(() -> draftTransactionService.reject(randomId, "test reason", "admin"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Draft not found");
        }
    }
}
