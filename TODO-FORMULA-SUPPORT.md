# TODO: Formula Support (1.6)

Unified formula evaluation for journal templates using SpEL.

**Reference:**
- `docs/06-implementation-plan.md` section 1.6
- `docs/99-decisions-and-questions.md` Decision #13

## Dependencies

- Journal Templates (1.4) ✅ Complete

## Problem Statement

**Two inconsistent formula implementations exist:**

| Location | Approach | Code |
|----------|----------|------|
| `TemplateExecutionEngine.evaluateFormula()` | Regex-based | `Pattern.compile("amount\\s*\\*\\s*([0-9.]+)")` |
| `TransactionService.calculateAmount()` | SpEL-based | `formula.replace("amount", value)` then SpEL parse |

**Risks if not unified:**
- Template preview shows different result than transaction post
- Regex cannot handle conditionals (`amount > 2000000 ? amount * 0.02 : 0`)
- Neither follows Decision #13 properly

---

## Decision #13 Specification

Per `docs/99-decisions-and-questions.md`:

```java
// Required approach
SimpleEvaluationContext.forReadOnlyDataBinding()

// Required: FormulaContext root object
FormulaContext { amount, rate, ... }

// Required formula patterns
amount * 0.11                              // PPN 11%
amount > 2000000 ? amount * 0.02 : 0       // PPh 23 threshold
transaction.amount * rate.ppn              // field references
```

---

## TODO Checklist

### 1. Create FormulaEvaluator Service

- [ ] Create `FormulaContext` record
  ```java
  public record FormulaContext(
      BigDecimal amount,
      // Future: rate object for tax rates
  ) {}
  ```

- [ ] Create `FormulaEvaluator` service
  ```java
  @Service
  public class FormulaEvaluator {
      BigDecimal evaluate(String formula, FormulaContext context);
      List<String> validate(String formula);
  }
  ```

- [ ] Use `SimpleEvaluationContext.forReadOnlyDataBinding()`
- [ ] Register `FormulaContext` as root object
- [ ] Handle null/blank formula → return amount

### 2. Supported Formula Patterns

- [ ] Simple pass-through: `amount`
- [ ] Percentage: `amount * 0.11`
- [ ] Division: `amount / 1.11`
- [ ] Addition: `amount + 1000`
- [ ] Subtraction: `amount - 1000`
- [ ] Conditional: `amount > 2000000 ? amount * 0.02 : 0`
- [ ] Constant: `1000000`

### 3. Formula Validation

- [ ] `FormulaEvaluator.validate(formula)` method
- [ ] Test formula against sample context before saving
- [ ] Return clear error messages for:
  - Syntax errors
  - Unknown variables
  - Division by zero potential

### 4. Update TemplateExecutionEngine

- [ ] Inject `FormulaEvaluator`
- [ ] Replace `evaluateFormula()` method body with FormulaEvaluator call
- [ ] Remove regex-based implementation
- [ ] Keep method signature for backward compatibility

### 5. Update TransactionService

- [ ] Inject `FormulaEvaluator`
- [ ] Replace `calculateAmount()` method body with FormulaEvaluator call
- [ ] Remove SpEL parser field
- [ ] Keep method signature for backward compatibility

### 6. Update JournalTemplateService

- [ ] Validate formula on template save
- [ ] Call `FormulaEvaluator.validate()` for each line
- [ ] Reject save if any formula invalid

### 7. Unit Tests

- [ ] `FormulaEvaluatorTest.java`
  - [ ] Test `amount` (pass-through)
  - [ ] Test `amount * 0.11` (percentage)
  - [ ] Test `amount / 1.11` (division)
  - [ ] Test `amount + 1000` (addition)
  - [ ] Test `amount - 1000` (subtraction)
  - [ ] Test `amount > 2000000 ? amount * 0.02 : 0` (conditional)
  - [ ] Test `1000000` (constant)
  - [ ] Test null formula → returns amount
  - [ ] Test blank formula → returns amount
  - [ ] Test invalid formula → throws exception
  - [ ] Test edge cases: zero, negative, large numbers

