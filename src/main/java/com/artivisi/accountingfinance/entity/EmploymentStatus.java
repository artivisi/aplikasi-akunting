package com.artivisi.accountingfinance.entity;

/**
 * Employment status for employees.
 */
public enum EmploymentStatus {
    ACTIVE("Aktif"),
    RESIGNED("Resign"),
    TERMINATED("PHK"),
    RETIRED("Pensiun");

    private final String displayName;

    EmploymentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
