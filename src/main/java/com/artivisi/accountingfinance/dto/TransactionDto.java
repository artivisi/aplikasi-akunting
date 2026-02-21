package com.artivisi.accountingfinance.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record TransactionDto(
        UUID id,

        String transactionNumber,

        @NotNull(message = "Transaction date is required")
        LocalDate transactionDate,

        @NotNull(message = "Template is required")
        UUID templateId,

        UUID projectId,

        UUID invoiceId,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0", message = "Amount must be non-negative")
        BigDecimal amount,

        @NotBlank(message = "Description is required")
        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description,

        @Size(max = 100, message = "Reference number must not exceed 100 characters")
        String referenceNumber,

        String notes,

        String status,

        Map<UUID, UUID> accountMappings,

        // For DETAILED templates - variable values keyed by variable name
        Map<String, BigDecimal> variables,

        // Tag IDs to assign to the transaction
        List<UUID> tagIds
) {}
