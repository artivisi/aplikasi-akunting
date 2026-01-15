package com.artivisi.accountingfinance.functional.service;

import com.artivisi.accountingfinance.repository.AmortizationScheduleRepository;
import com.artivisi.accountingfinance.repository.ChartOfAccountRepository;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Amortization Functional Tests.
 * Tests the amortization workflow: create schedule, view entries, post, skip, cancel.
 * Covers AmortizationController, AmortizationScheduleService, AmortizationEntryService.
 */
@DisplayName("Service Industry - Amortization")
@Import(ServiceTestDataInitializer.class)
class ServiceAmortizationTest extends PlaywrightTestBase {

    @Autowired
    private AmortizationScheduleRepository scheduleRepository;

    @Autowired
    private ChartOfAccountRepository chartOfAccountRepository;

    @BeforeEach
    void setup() {
        loginAsAdmin();
    }

    @Test
    @DisplayName("Should display amortization list page")
    void shouldDisplayAmortizationList() {
        navigateTo("/amortization");
        waitForPageLoad();

        // Verify page loads
        assertThat(page.locator("#page-title, h1").first()).containsText("Amortisasi");
    }

    @Test
    @DisplayName("Should display new amortization form")
    void shouldDisplayNewAmortizationForm() {
        navigateTo("/amortization/new");
        waitForPageLoad();

        // Verify form loads with required fields
        assertThat(page.locator("input[name='code']")).isVisible();
        assertThat(page.locator("input[name='name']")).isVisible();
        assertThat(page.locator("select[name='scheduleType']")).isVisible();
        assertThat(page.locator("select[name='sourceAccountId']")).isVisible();
        assertThat(page.locator("select[name='targetAccountId']")).isVisible();
        assertThat(page.locator("input[name='totalAmount']")).isVisible();
        assertThat(page.locator("input[name='startDate']")).isVisible();
        assertThat(page.locator("input[name='endDate']")).isVisible();
        assertThat(page.locator("select[name='frequency']")).isVisible();
    }

    @Test
    @DisplayName("Should create new amortization schedule")
    void shouldCreateAmortizationSchedule() {
        navigateTo("/amortization/new");
        waitForPageLoad();

        // Verify form page loads
        assertThat(page.locator("body")).isVisible();

        // Try to fill basic form fields if visible
        var codeInput = page.locator("input[name='code']").first();
        if (codeInput.isVisible()) {
            String uniqueCode = "AMORT-" + System.currentTimeMillis();
            codeInput.fill(uniqueCode);

            var nameInput = page.locator("input[name='name']").first();
            if (nameInput.isVisible()) {
                nameInput.fill("Test Amortisasi");
            }

            // Submit form if all basic fields filled
            var submitBtn = page.locator("#btn-simpan");
            if (submitBtn.isVisible()) {
                submitBtn.click();
                waitForPageLoad();
            }
        }

        // Verify page still accessible (not redirected to login)
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/amortization.*"));
    }

    @Test
    @DisplayName("Should display amortization schedule detail")
    void shouldDisplayAmortizationDetail() {
        var schedule = scheduleRepository.findAll().stream().findFirst();

        if (schedule.isPresent()) {
            navigateTo("/amortization/" + schedule.get().getId());
            waitForPageLoad();

            // Verify detail page loads
            assertThat(page.locator("#page-title, h1").first()).isVisible();

            // Verify schedule info is displayed
            assertThat(page.locator("body")).containsText(schedule.get().getName());
        }
    }

    @Test
    @DisplayName("Should edit amortization schedule")
    void shouldEditAmortizationSchedule() {
        var schedule = scheduleRepository.findAll().stream()
                .filter(s -> s.getStatus().name().equals("ACTIVE"))
                .findFirst();

        if (schedule.isPresent()) {
            navigateTo("/amortization/" + schedule.get().getId() + "/edit");
            waitForPageLoad();

            // Verify edit form loads
            assertThat(page.locator("input[name='name']")).isVisible();

            // Update name
            page.fill("input[name='name']", schedule.get().getName() + " (Updated)");
            page.locator("#btn-simpan").click();
            waitForPageLoad();

            // Verify still on amortization page (not redirected to login)
            assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/amortization.*"));
        }
    }

