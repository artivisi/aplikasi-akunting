package com.artivisi.accountingfinance.enums;

public enum InvoiceStatus {
    DRAFT("Draft", "Draf"),
    SENT("Sent", "Terkirim"),
    PAID("Paid", "Lunas"),
    OVERDUE("Overdue", "Jatuh Tempo"),
    CANCELLED("Cancelled", "Dibatalkan");

    private final String englishName;
    private final String indonesianName;

    InvoiceStatus(String englishName, String indonesianName) {
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
