package com.artivisi.accountingfinance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

/**
 * Request DTO for creating a DRAFT transaction directly (bypassing the
 * from-text/from-receipt draft workflow). Creates a Transaction in DRAFT
 * status with template and optional account overrides.
 */
public record CreateDraftRequest(

        @NotNull(message = "Template ID is required")
        UUID templateId,

        @NotBlank(message = "Description is required")
        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        String description,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        BigDecimal amount,

        @NotNull(message = "Transaction date is required")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate transactionDate,

        UUID projectId,

        /**
         * Specify accounts for template lines that have accountHint instead of a fixed account.
         * Key = accountHint string (e.g., "BANK") or lineOrder number (e.g., "2").
         * Value = accountId UUID.
         * Example: {"BANK": "648eaabb-..."} or {"2": "648eaabb-..."}
         */
        Map<String, UUID> accountSlots,

        /**
         * Formula variable values for DETAILED templates.
         * Key = variable name from template formula (e.g. "assetCost"), Value = amount.
         * Required when template uses non-standard formulas (not just "amount").
         */
        Map<String, BigDecimal> variables
) {
}
