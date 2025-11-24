package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.page.JournalFormPage;
import com.artivisi.accountingfinance.functional.page.JournalListPage;
import com.artivisi.accountingfinance.functional.page.LoginPage;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Journal Entry - Create (Section 4)")
class JournalEntryCreateTest extends PlaywrightTestBase {

    private LoginPage loginPage;
    private JournalListPage journalListPage;
    private JournalFormPage journalFormPage;

    @BeforeEach
    void setUp() {
        loginPage = new LoginPage(page, baseUrl());
        journalListPage = new JournalListPage(page, baseUrl());
        journalFormPage = new JournalFormPage(page, baseUrl());

        loginPage.navigate().loginAsAdmin();
    }

    @Nested
    @DisplayName("4.1 Navigation")
    class NavigationTests {

        @Test
        @DisplayName("Should display 'Tambah Jurnal' button on list page")
        void shouldDisplayAddJournalButton() {
            journalListPage.navigate();

            assertThat(page.locator("#btn-add-journal").isVisible()).isTrue();
        }

        @Test
        @DisplayName("Should navigate to create form when clicking 'Tambah Jurnal'")
        void shouldNavigateToCreateForm() {
            journalListPage.navigate();
            page.click("#btn-add-journal");

            page.waitForLoadState();
            assertThat(page.url()).contains("/journals/new");
        }

        @Test
        @DisplayName("Should display form page title 'Tambah Jurnal'")
        void shouldDisplayFormPageTitle() {
            journalFormPage.navigate();

            journalFormPage.assertPageTitleVisible();
            journalFormPage.assertPageTitleText("Tambah Jurnal");
        }
    }

    @Nested
    @DisplayName("4.2 Form Display")
    class FormDisplayTests {

        @BeforeEach
        void navigateToForm() {
            journalFormPage.navigate();
        }

        @Test
        @DisplayName("Should display journal date input")
        void shouldDisplayJournalDateInput() {
            journalFormPage.assertJournalDateVisible();
        }

        @Test
        @DisplayName("Should display reference number input")
        void shouldDisplayReferenceNumberInput() {
            journalFormPage.assertReferenceNumberVisible();
        }

        @Test
        @DisplayName("Should display description input")
        void shouldDisplayDescriptionInput() {
            journalFormPage.assertDescriptionVisible();
        }

        @Test
        @DisplayName("Should display save draft button")
        void shouldDisplaySaveDraftButton() {
            journalFormPage.assertSaveDraftButtonVisible();
        }

        @Test
        @DisplayName("Should display save and post button")
        void shouldDisplaySavePostButton() {
            journalFormPage.assertSavePostButtonVisible();
        }

        @Test
        @DisplayName("Should display cancel button")
        void shouldDisplayCancelButton() {
            journalFormPage.assertCancelButtonVisible();
        }
    }

    @Nested
    @DisplayName("4.3 Form Actions")
    class FormActionsTests {

        @BeforeEach
        void navigateToForm() {
            journalFormPage.navigate();
        }

        @Test
        @DisplayName("Should navigate back to list when clicking cancel")
        void shouldNavigateBackWhenClickingCancel() {
            journalFormPage.clickCancel();

            page.waitForLoadState();
            assertThat(page.url()).contains("/journals");
            assertThat(page.url()).doesNotContain("/new");
        }
    }
}
