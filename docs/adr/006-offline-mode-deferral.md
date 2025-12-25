# ADR-006: Offline Mode Implementation Deferral

## Status
DEFERRED to Phase 7+

**Date:** 2025-12-25
**Deciders:** Development Team
**Related:** Phase 6 (Security Hardening), Phase 7 (API Foundation)

## Context

User requested offline mode capability for:
1. Input transactions while offline (field work, poor connectivity)
2. View neraca saldo (trial balance) without internet connection

### Initial Assessment

**Current Architecture:**
- Server-rendered UI (Thymeleaf + HTMX + Alpine.js)
- REST API endpoints available (`/transactions/api`, `/api/trial-balance`)
- Alpine.js Persist plugin already in use (localStorage)
- Strict CSP with nonce-based scripts (no `unsafe-inline`, no `unsafe-eval`)

**Proposed Implementation:**
- Progressive Web App (PWA) with Service Worker
- IndexedDB for offline transaction queue
- Cached trial balance with timestamp indicator

**Estimated Effort:** 7-8 weeks
- Week 1-2: Service Worker + IndexedDB infrastructure
- Week 3-4: Transaction queue + sync mechanism
- Week 5: Trial balance caching
- Week 6-8: Security hardening (encryption, integrity checks, CSP testing)

## Analysis Results

### 1. Technical Feasibility: ✅ POSSIBLE

**Favorable Factors:**
- REST API endpoints exist
- Alpine.js Persist plugin provides localStorage foundation
- Template-based transactions are cacheable
- Clear data models suitable for offline queuing

**Challenges:**
- Server-side rendering limits offline UI capability
- CSRF protection requires workarounds (mitigated: API endpoints have CSRF disabled)
- Complex formula evaluation in Java (requires JavaScript port)
- Session timeout (15 minutes) conflicts with long offline periods

### 2. Security Analysis: 🔴 HIGH RISK

**Transaction Data Confidentiality Assessment:**

Initial assumption: "Transaction data is not confidential" → **INCORRECT**

Transaction fields contain:

| Field | Example | Confidentiality | Offline Risk |
|-------|---------|-----------------|--------------|
| `description` | "Jasa Konsultasi PT. ABC - INV-001" | HIGH (client PII) | Data breach |
| `description` | "Gaji karyawan Februari 2024" | MEDIUM (aggregate) | Employee privacy |
| `notes` | "Transfer BCA 1234567890 a.n. Ahmad" | HIGH (bank + name) | Financial fraud |
| `amount` | 500000000 | HIGH (competitive intel) | Pricing leak |
| `referenceNumber` | "INV-2025-001" | MEDIUM | Business intel |
| `accountMappings` | `{debit: "1.1.01", credit: "4.1.01"}` | MEDIUM | Structure leak |

**Confidentiality Classification:** 70% of transaction fields are HIGHLY CONFIDENTIAL.

**Regulatory Compliance:**
- GDPR/UU PDP: Transaction descriptions often contain personal data (client/employee names)
- Not covered by existing field-level encryption (EncryptedStringConverter only encrypts Employee.npwp, Employee.nikKtp, etc.)
- Offline storage without encryption violates data minimization principle

**Attack Vectors:**

1. **Physical Device Access** (Likelihood: HIGH - 1-2% annual laptop theft)
   - IndexedDB accessible via Chrome DevTools (F12 → Application → IndexedDB)
   - Extract all offline transactions in plaintext
   - Reveals: client list, pricing, margins, supplier relationships

2. **Malicious Browser Extension** (Likelihood: MEDIUM - 5-10% users)
   - Extensions with `storage` permission can read IndexedDB
   - Silent exfiltration to attacker server
   - Detection: VERY HARD

3. **Service Worker Compromise** (Likelihood: LOW, Impact: CRITICAL)
   - Service Worker can intercept ALL network requests
   - Cache poisoning (modify trial balance to hide fraud)
   - Data exfiltration via background sync

**Required Security Controls:**

1. **Client-Side Encryption** (Web Crypto API)
   - Algorithm: AES-256-GCM (match server EncryptedStringConverter)
   - Key derivation: PBKDF2 from user password (100,000 iterations)
   - Storage: sessionStorage (cleared on logout)
   - Encrypt: `description`, `amount`, `notes`, `referenceNumber`, `accountMappings`

2. **Integrity Protection**
   - HMAC-SHA256 signatures for cached reports
   - Detect tampering before display

3. **Service Worker Integrity**
   - Subresource Integrity (SRI) checks on registration
   - SHA-256 hash verification
   - Scope restriction (`/offline/` only, not site-wide)

4. **CSP Compatibility**
   - Remove CDN sources from script-src (host all libraries locally)
   - Verify Dexie.js CSP compatibility
   - Add IndexedDB regression tests to CspAlpineTest.java

**Additional Effort for Security:** +3-4 weeks

### 3. Complexity vs. Benefit Trade-off

**Effort Breakdown:**

