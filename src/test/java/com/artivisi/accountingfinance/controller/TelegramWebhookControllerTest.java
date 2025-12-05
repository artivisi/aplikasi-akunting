package com.artivisi.accountingfinance.controller;

import com.artivisi.accountingfinance.config.TelegramConfig;
import com.artivisi.accountingfinance.dto.telegram.TelegramUpdate;
import com.artivisi.accountingfinance.service.TelegramBotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Telegram Webhook Controller Tests")
class TelegramWebhookControllerTest {

    private static final String TEST_SECRET_TOKEN = "test-secret-token-12345";

    private MockMvc mockMvc;

    @Mock
    private TelegramBotService telegramBotService;

    @Mock
    private TelegramConfig telegramConfig;

    private TelegramConfig.Webhook webhookConfig;

    @BeforeEach
    void setUp() {
        webhookConfig = new TelegramConfig.Webhook();
        webhookConfig.setSecretToken(TEST_SECRET_TOKEN);
        when(telegramConfig.getWebhook()).thenReturn(webhookConfig);
        when(telegramConfig.isEnabled()).thenReturn(true);

        TelegramWebhookController controller = new TelegramWebhookController(telegramBotService, telegramConfig);
        controller.validateSecurityConfiguration();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("Should parse Telegram text message update")
    void shouldParseTextMessageUpdate() throws Exception {

        // Sample Telegram update JSON with text message
        String telegramJson = """
                {
                    "update_id": 123456789,
                    "message": {
                        "message_id": 1,
                        "from": {
                            "id": 123456,
                            "is_bot": false,
                            "first_name": "John",
                            "last_name": "Doe",
                            "username": "johndoe"
                        },
                        "chat": {
                            "id": 123456,
                            "first_name": "John",
                            "last_name": "Doe",
                            "username": "johndoe",
                            "type": "private"
                        },
                        "date": 1609459200,
                        "text": "/start"
                    }
                }
                """;

        mockMvc.perform(post("/api/telegram/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Telegram-Bot-Api-Secret-Token", TEST_SECRET_TOKEN)
                        .content(telegramJson))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        // Verify the service was called
        verify(telegramBotService).handleUpdate(any());
    }

    @Test
    @DisplayName("Should parse Telegram photo message update")
    void shouldParsePhotoMessageUpdate() throws Exception {

        // Sample Telegram update JSON with photo
        String telegramJson = """
                {
                    "update_id": 123456790,
                    "message": {
                        "message_id": 2,
                        "from": {
                            "id": 123456,
                            "is_bot": false,
                            "first_name": "John",
                            "last_name": "Doe",
                            "username": "johndoe"
                        },
                        "chat": {
                            "id": 123456,
                            "first_name": "John",
                            "last_name": "Doe",
                            "username": "johndoe",
                            "type": "private"
                        },
                        "date": 1609459200,
                        "photo": [
                            {
                                "file_id": "AgACAgUAAxkBAAIBGmYvZ...",
                                "file_unique_id": "AQADAgADr6cxG3gQ",
                                "file_size": 1234,
                                "width": 90,
                                "height": 60
                            },
                            {
                                "file_id": "AgACAgUAAxkBAAIBGmYvZ...",
                                "file_unique_id": "AQADAgADr6cxG3gQ",
                                "file_size": 12345,
                                "width": 320,
                                "height": 240
                            }
                        ],
                        "caption": "Here is my receipt"
                    }
                }
                """;

        mockMvc.perform(post("/api/telegram/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Telegram-Bot-Api-Secret-Token", TEST_SECRET_TOKEN)
                        .content(telegramJson))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        verify(telegramBotService).handleUpdate(any());
    }

    @Test
    @DisplayName("Should parse Telegram document message update")
    void shouldParseDocumentMessageUpdate() throws Exception {

        // Sample Telegram update JSON with document
        String telegramJson = """
                {
                    "update_id": 123456791,
                    "message": {
                        "message_id": 3,
                        "from": {
                            "id": 123456,
                            "is_bot": false,
                            "first_name": "John",
                            "username": "johndoe"
                        },
                        "chat": {
                            "id": 123456,
                            "type": "private"
                        },
                        "date": 1609459200,
                        "document": {
                            "file_name": "invoice.pdf",
                            "mime_type": "application/pdf",
                            "file_id": "BQACAgUAAxkBAAIBGmYvZ...",
                            "file_unique_id": "AgADAgADr6cxG",
                            "file_size": 54321
                        },
                        "caption": "My invoice"
                    }
                }
                """;

        mockMvc.perform(post("/api/telegram/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Telegram-Bot-Api-Secret-Token", TEST_SECRET_TOKEN)
                        .content(telegramJson))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        verify(telegramBotService).handleUpdate(any());
    }

    @Test
    @DisplayName("Should reject request with invalid secret token")
    void shouldRejectInvalidSecretToken() throws Exception {
        String telegramJson = """
                {
                    "update_id": 123456789,
                    "message": {
                        "message_id": 1,
                        "from": {
                            "id": 123456,
                            "is_bot": false,
                            "first_name": "John"
                        },
                        "chat": {
                            "id": 123456,
                            "type": "private"
                        },
                        "date": 1609459200,
                        "text": "/start"
                    }
                }
                """;

        mockMvc.perform(post("/api/telegram/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Telegram-Bot-Api-Secret-Token", "wrong-secret")
                        .content(telegramJson))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid secret token"));
    }

    @Test
    @DisplayName("Should reject request with missing secret token")
    void shouldRejectMissingSecretToken() throws Exception {
        String telegramJson = """
                {
                    "update_id": 123456789,
                    "message": {
                        "message_id": 1,
                        "from": {
                            "id": 123456,
                            "is_bot": false,
                            "first_name": "John"
                        },
                        "chat": {
                            "id": 123456,
                            "type": "private"
                        },
                        "date": 1609459200,
                        "text": "/start"
                    }
                }
                """;

        mockMvc.perform(post("/api/telegram/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(telegramJson))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid secret token"));
    }

    @Test
    @DisplayName("Should accept request with valid secret token")
    void shouldAcceptValidSecretToken() throws Exception {
        String telegramJson = """
                {
                    "update_id": 123456789,
                    "message": {
                        "message_id": 1,
                        "from": {
                            "id": 123456,
                            "is_bot": false,
                            "first_name": "John"
                        },
                        "chat": {
                            "id": 123456,
                            "type": "private"
                        },
                        "date": 1609459200,
                        "text": "/start"
                    }
                }
                """;

        mockMvc.perform(post("/api/telegram/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Telegram-Bot-Api-Secret-Token", TEST_SECRET_TOKEN)
                        .content(telegramJson))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));

