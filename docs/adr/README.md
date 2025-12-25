# Architecture Decision Records (ADR)

This directory contains Architecture Decision Records documenting significant technical decisions made during the development of the Indonesian Accounting Application.

## ADR Index

| ADR | Title | Status |
|-----|-------|--------|
| [001](001-core-architecture.md) | Core Architecture | Accepted |
| [002](002-template-system.md) | Template System | Accepted |
| [003](003-tax-compliance.md) | Tax & Compliance | Accepted |
| [004](004-feature-scope.md) | Feature Scope | Accepted |
| [005](005-infrastructure.md) | Infrastructure | Accepted |
| [006](006-offline-mode-deferral.md) | Offline Mode Deferral | Deferred |

## Quick Reference

### Core Architectural Decisions
- **Single-tenant** instance-per-client deployment
- **Transaction-centric** design with journal templates
- **Java 25 + Spring Boot 4.0 + Thymeleaf + HTMX** stack
- **Indonesian market** focus with SAK EMKM compliance

### Key Technical Choices
- **SpEL** for formula evaluation in templates
- **10-year data retention** per Indonesian tax law
- **Local FS / S3** dual storage implementation
- **CSV-based** bank and marketplace reconciliation

### Target Market
- IT Services / Consulting
- Photography / Videography
- Online Sellers (Marketplace)

## ADR Format

Each ADR follows this structure:

```markdown
# ADR-NNN: Title

## Status
Accepted | Superseded | Deprecated

## Context
What problem are we facing?

## Decisions
What did we decide and why?

## Consequences
What are the trade-offs?

## References
Links to related documentation
```

## Historical Context

For detailed discussion, Q&A, and alternatives considered during the decision-making process, see:
- [Historical Decisions Archive](../archive/decisions-historical.md)

## Adding New ADRs

1. Create file `NNN-short-title.md` (next sequential number)
2. Use the format above
3. Update this README index
4. Reference from related documentation as needed
