package com.artivisi.accountingfinance.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record JournalEntryDto(
        @NotNull(message = "Tanggal jurnal harus diisi")
        LocalDate journalDate,

        @Size(max = 100, message = "Nomor referensi maksimal 100 karakter")
        String referenceNumber,

        @NotBlank(message = "Keterangan harus diisi")
        @Size(max = 500, message = "Keterangan maksimal 500 karakter")
        String description,

        @NotEmpty(message = "Minimal harus ada 2 baris jurnal")
        @Valid
        List<JournalEntryLineDto> lines,

        boolean postImmediately
) {
    public record JournalEntryLineDto(
            @NotNull(message = "Akun harus dipilih")
            UUID accountId,

            @NotNull(message = "Debit harus diisi")
            BigDecimal debit,

            @NotNull(message = "Kredit harus diisi")
            BigDecimal credit,

            @Size(max = 255, message = "Keterangan baris maksimal 255 karakter")
            String lineDescription
    ) {}
}
