package com.artivisi.accountingfinance.dto;

import java.util.UUID;

public record AccountOptionDto(
        UUID id,
        String accountCode,
        String accountName
) {}
