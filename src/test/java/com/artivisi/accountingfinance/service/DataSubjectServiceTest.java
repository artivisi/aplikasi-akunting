package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.TestcontainersConfiguration;
import com.artivisi.accountingfinance.entity.Employee;
import com.artivisi.accountingfinance.entity.EmploymentStatus;
import com.artivisi.accountingfinance.entity.EmploymentType;
import com.artivisi.accountingfinance.entity.PayrollDetail;
import com.artivisi.accountingfinance.entity.PayrollRun;
import com.artivisi.accountingfinance.entity.PayrollStatus;
import com.artivisi.accountingfinance.entity.PtkpStatus;
import com.artivisi.accountingfinance.entity.User;
import com.artivisi.accountingfinance.enums.Role;
import com.artivisi.accountingfinance.repository.EmployeeRepository;
import com.artivisi.accountingfinance.repository.PayrollDetailRepository;
import com.artivisi.accountingfinance.repository.PayrollRunRepository;
import com.artivisi.accountingfinance.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests for DataSubjectService.
 * Tests GDPR/UU PDP data subject rights implementation.
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
@DisplayName("DataSubjectService - GDPR/UU PDP Compliance Tests")
@WithMockUser(username = "testadmin", roles = {"ADMIN"})
class DataSubjectServiceTest {

    @Autowired
    private DataSubjectService dataSubjectService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PayrollRunRepository payrollRunRepository;

