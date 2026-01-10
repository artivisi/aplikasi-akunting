package com.artivisi.accountingfinance.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for AmountToWordsUtil.
 * Tests Indonesian number to words conversion.
 */
@DisplayName("AmountToWordsUtil - Indonesian Number to Words")
class AmountToWordsUtilTest {

    @Test
    @DisplayName("Should return 'nol rupiah' for null")
    void shouldReturnNolRupiahForNull() {
        String result = AmountToWordsUtil.toWords(null);
        assertThat(result).isEqualTo("nol rupiah");
    }

    @Test
    @DisplayName("Should return 'nol rupiah' for zero")
    void shouldReturnNolRupiahForZero() {
        String result = AmountToWordsUtil.toWords(BigDecimal.ZERO);
        assertThat(result).isEqualTo("nol rupiah");
    }

    @ParameterizedTest
    @MethodSource("singleDigitAmounts")
    @DisplayName("Should convert single digit amounts")
    void shouldConvertSingleDigitAmounts(BigDecimal amount, String expected) {
        String result = AmountToWordsUtil.toWords(amount);
        assertThat(result).isEqualTo(expected);
    }

    static Stream<Arguments> singleDigitAmounts() {
        return Stream.of(
                Arguments.of(new BigDecimal("1"), "Satu rupiah"),
                Arguments.of(new BigDecimal("5"), "Lima rupiah"),
                Arguments.of(new BigDecimal("9"), "Sembilan rupiah")
        );
    }

    @ParameterizedTest
    @MethodSource("teenAmounts")
    @DisplayName("Should convert teen amounts (11-19)")
    void shouldConvertTeenAmounts(BigDecimal amount, String expected) {
        String result = AmountToWordsUtil.toWords(amount);
        assertThat(result).isEqualTo(expected);
    }

    static Stream<Arguments> teenAmounts() {
        return Stream.of(
                Arguments.of(new BigDecimal("10"), "Sepuluh rupiah"),
                Arguments.of(new BigDecimal("11"), "Sebelas rupiah"),
                Arguments.of(new BigDecimal("12"), "Dua belas rupiah"),
                Arguments.of(new BigDecimal("15"), "Lima belas rupiah"),
                Arguments.of(new BigDecimal("19"), "Sembilan belas rupiah")
        );
    }

    @ParameterizedTest
    @MethodSource("tensAmounts")
    @DisplayName("Should convert tens amounts (20-99)")
    void shouldConvertTensAmounts(BigDecimal amount, String expected) {
        String result = AmountToWordsUtil.toWords(amount);
        assertThat(result).isEqualTo(expected);
    }

    static Stream<Arguments> tensAmounts() {
        return Stream.of(
                Arguments.of(new BigDecimal("20"), "Dua puluh rupiah"),
                Arguments.of(new BigDecimal("25"), "Dua puluh lima rupiah"),
                Arguments.of(new BigDecimal("50"), "Lima puluh rupiah"),
                Arguments.of(new BigDecimal("99"), "Sembilan puluh sembilan rupiah")
        );
    }

    @ParameterizedTest
    @MethodSource("hundredsAmounts")
    @DisplayName("Should convert hundreds amounts (100-999)")
    void shouldConvertHundredsAmounts(BigDecimal amount, String expected) {
        String result = AmountToWordsUtil.toWords(amount);
        assertThat(result).isEqualTo(expected);
    }

    static Stream<Arguments> hundredsAmounts() {
        return Stream.of(
                Arguments.of(new BigDecimal("100"), "Seratus rupiah"),
                Arguments.of(new BigDecimal("150"), "Seratus lima puluh rupiah"),
                Arguments.of(new BigDecimal("200"), "Dua ratus rupiah"),
                Arguments.of(new BigDecimal("500"), "Lima ratus rupiah"),
                Arguments.of(new BigDecimal("999"), "Sembilan ratus sembilan puluh sembilan rupiah")
        );
    }

