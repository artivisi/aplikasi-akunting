package com.artivisi.accountingfinance.dto;

import java.math.BigDecimal;

/**
 * Context object for formula evaluation.
 * Provides variables accessible within formula expressions.
 *
 * <p>Usage in formulas:
 * <ul>
 *   <li>{@code amount} - the transaction amount</li>
 *   <li>{@code amount * 0.11} - percentage calculation</li>
 *   <li>{@code amount > 2000000 ? amount * 0.02 : 0} - conditional</li>
 * </ul>
 */
public record FormulaContext(
        BigDecimal amount
) {
    /**
     * Factory method for creating context with amount.
     */
    public static FormulaContext of(BigDecimal amount) {
        return new FormulaContext(amount);
    }

    /**
     * Factory method for creating context with amount as long.
     */
    public static FormulaContext of(long amount) {
        return new FormulaContext(BigDecimal.valueOf(amount));
    }
}
