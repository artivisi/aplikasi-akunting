# TODO: Amortization Schedules (1.8)

## Purpose

Automate recurring period-end adjustments for prepaid expenses, unearned revenue, and intangible assets. Users create schedules manually; system generates journal entries each period.

## Dependencies

- COA (1.1) - accounts for prepaid/unearned items
- JournalEntryService (1.2) - creates journal entries

## Schedule Types

| Type | Indonesian | Example | Journal: Debit | Journal: Credit |
|------|------------|---------|----------------|-----------------|
| `prepaid_expense` | Beban Dibayar Dimuka | Insurance, rent, licenses | Beban (target) | Dibayar Dimuka (source) |
| `unearned_revenue` | Pendapatan Diterima Dimuka | Advance payments, retainers | Diterima Dimuka (source) | Pendapatan (target) |
| `intangible_asset` | Aset Tak Berwujud | Website, software dev | Beban Amortisasi (target) | Akum. Amortisasi (source) |
| `accrued_revenue` | Pendapatan Akrual | Monthly retainer billed quarterly | Piutang Pendapatan (source) | Pendapatan (target) |

---

## Implementation Tasks

### Phase 1: Database & Entities

#### 1.1 COA Additions (V002 migration update)
- [ ] Add account: 1.1.05 Asuransi Dibayar Dimuka (asset)
- [ ] Add account: 1.1.06 Sewa Dibayar Dimuka (asset)
- [ ] Add account: 1.1.07 Langganan Dibayar Dimuka (asset)
- [ ] Add account: 1.1.08 Piutang Pendapatan (asset)
- [ ] Add header: 1.3 Aset Tak Berwujud (asset)
- [ ] Add account: 1.3.01 Website & Software (asset)
- [ ] Add account: 1.3.02 Akum. Amortisasi Aset Tak Berwujud (contra asset)
- [ ] Add account: 2.1.04 Pendapatan Diterima Dimuka (liability)
- [ ] Add account: 5.1.08 Beban Asuransi (expense)
- [ ] Add account: 5.1.09 Beban Amortisasi (expense)

#### 1.2 Database Schema (V007 migration)
```sql
CREATE TABLE amortization_schedules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    schedule_type VARCHAR(50) NOT NULL,  -- prepaid_expense, unearned_revenue, intangible_asset, accrued_revenue

    source_account_id UUID NOT NULL REFERENCES chart_of_accounts(id),
    target_account_id UUID NOT NULL REFERENCES chart_of_accounts(id),

    total_amount DECIMAL(19,2) NOT NULL,
    period_amount DECIMAL(19,2) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    frequency VARCHAR(20) NOT NULL DEFAULT 'monthly',  -- monthly, quarterly
    total_periods INT NOT NULL,

    completed_periods INT NOT NULL DEFAULT 0,
    amortized_amount DECIMAL(19,2) NOT NULL DEFAULT 0,
    remaining_amount DECIMAL(19,2) NOT NULL,

    auto_post BOOLEAN NOT NULL DEFAULT false,
    post_day INT DEFAULT 1,  -- day of month to post (1-28)
    status VARCHAR(20) NOT NULL DEFAULT 'active',  -- active, completed, cancelled

    created_by UUID REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE amortization_entries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    schedule_id UUID NOT NULL REFERENCES amortization_schedules(id),
    period_number INT NOT NULL,
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    amount DECIMAL(19,2) NOT NULL,

    journal_entry_id UUID REFERENCES journal_entries(id),
    status VARCHAR(20) NOT NULL DEFAULT 'pending',  -- pending, posted, skipped

    generated_at TIMESTAMP,
    posted_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    UNIQUE(schedule_id, period_number)
);

CREATE INDEX idx_amort_schedules_status ON amortization_schedules(status);
CREATE INDEX idx_amort_schedules_type ON amortization_schedules(schedule_type);
CREATE INDEX idx_amort_entries_schedule ON amortization_entries(schedule_id);
CREATE INDEX idx_amort_entries_status ON amortization_entries(status);
```

