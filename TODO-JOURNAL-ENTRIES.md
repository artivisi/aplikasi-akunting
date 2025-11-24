# TODO: Journal Entries (1.2)

Core double-entry bookkeeping engine. Item is only checked when verified by Playwright functional test.

## Purpose

- Accountants can record journal entries directly
- Core service reused by Reports (1.3), Templates (1.4), Transactions (1.5)

## Dependencies

- COA (1.1) ✅ Complete

---

## Current State

**Existing code:**
- Entity: `JournalEntry` - flat structure (each line is a record, linked to Transaction)
- Migration: `V005__create_journal_entries.sql` - table exists
- Repository: `JournalEntryRepository` - read queries only
- Service: `JournalEntryService` - read operations, GeneralLedger calculation
- Controller: `JournalEntryController` - read-only views
- Templates: `journals/list.html`, `journals/detail.html` - **PLACEHOLDER with hardcoded data**

**Design Decision Needed:**
Current design ties journal entries to Transactions. Plan suggests standalone entries.
Recommend: Keep current design but add manual entry support for adjustments.

---

## TODO List

### 1. Entity & Database ✅

- [x] Journal entry entity exists
- [x] Migration V005 exists
- [x] Repository with read queries
- [x] Add status field to journal_entries (DRAFT, POSTED, VOID)
- [x] Add posted_at, voided_at, void_reason fields
- [x] id_transaction already nullable (for manual entries)
- [x] JournalEntryStatus enum created

### 2. Service Layer ✅

- [x] JournalEntryService.findById() - exists
- [x] JournalEntryService.findByTransactionId() - exists
- [x] JournalEntryService.getGeneralLedger() - exists
- [x] JournalEntryService.create() - create manual entry (with auto journal number)
- [x] JournalEntryService.update() - update draft entry only
- [x] JournalEntryService.post() - validate and post entry
- [x] JournalEntryService.voidEntry() - void posted entry with reason
- [x] JournalEntryService.validateBalance() - debit must equal credit
- [x] Immutability enforcement - only draft entries can be edited

### 3. Journal Entry List UI (Buku Besar) ✅

- [x] Display page title "Buku Besar"
- [x] Controller endpoint exists (/journals)
- [x] Template exists (journals/list.html)
- [x] **Replace hardcoded data with dynamic data**
- [x] Account filter dropdown (dynamic from DB)
- [x] Date range filter (functional)
- [x] Display actual journal entries from DB
- [x] Show running balance per account
- [x] Click row navigates to detail
- [x] Pagination (functional)
- [x] Search filter (functional)

### 4. Create Journal Entry ✅

- [x] Navigate to create form via "Tambah Jurnal" button
- [x] Display form header fields (date, reference, description)
- [x] Display journal lines table (dynamic rows)
- [x] Add line button
- [x] Remove line button (disabled when only 2 lines)
- [x] Account dropdown (active accounts only)
- [x] Debit input field
- [x] Credit input field
- [x] Line description field
- [x] Display running total (debit/credit)
- [x] Display balance indicator (balanced/unbalanced)
- [x] Save as draft button
- [x] Save and post button (disabled when unbalanced)
- [x] Validation: at least 2 lines required
- [x] Validation: debit = credit before posting
- [x] Validation: date is required
- [ ] Success message after save (deferred - redirect to detail page)

### 5. Edit Journal Entry ✅

- [x] Navigate to edit form via edit button (draft only)
- [x] Form displays existing data
- [x] Can modify all fields
- [x] Save updates entry
- [x] Cannot edit posted entries (redirect to detail)
- [x] Cannot edit voided entries (redirect to detail)

### 6. View Journal Entry ✅

- [x] Detail template exists (journals/detail.html)
- [x] Controller endpoint exists (/journals/{id})
- [x] **Replace hardcoded data with dynamic data**
- [x] Detail view shows header fields from DB
- [x] Detail view shows all lines with accounts
- [x] Shows debit/credit totals (calculated)
- [x] Shows status with timestamp
- [x] Post button visible on draft entries
- [x] Void button visible on posted entries
- [x] Edit button visible on draft entries only
- [ ] Account impact section (before/after balances) - deferred

