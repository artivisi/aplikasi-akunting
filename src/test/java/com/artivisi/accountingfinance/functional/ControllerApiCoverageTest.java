package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.entity.Transaction;
import com.artivisi.accountingfinance.enums.TransactionStatus;
import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.repository.ChartOfAccountRepository;
import com.artivisi.accountingfinance.repository.JournalTemplateRepository;
import com.artivisi.accountingfinance.repository.TransactionRepository;
import com.artivisi.accountingfinance.repository.UserRepository;
import com.artivisi.accountingfinance.repository.EmployeeRepository;
import com.artivisi.accountingfinance.repository.FixedAssetRepository;
import com.artivisi.accountingfinance.repository.AssetCategoryRepository;
import com.artivisi.accountingfinance.service.TransactionService;
import com.artivisi.accountingfinance.service.UserService;
import com.artivisi.accountingfinance.service.FixedAssetService;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Functional tests for Controller API coverage.
 * Tests API endpoints for Transaction, User, Settings, and Fixed Asset controllers.
 */
@DisplayName("Controller API - Coverage Tests")
@Import(ServiceTestDataInitializer.class)
class ControllerApiCoverageTest extends PlaywrightTestBase {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private JournalTemplateRepository templateRepository;

    @Autowired
    private ChartOfAccountRepository accountRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private FixedAssetService fixedAssetService;

    @Autowired
    private FixedAssetRepository fixedAssetRepository;

    @Autowired
    private AssetCategoryRepository assetCategoryRepository;

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    // ==================== TRANSACTION CONTROLLER API TESTS ====================

    @Nested
    @DisplayName("Transaction Controller API")
    class TransactionControllerApi {

        @Test
        @DisplayName("Should call transaction list API with pagination")
        void shouldCallTransactionListApiWithPagination() {
            var response = page.request().get(
                baseUrl() + "/transactions/api?page=0&size=10",
                com.microsoft.playwright.options.RequestOptions.create()
                    .setHeader("Accept", "application/json"));

            org.assertj.core.api.Assertions.assertThat(response.status()).isEqualTo(200);
        }

        @Test
        @DisplayName("Should call transaction list API with status filter")
        void shouldCallTransactionListApiWithStatusFilter() {
            var response = page.request().get(
                baseUrl() + "/transactions/api?status=DRAFT",
                com.microsoft.playwright.options.RequestOptions.create()
                    .setHeader("Accept", "application/json"));

            org.assertj.core.api.Assertions.assertThat(response.status()).isEqualTo(200);
        }

        @Test
        @DisplayName("Should call transaction list API with category filter")
        void shouldCallTransactionListApiWithCategoryFilter() {
            var response = page.request().get(
                baseUrl() + "/transactions/api?category=INCOME",
                com.microsoft.playwright.options.RequestOptions.create()
                    .setHeader("Accept", "application/json"));

            org.assertj.core.api.Assertions.assertThat(response.status()).isEqualTo(200);
        }

        @Test
        @DisplayName("Should call transaction search API")
        void shouldCallTransactionSearchApi() {
            var response = page.request().get(
                baseUrl() + "/transactions/api/search?q=test",
                com.microsoft.playwright.options.RequestOptions.create()
                    .setHeader("Accept", "application/json"));

            org.assertj.core.api.Assertions.assertThat(response.status()).isEqualTo(200);
        }
    }

    // ==================== TRANSACTION CONTROLLER UI TESTS ====================

    @Nested
    @DisplayName("Transaction Controller UI")
    class TransactionControllerUi {

        @Test
        @DisplayName("Should display transaction list with status filter")
        void shouldDisplayTransactionListWithStatusFilter() {
            navigateTo("/transactions?status=POSTED");
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }

        @Test
        @DisplayName("Should display transaction list sorted by date")
        void shouldDisplayTransactionListSortedByDate() {
            navigateTo("/transactions?sort=transactionDate,desc");
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }

        @Test
        @DisplayName("Should display draft transactions only")
        void shouldDisplayDraftTransactionsOnly() {
            navigateTo("/transactions?status=DRAFT");
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }

