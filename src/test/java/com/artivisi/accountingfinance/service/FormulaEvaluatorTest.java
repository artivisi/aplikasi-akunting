package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.dto.FormulaContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("FormulaEvaluator Tests")
class FormulaEvaluatorTest {

    private FormulaEvaluator evaluator;

    @BeforeEach
    void setUp() {
        evaluator = new FormulaEvaluator();
    }

    @Nested
    @DisplayName("Basic Formula Evaluation")
    class BasicEvaluationTests {

        @Test
        @DisplayName("Should return amount for 'amount' formula")
        void shouldReturnAmountForAmountFormula() {
            FormulaContext context = FormulaContext.of(10_000_000L);

            BigDecimal result = evaluator.evaluate("amount", context);

            assertThat(result).isEqualByComparingTo("10000000");
        }

        @Test
        @DisplayName("Should return amount for null formula")
        void shouldReturnAmountForNullFormula() {
            FormulaContext context = FormulaContext.of(10_000_000L);

            BigDecimal result = evaluator.evaluate(null, context);

            assertThat(result).isEqualByComparingTo("10000000");
        }

        @Test
        @DisplayName("Should return amount for blank formula")
        void shouldReturnAmountForBlankFormula() {
            FormulaContext context = FormulaContext.of(10_000_000L);

            BigDecimal result = evaluator.evaluate("  ", context);

            assertThat(result).isEqualByComparingTo("10000000");
        }

        @Test
        @DisplayName("Should handle case insensitive 'AMOUNT'")
        void shouldHandleCaseInsensitiveAmount() {
            FormulaContext context = FormulaContext.of(10_000_000L);

            BigDecimal result = evaluator.evaluate("AMOUNT", context);

            assertThat(result).isEqualByComparingTo("10000000");
        }
    }

    @Nested
    @DisplayName("Percentage Calculations")
    class PercentageTests {

        @Test
        @DisplayName("Should calculate PPN 11% (amount * 0.11)")
        void shouldCalculatePpn11Percent() {
            FormulaContext context = FormulaContext.of(10_000_000L);

            BigDecimal result = evaluator.evaluate("amount * 0.11", context);

            assertThat(result).isEqualByComparingTo("1100000.00");
        }

        @Test
        @DisplayName("Should calculate PPh 23 2% (amount * 0.02)")
        void shouldCalculatePph23TwoPercent() {
            FormulaContext context = FormulaContext.of(5_000_000L);

            BigDecimal result = evaluator.evaluate("amount * 0.02", context);

            assertThat(result).isEqualByComparingTo("100000.00");
        }

        @Test
        @DisplayName("Should extract DPP from gross (amount / 1.11)")
        void shouldExtractDppFromGross() {
            // Gross amount including PPN 11%
            FormulaContext context = FormulaContext.of(11_100_000L);

            BigDecimal result = evaluator.evaluate("amount / 1.11", context);

            assertThat(result).isEqualByComparingTo("10000000.00");
        }

        @Test
        @DisplayName("Should calculate PPN from gross (amount - (amount / 1.11))")
        void shouldCalculatePpnFromGross() {
            FormulaContext context = FormulaContext.of(11_100_000L);

            BigDecimal result = evaluator.evaluate("amount - (amount / 1.11)", context);

            assertThat(result).isEqualByComparingTo("1100000.00");
        }
    }

    @Nested
    @DisplayName("Arithmetic Operations")
    class ArithmeticTests {

        @Test
        @DisplayName("Should calculate addition (amount + 1000)")
        void shouldCalculateAddition() {
            FormulaContext context = FormulaContext.of(10_000_000L);

            BigDecimal result = evaluator.evaluate("amount + 1000", context);

            assertThat(result).isEqualByComparingTo("10001000.00");
        }

        @Test
        @DisplayName("Should calculate subtraction (amount - 320000)")
        void shouldCalculateSubtraction() {
            FormulaContext context = FormulaContext.of(8_000_000L);

            BigDecimal result = evaluator.evaluate("amount - 320000", context);

            assertThat(result).isEqualByComparingTo("7680000.00");
        }

