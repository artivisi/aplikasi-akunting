package com.artivisi.accountingfinance.enums;

public enum MilestoneStatus {
    PENDING("Pending", "Menunggu"),
    IN_PROGRESS("In Progress", "Dalam Proses"),
    COMPLETED("Completed", "Selesai");

    private final String englishName;
    private final String indonesianName;

    MilestoneStatus(String englishName, String indonesianName) {
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
