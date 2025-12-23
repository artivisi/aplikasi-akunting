# Aplikasi Akunting

Accounting application for Indonesian small businesses. Spring Boot 4.0 + Thymeleaf + PostgreSQL.

**Production-ready features:**
- ✅ Core accounting (Chart of Accounts, journal entries, financial reports)
- ✅ Indonesian tax compliance (PPN, PPh 21/23/4(2), e-Faktur, e-Bupot)
- ✅ Payroll with BPJS and automatic PPh 21 calculation
- ✅ Fixed assets with depreciation (straight-line, declining balance)
- ✅ Inventory & simple production (FIFO/weighted average, BOM, production orders)
- ✅ Security hardening (field-level encryption, audit logging, RBAC)

**Industry support:**
- IT Services & Consulting
- Online Sellers (marketplace integration ready)
- Manufacturing (coffee shops, F&B with BOM)
- Education (universities, student billing & scholarships)

## Quick Start

```bash
# Prerequisites: Java 25, Docker

# Run tests
./mvnw test

# Run with visible browser (debugging)
./mvnw test -Dtest=ChartOfAccountSeedDataTest -Dplaywright.headless=false -Dplaywright.slowmo=100
```

## Documentation

| Document | Description |
|----------|-------------|
| [User Manual](https://artivisi.com/aplikasi-akunting/) | End-user documentation (Indonesian) |
| [Features & Roadmap](docs/01-features-and-roadmap.md) | Current features and future plans |
| [Architecture](docs/02-architecture.md) | Tech stack, data model, infrastructure |
| [Operations Guide](docs/03-operations-guide.md) | Deployment, release, backup/restore |
| [Tax Compliance](docs/04-tax-compliance.md) | Indonesian tax handling |
| [Security Testing](docs/05-penetration-testing-checklist.md) | Penetration testing checklist |
| [Implementation Plan](docs/06-implementation-plan.md) | Detailed implementation status |
| [ADRs](docs/adr/) | Architecture decision records |
| [SonarCloud](https://sonarcloud.io/project/overview?id=artivisi_aplikasi-akunting) | Code quality & security analysis |

## Project Status

**Current Phase:** Phase 6 (Security Hardening) - 8 of 10 subsections complete (SpotBugs audit: ✅ 0 issues)

**Completed:** Core accounting MVP, tax compliance, payroll, fixed assets, inventory & production, 4 industry seed packs, comprehensive user manual (15 files), 115 functional tests

See [Features & Roadmap](docs/01-features-and-roadmap.md) for complete feature list and [Implementation Plan](docs/06-implementation-plan.md) for detailed status.

## License

[AGPL-3.0](LICENSE)