### 7. Post Journal Entry ✅

- [x] Post button on draft entry
- [x] Validation: debit must equal credit
- [x] Confirmation dialog before posting
- [x] Status changes to POSTED
- [x] posted_at timestamp set
- [x] Entry becomes immutable
- [x] Success message displayed (page reloads with Posted status)

### 8. Void Journal Entry

- [ ] Void button on posted entry
- [ ] Void reason input required
- [ ] Confirmation dialog with reason
- [ ] Status changes to VOID
- [ ] voided_at timestamp set
- [ ] void_reason stored
- [ ] Original entry preserved (audit trail)
- [ ] Success message displayed

### 9. Account Validation (from COA)

- [ ] Account dropdown excludes inactive accounts
- [ ] Cannot change account type if has journal entries
- [ ] Cannot delete account if has journal entries

### 10. Balance Validation ✅

- [x] Real-time balance check on form
- [x] Visual indicator when balanced (green)
- [x] Visual indicator when unbalanced (red)
- [x] Cannot post if unbalanced (Save & Post button disabled)
- [x] Error message shows difference amount

---

## Test Files

1. `JournalEntryListTest.java` - List display and filtering ✅ (20 tests)
2. `JournalListPage.java` - Page object for list tests ✅
3. `JournalEntryCreateTest.java` - Create entry scenarios ✅ (10 tests)
4. `JournalFormPage.java` - Page object for form tests ✅
5. `JournalEntryEditTest.java` - Edit draft scenarios ✅ (12 tests)
6. `JournalEntryViewTest.java` - View detail scenarios ✅ (18 tests)
7. `JournalDetailPage.java` - Page object for detail tests ✅
8. `JournalEntryPostTest.java` - Post and validation scenarios ✅ (13 tests)
9. `JournalEntryVoidTest.java` - Void scenarios (TODO)
10. `JournalEntryValidationTest.java` - Balance and field validation (TODO)

---

## Key Service Interface

```java
@Service
public class JournalEntryService {

    JournalEntry create(JournalEntryRequest request);

    JournalEntry update(UUID id, JournalEntryRequest request);

    JournalEntry post(UUID id);

    JournalEntry void(UUID id, String reason);

    void validateBalance(List<JournalEntryLine> lines);

    List<JournalEntry> findByDateRange(LocalDate from, LocalDate to);

    List<JournalEntry> findByStatus(JournalEntryStatus status);

    Page<JournalEntry> search(JournalEntrySearchCriteria criteria, Pageable pageable);
}
```

---

## Database Schema

```sql
-- V003: Journal entries
CREATE TABLE journal_entries (
    id UUID PRIMARY KEY,
    entry_date DATE NOT NULL,
    reference_number VARCHAR(50),
    description VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    posted_at TIMESTAMP,
    voided_at TIMESTAMP,
    void_reason VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),

    CONSTRAINT chk_status CHECK (status IN ('DRAFT', 'POSTED', 'VOID'))
);

CREATE TABLE journal_entry_lines (
    id UUID PRIMARY KEY,
    journal_entry_id UUID NOT NULL REFERENCES journal_entries(id),
    account_id UUID NOT NULL REFERENCES chart_of_accounts(id),
    line_number INTEGER NOT NULL,
    debit DECIMAL(15,2) NOT NULL DEFAULT 0,
    credit DECIMAL(15,2) NOT NULL DEFAULT 0,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_debit_credit CHECK (debit >= 0 AND credit >= 0),
    CONSTRAINT chk_one_side CHECK (debit = 0 OR credit = 0)
);

CREATE INDEX idx_je_entry_date ON journal_entries(entry_date);
CREATE INDEX idx_je_status ON journal_entries(status);
CREATE INDEX idx_je_reference ON journal_entries(reference_number);
CREATE INDEX idx_jel_journal_entry ON journal_entry_lines(journal_entry_id);
CREATE INDEX idx_jel_account ON journal_entry_lines(account_id);
```

---

## Definition of Done

All items checked = Journal Entry feature complete. Each checkbox requires:
1. Feature works in UI
2. Playwright test verifies the feature
3. Test passes consistently
