package com.artivisi.accountingfinance.controller;

import com.artivisi.accountingfinance.entity.TagType;
import com.artivisi.accountingfinance.security.Permission;
import com.artivisi.accountingfinance.service.TagTypeService;
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

import static com.artivisi.accountingfinance.controller.ViewConstants.*;

@Controller
@RequestMapping("/tags/types")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('" + Permission.TAG_VIEW + "')")
public class TagTypeController {

    private static final String ATTR_SUCCESS_MESSAGE = "successMessage";
    private static final String REDIRECT_TAG_TYPES = "redirect:/tags/types";
    private static final String VIEW_FORM = "tags/types/form";

    private final TagTypeService tagTypeService;

    @GetMapping
    public String list(
            @RequestParam(required = false) String search,
            @RequestHeader(value = "HX-Request", required = false) String hxRequest,
            @PageableDefault(size = 20) Pageable pageable,
            Model model) {

        Page<TagType> tagTypes = tagTypeService.findBySearch(search, pageable);

        model.addAttribute("tagTypes", tagTypes);
        model.addAttribute("search", search);
        model.addAttribute(ATTR_CURRENT_PAGE, PAGE_TAG_TYPES);

        if ("true".equals(hxRequest)) {
            return "tags/types/fragments/tag-type-table :: table";
        }

        return "tags/types/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('" + Permission.TAG_CREATE + "')")
    public String newForm(Model model) {
        TagType tagType = new TagType();
        tagType.setActive(true);

        model.addAttribute("tagType", tagType);
        model.addAttribute(ATTR_CURRENT_PAGE, PAGE_TAG_TYPES);
        return VIEW_FORM;
    }

    @PostMapping("/new")
    @PreAuthorize("hasAuthority('" + Permission.TAG_CREATE + "')")
    public String create(
            @Valid @ModelAttribute("tagType") TagType tagType,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute(ATTR_CURRENT_PAGE, PAGE_TAG_TYPES);
            return VIEW_FORM;
        }

        try {
            tagTypeService.create(tagType);
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS_MESSAGE, "Tipe label berhasil ditambahkan");
            return REDIRECT_TAG_TYPES;
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Kode")) {
                bindingResult.rejectValue("code", "duplicate", e.getMessage());
            } else {
                bindingResult.reject("error", e.getMessage());
            }
            model.addAttribute(ATTR_CURRENT_PAGE, PAGE_TAG_TYPES);
            return VIEW_FORM;
        }
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAuthority('" + Permission.TAG_EDIT + "')")
    public String editForm(@PathVariable UUID id, Model model) {
        TagType tagType = tagTypeService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tipe label tidak ditemukan: " + id));

        model.addAttribute("tagType", tagType);
        model.addAttribute(ATTR_CURRENT_PAGE, PAGE_TAG_TYPES);
        return VIEW_FORM;
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasAuthority('" + Permission.TAG_EDIT + "')")
    public String update(
            @PathVariable UUID id,
            @Valid @ModelAttribute("tagType") TagType tagType,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute(ATTR_CURRENT_PAGE, PAGE_TAG_TYPES);
            return VIEW_FORM;
        }

        try {
            tagTypeService.update(id, tagType);
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS_MESSAGE, "Tipe label berhasil diubah");
            return REDIRECT_TAG_TYPES;
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Kode")) {
                bindingResult.rejectValue("code", "duplicate", e.getMessage());
            } else {
                bindingResult.reject("error", e.getMessage());
            }
            model.addAttribute(ATTR_CURRENT_PAGE, PAGE_TAG_TYPES);
            return VIEW_FORM;
        }
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAuthority('" + Permission.TAG_DELETE + "')")
    public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            tagTypeService.delete(id);
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS_MESSAGE, "Tipe label berhasil dihapus");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return REDIRECT_TAG_TYPES;
    }
}
