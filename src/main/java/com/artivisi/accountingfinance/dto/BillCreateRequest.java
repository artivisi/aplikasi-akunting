package com.artivisi.accountingfinance.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record BillCreateRequest(
    @NotBlank(message = "Nama vendor wajib diisi") String vendorName,
    @NotNull(message = "Tanggal tagihan wajib diisi") LocalDate billDate,
    @NotNull(message = "Tanggal jatuh tempo wajib diisi") LocalDate dueDate,
    String vendorInvoiceNumber,
    String notes,
    @NotEmpty(message = "Minimal satu baris tagihan wajib diisi") @Valid List<BillLineRequest> lines
) {}
