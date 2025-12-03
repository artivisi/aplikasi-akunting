package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.page.LoginPage;
import com.artivisi.accountingfinance.functional.page.UserDetailPage;
import com.artivisi.accountingfinance.functional.page.UserFormPage;
import com.artivisi.accountingfinance.functional.page.UserListPage;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("User Management & RBAC (Phase 3.7)")
class UserManagementTest extends PlaywrightTestBase {

    private LoginPage loginPage;
    private UserListPage listPage;
    private UserFormPage formPage;
    private UserDetailPage detailPage;

    @BeforeEach
    void setUp() {
        loginPage = new LoginPage(page, baseUrl());
        listPage = new UserListPage(page, baseUrl());
        formPage = new UserFormPage(page, baseUrl());
        detailPage = new UserDetailPage(page, baseUrl());

        loginPage.navigate().loginAsAdmin();
    }

    @Test
    @DisplayName("Should display user list page")
    void shouldDisplayUserListPage() {
        listPage.navigate();

        listPage.assertPageTitleVisible();
        listPage.assertPageTitleText("Kelola Pengguna");
    }

    @Test
    @DisplayName("Should display user table")
    void shouldDisplayUserTable() {
        listPage.navigate();

        listPage.assertTableVisible();
    }

    @Test
    @DisplayName("Should show admin user in list")
    void shouldShowAdminUserInList() {
        listPage.navigate();

        assertThat(listPage.hasUserWithUsername("admin")).isTrue();
    }

    @Test
    @DisplayName("Should display new user form")
    void shouldDisplayNewUserForm() {
        formPage.navigateToNew();

        formPage.assertPageTitleText("Pengguna Baru");
    }

    @Test
    @DisplayName("Should navigate to form from list page")
    void shouldNavigateToFormFromListPage() {
        listPage.navigate();
        listPage.clickNewUserButton();

        formPage.assertPageTitleText("Pengguna Baru");
    }

    @Test
    @DisplayName("Should create new user with ACCOUNTANT role")
    void shouldCreateNewUserWithAccountantRole() {
        formPage.navigateToNew();

        String uniqueUsername = "user" + System.currentTimeMillis() % 100000;
        String fullName = "Test User " + System.currentTimeMillis();

        formPage.fillUsername(uniqueUsername);
        formPage.fillPassword("password123");
        formPage.fillFullName(fullName);
        formPage.fillEmail(uniqueUsername + "@example.com");
        formPage.selectRole("ACCOUNTANT");
        formPage.clickSubmit();

        // Should redirect to user list with success message or detail page
        listPage.navigate();
        assertThat(listPage.hasUserWithUsername(uniqueUsername)).isTrue();
    }

    @Test
    @DisplayName("Should create new user with STAFF role")
    void shouldCreateNewUserWithStaffRole() {
        formPage.navigateToNew();

        String uniqueUsername = "staff" + System.currentTimeMillis() % 100000;
        String fullName = "Staff User " + System.currentTimeMillis();

        formPage.fillUsername(uniqueUsername);
        formPage.fillPassword("password123");
        formPage.fillFullName(fullName);
        formPage.selectRole("STAFF");
        formPage.clickSubmit();

        // Should redirect to user list
        listPage.navigate();
        assertThat(listPage.hasUserWithUsername(uniqueUsername)).isTrue();
    }

    @Test
    @DisplayName("Should view user detail")
    void shouldViewUserDetail() {
        listPage.navigate();
        listPage.clickUserDetailLink("admin");

        detailPage.assertUserFullNameText("Administrator");
        detailPage.assertActiveStatus();
    }

    @Test
    @DisplayName("Admin user should have ADMIN role badge")
    void adminUserShouldHaveAdminRoleBadge() {
        listPage.navigate();
        listPage.clickUserDetailLink("admin");

        assertThat(detailPage.hasRole("Administrator")).isTrue();
    }

    @Test
    @DisplayName("Should navigate to edit form")
    void shouldNavigateToEditForm() {
        // Navigate to admin user detail
        listPage.navigate();
        listPage.clickUserDetailLink("admin");

        // Wait for detail page
        page.waitForLoadState();

        // Click edit button
        detailPage.clickEditButton();

        // Should be on edit form
        page.waitForLoadState();
        assertThat(page.url()).contains("/edit");
    }

