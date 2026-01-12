# Coverage Improvement Plan

Goal: Increase code coverage from 49.66% to 75%

## Current State (Updated 2026-01-11)

- **Instruction Coverage**: 66% (47,129/71,020)
- **Line Coverage**: 65% (8,915/13,682)
- **Branch Coverage**: 51%
- **Target**: 75% instruction coverage
- **Gap**: ~6,400 instructions need coverage (9% to go)

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
| **Incremental** ✅ | Various | 47,129 | 66% (+10.95%) |
| **Phase 6** 🔄 | TBD | Target: 53,284 | 75% (+9%) |

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

---

## Phase 6: Controller Coverage Focus (Target: 75%)

### Controllers with <50% Coverage (Sorted by Impact)

| Controller | Coverage | Missed | Priority | Test Approach |
|------------|----------|--------|----------|---------------|
| SettingsController | 31% | 584 | High | Functional: company settings, audit logs |
| PayrollController | 24% | 581 | High | Functional: payroll calculations, reports |
| JournalTemplateController | 37% | 492 | Medium | Functional: template CRUD, usage |
| UserController | 19% | 467 | High | Functional: user CRUD, password change |
| DocumentController | 19% | 407 | Medium | Functional: upload, download, delete |
| TaxExportController | 3% | 247 | High | Functional: e-Faktur, Bupot export |
| MilestoneController | 10% | 224 | Medium | Functional: milestone lifecycle |
| AmortizationController | 24% | 190 | Medium | Functional: schedule CRUD |
| BpjsCalculatorController | 4% | 129 | Low | Functional: BPJS calculation |
| DataImportController | 3% | 108 | Low | Functional: CSV import |
| SelfServiceController | 18% | 97 | Low | Functional: self-service portal |

### Phase 6.1: High-Priority Controllers (~1,900 instructions)

**Tests to Create:**

1. **TaxExportFunctionalTest.java** - e-Faktur and Bupot exports
   - Test e-Faktur Keluaran export
   - Test e-Faktur Masukan export
   - Test Bupot 21/26 export
   - Test Bupot Unifikasi export
   - Expected coverage gain: ~250 instructions

2. **BpjsCalculatorFunctionalTest.java** - BPJS contribution calculator
   - Test BPJS TK calculation
   - Test BPJS Kesehatan calculation
   - Test combined BPJS calculation
   - Expected coverage gain: ~130 instructions

3. **SettingsControllerFunctionalTest.java** - Settings pages
   - Test company settings view/edit
   - Test bank account management
   - Test tax configuration
   - Test audit log viewing
   - Expected coverage gain: ~600 instructions

4. **PayrollControllerFunctionalTest.java** - Payroll UI
   - Test payroll run creation
   - Test payroll calculation trigger
   - Test payroll approval workflow
   - Test payroll report generation
   - Expected coverage gain: ~580 instructions

5. **UserControllerFunctionalTest.java** - User management
   - Test user list view
   - Test user create form
   - Test user edit form
   - Test password change
   - Test role assignment
   - Expected coverage gain: ~470 instructions

### Phase 6.2: Medium-Priority Controllers (~1,300 instructions)

**Tests to Create:**

6. **JournalTemplateFunctionalTest.java** - Journal templates
   - Test template list view
   - Test template create
   - Test template edit
   - Test template usage in transactions
   - Expected coverage gain: ~500 instructions

7. **DocumentControllerFunctionalTest.java** - Document management
   - Test document list view
   - Test document upload
   - Test document download
   - Test document deletion
   - Expected coverage gain: ~400 instructions

8. **MilestoneControllerFunctionalTest.java** - Project milestones
   - Test milestone create
   - Test milestone status transitions
   - Test milestone edit
   - Test milestone delete
   - Expected coverage gain: ~220 instructions

9. **AmortizationControllerFunctionalTest.java** - Amortization
   - Test schedule creation
   - Test schedule viewing
   - Test entry generation
   - Expected coverage gain: ~190 instructions

### Phase 6.3: Low-Priority Controllers (~330 instructions)

**Tests to Create:**

10. **DataImportFunctionalTest.java** - Data import
    - Test import page display
    - Test CSV file selection
    - Test import execution
    - Expected coverage gain: ~110 instructions

11. **SelfServiceFunctionalTest.java** - Employee self-service
    - Test payslip view
    - Test leave request
    - Test profile update
    - Expected coverage gain: ~100 instructions

### Estimated Coverage After Phase 6

| Phase | Instructions | Coverage |
|-------|-------------|----------|
| Current | 47,129 | 66% |
| Phase 6.1 | +2,030 | 69% |
| Phase 6.2 | +1,310 | 71% |
| Phase 6.3 | +330 | 71.5% |
| Service gaps | +2,500 | 75% |

### Files to Create

- [x] `src/test/java/.../functional/TaxExportFunctionalTest.java` ✅ (22 tests)
- [x] `src/test/java/.../functional/BpjsCalculatorFunctionalTest.java` ✅ (15 tests)
- [x] `src/test/java/.../functional/SettingsControllerFunctionalTest.java` ✅ (22 tests)
- [x] `src/test/java/.../functional/PayrollControllerFunctionalTest.java` ✅ (20 tests)
- [x] `src/test/java/.../functional/UserControllerFunctionalTest.java` ✅ (24 tests)
- [ ] `src/test/java/.../functional/JournalTemplateFunctionalTest.java`
- [ ] `src/test/java/.../functional/DocumentControllerFunctionalTest.java`
- [ ] `src/test/java/.../functional/MilestoneControllerFunctionalTest.java`
- [ ] `src/test/java/.../functional/AmortizationControllerFunctionalTest.java`
- [ ] `src/test/java/.../functional/DataImportFunctionalTest.java`
- [ ] `src/test/java/.../functional/SelfServiceFunctionalTest.java`
