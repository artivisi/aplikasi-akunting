# Implementation Plan

## Guiding Principles

1. **Go Live Fast** - MVP with core features only, add incrementally
2. **No Breaking Changes** - Database migrations must be backward compatible
3. **Feature Flags** - New features behind toggles until stable
4. **Data Safety** - Production data must never be corrupted or lost

## Phase Overview

| Phase | Focus | Status |
|-------|-------|--------|
| **0** | Project Setup | ✅ Complete |
| **1** | Core Accounting (MVP) - IT Services | 🔄 In Progress |
| **2** | Tax Compliance | ⏳ Not Started |
| **3** | Reconciliation | ⏳ Not Started |
| **4** | Payroll | ⏳ Not Started |
| **5** | Assets & Budget | ⏳ Not Started |
| **6+** | Other Industries, Advanced Features | ⏳ Not Started |

---

## Phase 0: Project Setup ✅

### 0.1 Development Environment
- [x] Spring Boot 4.0 project structure
- [x] PostgreSQL 17 local setup (Testcontainers for tests)
- [x] Flyway migration setup
- [x] Basic CI/CD pipeline (GitHub Actions)

### 0.2 Core Infrastructure
- [x] Spring Security configuration (session-based)
- [x] User authentication (login/logout)
- [x] Base entity classes (audit fields)
- [x] Exception handling (GlobalExceptionHandler)
- [x] Thymeleaf + HTMX base layout

### 0.3 Database Foundation
```sql
-- V001: Core tables
users
company_config
audit_logs
```

**Deliverable:** ✅ Running app with login, dashboard

**Deferred:**
- Local storage directory setup → Phase 2 (for document attachment)
- Soft delete → Phase 1.1 (with COA)

---

## Phase 1: Core Accounting (MVP)

**Goal:** Minimal viable accounting system to go live

**Scope:** IT Services industry only (primary use case for initial deployment)

### 1.1 Chart of Accounts ✅
- [x] Account entity and repository
- [x] Account types (asset, liability, equity, revenue, expense)
- [x] Hierarchical structure (parent/child)
- [x] Pre-seeded COA template: **IT Services only**
- [x] Soft delete (base entity with deleted_at, @SQLRestriction filter)
- [x] Account CRUD UI
- [x] Account activation/deactivation

**Note:** Other industry templates (Photography, Online Seller, General Freelancer) deferred to Phase 6+

```sql
-- V002: Chart of accounts
chart_of_accounts
```

### 1.2 Journal Templates (Basic)
- [ ] Template entity with versioning
- [ ] Template lines entity
- [ ] Category field (income, expense, payment, receipt, transfer)
- [ ] Cash flow category field
- [ ] System templates for IT Services (preloaded)
- [ ] Template list UI with category filter
- [ ] Template detail view

```sql
-- V003: Journal templates
journal_templates
journal_template_lines
```

**Note:** Tags, favorites, usage tracking deferred to Phase 1.5

### 1.3 Transactions
- [ ] Transaction entity with type and numbering
- [ ] Transaction sequences per type
- [ ] Status workflow (draft → posted → void)
- [ ] Transaction form (template-based)
- [ ] Account mapping from template
- [ ] Transaction list with filters
- [ ] Transaction detail view
- [ ] Void transaction (with reason)
- [ ] Account validation: cannot edit type if has transactions
- [ ] Account validation: cannot delete if has transactions
- [ ] Account dropdown: exclude inactive accounts

```sql
-- V004: Transactions
transactions
transaction_sequences
transaction_account_mappings
```

### 1.4 Journal Entries
- [ ] Journal entry entity
- [ ] Auto-generate from transaction + template
- [ ] Balance validation (debit = credit)
- [ ] Immutable after posting
- [ ] Journal entry list (general ledger view)

```sql
-- V005: Journal entries
journal_entries
```

### 1.5 Basic Formula Support
- [ ] SpEL integration with SimpleEvaluationContext
- [ ] FormulaContext class for transaction data
- [ ] Percentage calculations (100%, 11%, etc.)
- [ ] Simple arithmetic expressions
- [ ] Formula validation on template save

