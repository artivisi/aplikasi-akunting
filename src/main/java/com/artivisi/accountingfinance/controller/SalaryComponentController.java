package com.artivisi.accountingfinance.controller;

import com.artivisi.accountingfinance.entity.SalaryComponent;
import com.artivisi.accountingfinance.entity.SalaryComponentType;
import com.artivisi.accountingfinance.service.SalaryComponentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
@RequestMapping("/salary-components")
@RequiredArgsConstructor
@org.springframework.security.access.prepost.PreAuthorize("hasAuthority('" + com.artivisi.accountingfinance.security.Permission.SALARY_COMPONENT_VIEW + "')")
public class SalaryComponentController {

    private final SalaryComponentService salaryComponentService;

    @GetMapping
    public String list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) SalaryComponentType type,
            @RequestParam(required = false) Boolean active,
            @RequestHeader(value = "HX-Request", required = false) String hxRequest,
            @PageableDefault(size = 20) Pageable pageable,
            Model model) {

        Page<SalaryComponent> components = salaryComponentService.findByFilters(search, type, active, pageable);

        model.addAttribute("components", components);
        model.addAttribute("search", search);
        model.addAttribute("type", type);
        model.addAttribute("active", active);
        model.addAttribute("componentTypes", SalaryComponentType.values());
        model.addAttribute("currentPage", "salary-components");

        if ("true".equals(hxRequest)) {
            return "salary-components/fragments/component-table :: table";
        }

        return "salary-components/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        SalaryComponent component = new SalaryComponent();
        component.setComponentType(SalaryComponentType.EARNING);
        component.setIsPercentage(false);
        component.setIsTaxable(true);

        model.addAttribute("component", component);
        model.addAttribute("componentTypes", SalaryComponentType.values());
        model.addAttribute("currentPage", "salary-components");
        return "salary-components/form";
    }

    @PostMapping("/new")
    public String create(
            @Valid @ModelAttribute("component") SalaryComponent component,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "salary-components/form";
        }

        try {
            SalaryComponent saved = salaryComponentService.create(component);
            redirectAttributes.addFlashAttribute("successMessage", "Komponen gaji berhasil ditambahkan");
            return "redirect:/salary-components/" + saved.getId();
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Kode")) {
                bindingResult.rejectValue("code", "duplicate", e.getMessage());
            } else {
                bindingResult.reject("error", e.getMessage());
            }
            addFormAttributes(model);
            return "salary-components/form";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, Model model) {
        SalaryComponent component = salaryComponentService.findById(id);
        model.addAttribute("component", component);
        model.addAttribute("currentPage", "salary-components");
        return "salary-components/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable UUID id, Model model) {
        SalaryComponent component = salaryComponentService.findById(id);
        model.addAttribute("component", component);
        addFormAttributes(model);
        return "salary-components/form";
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable UUID id,
            @Valid @ModelAttribute("component") SalaryComponent component,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            component.setId(id);
            addFormAttributes(model);
            return "salary-components/form";
        }

        try {
            salaryComponentService.update(id, component);
            redirectAttributes.addFlashAttribute("successMessage", "Komponen gaji berhasil diperbarui");
            return "redirect:/salary-components/" + id;
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Kode")) {
                bindingResult.rejectValue("code", "duplicate", e.getMessage());
            } else {
                bindingResult.reject("error", e.getMessage());
            }
            component.setId(id);
            addFormAttributes(model);
            return "salary-components/form";
        }
    }

    @PostMapping("/{id}/deactivate")
    public String deactivate(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes) {

        try {
            salaryComponentService.deactivate(id);
            redirectAttributes.addFlashAttribute("successMessage", "Komponen gaji berhasil dinonaktifkan");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/salary-components/" + id;
    }

    @PostMapping("/{id}/activate")
    public String activate(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes) {

        salaryComponentService.activate(id);
        redirectAttributes.addFlashAttribute("successMessage", "Komponen gaji berhasil diaktifkan");
        return "redirect:/salary-components/" + id;
    }

    private void addFormAttributes(Model model) {
        model.addAttribute("componentTypes", SalaryComponentType.values());
        model.addAttribute("currentPage", "salary-components");
    }
}
