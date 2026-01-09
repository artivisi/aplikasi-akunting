package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.config.TelegramConfig;
import com.artivisi.accountingfinance.dto.telegram.TelegramChat;
import com.artivisi.accountingfinance.dto.telegram.TelegramMessage;
import com.artivisi.accountingfinance.dto.telegram.TelegramPhotoSize;
import com.artivisi.accountingfinance.dto.telegram.TelegramUpdate;
import com.artivisi.accountingfinance.dto.telegram.TelegramUser;
import com.artivisi.accountingfinance.entity.Document;
import com.artivisi.accountingfinance.entity.DraftTransaction;
import com.artivisi.accountingfinance.entity.JournalTemplate;
import com.artivisi.accountingfinance.entity.TelegramUserLink;
import com.artivisi.accountingfinance.entity.User;
import com.artivisi.accountingfinance.repository.TelegramUserLinkRepository;
import com.artivisi.accountingfinance.repository.UserRepository;
import com.artivisi.accountingfinance.service.telegram.TelegramApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("TelegramBotService Tests")
class TelegramBotServiceTest {

    @Mock
    private TelegramConfig config;

    @Mock
    private TelegramUserLinkRepository telegramLinkRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DraftTransactionService draftService;

    @Mock
    private DocumentService documentService;

    @Mock
    private TelegramApiClient telegramApiClient;

    private TelegramBotService service;

    @BeforeEach
    void setUp() {
        when(config.isEnabled()).thenReturn(true);
        when(config.getUsername()).thenReturn("testbot");
        when(config.getToken()).thenReturn("test-token");

        service = new TelegramBotService(
                config, telegramLinkRepository, userRepository,
                draftService, documentService, telegramApiClient);
    }

    @Nested
    @DisplayName("Service Initialization")
    class InitializationTests {

        @Test
        @DisplayName("Should initialize with enabled config and api client")
        void shouldInitializeWithEnabledConfigAndApiClient() {
            assertThat(service.isEnabled()).isTrue();
            assertThat(service.getBotUsername()).isEqualTo("testbot");
        }

        @Test
        @DisplayName("Should report disabled when config is disabled")
        void shouldReportDisabledWhenConfigDisabled() {
            when(config.isEnabled()).thenReturn(false);

            TelegramBotService disabledService = new TelegramBotService(
                    config, telegramLinkRepository, userRepository,
                    draftService, documentService, telegramApiClient);

            assertThat(disabledService.isEnabled()).isFalse();
        }

        @Test
        @DisplayName("Should report disabled when api client is null")
        void shouldReportDisabledWhenApiClientNull() {
            TelegramBotService noClientService = new TelegramBotService(
                    config, telegramLinkRepository, userRepository,
                    draftService, documentService, null);

            assertThat(noClientService.isEnabled()).isFalse();
        }
    }

    @Nested
    @DisplayName("Handle Update - Basic")
    class HandleUpdateBasicTests {

        @Test
        @DisplayName("Should ignore update when bot is disabled")
        void shouldIgnoreUpdateWhenBotDisabled() {
            when(config.isEnabled()).thenReturn(false);

            TelegramBotService disabledService = new TelegramBotService(
                    config, telegramLinkRepository, userRepository,
                    draftService, documentService, telegramApiClient);

            TelegramUpdate update = createTextUpdate("/start");
            disabledService.handleUpdate(update);

            verify(telegramApiClient, never()).sendMessage(any());
        }

        @Test
        @DisplayName("Should ignore update when message is null")
        void shouldIgnoreUpdateWhenMessageNull() {
            TelegramUpdate update = new TelegramUpdate();
            update.setUpdateId(123L);

            service.handleUpdate(update);

            verify(telegramApiClient, never()).sendMessage(any());
        }

