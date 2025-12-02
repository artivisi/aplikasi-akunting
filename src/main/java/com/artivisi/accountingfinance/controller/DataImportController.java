package com.artivisi.accountingfinance.controller;

import com.artivisi.accountingfinance.service.DataImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/settings/import")
@RequiredArgsConstructor
@Slf4j
@org.springframework.security.access.prepost.PreAuthorize("hasAuthority('" + com.artivisi.accountingfinance.security.Permission.DATA_IMPORT + "')")
public class DataImportController {

    private static final String ATTR_ERROR_MESSAGE = "errorMessage";
    private static final String ATTR_SUCCESS_MESSAGE = "successMessage";
    private static final String ATTR_CURRENT_PAGE = "currentPage";

    private final DataImportService dataImportService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute(ATTR_CURRENT_PAGE, "settings");
        return "import/index";
    }

    @PostMapping
    public String importData(
            @RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, "File tidak boleh kosong");
            return "redirect:/settings/import";
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".zip")) {
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, "Format file tidak didukung. Gunakan file ZIP hasil ekspor.");
            return "redirect:/settings/import";
        }

        try {
            byte[] zipData = file.getBytes();
            DataImportService.ImportResult result = dataImportService.importAllData(zipData);

            String message = String.format(
                "Import berhasil: %d record data, %d dokumen dalam %d ms",
                result.totalRecords(),
                result.documentCount(),
                result.durationMs()
            );
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS_MESSAGE, message);
            log.info("Data import completed: {}", message);

            return "redirect:/settings/import";
        } catch (IOException e) {
            log.error("Error importing data: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, "Error import: " + e.getMessage());
            return "redirect:/settings/import";
        } catch (Exception e) {
            log.error("Unexpected error during import: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, "Error: " + e.getMessage());
            return "redirect:/settings/import";
        }
    }
}