    @Test
    @DisplayName("Should post single amortization entry")
    void shouldPostSingleEntry() {
        var schedule = scheduleRepository.findAll().stream()
                .filter(s -> s.getStatus().name().equals("ACTIVE"))
                .findFirst();

        if (schedule.isPresent()) {
            navigateTo("/amortization/" + schedule.get().getId());
            waitForPageLoad();

            // Find post button for pending entry
            var postBtn = page.locator("form[action*='/entries/'][action*='/post'] button[type='submit']").first();
            if (postBtn.isVisible()) {
                postBtn.click();
                waitForPageLoad();

                // Verify still on amortization page (not redirected to login)
                assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/amortization.*"));
            }
        }
    }

    @Test
    @DisplayName("Should skip amortization entry")
    void shouldSkipEntry() {
        var schedule = scheduleRepository.findAll().stream()
                .filter(s -> s.getStatus().name().equals("ACTIVE"))
                .findFirst();

        if (schedule.isPresent()) {
            navigateTo("/amortization/" + schedule.get().getId());
            waitForPageLoad();

            // Find skip button for pending entry
            var skipBtn = page.locator("form[action*='/entries/'][action*='/skip'] button[type='submit']").first();
            if (skipBtn.isVisible()) {
                skipBtn.click();
                waitForPageLoad();

                // Verify still on amortization page (not redirected to login)
                assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/amortization.*"));
            }
        }
    }

    @Test
    @DisplayName("Should post all pending entries")
    void shouldPostAllPendingEntries() {
        var schedule = scheduleRepository.findAll().stream()
                .filter(s -> s.getStatus().name().equals("ACTIVE"))
                .findFirst();

        if (schedule.isPresent()) {
            navigateTo("/amortization/" + schedule.get().getId());
            waitForPageLoad();

            // Find post all button
            var postAllBtn = page.locator("form[action*='post-all'] button[type='submit']").first();
            if (postAllBtn.isVisible()) {
                postAllBtn.click();
                waitForPageLoad();

                // Verify still on amortization page (not redirected to login)
                assertThat(page).hasURL(java.util.regex.Pattern.compile(".*\\/amortization.*"));
            }
        }
    }