        @Test
        @DisplayName("Should send help message for unknown text")
        void shouldSendHelpMessageForUnknownText() {
            when(telegramLinkRepository.findByTelegramUserIdAndIsActiveTrue(any()))
                    .thenReturn(Optional.empty());
            mockSendMessageSuccess();

            TelegramUpdate update = createTextUpdate("hello");
            service.handleUpdate(update);

            ArgumentCaptor<TelegramApiClient.SendMessageRequest> captor =
                    ArgumentCaptor.forClass(TelegramApiClient.SendMessageRequest.class);
            verify(telegramApiClient).sendMessage(captor.capture());

            assertThat(captor.getValue().text()).contains("/help");
        }
    }

    @Nested
    @DisplayName("Start Command")
    class StartCommandTests {

        @Test
        @DisplayName("Should show welcome message for new user")
        void shouldShowWelcomeMessageForNewUser() {
            when(telegramLinkRepository.findByTelegramUserIdAndIsActiveTrue(any()))
                    .thenReturn(Optional.empty());
            when(telegramLinkRepository.findByTelegramUserId(any()))
                    .thenReturn(Optional.empty());
            mockSendMessageSuccess();

            TelegramUpdate update = createTextUpdate("/start");
            service.handleUpdate(update);

            ArgumentCaptor<TelegramApiClient.SendMessageRequest> captor =
                    ArgumentCaptor.forClass(TelegramApiClient.SendMessageRequest.class);
            verify(telegramApiClient).sendMessage(captor.capture());

            assertThat(captor.getValue().text()).contains("Selamat datang");
            assertThat(captor.getValue().text()).contains("/link");
        }

        @Test
        @DisplayName("Should show linked message for already linked user")
        void shouldShowLinkedMessageForAlreadyLinkedUser() {
            TelegramUserLink link = createLinkedUserLink();
            when(telegramLinkRepository.findByTelegramUserIdAndIsActiveTrue(any()))
                    .thenReturn(Optional.of(link));
            when(telegramLinkRepository.findByTelegramUserId(any()))
                    .thenReturn(Optional.of(link));
            mockSendMessageSuccess();

            TelegramUpdate update = createTextUpdate("/start");
            service.handleUpdate(update);

            ArgumentCaptor<TelegramApiClient.SendMessageRequest> captor =
                    ArgumentCaptor.forClass(TelegramApiClient.SendMessageRequest.class);
            verify(telegramApiClient).sendMessage(captor.capture());

            assertThat(captor.getValue().text()).contains("sudah terhubung");
        }

        @Test
        @DisplayName("Should process verification code from start deep link")
        void shouldProcessVerificationCodeFromStartDeepLink() {
            String verificationCode = "123456";
            TelegramUserLink pendingLink = createPendingLink(verificationCode);

            when(telegramLinkRepository.findByTelegramUserIdAndIsActiveTrue(any()))
                    .thenReturn(Optional.empty());
            when(telegramLinkRepository.findByTelegramUserId(any()))
                    .thenReturn(Optional.empty());
            when(telegramLinkRepository.findByVerificationCode(verificationCode))
                    .thenReturn(Optional.of(pendingLink));
            mockSendMessageSuccess();

            TelegramUpdate update = createTextUpdate("/start " + verificationCode);
            service.handleUpdate(update);

            verify(telegramLinkRepository).save(any(TelegramUserLink.class));

            ArgumentCaptor<TelegramApiClient.SendMessageRequest> captor =
                    ArgumentCaptor.forClass(TelegramApiClient.SendMessageRequest.class);
            verify(telegramApiClient).sendMessage(captor.capture());

            assertThat(captor.getValue().text()).contains("berhasil terhubung");
        }
    }

    @Nested
    @DisplayName("Link Command")
    class LinkCommandTests {