### 8. Test Templates with Formulas

- [ ] Create test migration `V903__formula_test_templates.sql`
- [ ] Add template: "Penjualan dengan PPN" (3 lines)
  - Debit: Bank/Kas → `amount`
  - Credit: Pendapatan → `amount / 1.11`
  - Credit: PPN Keluaran → `amount - (amount / 1.11)`
- [ ] Add template: "PPh 23 Jasa" (conditional)
  - Debit: Beban Jasa → `amount`
  - Credit: Kas/Bank → `amount - (amount > 2000000 ? amount * 0.02 : 0)`
  - Credit: Hutang PPh 23 → `amount > 2000000 ? amount * 0.02 : 0`

### 9. Functional Tests

- [ ] `FormulaTemplateTest.java`
  - [ ] Execute PPN template, verify calculated amounts
  - [ ] Execute PPh 23 template with amount > threshold
  - [ ] Execute PPh 23 template with amount < threshold (no withholding)
  - [ ] Preview shows correct calculated values

### 10. In-App Documentation

Users need guidance on formula syntax without leaving the app. Provide contextual help on the template form page.

#### 10.1 Formula Help Panel (Template Form)

- [ ] Create `templates/fragments/formula-help.html` fragment
- [ ] Add collapsible help section on template line form
- [ ] Include "Bantuan Formula" button/link next to formula input
- [ ] Show help panel inline (no modal - avoid context switch)

#### 10.2 Formula Syntax Reference

- [ ] Variable: `amount` - nilai transaksi yang diinput user
- [ ] Operator: `+`, `-`, `*`, `/`
- [ ] Kondisional: `kondisi ? nilai_jika_true : nilai_jika_false`
- [ ] Contoh format angka: `0.11` (bukan `11%`)

#### 10.3 Scenario Examples (Indonesian)

Each scenario should include:
- Deskripsi kasus
- Struktur jurnal (akun + formula)
- Contoh perhitungan dengan angka konkret

**Scenario 1: Penjualan Tunai Sederhana**
```
Kasus: Jual jasa Rp 10.000.000, terima tunai

Jurnal:
  Debit  - Kas/Bank      : amount     → Rp 10.000.000
  Kredit - Pendapatan    : amount     → Rp 10.000.000
```

**Scenario 2: Penjualan dengan PPN 11%**
```
Kasus: Jual jasa Rp 11.100.000 (sudah termasuk PPN)

Perhitungan:
  DPP (Dasar Pengenaan Pajak) = 11.100.000 / 1.11 = Rp 10.000.000
  PPN = 11.100.000 - 10.000.000 = Rp 1.100.000

Jurnal:
  Debit  - Bank          : amount                    → Rp 11.100.000
  Kredit - Pendapatan    : amount / 1.11             → Rp 10.000.000
  Kredit - PPN Keluaran  : amount - (amount / 1.11)  → Rp  1.100.000
```

**Scenario 3: Pembelian dengan PPN 11%**
```
Kasus: Beli perlengkapan Rp 5.550.000 (sudah termasuk PPN)

Perhitungan:
  DPP = 5.550.000 / 1.11 = Rp 5.000.000
  PPN = 5.550.000 - 5.000.000 = Rp 550.000

Jurnal:
  Debit  - Perlengkapan  : amount / 1.11             → Rp 5.000.000
  Debit  - PPN Masukan   : amount - (amount / 1.11)  → Rp   550.000
  Kredit - Kas/Bank      : amount                    → Rp 5.550.000
```

**Scenario 4: PPh 23 Jasa (2% jika > Rp 2.000.000)**
```
Kasus A: Bayar jasa konsultan Rp 5.000.000 (kena PPh 23)

Perhitungan:
  PPh 23 = 5.000.000 * 0.02 = Rp 100.000
  Dibayar ke vendor = 5.000.000 - 100.000 = Rp 4.900.000

Jurnal:
  Debit  - Beban Jasa       : amount                                           → Rp 5.000.000
  Kredit - Kas/Bank         : amount - (amount > 2000000 ? amount * 0.02 : 0)  → Rp 4.900.000
  Kredit - Hutang PPh 23    : amount > 2000000 ? amount * 0.02 : 0             → Rp   100.000

Kasus B: Bayar jasa Rp 1.500.000 (tidak kena PPh 23)

Jurnal:
  Debit  - Beban Jasa       : amount                                           → Rp 1.500.000
  Kredit - Kas/Bank         : amount - (amount > 2000000 ? amount * 0.02 : 0)  → Rp 1.500.000
  Kredit - Hutang PPh 23    : amount > 2000000 ? amount * 0.02 : 0             → Rp         0
```