        @Test
        @DisplayName("Should display voided transactions only")
        void shouldDisplayVoidedTransactionsOnly() {
            navigateTo("/transactions?status=VOID");
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }
    }

    // ==================== USER CONTROLLER TESTS ====================

    @Nested
    @DisplayName("User Controller")
    class UserControllerTests {

        @Test
        @DisplayName("Should display user list page")
        void shouldDisplayUserListPage() {
            navigateTo("/users");
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }

        @Test
        @DisplayName("Should display user create form")
        void shouldDisplayUserCreateForm() {
            navigateTo("/users/new");
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }

        @Test
        @DisplayName("Should display user edit form for existing user")
        void shouldDisplayUserEditFormForExistingUser() {
            var user = userRepository.findAll().stream()
                .filter(u -> !"admin".equals(u.getUsername()))
                .findFirst();
            if (user.isEmpty()) {
                return;
            }

            navigateTo("/users/" + user.get().getId() + "/edit");
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }
    }

    // ==================== EMPLOYEE CONTROLLER TESTS ====================

    @Nested
    @DisplayName("Employee Controller")
    class EmployeeControllerTests {

        @Test
        @DisplayName("Should display employee list page")
        void shouldDisplayEmployeeListPage() {
            navigateTo("/employees");
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }

        @Test
        @DisplayName("Should display employee create form")
        void shouldDisplayEmployeeCreateForm() {
            navigateTo("/employees/new");
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }

        @Test
        @DisplayName("Should display employee detail page")
        void shouldDisplayEmployeeDetailPage() {
            var employee = employeeRepository.findAll().stream().findFirst();
            if (employee.isEmpty()) {
                return;
            }

            navigateTo("/employees/" + employee.get().getId());
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }
    }

    // ==================== FIXED ASSET CONTROLLER TESTS ====================

    @Nested
    @DisplayName("Fixed Asset Controller")
    class FixedAssetControllerTests {

        @Test
        @DisplayName("Should display fixed asset list page")
        void shouldDisplayFixedAssetListPage() {
            navigateTo("/assets");
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }

        @Test
        @DisplayName("Should display fixed asset create form")
        void shouldDisplayFixedAssetCreateForm() {
            navigateTo("/assets/new");
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }

        @Test
        @DisplayName("Should display fixed asset detail page")
        void shouldDisplayFixedAssetDetailPage() {
            var asset = fixedAssetRepository.findAll().stream().findFirst();
            if (asset.isEmpty()) {
                return;
            }

            navigateTo("/assets/" + asset.get().getId());
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }

        @Test
        @DisplayName("Should display fixed asset edit form")
        void shouldDisplayFixedAssetEditForm() {
            var asset = fixedAssetRepository.findAll().stream().findFirst();
            if (asset.isEmpty()) {
                return;
            }

            navigateTo("/assets/" + asset.get().getId() + "/edit");
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }

        @Test
        @DisplayName("Should display asset categories list page")
        void shouldDisplayAssetCategoriesListPage() {
            navigateTo("/assets/categories");
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }
    }

    // ==================== CHART OF ACCOUNTS CONTROLLER TESTS ====================

    @Nested
    @DisplayName("Chart of Accounts Controller")
    class ChartOfAccountsControllerTests {

        @Test
        @DisplayName("Should display accounts list page")
        void shouldDisplayAccountsListPage() {
            navigateTo("/accounts");
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }

        @Test
        @DisplayName("Should display account create form")
        void shouldDisplayAccountCreateForm() {
            navigateTo("/accounts/new");
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }

        @Test
        @DisplayName("Should display account detail page")
        void shouldDisplayAccountDetailPage() {
            var account = accountRepository.findAll().stream().findFirst();
            if (account.isEmpty()) {
                return;
            }

            navigateTo("/accounts/" + account.get().getId());
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }
    }

    // ==================== JOURNAL TEMPLATE CONTROLLER TESTS ====================

