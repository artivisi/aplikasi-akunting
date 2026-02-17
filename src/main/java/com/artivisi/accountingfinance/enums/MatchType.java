package com.artivisi.accountingfinance.enums;

public enum MatchType {
    EXACT("Exact", "Persis Sama"),
    FUZZY_DATE("Fuzzy Date", "Tanggal Mendekati"),
    KEYWORD("Keyword", "Kata Kunci"),
    MANUAL("Manual", "Manual");

    private final String englishName;
    private final String indonesianName;

    MatchType(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }

    public String getEnglishName() { return englishName; }
    public String getIndonesianName() { return indonesianName; }
}
