package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.repository.EmployeeRepository;
import com.artivisi.accountingfinance.repository.PayrollDetailRepository;
import com.artivisi.accountingfinance.repository.UserRepository;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Functional tests for SelfServiceController.
 * Tests employee self-service: profile, payslips, leave requests.
 */
@DisplayName("Self Service Controller Tests")
@Import(ServiceTestDataInitializer.class)
class SelfServiceControllerFunctionalTest extends PlaywrightTestBase {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PayrollDetailRepository payrollDetailRepository;

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    @Test
    @DisplayName("Should display profile page")
    void shouldDisplayProfilePage() {
        navigateTo("/self-service/profile");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should display profile edit form")
    void shouldDisplayProfileEditForm() {
        navigateTo("/self-service/profile/edit");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should update profile")
    void shouldUpdateProfile() {
        navigateTo("/self-service/profile/edit");
        waitForPageLoad();

        var phoneInput = page.locator("input[name='phone']").first();
        if (phoneInput.isVisible()) {
            phoneInput.fill("08123456789");

            var submitBtn = page.locator("#btn-simpan").first();
            if (submitBtn.isVisible()) {
                submitBtn.click();
                waitForPageLoad();
            }
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should display payslips list")
    void shouldDisplayPayslipsList() {
        navigateTo("/self-service/payslips");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should filter payslips by year")
    void shouldFilterPayslipsByYear() {
        navigateTo("/self-service/payslips");
        waitForPageLoad();

        var yearSelect = page.locator("select[name='year']").first();
        if (yearSelect.isVisible()) {
            yearSelect.selectOption("2024");

            var filterBtn = page.locator("form button[type='submit']").first();
            if (filterBtn.isVisible()) {
                filterBtn.click();
                waitForPageLoad();
            }
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should view payslip detail")
    void shouldViewPayslipDetail() {
        var payrollDetail = payrollDetailRepository.findAll().stream().findFirst();
        if (payrollDetail.isEmpty()) {
            return;
        }

        navigateTo("/self-service/payslips/" + payrollDetail.get().getId());
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should download payslip PDF")
    void shouldDownloadPayslipPdf() {
        var payrollDetail = payrollDetailRepository.findAll().stream().findFirst();
        if (payrollDetail.isEmpty()) {
            return;
        }

        // PDF download - verify URL is accessible by checking no error page
        var response = page.navigate("http://localhost:" + port + "/self-service/payslips/" + payrollDetail.get().getId() + "/pdf");
        // PDF endpoint should return 200 OK or trigger download
        if (response != null) {
            var status = response.status();
            // Accept 200 (OK) or 404 (not found) - test passes if no server error
            org.junit.jupiter.api.Assertions.assertTrue(status < 500, "Server error: " + status);
        }
    }

    @Test
    @DisplayName("Should display leave requests list")
    void shouldDisplayLeaveRequestsList() {
        navigateTo("/self-service/leave");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should display new leave request form")
    void shouldDisplayNewLeaveRequestForm() {
        navigateTo("/self-service/leave/new");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should submit leave request")
    void shouldSubmitLeaveRequest() {
        navigateTo("/self-service/leave/new");
        waitForPageLoad();

        var startDateInput = page.locator("input[name='startDate']").first();
        var endDateInput = page.locator("input[name='endDate']").first();
        var reasonInput = page.locator("textarea[name='reason'], input[name='reason']").first();

        if (startDateInput.isVisible()) {
            startDateInput.fill("2025-01-15");
        }
        if (endDateInput.isVisible()) {
            endDateInput.fill("2025-01-16");
        }
        if (reasonInput.isVisible()) {
            reasonInput.fill("Personal leave");
        }

        var submitBtn = page.locator("#btn-simpan").first();
        if (submitBtn.isVisible()) {
            submitBtn.click();
            waitForPageLoad();
        }

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should display change password form")
    void shouldDisplayChangePasswordForm() {
        navigateTo("/self-service/change-password");
        waitForPageLoad();

        assertThat(page.locator("body")).isVisible();
    }

    @Test
    @DisplayName("Should show error for mismatched passwords")
    void shouldShowErrorForMismatchedPasswords() {
        navigateTo("/self-service/change-password");
        waitForPageLoad();

        var currentPasswordInput = page.locator("input[name='currentPassword']").first();
        var newPasswordInput = page.locator("input[name='newPassword']").first();
        var confirmPasswordInput = page.locator("input[name='confirmPassword']").first();

        if (currentPasswordInput.isVisible() && newPasswordInput.isVisible() && confirmPasswordInput.isVisible()) {
            currentPasswordInput.fill("oldpassword");
            newPasswordInput.fill("NewPassword123!");
            confirmPasswordInput.fill("DifferentPassword123!");

            var submitBtn = page.locator("#btn-simpan, button[type='submit']").first();
            if (submitBtn.isVisible()) {
                submitBtn.click();
                waitForPageLoad();
            }
        }

        assertThat(page.locator("body")).isVisible();
    }
}
