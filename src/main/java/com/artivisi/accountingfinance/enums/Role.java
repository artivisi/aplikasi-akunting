package com.artivisi.accountingfinance.enums;

/**
 * User roles for role-based access control.
 * Users can have multiple roles (additive permissions).
 */
public enum Role {
    /**
     * Full system access including user management
     */
    ADMIN("Administrator", "Full system access including user management"),

    /**
     * Business owner - all business features, no user management
     */
    OWNER("Pemilik", "Access to all business features"),

    /**
     * Accountant - accounting operations and reports
     */
    ACCOUNTANT("Akuntan", "Accounting operations and reports"),

    /**
     * Staff - limited operations (view, create drafts)
     */
    STAFF("Staf", "Limited access to daily operations"),

    /**
     * Auditor - read-only access to all reports
     */
    AUDITOR("Auditor", "Read-only access to reports"),

    /**
     * Employee - access to own payslips and profile only
     */
    EMPLOYEE("Karyawan", "Access to own payslips and profile");

    private final String displayName;
    private final String description;

    Role(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
