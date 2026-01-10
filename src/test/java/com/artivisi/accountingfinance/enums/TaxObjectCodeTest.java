package com.artivisi.accountingfinance.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for TaxObjectCode enum.
 * Tests Indonesian tax object code lookups and operations.
 */
@DisplayName("TaxObjectCode - Indonesian Tax Object Codes")
class TaxObjectCodeTest {

    // ==================== Basic Getters ====================

    @Test
    @DisplayName("Should return code for PPH23 service")
    void shouldReturnCodeForPph23Service() {
        assertThat(TaxObjectCode.PPH23_JASA_TEKNIK.getCode()).isEqualTo("24-104-01");
        assertThat(TaxObjectCode.PPH23_JASA_KONSULTAN.getCode()).isEqualTo("24-104-03");
    }

    @Test
    @DisplayName("Should return description for PPH23 service")
    void shouldReturnDescriptionForPph23Service() {
        assertThat(TaxObjectCode.PPH23_JASA_TEKNIK.getDescription()).isEqualTo("Jasa Teknik");
        assertThat(TaxObjectCode.PPH23_JASA_CATERING.getDescription()).isEqualTo("Jasa Katering");
    }

    @Test
    @DisplayName("Should return default rate for PPH23")
    void shouldReturnDefaultRateForPph23() {
        assertThat(TaxObjectCode.PPH23_JASA_TEKNIK.getDefaultRate()).isEqualByComparingTo(new BigDecimal("2.00"));
        assertThat(TaxObjectCode.PPH23_SEWA_KENDARAAN.getDefaultRate()).isEqualByComparingTo(new BigDecimal("2.00"));
    }

    @Test
    @DisplayName("Should return default rate for PPH42")
    void shouldReturnDefaultRateForPph42() {
        assertThat(TaxObjectCode.PPH42_SEWA_TANAH_BANGUNAN.getDefaultRate()).isEqualByComparingTo(new BigDecimal("10.00"));
        assertThat(TaxObjectCode.PPH42_UMKM.getDefaultRate()).isEqualByComparingTo(new BigDecimal("0.50"));
    }

    @Test
    @DisplayName("Should return tax type")
    void shouldReturnTaxType() {
        assertThat(TaxObjectCode.PPH23_JASA_TEKNIK.getTaxType()).isEqualTo(TaxType.PPH_23);
        assertThat(TaxObjectCode.PPH42_SEWA_TANAH_BANGUNAN.getTaxType()).isEqualTo(TaxType.PPH_42);
        assertThat(TaxObjectCode.PPH21_HONORARIUM.getTaxType()).isEqualTo(TaxType.PPH_21);
    }

    @Test
    @DisplayName("Should return display name")
    void shouldReturnDisplayName() {
        String displayName = TaxObjectCode.PPH23_JASA_TEKNIK.getDisplayName();
        assertThat(displayName).isEqualTo("24-104-01 - Jasa Teknik");
    }

    // ==================== fromCode ====================

    @Test
    @DisplayName("Should find code by string")
    void shouldFindCodeByString() {
        TaxObjectCode code = TaxObjectCode.fromCode("24-104-01");
        assertThat(code).isEqualTo(TaxObjectCode.PPH23_JASA_TEKNIK);
    }

    @Test
    @DisplayName("Should return null for unknown code")
    void shouldReturnNullForUnknownCode() {
        TaxObjectCode code = TaxObjectCode.fromCode("99-999-99");
        assertThat(code).isNull();
    }

    @Test
    @DisplayName("Should return null for null code")
    void shouldReturnNullForNullCode() {
        TaxObjectCode code = TaxObjectCode.fromCode(null);
        assertThat(code).isNull();
    }

    @Test
    @DisplayName("Should find all PPH23 service codes")
    void shouldFindAllPph23ServiceCodes() {
        assertThat(TaxObjectCode.fromCode("24-104-02")).isEqualTo(TaxObjectCode.PPH23_JASA_MANAJEMEN);
        assertThat(TaxObjectCode.fromCode("24-104-03")).isEqualTo(TaxObjectCode.PPH23_JASA_KONSULTAN);
        assertThat(TaxObjectCode.fromCode("24-104-04")).isEqualTo(TaxObjectCode.PPH23_JASA_AKUNTANSI);
        assertThat(TaxObjectCode.fromCode("24-104-21")).isEqualTo(TaxObjectCode.PPH23_JASA_CATERING);
    }

