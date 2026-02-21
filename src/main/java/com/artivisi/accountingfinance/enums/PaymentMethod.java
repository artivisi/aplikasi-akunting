package com.artivisi.accountingfinance.enums;

public enum PaymentMethod {
    TRANSFER("Transfer Bank", "Transfer"),
    CASH("Tunai", "Cash"),
    CHECK("Cek/Giro", "Check"),
    CREDIT_CARD("Kartu Kredit", "Credit Card"),
    EWALLET("E-Wallet", "E-Wallet"),
    OTHER("Lainnya", "Other");

    private final String indonesianName;
    private final String englishName;

    PaymentMethod(String indonesianName, String englishName) {
        this.indonesianName = indonesianName;
        this.englishName = englishName;
    }

    public String getIndonesianName() {
        return indonesianName;
    }

    public String getEnglishName() {
        return englishName;
    }
}
