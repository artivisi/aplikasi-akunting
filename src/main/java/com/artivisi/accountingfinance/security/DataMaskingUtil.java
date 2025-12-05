package com.artivisi.accountingfinance.security;

/**
 * Utility class for masking sensitive data in UI views.
 * Helps protect PII (Personally Identifiable Information) from unauthorized viewing.
 */
public final class DataMaskingUtil {

    private static final char MASK_CHAR = '*';

    private DataMaskingUtil() {
        // Utility class
    }

    /**
     * Masks a NIK KTP (16 digits), showing first 4 and last 4 digits.
     * Example: 3201234567890001 -> 3201********0001
     */
    public static String maskNik(String nik) {
        if (nik == null || nik.length() < 8) {
            return nik;
        }
        return maskMiddle(nik, 4, 4);
    }

    /**
     * Masks an NPWP (formatted XX.XXX.XXX.X-XXX.XXX), showing first 4 and last 4 chars.
     * Example: 12.345.678.9-012.345 -> 12.3***********345
     */
    public static String maskNpwp(String npwp) {
        if (npwp == null || npwp.length() < 8) {
            return npwp;
        }
        return maskMiddle(npwp, 4, 3);
    }

    /**
     * Masks a bank account number, showing first 3 and last 3 digits.
     * Example: 1234567890 -> 123****890
     */
    public static String maskBankAccount(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 6) {
            return accountNumber;
        }
        return maskMiddle(accountNumber, 3, 3);
    }

    /**
     * Masks a phone number, showing first 4 and last 3 digits.
     * Example: 081234567890 -> 0812*****890
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return maskMiddle(phone, 4, 3);
    }

    /**
     * Masks a BPJS number, showing first 3 and last 3 digits.
     * Example: 0001234567890 -> 000*******890
     */
    public static String maskBpjsNumber(String bpjsNumber) {
        if (bpjsNumber == null || bpjsNumber.length() < 6) {
            return bpjsNumber;
        }
        return maskMiddle(bpjsNumber, 3, 3);
    }

    /**
     * Masks an email address, showing first 2 chars before @ and domain.
     * Example: john.doe@example.com -> jo******@example.com
     */
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 2) {
            return email;
        }
        String local = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        return local.substring(0, 2) + repeat(MASK_CHAR, local.length() - 2) + domain;
    }

    /**
     * Generic masking function that masks the middle portion of a string.
     *
     * @param value The string to mask
     * @param showFirst Number of characters to show at the beginning
     * @param showLast Number of characters to show at the end
     * @return The masked string
     */
    public static String maskMiddle(String value, int showFirst, int showLast) {
        if (value == null) {
            return null;
        }
        int length = value.length();
        if (length <= showFirst + showLast) {
            return value;
        }
        int maskLength = length - showFirst - showLast;
        return value.substring(0, showFirst) +
               repeat(MASK_CHAR, maskLength) +
               value.substring(length - showLast);
    }

    private static String repeat(char c, int count) {
        if (count <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            sb.append(c);
        }
        return sb.toString();
    }
}
