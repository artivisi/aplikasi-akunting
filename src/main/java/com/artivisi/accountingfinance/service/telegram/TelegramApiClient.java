package com.artivisi.accountingfinance.service.telegram;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * Spring HTTP Interface for Telegram Bot API.
 * Uses declarative HTTP clients with RestClient backing.
 */
public interface TelegramApiClient {

    @PostExchange("/sendMessage")
    SendMessageResponse sendMessage(@RequestBody SendMessageRequest request);

    @GetExchange("/getFile")
    GetFileResponse getFile(@RequestBody GetFileRequest request);
    
    /**
     * Download file from Telegram servers.
     * Note: This uses a different base URL (file API).
     */
    @GetExchange("/file/bot{token}/{filePath}")
    byte[] downloadFile(@PathVariable String token, @PathVariable String filePath);

    // Request/Response DTOs
    @JsonInclude(JsonInclude.Include.NON_NULL)
    record SendMessageRequest(
        Long chat_id,
        String text,
        String parse_mode
    ) {}

    record SendMessageResponse(
        Boolean ok,
        String description
    ) {}

    record GetFileRequest(
        String file_id
    ) {}

    record GetFileResponse(
        Boolean ok,
        FileResult result,
        String description
    ) {}

    record FileResult(
        String file_id,
        String file_unique_id,
        Long file_size,
        String file_path
    ) {}
}
