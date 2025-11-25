package com.artivisi.accountingfinance.enums;

public enum ProjectStatus {
    ACTIVE("Active", "Aktif"),
    COMPLETED("Completed", "Selesai"),
    ARCHIVED("Archived", "Diarsipkan");

    private final String englishName;
    private final String indonesianName;

    ProjectStatus(String englishName, String indonesianName) {
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