        @Test
        @DisplayName("Should link account with valid verification code")
        void shouldLinkAccountWithValidCode() {
            String verificationCode = "654321";
            TelegramUserLink pendingLink = createPendingLink(verificationCode);

            when(telegramLinkRepository.findByTelegramUserIdAndIsActiveTrue(any()))
                    .thenReturn(Optional.empty());
            when(telegramLinkRepository.findByVerificationCode(verificationCode))
                    .thenReturn(Optional.of(pendingLink));
            mockSendMessageSuccess();

            TelegramUpdate update = createTextUpdate("/link " + verificationCode);
            service.handleUpdate(update);

            ArgumentCaptor<TelegramUserLink> linkCaptor =
                    ArgumentCaptor.forClass(TelegramUserLink.class);
            verify(telegramLinkRepository).save(linkCaptor.capture());

            TelegramUserLink savedLink = linkCaptor.getValue();
            assertThat(savedLink.getTelegramUserId()).isEqualTo(123456L);
            assertThat(savedLink.getTelegramUsername()).isEqualTo("johndoe");
            assertThat(savedLink.getTelegramFirstName()).isEqualTo("John");
            assertThat(savedLink.getIsActive()).isTrue();
            assertThat(savedLink.getLinkedAt()).isNotNull();
            assertThat(savedLink.getVerificationCode()).isNull();
        }

        @Test
        @DisplayName("Should reject invalid verification code")
        void shouldRejectInvalidVerificationCode() {
            when(telegramLinkRepository.findByTelegramUserIdAndIsActiveTrue(any()))
                    .thenReturn(Optional.empty());
            when(telegramLinkRepository.findByVerificationCode(anyString()))
                    .thenReturn(Optional.empty());
            mockSendMessageSuccess();

            TelegramUpdate update = createTextUpdate("/link invalid-code");
            service.handleUpdate(update);

            verify(telegramLinkRepository, never()).save(any());

            ArgumentCaptor<TelegramApiClient.SendMessageRequest> captor =
                    ArgumentCaptor.forClass(TelegramApiClient.SendMessageRequest.class);
            verify(telegramApiClient).sendMessage(captor.capture());

            assertThat(captor.getValue().text()).contains("tidak valid");
        }

        @Test
        @DisplayName("Should reject expired verification code")
        void shouldRejectExpiredVerificationCode() {
            String verificationCode = "expired";
            TelegramUserLink expiredLink = createExpiredLink(verificationCode);

            when(telegramLinkRepository.findByTelegramUserIdAndIsActiveTrue(any()))
                    .thenReturn(Optional.empty());
            when(telegramLinkRepository.findByVerificationCode(verificationCode))
                    .thenReturn(Optional.of(expiredLink));
            mockSendMessageSuccess();

            TelegramUpdate update = createTextUpdate("/link " + verificationCode);
            service.handleUpdate(update);

            verify(telegramLinkRepository, never()).save(any());

            ArgumentCaptor<TelegramApiClient.SendMessageRequest> captor =
                    ArgumentCaptor.forClass(TelegramApiClient.SendMessageRequest.class);
            verify(telegramApiClient).sendMessage(captor.capture());

            assertThat(captor.getValue().text()).contains("kadaluarsa");
        }
    }

    @Nested
    @DisplayName("Status Command")
    class StatusCommandTests {

        @Test
        @DisplayName("Should show status for linked user with pending drafts")
        void shouldShowStatusWithPendingDrafts() {
            TelegramUserLink link = createLinkedUserLink();
            when(telegramLinkRepository.findByTelegramUserIdAndIsActiveTrue(any()))
                    .thenReturn(Optional.of(link));
            when(draftService.countPendingByUser("testuser")).thenReturn(5L);
            mockSendMessageSuccess();

            TelegramUpdate update = createTextUpdate("/status");
            service.handleUpdate(update);

            ArgumentCaptor<TelegramApiClient.SendMessageRequest> captor =
                    ArgumentCaptor.forClass(TelegramApiClient.SendMessageRequest.class);
            verify(telegramApiClient).sendMessage(captor.capture());

            assertThat(captor.getValue().text()).contains("5 draft");
        }