        @Test
        @DisplayName("Should return constant value")
        void shouldReturnConstantValue() {
            FormulaContext context = FormulaContext.of(10_000_000L);

            BigDecimal result = evaluator.evaluate("1000000", context);

            assertThat(result).isEqualByComparingTo("1000000.00");
        }
    }

    @Nested
    @DisplayName("Conditional Expressions")
    class ConditionalTests {

        @Test
        @DisplayName("Should apply PPh 23 when amount > threshold")
        void shouldApplyPph23WhenAboveThreshold() {
            // Amount 5,000,000 > 2,000,000 threshold
            FormulaContext context = FormulaContext.of(5_000_000L);

            BigDecimal result = evaluator.evaluate("amount > 2000000 ? amount * 0.02 : 0", context);

            assertThat(result).isEqualByComparingTo("100000.00");
        }

        @Test
        @DisplayName("Should return zero when amount <= threshold")
        void shouldReturnZeroWhenBelowThreshold() {
            // Amount 1,500,000 <= 2,000,000 threshold
            FormulaContext context = FormulaContext.of(1_500_000L);

            BigDecimal result = evaluator.evaluate("amount > 2000000 ? amount * 0.02 : 0", context);

            assertThat(result).isEqualByComparingTo("0.00");
        }

        @Test
        @DisplayName("Should calculate net payment with conditional PPh 23")
        void shouldCalculateNetPaymentWithConditionalPph23() {
            FormulaContext context = FormulaContext.of(5_000_000L);

            // Net = amount - PPh23 (if > threshold)
            BigDecimal result = evaluator.evaluate("amount - (amount > 2000000 ? amount * 0.02 : 0)", context);

            assertThat(result).isEqualByComparingTo("4900000.00");
        }

