package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.entity.ChartOfAccount;
import com.artivisi.accountingfinance.entity.JournalEntry;
import com.artivisi.accountingfinance.entity.JournalTemplate;
import com.artivisi.accountingfinance.entity.JournalTemplateLine;
import com.artivisi.accountingfinance.enums.JournalPosition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class TemplateExecutionEngine {

    private final JournalEntryService journalEntryService;
    private final JournalTemplateService journalTemplateService;

    /**
     * Execute a template and create journal entries.
     * Creates entries in DRAFT status.
     */
    @Transactional
    public ExecutionResult execute(JournalTemplate template, ExecutionContext context) {
        List<String> validationErrors = validate(template, context);
        if (!validationErrors.isEmpty()) {
            throw new IllegalArgumentException("Template validation failed: " + String.join(", ", validationErrors));
        }

        List<JournalEntry> entries = buildJournalEntries(template, context);
        List<JournalEntry> savedEntries = journalEntryService.create(entries);

        // Record usage
        journalTemplateService.recordUsage(template.getId());

        return new ExecutionResult(
                savedEntries.get(0).getJournalNumber(),
                savedEntries
        );
    }

    /**
     * Preview template execution without saving.
     */
    public PreviewResult preview(JournalTemplate template, ExecutionContext context) {
        List<String> validationErrors = validate(template, context);
        if (!validationErrors.isEmpty()) {
            return new PreviewResult(false, validationErrors, List.of(), BigDecimal.ZERO, BigDecimal.ZERO);
        }

        List<JournalEntry> entries = buildJournalEntries(template, context);

        BigDecimal totalDebit = entries.stream()
                .map(JournalEntry::getDebitAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCredit = entries.stream()
                .map(JournalEntry::getCreditAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new PreviewResult(true, List.of(), entries, totalDebit, totalCredit);
    }

    /**
     * Validate template and execution context.
     */
    public List<String> validate(JournalTemplate template, ExecutionContext context) {
        List<String> errors = new ArrayList<>();

        if (template == null) {
            errors.add("Template is required");
            return errors;
        }

        if (!template.getActive()) {
            errors.add("Template is not active");
        }

        if (template.getLines() == null || template.getLines().size() < 2) {
            errors.add("Template must have at least 2 lines");
        }

        if (context == null) {
            errors.add("Execution context is required");
            return errors;
        }

        if (context.transactionDate() == null) {
            errors.add("Transaction date is required");
        }

        if (context.amount() == null) {
            errors.add("Amount is required");
        } else if (context.amount().compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("Amount must be positive");
        }

        if (context.description() == null || context.description().isBlank()) {
            errors.add("Description is required");
        }

        return errors;
    }

    private List<JournalEntry> buildJournalEntries(JournalTemplate template, ExecutionContext context) {
        List<JournalEntry> entries = new ArrayList<>();

        for (JournalTemplateLine line : template.getLines()) {
            JournalEntry entry = new JournalEntry();
            entry.setJournalDate(context.transactionDate());
            entry.setAccount(line.getAccount());

            // Build description: template description or line description or context description
            String lineDesc = line.getDescription();
            if (lineDesc != null && !lineDesc.isBlank()) {
                entry.setDescription(lineDesc + " - " + context.description());
            } else {
                entry.setDescription(context.description());
            }

            // Evaluate formula to get amount
            BigDecimal amount = evaluateFormula(line.getFormula(), context.amount());

            // Set debit or credit based on position
            if (line.getPosition() == JournalPosition.DEBIT) {
                entry.setDebitAmount(amount);
                entry.setCreditAmount(BigDecimal.ZERO);
            } else {
                entry.setDebitAmount(BigDecimal.ZERO);
                entry.setCreditAmount(amount);
            }

            entries.add(entry);
        }

        return entries;
    }

    /**
     * Evaluate formula expressions.
     * Supported formats:
     * - "amount" - returns the full amount
     * - "amount * 0.11" - multiplies amount by factor
     * - "amount / 2" - divides amount by factor
     * - Numeric literal like "1000000" - returns the constant
     */
    BigDecimal evaluateFormula(String formula, BigDecimal amount) {
        if (formula == null || formula.isBlank()) {
            return amount;
        }

        String trimmed = formula.trim().toLowerCase();

        // Handle simple "amount"
        if (trimmed.equals("amount")) {
            return amount;
        }

        // Handle multiplication: amount * factor
        Pattern multiplyPattern = Pattern.compile("amount\\s*\\*\\s*([0-9.]+)");
        Matcher multiplyMatcher = multiplyPattern.matcher(trimmed);
        if (multiplyMatcher.matches()) {
            BigDecimal factor = new BigDecimal(multiplyMatcher.group(1));
            return amount.multiply(factor).setScale(2, RoundingMode.HALF_UP);
        }

        // Handle division: amount / factor
        Pattern dividePattern = Pattern.compile("amount\\s*/\\s*([0-9.]+)");
        Matcher divideMatcher = dividePattern.matcher(trimmed);
        if (divideMatcher.matches()) {
            BigDecimal divisor = new BigDecimal(divideMatcher.group(1));
            return amount.divide(divisor, 2, RoundingMode.HALF_UP);
        }

        // Handle subtraction: amount - factor
        Pattern subtractPattern = Pattern.compile("amount\\s*-\\s*([0-9.]+)");
        Matcher subtractMatcher = subtractPattern.matcher(trimmed);
        if (subtractMatcher.matches()) {
            BigDecimal subtractValue = new BigDecimal(subtractMatcher.group(1));
            return amount.subtract(subtractValue).setScale(2, RoundingMode.HALF_UP);
        }

        // Handle addition: amount + factor
        Pattern addPattern = Pattern.compile("amount\\s*\\+\\s*([0-9.]+)");
        Matcher addMatcher = addPattern.matcher(trimmed);
        if (addMatcher.matches()) {
            BigDecimal addValue = new BigDecimal(addMatcher.group(1));
            return amount.add(addValue).setScale(2, RoundingMode.HALF_UP);
        }

        // Try to parse as numeric constant
        try {
            return new BigDecimal(trimmed).setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid formula: " + formula);
        }
    }

    // Records for input/output

    public record ExecutionContext(
            LocalDate transactionDate,
            BigDecimal amount,
            String description
    ) {}

    public record ExecutionResult(
            String journalNumber,
            List<JournalEntry> entries
    ) {}

    public record PreviewResult(
            boolean valid,
            List<String> errors,
            List<JournalEntry> entries,
            BigDecimal totalDebit,
            BigDecimal totalCredit
    ) {}
}