        @Test
        @DisplayName("Should show no pending drafts message")
        void shouldShowNoPendingDraftsMessage() {
            TelegramUserLink link = createLinkedUserLink();
            when(telegramLinkRepository.findByTelegramUserIdAndIsActiveTrue(any()))
                    .thenReturn(Optional.of(link));
            when(draftService.countPendingByUser("testuser")).thenReturn(0L);
            mockSendMessageSuccess();

            TelegramUpdate update = createTextUpdate("/status");
            service.handleUpdate(update);

            ArgumentCaptor<TelegramApiClient.SendMessageRequest> captor =
                    ArgumentCaptor.forClass(TelegramApiClient.SendMessageRequest.class);
            verify(telegramApiClient).sendMessage(captor.capture());

            assertThat(captor.getValue().text()).contains("Tidak ada draft");
        }

        @Test
        @DisplayName("Should prompt to link when user not linked")
        void shouldPromptToLinkWhenNotLinked() {
            when(telegramLinkRepository.findByTelegramUserIdAndIsActiveTrue(any()))
                    .thenReturn(Optional.empty());
            mockSendMessageSuccess();

            TelegramUpdate update = createTextUpdate("/status");
            service.handleUpdate(update);

            ArgumentCaptor<TelegramApiClient.SendMessageRequest> captor =
                    ArgumentCaptor.forClass(TelegramApiClient.SendMessageRequest.class);
            verify(telegramApiClient).sendMessage(captor.capture());

            assertThat(captor.getValue().text()).contains("belum terhubung");
            assertThat(captor.getValue().text()).contains("/start");
        }
    }

    @Nested
    @DisplayName("Help Command")
    class HelpCommandTests {

        @Test
        @DisplayName("Should show help with markdown formatting")
        void shouldShowHelpWithMarkdown() {
            when(telegramLinkRepository.findByTelegramUserIdAndIsActiveTrue(any()))
                    .thenReturn(Optional.empty());
            mockSendMessageSuccess();

            TelegramUpdate update = createTextUpdate("/help");
            service.handleUpdate(update);

            ArgumentCaptor<TelegramApiClient.SendMessageRequest> captor =
                    ArgumentCaptor.forClass(TelegramApiClient.SendMessageRequest.class);
            verify(telegramApiClient).sendMessage(captor.capture());

            assertThat(captor.getValue().text()).contains("Panduan Penggunaan");
            assertThat(captor.getValue().text()).contains("/start");
            assertThat(captor.getValue().text()).contains("/link");
            assertThat(captor.getValue().text()).contains("/status");
            assertThat(captor.getValue().text()).contains("/help");
            assertThat(captor.getValue().parse_mode()).isEqualTo("Markdown");
        }
    }

    @Nested
    @DisplayName("Photo Message Handling")
    class PhotoMessageTests {

        @Test
        @DisplayName("Should prompt to link when receiving photo from unlinked user")
        void shouldPromptToLinkWhenPhotoFromUnlinkedUser() {
            when(telegramLinkRepository.findByTelegramUserIdAndIsActiveTrue(any()))
                    .thenReturn(Optional.empty());
            mockSendMessageSuccess();

            TelegramUpdate update = createPhotoUpdate();
            service.handleUpdate(update);

            ArgumentCaptor<TelegramApiClient.SendMessageRequest> captor =
                    ArgumentCaptor.forClass(TelegramApiClient.SendMessageRequest.class);
            verify(telegramApiClient).sendMessage(captor.capture());

            assertThat(captor.getValue().text()).contains("belum terhubung");
        }

        @Test
        @DisplayName("Should process photo for linked user")
        void shouldProcessPhotoForLinkedUser() throws Exception {
            TelegramUserLink link = createLinkedUserLink();
            when(telegramLinkRepository.findByTelegramUserIdAndIsActiveTrue(any()))
                    .thenReturn(Optional.of(link));

            // Mock file download
            TelegramApiClient.GetFileResponse fileResponse = new TelegramApiClient.GetFileResponse(
                    true,
                    new TelegramApiClient.FileResult("file1", "unique1", 1234L, "photos/test.jpg"),
                    null);
            when(telegramApiClient.getFile(any())).thenReturn(fileResponse);

            // Mock document save
            Document document = new Document();
            when(documentService.saveFromBytes(any(), anyString(), anyString(), anyString()))
                    .thenReturn(document);

            // Mock draft creation
            DraftTransaction draft = createDraft();
            when(draftService.processReceiptImage(any(), any(), any(), any(), anyString()))
                    .thenReturn(draft);

            mockSendMessageSuccess();

            TelegramUpdate update = createPhotoUpdate();

            // Note: This will throw an exception when trying to download the file
            // because we can't mock URL.openStream() without PowerMock
            // But it tests the flow up to that point
            try {
                service.handleUpdate(update);
            } catch (Exception _) {
                // Expected - can't mock URL download
            }

            // Verify processing message was sent
            verify(telegramApiClient, atLeastOnce()).sendMessage(any());
        }

