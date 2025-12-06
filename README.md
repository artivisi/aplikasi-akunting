# Aplikasi Akunting

Accounting application for Indonesian small businesses. Spring Boot 4.0 + Thymeleaf + PostgreSQL.

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

See [Features & Roadmap](docs/01-features-and-roadmap.md) for current status and [Implementation Plan](docs/06-implementation-plan.md) for details.

## License

[AGPL-3.0](LICENSE)
