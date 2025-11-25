package com.artivisi.accountingfinance.functional;

import com.artivisi.accountingfinance.functional.page.ClientDetailPage;
import com.artivisi.accountingfinance.functional.page.ClientFormPage;
import com.artivisi.accountingfinance.functional.page.ClientListPage;
import com.artivisi.accountingfinance.functional.page.LoginPage;
import com.artivisi.accountingfinance.ui.PlaywrightTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Client Management (Section 1.9)")
class ClientTest extends PlaywrightTestBase {

    private LoginPage loginPage;
    private ClientListPage listPage;
    private ClientFormPage formPage;
    private ClientDetailPage detailPage;

    @BeforeEach
    void setUp() {
        loginPage = new LoginPage(page, baseUrl());
        listPage = new ClientListPage(page, baseUrl());
        formPage = new ClientFormPage(page, baseUrl());
        detailPage = new ClientDetailPage(page, baseUrl());

        loginPage.navigate().loginAsAdmin();
    }

    @Nested
    @DisplayName("1.9.1 Client List")
    class ClientListTests {

        @Test
        @DisplayName("Should display client list page")
        void shouldDisplayClientListPage() {
            listPage.navigate();

            listPage.assertPageTitleVisible();
            listPage.assertPageTitleText("Daftar Klien");
        }

        @Test
        @DisplayName("Should display client table")
        void shouldDisplayClientTable() {
            listPage.navigate();

            listPage.assertTableVisible();
        }
    }

    @Nested
    @DisplayName("1.9.2 Client Form")
    class ClientFormTests {

        @Test
        @DisplayName("Should display new client form")
        void shouldDisplayNewClientForm() {
            formPage.navigateToNew();

            formPage.assertPageTitleText("Klien Baru");
        }

        @Test
        @DisplayName("Should navigate to form from list page")
        void shouldNavigateToFormFromListPage() {
            listPage.navigate();
            listPage.clickNewClientButton();

            formPage.assertPageTitleText("Klien Baru");
        }
    }

    @Nested
    @DisplayName("1.9.3 Client CRUD")
    class ClientCrudTests {

        @Test
        @DisplayName("Should create new client")
        void shouldCreateNewClient() {
            formPage.navigateToNew();

            String uniqueCode = "CLT-TEST-" + System.currentTimeMillis();
            String uniqueName = "Test Client " + System.currentTimeMillis();

            formPage.fillCode(uniqueCode);
            formPage.fillName(uniqueName);
            formPage.fillContactPerson("John Doe");
            formPage.fillEmail("john@example.com");
            formPage.fillPhone("021-1234567");
            formPage.clickSubmit();

            // Should redirect to detail page
            detailPage.assertClientNameText(uniqueName);
            detailPage.assertClientCodeText(uniqueCode);
        }

        @Test
        @DisplayName("Should show client in list after creation")
        void shouldShowClientInListAfterCreation() {
            formPage.navigateToNew();

            String uniqueCode = "CLT-LIST-" + System.currentTimeMillis();
            String uniqueName = "List Test Client " + System.currentTimeMillis();

            formPage.fillCode(uniqueCode);
            formPage.fillName(uniqueName);
            formPage.clickSubmit();

            // Navigate to list and search
            listPage.navigate();
            listPage.search(uniqueCode);

            assertThat(listPage.hasClientWithName(uniqueName)).isTrue();
        }
    }

    @Nested
    @DisplayName("1.9.4 Client Status")
    class ClientStatusTests {

        @Test
        @DisplayName("Should deactivate active client")
        void shouldDeactivateActiveClient() {
            // Create a client first
            formPage.navigateToNew();

            String uniqueCode = "CLT-DEACT-" + System.currentTimeMillis();
            String uniqueName = "Deactivate Test " + System.currentTimeMillis();

            formPage.fillCode(uniqueCode);
            formPage.fillName(uniqueName);
            formPage.clickSubmit();

            // Should be active by default
            detailPage.assertStatusText("Aktif");
            assertThat(detailPage.hasDeactivateButton()).isTrue();

            // Deactivate
            detailPage.clickDeactivateButton();

            // Should show inactive status
            detailPage.assertStatusText("Nonaktif");
            assertThat(detailPage.hasActivateButton()).isTrue();
        }

        @Test
        @DisplayName("Should activate inactive client")
        void shouldActivateInactiveClient() {
            // Create and deactivate a client first
            formPage.navigateToNew();

            String uniqueCode = "CLT-ACT-" + System.currentTimeMillis();
            String uniqueName = "Activate Test " + System.currentTimeMillis();

            formPage.fillCode(uniqueCode);
            formPage.fillName(uniqueName);
            formPage.clickSubmit();

            detailPage.clickDeactivateButton();
            detailPage.assertStatusText("Nonaktif");

            // Activate
            detailPage.clickActivateButton();

            // Should show active status
            detailPage.assertStatusText("Aktif");
            assertThat(detailPage.hasDeactivateButton()).isTrue();
        }
    }
}
