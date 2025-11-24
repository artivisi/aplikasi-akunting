# TODO: Chart of Accounts (1.1)

Complete COA features with functional tests. Item is only checked when verified by Playwright test.

## Current State

**Existing code:**
- Entity: `ChartOfAccount` with parent/child hierarchy
- Repository: `ChartOfAccountRepository` with queries
- Service: `ChartOfAccountService` with CRUD methods
- Controller: `ChartOfAccountsController` with MVC endpoints
- UI: `accounts/list.html`, `accounts/form.html`, `accounts/fragments.html`
- Migration: `V002__create_chart_of_accounts.sql` with IT Services seed data

**Existing tests:**
- `ChartOfAccountSeedDataTest` - verifies seed data display only

---

## TODO List

### 1. Account List Display
- [x] Display page title "Bagan Akun"
- [x] Display "Tambah Akun" button
- [x] Display root accounts from seed data (ASET, LIABILITAS, EKUITAS, PENDAPATAN, BEBAN)
- [x] Display account type badges
- [x] Expand/collapse hierarchy to show child accounts
- [x] Display edit button on each account
- [x] Display delete button on leaf accounts
- [x] Display activate/deactivate button

### 2. Create Account
- [x] Navigate to create form via "Tambah Akun" button
- [x] Display empty form with all fields
- [x] Submit form creates new account in database
- [x] New account appears in list after creation
- [x] Success message displayed after creation
- [x] Validation: account code is required
- [x] Validation: account name is required
- [x] Validation: account type is required
- [x] Validation: normal balance is required
- [x] Validation: account code must be unique
- [x] Create child account under parent
- [x] Child account inherits parent's account type

### 3. Edit Account
- [x] Navigate to edit form via edit button
- [x] Form displays existing account data
- [x] Submit form updates account in database
- [x] Updated data appears in list after save
- [x] Success message displayed after update
- [x] Validation errors displayed on invalid input
- [x] Cannot change account type if account has children
- [ ] Cannot change account type if account has transactions (future)

### 4. Delete Account (Soft Delete)
- [x] Delete button visible on leaf accounts only
- [x] Click delete sets deleted_at timestamp (soft delete)
- [x] Deleted account disappears from list
- [x] Success message displayed after deletion
- [x] Cannot delete account with children
- [ ] Cannot delete account with transactions (future)
- [x] Confirmation dialog before delete

### 5. Activate/Deactivate Account
- [x] Deactivate button visible on active accounts
- [x] Activate button visible on inactive accounts
- [x] Click deactivate changes status to inactive
- [x] Inactive accounts visually distinguished (grayed out)
- [ ] Inactive accounts not shown in transaction dropdowns (future)
- [x] Success message displayed after status change

### 6. Hierarchical Structure
- [x] Parent account can have children
- [x] Child accounts indented under parent
- [x] Expand button shows children
- [x] Collapse button hides children
- [x] Level calculation correct (parent level + 1)
- [x] Account code follows parent pattern

### 7. Seed Data Verification
- [x] 5 root accounts exist (ASET, LIABILITAS, EKUITAS, PENDAPATAN, BEBAN)
- [x] ASET has children (Aset Lancar, Aset Tetap)
- [x] Aset Lancar has leaf accounts (Kas, Bank BCA, Bank BNI)
- [x] All seed accounts have correct types
- [x] All seed accounts have correct normal balance
- [x] IT Services specific accounts exist

### 8. Soft Delete
- [x] Base entity with `deleted_at` timestamp field
- [x] JPA filter to auto-exclude deleted records (@SQLRestriction)
- [x] Migration to add `deleted_at` column to `chart_of_accounts`
- [x] Delete action sets `deleted_at` instead of removing row
- [x] Deleted accounts not visible in list (via @SQLRestriction)
- [x] Deleted accounts not available in dropdowns
- [ ] Admin can view deleted accounts (future)

---

## Test Files to Create

1. ~~`ChartOfAccountCreateTest.java` - Create account scenarios~~ ✅ Done
2. ~~`ChartOfAccountEditTest.java` - Edit account scenarios~~ ✅ Done
3. ~~`ChartOfAccountDeleteTest.java` - Delete/soft delete scenarios~~ ✅ Done
4. ~~`ChartOfAccountStatusTest.java` - Activate/deactivate scenarios~~ ✅ Done
5. `ChartOfAccountValidationTest.java` - Validation error scenarios

---

## Definition of Done

All items checked = COA feature complete. Each checkbox requires:
1. Feature works in UI
2. Playwright test verifies the feature
3. Test passes consistently
