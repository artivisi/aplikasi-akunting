package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.dto.FormulaContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.SpelParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Unified formula evaluation service using SpEL.
 *
 * <p>Supported formula patterns:
 * <ul>
 *   <li>{@code amount} - pass-through</li>
 *   <li>{@code amount * 0.11} - percentage (PPN 11%)</li>
 *   <li>{@code amount / 1.11} - division (extract DPP from gross)</li>
 *   <li>{@code amount + 1000} - addition</li>
 *   <li>{@code amount - 1000} - subtraction</li>
 *   <li>{@code amount > 2000000 ? amount * 0.02 : 0} - conditional (PPh 23)</li>
 *   <li>{@code 1000000} - constant value</li>
 * </ul>
 *
 * <p>Per Decision #13: Uses SimpleEvaluationContext for secure sandbox evaluation.
 */
@Service
public class FormulaEvaluator {

    private final ExpressionParser parser = new SpelExpressionParser();

    /**
     * Evaluate a formula expression with the given context.
     *
     * @param formula the formula expression (e.g., "amount * 0.11")
     * @param context the context containing variables (e.g., amount)
     * @return the calculated result, scaled to 2 decimal places
     * @throws IllegalArgumentException if the formula is invalid
     */
    public BigDecimal evaluate(String formula, FormulaContext context) {
        if (formula == null || formula.isBlank()) {
            return context.amount();
        }

        String trimmed = formula.trim();

        // Handle simple "amount" case directly
        if (trimmed.equalsIgnoreCase("amount")) {
            return context.amount();
        }

        try {
            SimpleEvaluationContext evalContext = SimpleEvaluationContext
                    .forReadOnlyDataBinding()
                    .withRootObject(context)
                    .build();

            Expression expression = parser.parseExpression(trimmed);
            Object result = expression.getValue(evalContext);

            return toBigDecimal(result);
        } catch (SpelParseException e) {
            throw new IllegalArgumentException("Invalid formula syntax: " + formula + " - " + e.getMessage(), e);
        } catch (SpelEvaluationException e) {
            throw new IllegalArgumentException("Formula evaluation error: " + formula + " - " + e.getMessage(), e);
        }
    }

    /**
     * Validate a formula expression without evaluating it for real use.
     * Tests the formula against a sample context to check for errors.
     *
     * @param formula the formula expression to validate
     * @return list of error messages (empty if valid)
     */
    public List<String> validate(String formula) {
        List<String> errors = new ArrayList<>();

        if (formula == null || formula.isBlank()) {
            return errors; // Empty formula is valid (defaults to amount)
        }

        String trimmed = formula.trim();

        // Simple "amount" is always valid
        if (trimmed.equalsIgnoreCase("amount")) {
            return errors;
        }

        // Try to parse the expression
        try {
            parser.parseExpression(trimmed);
        } catch (SpelParseException e) {
            errors.add("Syntax error: " + e.getMessage());
            return errors;
        }

        // Try to evaluate with sample data
        try {
            FormulaContext sampleContext = FormulaContext.of(10_000_000L);
            SimpleEvaluationContext evalContext = SimpleEvaluationContext
                    .forReadOnlyDataBinding()
                    .withRootObject(sampleContext)
                    .build();

            Expression expression = parser.parseExpression(trimmed);
            Object result = expression.getValue(evalContext);

            // Check result is numeric
            if (result == null) {
                errors.add("Formula returned null result");
            } else if (!(result instanceof Number)) {
                errors.add("Formula must return a numeric value, got: " + result.getClass().getSimpleName());
            }
        } catch (SpelEvaluationException e) {
            errors.add("Evaluation error: " + e.getMessage());
        }

        return errors;
    }

    /**
     * Preview formula evaluation with a sample amount.
     * Useful for showing calculated result in the UI.
     *
     * @param formula the formula expression
     * @param sampleAmount the sample amount to use
     * @return the calculated result, or null if formula is invalid
     */
    public BigDecimal preview(String formula, BigDecimal sampleAmount) {
        try {
            return evaluate(formula, FormulaContext.of(sampleAmount));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private BigDecimal toBigDecimal(Object result) {
        if (result == null) {
            throw new IllegalArgumentException("Formula returned null result");
        }

        if (result instanceof BigDecimal bd) {
            return bd.setScale(2, RoundingMode.HALF_UP);
        }

        if (result instanceof Number num) {
            return BigDecimal.valueOf(num.doubleValue()).setScale(2, RoundingMode.HALF_UP);
        }

        throw new IllegalArgumentException("Formula must return a numeric value, got: " + result.getClass().getSimpleName());
    }
}
