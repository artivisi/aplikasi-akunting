package com.artivisi.accountingfinance.controller;

import com.artivisi.accountingfinance.entity.AssetCategory;
import com.artivisi.accountingfinance.entity.AssetStatus;
import com.artivisi.accountingfinance.entity.DepreciationEntry;
import com.artivisi.accountingfinance.entity.DepreciationMethod;
import com.artivisi.accountingfinance.entity.DisposalType;
import com.artivisi.accountingfinance.entity.FixedAsset;
import com.artivisi.accountingfinance.security.Permission;
import com.artivisi.accountingfinance.service.AssetCategoryService;
import com.artivisi.accountingfinance.service.FixedAssetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import static com.artivisi.accountingfinance.controller.ViewConstants.*;

@Controller
@RequestMapping("/assets")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('" + Permission.ASSET_VIEW + "')")
public class FixedAssetController {

    private final FixedAssetService fixedAssetService;
    private final AssetCategoryService assetCategoryService;

    @GetMapping
    public String list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) AssetStatus status,
            @RequestParam(required = false) UUID categoryId,
            @RequestHeader(value = "HX-Request", required = false) String hxRequest,
            @PageableDefault(size = 20) Pageable pageable,
            Model model) {

        Page<FixedAsset> assets = fixedAssetService.findByFilters(search, status, categoryId, pageable);

        model.addAttribute("assets", assets);
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("statuses", AssetStatus.values());
        model.addAttribute("categories", assetCategoryService.findAllActive());
        model.addAttribute(ATTR_CURRENT_PAGE, PAGE_ASSETS);

        // Summary stats
        model.addAttribute("totalBookValue", fixedAssetService.getTotalBookValue());
        model.addAttribute("totalPurchaseCost", fixedAssetService.getTotalPurchaseCost());
        model.addAttribute("totalAccumulatedDepreciation", fixedAssetService.getTotalAccumulatedDepreciation());
        model.addAttribute("pendingDepreciationCount", fixedAssetService.countPendingDepreciationEntries());

        if ("true".equals(hxRequest)) {
            return "assets/fragments/asset-table :: table";
        }

        return "assets/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAuthority('" + Permission.ASSET_CREATE + "')")
    public String newForm(Model model) {
        FixedAsset asset = new FixedAsset();
        asset.setDepreciationMethod(DepreciationMethod.STRAIGHT_LINE);
        asset.setResidualValue(BigDecimal.ZERO);
        asset.setPurchaseDate(LocalDate.now());
        asset.setDepreciationStartDate(LocalDate.now().withDayOfMonth(1));

        model.addAttribute("asset", asset);
        addFormAttributes(model);
        return "assets/form";
    }

    @PostMapping("/new")
    @PreAuthorize("hasAuthority('" + Permission.ASSET_CREATE + "')")
    public String create(
            @Valid @ModelAttribute("asset") FixedAsset asset,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            addFormAttributes(model);
            return "assets/form";
        }

        try {
            FixedAsset saved = fixedAssetService.create(asset);
            redirectAttributes.addFlashAttribute("successMessage", "Aset berhasil ditambahkan");
            return "redirect:/assets/" + saved.getId();
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Kode aset")) {
                bindingResult.rejectValue("assetCode", "duplicate", e.getMessage());
            } else {
                bindingResult.reject("error", e.getMessage());
            }
            addFormAttributes(model);
            return "assets/form";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, Model model) {
        FixedAsset asset = fixedAssetService.findByIdWithDetails(id);
        List<DepreciationEntry> depreciationHistory = fixedAssetService.getDepreciationHistory(id);

        model.addAttribute("asset", asset);
        model.addAttribute("depreciationHistory", depreciationHistory);
        model.addAttribute("monthlyDepreciation", fixedAssetService.calculateMonthlyDepreciation(asset));
        model.addAttribute(ATTR_CURRENT_PAGE, PAGE_ASSETS);
        return "assets/detail";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAuthority('" + Permission.ASSET_EDIT + "')")
    public String editForm(@PathVariable UUID id, Model model) {
        FixedAsset asset = fixedAssetService.findByIdWithDetails(id);
        model.addAttribute("asset", asset);
        addFormAttributes(model);
        return "assets/form";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permission.ASSET_EDIT + "')")
    public String update(
            @PathVariable UUID id,
            @Valid @ModelAttribute("asset") FixedAsset asset,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            asset.setId(id);
            addFormAttributes(model);
            return "assets/form";
        }

        try {
            fixedAssetService.update(id, asset);
            redirectAttributes.addFlashAttribute("successMessage", "Aset berhasil diperbarui");
            return "redirect:/assets/" + id;
        } catch (IllegalArgumentException | IllegalStateException e) {
            bindingResult.reject("error", e.getMessage());
            asset.setId(id);
            addFormAttributes(model);
            return "assets/form";
        }
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAuthority('" + Permission.ASSET_DELETE + "')")
    public String delete(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes) {

        try {
            fixedAssetService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Aset berhasil dihapus");
            return "redirect:/assets";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/assets/" + id;
        }
    }

    // Depreciation Management

    @GetMapping("/depreciation")
    @PreAuthorize("hasAuthority('" + Permission.ASSET_DEPRECIATE + "')")
    public String depreciationList(Model model) {
        List<DepreciationEntry> pendingEntries = fixedAssetService.getPendingDepreciationEntries();

        model.addAttribute("pendingEntries", pendingEntries);
        model.addAttribute(ATTR_CURRENT_PAGE, PAGE_ASSETS);
        return "assets/depreciation";
    }

    @PostMapping("/depreciation/generate")
    @PreAuthorize("hasAuthority('" + Permission.ASSET_DEPRECIATE + "')")
    public String generateDepreciation(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth period,
            RedirectAttributes redirectAttributes) {

        List<DepreciationEntry> entries = fixedAssetService.generateDepreciationEntries(period);

        if (entries.isEmpty()) {
            redirectAttributes.addFlashAttribute("infoMessage", "Tidak ada aset yang perlu disusutkan untuk periode " + period);
        } else {
            redirectAttributes.addFlashAttribute("successMessage",
                    "Berhasil generate " + entries.size() + " entri penyusutan untuk periode " + period);
        }
        return "redirect:/assets/depreciation";
    }

    @PostMapping("/depreciation/{entryId}/post")
    @PreAuthorize("hasAuthority('" + Permission.ASSET_DEPRECIATE + "')")
    public String postDepreciationEntry(
            @PathVariable UUID entryId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            DepreciationEntry entry = fixedAssetService.postDepreciationEntry(entryId, authentication.getName());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Penyusutan untuk " + entry.getFixedAsset().getName() + " berhasil di-posting");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/assets/depreciation";
    }

    @PostMapping("/depreciation/{entryId}/skip")
    @PreAuthorize("hasAuthority('" + Permission.ASSET_DEPRECIATE + "')")
    public String skipDepreciationEntry(
            @PathVariable UUID entryId,
            RedirectAttributes redirectAttributes) {

        fixedAssetService.skipDepreciationEntry(entryId);
        redirectAttributes.addFlashAttribute("successMessage", "Entri penyusutan di-skip");
        return "redirect:/assets/depreciation";
    }

    @PostMapping("/depreciation/post-all")
    @PreAuthorize("hasAuthority('" + Permission.ASSET_DEPRECIATE + "')")
    public String postAllDepreciation(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth period,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        int count = fixedAssetService.postAllPendingDepreciation(period, authentication.getName());

        if (count == 0) {
            redirectAttributes.addFlashAttribute("infoMessage", "Tidak ada entri penyusutan yang perlu di-posting");
        } else {
            redirectAttributes.addFlashAttribute("successMessage",
                    "Berhasil posting " + count + " entri penyusutan");
        }
        return "redirect:/assets/depreciation";
    }

    // Asset Disposal

    @GetMapping("/{id}/dispose")
    @PreAuthorize("hasAuthority('" + Permission.ASSET_DISPOSE + "')")
    public String disposeForm(@PathVariable UUID id, Model model) {
        FixedAsset asset = fixedAssetService.findByIdWithDetails(id);

        if (asset.isDisposed()) {
            return "redirect:/assets/" + id;
        }

        model.addAttribute("asset", asset);
        model.addAttribute("disposalTypes", DisposalType.values());
        model.addAttribute("disposalDate", LocalDate.now());
        model.addAttribute(ATTR_CURRENT_PAGE, PAGE_ASSETS);
        return "assets/dispose";
    }

    @PostMapping("/{id}/dispose")
    @PreAuthorize("hasAuthority('" + Permission.ASSET_DISPOSE + "')")
    public String dispose(
            @PathVariable UUID id,
            @RequestParam DisposalType disposalType,
            @RequestParam(required = false) BigDecimal proceeds,
            @RequestParam(required = false) String notes,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate disposalDate,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            FixedAsset disposed = fixedAssetService.disposeAsset(
                    id, disposalType, proceeds, notes, disposalDate, authentication.getName());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Aset " + disposed.getName() + " berhasil dilepas");
            return "redirect:/assets/" + id;
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/assets/" + id + "/dispose";
        }
    }

    private void addFormAttributes(Model model) {
        model.addAttribute("categories", assetCategoryService.findAllActive());
        model.addAttribute("depreciationMethods", DepreciationMethod.values());
        model.addAttribute(ATTR_CURRENT_PAGE, PAGE_ASSETS);
    }
}
