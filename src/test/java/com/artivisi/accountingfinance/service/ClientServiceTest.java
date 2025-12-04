package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.TestcontainersConfiguration;
import com.artivisi.accountingfinance.entity.Client;
import com.artivisi.accountingfinance.repository.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for ClientService.
 * Tests actual database queries and business logic.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
@DisplayName("ClientService Integration Tests")
class ClientServiceTest {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientRepository clientRepository;

    @Nested
    @DisplayName("Find Operations")
    class FindOperationsTests {

        @Test
        @DisplayName("findById should return client with correct data")
        void findByIdShouldReturnClient() {
            Client client = createTestClient();

            Client found = clientService.findById(client.getId());

            assertThat(found).isNotNull();
            assertThat(found.getId()).isEqualTo(client.getId());
            assertThat(found.getCode()).isEqualTo(client.getCode());
            assertThat(found.getName()).isEqualTo(client.getName());
        }

        @Test
        @DisplayName("findById should throw EntityNotFoundException for invalid ID")
        void findByIdShouldThrowForInvalidId() {
            UUID invalidId = UUID.randomUUID();

            assertThatThrownBy(() -> clientService.findById(invalidId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Client not found with id");
        }

        @Test
        @DisplayName("findByCode should return correct client")
        void findByCodeShouldReturnClient() {
            Client client = createTestClient();

            Client found = clientService.findByCode(client.getCode());

            assertThat(found).isNotNull();
            assertThat(found.getCode()).isEqualTo(client.getCode());
        }

        @Test
        @DisplayName("findByCode should throw for invalid code")
        void findByCodeShouldThrowForInvalidCode() {
            assertThatThrownBy(() -> clientService.findByCode("INVALID-CODE"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Client not found with code");
        }

        @Test
        @DisplayName("findAll should return paginated results")
        void findAllShouldReturnPaginatedResults() {
            createTestClient();
            createTestClient();

            Page<Client> page = clientService.findAll(PageRequest.of(0, 10));

            assertThat(page.getContent()).isNotEmpty();
            assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(2);
        }

        @Test
        @DisplayName("findByFilters should filter by active status")
        void findByFiltersShouldFilterByActiveStatus() {
            Client active = createTestClient();
            Client inactive = createTestClient();
            clientService.deactivate(inactive.getId());

            Page<Client> activePage = clientService.findByFilters(true, null, PageRequest.of(0, 10));

            assertThat(activePage.getContent()).isNotEmpty().allMatch(Client::isActive);
        }

        @Test
        @DisplayName("findByFilters should search by name")
        void findByFiltersShouldSearchByName() {
            Client client = buildTestClient();
            client.setName("Unique Client Name XYZ");
            clientService.create(client);

            Page<Client> page = clientService.findByFilters(null, "Unique", PageRequest.of(0, 10));

            assertThat(page.getContent()).isNotEmpty();
            assertThat(page.getContent()).anyMatch(c -> c.getName().contains("Unique"));
        }

        @Test
        @DisplayName("findActiveClients should return only active clients")
        void findActiveClientsShouldReturnOnlyActive() {
            Client active = createTestClient();
            Client inactive = createTestClient();
            clientService.deactivate(inactive.getId());

            List<Client> activeClients = clientService.findActiveClients();

            assertThat(activeClients).allMatch(Client::isActive);
            assertThat(activeClients).anyMatch(c -> c.getId().equals(active.getId()));
            assertThat(activeClients).noneMatch(c -> c.getId().equals(inactive.getId()));
        }
    }

    @Nested
    @DisplayName("Create Client")
    class CreateClientTests {

        @Test
        @DisplayName("create should save client with active status")
        void createShouldSaveWithActiveStatus() {
            Client client = buildTestClient();

            Client saved = clientService.create(client);

            assertThat(saved.getId()).isNotNull();
            assertThat(saved.isActive()).isTrue();
        }

        @Test
        @DisplayName("create should persist all fields correctly")
        void createShouldPersistAllFields() {
            Client client = buildTestClient();
            client.setContactPerson("John Doe");
            client.setEmail("john@example.com");
            client.setPhone("081234567890");
            client.setAddress("Jl. Test No. 123");
            client.setNotes("Test notes");
            client.setNpwp("12.345.678.9-012.345");
            client.setNitku("1234567890123456789012");
            client.setNik("1234567890123456");

            Client saved = clientService.create(client);

            Client retrieved = clientRepository.findById(saved.getId()).orElseThrow();
            assertThat(retrieved.getContactPerson()).isEqualTo("John Doe");
            assertThat(retrieved.getEmail()).isEqualTo("john@example.com");
            assertThat(retrieved.getPhone()).isEqualTo("081234567890");
            assertThat(retrieved.getAddress()).isEqualTo("Jl. Test No. 123");
            assertThat(retrieved.getNpwp()).isEqualTo("12.345.678.9-012.345");
        }

        @Test
        @DisplayName("create should throw for duplicate code")
        void createShouldThrowForDuplicateCode() {
            Client first = createTestClient();

            Client second = buildTestClient();
            second.setCode(first.getCode());

            assertThatThrownBy(() -> clientService.create(second))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Client code already exists");
        }
    }

    @Nested
    @DisplayName("Update Client")
    class UpdateClientTests {

        @Test
        @DisplayName("update should modify client fields")
        void updateShouldModifyFields() {
            Client client = createTestClient();

            Client updateData = buildUpdateData(client);
            updateData.setName("Updated Name");
            updateData.setContactPerson("Jane Doe");
            updateData.setEmail("jane@example.com");

            Client updated = clientService.update(client.getId(), updateData);

            assertThat(updated.getName()).isEqualTo("Updated Name");
            assertThat(updated.getContactPerson()).isEqualTo("Jane Doe");
            assertThat(updated.getEmail()).isEqualTo("jane@example.com");
        }

        @Test
        @DisplayName("update should allow changing code if not duplicate")
        void updateShouldAllowChangingCode() {
            Client client = createTestClient();
            String newCode = "NEW-" + System.nanoTime();

            Client updateData = buildUpdateData(client);
            updateData.setCode(newCode);

            Client updated = clientService.update(client.getId(), updateData);

            assertThat(updated.getCode()).isEqualTo(newCode);
        }

        @Test
        @DisplayName("update should throw for duplicate code")
        void updateShouldThrowForDuplicateCode() {
            Client first = createTestClient();
            Client second = createTestClient();

            Client updateData = buildUpdateData(second);
            updateData.setCode(first.getCode());

            assertThatThrownBy(() -> clientService.update(second.getId(), updateData))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Client code already exists");
        }

        @Test
        @DisplayName("update should throw for non-existent client")
        void updateShouldThrowForNonExistentClient() {
            UUID invalidId = UUID.randomUUID();
            Client updateData = buildTestClient();

            assertThatThrownBy(() -> clientService.update(invalidId, updateData))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Client not found");
        }
    }

    @Nested
    @DisplayName("Activate/Deactivate Client")
    class ActivateDeactivateTests {

        @Test
        @DisplayName("deactivate should set active to false")
        void deactivateShouldSetActiveFalse() {
            Client client = createTestClient();
            assertThat(client.isActive()).isTrue();

            clientService.deactivate(client.getId());

            Client deactivated = clientRepository.findById(client.getId()).orElseThrow();
            assertThat(deactivated.isActive()).isFalse();
        }

        @Test
        @DisplayName("activate should set active to true")
        void activateShouldSetActiveTrue() {
            Client client = createTestClient();
            clientService.deactivate(client.getId());

            clientService.activate(client.getId());

            Client activated = clientRepository.findById(client.getId()).orElseThrow();
            assertThat(activated.isActive()).isTrue();
        }

        @Test
        @DisplayName("deactivate should throw for non-existent client")
        void deactivateShouldThrowForNonExistent() {
            UUID invalidId = UUID.randomUUID();

            assertThatThrownBy(() -> clientService.deactivate(invalidId))
                .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("activate should throw for non-existent client")
        void activateShouldThrowForNonExistent() {
            UUID invalidId = UUID.randomUUID();

            assertThatThrownBy(() -> clientService.activate(invalidId))
                .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Count Operations")
    class CountOperationsTests {

        @Test
        @DisplayName("countActiveClients should return correct count")
        void countActiveClientsShouldReturnCorrectCount() {
            long initialCount = clientService.countActiveClients();

            createTestClient();
            createTestClient();
            Client inactive = createTestClient();
            clientService.deactivate(inactive.getId());

            long newCount = clientService.countActiveClients();

            assertThat(newCount).isEqualTo(initialCount + 2);
        }
    }

    // Helper methods

    private Client createTestClient() {
        Client client = buildTestClient();
        return clientService.create(client);
    }

    private Client buildTestClient() {
        Client client = new Client();
        client.setCode("CLI-" + System.nanoTime());
        client.setName("Test Client " + System.currentTimeMillis());
        return client;
    }

    private Client buildUpdateData(Client existing) {
        Client updateData = new Client();
        updateData.setCode(existing.getCode());
        updateData.setName(existing.getName());
        updateData.setContactPerson(existing.getContactPerson());
        updateData.setEmail(existing.getEmail());
        updateData.setPhone(existing.getPhone());
        updateData.setAddress(existing.getAddress());
        updateData.setNotes(existing.getNotes());
        return updateData;
    }
}
