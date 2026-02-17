package com.artivisi.accountingfinance.enums;

public enum ReconciliationStatus {
    DRAFT("Draft", "Draft", "yellow"),
    IN_PROGRESS("In Progress", "Sedang Dikerjakan", "blue"),
    COMPLETED("Completed", "Selesai", "green");

    private final String englishName;
    private final String indonesianName;
    private final String colorCode;

    ReconciliationStatus(String englishName, String indonesianName, String colorCode) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
        this.colorCode = colorCode;
    }

    public String getEnglishName() { return englishName; }
    public String getIndonesianName() { return indonesianName; }
    public String getColorCode() { return colorCode; }
}