#### 1.3 Entity Classes
- [ ] `AmortizationSchedule.java` entity
  - Fields: id, code, name, description, scheduleType, sourceAccount, targetAccount
  - Fields: totalAmount, periodAmount, startDate, endDate, frequency, totalPeriods
  - Fields: completedPeriods, amortizedAmount, remainingAmount
  - Fields: autoPost, postDay, status, createdBy, createdAt, updatedAt
  - Enum: ScheduleType (PREPAID_EXPENSE, UNEARNED_REVENUE, INTANGIBLE_ASSET, ACCRUED_REVENUE)
  - Enum: ScheduleStatus (ACTIVE, COMPLETED, CANCELLED)
  - Enum: Frequency (MONTHLY, QUARTERLY)
- [ ] `AmortizationEntry.java` entity
  - Fields: id, schedule, periodNumber, periodStart, periodEnd, amount
  - Fields: journalEntry, status, generatedAt, postedAt, createdAt
  - Enum: EntryStatus (PENDING, POSTED, SKIPPED)

#### 1.4 Repositories
- [ ] `AmortizationScheduleRepository.java`
  - findByStatus(status)
  - findByScheduleType(type)
  - findByStatusAndScheduleType(status, type)
  - findActiveSchedulesForPeriod(date)
- [ ] `AmortizationEntryRepository.java`
  - findByScheduleId(scheduleId)
  - findByScheduleIdAndStatus(scheduleId, status)
  - findPendingEntriesForDate(date)

---

### Phase 2: Service Layer

#### 2.1 AmortizationScheduleService
- [ ] `create(request)` - create schedule, calculate periods and amounts
- [ ] `calculatePeriods(startDate, endDate, frequency)` - calculate number of periods
- [ ] `calculatePeriodAmount(totalAmount, totalPeriods)` - divide evenly
- [ ] `generateEntries(schedule)` - pre-generate all entry records as pending
- [ ] `handleRounding(schedule)` - last period absorbs rounding difference
- [ ] `findById(id)` / `findAll(filters)`
- [ ] `cancel(id)` - mark schedule cancelled, prevent further entries
- [ ] `complete(id)` - mark schedule completed when all entries posted

#### 2.2 AmortizationEntryService
- [ ] `generateJournalEntry(entry)` - creates draft journal entry
- [ ] `postEntry(entryId)` - posts the journal entry
- [ ] `skipEntry(entryId)` - marks entry as skipped
- [ ] `findPendingForPeriod(date)` - entries due for a given date

#### 2.3 AmortizationBatchService
- [ ] `processMonthlyAmortization(asOfDate)` - batch job entry point
- [ ] For each active schedule with auto_post=true:
  - Find pending entry for current period
  - Generate and post journal entry
- [ ] For each active schedule with auto_post=false:
  - Generate draft journal entry only
- [ ] Update schedule counters after each entry

---

### Phase 3: Controller & UI

#### 3.1 AmortizationController (CRUD)
- [ ] `GET /amortization` - list schedules with filters (type, status)
- [ ] `GET /amortization/new` - form for new schedule
- [ ] `POST /amortization` - create schedule
- [ ] `GET /amortization/{id}` - schedule detail with entries
- [ ] `POST /amortization/{id}/cancel` - cancel schedule
- [ ] `DELETE /amortization/{id}` - delete (only if no posted entries)

#### 3.2 AmortizationEntryController
- [ ] `POST /amortization/{id}/entries/{entryId}/post` - post single entry
- [ ] `POST /amortization/{id}/entries/{entryId}/skip` - skip single entry
- [ ] `POST /amortization/{id}/entries/post-all-pending` - batch post pending

#### 3.3 Templates (Thymeleaf + HTMX)
- [ ] `amortization/list.html` - schedule list with filters
  - HTMX: filter by type/status swaps table fragment
  - Columns: code, name, type, total, remaining, status, actions
- [ ] `amortization/form.html` - create/edit form
  - Type selector (shows appropriate account suggestions)
  - Date range picker
  - Frequency dropdown
  - Auto-calculate preview (period amount, total periods)
  - Auto-post toggle with post day selector
