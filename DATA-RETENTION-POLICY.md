# Data Retention Policy

## Overview

This document defines data retention periods and deletion policies for the accounting application, in compliance with Indonesian tax regulations.

## Legal Requirements

### UU No. 28 Tahun 2007 (KUP)
- Financial records must be retained for **10 years** from the end of the tax year
- Includes: books, records, documents (physical and electronic)
- Applies to: transactions, journal entries, invoices, tax reports

### PP No. 50 Tahun 2022 (PDPJP)
- Personal data must have defined retention periods
- Data subject has right to request deletion (with exceptions for legal obligations)

## Retention Periods by Data Type

| Data Type | Retention Period | Legal Basis | Notes |
|-----------|------------------|-------------|-------|
| **Financial Records** | 10 years | UU 28/2007 | Cannot be deleted |
| Transactions | 10 years | UU 28/2007 | Includes soft-deleted |
| Journal Entries | 10 years | UU 28/2007 | Includes voided entries |
| Invoices | 10 years | UU 28/2007 | |
| Chart of Accounts | Permanent | Business need | |
| **Tax Records** | 10 years | UU 28/2007 | |
| Tax Reports (PPN, PPh) | 10 years | UU 28/2007 | |
| Bukti Potong | 10 years | UU 28/2007 | |
| e-Faktur exports | 10 years | UU 28/2007 | |
| **Payroll Records** | 10 years | UU 28/2007 | |
| PayrollRun | 10 years | UU 28/2007 | |
| PayrollDetail | 10 years | UU 28/2007 | |
| Employee data | 10 years after termination | UU 28/2007 | |
| **Documents/Attachments** | 10 years | UU 28/2007 | Linked to transactions |
| **Operational Data** | | | |
| Audit Logs | 2 years | Internal policy | Can be purged |
| Draft Transactions (Telegram) | 90 days | Internal policy | Unprocessed drafts |
| User Sessions | 30 days | Internal policy | |
| **Configuration** | | | |
| Users | Permanent | Business need | Soft delete only |
| Templates | Permanent | Business need | Versioned |
| Clients | 10 years | UU 28/2007 | Linked to invoices |
| Projects | 10 years | UU 28/2007 | Linked to transactions |

## Soft Delete vs Hard Delete

### Soft Delete (Default Behavior)
All entities use soft delete via `deleted_at` timestamp:
- Record remains in database with `deleted_at` set
- Excluded from normal queries via `@SQLRestriction`
- Can be restored if needed
- Required for audit trail integrity

**Soft-deleted entities:**
- Account (Chart of Accounts)
- JournalEntry, JournalEntryLine
- Transaction
- JournalTemplate
- Client, Project, Invoice
- Employee
- Document
- User

### Hard Delete
Hard delete is NOT supported through the application UI. Manual database operations required.

**When hard delete may be considered:**
- Audit logs older than 2 years
- Draft transactions (Telegram) older than 90 days
- Test data in non-production environments

**Hard delete procedure:**
1. Create database backup
2. Document reason for deletion
3. Execute SQL directly (not through application)
4. Verify referential integrity
5. Update audit log

## Backup Retention

Configured in `deploy/ansible/roles/backup/`:

| Backup Type | Retention |
|-------------|-----------|
| Daily backups | 7 days |
| Weekly backups | 4 weeks |
| Monthly backups | 12 months |
| Yearly backups | 10 years |

## Document Storage

**Location:** `/var/lib/accounting/documents/`

**Structure:**
```
documents/
├── 2024/
│   ├── 01/
│   │   ├── {uuid}.pdf
│   │   └── {uuid}.jpg
│   └── 12/
└── 2025/
```

**Retention:** Same as linked transaction (10 years)

## Data Export

Users can export all their data via:
- **Reports:** PDF/Excel export per report type
- **Full Export:** Settings > Export All Data (ZIP archive)

Export includes:
- All transactions (CSV)
- All journal entries (CSV)
- All reports (PDF)
- All documents (original files)
- Audit logs (CSV)

## Audit Log Cleanup

Audit logs older than 2 years may be purged:

```sql
-- Manual cleanup (run as DBA)
DELETE FROM audit_logs
WHERE created_at < NOW() - INTERVAL '2 years';
```

**Schedule:** Manual, or automated monthly if table exceeds 1GB.

## Data Subject Rights (PDPJP)

### Right to Access
- Users can view all their data through the application
- Full data export available

### Right to Deletion
- **Cannot honor** for financial records within 10-year retention period (legal obligation exception)
- **Can honor** for non-financial personal data after retention period

### Right to Correction
- Users can update their profile information
- Financial records cannot be modified after posting (void and recreate instead)

## Implementation Notes

### Current Status
- [x] Soft delete on all entities
- [x] Backup automation (Ansible)
- [x] Document storage with retention
- [x] Report exports (PDF/Excel)
- [ ] Full data export (ZIP) - Planned
- [ ] Automated audit log cleanup - Deferred

### Compliance Checklist
- [x] 10-year financial record retention
- [x] Audit trail for all changes
- [x] Secure document storage
- [x] Backup with offsite copy
- [ ] Data export on request

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | 2024-11 | Initial policy |
