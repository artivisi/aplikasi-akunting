package com.artivisi.accountingfinance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record BillLineRequest(
    @NotBlank(message = "Deskripsi wajib diisi") String description,
    BigDecimal quantity,
    @NotNull(message = "Harga satuan wajib diisi") BigDecimal unitPrice,
    BigDecimal taxRate,
    String expenseAccountCode
) {}