- [ ] `amortization/detail.html` - schedule detail with entry table
  - Summary: total, amortized, remaining
  - Entry table: period, dates, amount, status, actions
  - HTMX: inline post/skip updates row without reload
- [ ] `fragments/amortization-table.html` - reusable table fragment

---

### Phase 4: Batch Job & Dashboard

#### 4.1 Scheduled Batch Job
- [ ] Spring `@Scheduled` job for monthly processing
- [ ] Run on configurable day of month (default: 1st)
- [ ] Process all due amortization entries
- [ ] Log processing results

#### 4.2 Period-End Dashboard Widget
- [ ] Pending amortization entries count
- [ ] Link to pending entries list
- [ ] Quick action: "Post All Pending"

---

### Phase 5: Functional Tests

#### 5.1 Schedule CRUD Tests
- [ ] Create prepaid expense schedule
- [ ] Create unearned revenue schedule
- [ ] View schedule list with filters
- [ ] View schedule detail with entries

#### 5.2 Entry Processing Tests
- [ ] Post single entry - verify journal entry created
- [ ] Skip entry - verify status updated
- [ ] Post all pending - batch operation
- [ ] Complete schedule - all entries posted

#### 5.3 Rounding Tests
- [ ] Total 100,000 / 3 periods = 33,333.33 + 33,333.33 + 33,333.34
- [ ] Verify last entry absorbs rounding difference

#### 5.4 Auto-Post Tests
- [ ] Schedule with auto_post=true
- [ ] Run batch job
- [ ] Verify entries posted automatically

---

## Workflow Summary

```
1. User creates schedule:
   - Name, type, accounts, total amount
   - Start/end date, frequency
   - Auto-post toggle

2. System calculates:
   - total_periods = months between start/end
   - period_amount = total_amount / total_periods
   - remaining_amount = total_amount

3. System generates entries (all periods, status=pending)

4. Monthly batch job:
   - If auto_post=true: generate & post journal entry
   - If auto_post=false: generate draft journal entry

5. User reviews drafts, posts manually if needed

6. When all entries posted:
   - Schedule status → completed
   - remaining_amount → 0
```

---

## Journal Entry Examples

### Prepaid Insurance (12 months, Rp 12,000,000)
```
Entry Date: 2025-01-31
Description: Amortisasi Asuransi - Januari 2025

Debit:  5.1.08 Beban Asuransi         Rp 1,000,000
Credit: 1.1.05 Asuransi Dibayar Dimuka  Rp 1,000,000
```

### Unearned Revenue (3 months retainer, Rp 30,000,000)
```
Entry Date: 2025-01-31
Description: Pengakuan Pendapatan Retainer - Januari 2025

Debit:  2.1.04 Pendapatan Diterima Dimuka  Rp 10,000,000
Credit: 4.1.01 Pendapatan Jasa             Rp 10,000,000
```

---

## Files to Create

```
src/main/java/com/artivisi/accountingfinance/
├── entity/
│   ├── AmortizationSchedule.java
│   └── AmortizationEntry.java
├── repository/
│   ├── AmortizationScheduleRepository.java
│   └── AmortizationEntryRepository.java
├── service/
│   ├── AmortizationScheduleService.java
│   ├── AmortizationEntryService.java
│   └── AmortizationBatchService.java
├── controller/
│   └── AmortizationController.java
└── dto/
    ├── AmortizationScheduleRequest.java
    └── AmortizationScheduleResponse.java

src/main/resources/
├── db/migration/
│   └── V007__amortization_schedules.sql
└── templates/amortization/
    ├── list.html
    ├── form.html
    ├── detail.html
    └── fragments/table.html

src/test/java/com/artivisi/accountingfinance/functional/
└── AmortizationScheduleTest.java
```

---

## Acceptance Criteria

1. User can create amortization schedule for prepaid expense
2. System calculates period amounts correctly (handles rounding)
3. All entries pre-generated with pending status
4. User can post entries manually
5. Auto-post schedules process automatically via batch job
6. Journal entries created with correct debit/credit based on type
7. Schedule marked complete when all entries posted
8. HTMX partial updates work (filters, inline post/skip)
9. Functional tests pass for all schedule types
