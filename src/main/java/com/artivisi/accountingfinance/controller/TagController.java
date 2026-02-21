package com.artivisi.accountingfinance.controller;

import com.artivisi.accountingfinance.entity.Tag;
import com.artivisi.accountingfinance.entity.TagType;
import com.artivisi.accountingfinance.security.Permission;
import com.artivisi.accountingfinance.service.TagService;
import com.artivisi.accountingfinance.service.TagTypeService;
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
@RequestMapping("/tags/types/{tagTypeId}/tags")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('" + Permission.TAG_VIEW + "')")
public class TagController {

    private static final String ATTR_SUCCESS_MESSAGE = "successMessage";
    private static final String VIEW_FORM = "tags/form";

    private final TagService tagService;
    private final TagTypeService tagTypeService;

    @GetMapping
    public String list(
            @PathVariable UUID tagTypeId,
            @RequestParam(required = false) String search,
            @RequestHeader(value = "HX-Request", required = false) String hxRequest,
            @PageableDefault(size = 20) Pageable pageable,
            Model model) {

        TagType tagType = tagTypeService.findById(tagTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Tipe label tidak ditemukan: " + tagTypeId));

        Page<Tag> tags = tagService.findByTagTypeAndSearch(tagTypeId, search, pageable);

        model.addAttribute("tagType", tagType);
        model.addAttribute("tags", tags);
        model.addAttribute("search", search);
        model.addAttribute(ATTR_CURRENT_PAGE, PAGE_TAGS);

        if ("true".equals(hxRequest)) {
            return "tags/fragments/tag-table :: table";
        }

        return "tags/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('" + Permission.TAG_CREATE + "')")
    public String newForm(@PathVariable UUID tagTypeId, Model model) {
        TagType tagType = tagTypeService.findById(tagTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Tipe label tidak ditemukan: " + tagTypeId));

        Tag tag = new Tag();
        tag.setTagType(tagType);
        tag.setActive(true);

        model.addAttribute("tagType", tagType);
        model.addAttribute("tag", tag);
        model.addAttribute(ATTR_CURRENT_PAGE, PAGE_TAGS);
        return VIEW_FORM;
    }

    @PostMapping("/new")
    @PreAuthorize("hasAuthority('" + Permission.TAG_CREATE + "')")
    public String create(
            @PathVariable UUID tagTypeId,
            @ModelAttribute("tag") Tag tag,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        TagType tagType = tagTypeService.findById(tagTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Tipe label tidak ditemukan: " + tagTypeId));

        tag.setTagType(tagType);

        // Validate code and name manually since @Valid would fail on tagType before we set it
        if (tag.getCode() == null || tag.getCode().isBlank()) {
            bindingResult.rejectValue("code", "NotBlank", "Kode label wajib diisi");
        }
        if (tag.getName() == null || tag.getName().isBlank()) {
            bindingResult.rejectValue("name", "NotBlank", "Nama label wajib diisi");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("tagType", tagType);
            model.addAttribute(ATTR_CURRENT_PAGE, PAGE_TAGS);
            return VIEW_FORM;
        }

        try {
            tagService.create(tag);
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS_MESSAGE, "Label berhasil ditambahkan");
            return "redirect:/tags/types/" + tagTypeId + "/tags";
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Kode")) {
                bindingResult.rejectValue("code", "duplicate", e.getMessage());
            } else {
                bindingResult.reject("error", e.getMessage());
            }
            model.addAttribute("tagType", tagType);
            model.addAttribute(ATTR_CURRENT_PAGE, PAGE_TAGS);
            return VIEW_FORM;
        }
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAuthority('" + Permission.TAG_EDIT + "')")
    public String editForm(@PathVariable UUID tagTypeId, @PathVariable UUID id, Model model) {
        TagType tagType = tagTypeService.findById(tagTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Tipe label tidak ditemukan: " + tagTypeId));

        Tag tag = tagService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Label tidak ditemukan: " + id));

        model.addAttribute("tagType", tagType);
        model.addAttribute("tag", tag);
        model.addAttribute(ATTR_CURRENT_PAGE, PAGE_TAGS);
        return VIEW_FORM;
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasAuthority('" + Permission.TAG_EDIT + "')")
    public String update(
            @PathVariable UUID tagTypeId,
            @PathVariable UUID id,
            @ModelAttribute("tag") Tag tag,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        TagType tagType = tagTypeService.findById(tagTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Tipe label tidak ditemukan: " + tagTypeId));

        // Validate manually
        if (tag.getCode() == null || tag.getCode().isBlank()) {
            bindingResult.rejectValue("code", "NotBlank", "Kode label wajib diisi");
        }
        if (tag.getName() == null || tag.getName().isBlank()) {
            bindingResult.rejectValue("name", "NotBlank", "Nama label wajib diisi");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("tagType", tagType);
            model.addAttribute(ATTR_CURRENT_PAGE, PAGE_TAGS);
            return VIEW_FORM;
        }

        try {
            tagService.update(id, tag);
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS_MESSAGE, "Label berhasil diubah");
            return "redirect:/tags/types/" + tagTypeId + "/tags";
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Kode")) {
                bindingResult.rejectValue("code", "duplicate", e.getMessage());
            } else {
                bindingResult.reject("error", e.getMessage());
            }
            model.addAttribute("tagType", tagType);
            model.addAttribute(ATTR_CURRENT_PAGE, PAGE_TAGS);
            return VIEW_FORM;
        }
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAuthority('" + Permission.TAG_DELETE + "')")
    public String delete(@PathVariable UUID tagTypeId, @PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            tagService.delete(id);
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS_MESSAGE, "Label berhasil dihapus");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/tags/types/" + tagTypeId + "/tags";
    }
}
