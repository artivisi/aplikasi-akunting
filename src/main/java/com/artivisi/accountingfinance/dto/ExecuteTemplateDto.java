package com.artivisi.accountingfinance.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExecuteTemplateDto(
        @NotNull(message = "Transaction date is required")
        LocalDate transactionDate,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be positive")
        BigDecimal amount,

        @NotBlank(message = "Description is required")
        String description
) {}
