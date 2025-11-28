package com.artivisi.accountingfinance.entity;

import java.math.BigDecimal;

/**
 * PTKP (Penghasilan Tidak Kena Pajak) Status codes based on Indonesian tax regulations.
 * Updated for 2024 rates (PMK 101/PMK.010/2016)
 */
public enum PtkpStatus {
    TK_0("TK/0", "Tidak Kawin tanpa tanggungan", new BigDecimal("54000000")),
    TK_1("TK/1", "Tidak Kawin 1 tanggungan", new BigDecimal("58500000")),
    TK_2("TK/2", "Tidak Kawin 2 tanggungan", new BigDecimal("63000000")),
    TK_3("TK/3", "Tidak Kawin 3 tanggungan", new BigDecimal("67500000")),
    K_0("K/0", "Kawin tanpa tanggungan", new BigDecimal("58500000")),
    K_1("K/1", "Kawin 1 tanggungan", new BigDecimal("63000000")),
    K_2("K/2", "Kawin 2 tanggungan", new BigDecimal("67500000")),
    K_3("K/3", "Kawin 3 tanggungan", new BigDecimal("72000000")),
    K_I_0("K/I/0", "Kawin penghasilan istri digabung tanpa tanggungan", new BigDecimal("112500000")),
    K_I_1("K/I/1", "Kawin penghasilan istri digabung 1 tanggungan", new BigDecimal("117000000")),
    K_I_2("K/I/2", "Kawin penghasilan istri digabung 2 tanggungan", new BigDecimal("121500000")),
    K_I_3("K/I/3", "Kawin penghasilan istri digabung 3 tanggungan", new BigDecimal("126000000"));

    private final String code;
    private final String description;
    private final BigDecimal annualAmount;

    PtkpStatus(String code, String description, BigDecimal annualAmount) {
        this.code = code;
        this.description = description;
        this.annualAmount = annualAmount;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAnnualAmount() {
        return annualAmount;
    }

    public BigDecimal getMonthlyAmount() {
        return annualAmount.divide(BigDecimal.valueOf(12), 0, java.math.RoundingMode.DOWN);
    }

    public String getDisplayName() {
        return code + " - " + description;
    }
}