    @Autowired
    private PayrollDetailRepository payrollDetailRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        testEmployee = createTestEmployee();
    }

    private Employee createTestEmployee() {
        Employee employee = new Employee();
        employee.setEmployeeId("EMP-" + System.currentTimeMillis());
        employee.setName("Test Employee");
        employee.setEmail("test@example.com");
        employee.setPhone("08123456789");
        employee.setAddress("Jl. Test No. 123, Jakarta");
        employee.setNikKtp("3175012345678901");
        employee.setNpwp("12.345.678.9-012.000");
        employee.setBankName("BCA");
        employee.setBankAccountNumber("1234567890");
        employee.setBankAccountName("Test Employee");
        employee.setBpjsKesehatanNumber("0001234567890");
        employee.setBpjsKetenagakerjaanNumber("12345678901");
        employee.setHireDate(LocalDate.of(2020, 1, 1));
        employee.setJobTitle("Software Engineer");
        employee.setDepartment("IT");
        employee.setEmploymentType(EmploymentType.PERMANENT);
        employee.setEmploymentStatus(EmploymentStatus.ACTIVE);
        employee.setPtkpStatus(PtkpStatus.TK_0);
        employee.setActive(true);
        return employeeRepository.save(employee);
    }

    private User createTestUser() {
        User user = new User();
        user.setUsername("testuser" + System.currentTimeMillis());
        user.setPassword(passwordEncoder.encode("TestPass123!"));
        user.setFullName("Test User");
        user.setActive(true);
        user.setRoles(Set.of(Role.STAFF), "test");
        return userRepository.save(user);
    }

    private void createPayrollRecordForEmployee(Employee employee) {
        PayrollRun payrollRun = new PayrollRun();
        payrollRun.setPayrollPeriod("2024-01");
        payrollRun.setPeriodStart(LocalDate.of(2024, 1, 1));
        payrollRun.setPeriodEnd(LocalDate.of(2024, 1, 31));
        payrollRun.setStatus(PayrollStatus.POSTED);
        payrollRun.setTotalGross(BigDecimal.valueOf(10000000));
        payrollRun.setTotalDeductions(BigDecimal.valueOf(1000000));
        payrollRun.setTotalNetPay(BigDecimal.valueOf(9000000));
        payrollRun.setEmployeeCount(1);
        payrollRun = payrollRunRepository.save(payrollRun);

        PayrollDetail payrollDetail = new PayrollDetail();
        payrollDetail.setPayrollRun(payrollRun);
        payrollDetail.setEmployee(employee);
        payrollDetail.setGrossSalary(BigDecimal.valueOf(10000000));
        payrollDetail.setTotalDeductions(BigDecimal.valueOf(1000000));
        payrollDetail.setNetPay(BigDecimal.valueOf(9000000));
        payrollDetailRepository.save(payrollDetail);
    }

    @Nested
    @DisplayName("Right to Access (GDPR Art. 15)")
    class RightToAccessTests {

        @Test
        @DisplayName("Should export all personal data for employee")
        void shouldExportAllPersonalData() {
            Map<String, Object> exportedData = dataSubjectService.exportPersonalData(testEmployee.getId());

            assertThat(exportedData).isNotNull()
                .containsEntry("employee_id", testEmployee.getEmployeeId())
                .containsEntry("name", "Test Employee")
                .containsEntry("email", "test@example.com")
                .containsEntry("phone", "08123456789")
                .containsEntry("job_title", "Software Engineer")
                .containsEntry("department", "IT");
            assertThat(exportedData.get("export_timestamp")).isNotNull();
        }

        @Test
        @DisplayName("Should mask sensitive data in export")
        void shouldMaskSensitiveDataInExport() {
            Map<String, Object> exportedData = dataSubjectService.exportPersonalData(testEmployee.getId());

            // Sensitive fields should be masked (show first 2 and last 2 chars)
            String maskedNik = (String) exportedData.get("nik_ktp");
            String maskedNpwp = (String) exportedData.get("npwp");
            String maskedBankAccount = (String) exportedData.get("bank_account_number");

            assertThat(maskedNik).contains("****");
            assertThat(maskedNpwp).contains("****");
            assertThat(maskedBankAccount).contains("****");
        }

        @Test
        @DisplayName("Should throw exception for non-existent employee")
        void shouldThrowForNonExistentEmployee() {
            UUID invalidId = UUID.randomUUID();

            assertThatThrownBy(() -> dataSubjectService.exportPersonalData(invalidId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Employee not found");
        }
    }

    @Nested
    @DisplayName("Right to Erasure (GDPR Art. 17)")
    class RightToErasureTests {

        @Test
        @DisplayName("Should anonymize employee data")
        void shouldAnonymizeEmployeeData() {
            UUID employeeId = testEmployee.getId();
            String originalName = testEmployee.getName();

            dataSubjectService.anonymizeEmployee(employeeId, "Data subject request");

            Employee anonymized = employeeRepository.findById(employeeId).orElseThrow();

            // Check PII is anonymized
            assertThat(anonymized.getName()).startsWith("ANONYMIZED-");
            assertThat(anonymized.getName()).isNotEqualTo(originalName);
            assertThat(anonymized.getEmail()).isNull();
            assertThat(anonymized.getPhone()).isNull();
            assertThat(anonymized.getAddress()).isNull();
            assertThat(anonymized.getNikKtp()).isNull();
            assertThat(anonymized.getNpwp()).isNull();
            assertThat(anonymized.getBankName()).isNull();
            assertThat(anonymized.getBankAccountNumber()).isNull();
            assertThat(anonymized.getBankAccountName()).isNull();
            assertThat(anonymized.getBpjsKesehatanNumber()).isNull();
            assertThat(anonymized.getBpjsKetenagakerjaanNumber()).isNull();

            // Check employee is deactivated
            assertThat(anonymized.isActive()).isFalse();

            // Check notes contain anonymization record
            assertThat(anonymized.getNotes()).contains("Data anonymized per data subject request");
        }

        @Test
        @DisplayName("Should delete user without associated employee")
        void shouldDeleteUserWithoutEmployee() {
            User user = createTestUser();
            UUID userId = user.getId();

            dataSubjectService.deleteUser(userId, "Account closure request");

            assertThat(userRepository.findById(userId)).isEmpty();
        }

        @Test
        @DisplayName("Should reject deletion of user with associated employee")
        void shouldRejectDeletionOfUserWithEmployee() {
            // Create user and link to employee
            User user = createTestUser();
            testEmployee.setUser(user);
            employeeRepository.save(testEmployee);

            assertThatThrownBy(() -> dataSubjectService.deleteUser(user.getId(), "Test"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Cannot delete user with associated employee record");
        }

        @Test
        @DisplayName("Should throw exception for non-existent employee on anonymize")
        void shouldThrowForNonExistentEmployeeOnAnonymize() {
            UUID invalidId = UUID.randomUUID();

            assertThatThrownBy(() -> dataSubjectService.anonymizeEmployee(invalidId, "Test"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Employee not found");
        }
    }

    @Nested
    @DisplayName("Data Retention Status")
    class DataRetentionTests {

        @Test
        @DisplayName("Should return retention status for active employee")
        void shouldReturnRetentionStatusForActiveEmployee() {
            DataSubjectService.DataRetentionStatus status =
                    dataSubjectService.getRetentionStatus(testEmployee.getId());

            assertThat(status).isNotNull();
            assertThat(status.employeeId()).isEqualTo(testEmployee.getId());
            assertThat(status.retentionYears()).isEqualTo(10); // Indonesian tax law requirement
            assertThat(status.message()).isNotNull();
        }

        @Test
        @DisplayName("Should indicate retention required for employee with financial records")
        void shouldIndicateRetentionRequired() {
            // Create payroll records for the employee
            createPayrollRecordForEmployee(testEmployee);

            DataSubjectService.DataRetentionStatus status =
                    dataSubjectService.getRetentionStatus(testEmployee.getId());

            // Employee with payroll records requires retention
            assertThat(status.hasFinancialRecords()).isTrue();
            assertThat(status.message()).contains("retained");
        }

        @Test
        @DisplayName("Should allow deletion for employee without financial records")
        void shouldAllowDeletionWithoutFinancialRecords() {
            // Test employee has no payroll records
            DataSubjectService.DataRetentionStatus status =
                    dataSubjectService.getRetentionStatus(testEmployee.getId());

            // Employee without payroll records can be deleted
            assertThat(status.hasFinancialRecords()).isFalse();
            assertThat(status.canBeDeleted()).isTrue();
            assertThat(status.message()).contains("can be deleted");
        }

        @Test
        @DisplayName("Should calculate retention end date for resigned employee")
        void shouldCalculateRetentionEndDateForResignedEmployee() {
            testEmployee.setResignDate(LocalDate.of(2023, 6, 15));
            employeeRepository.save(testEmployee);

            DataSubjectService.DataRetentionStatus status =
                    dataSubjectService.getRetentionStatus(testEmployee.getId());

            assertThat(status.retentionEndDate()).isNotNull();
            // Retention should be 10 years after resign date
            assertThat(status.retentionEndDate().getYear()).isEqualTo(2033);
        }

        @Test
        @DisplayName("Should throw exception for non-existent employee")
        void shouldThrowForNonExistentEmployee() {
            UUID invalidId = UUID.randomUUID();

            assertThatThrownBy(() -> dataSubjectService.getRetentionStatus(invalidId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Employee not found");
        }
    }
}