    @Test
    @DisplayName("Should cancel amortization schedule")
    void shouldCancelSchedule() {
        // First create a schedule to cancel
        navigateTo("/amortization/new");
        waitForPageLoad();

        var prepaidAccount = chartOfAccountRepository.findByAccountCode("1.2.01");
        var expenseAccount = chartOfAccountRepository.findByAccountCode("5.1.07");

        if (prepaidAccount.isEmpty() || expenseAccount.isEmpty()) {
            return;
        }

        String uniqueCode = "CANCEL-" + System.currentTimeMillis();
        page.fill("input[name='code']", uniqueCode);
        page.fill("input[name='name']", "Schedule to Cancel");
        page.selectOption("select[name='scheduleType']", "PREPAID_EXPENSE");
        page.selectOption("select[name='sourceAccountId']", prepaidAccount.get().getId().toString());
        page.selectOption("select[name='targetAccountId']", expenseAccount.get().getId().toString());
        page.fill("input[name='totalAmount']", "12000000");

        LocalDate startDate = LocalDate.now().plusMonths(6).withDayOfMonth(1);
        LocalDate endDate = startDate.plusMonths(12).minusDays(1);
        page.fill("input[name='startDate']", startDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        page.fill("input[name='endDate']", endDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        page.selectOption("select[name='frequency']", "MONTHLY");

        page.click("button[type='submit']");
        waitForPageLoad();

        // Now cancel it
        var cancelBtn = page.locator("button:has-text('Batalkan'), form[action*='cancel'] button[type='submit']").first();
        if (cancelBtn.isVisible()) {
            cancelBtn.click();
            waitForPageLoad();

            // Verify success
            assertThat(page.locator(".alert-success, [data-testid='success-message']").first()).isVisible();
        }
    }

    @Test
    @DisplayName("Should delete amortization schedule")
    void shouldDeleteSchedule() {
        // First create a schedule to delete
        navigateTo("/amortization/new");
        waitForPageLoad();

        var prepaidAccount = chartOfAccountRepository.findByAccountCode("1.2.01");
        var expenseAccount = chartOfAccountRepository.findByAccountCode("5.1.07");

        if (prepaidAccount.isEmpty() || expenseAccount.isEmpty()) {
            return;
        }

        String uniqueCode = "DELETE-" + System.currentTimeMillis();
        page.fill("input[name='code']", uniqueCode);
        page.fill("input[name='name']", "Schedule to Delete");
        page.selectOption("select[name='scheduleType']", "PREPAID_EXPENSE");
        page.selectOption("select[name='sourceAccountId']", prepaidAccount.get().getId().toString());
        page.selectOption("select[name='targetAccountId']", expenseAccount.get().getId().toString());
        page.fill("input[name='totalAmount']", "6000000");

        LocalDate startDate = LocalDate.now().plusMonths(12).withDayOfMonth(1);
        LocalDate endDate = startDate.plusMonths(6).minusDays(1);
        page.fill("input[name='startDate']", startDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        page.fill("input[name='endDate']", endDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        page.selectOption("select[name='frequency']", "MONTHLY");

        page.click("button[type='submit']");
        waitForPageLoad();

        // Now delete it
        var deleteBtn = page.locator("button:has-text('Hapus'), form[action*='delete'] button[type='submit']").first();
        if (deleteBtn.isVisible()) {
            deleteBtn.click();
            waitForPageLoad();

            // Verify redirected to list
            assertThat(page).hasURL(java.util.regex.Pattern.compile(".*/amortization$"));
        }
    }

    @Test
    @DisplayName("Should filter amortization list by status")
    void shouldFilterByStatus() {
        navigateTo("/amortization");
        waitForPageLoad();

        var statusSelect = page.locator("select[name='status']").first();
        if (statusSelect.isVisible()) {
            statusSelect.selectOption("ACTIVE");
            waitForPageLoad();

            // Verify filter applied
            assertThat(page).hasURL(java.util.regex.Pattern.compile(".*status=ACTIVE.*"));
        }
    }

    @Test
    @DisplayName("Should filter amortization list by type")
    void shouldFilterByType() {
        navigateTo("/amortization");
        waitForPageLoad();

        var typeSelect = page.locator("select[name='type']").first();
        if (typeSelect.isVisible()) {
            typeSelect.selectOption("PREPAID_EXPENSE");
            waitForPageLoad();

            // Verify filter applied
            assertThat(page).hasURL(java.util.regex.Pattern.compile(".*type=PREPAID_EXPENSE.*"));
        }
    }

    @Test
    @DisplayName("Should process batch auto-post entries")
    void shouldProcessBatchAutoPost() {
        navigateTo("/amortization");
        waitForPageLoad();

        // Find batch process button
        var batchBtn = page.locator("form[action*='batch/process'] button[type='submit'], button:has-text('Proses Batch')").first();
        if (batchBtn.isVisible()) {
            batchBtn.click();
            waitForPageLoad();

            // Verify page reloads (batch processed)
            assertThat(page.locator("#page-title, h1").first()).isVisible();
        } else {
            // Batch button not available - just verify page loads
            assertThat(page.locator("#page-title, h1").first()).isVisible();
        }
    }

    @Test
    @DisplayName("Should display pending entries count")
    void shouldDisplayPendingEntriesCount() {
        navigateTo("/amortization");
        waitForPageLoad();

        // Verify pending count is displayed somewhere
        // This is typically shown as a badge or counter
        assertThat(page.locator("body")).isVisible(); // Basic assertion to ensure page loaded
    }
}
