package com.artivisi.accountingfinance.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.artivisi.accountingfinance.controller.ViewConstants.*;

/**
 * Controller for the about page.
 * Uses git.properties generated at build time by git-commit-id-maven-plugin
 * instead of executing git commands at runtime (avoids command injection risk).
 */
@Controller
@RequestMapping("/about")
@Slf4j
public class AboutController {

    @Value("${git.commit.id.abbrev:unknown}")
    private String commitId;

    @Value("${git.tags:}")
    private String gitTags;

    @GetMapping
    public String about(Model model) {
        model.addAttribute("commitId", commitId);

        // Git tags may be empty if no tag matches
        String gitTag = (gitTags != null && !gitTags.isBlank()) ? gitTags : null;
        model.addAttribute("gitTag", gitTag);

        model.addAttribute(ATTR_CURRENT_PAGE, PAGE_ABOUT);
        return "about";
    }
}
