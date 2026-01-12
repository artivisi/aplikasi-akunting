package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.TestcontainersConfiguration;
import com.artivisi.accountingfinance.entity.Employee;
import com.artivisi.accountingfinance.entity.EmployeeSalaryComponent;
import com.artivisi.accountingfinance.entity.SalaryComponent;
import com.artivisi.accountingfinance.entity.SalaryComponentType;
import com.artivisi.accountingfinance.repository.EmployeeRepository;
import com.artivisi.accountingfinance.repository.SalaryComponentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for SalaryComponentService.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
@DisplayName("SalaryComponentService Integration Tests")
class SalaryComponentServiceTest {

    @Autowired
    private SalaryComponentService salaryComponentService;

    @Autowired
    private SalaryComponentRepository salaryComponentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    // Test employee ID from V907__employee_test_data.sql
    private static final UUID TEST_EMPLOYEE_ID = UUID.fromString("e0000000-0000-0000-0000-000000000001");

    private Employee testEmployee;

    @BeforeEach
    void setup() {
        testEmployee = employeeRepository.findById(TEST_EMPLOYEE_ID)
                .orElse(null);
    }

    @Nested
    @DisplayName("Find Salary Component Operations")
    class FindOperationsTests {

        @Test
        @DisplayName("Should throw exception for non-existent ID")
        void shouldThrowExceptionForNonExistentId() {
            UUID randomId = UUID.randomUUID();
            assertThatThrownBy(() -> salaryComponentService.findById(randomId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("tidak ditemukan");
        }

        @Test
        @DisplayName("Should throw exception for non-existent code")
        void shouldThrowExceptionForNonExistentCode() {
            assertThatThrownBy(() -> salaryComponentService.findByCode("NON-EXISTENT-CODE"))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("tidak ditemukan");
        }

        @Test
        @DisplayName("Should find all active salary components")
        void shouldFindAllActiveSalaryComponents() {
            List<SalaryComponent> activeComponents = salaryComponentService.findAllActive();
            assertThat(activeComponents).isNotNull();
        }

        @Test
        @DisplayName("Should find salary components by filters with null values")
        void shouldFindByFiltersWithNullValues() {
            Page<SalaryComponent> result = salaryComponentService.findByFilters(
                    null, null, null, PageRequest.of(0, 10));
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should find salary components by filters with search")
        void shouldFindByFiltersWithSearch() {
            Page<SalaryComponent> result = salaryComponentService.findByFilters(
                    "gaji", null, null, PageRequest.of(0, 10));
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should find salary components by filters with type EARNING")
        void shouldFindByFiltersWithTypeEarning() {
            Page<SalaryComponent> result = salaryComponentService.findByFilters(
                    null, SalaryComponentType.EARNING, null, PageRequest.of(0, 10));
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should find salary components by filters with type DEDUCTION")
        void shouldFindByFiltersWithTypeDeduction() {
            Page<SalaryComponent> result = salaryComponentService.findByFilters(
                    null, SalaryComponentType.DEDUCTION, null, PageRequest.of(0, 10));
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should find salary components by filters with active flag")
        void shouldFindByFiltersWithActiveFlag() {
            Page<SalaryComponent> result = salaryComponentService.findByFilters(
                    null, null, true, PageRequest.of(0, 10));
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should find by type and active")
        void shouldFindByTypeAndActive() {
            List<SalaryComponent> components = salaryComponentService.findByTypeAndActive(SalaryComponentType.EARNING);
            assertThat(components).isNotNull();
        }

        @Test
        @DisplayName("Should count active components")
        void shouldCountActiveComponents() {
            long count = salaryComponentService.countActive();
            assertThat(count).isGreaterThanOrEqualTo(0);
        }
    }

    @Nested
    @DisplayName("Create Salary Component Operations")
    class CreateOperationsTests {

        @Test
        @DisplayName("Should create salary component")
        void shouldCreateSalaryComponent() {
            SalaryComponent component = new SalaryComponent();
            component.setCode("TEST-COMP-001");
            component.setName("Test Component");
            component.setDescription("Test description");
            component.setComponentType(SalaryComponentType.EARNING);
            component.setIsPercentage(false);
            component.setDefaultAmount(new BigDecimal("500000"));
            component.setIsTaxable(true);

            SalaryComponent saved = salaryComponentService.create(component);

            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getCode()).isEqualTo("TEST-COMP-001");
            assertThat(saved.isActive()).isTrue();
            assertThat(saved.getDisplayOrder()).isNotNull();
        }

        @Test
        @DisplayName("Should reject duplicate code")
        void shouldRejectDuplicateCode() {
            // Create first component
            SalaryComponent comp1 = new SalaryComponent();
            comp1.setCode("TEST-DUPLICATE");
            comp1.setName("First Component");
            comp1.setComponentType(SalaryComponentType.EARNING);
            comp1.setIsPercentage(false);
            salaryComponentService.create(comp1);

            // Try to create second with same code
            SalaryComponent comp2 = new SalaryComponent();
            comp2.setCode("TEST-DUPLICATE");
            comp2.setName("Second Component");
            comp2.setComponentType(SalaryComponentType.EARNING);
            comp2.setIsPercentage(false);

            assertThatThrownBy(() -> salaryComponentService.create(comp2))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("sudah digunakan");
        }

        @Test
        @DisplayName("Should reject percentage component without default rate")
        void shouldRejectPercentageComponentWithoutDefaultRate() {
            SalaryComponent component = new SalaryComponent();
            component.setCode("TEST-PERCENT-NO-RATE");
            component.setName("Percentage Component");
            component.setComponentType(SalaryComponentType.EARNING);
            component.setIsPercentage(true);
            // No default rate set

            assertThatThrownBy(() -> salaryComponentService.create(component))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("harus memiliki nilai default rate");
        }

        @Test
        @DisplayName("Should create percentage component with default rate")
        void shouldCreatePercentageComponentWithDefaultRate() {
            SalaryComponent component = new SalaryComponent();
            component.setCode("TEST-PERCENT-WITH-RATE");
            component.setName("Percentage Component With Rate");
            component.setComponentType(SalaryComponentType.EARNING);
            component.setIsPercentage(true);
            component.setDefaultRate(new BigDecimal("0.05"));

            SalaryComponent saved = salaryComponentService.create(component);

            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getDefaultRate()).isEqualByComparingTo(new BigDecimal("0.05"));
        }
    }

    @Nested
    @DisplayName("Update Salary Component Operations")
    class UpdateOperationsTests {

        @Test
        @DisplayName("Should update salary component")
        void shouldUpdateSalaryComponent() {
            // Create a component
            SalaryComponent component = new SalaryComponent();
            component.setCode("TEST-UPDATE-001");
            component.setName("Original Name");
            component.setComponentType(SalaryComponentType.EARNING);
            component.setIsPercentage(false);
            SalaryComponent saved = salaryComponentService.create(component);

            // Update it
            saved.setName("Updated Name");
            saved.setDescription("Updated description");
            SalaryComponent updated = salaryComponentService.update(saved.getId(), saved);

            assertThat(updated.getName()).isEqualTo("Updated Name");
            assertThat(updated.getDescription()).isEqualTo("Updated description");
        }

        @Test
        @DisplayName("Should update salary component with same code")
        void shouldUpdateSalaryComponentWithSameCode() {
            // Create a component
            SalaryComponent component = new SalaryComponent();
            component.setCode("TEST-SAME-CODE");
            component.setName("Original Name");
            component.setComponentType(SalaryComponentType.EARNING);
            component.setIsPercentage(false);
            SalaryComponent saved = salaryComponentService.create(component);

            // Update with same code (should work)
            saved.setName("New Name");
            SalaryComponent updated = salaryComponentService.update(saved.getId(), saved);

            assertThat(updated.getCode()).isEqualTo("TEST-SAME-CODE");
            assertThat(updated.getName()).isEqualTo("New Name");
        }
    }

    @Nested
    @DisplayName("Activate/Deactivate Operations")
    class ActivateDeactivateTests {

        @Test
        @DisplayName("Should deactivate salary component")
        void shouldDeactivateSalaryComponent() {
            // Create a component
            SalaryComponent component = new SalaryComponent();
            component.setCode("TEST-DEACTIVATE");
            component.setName("To Deactivate");
            component.setComponentType(SalaryComponentType.EARNING);
            component.setIsPercentage(false);
            SalaryComponent saved = salaryComponentService.create(component);

            // Deactivate it
            salaryComponentService.deactivate(saved.getId());

            SalaryComponent found = salaryComponentService.findById(saved.getId());
            assertThat(found.isActive()).isFalse();
        }

        @Test
        @DisplayName("Should activate salary component")
        void shouldActivateSalaryComponent() {
            // Create a component
            SalaryComponent component = new SalaryComponent();
            component.setCode("TEST-ACTIVATE");
            component.setName("To Activate");
            component.setComponentType(SalaryComponentType.EARNING);
            component.setIsPercentage(false);
            SalaryComponent saved = salaryComponentService.create(component);

            // Deactivate then activate
            salaryComponentService.deactivate(saved.getId());
            salaryComponentService.activate(saved.getId());

            SalaryComponent found = salaryComponentService.findById(saved.getId());
            assertThat(found.isActive()).isTrue();
        }
    }

    @Nested
    @DisplayName("Employee Salary Component Operations")
    class EmployeeSalaryComponentTests {

        @Test
        @DisplayName("Should find components by employee")
        void shouldFindComponentsByEmployee() {
            if (testEmployee == null) return;

            List<EmployeeSalaryComponent> components = salaryComponentService.findByEmployee(testEmployee.getId());
            assertThat(components).isNotNull();
        }

        @Test
        @DisplayName("Should find active components for employee")
        void shouldFindActiveComponentsForEmployee() {
            if (testEmployee == null) return;

            List<EmployeeSalaryComponent> components = salaryComponentService.findActiveComponentsForEmployee(
                    testEmployee.getId(), LocalDate.now());
            assertThat(components).isNotNull();
        }

        @Test
        @DisplayName("Should find active earnings for employee")
        void shouldFindActiveEarningsForEmployee() {
            if (testEmployee == null) return;

            List<EmployeeSalaryComponent> earnings = salaryComponentService.findActiveEarningsForEmployee(
                    testEmployee.getId(), LocalDate.now());
            assertThat(earnings).isNotNull();
        }

        @Test
        @DisplayName("Should find active deductions for employee")
        void shouldFindActiveDeductionsForEmployee() {
            if (testEmployee == null) return;

            List<EmployeeSalaryComponent> deductions = salaryComponentService.findActiveDeductionsForEmployee(
                    testEmployee.getId(), LocalDate.now());
            assertThat(deductions).isNotNull();
        }

        @Test
        @DisplayName("Should find active company contributions for employee")
        void shouldFindActiveCompanyContributionsForEmployee() {
            if (testEmployee == null) return;

            List<EmployeeSalaryComponent> contributions = salaryComponentService.findActiveCompanyContributionsForEmployee(
                    testEmployee.getId(), LocalDate.now());
            assertThat(contributions).isNotNull();
        }

        @Test
        @DisplayName("Should assign component to employee")
        void shouldAssignComponentToEmployee() {
            if (testEmployee == null) return;

            // Create a salary component (code max 20 chars)
            SalaryComponent component = new SalaryComponent();
            component.setCode("TST-ASN-" + System.currentTimeMillis() % 10000);
            component.setName("Test Assignment Component");
            component.setComponentType(SalaryComponentType.EARNING);
            component.setIsPercentage(false);
            SalaryComponent saved = salaryComponentService.create(component);

            // Assign to employee
            EmployeeSalaryComponent assigned = salaryComponentService.assignComponentToEmployee(
                    testEmployee, saved, LocalDate.now(), null, new BigDecimal("500000"), "Test notes");

            assertThat(assigned.getId()).isNotNull();
            assertThat(assigned.getEmployee().getId()).isEqualTo(testEmployee.getId());
            assertThat(assigned.getSalaryComponent().getId()).isEqualTo(saved.getId());
        }

        @Test
        @DisplayName("Should reject duplicate assignment")
        void shouldRejectDuplicateAssignment() {
            if (testEmployee == null) return;

            // Create a salary component (code max 20 chars)
            SalaryComponent component = new SalaryComponent();
            component.setCode("TST-DUP-" + System.currentTimeMillis() % 10000);
            component.setName("Test Duplicate Assignment");
            component.setComponentType(SalaryComponentType.EARNING);
            component.setIsPercentage(false);
            SalaryComponent saved = salaryComponentService.create(component);

            // First assignment
            salaryComponentService.assignComponentToEmployee(
                    testEmployee, saved, LocalDate.now(), null, new BigDecimal("500000"), null);

            // Second assignment should fail
            assertThatThrownBy(() -> salaryComponentService.assignComponentToEmployee(
                    testEmployee, saved, LocalDate.now(), null, new BigDecimal("500000"), null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("sudah ditugaskan");
        }
    }
}
