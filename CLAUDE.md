# Claude Instructions

## Project Overview

Indonesian accounting application for small businesses. Spring Boot 4.0 + Thymeleaf + PostgreSQL.

## Current Status

- **Phase 0:** ✅ Complete (project setup, auth, CI/CD)
- **Phase 1:** ✅ Complete (Core Accounting MVP)
  - 1.1 COA: ✅ Complete
  - 1.2 Journal Entries: ✅ Complete
  - 1.3 Basic Reports: ✅ Complete
  - 1.4 Journal Templates: ✅ Complete
  - 1.5 Transactions: ✅ Complete
  - 1.6 Formula Support: ✅ Complete
  - 1.7 Template Enhancements: ✅ Complete
  - 1.7.5 HTMX Optimization: ✅ Complete
  - 1.8 Amortization Schedules: ✅ Complete
  - 1.9 Project Tracking: ✅ Complete
  - 1.10 Dashboard KPIs: ✅ Complete
  - 1.11 User Manual: ✅ Complete
  - 1.12 Data Import: ✅ Complete
  - 1.13 Deployment & Operations: ✅ Complete
- **Phase 2:** Tax Compliance 🚧 In Progress
  - 2.0 Refactoring: ✅ Complete
  - 2.1 Document Attachment: ✅ Complete
  - 2.2 Telegram Receipt Import: ✅ Complete
  - 2.3-2.5 Tax Accounts/Templates: ✅ Complete
  - 2.6 Tax Reports: ✅ Complete
  - 2.7 Fiscal Period Management: ✅ Complete
  - 2.8 Tax Calendar: ⏳ Next
  - 2.9 Backup & Restore: ✅ Complete
  - See `docs/06-implementation-plan.md` for full plan

## Key Files

| Purpose | Location |
|---------|----------|
| Implementation Plan | `docs/06-implementation-plan.md` |
| User Manual | `docs/user-manual/*.md` |
| Entities | `src/main/java/.../entity/` |
| Services | `src/main/java/.../service/` |
| Controllers | `src/main/java/.../controller/` |
| Templates | `src/main/resources/templates/` |
| Migrations | `src/main/resources/db/migration/` |
| Functional Tests | `src/test/java/.../functional/` |
| Infrastructure (Pulumi) | `deploy/pulumi/` |
| Configuration (Ansible) | `deploy/ansible/` |

## Development Guidelines

1. **Feature completion criteria:** Item is only checked when verified by Playwright functional test
2. **No fallback/default values:** Throw errors instead of silently handling missing data
3. **Technical language:** No marketing speak, strictly technical documentation
4. **Test-driven:** Write functional tests for new features
5. **Migration strategy:** Modify existing migrations instead of creating new ones (pre-production)

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
- Flyway migrations: V001-V010
- Seed data: IT Services COA, admin user (admin/admin)

## Architecture

```
User → Controller (MVC) → Service → Repository → PostgreSQL
         ↓
    Thymeleaf Templates (HTMX + Alpine.js)
```

## Current Focus

Phase 1 (Core Accounting MVP) is complete. All features implemented:
- COA, Journal Entries, Reports, Templates, Transactions
- Formula Support, Template Enhancements, HTMX Optimization
- Amortization Schedules, Project Tracking, Dashboard KPIs
- User Manual (14 chapters with automated screenshots)
- Data Import (COA & Templates from JSON/Excel)
- Deployment & Operations (Pulumi, Ansible, Backup/Restore)

Phase 2 (Tax Compliance) in progress:
- Document Attachment, Telegram Receipt Import complete
- Tax Accounts and Templates (PPN, PPh) complete
- Tax Reports (PPN Summary, PPh 23, Coretax Export) complete
- Fiscal Period Management complete
- Backup & Restore utility complete
- Next: Tax Calendar (2.8)

See `docs/06-implementation-plan.md` for full plan