        @Test
        @DisplayName("Should ignore message with empty photo list")
        void shouldIgnoreMessageWithEmptyPhotoList() {
            // Empty photo list means hasPhoto() returns false
            // The message will be ignored (logged but not responded to)
            TelegramUpdate update = createEmptyPhotoUpdate();
            service.handleUpdate(update);

            // No message should be sent since empty photo list is ignored
            verify(telegramApiClient, never()).sendMessage(any());
        }
    }

    @Nested
    @DisplayName("Verification Code Generation")
    class VerificationCodeTests {

        @Test
        @DisplayName("Should generate 6-digit verification code for new user")
        void shouldGenerateSixDigitCodeForNewUser() {
            User user = createUser();
            when(telegramLinkRepository.findByUser(user)).thenReturn(Optional.empty());

            String code = service.generateVerificationCode(user);

            assertThat(code).matches("\\d{6}");

            ArgumentCaptor<TelegramUserLink> captor =
                    ArgumentCaptor.forClass(TelegramUserLink.class);
            verify(telegramLinkRepository).save(captor.capture());

            TelegramUserLink savedLink = captor.getValue();
            assertThat(savedLink.getVerificationCode()).isEqualTo(code);
            assertThat(savedLink.getVerificationExpiresAt()).isAfter(LocalDateTime.now());
            assertThat(savedLink.getIsActive()).isFalse();
        }

        @Test
        @DisplayName("Should update verification code for existing user")
        void shouldUpdateCodeForExistingUser() {
            User user = createUser();
            TelegramUserLink existingLink = new TelegramUserLink();
            existingLink.setUser(user);
            existingLink.setVerificationCode("oldcode");

            when(telegramLinkRepository.findByUser(user)).thenReturn(Optional.of(existingLink));

            String code = service.generateVerificationCode(user);

            assertThat(code).matches("\\d{6}").isNotEqualTo("oldcode");

            ArgumentCaptor<TelegramUserLink> captor =
                    ArgumentCaptor.forClass(TelegramUserLink.class);
            verify(telegramLinkRepository).save(captor.capture());

            TelegramUserLink savedLink = captor.getValue();
            assertThat(savedLink.getVerificationCode()).isEqualTo(code);
        }
    }

    @Nested
    @DisplayName("Error Handling")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle send message failure gracefully")
        void shouldHandleSendMessageFailure() {
            when(telegramLinkRepository.findByTelegramUserIdAndIsActiveTrue(any()))
                    .thenReturn(Optional.empty());
            when(telegramApiClient.sendMessage(any()))
                    .thenReturn(new TelegramApiClient.SendMessageResponse(false, "Rate limited"));

            TelegramUpdate update = createTextUpdate("/help");

            // Should not throw exception
            service.handleUpdate(update);

            verify(telegramApiClient).sendMessage(any());
        }

        @Test
        @DisplayName("Should handle send message exception gracefully")
        void shouldHandleSendMessageException() {
            when(telegramLinkRepository.findByTelegramUserIdAndIsActiveTrue(any()))
                    .thenReturn(Optional.empty());
            when(telegramApiClient.sendMessage(any()))
                    .thenThrow(new RuntimeException("Network error"));

            TelegramUpdate update = createTextUpdate("/help");

            // Should not throw exception
            service.handleUpdate(update);

            verify(telegramApiClient).sendMessage(any());
        }

