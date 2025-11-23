package com.artivisi.accountingfinance.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/templates")
public class JournalTemplateController {

    @GetMapping
    public String list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean favorites,
            Model model) {
        model.addAttribute("currentPage", "templates");
        model.addAttribute("selectedCategory", category);
        model.addAttribute("searchQuery", search);
        model.addAttribute("showFavorites", favorites);
        return "templates/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable String id, Model model) {
        model.addAttribute("currentPage", "templates");
        model.addAttribute("templateId", id);
        return "templates/detail";
    }

    @GetMapping("/new")
    public String create(Model model) {
        model.addAttribute("currentPage", "templates");
        model.addAttribute("isEdit", false);
        model.addAttribute("isDuplicate", false);
        return "templates/form";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable String id, Model model) {
        model.addAttribute("currentPage", "templates");
        model.addAttribute("isEdit", true);
        model.addAttribute("isDuplicate", false);
        model.addAttribute("templateId", id);
        return "templates/form";
    }

    @GetMapping("/{id}/duplicate")
    public String duplicate(@PathVariable String id, Model model) {
        model.addAttribute("currentPage", "templates");
        model.addAttribute("isEdit", false);
        model.addAttribute("isDuplicate", true);
        model.addAttribute("sourceTemplateId", id);
        return "templates/form";
    }
}
