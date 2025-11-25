package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.entity.Client;
import com.artivisi.accountingfinance.repository.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientService {

    private final ClientRepository clientRepository;

    public Client findById(UUID id) {
        return clientRepository.findByIdWithProjects(id)
                .orElseThrow(() -> new EntityNotFoundException("Client not found with id: " + id));
    }

    public Client findByCode(String code) {
        return clientRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Client not found with code: " + code));
    }

    public Page<Client> findAll(Pageable pageable) {
        return clientRepository.findAllByOrderByNameAsc(pageable);
    }

    public Page<Client> findByFilters(Boolean active, String search, Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return clientRepository.findByFiltersAndSearch(active, search, pageable);
        }
        return clientRepository.findByFilters(active, pageable);
    }

    public List<Client> findActiveClients() {
        return clientRepository.findByActiveTrue();
    }

    @Transactional
    public Client create(Client client) {
        if (clientRepository.existsByCode(client.getCode())) {
            throw new IllegalArgumentException("Client code already exists: " + client.getCode());
        }
        client.setActive(true);
        return clientRepository.save(client);
    }

    @Transactional
    public Client update(UUID id, Client updatedClient) {
        Client existing = findById(id);

        // Check if code is being changed and already exists
        if (!existing.getCode().equals(updatedClient.getCode()) &&
                clientRepository.existsByCode(updatedClient.getCode())) {
            throw new IllegalArgumentException("Client code already exists: " + updatedClient.getCode());
        }

        existing.setCode(updatedClient.getCode());
        existing.setName(updatedClient.getName());
        existing.setContactPerson(updatedClient.getContactPerson());
        existing.setEmail(updatedClient.getEmail());
        existing.setPhone(updatedClient.getPhone());
        existing.setAddress(updatedClient.getAddress());
        existing.setNotes(updatedClient.getNotes());

        return clientRepository.save(existing);
    }

    @Transactional
    public void deactivate(UUID id) {
        Client client = findById(id);
        client.setActive(false);
        clientRepository.save(client);
    }

    @Transactional
    public void activate(UUID id) {
        Client client = findById(id);
        client.setActive(true);
        clientRepository.save(client);
    }

    public long countActiveClients() {
        return clientRepository.countByActiveTrue();
    }
}
