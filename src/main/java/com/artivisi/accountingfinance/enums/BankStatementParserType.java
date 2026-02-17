package com.artivisi.accountingfinance.enums;

public enum BankStatementParserType {
    BCA("BCA", "Bank Central Asia"),
    MANDIRI("Mandiri", "Bank Mandiri"),
    BNI("BNI", "Bank Negara Indonesia"),
    BSI("BSI", "Bank Syariah Indonesia"),
    CIMB("CIMB", "CIMB Niaga"),
    CUSTOM("Custom", "Konfigurasi Kustom");

    private final String displayName;
    private final String description;

    BankStatementParserType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}
