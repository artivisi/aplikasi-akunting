# TODO: Basic Reports (1.3)

Validate journal entries and provide financial output. Item is only checked when verified by Playwright functional test.

## Purpose

- Trial Balance is the ultimate test of double-entry correctness
- Provide financial statements for business decisions
- Reused by account balance display and validation checks

## Dependencies

- COA (1.1) ✅ Complete
- Journal Entries (1.2) ✅ Complete

---

## TODO List

### 0. Test Data Preparation ✅

Test data via Flyway test-only migration (`src/test/resources/db/testmigration/V901__report_test_data.sql`).

**Setup:**
- [x] Configure Flyway to include test migration location in test profile
- [x] Create `V901__report_test_data.sql` with comprehensive test data

**Test Data Requirements:**
- [x] Journal entries across multiple months (Jan-Jun 2024)
- [x] Entries for all account types (Asset, Liability, Equity, Revenue, Expense)
- [x] Mix of POSTED and VOID entries (verify VOID excluded from calculations)
- [x] Multiple entries per account (for running balance verification)
- [x] Balanced entries (debit = credit per journal)
- [x] DRAFT entry (should also be excluded from calculations)

**Test Transactions (V901):**
| Journal | Date | Description | Debit Account | Credit Account | Amount |
|---------|------|-------------|---------------|----------------|--------|
| JRN-2024-0001 | 2024-01-05 | Capital injection | Cash | Modal Disetor | 100,000,000 |
| JRN-2024-0002 | 2024-01-15 | Equipment purchase | Peralatan Komputer | Bank BCA | 20,000,000 |
| JRN-2024-0003 | 2024-02-10 | Consulting revenue | Bank BCA | Pendapatan Konsultasi | 15,000,000 |
| JRN-2024-0004 | 2024-02-28 | Salary expense | Beban Gaji | Cash | 8,000,000 |
| JRN-2024-0005 | 2024-03-15 | Development revenue | Bank BCA | Pendapatan Development | 25,000,000 |
| JRN-2024-0006 | 2024-03-20 | Cloud expense | Beban Server | Bank BCA | 2,000,000 |
| JRN-2024-0007 | 2024-04-05 | Consulting revenue | Bank BCA | Pendapatan Konsultasi | 12,000,000 |
| JRN-2024-0008 | 2024-04-15 | **VOID** | Beban Gaji | Cash | 5,000,000 |
| JRN-2024-0009 | 2024-05-31 | Salary expense | Beban Gaji | Cash | 8,000,000 |
| JRN-2024-0010 | 2024-06-10 | Equipment on credit | Peralatan Komputer | Hutang Usaha | 10,000,000 |
| JRN-2024-0011 | 2024-06-30 | Depreciation | Beban Penyusutan | Akum. Penyusutan | 1,000,000 |
| JRN-2024-0012 | 2024-06-30 | **DRAFT** | Beban Gaji | Cash | 3,000,000 |

**Expected Totals (for test assertions):**

Trial Balance (as of 2024-06-30, POSTED only, includes 2023 entries):
| Account | Debit Balance | Credit Balance |
|---------|---------------|----------------|
| Cash (1.1.01) | 134,000,000 | |
| Bank BCA (1.1.02) | 40,000,000 | |
| Peralatan Komputer (1.2.01) | 30,000,000 | |
| Akum. Peny. Peralatan (1.2.02) | | 1,000,000 |
| Hutang Usaha (2.1.01) | | 10,000,000 |
| Modal Disetor (3.1.01) | | 150,000,000 |
| Pendapatan Jasa Konsultasi (4.1.01) | | 37,000,000 |
| Pendapatan Jasa Development (4.1.02) | | 25,000,000 |
| Beban Gaji (5.1.01) | 16,000,000 | |
| Beban Server & Cloud (5.1.02) | 2,000,000 | |
| Beban Penyusutan (5.1.07) | 1,000,000 | |
| **TOTAL** | **223,000,000** | **223,000,000** |

Balance Sheet (as of 2024-06-30):
- Total Assets = 203,000,000 (Cash 134M + BCA 40M + Peralatan 30M - Akum 1M)
- Total Liabilities = 10,000,000
- Total Equity = 193,000,000 (Modal 150M + Prior Year 10M + Current Year NI 33M)
- **Assets = Liabilities + Equity** ✓ (203M = 10M + 193M)

Income Statement (2024-01-01 to 2024-06-30):
- Total Revenue = 52,000,000 (Konsultasi 27M + Development 25M)
- Total Expense = 19,000,000 (Gaji 16M + Server 2M + Penyusutan 1M)
- **Net Income = 33,000,000**

---

### 1. Trial Balance Report ✅

- [x] TrialBalanceService.calculateTrialBalance(asOfDate)
- [x] Calculate debit/credit totals per account
- [x] Show only accounts with activity
- [x] Trial Balance UI page (`/reports/trial-balance`)
- [x] Date selector (as of date)
- [x] Verify debit total = credit total
- [x] Playwright tests (TrialBalanceTest.java - 20 tests)

### 2. General Ledger Report ✅

Already implemented at `/journals` (Buku Besar):
- [x] JournalEntryService.getGeneralLedger() and getGeneralLedgerPaged()
- [x] General Ledger UI at `/journals` (linked from reports index)
- [x] Account selector dropdown
- [x] Date range filter
- [x] Opening balance, transactions, closing balance
- [x] Running balance column
- [x] Playwright tests (JournalEntryListTest.java - 20 tests)

### 3. Balance Sheet (Laporan Posisi Keuangan) ✅

- [x] ReportService.generateBalanceSheet(asOfDate) - already implemented
- [x] Group accounts by type: Asset, Liability, Equity
- [x] Calculate subtotals per account type
- [x] Verify Assets = Liabilities + Equity (balance check)
- [x] Balance Sheet UI page (`/reports/balance-sheet`) - updated with dynamic data
- [x] Date selector (as of date)
- [x] Current year earnings calculation
- [x] Prior year retained earnings calculation
- [x] Contra-asset handling (Accumulated Depreciation)
- [x] Playwright tests (BalanceSheetTest.java - 28 tests)

### 4. Income Statement (Laporan Laba Rugi)

- [ ] IncomeStatementService.generateIncomeStatement(startDate, endDate)
- [ ] Group accounts by type: Revenue, Expense
- [ ] Calculate Net Income = Revenue - Expense
- [ ] Income Statement UI page (`/reports/income-statement`)
- [ ] Date range filter
- [ ] Hierarchical account display

### 5. Export Features

- [ ] PDF export for all reports
- [ ] Excel export for all reports
- [ ] Print-friendly CSS

---

## Service Methods

```java
AccountBalanceCalculator {
    calculateTrialBalance(LocalDate asOf) → List<AccountBalance>
    calculateAccountBalance(UUID accountId, DateRange) → Balance
    getAccountTransactions(UUID accountId, DateRange) → List<JournalEntry>
}

BalanceSheetService {
    generateBalanceSheet(LocalDate asOf) → BalanceSheet
}

IncomeStatementService {
    generateIncomeStatement(LocalDate start, LocalDate end) → IncomeStatement
}
```

---

## Test Files

1. `TrialBalanceTest.java` - Trial balance calculation and display
2. `GeneralLedgerReportTest.java` - General ledger report
3. `BalanceSheetTest.java` - Balance sheet generation
4. `IncomeStatementTest.java` - Income statement generation
5. `ReportExportTest.java` - PDF/Excel export

---

## Notes

- Balances calculated on-the-fly from journal_entries (no materialized balances)
- Only POSTED entries included in calculations
- VOID entries excluded from calculations