    @Test
    @DisplayName("Should find PPH42 codes")
    void shouldFindPph42Codes() {
        assertThat(TaxObjectCode.fromCode("28-409-01")).isEqualTo(TaxObjectCode.PPH42_SEWA_TANAH_BANGUNAN);
        assertThat(TaxObjectCode.fromCode("28-423-01")).isEqualTo(TaxObjectCode.PPH42_UMKM);
    }

    // ==================== getByTaxType ====================

    @Test
    @DisplayName("Should get all PPH23 codes")
    void shouldGetAllPph23Codes() {
        TaxObjectCode[] pph23Codes = TaxObjectCode.getByTaxType(TaxType.PPH_23);

        assertThat(pph23Codes).isNotEmpty();
        assertThat(pph23Codes).allMatch(code -> code.getTaxType() == TaxType.PPH_23);
        assertThat(pph23Codes).contains(
                TaxObjectCode.PPH23_JASA_TEKNIK,
                TaxObjectCode.PPH23_JASA_KONSULTAN,
                TaxObjectCode.PPH23_SEWA_KENDARAAN
        );
    }

    @Test
    @DisplayName("Should get all PPH42 codes")
    void shouldGetAllPph42Codes() {
        TaxObjectCode[] pph42Codes = TaxObjectCode.getByTaxType(TaxType.PPH_42);

        assertThat(pph42Codes).isNotEmpty();
        assertThat(pph42Codes).allMatch(code -> code.getTaxType() == TaxType.PPH_42);
        assertThat(pph42Codes).contains(
                TaxObjectCode.PPH42_SEWA_TANAH_BANGUNAN,
                TaxObjectCode.PPH42_UMKM
        );
    }

    @Test
    @DisplayName("Should get PPH21 codes")
    void shouldGetPph21Codes() {
        TaxObjectCode[] pph21Codes = TaxObjectCode.getByTaxType(TaxType.PPH_21);

        assertThat(pph21Codes).isNotEmpty();
        assertThat(pph21Codes).contains(TaxObjectCode.PPH21_HONORARIUM);
    }

    // ==================== Enum Values ====================

    @Test
    @DisplayName("Should have all expected PPH23 service codes")
    void shouldHaveAllExpectedPph23ServiceCodes() {
        TaxObjectCode[] allCodes = TaxObjectCode.values();

        // Count PPH23 codes
        long pph23Count = java.util.Arrays.stream(allCodes)
                .filter(c -> c.getTaxType() == TaxType.PPH_23)
                .count();

        assertThat(pph23Count).isGreaterThan(10);
    }

    @Test
    @DisplayName("Should have construction service codes")
    void shouldHaveConstructionServiceCodes() {
        assertThat(TaxObjectCode.PPH42_JASA_KONSTRUKSI_KECIL).isNotNull();
        assertThat(TaxObjectCode.PPH42_JASA_KONSTRUKSI_MENENGAH).isNotNull();
        assertThat(TaxObjectCode.PPH42_JASA_KONSTRUKSI_KONSULTAN).isNotNull();
    }

    @Test
    @DisplayName("Should have correct construction rates")
    void shouldHaveCorrectConstructionRates() {
        // Per PP 9/2022 construction tax rates
        assertThat(TaxObjectCode.PPH42_JASA_KONSTRUKSI_KECIL.getDefaultRate())
                .isEqualByComparingTo(new BigDecimal("1.75"));
        assertThat(TaxObjectCode.PPH42_JASA_KONSTRUKSI_MENENGAH.getDefaultRate())
                .isEqualByComparingTo(new BigDecimal("2.65"));
        assertThat(TaxObjectCode.PPH42_JASA_KONSTRUKSI_KONSULTAN.getDefaultRate())
                .isEqualByComparingTo(new BigDecimal("3.50"));
    }

    @Test
    @DisplayName("Should have UMKM rate 0.5%")
    void shouldHaveUmkmRate() {
        // Per PP 55/2022 UMKM rate
        assertThat(TaxObjectCode.PPH42_UMKM.getDefaultRate())
                .isEqualByComparingTo(new BigDecimal("0.50"));
    }
}
