# ADR-002: Template System

## Status
Accepted

## Context
The transaction-centric architecture requires a flexible template system to generate journal entries from user-friendly transaction forms. Templates must handle various business scenarios without code changes.

## Decisions

### 2.1 Journal Template Approach
**Decision:** Configurable journal templates with preloaded defaults and power-user customization.

**Rationale:**
- Flexibility for different business scenarios
- No code changes needed for new transaction types
- Users can adapt to their specific needs
- Handles simple to complex entries (one-to-many, many-to-many)

**Trade-offs:**
- More database complexity
- Template builder UI required for power users

### 2.2 Template Versioning
**Decision:** Each edit creates new version; old transactions link to old version.

**Rationale:**
- Required for audit traceability
- Historical accuracy of what template was used at transaction time
- Compliance requirement for accounting records

**Implementation:**
- Each template edit creates a new version record
- Journal entries store reference to specific template version used
- Template version history viewable for audit purposes

### 2.3 Formula Engine
**Decision:** Full expression language using SpEL with SimpleEvaluationContext.

**Rationale:**
- SpEL is built-in to Spring - no additional dependencies
- SimpleEvaluationContext provides secure sandbox
- Blocks dangerous operations (type references, constructors, bean references)
- Supports arithmetic, comparison, logical, ternary conditionals

**Example formulas:**
- `amount * 0.11` (PPN 11%)
- `amount > 2000000 ? amount * 0.02 : 0` (PPh 23 threshold)
- `transaction.amount * rate.ppn` (field references)

**Trade-offs:**
- Formula errors surface at runtime (mitigate with validation on template save)

### 2.4 Template Organization
**Decision:** Category + tags + favorites + frequently used + search.

**Rationale:**
- Category provides primary structure for navigation
- Tags enable flexible cross-categorization
- Favorites for quick access to preferred templates
- Frequently used for automatic surfacing of common templates

**Implementation:**
- Each template has one category (required) and multiple tags (optional)
- User-specific favorites list
- Track usage count per template per user

### 2.5 No Conditional Logic in Templates
**Decision:** No conditional logic - keep templates simple.

**Rationale:**
- Simpler implementation, predictable behavior
- Easier to debug when journal entries are incorrect
- Users explicitly choose the correct template

**Implementation:**
- Create separate templates for different scenarios (e.g., "Penjualan Jasa", "Penjualan Jasa + PPh 23")
- Preview/simulation functionality before committing
- Template selector guides users to correct template

### 2.6 No Runtime Validation
**Decision:** Templates pre-generated per instance during setup.

**Rationale:**
- Single-tenant architecture: each database is company-specific
- Templates tailored per instance during SaaS onboarding
- No need for runtime validation

## Consequences
- Template system is the core abstraction for all accounting operations
- Non-accountants can record transactions without understanding debits/credits
- Templates can evolve without affecting historical data integrity
- Power users can create custom templates for unique scenarios

## References
See [Historical Discussion](../archive/decisions-historical.md) for detailed Q&A and alternatives considered.
