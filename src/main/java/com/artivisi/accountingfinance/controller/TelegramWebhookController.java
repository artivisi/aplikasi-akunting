package com.artivisi.accountingfinance.controller;

import com.artivisi.accountingfinance.config.TelegramConfig;
import com.artivisi.accountingfinance.dto.telegram.TelegramUpdate;
import com.artivisi.accountingfinance.service.TelegramBotService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/telegram")
public class TelegramWebhookController {

    private static final Logger log = LoggerFactory.getLogger(TelegramWebhookController.class);

    private final TelegramBotService telegramBotService;
    private final TelegramConfig telegramConfig;

    public TelegramWebhookController(TelegramBotService telegramBotService, TelegramConfig telegramConfig) {
        this.telegramBotService = telegramBotService;
        this.telegramConfig = telegramConfig;
    }

    @PostConstruct
    public void validateSecurityConfiguration() {
        if (telegramConfig.isEnabled()) {
            String secretToken = telegramConfig.getWebhook().getSecretToken();
            if (secretToken == null || secretToken.isBlank()) {
                throw new IllegalStateException(
                    "Telegram bot is enabled but webhook secret token is not configured. " +
                    "Set telegram.bot.webhook.secret-token property for secure webhook authentication.");
            }
            log.info("Telegram webhook security: secret token configured");
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> onUpdate(
            @RequestBody TelegramUpdate update,
            @RequestHeader(value = "X-Telegram-Bot-Api-Secret-Token", required = false) String secretToken) {

        // Secret token is required when Telegram is enabled (validated at startup)
        String configuredSecret = telegramConfig.getWebhook().getSecretToken();
        if (secretToken == null || !secretToken.equals(configuredSecret)) {
            log.warn("Invalid or missing secret token in webhook request");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid secret token");
        }

        log.debug("Received Telegram update: {}", update.getUpdateId());

        try {
            telegramBotService.handleUpdate(update);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("Error handling Telegram update", e);
            // Telegram Bot API requires 200 response to acknowledge receipt
            // Returning non-200 causes Telegram to retry the webhook indefinitely
            // See: https://core.telegram.org/bots/api#setwebhook
            return ResponseEntity.status(HttpStatus.OK).body("Error handled");
        }
    }
}