### 1.6 Basic Reports
- [ ] Trial Balance
- [ ] General Ledger
- [ ] Balance Sheet (Laporan Posisi Keuangan)
- [ ] Income Statement (Laporan Laba Rugi)
- [ ] PDF export
- [ ] Excel export

### 1.7 Account Balances (Materialized)
- [ ] Account balances entity
- [ ] Balance calculation on transaction post
- [ ] Period-based aggregation
- [ ] Balance recalculation utility

```sql
-- V006: Account balances
account_balances
```

### 1.8 Template Enhancements
- [ ] Template tags
- [ ] User favorites
- [ ] Usage tracking
- [ ] Search functionality
- [ ] Recently used list

```sql
-- V007: Template preferences
journal_template_tags
user_template_preferences
```

**Deliverable:** Working accounting system - can record transactions, generate reports

**Note:** Document attachment deferred to Phase 2. Store receipts in external folder during MVP.

### MVP Checklist for Go Live
- [ ] Can create transactions using templates
- [ ] Can generate Balance Sheet and Income Statement
- [ ] Can export reports to PDF/Excel
- [ ] Basic user management
- [ ] Database backup via pg_dump (no documents yet)
- [ ] Production deployment tested

---

## Phase 2: Tax Compliance

**Goal:** Indonesian tax features (PPN, PPh) + document attachment

### 2.0 Infrastructure (Deferred from Phase 0)
- [ ] Local storage directory setup

### 2.1 Document Attachment
- [ ] Document entity
- [ ] Local filesystem storage
- [ ] File upload UI (single file)
- [ ] Attach to transaction
- [ ] View/download document
- [ ] Thumbnail generation (images)

```sql
-- V008: Documents
documents
```

### 2.2 Tax Accounts Setup
- [ ] Pre-configured tax accounts in COA templates
- [ ] PPN Masukan / Keluaran accounts
- [ ] PPh 21, 23, 4(2), 25, 29 accounts

### 2.3 PPN Templates
- [ ] Penjualan + PPN Keluaran template
- [ ] Pembelian + PPN Masukan template
- [ ] PPN calculation (11%)
- [ ] Non-PKP templates (no PPN)

### 2.4 PPh Templates
- [ ] PPh 23 withholding templates (2%)
- [ ] PPh 4(2) templates
- [ ] Conditional formulas for thresholds

### 2.5 Tax Reports
- [ ] PPN Summary Report
- [ ] PPN Detail (Keluaran/Masukan)
- [ ] PPh 23 Withholding Report
- [ ] e-Faktur CSV export format
- [ ] e-Bupot export format

### 2.6 Fiscal Period Management
- [ ] Fiscal periods entity
- [ ] Period status (open, month_closed, tax_filed)
- [ ] Soft lock on month close
- [ ] Hard lock after tax filing
- [ ] Period close workflow

```sql
-- V009: Fiscal periods
fiscal_periods
```

### 2.7 Tax Calendar
- [ ] Tax deadline configuration
- [ ] Dashboard reminders
- [ ] Monthly checklist

### 2.8 Backup & Restore Utility
- [ ] Backup service (database + documents)
- [ ] Coordinated backup (consistent state between DB and files)
- [ ] Backup to local directory
- [ ] Restore utility with validation
- [ ] Backup scheduling (manual trigger for MVP)
- [ ] Backup manifest (metadata, timestamp, file list)

**Deliverable:** Tax-compliant accounting with export formats for DJP, document storage, and proper backup/restore

---

## Phase 3: Reconciliation

**Goal:** Bank and marketplace reconciliation

### 3.1 Bank Parser Infrastructure
- [ ] Bank parser config entity
- [ ] ConfigurableBankStatementParser class
- [ ] Column name matching with fallback
- [ ] Preload configs (BCA, BNI, BSI, CIMB)
- [ ] Admin UI for parser config

```sql
-- V010: Bank parser configs
bank_parser_configs
```

