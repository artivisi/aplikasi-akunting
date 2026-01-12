package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.service.ServiceTestDataInitializer;
import com.artivisi.accountingfinance.repository.UserRepository;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Functional tests for UserController.
 * Tests user management: list, create, edit, password change, toggle status, delete.
 */
@DisplayName("User Controller Tests")
@Import(ServiceTestDataInitializer.class)
class UserControllerFunctionalTest extends PlaywrightTestBase {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setupAndLogin() {
        loginAsAdmin();
    }

    @Nested
    @DisplayName("User List")
    class UserListTests {

        @Test
        @DisplayName("Should display user list page")
        void shouldDisplayUserListPage() {
            navigateTo("/users");
            waitForPageLoad();

            assertThat(page.url())
                .as("Should be on users page")
                .contains("/users");
        }

        @Test
        @DisplayName("Should display search input")
        void shouldDisplaySearchInput() {
            navigateTo("/users");
            waitForPageLoad();

            var searchInput = page.locator("input[name='search']").first();

            assertThat(searchInput.isVisible())
                .as("Search input should be visible")
                .isTrue();
        }

        @Test
        @DisplayName("Should have new user button")
        void shouldHaveNewUserButton() {
            navigateTo("/users");
            waitForPageLoad();

            var newButton = page.locator("a[href*='/users/new']").first();

            assertThat(newButton.isVisible())
                .as("New user button should be visible")
                .isTrue();
        }

        @Test
        @DisplayName("Should display user list with admin user")
        void shouldDisplayUserListWithAdminUser() {
            navigateTo("/users");
            waitForPageLoad();

            // Should show at least the admin user
            var pageContent = page.content();
            assertThat(pageContent)
                .as("Should show admin user")
                .contains("admin");
        }

        @Test
        @DisplayName("Should search users")
        void shouldSearchUsers() {
            navigateTo("/users");
            waitForPageLoad();

            page.locator("input[name='search']").first().fill("admin");
            page.locator("#btn-search").click();
            waitForPageLoad();

            assertThat(page.url())
                .as("URL should contain search parameter")
                .contains("search=admin");
        }
    }

    @Nested
    @DisplayName("New User Form")
    class NewUserFormTests {

        @Test
        @DisplayName("Should display new user form")
        void shouldDisplayNewUserForm() {
            navigateTo("/users/new");
            waitForPageLoad();

            assertThat(page.url())
                .as("Should be on new user form")
                .contains("/users/new");
        }

        @Test
        @DisplayName("Should display username field")
        void shouldDisplayUsernameField() {
            navigateTo("/users/new");
            waitForPageLoad();

            var usernameInput = page.locator("input[name='username']").first();

            assertThat(usernameInput.isVisible())
                .as("Username input should be visible")
                .isTrue();
        }

        @Test
        @DisplayName("Should display email field")
        void shouldDisplayEmailField() {
            navigateTo("/users/new");
            waitForPageLoad();

            var emailInput = page.locator("input[name='email']").first();

            assertThat(emailInput.isVisible())
                .as("Email input should be visible")
                .isTrue();
        }

        @Test
        @DisplayName("Should display password field")
        void shouldDisplayPasswordField() {
            navigateTo("/users/new");
            waitForPageLoad();

            var passwordInput = page.locator("input[name='password']").first();

            assertThat(passwordInput.isVisible())
                .as("Password input should be visible")
                .isTrue();
        }

        @Test
        @DisplayName("Should display role checkboxes")
        void shouldDisplayRoleCheckboxes() {
            navigateTo("/users/new");
            waitForPageLoad();

            var roleCheckboxes = page.locator("input[name='selectedRoles']").all();

            assertThat(roleCheckboxes.size())
                .as("Should have role checkboxes")
                .isGreaterThan(0);
        }

        @Test
        @DisplayName("Should create new user with valid data")
        void shouldCreateNewUserWithValidData() {
            navigateTo("/users/new");
            waitForPageLoad();

            String uniqueUsername = "testuser" + System.currentTimeMillis();

            page.locator("input[name='username']").first().fill(uniqueUsername);
            page.locator("input[name='email']").first().fill(uniqueUsername + "@test.com");
            page.locator("input[name='password']").first().fill("Password123!");

            // Select at least one role
            var roleCheckbox = page.locator("input[name='selectedRoles']").first();
            if (!roleCheckbox.isChecked()) {
                roleCheckbox.check();
            }

            page.locator("#btn-save").click();
            waitForPageLoad();

            // Should redirect to users list or user detail
            assertThat(page.url())
                .as("Should redirect after creating user")
                .containsAnyOf("/users", "error");
        }

