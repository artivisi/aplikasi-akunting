package com.artivisi.accountingfinance.enums;

public enum StatementItemMatchStatus {
    UNMATCHED("Unmatched", "Belum Cocok", "gray"),
    MATCHED("Matched", "Cocok", "green"),
    BANK_ONLY("Bank Only", "Hanya di Bank", "orange"),
    BOOK_ONLY("Book Only", "Hanya di Buku", "purple");

    private final String englishName;
    private final String indonesianName;
    private final String colorCode;

    StatementItemMatchStatus(String englishName, String indonesianName, String colorCode) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
        this.colorCode = colorCode;
    }

    public String getEnglishName() { return englishName; }
    public String getIndonesianName() { return indonesianName; }
    public String getColorCode() { return colorCode; }
}
