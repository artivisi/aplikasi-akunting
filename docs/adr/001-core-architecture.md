# ADR-001: Core Architecture

## Status
Accepted

## Context
Building an accounting application for Indonesian small businesses (IT services, photographers, online sellers). Need to decide on fundamental architectural patterns that will shape the entire system.

## Decisions

### 1.1 Transaction-Centric Design
**Decision:** Use transaction forms with journal templates instead of direct account-based entry.

**Rationale:**
- Target users have minimal accounting knowledge
- Reduces data entry errors
- Maintains proper double-entry bookkeeping behind the scenes
- More intuitive for business owners

**Trade-offs:**
- More complex implementation
- Less direct control for power users (mitigated by manual journal entry option)

### 1.2 Single-Tenant Architecture
**Decision:** Instance-per-client deployment (not multi-tenant with tenant_id).

**Rationale:**
- Simpler codebase (no tenant_id in every query)
- Complete data and process isolation
- No risk of cross-tenant data leaks
- Easier compliance for accounting data
- Can co-locate multiple instances for cost efficiency

**Deployment Progression:**
1. Phase 1 (MVP): Single company, Docker Compose
2. Phase 2 (Early SaaS): Multi-instance single node, control plane
3. Phase 3 (Growth): Node per client, Pulumi automation

**Trade-offs:**
- Need control plane for SaaS automation
- More containers/processes to manage

### 1.3 Technology Stack
**Decision:** Java 25 + Spring Boot 4.0 + Thymeleaf + HTMX + Alpine.js + PostgreSQL 17

**Rationale:**
- BigDecimal for precise financial calculations
- Type safety for complex business logic
- Mature transaction management (ACID)
- Virtual threads for efficient resource utilization
- Large Java talent pool in Indonesia
- HTMX provides modern UX without SPA complexity

**Trade-offs:**
- Less flashy UI than SPA (acceptable for target users)
- Server-rendered pages (suitable for form-heavy workflows)

### 1.4 Indonesian Market Focus
**Decision:** Build specifically for Indonesian tax compliance, not generic multi-country.

**Rationale:**
- Target market is 100% Indonesian
- Indonesian tax rules are specific and complex
- Generic solutions often inadequate for compliance
- Competitive differentiator

**Trade-offs:**
- Not suitable for international expansion without significant changes

### 1.5 Target Market Segments
**Decision:** Focus on service businesses and simple online sellers.

**Segments:**
1. IT/Software consulting and training
2. Wedding photographers/videographers
3. Home-based online sellers (marketplace + social media)

**Trade-offs:**
- Not suitable for manufacturing/production businesses
- Limited appeal to traditional retail
- No complex inventory costing

## Consequences
- All features designed around business owner workflows, not accountant workflows
- Tax compliance is a core feature, not an afterthought
- Template system becomes central to the application
- Each deployment is isolated, simplifying data protection compliance

## References
See [Historical Discussion](../archive/decisions-historical.md) for detailed Q&A and alternatives considered.
