package com.artivisi.accountingfinance.enums;

public enum PaymentTrigger {
    ON_SIGNING("On Signing", "Saat Penandatanganan"),
    ON_MILESTONE("On Milestone", "Saat Milestone Selesai"),
    ON_COMPLETION("On Completion", "Saat Proyek Selesai"),
    FIXED_DATE("Fixed Date", "Tanggal Tetap");

    private final String englishName;
    private final String indonesianName;

    PaymentTrigger(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getIndonesianName() {
        return indonesianName;
    }
}