    @Test
    @DisplayName("Should toggle user active status")
    void shouldToggleUserActiveStatus() {
        // First create a user to toggle
        formPage.navigateToNew();

        String uniqueUsername = "toggle" + System.currentTimeMillis() % 100000;

        formPage.fillUsername(uniqueUsername);
        formPage.fillPassword("password123");
        formPage.fillFullName("Toggle Test User");
        formPage.selectRole("STAFF");
        formPage.clickSubmit();

        // Navigate to user detail
        listPage.navigate();
        listPage.clickUserDetailLink(uniqueUsername);

        // Initially active
        detailPage.assertActiveStatus();

        // Toggle to inactive
        detailPage.clickToggleActiveButton();
        detailPage.assertInactiveStatus();
    }

    @Test
    @DisplayName("Should change user password")
    void shouldChangeUserPassword() {
        // First create a user
        formPage.navigateToNew();

        String uniqueUsername = "pwdtest" + System.currentTimeMillis() % 100000;

        formPage.fillUsername(uniqueUsername);
        formPage.fillPassword("oldpassword");
        formPage.fillFullName("Password Test User");
        formPage.selectRole("STAFF");
        formPage.clickSubmit();

        // Navigate to user detail and change password
        listPage.navigate();
        listPage.clickUserDetailLink(uniqueUsername);

        // Click change password link
        page.locator("a:has-text('Ubah Password')").click();

        // Fill password form
        page.locator("input[name='newPassword']").fill("newpassword123");
        page.locator("input[name='confirmPassword']").fill("newpassword123");
        page.locator("button[type='submit']:has-text('Simpan')").click();

        // Should redirect back to detail page with success message
        assertThat(page.locator("body").textContent()).contains("Password berhasil diubah");
    }

    @Test
    @DisplayName("Should show error when passwords dont match")
    void shouldShowErrorWhenPasswordsDontMatch() {
        // First create a user
        formPage.navigateToNew();

        String uniqueUsername = "pwderr" + System.currentTimeMillis() % 100000;

        formPage.fillUsername(uniqueUsername);
        formPage.fillPassword("testpass");
        formPage.fillFullName("Password Error Test");
        formPage.selectRole("STAFF");
        formPage.clickSubmit();

        // Navigate to change password
        listPage.navigate();
        listPage.clickUserDetailLink(uniqueUsername);
        page.locator("a:has-text('Ubah Password')").click();

        // Fill mismatched passwords
        page.locator("input[name='newPassword']").fill("newpassword123");
        page.locator("input[name='confirmPassword']").fill("differentpassword");
        page.locator("button[type='submit']:has-text('Simpan')").click();

        // Should show error
        assertThat(page.locator("body").textContent()).contains("Password tidak cocok");
    }

    @Test
    @DisplayName("Should navigate to change password form")
    void shouldNavigateToChangePasswordForm() {
        // Navigate to admin user detail
        listPage.navigate();
        listPage.clickUserDetailLink("admin");

        // Wait for detail page to load
        page.waitForLoadState();
        page.locator("a:has-text('Ubah Password')").click();

        // Wait for change password form
        page.waitForLoadState();
        assertThat(page.url()).contains("/change-password");
    }

    @Test
    @DisplayName("Should search users")
    void shouldSearchUsers() {
        listPage.navigate();

        // Search for admin
        page.locator("input[name='search']").fill("admin");
        page.locator("button:has-text('Cari')").click();

        // Should find admin
        assertThat(listPage.hasUserWithUsername("admin")).isTrue();
    }

    @Test
    @DisplayName("Should show error when no role selected")
    void shouldShowErrorWhenNoRoleSelected() {
        formPage.navigateToNew();

        String uniqueUsername = "norole" + System.currentTimeMillis() % 100000;

        formPage.fillUsername(uniqueUsername);
        formPage.fillPassword("password123");
        formPage.fillFullName("No Role Test");
        // Don't select any role
        formPage.clickSubmit();

        // Should show error
        assertThat(page.locator("body").textContent()).contains("At least one role must be selected");
    }
}
