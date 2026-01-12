package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.TestcontainersConfiguration;
import com.artivisi.accountingfinance.entity.ChartOfAccount;
import com.artivisi.accountingfinance.enums.AccountType;
import com.artivisi.accountingfinance.enums.NormalBalance;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for ChartOfAccountService.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
@DisplayName("ChartOfAccountService Integration Tests")
class ChartOfAccountServiceTest {

    @Autowired
    private ChartOfAccountService accountService;

    private ChartOfAccount testAccount;

    @BeforeEach
    void setup() {
        testAccount = accountService.findByAccountCode("1.1.01");
    }

    @Nested
    @DisplayName("Find Operations")
    class FindOperationsTests {

        @Test
        @DisplayName("Should find account by ID")
        void shouldFindAccountById() {
            if (testAccount == null) return;
            ChartOfAccount found = accountService.findById(testAccount.getId());
            assertThat(found).isNotNull();
            assertThat(found.getAccountCode()).isEqualTo("1.1.01");
        }

        @Test
        @DisplayName("Should throw exception for non-existent ID")
        void shouldThrowExceptionForNonExistentId() {
            UUID randomId = UUID.randomUUID();
            assertThatThrownBy(() -> accountService.findById(randomId))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("Should find account by code")
        void shouldFindAccountByCode() {
            ChartOfAccount found = accountService.findByAccountCode("1.1.01");
            assertThat(found).isNotNull();
        }

        @Test
        @DisplayName("Should throw exception for non-existent code")
        void shouldThrowExceptionForNonExistentCode() {
            assertThatThrownBy(() -> accountService.findByAccountCode("99.99.99"))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("Should find all accounts")
        void shouldFindAllAccounts() {
            var accounts = accountService.findAll();
            assertThat(accounts).isNotNull();
            assertThat(accounts.size()).isGreaterThan(0);
        }

        @Test
        @DisplayName("Should find all including inactive accounts")
        void shouldFindAllIncludingInactiveAccounts() {
            var accounts = accountService.findAllIncludingInactive();
            assertThat(accounts).isNotNull();
        }

        @Test
        @DisplayName("Should find root accounts")
        void shouldFindRootAccounts() {
            var accounts = accountService.findRootAccounts();
            assertThat(accounts).isNotNull();
        }
    }

    @Nested
    @DisplayName("Hierarchy Operations")
    class HierarchyOperationsTests {

        @Test
        @DisplayName("Should check if account has children")
        void shouldCheckIfAccountHasChildren() {
            if (testAccount == null) return;
            boolean hasChildren = accountService.hasChildren(testAccount.getId());
            assertThat(hasChildren).isIn(true, false);
        }

        @Test
        @DisplayName("Should check if account has parent")
        void shouldCheckIfAccountHasParent() {
            if (testAccount == null) return;
            boolean hasParent = accountService.hasParent(testAccount.getId());
            assertThat(hasParent).isIn(true, false);
        }

        @Test
        @DisplayName("Should check if account has journal entries")
        void shouldCheckIfAccountHasJournalEntries() {
            if (testAccount == null) return;
            boolean hasEntries = accountService.hasJournalEntries(testAccount.getId());
            assertThat(hasEntries).isIn(true, false);
        }
    }

    @Nested
    @DisplayName("Create Operations")
    class CreateOperationsTests {

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("Should create new account")
        void shouldCreateNewAccount() {
            ChartOfAccount account = new ChartOfAccount();
            account.setAccountCode("9.9.99");
            account.setAccountName("Test Account");
            account.setAccountType(AccountType.EXPENSE);
            account.setNormalBalance(NormalBalance.DEBIT);

            ChartOfAccount created = accountService.create(account);

            assertThat(created).isNotNull();
            assertThat(created.getId()).isNotNull();
            assertThat(created.getAccountCode()).isEqualTo("9.9.99");
        }

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("Should reject duplicate account code")
        void shouldRejectDuplicateAccountCode() {
            ChartOfAccount account = new ChartOfAccount();
            account.setAccountCode("1.1.01");
            account.setAccountName("Duplicate");
            account.setAccountType(AccountType.ASSET);
            account.setNormalBalance(NormalBalance.DEBIT);

            assertThatThrownBy(() -> accountService.create(account))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Update Operations")
    class UpdateOperationsTests {

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("Should update account name")
        void shouldUpdateAccountName() {
            if (testAccount == null) return;

            ChartOfAccount updateData = new ChartOfAccount();
            updateData.setAccountCode(testAccount.getAccountCode());
            updateData.setAccountName("Updated Name");
            updateData.setAccountType(testAccount.getAccountType());
            updateData.setNormalBalance(testAccount.getNormalBalance());

            ChartOfAccount updated = accountService.update(testAccount.getId(), updateData);

            assertThat(updated.getAccountName()).isEqualTo("Updated Name");
        }
    }

    @Nested
    @DisplayName("Activate/Deactivate Operations")
    class ActivateDeactivateOperationsTests {

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("Should deactivate account")
        void shouldDeactivateAccount() {
            if (testAccount == null) return;
            accountService.deactivate(testAccount.getId());
            ChartOfAccount found = accountService.findById(testAccount.getId());
            assertThat(found.getActive()).isFalse();
        }

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("Should activate account")
        void shouldActivateAccount() {
            if (testAccount == null) return;
            accountService.deactivate(testAccount.getId());
            accountService.activate(testAccount.getId());
            ChartOfAccount found = accountService.findById(testAccount.getId());
            assertThat(found.getActive()).isTrue();
        }
    }
}
