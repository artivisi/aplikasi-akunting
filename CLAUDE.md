# Claude Instructions

## Project Overview

Indonesian accounting application for small businesses. Spring Boot 4.0 + Thymeleaf + PostgreSQL.

## Current Status

- **Phase 0:** ✅ Complete (project setup, auth, CI/CD)
- **Phase 1:** 🔄 In Progress (Core Accounting MVP)
  - 1.1 COA: ✅ Complete
  - 1.2 Journal Entries: ✅ Complete
  - 1.3 Basic Reports: ✅ Complete
  - 1.4 Journal Templates: ✅ Complete
  - 1.6 Formula Support: ⏳ In Progress - See `TODO-FORMULA-SUPPORT.md`
  - 1.5 Transactions: ⏳ Pending (after 1.6) - See `TODO-TRANSACTIONS.md`
  - See `docs/06-implementation-plan.md` for full plan

## Key Files

| Purpose | Location |
|---------|----------|
| Implementation Plan | `docs/06-implementation-plan.md` |
| Formula Support TODO | `TODO-FORMULA-SUPPORT.md` |
| Transactions TODO | `TODO-TRANSACTIONS.md` |
| Entities | `src/main/java/.../entity/` |
| Services | `src/main/java/.../service/` |
| Controllers | `src/main/java/.../controller/` |
| Templates | `src/main/resources/templates/` |
| Migrations | `src/main/resources/db/migration/` |
| Functional Tests | `src/test/java/.../functional/` |

## Development Guidelines

1. **Feature completion criteria:** Item is only checked when verified by Playwright functional test
2. **No fallback/default values:** Throw errors instead of silently handling missing data
3. **Technical language:** No marketing speak, strictly technical documentation
4. **Test-driven:** Write functional tests for new features

## Running the App

```bash
# Run tests
./mvnw test

# Run specific functional test
./mvnw test -Dtest=ChartOfAccountSeedDataTest

# Run with visible browser (debugging)
./mvnw test -Dtest=ChartOfAccountSeedDataTest -Dplaywright.headless=false -Dplaywright.slowmo=100
```

## Database

- PostgreSQL via Testcontainers (tests)
- Flyway migrations: V001-V006
- Seed data: IT Services COA, admin user (admin/admin)

## Architecture

```
User → Controller (MVC) → Service → Repository → PostgreSQL
         ↓
    Thymeleaf Templates (HTMX + Alpine.js)
```

## Current Focus

Next: Formula Support (1.6) per implementation plan:
1. Create unified `FormulaEvaluator` service using SpEL
2. Create `FormulaContext` record for transaction data
3. Update `TemplateExecutionEngine` to use FormulaEvaluator
4. Update `TransactionService` to use FormulaEvaluator
5. Write unit tests for all formula patterns
6. Add test templates with formulas (PPN, PPh 23)

**Why 1.6 before 1.5:** Two inconsistent formula implementations exist (regex vs SpEL). Unifying them first prevents preview ≠ post bugs in Transactions.
