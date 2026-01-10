# Coverage Improvement Plan

Goal: Increase code coverage from 49.66% to 75%

## Current State

- **Instruction Coverage**: 49.66% (35,269/71,026)
- **Line Coverage**: 49.02% (6,709/13,685)
- **Target**: 75% instruction coverage
- **Gap**: ~18,000 instructions need coverage

## Test Strategy Priority

1. **Playwright Functional Tests** (highest priority) - tests via UI interactions
2. **Integration Tests with Real DB** - tests services with actual database
3. **Unit Tests with Mocks** (avoid unless necessary)

## Coverage Gap Analysis

### Priority 1: Payroll Area (5,600 missed instructions)

| Class | Coverage | Missed | Test Approach |
|-------|----------|--------|---------------|
| PayrollReportService | 0% | 4,052 | Integration test + functional test via Reports |
| PayrollController | 13% | 664 | Functional test: complete payroll workflow |
| PayrollService | 6% | 540 | Integration test: payroll calculation scenarios |
| SalaryComponentService | 8% | 350 | Functional test: salary component CRUD |

**Test Classes to Create/Enhance**:
- `ServicePayrollLifecycleTest.java` - enhance with complete payroll run
- `PayrollServiceIntegrationTest.java` - integration tests for payroll calculations
- `PayrollReportServiceTest.java` - integration tests for report generation

### Priority 2: Accounting Core (4,500 missed instructions)

| Class | Coverage | Missed | Test Approach |
|-------|----------|--------|---------------|
| CoretaxExportService | 4% | 1,193 | Integration test: export e-Faktur, Bupot |
| JournalEntryService | 13% | 1,041 | Integration test: journal posting, ledger |
| TransactionService | 2% | 796 | Functional test: transaction CRUD |
| FiscalYearClosingService | 25% | 747 | Integration test: year-end closing |
| AmortizationScheduleService | 7% | 438 | Functional test: amortization creation |
| AmortizationEntryService | 0% | 331 | Functional test: amortization entries |

**Test Classes to Create/Enhance**:
- `CoretaxExportServiceTest.java` - integration tests for tax exports
- `JournalEntryServiceTest.java` - integration tests for journal entries
- `TransactionServiceTest.java` - enhance with more scenarios
- `FiscalYearClosingServiceTest.java` - integration tests for closing
- `ServiceAmortizationTest.java` - new functional test

### Priority 3: Controllers with 0% Coverage (550 missed instructions)

| Controller | Missed | Test Approach |
|------------|--------|---------------|
| PaymentTermController | 301 | Functional test: payment terms CRUD |
| MilestoneController | 250 | Functional test: milestone CRUD |

**Test Classes to Create**:
- `ServicePaymentTermTest.java` - new functional test for payment terms
- `ServiceMilestoneTest.java` - new functional test for milestones

### Priority 4: Project Area (560 missed instructions)

| Class | Coverage | Missed | Test Approach |
|-------|----------|--------|---------------|
| ProjectMilestoneService | 1% | 349 | Integration test: milestone operations |
| ProjectPaymentTermService | 0% | 209 | Integration test: payment term operations |

**Test Classes to Enhance**:
- `ServiceClientProjectTest.java` - add milestone and payment term workflows

### Priority 5: Settings & User Management (1,500 missed instructions)

| Class | Coverage | Missed | Test Approach |
|-------|----------|--------|---------------|
| SettingsController | 14% | 729 | Functional test: company settings |
| UserController | 17% | 477 | Functional test: user management CRUD |
| SelfServiceController | 18% | 312 | Functional test: employee self-service |

**Test Classes to Create/Enhance**:
- `ServiceUserManagementTest.java` - new functional test
- `ServiceSettingsTest.java` - enhance for company config
- `SelfServiceFunctionalTest.java` - new functional test

### Priority 6: Document & Invoice (1,200 missed instructions)

| Class | Coverage | Missed | Test Approach |
|-------|----------|--------|---------------|
| DocumentController | 19% | 407 | Functional test: document upload |
| InvoiceController | 27% | 385 | Functional test: invoice CRUD |
| DraftTransactionService | 11% | 392 | Integration test: draft handling |

