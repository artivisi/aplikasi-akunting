package com.artivisi.accountingfinance.controller;

import com.artivisi.accountingfinance.entity.Employee;
import com.artivisi.accountingfinance.entity.EmploymentStatus;
import com.artivisi.accountingfinance.entity.EmploymentType;
import com.artivisi.accountingfinance.entity.PtkpStatus;
import com.artivisi.accountingfinance.security.Permission;
import com.artivisi.accountingfinance.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/employees")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('" + Permission.EMPLOYEE_VIEW + "')")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public String list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) EmploymentStatus status,
            @RequestParam(required = false) Boolean active,
            @RequestHeader(value = "HX-Request", required = false) String hxRequest,
            @PageableDefault(size = 20) Pageable pageable,
            Model model) {

        Page<Employee> employees = employeeService.findByFilters(search, status, active, pageable);

        model.addAttribute("employees", employees);
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        model.addAttribute("active", active);
        model.addAttribute("employmentStatuses", EmploymentStatus.values());
        model.addAttribute("currentPage", "employees");

        if ("true".equals(hxRequest)) {
            return "employees/fragments/employee-table :: table";
        }

        return "employees/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('" + Permission.EMPLOYEE_CREATE + "')")
    public String newForm(Model model) {
        Employee employee = new Employee();
        employee.setEmploymentStatus(EmploymentStatus.ACTIVE);
        employee.setEmploymentType(EmploymentType.PERMANENT);
        employee.setPtkpStatus(PtkpStatus.TK_0);

        model.addAttribute("employee", employee);
        model.addAttribute("ptkpStatuses", PtkpStatus.values());
        model.addAttribute("employmentTypes", EmploymentType.values());
        model.addAttribute("employmentStatuses", EmploymentStatus.values());
        model.addAttribute("currentPage", "employees");
        return "employees/form";
    }

    @PostMapping("/new")
    @PreAuthorize("hasAuthority('" + Permission.EMPLOYEE_CREATE + "')")
    public String create(
            @Valid @ModelAttribute("employee") Employee employee,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "employees/form";
        }

        try {
            Employee saved = employeeService.create(employee);
            redirectAttributes.addFlashAttribute("successMessage", "Karyawan berhasil ditambahkan");
            return "redirect:/employees/" + saved.getId();
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("NIK")) {
                bindingResult.rejectValue("employeeId", "duplicate", e.getMessage());
            } else if (e.getMessage().contains("NPWP")) {
                bindingResult.rejectValue("npwp", "duplicate", e.getMessage());
            } else {
                bindingResult.reject("error", e.getMessage());
            }
            addFormAttributes(model);
            return "employees/form";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, Model model) {
        Employee employee = employeeService.findById(id);
        model.addAttribute("employee", employee);
        model.addAttribute("currentPage", "employees");
        return "employees/detail";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAuthority('" + Permission.EMPLOYEE_EDIT + "')")
    public String editForm(@PathVariable UUID id, Model model) {
        Employee employee = employeeService.findById(id);
        model.addAttribute("employee", employee);
        addFormAttributes(model);
        return "employees/form";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permission.EMPLOYEE_EDIT + "')")
    public String update(
            @PathVariable UUID id,
            @Valid @ModelAttribute("employee") Employee employee,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            employee.setId(id);
            addFormAttributes(model);
            return "employees/form";
        }

        try {
            employeeService.update(id, employee);
            redirectAttributes.addFlashAttribute("successMessage", "Karyawan berhasil diperbarui");
            return "redirect:/employees/" + id;
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("NIK")) {
                bindingResult.rejectValue("employeeId", "duplicate", e.getMessage());
            } else if (e.getMessage().contains("NPWP")) {
                bindingResult.rejectValue("npwp", "duplicate", e.getMessage());
            } else {
                bindingResult.reject("error", e.getMessage());
            }
            employee.setId(id);
            addFormAttributes(model);
            return "employees/form";
        }
    }

    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasAuthority('" + Permission.EMPLOYEE_EDIT + "')")
    public String deactivate(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes) {

        employeeService.deactivate(id);
        redirectAttributes.addFlashAttribute("successMessage", "Karyawan berhasil dinonaktifkan");
        return "redirect:/employees/" + id;
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('" + Permission.EMPLOYEE_EDIT + "')")
    public String activate(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes) {

        employeeService.activate(id);
        redirectAttributes.addFlashAttribute("successMessage", "Karyawan berhasil diaktifkan");
        return "redirect:/employees/" + id;
    }

    private void addFormAttributes(Model model) {
        model.addAttribute("ptkpStatuses", PtkpStatus.values());
        model.addAttribute("employmentTypes", EmploymentType.values());
        model.addAttribute("employmentStatuses", EmploymentStatus.values());
        model.addAttribute("currentPage", "employees");
    }
}
