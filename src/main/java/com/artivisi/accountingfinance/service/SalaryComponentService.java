package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.entity.Employee;
import com.artivisi.accountingfinance.entity.EmployeeSalaryComponent;
import com.artivisi.accountingfinance.entity.SalaryComponent;
import com.artivisi.accountingfinance.entity.SalaryComponentType;
import com.artivisi.accountingfinance.repository.EmployeeSalaryComponentRepository;
import com.artivisi.accountingfinance.repository.SalaryComponentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SalaryComponentService {

    private final SalaryComponentRepository salaryComponentRepository;
    private final EmployeeSalaryComponentRepository employeeSalaryComponentRepository;

    // ==================== Salary Component CRUD ====================

    public SalaryComponent findById(UUID id) {
        return salaryComponentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Komponen gaji tidak ditemukan dengan id: " + id));
    }

    public SalaryComponent findByCode(String code) {
        return salaryComponentRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Komponen gaji tidak ditemukan dengan kode: " + code));
    }

    public Page<SalaryComponent> findByFilters(String search, SalaryComponentType type, Boolean active, Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return salaryComponentRepository.findByFiltersAndSearch(search, type, active, pageable);
        }
        return salaryComponentRepository.findByFilters(type, active, pageable);
    }

    public List<SalaryComponent> findAllActive() {
        return salaryComponentRepository.findByActiveTrueOrderByDisplayOrderAsc();
    }

    public List<SalaryComponent> findByTypeAndActive(SalaryComponentType type) {
        return salaryComponentRepository.findByComponentTypeAndActiveTrueOrderByDisplayOrderAsc(type);
    }

    @Transactional
    public SalaryComponent create(SalaryComponent component) {
        validateNewComponent(component);

        // Auto-assign display order if not set
        if (component.getDisplayOrder() == null || component.getDisplayOrder() == 0) {
            Integer maxOrder = salaryComponentRepository.findMaxDisplayOrder();
            component.setDisplayOrder(maxOrder != null ? maxOrder + 10 : 10);
        }

        component.setActive(true);
        return salaryComponentRepository.save(component);
    }

    @Transactional
    public SalaryComponent update(UUID id, SalaryComponent updatedComponent) {
        SalaryComponent existing = findById(id);

        if (existing.isSystemComponent()) {
            throw new IllegalArgumentException("Komponen sistem tidak dapat diubah");
        }

        validateUpdatedComponent(existing, updatedComponent);

        existing.setCode(updatedComponent.getCode());
        existing.setName(updatedComponent.getName());
        existing.setDescription(updatedComponent.getDescription());
        existing.setComponentType(updatedComponent.getComponentType());
        existing.setIsPercentage(updatedComponent.getIsPercentage());
        existing.setDefaultRate(updatedComponent.getDefaultRate());
        existing.setDefaultAmount(updatedComponent.getDefaultAmount());
        existing.setDisplayOrder(updatedComponent.getDisplayOrder());
        existing.setIsTaxable(updatedComponent.getIsTaxable());
        existing.setBpjsCategory(updatedComponent.getBpjsCategory());

        return salaryComponentRepository.save(existing);
    }

    @Transactional
    public void deactivate(UUID id) {
        SalaryComponent component = findById(id);
        if (component.isSystemComponent()) {
            throw new IllegalArgumentException("Komponen sistem tidak dapat dinonaktifkan");
        }
        component.setActive(false);
        salaryComponentRepository.save(component);
    }

    @Transactional
    public void activate(UUID id) {
        SalaryComponent component = findById(id);
        component.setActive(true);
        salaryComponentRepository.save(component);
    }

    public long countActive() {
        return salaryComponentRepository.countByActiveTrue();
    }

    private void validateNewComponent(SalaryComponent component) {
        if (salaryComponentRepository.existsByCode(component.getCode())) {
            throw new IllegalArgumentException("Kode komponen sudah digunakan: " + component.getCode());
        }
        validateComponentData(component);
    }

    private void validateUpdatedComponent(SalaryComponent existing, SalaryComponent updated) {
        if (!existing.getCode().equals(updated.getCode()) &&
                salaryComponentRepository.existsByCode(updated.getCode())) {
            throw new IllegalArgumentException("Kode komponen sudah digunakan: " + updated.getCode());
        }
        validateComponentData(updated);
    }

    private void validateComponentData(SalaryComponent component) {
        if (component.isPercentageBased() && component.getDefaultRate() == null) {
            throw new IllegalArgumentException("Komponen persentase harus memiliki nilai default rate");
        }
    }

    // ==================== Employee Salary Component ====================

    public List<EmployeeSalaryComponent> findByEmployee(UUID employeeId) {
        return employeeSalaryComponentRepository.findByEmployeeId(employeeId);
    }

    public List<EmployeeSalaryComponent> findActiveComponentsForEmployee(UUID employeeId, LocalDate asOfDate) {
        return employeeSalaryComponentRepository.findActiveComponentsForEmployee(employeeId, asOfDate);
    }

    public List<EmployeeSalaryComponent> findActiveEarningsForEmployee(UUID employeeId, LocalDate asOfDate) {
        return employeeSalaryComponentRepository.findActiveComponentsForEmployeeByType(
                employeeId, SalaryComponentType.EARNING, asOfDate);
    }

    public List<EmployeeSalaryComponent> findActiveDeductionsForEmployee(UUID employeeId, LocalDate asOfDate) {
        return employeeSalaryComponentRepository.findActiveComponentsForEmployeeByType(
                employeeId, SalaryComponentType.DEDUCTION, asOfDate);
    }

    public List<EmployeeSalaryComponent> findActiveCompanyContributionsForEmployee(UUID employeeId, LocalDate asOfDate) {
        return employeeSalaryComponentRepository.findActiveComponentsForEmployeeByType(
                employeeId, SalaryComponentType.COMPANY_CONTRIBUTION, asOfDate);
    }

    @Transactional
    public EmployeeSalaryComponent assignComponentToEmployee(Employee employee, SalaryComponent component,
                                                              LocalDate effectiveDate, java.math.BigDecimal rate,
                                                              java.math.BigDecimal amount, String notes) {
        // Check if already assigned
        if (employeeSalaryComponentRepository.existsByEmployeeAndSalaryComponent(employee, component)) {
            throw new IllegalArgumentException("Komponen " + component.getName() + " sudah ditugaskan ke karyawan ini");
        }

        EmployeeSalaryComponent esc = new EmployeeSalaryComponent();
        esc.setEmployee(employee);
        esc.setSalaryComponent(component);
        esc.setEffectiveDate(effectiveDate);
        esc.setRate(rate);
        esc.setAmount(amount);
        esc.setNotes(notes);

        return employeeSalaryComponentRepository.save(esc);
    }

    @Transactional
    public EmployeeSalaryComponent updateEmployeeComponent(UUID id, java.math.BigDecimal rate,
                                                           java.math.BigDecimal amount, LocalDate effectiveDate,
                                                           LocalDate endDate, String notes) {
        EmployeeSalaryComponent esc = employeeSalaryComponentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Komponen karyawan tidak ditemukan"));

        esc.setRate(rate);
        esc.setAmount(amount);
        esc.setEffectiveDate(effectiveDate);
        esc.setEndDate(endDate);
        esc.setNotes(notes);

        return employeeSalaryComponentRepository.save(esc);
    }

    @Transactional
    public void removeComponentFromEmployee(UUID employeeSalaryComponentId) {
        employeeSalaryComponentRepository.deleteById(employeeSalaryComponentId);
    }

    @Transactional
    public void endEmployeeComponent(UUID id, LocalDate endDate) {
        EmployeeSalaryComponent esc = employeeSalaryComponentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Komponen karyawan tidak ditemukan"));
        esc.setEndDate(endDate);
        employeeSalaryComponentRepository.save(esc);
    }
}