### 3.2 Bank Reconciliation
- [ ] Bank reconciliation entity
- [ ] Statement items entity
- [ ] CSV upload and parsing
- [ ] Auto-matching (exact date + amount)
- [ ] Fuzzy matching (±1 day)
- [ ] Manual matching UI
- [ ] Create missing transactions from statement
- [ ] Reconciliation report

```sql
-- V011: Bank reconciliation
bank_reconciliations
bank_statement_items
```

### 3.3 Marketplace Parser Infrastructure
- [ ] Marketplace parser config entity
- [ ] ConfigurableMarketplaceParser class
- [ ] Preload configs (Tokopedia, Shopee, Bukalapak, Lazada)

```sql
-- V012: Marketplace parser configs
marketplace_parser_configs
```

### 3.4 Marketplace Reconciliation
- [ ] Settlement upload and parsing
- [ ] Order matching
- [ ] Fee expense auto-creation
- [ ] Marketplace reconciliation report

### 3.5 Cash Flow Statement
- [ ] Cash flow report generation
- [ ] Group by cash_flow_category from templates
- [ ] Operating/Investing/Financing sections
- [ ] PDF/Excel export

**Deliverable:** Automated reconciliation for bank and marketplace transactions

---

## Phase 4: Payroll

**Goal:** Full payroll with PPh 21 and BPJS

### 4.1 Employee Management
- [ ] Employee entity
- [ ] Employee CRUD UI
- [ ] PTKP status configuration
- [ ] NPWP validation

```sql
-- V013: Employees
employees
```

### 4.2 Salary Components
- [ ] Salary component entity
- [ ] Component types (gaji pokok, tunjangan, BPJS, etc.)
- [ ] Preloaded component templates for IT Services
- [ ] Employee salary configuration UI

```sql
-- V014: Salary components
salary_components
```

### 4.3 BPJS Calculation
- [ ] BPJS Kesehatan rates (4% + 1%)
- [ ] BPJS Ketenagakerjaan rates (JKK, JKM, JHT, JP)
- [ ] Company vs employee portion
- [ ] Auto-calculation service

### 4.4 PPh 21 Calculation
- [ ] Progressive tax rates (5%-35%)
- [ ] PTKP deduction by status
- [ ] Biaya jabatan (5%, max 500rb)
- [ ] Monthly vs annual calculation
- [ ] PPh 21 calculator service

### 4.5 Payroll Processing
- [ ] Payroll run entity
- [ ] Payroll details entity
- [ ] Monthly payroll workflow
- [ ] Calculate all employees
- [ ] Review and adjust
- [ ] Post to journal entries
- [ ] Generate payslips

```sql
-- V015: Payroll
payroll_runs
payroll_details
```

### 4.6 Payroll Reports
- [ ] Payroll summary report
- [ ] PPh 21 monthly report
- [ ] BPJS report
- [ ] Payslip PDF generation

**Deliverable:** Complete payroll system with tax compliance

---

## Phase 5: Assets & Budget

**Goal:** Fixed asset tracking and budget management

### 5.1 Fixed Asset Register
- [ ] Fixed asset entity
- [ ] Asset categories
- [ ] Asset CRUD UI
- [ ] Purchase recording

```sql
-- V016: Fixed assets
fixed_assets
```

### 5.2 Depreciation
- [ ] Straight-line calculation
- [ ] Declining balance calculation
- [ ] Depreciation schedule
- [ ] Monthly depreciation batch job
- [ ] Auto-journal via templates

### 5.3 Asset Disposal
- [ ] Disposal workflow
- [ ] Gain/loss calculation
- [ ] Disposal journal entry

### 5.4 Budget Setup
- [ ] Budget entity
- [ ] Budget per account per period
- [ ] Budget CRUD UI
- [ ] Copy from previous period

```sql
-- V017: Budgets
budgets
```

### 5.5 Budget Reports
- [ ] Budget vs Actual report
- [ ] Variance analysis
- [ ] Over-budget highlighting
- [ ] PDF/Excel export

