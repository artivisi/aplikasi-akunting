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
}
