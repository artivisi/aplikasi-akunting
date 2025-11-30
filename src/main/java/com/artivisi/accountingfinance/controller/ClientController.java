package com.artivisi.accountingfinance.controller;

import com.artivisi.accountingfinance.entity.Client;
import com.artivisi.accountingfinance.service.ClientService;
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
@RequestMapping("/clients")
@RequiredArgsConstructor
@org.springframework.security.access.prepost.PreAuthorize("hasAuthority('" + com.artivisi.accountingfinance.security.Permission.CLIENT_VIEW + "')")
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    public String list(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String search,
            @RequestHeader(value = "HX-Request", required = false) String hxRequest,
            @PageableDefault(size = 20) Pageable pageable,
            Model model) {

        Page<Client> clients = clientService.findByFilters(active, search, pageable);

        model.addAttribute("clients", clients);
        model.addAttribute("active", active);
        model.addAttribute("search", search);
        model.addAttribute("currentPage", "clients");

        if ("true".equals(hxRequest)) {
            return "clients/fragments/client-table :: table";
        }

        return "clients/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("client", new Client());
        model.addAttribute("currentPage", "clients");
        return "clients/form";
    }

    @PostMapping("/new")
    public String create(
            @Valid @ModelAttribute("client") Client client,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("currentPage", "clients");
            return "clients/form";
        }

        try {
            Client saved = clientService.create(client);
            redirectAttributes.addFlashAttribute("successMessage", "Klien berhasil ditambahkan");
            return "redirect:/clients/" + saved.getId();
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("code", "duplicate", e.getMessage());
            model.addAttribute("currentPage", "clients");
            return "clients/form";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, Model model) {
        Client client = clientService.findById(id);
        model.addAttribute("client", client);
        model.addAttribute("currentPage", "clients");
        return "clients/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable UUID id, Model model) {
        Client client = clientService.findById(id);
        model.addAttribute("client", client);
        model.addAttribute("currentPage", "clients");
        return "clients/form";
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable UUID id,
            @Valid Client client,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            client.setId(id);
            model.addAttribute("currentPage", "clients");
            return "clients/form";
        }

        try {
            clientService.update(id, client);
            redirectAttributes.addFlashAttribute("successMessage", "Klien berhasil diperbarui");
            return "redirect:/clients/" + id;
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("code", "duplicate", e.getMessage());
            client.setId(id);
            model.addAttribute("currentPage", "clients");
            return "clients/form";
        }
    }

    @PostMapping("/{id}/deactivate")
    public String deactivate(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes) {

        clientService.deactivate(id);
        redirectAttributes.addFlashAttribute("successMessage", "Klien berhasil dinonaktifkan");
        return "redirect:/clients/" + id;
    }

    @PostMapping("/{id}/activate")
    public String activate(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes) {

        clientService.activate(id);
        redirectAttributes.addFlashAttribute("successMessage", "Klien berhasil diaktifkan");
        return "redirect:/clients/" + id;
    }
}
