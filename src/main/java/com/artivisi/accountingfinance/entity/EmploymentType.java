package com.artivisi.accountingfinance.entity;

/**
 * Employment type (full-time, part-time, contract, etc.)
 */
public enum EmploymentType {
    PERMANENT("Tetap"),
    CONTRACT("Kontrak"),
    PROBATION("Percobaan"),
    FREELANCE("Freelance");

    private final String displayName;

    EmploymentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
