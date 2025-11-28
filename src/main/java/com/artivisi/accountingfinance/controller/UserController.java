package com.artivisi.accountingfinance.controller;

import com.artivisi.accountingfinance.entity.User;
import com.artivisi.accountingfinance.enums.Role;
import com.artivisi.accountingfinance.security.Permission;
import com.artivisi.accountingfinance.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasAuthority('" + Permission.USER_VIEW + "')")
public class UserController {

    private final UserService userService;

    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            Model model) {

        Page<User> users = userService.search(search,
                PageRequest.of(page, size, Sort.by("username")));

        model.addAttribute("users", users);
        model.addAttribute("search", search);
        model.addAttribute("roles", Role.values());

        return "users/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('" + Permission.USER_CREATE + "')")
    public String newForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", Role.values());
        model.addAttribute("selectedRoles", new HashSet<>());
        return "users/form";
    }

    @PostMapping
    @PreAuthorize("hasAuthority('" + Permission.USER_CREATE + "')")
    public String create(
            @Valid @ModelAttribute User user,
            BindingResult bindingResult,
            @RequestParam(value = "selectedRoles", required = false) String[] selectedRoleNames,
            @RequestParam String password,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", Role.values());
            model.addAttribute("selectedRoles", selectedRoleNames != null ?
                    Arrays.stream(selectedRoleNames).map(Role::valueOf).collect(Collectors.toSet()) :
                    new HashSet<>());
            return "users/form";
        }

        try {
            Set<Role> roles = selectedRoleNames != null ?
                    Arrays.stream(selectedRoleNames).map(Role::valueOf).collect(Collectors.toSet()) :
                    new HashSet<>();

            if (roles.isEmpty()) {
                model.addAttribute("errorMessage", "At least one role must be selected");
                model.addAttribute("roles", Role.values());
                model.addAttribute("selectedRoles", roles);
                return "users/form";
            }

            user.setPassword(password);
            userService.create(user, roles);
            redirectAttributes.addFlashAttribute("successMessage", "Pengguna berhasil dibuat: " + user.getUsername());
            return "redirect:/users";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("roles", Role.values());
            model.addAttribute("selectedRoles", selectedRoleNames != null ?
                    Arrays.stream(selectedRoleNames).map(Role::valueOf).collect(Collectors.toSet()) :
                    new HashSet<>());
            return "users/form";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, Model model) {
        User user = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        model.addAttribute("user", user);
        return "users/detail";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAuthority('" + Permission.USER_EDIT + "')")
    public String editForm(@PathVariable UUID id, Model model) {
        User user = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        model.addAttribute("selectedRoles", user.getRoles());
        return "users/form";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permission.USER_EDIT + "')")
    public String update(
            @PathVariable UUID id,
            @Valid @ModelAttribute User user,
            BindingResult bindingResult,
            @RequestParam(value = "selectedRoles", required = false) String[] selectedRoleNames,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", Role.values());
            model.addAttribute("selectedRoles", selectedRoleNames != null ?
                    Arrays.stream(selectedRoleNames).map(Role::valueOf).collect(Collectors.toSet()) :
                    new HashSet<>());
            return "users/form";
        }

        try {
            Set<Role> roles = selectedRoleNames != null ?
                    Arrays.stream(selectedRoleNames).map(Role::valueOf).collect(Collectors.toSet()) :
                    new HashSet<>();

            if (roles.isEmpty()) {
                model.addAttribute("errorMessage", "At least one role must be selected");
                model.addAttribute("roles", Role.values());
                model.addAttribute("selectedRoles", roles);
                return "users/form";
            }

            userService.update(id, user, roles);
            redirectAttributes.addFlashAttribute("successMessage", "Pengguna berhasil diperbarui: " + user.getUsername());
            return "redirect:/users/" + id;
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("roles", Role.values());
            model.addAttribute("selectedRoles", selectedRoleNames != null ?
                    Arrays.stream(selectedRoleNames).map(Role::valueOf).collect(Collectors.toSet()) :
                    new HashSet<>());
            return "users/form";
        }
    }

    @GetMapping("/{id}/change-password")
    @PreAuthorize("hasAuthority('" + Permission.USER_EDIT + "')")
    public String changePasswordForm(@PathVariable UUID id, Model model) {
        User user = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        model.addAttribute("user", user);
        return "users/change-password";
    }

    @PostMapping("/{id}/change-password")
    @PreAuthorize("hasAuthority('" + Permission.USER_EDIT + "')")
    public String changePassword(
            @PathVariable UUID id,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes,
            Model model) {

        User user = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("user", user);
            model.addAttribute("errorMessage", "Password tidak cocok");
            return "users/change-password";
        }

        if (newPassword.length() < 4) {
            model.addAttribute("user", user);
            model.addAttribute("errorMessage", "Password minimal 4 karakter");
            return "users/change-password";
        }

        userService.changePassword(id, newPassword);
        redirectAttributes.addFlashAttribute("successMessage", "Password berhasil diubah");
        return "redirect:/users/" + id;
    }

    @PostMapping("/{id}/toggle-active")
    @PreAuthorize("hasAuthority('" + Permission.USER_EDIT + "')")
    public String toggleActive(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        userService.toggleActive(id);
        redirectAttributes.addFlashAttribute("successMessage", "Status pengguna berhasil diubah");
        return "redirect:/users/" + id;
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAuthority('" + Permission.USER_DELETE + "')")
    public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            userService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Pengguna berhasil dihapus");
            return "redirect:/users";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/users/" + id;
        }
    }
}
