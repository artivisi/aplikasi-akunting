package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.dto.FormulaContext;
import com.artivisi.accountingfinance.entity.JournalEntry;
import com.artivisi.accountingfinance.entity.JournalTemplate;
import com.artivisi.accountingfinance.entity.JournalTemplateLine;
import com.artivisi.accountingfinance.entity.Transaction;
import com.artivisi.accountingfinance.enums.JournalPosition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TemplateExecutionEngine {

    private final JournalEntryService journalEntryService;
    private final JournalTemplateService journalTemplateService;
    private final FormulaEvaluator formulaEvaluator;

    /**
     * Execute a template and create journal entries via Transaction.
     * Creates entries in DRAFT status.
     */
    @Transactional
    public ExecutionResult execute(JournalTemplate template, ExecutionContext context) {
        List<String> validationErrors = validate(template, context);
        if (!validationErrors.isEmpty()) {
            throw new IllegalArgumentException("Template validation failed: " + String.join(", ", validationErrors));
        }

        List<JournalEntry> entries = buildJournalEntries(template, context);

        // Create Transaction as header
        Transaction transaction = new Transaction();
        transaction.setTransactionDate(context.transactionDate());
        transaction.setDescription(context.description());
        transaction.setReferenceNumber(context.referenceNumber());
        transaction.setJournalTemplate(template);

        Transaction saved = journalEntryService.create(transaction, entries);

        // Record usage
        journalTemplateService.recordUsage(template.getId());

        return new ExecutionResult(
                saved.getJournalEntries().get(0).getJournalNumber(),
                saved.getJournalEntries()
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

        List<PreviewEntry> previewEntries = new ArrayList<>();
        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;

        for (JournalTemplateLine line : template.getLines()) {
            BigDecimal amount = evaluateFormula(line.getFormula(), context.amount());
            BigDecimal debit = line.getPosition() == JournalPosition.DEBIT ? amount : BigDecimal.ZERO;
            BigDecimal credit = line.getPosition() == JournalPosition.CREDIT ? amount : BigDecimal.ZERO;

            previewEntries.add(new PreviewEntry(
                    line.getAccount().getAccountCode(),
                    line.getAccount().getAccountName(),
                    context.description(),
                    debit,
                    credit
            ));

            totalDebit = totalDebit.add(debit);
            totalCredit = totalCredit.add(credit);
        }

        return new PreviewResult(true, List.of(), previewEntries, totalDebit, totalCredit);
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
            entry.setAccount(line.getAccount());

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
     * Evaluate formula expressions using FormulaEvaluator.
     * Delegates to unified SpEL-based evaluation.
     *
     * @see FormulaEvaluator
     */
    BigDecimal evaluateFormula(String formula, BigDecimal amount) {
        return formulaEvaluator.evaluate(formula, FormulaContext.of(amount));
    }

    // Records for input/output

    public record ExecutionContext(
            LocalDate transactionDate,
            BigDecimal amount,
            String description,
            String referenceNumber
    ) {
        // Constructor without referenceNumber for backward compatibility
        public ExecutionContext(LocalDate transactionDate, BigDecimal amount, String description) {
            this(transactionDate, amount, description, null);
        }
    }

    public record ExecutionResult(
            String journalNumber,
            List<JournalEntry> entries
    ) {}

    public record PreviewEntry(
            String accountCode,
            String accountName,
            String description,
            BigDecimal debitAmount,
            BigDecimal creditAmount
    ) {}

    public record PreviewResult(
            boolean valid,
            List<String> errors,
            List<PreviewEntry> entries,
            BigDecimal totalDebit,
            BigDecimal totalCredit
    ) {}
}
