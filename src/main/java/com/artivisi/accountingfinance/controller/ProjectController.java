package com.artivisi.accountingfinance.controller;

import com.artivisi.accountingfinance.entity.Project;
import com.artivisi.accountingfinance.enums.ProjectStatus;
import com.artivisi.accountingfinance.service.ClientService;
import com.artivisi.accountingfinance.service.ProjectService;
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
@RequestMapping("/projects")
@RequiredArgsConstructor
@org.springframework.security.access.prepost.PreAuthorize("hasAuthority('" + com.artivisi.accountingfinance.security.Permission.PROJECT_VIEW + "')")
public class ProjectController {

    private final ProjectService projectService;
    private final ClientService clientService;

    @GetMapping
    public String list(
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(required = false) UUID clientId,
            @RequestParam(required = false) String search,
            @RequestHeader(value = "HX-Request", required = false) String hxRequest,
            @PageableDefault(size = 20) Pageable pageable,
            Model model) {

        Page<Project> projects = projectService.findByFilters(status, clientId, search, pageable);

        model.addAttribute("projects", projects);
        model.addAttribute("status", status);
        model.addAttribute("clientId", clientId);
        model.addAttribute("search", search);
        model.addAttribute("statuses", ProjectStatus.values());
        model.addAttribute("clients", clientService.findActiveClients());
        model.addAttribute("currentPage", "projects");

        if ("true".equals(hxRequest)) {
            return "projects/fragments/project-table :: table";
        }

        return "projects/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("project", new Project());
        model.addAttribute("clients", clientService.findActiveClients());
        model.addAttribute("currentPage", "projects");
        return "projects/form";
    }

    @PostMapping("/new")
    public String create(
            @Valid @ModelAttribute("project") Project project,
            BindingResult bindingResult,
            @RequestParam(required = false) UUID clientId,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("clients", clientService.findActiveClients());
            model.addAttribute("currentPage", "projects");
            return "projects/form";
        }

        try {
            Project saved = projectService.create(project, clientId);
            redirectAttributes.addFlashAttribute("successMessage", "Proyek berhasil ditambahkan");
            return "redirect:/projects/" + saved.getId();
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("code", "duplicate", e.getMessage());
            model.addAttribute("clients", clientService.findActiveClients());
            model.addAttribute("currentPage", "projects");
            return "projects/form";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, Model model) {
        Project project = projectService.findById(id);
        model.addAttribute("project", project);
        model.addAttribute("currentPage", "projects");
        return "projects/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable UUID id, Model model) {
        Project project = projectService.findById(id);
        model.addAttribute("project", project);
        model.addAttribute("clients", clientService.findActiveClients());
        model.addAttribute("currentPage", "projects");
        return "projects/form";
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable UUID id,
            @Valid @ModelAttribute("project") Project project,
            BindingResult bindingResult,
            @RequestParam(required = false) UUID clientId,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            project.setId(id);
            model.addAttribute("clients", clientService.findActiveClients());
            model.addAttribute("currentPage", "projects");
            return "projects/form";
        }

        try {
            projectService.update(id, project, clientId);
            redirectAttributes.addFlashAttribute("successMessage", "Proyek berhasil diperbarui");
            return "redirect:/projects/" + id;
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("code", "duplicate", e.getMessage());
            project.setId(id);
            model.addAttribute("clients", clientService.findActiveClients());
            model.addAttribute("currentPage", "projects");
            return "projects/form";
        }
    }

    @PostMapping("/{id}/complete")
    public String complete(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes) {

        projectService.complete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Proyek berhasil diselesaikan");
        return "redirect:/projects/" + id;
    }

    @PostMapping("/{id}/archive")
    public String archive(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes) {

        projectService.archive(id);
        redirectAttributes.addFlashAttribute("successMessage", "Proyek berhasil diarsipkan");
        return "redirect:/projects/" + id;
    }

    @PostMapping("/{id}/reactivate")
    public String reactivate(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes) {

        projectService.reactivate(id);
        redirectAttributes.addFlashAttribute("successMessage", "Proyek berhasil diaktifkan kembali");
        return "redirect:/projects/" + id;
    }
}