**Scenario 5: Pembayaran Gaji dengan Potongan Tetap**
```
Kasus: Gaji Rp 8.000.000, potongan BPJS Rp 320.000 (fixed)

Jurnal:
  Debit  - Beban Gaji       : amount           → Rp 8.000.000
  Kredit - Kas/Bank         : amount - 320000  → Rp 7.680.000
  Kredit - Hutang BPJS      : 320000           → Rp   320.000
```

#### 10.4 UI Implementation

- [ ] Help panel design (collapsible, stays on page)
- [ ] Syntax reference table at top
- [ ] Scenario cards with copy button for formulas
- [ ] "Coba Formula" - live preview with sample amount input
- [ ] Indonesian language throughout

#### 10.5 Formula Preview on Template Form

- [ ] Add sample amount input field (default: Rp 10.000.000)
- [ ] Show calculated result next to each formula field
- [ ] Real-time update as user types formula
- [ ] Show error message if formula invalid

---

## Implementation Notes

### FormulaEvaluator Structure

```java
@Service
public class FormulaEvaluator {

    private final ExpressionParser parser = new SpelExpressionParser();

    public BigDecimal evaluate(String formula, FormulaContext context) {
        if (formula == null || formula.isBlank()) {
            return context.amount();
        }

        SimpleEvaluationContext evalContext = SimpleEvaluationContext
            .forReadOnlyDataBinding()
            .withRootObject(context)
            .build();

        Expression expression = parser.parseExpression(formula);
        Object result = expression.getValue(evalContext);

        return toBigDecimal(result);
    }

    public List<String> validate(String formula) {
        // Try to parse and evaluate with sample data
        // Return list of errors (empty if valid)
    }
}
```

### FormulaContext Structure

```java
public record FormulaContext(
    BigDecimal amount
    // Future additions:
    // BigDecimal ppnRate,
    // BigDecimal pph23Rate,
    // etc.
) {
    // Convenience factory
    public static FormulaContext of(BigDecimal amount) {
        return new FormulaContext(amount);
    }
}
```

---

## Files to Create

| File | Purpose |
|------|---------|
| `service/FormulaEvaluator.java` | Unified formula evaluation |
| `dto/FormulaContext.java` | Context record for formula variables |
| `templates/fragments/formula-help.html` | In-app documentation fragment |
| `test/.../FormulaEvaluatorTest.java` | Unit tests |
| `test/resources/db/testmigration/V903__formula_test_templates.sql` | Test templates |
| `test/.../functional/FormulaTemplateTest.java` | Functional tests |

## Files to Modify

| File | Change |
|------|--------|
| `service/TemplateExecutionEngine.java` | Use FormulaEvaluator |
| `service/TransactionService.java` | Use FormulaEvaluator |
| `service/JournalTemplateService.java` | Validate formula on save |
| `templates/templates/form.html` | Add formula help panel + live preview |
| `controller/JournalTemplateController.java` | Add formula preview endpoint |

---

## Current Status

**Status:** ⏳ Not Started

**Estimated effort:** 2-3 days
- Backend (FormulaEvaluator + tests): 1 day
- In-app documentation + UI: 1 day
- Integration + functional tests: 0.5-1 day

**Next steps:**
1. Create FormulaContext record
2. Create FormulaEvaluator service
3. Write unit tests
4. Update TemplateExecutionEngine
5. Update TransactionService
6. Add formula validation to template save
7. Create in-app documentation (formula-help.html)
8. Add formula preview to template form
9. Create test templates
10. Write functional tests
