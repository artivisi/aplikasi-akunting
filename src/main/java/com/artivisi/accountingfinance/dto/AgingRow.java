package com.artivisi.accountingfinance.dto;

import java.util.UUID;

public record AgingRow(
        UUID entityId,
        String code,
        String name,
        AgingBucket bucket
) {
}