        @Test
        @DisplayName("Should return full amount when below threshold")
        void shouldReturnFullAmountWhenBelowThreshold() {
            FormulaContext context = FormulaContext.of(1_500_000L);

            BigDecimal result = evaluator.evaluate("amount - (amount > 2000000 ? amount * 0.02 : 0)", context);

            assertThat(result).isEqualByComparingTo("1500000.00");
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle zero amount")
        void shouldHandleZeroAmount() {
            FormulaContext context = FormulaContext.of(0L);

            BigDecimal result = evaluator.evaluate("amount * 0.11", context);

            assertThat(result).isEqualByComparingTo("0.00");
        }

        @Test
        @DisplayName("Should handle large amount")
        void shouldHandleLargeAmount() {
            FormulaContext context = FormulaContext.of(1_000_000_000_000L); // 1 trillion

            BigDecimal result = evaluator.evaluate("amount * 0.11", context);

            assertThat(result).isEqualByComparingTo("110000000000.00");
        }

        @Test
        @DisplayName("Should round to 2 decimal places")
        void shouldRoundToTwoDecimalPlaces() {
            FormulaContext context = FormulaContext.of(new BigDecimal("10000.333"));

            BigDecimal result = evaluator.evaluate("amount / 3", context);

            assertThat(result.scale()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("Error Handling")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should throw exception for invalid syntax")
        void shouldThrowExceptionForInvalidSyntax() {
            FormulaContext context = FormulaContext.of(10_000_000L);

            assertThatThrownBy(() -> evaluator.evaluate("amount * * 0.11", context))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid formula syntax");
        }

        @Test
        @DisplayName("Should throw exception for unknown variable")
        void shouldThrowExceptionForUnknownVariable() {
            FormulaContext context = FormulaContext.of(10_000_000L);

            assertThatThrownBy(() -> evaluator.evaluate("unknownVar * 0.11", context))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Formula evaluation error");
        }
    }

    @Nested
    @DisplayName("Formula Validation")
    class ValidationTests {

        @Test
        @DisplayName("Should return empty list for valid formula")
        void shouldReturnEmptyListForValidFormula() {
            List<String> errors = evaluator.validate("amount * 0.11");

            assertThat(errors).isEmpty();
        }

        @Test
        @DisplayName("Should return empty list for null formula")
        void shouldReturnEmptyListForNullFormula() {
            List<String> errors = evaluator.validate(null);

            assertThat(errors).isEmpty();
        }

        @Test
        @DisplayName("Should return empty list for 'amount' formula")
        void shouldReturnEmptyListForAmountFormula() {
            List<String> errors = evaluator.validate("amount");

            assertThat(errors).isEmpty();
        }

        @Test
        @DisplayName("Should return errors for invalid syntax")
        void shouldReturnErrorsForInvalidSyntax() {
            List<String> errors = evaluator.validate("amount * * 0.11");

            assertThat(errors).isNotEmpty();
            assertThat(errors.get(0)).containsIgnoringCase("syntax");
        }

        @Test
        @DisplayName("Should accept unknown variable during validation (will be checked at runtime)")
        void shouldAcceptUnknownVariableDuringValidation() {
            // Validation doesn't know all runtime variables, so it accepts simple identifiers
            // Runtime evaluation will fail if the variable isn't provided
            List<String> errors = evaluator.validate("unknownVar * 0.11");

            assertThat(errors).isEmpty();
        }

        @Test
        @DisplayName("Should validate conditional expressions")
        void shouldValidateConditionalExpressions() {
            List<String> errors = evaluator.validate("amount > 2000000 ? amount * 0.02 : 0");

            assertThat(errors).isEmpty();
        }
    }

    @Nested
    @DisplayName("Preview Function")
    class PreviewTests {

        @Test
        @DisplayName("Should preview formula with sample amount")
        void shouldPreviewFormulaWithSampleAmount() {
            BigDecimal result = evaluator.preview("amount * 0.11", new BigDecimal("10000000"));

            assertThat(result).isEqualByComparingTo("1100000.00");
        }

        @Test
        @DisplayName("Should return null for invalid formula")
        void shouldReturnNullForInvalidFormula() {
            BigDecimal result = evaluator.preview("invalid ** formula", new BigDecimal("10000000"));

            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("Dynamic Variables - Payroll Scenario")
    class PayrollVariablesTests {

        @Test
        @DisplayName("Should evaluate simple payroll variable - grossSalary")
        void shouldEvaluateGrossSalaryVariable() {
            FormulaContext context = FormulaContext.of(
                BigDecimal.valueOf(10_000_000),
                Map.of(
                    "grossSalary", BigDecimal.valueOf(10_000_000),
                    "companyBpjs", BigDecimal.valueOf(500_000),
                    "netPay", BigDecimal.valueOf(9_000_000),
                    "totalBpjs", BigDecimal.valueOf(800_000),
                    "pph21", BigDecimal.valueOf(200_000)
                )
            );

            BigDecimal result = evaluator.evaluate("grossSalary", context);

            assertThat(result).isEqualByComparingTo("10000000");
        }

        @Test
        @DisplayName("Should evaluate company BPJS Kesehatan (80% of companyBpjs)")
        void shouldEvaluateCompanyBpjsKesehatan() {
            FormulaContext context = FormulaContext.of(
                BigDecimal.valueOf(10_000_000),
                Map.of("companyBpjs", BigDecimal.valueOf(500_000))
            );

            BigDecimal result = evaluator.evaluate("companyBpjs * 0.8", context);

            assertThat(result).isEqualByComparingTo("400000.00");
        }

        @Test
        @DisplayName("Should evaluate company BPJS Ketenagakerjaan (20% of companyBpjs)")
        void shouldEvaluateCompanyBpjsKetenagakerjaan() {
            FormulaContext context = FormulaContext.of(
                BigDecimal.valueOf(10_000_000),
                Map.of("companyBpjs", BigDecimal.valueOf(500_000))
            );

            BigDecimal result = evaluator.evaluate("companyBpjs * 0.2", context);

            assertThat(result).isEqualByComparingTo("100000.00");
        }

        @Test
        @DisplayName("Should evaluate netPay variable")
        void shouldEvaluateNetPayVariable() {
            FormulaContext context = FormulaContext.of(
                BigDecimal.valueOf(10_000_000),
                Map.of("netPay", BigDecimal.valueOf(9_000_000))
            );

            BigDecimal result = evaluator.evaluate("netPay", context);

            assertThat(result).isEqualByComparingTo("9000000");
        }

        @Test
        @DisplayName("Should evaluate totalBpjs variable")
        void shouldEvaluateTotalBpjsVariable() {
            FormulaContext context = FormulaContext.of(
                BigDecimal.valueOf(10_000_000),
                Map.of("totalBpjs", BigDecimal.valueOf(800_000))
            );

            BigDecimal result = evaluator.evaluate("totalBpjs", context);

            assertThat(result).isEqualByComparingTo("800000");
        }

        @Test
        @DisplayName("Should evaluate pph21 variable")
        void shouldEvaluatePph21Variable() {
            FormulaContext context = FormulaContext.of(
                BigDecimal.valueOf(10_000_000),
                Map.of("pph21", BigDecimal.valueOf(200_000))
            );

            BigDecimal result = evaluator.evaluate("pph21", context);

            assertThat(result).isEqualByComparingTo("200000");
        }
    }

    @Nested
    @DisplayName("Dynamic Variables - Fee Scenario")
    class FeeVariablesTests {

        @Test
        @DisplayName("Should evaluate fee variable")
        void shouldEvaluateFeeVariable() {
            FormulaContext context = FormulaContext.of(
                BigDecimal.valueOf(10_000_000),
                Map.of("fee", BigDecimal.valueOf(5_000_000))
            );

            BigDecimal result = evaluator.evaluate("fee", context);

            assertThat(result).isEqualByComparingTo("5000000");
        }

        @Test
        @DisplayName("Should calculate PPN from fee (fee * 0.11)")
        void shouldCalculatePpnFromFee() {
            FormulaContext context = FormulaContext.of(
                BigDecimal.valueOf(10_000_000),
                Map.of("fee", BigDecimal.valueOf(5_000_000))
            );

            BigDecimal result = evaluator.evaluate("fee * 0.11", context);

            assertThat(result).isEqualByComparingTo("550000.00");
        }

        @Test
        @DisplayName("Should calculate DPP from fee (fee * 0.89)")
        void shouldCalculateDppFromFee() {
            FormulaContext context = FormulaContext.of(
                BigDecimal.valueOf(10_000_000),
                Map.of("fee", BigDecimal.valueOf(5_000_000))
            );

            BigDecimal result = evaluator.evaluate("fee * 0.89", context);

            assertThat(result).isEqualByComparingTo("4450000.00");
        }
    }

    @Nested
    @DisplayName("Dynamic Variables - Multiple Variables")
    class MultipleVariablesTests {

        @Test
        @DisplayName("Should evaluate addition of two variables (principal + interest)")
        void shouldEvaluateAdditionOfTwoVariables() {
            FormulaContext context = FormulaContext.of(
                BigDecimal.valueOf(10_000_000),
                Map.of(
                    "principal", BigDecimal.valueOf(8_000_000),
                    "interest", BigDecimal.valueOf(1_500_000)
                )
            );

            BigDecimal result = evaluator.evaluate("principal + interest", context);

            assertThat(result).isEqualByComparingTo("9500000.00");
        }

        @Test
        @DisplayName("Should evaluate addition of three variables")
        void shouldEvaluateAdditionOfThreeVariables() {
            FormulaContext context = FormulaContext.of(
                BigDecimal.valueOf(10_000_000),
                Map.of(
                    "principal", BigDecimal.valueOf(8_000_000),
                    "interest", BigDecimal.valueOf(1_500_000),
                    "adminFee", BigDecimal.valueOf(500_000)
                )
            );

            BigDecimal result = evaluator.evaluate("principal + interest + adminFee", context);

            assertThat(result).isEqualByComparingTo("10000000.00");
        }

        @Test
        @DisplayName("Should evaluate single custom variable (adminFee)")
        void shouldEvaluateSingleCustomVariable() {
            FormulaContext context = FormulaContext.of(
                BigDecimal.valueOf(10_000_000),
                Map.of("adminFee", BigDecimal.valueOf(500_000))
            );

            BigDecimal result = evaluator.evaluate("adminFee", context);

            assertThat(result).isEqualByComparingTo("500000");
        }
    }

    @Nested
    @DisplayName("Dynamic Variables - Validation")
    class DynamicVariableValidationTests {

        @Test
        @DisplayName("Should accept simple identifier without context (grossSalary)")
        void shouldAcceptSimpleIdentifierGrossSalary() {
            List<String> errors = evaluator.validate("grossSalary");

            assertThat(errors).isEmpty();
        }

        @Test
        @DisplayName("Should accept simple identifier without context (fee)")
        void shouldAcceptSimpleIdentifierFee() {
            List<String> errors = evaluator.validate("fee");

            assertThat(errors).isEmpty();
        }

        @Test
        @DisplayName("Should accept simple identifier without context (companyBpjs)")
        void shouldAcceptSimpleIdentifierCompanyBpjs() {
            List<String> errors = evaluator.validate("companyBpjs");

            assertThat(errors).isEmpty();
        }

        @Test
        @DisplayName("Should accept simple identifier without context (netPay)")
        void shouldAcceptSimpleIdentifierNetPay() {
            List<String> errors = evaluator.validate("netPay");

            assertThat(errors).isEmpty();
        }

        @Test
        @DisplayName("Should accept simple identifier without context (totalBpjs)")
        void shouldAcceptSimpleIdentifierTotalBpjs() {
            List<String> errors = evaluator.validate("totalBpjs");

            assertThat(errors).isEmpty();
        }

        @Test
        @DisplayName("Should accept simple identifier without context (pph21)")
        void shouldAcceptSimpleIdentifierPph21() {
            List<String> errors = evaluator.validate("pph21");

            assertThat(errors).isEmpty();
        }

        @Test
        @DisplayName("Should accept variable in expression (companyBpjs * 0.8)")
        void shouldAcceptVariableInExpression() {
            List<String> errors = evaluator.validate("companyBpjs * 0.8");

            assertThat(errors).isEmpty();
        }

        @Test
        @DisplayName("Should accept multiple variables in expression (principal + interest)")
        void shouldAcceptMultipleVariablesInExpression() {
            List<String> errors = evaluator.validate("principal + interest");

            assertThat(errors).isEmpty();
        }

        @Test
        @DisplayName("Should accept complex expression with multiple variables")
        void shouldAcceptComplexExpressionWithMultipleVariables() {
            List<String> errors = evaluator.validate("principal + interest + adminFee");

            assertThat(errors).isEmpty();
        }

        @Test
        @DisplayName("Should accept camelCase identifiers (bpjsKesehatan)")
        void shouldAcceptCamelCaseIdentifiers() {
            List<String> errors = evaluator.validate("bpjsKesehatan");

            assertThat(errors).isEmpty();
        }
    }

    @Nested
    @DisplayName("Dynamic Variables - Error Handling")
    class DynamicVariableErrorTests {

        @Test
        @DisplayName("Should throw exception when variable not provided in context")
        void shouldThrowExceptionWhenVariableNotProvided() {
            FormulaContext context = FormulaContext.of(BigDecimal.valueOf(10_000_000));

            assertThatThrownBy(() -> evaluator.evaluate("grossSalary", context))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Formula evaluation error");
        }

        @Test
        @DisplayName("Should throw exception when variable in expression not provided")
        void shouldThrowExceptionWhenVariableInExpressionNotProvided() {
            FormulaContext context = FormulaContext.of(BigDecimal.valueOf(10_000_000));

            assertThatThrownBy(() -> evaluator.evaluate("companyBpjs * 0.8", context))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Formula evaluation error");
        }

        @Test
        @DisplayName("Should throw exception when one of multiple variables missing")
        void shouldThrowExceptionWhenOneVariableMissing() {
            FormulaContext context = FormulaContext.of(
                BigDecimal.valueOf(10_000_000),
                Map.of("principal", BigDecimal.valueOf(8_000_000))
                // Missing "interest" variable
            );

            assertThatThrownBy(() -> evaluator.evaluate("principal + interest", context))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Formula evaluation error");
        }
    }
}
