package com.artivisi.accountingfinance.enums;

public enum JournalEntryStatus {
    DRAFT("Draft", "Draft", "yellow"),
    POSTED("Posted", "Terposting", "green"),
    VOID("Void", "Dibatalkan", "red");

    private final String englishName;
    private final String indonesianName;
    private final String colorCode;

    JournalEntryStatus(String englishName, String indonesianName, String colorCode) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
        this.colorCode = colorCode;
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getIndonesianName() {
        return indonesianName;
    }

    public String getColorCode() {
        return colorCode;
    }
}
