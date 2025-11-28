package com.artivisi.accountingfinance.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SalaryComponentType {
    EARNING("Pendapatan", "Komponen yang menambah gaji"),
    DEDUCTION("Potongan", "Komponen yang mengurangi gaji karyawan"),
    COMPANY_CONTRIBUTION("Kontribusi Perusahaan", "Beban perusahaan (tidak dipotong dari gaji)");

    private final String displayName;
    private final String description;
}
