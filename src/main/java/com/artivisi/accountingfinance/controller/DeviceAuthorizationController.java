package com.artivisi.accountingfinance.controller;

import com.artivisi.accountingfinance.entity.DeviceCode;
import com.artivisi.accountingfinance.entity.User;
import com.artivisi.accountingfinance.repository.UserRepository;
import com.artivisi.accountingfinance.security.LogSanitizer;
import com.artivisi.accountingfinance.service.DeviceAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * Web controller for device authorization (browser-based).
 * User visits this page to authorize a device using the user code.
 */
@Controller
@RequestMapping("/device")
@RequiredArgsConstructor
@Slf4j
public class DeviceAuthorizationController {

    private static final String VIEW_DEVICE_AUTHORIZE = "device-authorize";
    private static final String ATTR_ERROR = "error";
    private static final String ATTR_SUCCESS = "success";
    private static final String REDIRECT_DEVICE_CODE = "redirect:/device?code=";

    private final DeviceAuthService deviceAuthService;
    private final UserRepository userRepository;

    /**
     * Show device authorization page.
     * GET /device?code=XXXX-XXXX
     */
    @GetMapping
    public String showAuthorizationPage(
            @RequestParam(required = false) String code,
            Authentication authentication,
            Model model) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login?returnUrl=/device" +
                    (code != null ? "?code=" + code : "");
        }

        model.addAttribute("userCode", code != null ? code : "");
        model.addAttribute("username", authentication.getName());
        model.addAttribute("validCode", false);

        if (code == null || code.isBlank()) {
            return VIEW_DEVICE_AUTHORIZE;
        }

        Optional<DeviceCode> deviceCode = deviceAuthService.findByUserCode(code);

        if (deviceCode.isEmpty()) {
            model.addAttribute(ATTR_ERROR, "Kode perangkat tidak valid.");
            return VIEW_DEVICE_AUTHORIZE;
        }

        DeviceCode dc = deviceCode.get();

        if (dc.isExpired()) {
            model.addAttribute(ATTR_ERROR, "Kode perangkat telah kedaluwarsa. Silakan mulai lagi dari aplikasi.");
            return VIEW_DEVICE_AUTHORIZE;
        }

        if (dc.getStatus() == DeviceCode.Status.AUTHORIZED) {
            model.addAttribute(ATTR_SUCCESS, "Perangkat sudah diotorisasi sebelumnya.");
            return VIEW_DEVICE_AUTHORIZE;
        }

        model.addAttribute("clientId", dc.getClientId());
        model.addAttribute("validCode", true);
        return VIEW_DEVICE_AUTHORIZE;
    }

    /**
     * Authorize device.
     * POST /device/authorize
     */
    @PostMapping("/authorize")
    public String authorizeDevice(
            @RequestParam String userCode,
            @RequestParam(required = false) String deviceName,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        try {
            User user = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new IllegalStateException("User not found"));

            deviceAuthService.authorizeDevice(userCode, user);

            redirectAttributes.addFlashAttribute(ATTR_SUCCESS,
                    "Perangkat berhasil diotorisasi! Anda dapat kembali ke aplikasi.");

            log.info("User {} authorized device with code {}",
                    LogSanitizer.username(user.getUsername()),
                    LogSanitizer.sanitize(userCode));

            return "redirect:/device/success";

        } catch (IllegalArgumentException _) {
            redirectAttributes.addFlashAttribute(ATTR_ERROR, "Kode perangkat tidak valid.");
            return REDIRECT_DEVICE_CODE + userCode;

        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute(ATTR_ERROR, e.getMessage());
            return REDIRECT_DEVICE_CODE + userCode;
        }
    }

    /**
     * Show success page after authorization.
     */
    @GetMapping("/success")
    public String showSuccessPage() {
        return "device-success";
    }

    /**
     * Deny device authorization.
     * POST /device/deny
     */
    @PostMapping("/deny")
    public String denyDevice(
            @RequestParam String userCode,
            RedirectAttributes redirectAttributes) {

        try {
            Optional<DeviceCode> deviceCode = deviceAuthService.findByUserCode(userCode);

            if (deviceCode.isPresent()) {
                DeviceCode dc = deviceCode.get();
                dc.deny();

                redirectAttributes.addFlashAttribute(ATTR_SUCCESS,
                        "Otorisasi perangkat ditolak.");
            }

            return "redirect:/device/success";

        } catch (Exception _) {
            redirectAttributes.addFlashAttribute(ATTR_ERROR, "Gagal menolak otorisasi.");
            return REDIRECT_DEVICE_CODE + userCode;
        }
    }
}