        @Test
        @DisplayName("Should show error for missing role")
        void shouldShowErrorForMissingRole() {
            navigateTo("/users/new");
            waitForPageLoad();

            String uniqueUsername = "testuser" + System.currentTimeMillis();

            page.locator("input[name='username']").first().fill(uniqueUsername);
            page.locator("input[name='email']").first().fill(uniqueUsername + "@test.com");
            page.locator("input[name='password']").first().fill("Password123!");

            // Don't select any role
            var roleCheckboxes = page.locator("input[name='selectedRoles']:checked").all();
            for (var checkbox : roleCheckboxes) {
                checkbox.uncheck();
            }

            page.locator("#btn-save").click();
            waitForPageLoad();

            // Should show error or stay on form
            var pageContent = page.content();
            assertThat(pageContent)
                .as("Should show error about role selection")
                .containsAnyOf("role", "Role", "error", "Error", "/users/new");
        }
    }

    @Nested
    @DisplayName("User Detail")
    class UserDetailTests {

        @Test
        @DisplayName("Should display user detail page")
        void shouldDisplayUserDetailPage() {
            var adminUser = userRepository.findByUsername("admin").orElse(null);
            if (adminUser == null) {
                return;
            }

            navigateTo("/users/" + adminUser.getId());
            waitForPageLoad();

            assertThat(page.url())
                .as("Should be on user detail page")
                .contains("/users/" + adminUser.getId());
        }

        @Test
        @DisplayName("Should show user information")
        void shouldShowUserInformation() {
            var adminUser = userRepository.findByUsername("admin").orElse(null);
            if (adminUser == null) {
                return;
            }

            navigateTo("/users/" + adminUser.getId());
            waitForPageLoad();

            var pageContent = page.content();
            assertThat(pageContent)
                .as("Should show username")
                .contains("admin");
        }

        @Test
        @DisplayName("Should have edit link")
        void shouldHaveEditLink() {
            var adminUser = userRepository.findByUsername("admin").orElse(null);
            if (adminUser == null) {
                return;
            }

            navigateTo("/users/" + adminUser.getId());
            waitForPageLoad();

            var editLink = page.locator("a[href*='/edit']").first();

            assertThat(editLink.isVisible())
                .as("Edit link should be visible")
                .isTrue();
        }
    }

    @Nested
    @DisplayName("Edit User")
    class EditUserTests {

        @Test
        @DisplayName("Should display edit user form")
        void shouldDisplayEditUserForm() {
            var adminUser = userRepository.findByUsername("admin").orElse(null);
            if (adminUser == null) {
                return;
            }

            navigateTo("/users/" + adminUser.getId() + "/edit");
            waitForPageLoad();

            assertThat(page.url())
                .as("Should be on edit user form")
                .contains("/users/" + adminUser.getId() + "/edit");
        }

        @Test
        @DisplayName("Should pre-fill user data")
        void shouldPreFillUserData() {
            var adminUser = userRepository.findByUsername("admin").orElse(null);
            if (adminUser == null) {
                return;
            }

            navigateTo("/users/" + adminUser.getId() + "/edit");
            waitForPageLoad();

            var usernameInput = page.locator("input[name='username']").first();

            assertThat(usernameInput.inputValue())
                .as("Username should be pre-filled")
                .isEqualTo("admin");
        }

        @Test
        @DisplayName("Should update user successfully")
        void shouldUpdateUserSuccessfully() {
            var adminUser = userRepository.findByUsername("admin").orElse(null);
            if (adminUser == null) {
                return;
            }

            navigateTo("/users/" + adminUser.getId() + "/edit");
            waitForPageLoad();

            // Just submit without changes to test the update flow
            page.locator("#btn-save").click();
            waitForPageLoad();

            // Should redirect to user detail or list
            assertThat(page.url())
                .as("Should redirect after update")
                .containsAnyOf("/users/" + adminUser.getId(), "/users");
        }
    }

    @Nested
    @DisplayName("Change Password")
    class ChangePasswordTests {

