package com.artivisi.accountingfinance.repository;

import com.artivisi.accountingfinance.entity.Employee;
import com.artivisi.accountingfinance.entity.EmployeeSalaryComponent;
import com.artivisi.accountingfinance.entity.SalaryComponent;
import com.artivisi.accountingfinance.entity.SalaryComponentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeSalaryComponentRepository extends JpaRepository<EmployeeSalaryComponent, UUID> {

    List<EmployeeSalaryComponent> findByEmployee(Employee employee);

    List<EmployeeSalaryComponent> findByEmployeeId(UUID employeeId);

    List<EmployeeSalaryComponent> findBySalaryComponent(SalaryComponent salaryComponent);

    Optional<EmployeeSalaryComponent> findByEmployeeAndSalaryComponent(Employee employee, SalaryComponent salaryComponent);

    boolean existsByEmployeeAndSalaryComponent(Employee employee, SalaryComponent salaryComponent);

    @Query("SELECT esc FROM EmployeeSalaryComponent esc " +
           "JOIN FETCH esc.salaryComponent sc " +
           "WHERE esc.employee.id = :employeeId " +
           "AND esc.effectiveDate <= :asOfDate " +
           "AND (esc.endDate IS NULL OR esc.endDate >= :asOfDate) " +
           "AND sc.active = true " +
           "ORDER BY sc.displayOrder ASC")
    List<EmployeeSalaryComponent> findActiveComponentsForEmployee(
            @Param("employeeId") UUID employeeId,
            @Param("asOfDate") LocalDate asOfDate);

    @Query("SELECT esc FROM EmployeeSalaryComponent esc " +
           "JOIN FETCH esc.salaryComponent sc " +
           "WHERE esc.employee.id = :employeeId " +
           "AND sc.componentType = :type " +
           "AND esc.effectiveDate <= :asOfDate " +
           "AND (esc.endDate IS NULL OR esc.endDate >= :asOfDate) " +
           "AND sc.active = true " +
           "ORDER BY sc.displayOrder ASC")
    List<EmployeeSalaryComponent> findActiveComponentsForEmployeeByType(
            @Param("employeeId") UUID employeeId,
            @Param("type") SalaryComponentType type,
            @Param("asOfDate") LocalDate asOfDate);

    @Query("SELECT esc FROM EmployeeSalaryComponent esc " +
           "JOIN FETCH esc.salaryComponent sc " +
           "JOIN FETCH esc.employee e " +
           "WHERE e.active = true " +
           "AND esc.effectiveDate <= :asOfDate " +
           "AND (esc.endDate IS NULL OR esc.endDate >= :asOfDate) " +
           "AND sc.active = true " +
           "ORDER BY e.employeeId, sc.displayOrder ASC")
    List<EmployeeSalaryComponent> findAllActiveComponentsForActiveEmployees(@Param("asOfDate") LocalDate asOfDate);

    void deleteByEmployee(Employee employee);

    void deleteBySalaryComponent(SalaryComponent salaryComponent);
}