**Deliverable:** Asset management and budget tracking

---

## Phase 6+: Future Enhancements

### Additional Industry Templates
- [ ] Photography COA and journal templates
- [ ] Online Seller COA and journal templates
- [ ] General Freelancer COA and journal templates
- [ ] Industry-specific salary component templates

### Document Management Enhancements
- [ ] S3-compatible storage backend
- [ ] Image compression on upload
- [ ] PDF optimization
- [ ] ClamAV virus scanning
- [ ] Bulk upload
- [ ] Document access logging

### Advanced Features (As Needed)
- [ ] Multi-currency support
- [ ] API for mobile app
- [ ] Custom report builder
- [ ] Dashboard analytics
- [ ] Automated backups
- [ ] Admin: view soft-deleted records

### Custom Projects (Per Client Request)
- [ ] PJAP integration (e-Faktur, e-Bupot)
- [ ] Digital signature (PSrE)
- [ ] E-Meterai integration
- [ ] Payment gateway integration

---

## Database Migration Strategy

### Rules
1. **Always add, never remove** - Mark columns as deprecated, don't delete
2. **Nullable first** - New columns must be nullable or have defaults
3. **Backfill separately** - Data migration in separate step from schema change
4. **Test rollback** - Every migration must have tested rollback script

### Migration Naming
```
V{sequence}__{description}.sql
V001__create_users_table.sql
V002__create_chart_of_accounts.sql
V003__create_journal_templates.sql
```

### Adding Features Without Breaking Data

**Example: Adding tags to existing templates**

```sql
-- V007__add_template_tags.sql
-- Step 1: Create new table (no impact on existing data)
CREATE TABLE journal_template_tags (
    id UUID PRIMARY KEY,
    template_id UUID NOT NULL REFERENCES journal_templates(id),
    tag VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Step 2: Create index
CREATE INDEX idx_template_tags_template ON journal_template_tags(template_id);

-- No data migration needed - existing templates just have no tags
-- Application handles empty tags gracefully
```

**Example: Adding new column to existing table**

```sql
-- V009__add_fiscal_periods.sql
-- New table, no impact
CREATE TABLE fiscal_periods (...);

-- V010__add_period_status_to_transactions.sql
-- Add nullable column first
ALTER TABLE transactions
ADD COLUMN fiscal_period_id UUID REFERENCES fiscal_periods(id);

-- Backfill in application or separate migration
-- UPDATE transactions SET fiscal_period_id = ... WHERE fiscal_period_id IS NULL;
```

---

## Deployment Checklist

### Pre-Production
- [ ] All migrations tested on copy of production data
- [ ] Rollback scripts verified
- [ ] Backup taken before deployment
- [ ] Feature flags configured
- [ ] Monitoring alerts set up

### Production Deployment
1. Take database backup
2. Enable maintenance mode (if needed)
3. Run database migrations
4. Deploy new application version
5. Verify application starts
6. Run smoke tests
7. Disable maintenance mode
8. Monitor for errors

### Rollback Plan
1. If migration failed: restore from backup
2. If application failed: redeploy previous version
3. If data issue: run corrective migration

---

## Testing Strategy

### Per Phase
- [ ] Unit tests for business logic
- [ ] Integration tests for database operations
- [ ] Functional tests (Playwright) for critical paths
- [ ] Migration tests on sample data

### Critical Paths to Test
1. Transaction creation and posting
2. Journal entry balance validation
3. Report generation
4. User authentication
5. Period locking enforcement

---

## Go-Live Criteria

### MVP (Phase 1) Go-Live
- [ ] All Phase 1 features completed
- [ ] No critical bugs
- [ ] Performance acceptable (< 2s page load)
- [ ] Backup/restore tested
- [ ] Production environment ready
- [ ] Monitoring in place
- [ ] Support process defined

### Production Readiness
- [ ] Security review completed
- [ ] Data retention policy implemented
- [ ] User documentation ready
- [ ] Admin can manage users
- [ ] Can export all data (regulatory compliance)