**Test Classes to Create/Enhance**:
- `ServiceInvoiceTest.java` - new functional test
- `DocumentUploadTest.java` - new functional test
- `DraftTransactionServiceTest.java` - integration test

### Priority 7: Manufacturing (930 missed instructions)

| Class | Coverage | Missed | Test Approach |
|-------|----------|--------|---------------|
| ProductionOrderService | 4% | 517 | Functional test: production lifecycle |
| BillOfMaterialService | 6% | 414 | Functional test: BOM management |

**Test Classes to Enhance**:
- `MfgProductionTest.java` - add more production order scenarios
- `MfgBomTest.java` - add more BOM scenarios

### Priority 8: Other Services

| Class | Coverage | Missed | Test Approach |
|-------|----------|--------|---------------|
| FiscalPeriodService | 4% | 264 | Integration test: fiscal period management |
| TaxObjectCode | 0% | 373 | Unit test (enum methods) |
| VisionOcrService | 0% | 119 | Integration test (if Google Vision configured) |
| AmountToWordsUtil | 0% | 222 | Unit test (utility class) |
| RateLimitService | 8% | 241 | Integration test: rate limiting |

## Implementation Plan

### Phase 1: High-Impact Functional Tests

1. **ServicePayrollComprehensiveTest** - Complete payroll workflow
   - Create payroll run
   - Calculate salaries with BPJS
   - Generate payroll report
   - Export payroll data
   - Expected coverage gain: ~3,000 instructions

2. **ServiceAmortizationTest** - Amortization workflow
   - Create amortization schedule
   - Generate amortization entries
   - Post entries to journal
   - Expected coverage gain: ~700 instructions

3. **ServiceMilestonePaymentTermTest** - Project billing
   - Create payment terms for project
   - Create milestones
   - Update milestone status
   - Expected coverage gain: ~500 instructions

### Phase 2: Integration Tests for Services

4. **CoretaxExportServiceTest** - Tax export integration
   - Export e-Faktur Keluaran
   - Export e-Faktur Masukan
   - Export Bupot Unifikasi
   - Expected coverage gain: ~1,100 instructions

5. **JournalEntryServiceTest** - Journal operations
   - Post journal entry
   - Generate ledger
   - Reverse entry
   - Expected coverage gain: ~900 instructions

6. **FiscalYearClosingServiceTest** - Year-end closing
   - Preview closing entries
   - Execute closing
   - Generate closing report
   - Expected coverage gain: ~600 instructions

### Phase 3: User Management & Settings

7. **ServiceUserManagementTest** - User CRUD
   - Create user
   - Update user
   - Change password
   - Manage roles
   - Expected coverage gain: ~450 instructions

8. **ServiceSettingsTest** - Company settings
   - Update company config
   - Configure bank accounts
   - Set tax parameters
   - Expected coverage gain: ~700 instructions

### Phase 4: Document & Invoice

9. **ServiceInvoiceTest** - Invoice workflow
   - Create invoice
   - Send invoice
   - Record payment
   - Mark as paid/overdue
   - Expected coverage gain: ~350 instructions

10. **DocumentUploadTest** - Document management
    - Upload document
    - Download document
    - Delete document
    - Expected coverage gain: ~400 instructions

### Phase 5: Utility & Service Coverage

11. **AmountToWordsUtilTest** - Unit test
    - Test number to Indonesian words
    - Expected coverage gain: ~200 instructions

12. **TaxObjectCodeTest** - Unit test
    - Test enum methods
    - Expected coverage gain: ~350 instructions

13. **RateLimitServiceTest** - Integration test
    - Test rate limiting behavior
    - Expected coverage gain: ~220 instructions

## Actual Coverage Progress

