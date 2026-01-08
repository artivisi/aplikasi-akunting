package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.entity.Client;
import com.artivisi.accountingfinance.entity.Project;
import com.artivisi.accountingfinance.enums.ProjectStatus;
import com.artivisi.accountingfinance.repository.ClientRepository;
import com.artivisi.accountingfinance.repository.ProjectRepository;
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
public class ProjectService {

    private static final String ERR_PROJECT_NOT_FOUND = "Project not found with id: ";

    private final ProjectRepository projectRepository;
    private final ClientRepository clientRepository;

    public Project findById(UUID id) {
        return projectRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException(ERR_PROJECT_NOT_FOUND + id));
    }

    public Project findByCode(String code) {
        return projectRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with code: " + code));
    }

    public Page<Project> findAll(Pageable pageable) {
        return projectRepository.findAllByOrderByCodeAsc(pageable);
    }

    public Page<Project> findByFilters(ProjectStatus status, UUID clientId, String search, Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return projectRepository.findByFiltersAndSearch(status, clientId, search, pageable);
        }
        return projectRepository.findByFilters(status, clientId, pageable);
    }

    public List<Project> findByClientId(UUID clientId) {
        return projectRepository.findByClientId(clientId);
    }

    public List<Project> findActiveProjects() {
        return projectRepository.findByStatus(ProjectStatus.ACTIVE);
    }

    @Transactional
    public Project create(Project project, UUID clientId) {
        if (projectRepository.existsByCode(project.getCode())) {
            throw new IllegalArgumentException("Project code already exists: " + project.getCode());
        }

        if (clientId != null) {
            Client client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new EntityNotFoundException("Client not found with id: " + clientId));
            project.setClient(client);
        }

        project.setStatus(ProjectStatus.ACTIVE);
        return projectRepository.save(project);
    }

    @Transactional
    public Project update(UUID id, Project updatedProject, UUID clientId) {
        Project existing = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ERR_PROJECT_NOT_FOUND + id));

        // Check if code is being changed and already exists
        if (!existing.getCode().equals(updatedProject.getCode()) &&
                projectRepository.existsByCode(updatedProject.getCode())) {
            throw new IllegalArgumentException("Project code already exists: " + updatedProject.getCode());
        }

        if (clientId != null) {
            Client client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new EntityNotFoundException("Client not found with id: " + clientId));
            existing.setClient(client);
        } else {
            existing.setClient(null);
        }

        existing.setCode(updatedProject.getCode());
        existing.setName(updatedProject.getName());
        existing.setDescription(updatedProject.getDescription());
        existing.setContractValue(updatedProject.getContractValue());
        existing.setBudgetAmount(updatedProject.getBudgetAmount());
        existing.setStartDate(updatedProject.getStartDate());
        existing.setEndDate(updatedProject.getEndDate());

        return projectRepository.save(existing);
    }

    @Transactional
    public void updateStatus(UUID id, ProjectStatus newStatus) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ERR_PROJECT_NOT_FOUND + id));
        project.setStatus(newStatus);
        projectRepository.save(project);
    }

    @Transactional
    public void archive(UUID id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ERR_PROJECT_NOT_FOUND + id));
        project.setStatus(ProjectStatus.ARCHIVED);
        projectRepository.save(project);
    }

    @Transactional
    public void complete(UUID id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ERR_PROJECT_NOT_FOUND + id));
        project.setStatus(ProjectStatus.COMPLETED);
        projectRepository.save(project);
    }

    @Transactional
    public void reactivate(UUID id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ERR_PROJECT_NOT_FOUND + id));
        project.setStatus(ProjectStatus.ACTIVE);
        projectRepository.save(project);
    }

    public long countByStatus(ProjectStatus status) {
        return projectRepository.countByStatus(status);
    }

    public long countActiveProjects() {
        return countByStatus(ProjectStatus.ACTIVE);
    }
}