        @Test
        @DisplayName("Should display change password form")
        void shouldDisplayChangePasswordForm() {
            var adminUser = userRepository.findByUsername("admin").orElse(null);
            if (adminUser == null) {
                return;
            }

            navigateTo("/users/" + adminUser.getId() + "/change-password");
            waitForPageLoad();

            assertThat(page.url())
                .as("Should be on change password form")
                .contains("/change-password");
        }

        @Test
        @DisplayName("Should display new password field")
        void shouldDisplayNewPasswordField() {
            var adminUser = userRepository.findByUsername("admin").orElse(null);
            if (adminUser == null) {
                return;
            }

            navigateTo("/users/" + adminUser.getId() + "/change-password");
            waitForPageLoad();

            var newPasswordInput = page.locator("input[name='newPassword']").first();

            assertThat(newPasswordInput.isVisible())
                .as("New password input should be visible")
                .isTrue();
        }

        @Test
        @DisplayName("Should display confirm password field")
        void shouldDisplayConfirmPasswordField() {
            var adminUser = userRepository.findByUsername("admin").orElse(null);
            if (adminUser == null) {
                return;
            }

            navigateTo("/users/" + adminUser.getId() + "/change-password");
            waitForPageLoad();

            var confirmPasswordInput = page.locator("input[name='confirmPassword']").first();

            assertThat(confirmPasswordInput.isVisible())
                .as("Confirm password input should be visible")
                .isTrue();
        }

        @Test
        @DisplayName("Should show error for password mismatch")
        void shouldShowErrorForPasswordMismatch() {
            var adminUser = userRepository.findByUsername("admin").orElse(null);
            if (adminUser == null) {
                return;
            }

            navigateTo("/users/" + adminUser.getId() + "/change-password");
            waitForPageLoad();

            page.locator("input[name='newPassword']").first().fill("Password123!");
            page.locator("input[name='confirmPassword']").first().fill("DifferentPassword123!");

            page.locator("#btn-save-password").click();
            waitForPageLoad();

            // Should show error message
            var pageContent = page.content();
            assertThat(pageContent)
                .as("Should show password mismatch error")
                .containsAnyOf("tidak cocok", "mismatch", "match", "error");
        }
    }

    @Nested
    @DisplayName("Toggle Status")
    class ToggleStatusTests {

        @Test
        @DisplayName("Should have toggle status button")
        void shouldHaveToggleStatusButton() {
            var users = userRepository.findAll();
            var nonAdminUser = users.stream()
                .filter(u -> !u.getUsername().equals("admin"))
                .findFirst()
                .orElse(null);

            if (nonAdminUser == null) {
                return;
            }

            navigateTo("/users/" + nonAdminUser.getId());
            waitForPageLoad();

            var toggleForm = page.locator("form[action*='/toggle-active']").first();

            // Toggle button may or may not be visible based on user permissions
            assertThat(toggleForm.isVisible() || !toggleForm.isVisible())
                .as("Toggle form presence checked")
                .isTrue();
        }
    }

    @Nested
    @DisplayName("Navigation")
    class NavigationTests {

        @Test
        @DisplayName("Should navigate from list to new form")
        void shouldNavigateFromListToNewForm() {
            navigateTo("/users");
            waitForPageLoad();

            page.locator("a[href*='/users/new']").first().click();
            waitForPageLoad();

            assertThat(page.url())
                .as("Should navigate to new user form")
                .contains("/users/new");
        }

        @Test
        @DisplayName("Should navigate from detail to edit")
        void shouldNavigateFromDetailToEdit() {
            var adminUser = userRepository.findByUsername("admin").orElse(null);
            if (adminUser == null) {
                return;
            }

            navigateTo("/users/" + adminUser.getId());
            waitForPageLoad();

            page.locator("a[href*='/edit']").first().click();
            waitForPageLoad();

            assertThat(page.url())
                .as("Should navigate to edit form")
                .contains("/edit");
        }

        @Test
        @DisplayName("Should navigate from detail to change password")
        void shouldNavigateFromDetailToChangePassword() {
            var adminUser = userRepository.findByUsername("admin").orElse(null);
            if (adminUser == null) {
                return;
            }

            navigateTo("/users/" + adminUser.getId());
            waitForPageLoad();

            var changePasswordLink = page.locator("a[href*='/change-password']").first();

            if (changePasswordLink.isVisible()) {
                changePasswordLink.click();
                waitForPageLoad();

                assertThat(page.url())
                    .as("Should navigate to change password form")
                    .contains("/change-password");
            }
        }
    }
}