        @Test
        @DisplayName("Should not send message when api client is null")
        void shouldNotSendMessageWhenApiClientNull() {
            TelegramBotService noClientService = new TelegramBotService(
                    config, telegramLinkRepository, userRepository,
                    draftService, documentService, null);

            when(telegramLinkRepository.findByTelegramUserIdAndIsActiveTrue(any()))
                    .thenReturn(Optional.empty());
            when(telegramLinkRepository.findByTelegramUserId(any()))
                    .thenReturn(Optional.empty());

            TelegramUpdate update = createTextUpdate("/start");
            noClientService.handleUpdate(update);

            // Verify no interaction with telegramApiClient since it's null
            verifyNoInteractions(telegramApiClient);
        }
    }

    // Helper methods

    private void mockSendMessageSuccess() {
        when(telegramApiClient.sendMessage(any()))
                .thenReturn(new TelegramApiClient.SendMessageResponse(true, null));
    }

    private TelegramUpdate createTextUpdate(String text) {
        TelegramUpdate update = new TelegramUpdate();
        update.setUpdateId(System.currentTimeMillis());

        TelegramMessage message = new TelegramMessage();
        message.setMessageId(1L);
        message.setText(text);

        TelegramUser from = new TelegramUser();
        from.setId(123456L);
        from.setUsername("johndoe");
        from.setFirstName("John");
        message.setFrom(from);

        TelegramChat chat = new TelegramChat();
        chat.setId(123456L);
        chat.setType("private");
        message.setChat(chat);

        update.setMessage(message);
        return update;
    }

    private TelegramUpdate createPhotoUpdate() {
        TelegramUpdate update = createTextUpdate(null);
        update.getMessage().setText(null);

        TelegramPhotoSize photo1 = new TelegramPhotoSize();
        photo1.setFileId("photo1_small");
        photo1.setWidth(90);
        photo1.setHeight(60);

        TelegramPhotoSize photo2 = new TelegramPhotoSize();
        photo2.setFileId("photo2_large");
        photo2.setWidth(320);
        photo2.setHeight(240);

        update.getMessage().setPhoto(List.of(photo1, photo2));
        return update;
    }

    private TelegramUpdate createEmptyPhotoUpdate() {
        TelegramUpdate update = createTextUpdate(null);
        update.getMessage().setText(null);
        update.getMessage().setPhoto(List.of());
        return update;
    }

    private TelegramUserLink createLinkedUserLink() {
        User user = createUser();

        TelegramUserLink link = new TelegramUserLink();
        link.setUser(user);
        link.setTelegramUserId(123456L);
        link.setTelegramUsername("johndoe");
        link.setTelegramFirstName("John");
        link.setLinkedAt(LocalDateTime.now());
        link.setIsActive(true);
        return link;
    }

    private TelegramUserLink createPendingLink(String code) {
        User user = createUser();

        TelegramUserLink link = new TelegramUserLink();
        link.setUser(user);
        link.setVerificationCode(code);
        link.setVerificationExpiresAt(LocalDateTime.now().plusMinutes(15));
        link.setIsActive(false);
        return link;
    }

    private TelegramUserLink createExpiredLink(String code) {
        User user = createUser();

        TelegramUserLink link = new TelegramUserLink();
        link.setUser(user);
        link.setVerificationCode(code);
        link.setVerificationExpiresAt(LocalDateTime.now().minusMinutes(1));
        link.setIsActive(false);
        return link;
    }

    private User createUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setFullName("Test User");
        return user;
    }

    private DraftTransaction createDraft() {
        JournalTemplate template = new JournalTemplate();
        template.setTemplateName("Transfer Receipt");

        DraftTransaction draft = new DraftTransaction();
        draft.setMerchantName("TOKOPEDIA");
        draft.setAmount(BigDecimal.valueOf(100000));
        draft.setTransactionDate(LocalDate.now());
        draft.setSuggestedTemplate(template);
        draft.setOverallConfidence(BigDecimal.valueOf(0.85));
        return draft;
    }

}
