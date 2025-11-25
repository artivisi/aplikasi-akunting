# TODO: Journal Templates (1.4 Basic)

Predefined recipes for common transactions. Generates journal entries automatically.

**Reference:** `docs/06-implementation-plan.md` section 1.4, `docs/99-decisions-and-questions.md`

## Dependencies

- COA (1.1) ✅ Complete
- Journal Entries (1.2) ✅ Complete

## Scope (from implementation plan)

**In scope for 1.4:**
- Template entity with versioning
- Template lines entity (account mappings, debit/credit rules)
- Category field (income, expense, payment, receipt, transfer)
- Cash flow category field (operating, investing, financing)
- System templates for IT Services (preloaded via migration)
- Template CRUD UI
- Template list with category filter
- Template detail view
- Template execution (generates journal entry)

**NOT in scope (deferred):**
- Tags, favorites, usage tracking → 1.8 Template Enhancements
- SpEL formula support → 1.6 Formula Support (optional for MVP)
- Search functionality → 1.8

---

## TODO Checklist

### 1. Database Schema ✅

- [x] V003__create_journal_templates.sql migration
- [x] journal_templates table with versioning (version field)
- [x] journal_template_lines table

### 2. Entity Classes ✅

- [x] JournalTemplate entity with version field
- [x] JournalTemplateLine entity
- [x] TemplateCategory enum (INCOME, EXPENSE, PAYMENT, RECEIPT, TRANSFER, ADJUSTMENT)
- [x] CashFlowCategory enum (OPERATING, INVESTING, FINANCING)
- [x] JournalTemplateRepository
- [x] JournalTemplateLineRepository

### 3. Service Layer ✅

- [x] JournalTemplateService with CRUD operations
- [x] Version increment on update (Decision #10)
- [x] System template protection (cannot edit/delete is_system=true)
- [x] TemplateExecutionEngine.execute() → creates JournalEntry
- [x] TemplateExecutionEngine.preview() → shows what will be created (Decision #14)
- [x] TemplateExecutionEngine.validate()

**Versioning (Decision #10):** ✅
- [x] Version field increments on each update
- [x] Journal entries store template_version used at execution time
- [x] Display version number in detail view

### 4. System Templates (IT Services) ✅

- [x] Pre-seeded via V003 migration (12 templates)
- [x] Categories: INCOME, EXPENSE, PAYMENT, RECEIPT, TRANSFER, ADJUSTMENT

### 5. Template List UI ✅

- [x] Route GET /templates
- [x] Dynamic list from database (Thymeleaf th:each)
- [x] Category filter tabs (functional)
- [x] Link to detail page for each template

### 6. Template Detail View ✅

- [x] Route GET /templates/{id}
- [x] Display template info (name, category, cash flow category)
- [x] Display template lines (account, position, formula)
- [x] Execute button → links to execution page
- [x] Edit button (hidden for system templates)

### 7. Template CRUD UI ✅

- [x] Routes: GET /templates/new, GET /templates/{id}/edit
- [x] Form POST to /templates (create) and /templates/{id} (update)
- [x] Spring MVC form binding with POST endpoints
- [x] Account dropdown uses ${accounts} with th:each
- [x] Add/remove template lines (Alpine.js for dynamic rows)
- [x] Load existing data for edit mode with Thymeleaf th:each in JavaScript
- [x] System template protection (edit/delete buttons hidden)
- [x] Delete via POST /templates/{id}/delete

### 8. Template Execution UI ✅

- [x] Route GET /templates/{id}/execute
- [x] Execution form (date, amount, description)
- [x] Preview button → shows generated journal lines
- [x] Execute button → creates journal entry
- [x] Success message with link to created journal

### 9. Playwright Tests ✅

- [x] Template list page loads (2 tests)
- [x] Template detail page loads (3 tests)
- [x] Template execution flow (preview → execute) (4 tests)
- [x] Validation errors displayed (2 tests)
- [x] Template form page display (3 tests)
- [x] Template create flow (save, verify in list) (2 tests)
- [x] Template edit flow (version increments after save) (2 tests)
- [x] Template delete flow (non-system only) (1 test)
- [x] System template protection (edit/delete buttons hidden) (3 tests)
- [x] Version displayed in detail view (verified in all relevant tests)

**Total: 24 Playwright tests, all passing**

---

## Key Decisions Applied

| Decision | Description | Applied |
|----------|-------------|---------|
| #10 | Version templates, increment on edit | ✅ Service increments version |
| #14 | No conditional logic, use preview | ✅ Preview before execute |
| #2 | Configurable templates with preloaded defaults | ✅ System templates seeded |

---

## Current Status ✅ COMPLETE

**All features implemented and tested:**
- ✅ Backend complete (entities, service, execution engine)
- ✅ System templates seeded (12 templates for IT services)
- ✅ Template list with dynamic data and category filters
- ✅ Template detail view with version display
- ✅ Template CRUD (create, edit, delete with versioning)
- ✅ Template execution flow (preview → execute → journal created)
- ✅ System template protection (cannot edit/delete)
- ✅ 24 Playwright tests passing

**Implementation completed:**
- list.html made fully dynamic with Thymeleaf
- form.html uses Spring MVC POST pattern with proper form binding
- Account dropdown populated from database via ${accounts}
- Alpine.js loading existing template data for edit mode
- Version increments on each update
- Test migration (V902) with non-system template for CRUD testing