        verify(telegramBotService).handleUpdate(any());
    }

    @Test
    @DisplayName("Should handle service exception gracefully")
    void shouldHandleServiceException() throws Exception {
        // Mock service to throw exception
        doThrow(new RuntimeException("Service error")).when(telegramBotService).handleUpdate(any());

        String telegramJson = """
                {
                    "update_id": 123456789,
                    "message": {
                        "message_id": 1,
                        "from": {
                            "id": 123456,
                            "is_bot": false,
                            "first_name": "John"
                        },
                        "chat": {
                            "id": 123456,
                            "type": "private"
                        },
                        "date": 1609459200,
                        "text": "/start"
                    }
                }
                """;

        // Should still return 200 to prevent Telegram from retrying
        mockMvc.perform(post("/api/telegram/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Telegram-Bot-Api-Secret-Token", TEST_SECRET_TOKEN)
                        .content(telegramJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Error handled"));
    }

    @Nested
    @DisplayName("Security Configuration Validation Tests")
    class SecurityConfigurationValidationTests {

        @Test
        @DisplayName("Should fail if secret token is null when Telegram is enabled")
        void shouldFailIfSecretTokenIsNull() {
            TelegramConfig.Webhook webhook = new TelegramConfig.Webhook();
            webhook.setSecretToken(null);

            TelegramConfig config = mock(TelegramConfig.class);
            when(config.isEnabled()).thenReturn(true);
            when(config.getWebhook()).thenReturn(webhook);

            TelegramWebhookController controller = new TelegramWebhookController(telegramBotService, config);

            assertThatThrownBy(controller::validateSecurityConfiguration)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("webhook secret token is not configured");
        }

        @Test
        @DisplayName("Should fail if secret token is blank when Telegram is enabled")
        void shouldFailIfSecretTokenIsBlank() {
            TelegramConfig.Webhook webhook = new TelegramConfig.Webhook();
            webhook.setSecretToken("   ");

            TelegramConfig config = mock(TelegramConfig.class);
            when(config.isEnabled()).thenReturn(true);
            when(config.getWebhook()).thenReturn(webhook);

            TelegramWebhookController controller = new TelegramWebhookController(telegramBotService, config);

            assertThatThrownBy(controller::validateSecurityConfiguration)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("webhook secret token is not configured");
        }

        @Test
        @DisplayName("Should not fail if Telegram is disabled")
        void shouldNotFailIfTelegramIsDisabled() {
            TelegramConfig.Webhook webhook = new TelegramConfig.Webhook();
            webhook.setSecretToken(null);

            TelegramConfig config = mock(TelegramConfig.class);
            when(config.isEnabled()).thenReturn(false);

            TelegramWebhookController controller = new TelegramWebhookController(telegramBotService, config);

            // Should not throw
            controller.validateSecurityConfiguration();
        }
    }
}