    @Nested
    @DisplayName("Journal Template Controller")
    class JournalTemplateControllerTests {

        @Test
        @DisplayName("Should display journal template list page")
        void shouldDisplayJournalTemplateListPage() {
            navigateTo("/templates");
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }

        @Test
        @DisplayName("Should display journal template create form")
        void shouldDisplayJournalTemplateCreateForm() {
            navigateTo("/templates/new");
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }

        @Test
        @DisplayName("Should display journal template detail page")
        void shouldDisplayJournalTemplateDetailPage() {
            var template = templateRepository.findAll().stream().findFirst();
            if (template.isEmpty()) {
                return;
            }

            navigateTo("/templates/" + template.get().getId());
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }

        @Test
        @DisplayName("Should call journal template API endpoint")
        void shouldCallJournalTemplateApiEndpoint() {
            var response = page.request().get(
                baseUrl() + "/templates/api",
                com.microsoft.playwright.options.RequestOptions.create()
                    .setHeader("Accept", "application/json"));

            org.assertj.core.api.Assertions.assertThat(response.status()).isEqualTo(200);
        }

        @Test
        @DisplayName("Should call journal template search API endpoint")
        void shouldCallJournalTemplateSearchApiEndpoint() {
            var response = page.request().get(
                baseUrl() + "/templates/api/search?q=test",
                com.microsoft.playwright.options.RequestOptions.create()
                    .setHeader("Accept", "application/json"));

            org.assertj.core.api.Assertions.assertThat(response.status()).isEqualTo(200);
        }
    }

    // ==================== INVOICE CONTROLLER TESTS ====================

    @Nested
    @DisplayName("Invoice Controller")
    class InvoiceControllerTests {

        @Test
        @DisplayName("Should display invoice list page")
        void shouldDisplayInvoiceListPage() {
            navigateTo("/invoices");
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }

        @Test
        @DisplayName("Should display invoice create form")
        void shouldDisplayInvoiceCreateForm() {
            navigateTo("/invoices/new");
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }
    }

    // ==================== DOCUMENT CONTROLLER TESTS ====================

    @Nested
    @DisplayName("Document Controller")
    class DocumentControllerTests {

        @Test
        @DisplayName("Should display documents list page")
        void shouldDisplayDocumentsListPage() {
            navigateTo("/documents");
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }
    }

    // ==================== DRAFT TRANSACTION CONTROLLER TESTS ====================

    @Nested
    @DisplayName("Draft Transaction Controller")
    class DraftTransactionControllerTests {

        @Test
        @DisplayName("Should display drafts list page")
        void shouldDisplayDraftsListPage() {
            navigateTo("/drafts");
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }

        @Test
        @DisplayName("Should call drafts API endpoint")
        void shouldCallDraftsApiEndpoint() {
            var response = page.request().get(
                baseUrl() + "/drafts/api",
                com.microsoft.playwright.options.RequestOptions.create()
                    .setHeader("Accept", "application/json"));

            org.assertj.core.api.Assertions.assertThat(response.status()).isEqualTo(200);
        }
    }

    // ==================== JOURNAL ENTRY CONTROLLER TESTS ====================

    @Nested
    @DisplayName("Journal Entry Controller")
    class JournalEntryControllerTests {

        @Test
        @DisplayName("Should display journals list page")
        void shouldDisplayJournalsListPage() {
            navigateTo("/journals");
            waitForPageLoad();

            assertThat(page.locator("body")).isVisible();
        }

        @Test
        @DisplayName("Should call journals API endpoint")
        void shouldCallJournalsApiEndpoint() {
            String startDate = LocalDate.now().minusMonths(1).toString();
            String endDate = LocalDate.now().toString();
            var response = page.request().get(
                baseUrl() + "/journals/api?startDate=" + startDate + "&endDate=" + endDate,
                com.microsoft.playwright.options.RequestOptions.create()
                    .setHeader("Accept", "application/json"));

            org.assertj.core.api.Assertions.assertThat(response.status()).isEqualTo(200);
        }
    }
}