| Phase | Tests Added | Instructions Covered | Coverage |
|-------|-------------|----------------------|----------|
| Initial | - | 35,269 | 49.66% |
| **Phase 1** ✅ | 45 functional | 36,605 | 51.54% (+1.88%) |
| **Phase 2** ✅ | 39 integration | 38,548 | 54.28% (+2.74%) |
| **Phase 3** ✅ | 34 functional | 38,787 | 54.61% (+0.33%) |
| **Phase 4** ✅ | 44 tests | 38,236 | 53.84% (-0.77%) |
| **Phase 5** ✅ | 78 unit tests | 39,100 | 55.05% (+1.21%) |

### Phase 1 Tests Created (45 tests)
- `ServicePayrollComprehensiveTest.java` - 15 tests
- `ServiceAmortizationTest.java` - 14 tests
- `ServiceMilestonePaymentTermTest.java` - 16 tests

### Phase 2 Tests Created (39 tests)
- `CoretaxExportServiceTest.java` - 10 tests (e-Faktur, e-Bupot export)
- `JournalEntryServiceIntegrationTest.java` - 16 tests (journal CRUD, ledger)
- `FiscalYearClosingServiceTest.java` - 13 tests (year-end closing)

### Phase 3 Tests Created (34 tests)
- `ServiceUserManagementTest.java` - 15 tests (user CRUD, password, toggle status)
- `ServiceSettingsComprehensiveTest.java` - 19 tests (company config, bank accounts, audit logs)

### Phase 4 Tests Created (44 tests)
- `ServiceInvoiceTest.java` - 19 tests (invoice CRUD, send, mark paid, cancel, print)
- `ServiceDocumentTest.java` - 9 tests (document view, download, transaction/invoice docs)
- `DraftTransactionServiceIntegrationTest.java` - 16 tests (draft CRUD, approve, reject, filtering)

### Phase 5 Tests Created (78 tests)
- `AmountToWordsUtilTest.java` - 37 tests (Indonesian number to words conversion)
- `TaxObjectCodeTest.java` - 18 tests (tax object code lookups, filtering)
- `RateLimitServiceTest.java` - 23 tests (login/API/general rate limiting)

Note: To reach 75% coverage, additional phases are needed focusing on:
- Export services (PayrollReportService, DataImportService, ReportExportService)
- Utility tests for AmountToWords, TaxObjectCode

## Test Data Requirements

All functional tests use industry seed packs:
- IT Service pack: `industry-seed/it-service/` - for service industry tests
- Online Seller pack: `industry-seed/online-seller/` - for seller tests
- Coffee Shop pack: `industry-seed/coffee-shop/` - for manufacturing tests
- Campus pack: `industry-seed/campus/` - for education tests

Additional test data migrations may be needed in:
- `src/test/resources/db/test/integration/` (V900-V912)

## Files Created/To Create

### Functional Tests
- [x] `src/test/java/.../functional/service/ServicePayrollComprehensiveTest.java` ✅
- [x] `src/test/java/.../functional/service/ServiceAmortizationTest.java` ✅
- [x] `src/test/java/.../functional/service/ServiceMilestonePaymentTermTest.java` ✅
- [x] `src/test/java/.../functional/service/ServiceUserManagementTest.java` ✅
- [x] `src/test/java/.../functional/service/ServiceSettingsComprehensiveTest.java` ✅
- [x] `src/test/java/.../functional/service/ServiceInvoiceTest.java` ✅
- [x] `src/test/java/.../functional/service/ServiceDocumentTest.java` ✅

### Integration Tests
- [x] `src/test/java/.../service/CoretaxExportServiceTest.java` ✅
- [x] `src/test/java/.../service/JournalEntryServiceIntegrationTest.java` ✅
- [x] `src/test/java/.../service/FiscalYearClosingServiceTest.java` ✅
- [x] `src/test/java/.../service/DraftTransactionServiceIntegrationTest.java` ✅
- [ ] `src/test/java/.../service/RateLimitServiceIntegrationTest.java`

### Unit Tests
- [x] `src/test/java/.../util/AmountToWordsUtilTest.java` ✅
- [x] `src/test/java/.../enums/TaxObjectCodeTest.java` ✅
- [x] `src/test/java/.../security/RateLimitServiceTest.java` ✅
