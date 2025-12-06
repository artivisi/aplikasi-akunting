# Claude Instructions

## Project Overview

Indonesian accounting application for small businesses. Spring Boot 4.0 + Thymeleaf + PostgreSQL.

## Current Status

- **Phase 0:** ✅ Complete (project setup, auth, CI/CD)
- **Phase 1:** ✅ Complete (Core Accounting MVP)
- **Phase 2:** ✅ Complete (Tax Compliance + Cash Flow)
- **Phase 3:** ✅ Complete (Payroll + RBAC + Employee Self-Service)
- **Phase 4:** ✅ Complete (Fixed Assets)
- **Phase 5:** ✅ Complete (Inventory & Production)
  - 5.1 Product Master: ✅ Complete
  - 5.2 Inventory Transactions: ✅ Complete
  - 5.3 Inventory Reports: ✅ Complete
  - 5.4 Simple Production (BOM): ✅ Complete
  - 5.5 Integration with Sales: ✅ Complete
- **Phase 6:** 🔄 In Progress (Security Hardening)
  - 6.1-6.5: ✅ Complete (Critical fixes, Encryption, Auth hardening, Input validation, Audit logging)
  - 6.6: ✅ Complete (Data Protection)
  - 6.7: ✅ Complete (API Security)
  - 6.8: 🔄 Partial (GDPR/UU PDP - consent management, breach response pending)
  - 6.9: 🔄 Partial (DevSecOps - container security, API fuzzing pending)
  - 6.10: ✅ Complete (Security Documentation)
- **Phase 7:** ⏳ Not Started (API Foundation)
- See `docs/06-implementation-plan.md` for full plan

## Key Files

| Purpose | Location |
|---------|----------|
| Features & Roadmap | `docs/01-features-and-roadmap.md` |
| Architecture | `docs/02-architecture.md` |
| Operations Guide | `docs/03-operations-guide.md` |
| Tax Compliance | `docs/04-tax-compliance.md` |
| Implementation Plan | `docs/06-implementation-plan.md` |
| ADRs | `docs/adr/` |
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

Phase 6 (Security Hardening) in progress!

Phase 5 highlights (complete):
- Product and ProductCategory entities with FIFO/Weighted Average costing
- Inventory transactions: Purchase, Sale, Adjustment, Production In/Out
- FIFO layers and weighted average cost calculation
- BOM (Bill of Materials) for simple production
- Production orders with component consumption and finished goods receipt
- Auto-COGS calculation on sales with margin analysis
- Product profitability reports
- Playwright functional tests (73+ tests)

Phase 6 highlights (in progress):
- Field-level encryption (AES-256-GCM) for PII fields
- Document storage encryption with backward compatibility
- Password complexity enforcement (12+ chars, mixed case, numbers, special)
- Account lockout (5 attempts, 30-minute lockout)
- Rate limiting on login and API endpoints
- Comprehensive security audit logging
- Data masking for sensitive fields in UI
- GDPR/UU PDP compliance (DSAR export, anonymization)
- DevSecOps: CodeQL, SonarCloud, OWASP Dependency-Check, ZAP DAST
- Security regression tests (Playwright + JUnit)

Next: Complete Phase 6 remaining items (consent management, container security)

See `docs/06-implementation-plan.md` for full plan