| Component | Effort | Priority |
|-----------|--------|----------|
| PWA infrastructure | 2 weeks | Core |
| Transaction queue + sync | 2 weeks | Core |
| Trial balance caching | 1 week | Nice-to-have |
| Client-side encryption | 2 weeks | **CRITICAL** |
| Integrity protection | 1 week | **CRITICAL** |
| CSP regression testing | 1 week | **CRITICAL** |
| **TOTAL** | **7-8 weeks** | |

**Benefit Assessment:**

- **Use case:** Field workers in poor connectivity areas
- **Frequency:** Occasional (not primary workflow)
- **User base:** Small subset of total users
- **Workaround:** Mobile hotspot, offline draft notes

**Verdict:** **Effort (7-8 weeks) >> Benefit (occasional use case)**

### 4. Phase 6 Conflict

**Current Phase 6 Status:**
- 6.1-6.7: ✅ Complete (Critical fixes, Encryption, Auth, Input validation, Audit)
- 6.8: 🔄 Partial (GDPR/UU PDP - **consent management pending**)
- 6.9: 🔄 Partial (DevSecOps - **container security, API fuzzing pending**)
- 6.10: ✅ Complete (Security Documentation)

**Conflict:** Offline mode security work (encryption, CSP) competes with Phase 6 completion.

**Risk:** Delaying Phase 6 completion extends security vulnerability window.

### 5. Phase 7 Synergy

**Phase 7 (API Foundation) includes:**
- REST API versioning
- OpenAPI specification
- Better sync architecture (conflict resolution, idempotency)
- API authentication (OAuth2/JWT)

**Better Approach:**
- Complete Phase 6 first (secure foundation)
- Build Phase 7 API properly
- Revisit offline mode with proper sync infrastructure

## Decision

**DEFER offline mode implementation** until Phase 7+ completion.

**Rationale:**

1. **Disproportionate Effort:** 7-8 weeks for occasional use case
2. **Security Complexity:** Transaction data confidentiality requires full encryption stack
3. **Phase Priorities:** Phase 6 security hardening incomplete
4. **Better Architecture:** Phase 7 API Foundation provides proper sync foundation
5. **CSP Regression Risk:** Service Worker integration may break existing nonce-based CSP

## Alternatives Considered

### Option 1: Full Secure PWA (REJECTED)
- **Effort:** 7-8 weeks
- **Security:** Comprehensive (encryption, integrity, Service Worker checks)
- **Rejection reason:** Cost too high for limited benefit

### Option 2: Offline Lite (Metadata Only) (REJECTED)
- **Approach:** Store only `templateId`, `transactionDate`, `status` (no sensitive data)
- **User flow:** Create skeleton offline → Complete details when online
- **Effort:** 2 weeks
- **Pros:** No encryption complexity, zero confidentiality risk
- **Cons:** Poor UX (must re-enter amount/description)
- **Rejection reason:** Defeats purpose of offline mode

### Option 3: LocalStorage Quick Draft (REJECTED)
- **Approach:** Alpine.js Persist to save form state, restore on reload
- **Effort:** 1 week
- **Scope:** No true offline mode, just form persistence
- **Rejection reason:** Insufficient for field work use case

### Option 4: Defer to Phase 7+ (ACCEPTED) ✅
- **Approach:** Wait for API Foundation, revisit with better architecture
- **Effort:** 0 weeks (current), reassess in Phase 7
- **Pros:** Focus on Phase 6 completion, leverage Phase 7 infrastructure
- **Cons:** No offline capability in near term
- **Acceptance reason:** Best alignment with project roadmap and priorities

## Consequences

### Immediate (Phase 6)
- ✅ **Positive:** Development resources focus on completing Phase 6 security hardening
- ✅ **Positive:** No CSP regression risk from Service Worker introduction
- ✅ **Positive:** Avoid premature architecture decisions (better to build on Phase 7 API)

### User Impact
- ⚠️ **Neutral:** Users must have internet connection for transaction input
- ⚠️ **Neutral:** Trial balance requires connection
- ✅ **Mitigated:** Workarounds available (mobile hotspot, offline notes → enter later)

### Future (Phase 7+)
- ✅ **Positive:** Better sync architecture (idempotency, conflict resolution, versioning)
- ✅ **Positive:** Proper API authentication foundation (OAuth2/JWT for offline token refresh)
- 🔄 **Revisit:** Reassess offline mode viability after Phase 7 API completion

## Revisit Criteria

Reconsider offline mode implementation when:

1. **Phase 7 Complete:** REST API with versioning, OpenAPI spec, proper sync
2. **User Demand:** Multiple users report connectivity issues blocking work
3. **Competitive Pressure:** Competitors offer offline mode as differentiator
4. **Technology Maturity:** Better browser APIs for secure offline storage (e.g., Storage Buckets API)

**Estimated Timeline:** Q2-Q3 2026 (after Phase 7)

## References

- Analysis document: Internal offline mode viability analysis (2025-12-25)
- Security documentation: `docs/user-manual/11-keamanan-kepatuhan.md`
- Encryption implementation: `src/main/java/.../security/EncryptedStringConverter.java`
- CSP configuration: `src/main/java/.../security/CspNonceHeaderWriter.java`
- Phase 6 plan: `docs/06-implementation-plan.md` (Security Hardening section)