    @ParameterizedTest
    @MethodSource("thousandsAmounts")
    @DisplayName("Should convert thousands amounts (1,000-999,999)")
    void shouldConvertThousandsAmounts(BigDecimal amount, String expected) {
        String result = AmountToWordsUtil.toWords(amount);
        assertThat(result).isEqualTo(expected);
    }

    static Stream<Arguments> thousandsAmounts() {
        return Stream.of(
                Arguments.of(new BigDecimal("1000"), "Seribu rupiah"),
                Arguments.of(new BigDecimal("1500"), "Seribu lima ratus rupiah"),
                Arguments.of(new BigDecimal("2000"), "Dua ribu rupiah"),
                Arguments.of(new BigDecimal("5000"), "Lima ribu rupiah"),
                Arguments.of(new BigDecimal("10000"), "Sepuluh ribu rupiah"),
                Arguments.of(new BigDecimal("100000"), "Seratus ribu rupiah"),
                Arguments.of(new BigDecimal("500000"), "Lima ratus ribu rupiah")
        );
    }

    @ParameterizedTest
    @MethodSource("millionsAmounts")
    @DisplayName("Should convert millions amounts (1,000,000+)")
    void shouldConvertMillionsAmounts(BigDecimal amount, String expected) {
        String result = AmountToWordsUtil.toWords(amount);
        assertThat(result).isEqualTo(expected);
    }

    static Stream<Arguments> millionsAmounts() {
        return Stream.of(
                Arguments.of(new BigDecimal("1000000"), "Satu juta rupiah"),
                Arguments.of(new BigDecimal("5000000"), "Lima juta rupiah"),
                Arguments.of(new BigDecimal("10000000"), "Sepuluh juta rupiah"),
                Arguments.of(new BigDecimal("100000000"), "Seratus juta rupiah")
        );
    }

    @ParameterizedTest
    @MethodSource("billionsAmounts")
    @DisplayName("Should convert billions amounts (1,000,000,000+)")
    void shouldConvertBillionsAmounts(BigDecimal amount, String expected) {
        String result = AmountToWordsUtil.toWords(amount);
        assertThat(result).isEqualTo(expected);
    }

    static Stream<Arguments> billionsAmounts() {
        return Stream.of(
                Arguments.of(new BigDecimal("1000000000"), "Satu miliar rupiah"),
                Arguments.of(new BigDecimal("5000000000"), "Lima miliar rupiah"),
                Arguments.of(new BigDecimal("10000000000"), "Sepuluh miliar rupiah")
        );
    }

    @Test
    @DisplayName("Should convert trillions amounts")
    void shouldConvertTrillionsAmounts() {
        String result = AmountToWordsUtil.toWords(new BigDecimal("1000000000000"));
        assertThat(result).isEqualTo("Satu triliun rupiah");
    }

    @Test
    @DisplayName("Should handle complex Indonesian amounts")
    void shouldHandleComplexIndonesianAmounts() {
        // Common invoice amounts
        String result1 = AmountToWordsUtil.toWords(new BigDecimal("1234567"));
        assertThat(result1).contains("juta");
        assertThat(result1).endsWith("rupiah");

        // Typical salary amount
        String result2 = AmountToWordsUtil.toWords(new BigDecimal("7500000"));
        assertThat(result2).isEqualTo("Tujuh juta lima ratus ribu rupiah");
    }

    @Test
    @DisplayName("Should round decimal amounts")
    void shouldRoundDecimalAmounts() {
        String result = AmountToWordsUtil.toWords(new BigDecimal("1500.75"));
        assertThat(result).isEqualTo("Seribu lima ratus satu rupiah");
    }

    @Test
    @DisplayName("Should capitalize first letter")
    void shouldCapitalizeFirstLetter() {
        String result = AmountToWordsUtil.toWords(new BigDecimal("1"));
        assertThat(result.charAt(0)).isUpperCase();
    }
}
