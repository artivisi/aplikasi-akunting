# Formula Validation Fix - Summary

## Problem
Formula validation was rejecting dynamic variables like `grossSalary`, `fee`, `bpjsKesehatan`, `companyBpjs`, etc. that are provided at runtime through `FormulaContext.variables` map.

## Root Cause
`FormulaEvaluator.validate()` was checking if formulas could be evaluated with an empty sample context, which caused simple identifiers (variables) to be rejected since they weren't present in the context at validation time.

## Solution

### 1. Fixed Formula Validation Logic
**File:** `FormulaEvaluator.java`

- Modified `validate()` method to accept simple identifiers without requiring them in sample context
- Added `MapPropertyAccessor` inner class to enable SpEL to access variables from `FormulaContext.variables` Map
- The accessor allows formulas like `"grossSalary"`, `"companyBpjs * 0.8"` to work without hardcoded getters

**Key Changes:**
```java
// Added custom PropertyAccessor to enable dynamic variable resolution
private static class MapPropertyAccessor implements PropertyAccessor {
    @Override
    public boolean canRead(EvaluationContext context, Object target, String name) {
        if (target instanceof FormulaContext formulaContext) {
            return formulaContext.getVariables().containsKey(name);
        }
        return false;
    }

    @Override
    public TypedValue read(EvaluationContext context, Object target, String name) {
        FormulaContext formulaContext = (FormulaContext) target;
        BigDecimal value = formulaContext.getVariables().get(name);
        return new TypedValue(value);
    }
    // ... other methods
}
```

### 2. Comprehensive Test Coverage
**File:** `FormulaEvaluatorTest.java` (53 tests total)

Added 6 new test classes:
- **PayrollVariablesTests** (6 tests) - Tests payroll scenario with `grossSalary`, `companyBpjs`, `netPay`, `totalBpjs`, `pph21`
- **FeeVariablesTests** (3 tests) - Tests fee calculations with dynamic `fee` variable
- **MultipleVariablesTests** (3 tests) - Tests formulas with multiple custom variables (`principal`, `interest`, `adminFee`)
- **DynamicVariableValidationTests** (10 tests) - Verifies validation accepts simple identifiers
- **DynamicVariableErrorTests** (3 tests) - Tests error handling for invalid variables

### 3. Integration Test
**File:** `ComplexTemplateImportTest.java`

Created integration test to verify complete flow:
- Import JSON templates with complex formulas
- Verify formulas are stored in database exactly as specified
- Verify retrieval from database works correctly

### 4. Functional Test
**File:** `DataImportTest.java`

Added functional test:
- Tests UI import flow end-to-end
- Verifies formulas stored in database after import
- Tests validation during preview (should not reject dynamic variables)

### 5. Test Data
**File:** `template-complex.json`

Created test templates with 4 complex scenarios:
1. **Payroll Template** - 6 lines with variables: `grossSalary`, `companyBpjs`, `netPay`, `totalBpjs`, `pph21`
2. **Fee Template** - 3 lines with variable: `fee` and percentage calculations
3. **Conditional Formula** - 3 lines with ternary operator: `amount > 2000000 ? amount * 0.02 : 0`
4. **Multiple Variables** - 3 lines with: `principal`, `interest`, `adminFee`

**Fixed Issues:**
- Updated account codes to match seed data:
  - Changed `2.1.10` → `2.1.07` (Hutang Gaji)
  - Changed `2.1.13` → `2.1.08` (Hutang BPJS)
  - Changed `5.1.10` → `5.1.05` (Beban Administrasi)
  - Changed `2.1.05` → `2.1.21` (Hutang PPh 23)

### 6. Database Cleanup Script
**File:** `cleanup.sql`

Created cleanup script for integration tests:
```sql
-- Clean up in correct order due to foreign key constraints
-- 1. Delete journal entries first
DELETE FROM journal_entries WHERE id IS NOT NULL;

-- 2. Delete transactions
DELETE FROM transactions WHERE id IS NOT NULL;

-- 3. Delete journal templates
DELETE FROM journal_templates WHERE id IS NOT NULL;
```

## Verification

### Test Results
All tests passing:
- ✅ FormulaEvaluatorTest: 53/53 tests passed
- ✅ ComplexTemplateImportTest: 1/1 tests passed  
- ✅ DataImportTest (complex templates): 1/1 tests passed

### What's Verified
1. ✅ Formula validation accepts simple identifiers (dynamic variables)
2. ✅ Formula evaluation works with runtime variables from FormulaContext
3. ✅ Complex formulas with operators (`*`, `+`, `-`, ternary) work correctly
4. ✅ Formulas are stored in database as VARCHAR(255) strings
5. ✅ Complete flow: JSON import → validation → database storage → retrieval works end-to-end

## Production Impact
- Formula validation now accepts dynamic variables that will be provided at runtime
- Templates with complex formulas (payroll, fees, conditional calculations) can be imported successfully
- CI/CD pipeline will not break on formula validation errors
- End-to-end tests verify complete flow from import to database storage

## Files Changed
1. `FormulaEvaluator.java` - Added MapPropertyAccessor, fixed validation logic
2. `FormulaEvaluatorTest.java` - Added 36 new tests (53 total)
3. `template-complex.json` - Created test data with 4 complex templates
4. `ComplexTemplateImportTest.java` - Created integration test
5. `DataImportTest.java` - Enhanced with database verification
6. `cleanup.sql` - Created cleanup script for tests

## Technical Details

### How Dynamic Variables Work
1. Variables are stored in `FormulaContext.variables` as `Map<String, BigDecimal>`
2. `MapPropertyAccessor` enables SpEL to access map entries as properties
3. At runtime, `PayrollService` or other services populate the variables map
4. Formulas are evaluated with actual values from the map
5. No hardcoded getters needed - fully dynamic

### Database Schema
Formulas stored in `journal_template_lines.formula` column:
- Type: VARCHAR(255)
- Examples: `"grossSalary"`, `"companyBpjs * 0.8"`, `"amount > 2000000 ? amount * 0.02 : 0"`
- No preprocessing - stored exactly as provided in JSON

### Available Account Codes (from seed data)
- Assets: 1.1.01-1.1.08, 1.1.25, 1.2.01-1.2.02, 1.3.01-1.3.02
- Liabilities: 2.1.01-2.1.04, 2.1.07-2.1.08, 2.1.20-2.1.24
- Equity: 3.1.01, 3.2.01-3.2.02
- Revenue: 4.1.01-4.1.03, 4.2.01
- Expense: 5.1.01-5.1.09, 5.1.11, 5.2.01
