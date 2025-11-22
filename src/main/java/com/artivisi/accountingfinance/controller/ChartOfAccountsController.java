package com.artivisi.accountingfinance.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/accounts")
public class ChartOfAccountsController {

    @GetMapping
    public String list(Model model) {
        model.addAttribute("currentPage", "accounts");
        return "accounts/list";
    }

    @GetMapping("/new")
    public String create(Model model) {
        model.addAttribute("currentPage", "accounts");
        model.addAttribute("isEdit", false);
        return "accounts/form";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable String id, Model model) {
        model.addAttribute("currentPage", "accounts");
        model.addAttribute("isEdit", true);
        model.addAttribute("accountId", id);
        return "accounts/form";
    }
}
