# Decisions & Open Questions

## Decisions Made

### 1. Transaction-Centric Architecture ✓
**Decision:** Use transaction forms with journal templates instead of direct account-based entry

**Rationale:**
- Target users have minimal accounting knowledge
- Reduces data entry errors
- Maintains proper double-entry bookkeeping behind the scenes
- More intuitive for business owners

**Trade-offs:**
- More complex implementation
- Need to build template system
- Less direct control for power users (mitigated by manual journal entry option)

### 2. Journal Template Approach ✓
**Decision:** Implement configurable journal templates with preloaded defaults and power-user customization

**Rationale:**
- Flexibility for different business scenarios
- No code changes needed for new transaction types
- Users can adapt to their specific needs
- Handles simple to complex entries (one-to-many, many-to-many)

**Trade-offs:**
- More database complexity
- Template builder UI required for power users
- Template validation logic needed

### 3. Single-Tenant Architecture ✓
**Decision:** Instance-per-client deployment (not multi-tenant with tenant_id)

**Rationale:**
- Simpler codebase (no tenant_id in every query)
- Complete data and process isolation
- No risk of cross-tenant data leaks
- Easier compliance for accounting data
- Can co-locate multiple instances for cost efficiency
- Can isolate high-value clients on dedicated VPS

**Deployment Progression:**
1. **Phase 1 (MVP):** Single company, manual deployment, Docker Compose
2. **Phase 2 (Early SaaS):** Multi-instance single node, control plane for client management, 5-50 clients
3. **Phase 3 (Growth):** Node per client, Pulumi automation, 50+ clients with premium tiers

**Trade-offs:**
- Need control plane app for SaaS automation (Phase 2+)
- More containers/processes to manage
- Acceptable: Focus on product stability first (Phase 1), automate incrementally
- More testing scenarios

### 4. Indonesian Tax Focus ✓
**Decision:** Build specifically for Indonesian tax compliance, not generic multi-country

**Rationale:**
- Target market is 100% Indonesian
- Indonesian tax rules are specific and complex
- Generic solutions often inadequate for compliance
- Competitive differentiator

**Trade-offs:**
- Not suitable for international expansion without significant changes
- Hardcoded Indonesian assumptions

### 5. No Near-Term Bank Integration ✓
**Decision:** Defer bank integration to later phase

**Rationale:**
- Focus on core bookkeeping and tax compliance first
- Manual entry sufficient for target market initially
- Complex integration requirements
- Regulatory/security overhead

**Trade-offs:**
- More manual data entry
- Potential for errors in transaction entry
- Less competitive vs solutions with bank feeds

### 6. Target Market Segments ✓
**Decision:** Focus on service businesses and simple online sellers

**Segments:**
1. IT/Software consulting and training (own business)
2. Wedding photographers/videographers (freelance + small studios)
3. Home-based online sellers (marketplace + social media)

**Rationale:**
- Clear market focus based on accessible users
- Service-heavy businesses align with transaction-centric approach
- Online sellers need simple inventory (no manufacturing complexity)
- All segments benefit from project/job tracking

**Trade-offs:**
- Not suitable for manufacturing/production businesses
- Limited appeal to traditional retail (warung, toko)
- No complex inventory costing

### 7. Project/Job Costing Required ✓
**Decision:** Include project/job costing from Phase 1

**Rationale:**
- Critical for photographers (per-event profitability)
- Important for consultants/developers (per-project tracking)
- Online sellers can use for product line analysis
- Competitive differentiator

**Implementation:** Simple project tagging with profitability reports (Option B/C hybrid)

**Trade-offs:**
- More database complexity
- Additional UI for project management
- More complex reporting

### 8. Simple Inventory for Online Sellers ✓
**Decision:** Include basic inventory tracking in Phase 1

**Rationale:**
- Online sellers need stock quantity tracking
- Simple buy/sell model (no production)
- COGS calculation needed for profit analysis
- Not complex manufacturing

**Implementation:** Basic inventory with FIFO or Average costing, no production/assembly

**Trade-offs:**
- Not suitable for manufacturers
- Limited to simple buy/sell model

### 9. Technology Stack: Spring Boot Monolith ✓
**Decision:** Java 25 + Spring Boot 4.0 + Thymeleaf + HTMX + Alpine.js + PostgreSQL 17

**Date:** 2025-11-19

**Alternatives Considered:**
1. Spring Boot + React/Vue (Split BE/FE)
2. ExpressJS + Handlebars (Node.js)
3. Spring Boot + Thymeleaf + HTMX (Selected)

**Rationale:**
- **Monolith over Split BE/FE:**
  - Single codebase reduces maintenance overhead
  - No data model or validation duplication
  - Simple session-based authentication
  - Faster development and debugging
  - Sufficient UX for form-heavy accounting workflows

- **Java 25 + Spring Boot over Node.js:**
  - BigDecimal for precise financial calculations
  - Type safety for complex business logic
  - Mature transaction management (ACID)
  - Virtual threads for efficient resource utilization (especially when co-locating instances)
  - Large Java talent pool in Indonesia
  - Battle-tested security for financial applications

- **HTMX Enhancement:**
  - Modern UX without SPA complexity
  - Partial page updates
  - Progressive enhancement
  - No build process required

- **Document Storage Strategy:**
  - Phase 1: MinIO (self-hosted, cost-optimized)
  - Phase 2+: Indonesian cloud or AWS/GCP
  - S3-compatible API for easy migration

- **DevSecOps Tools:**
  - Docker Compose (local development)
  - Ansible (IaC, deployment)
  - Playwright (functional testing)
  - K6 (performance testing)
  - SonarQube, OWASP tools, Trivy (security)

**Implementation:** See [Technology Stack Documentation](05-technology-stack.md) for details

**Trade-offs:**
- Less flashy UI than SPA (acceptable for target users)
- Server-rendered pages (suitable for form-heavy workflows)
- MinIO requires ops overhead (offset by cost savings)
- Can refactor to API architecture later if mobile app needed

**Review Date:** After MVP launch

## Open Questions

### Template System Design

#### Q1: Template Versioning?
**Question:** If a user edits a journal template, should old transactions remain linked to the old version, or should they reference the current version?

**Options:**
A. Version templates - each edit creates new version, old transactions link to old version
B. No versioning - template changes affect how old transactions display
C. Immutable templates - cannot edit, must create new template

**Considerations:**
- Audit trail requirements
- Historical accuracy
- User confusion
- Database complexity

**Recommendation needed:** Lean toward Option A (versioning) for audit compliance, but adds complexity.

---

#### Q2: Template Validation Rules?
**Question:** Should templates have conditional availability based on company configuration?

**Examples:**
- "Penjualan + PPN" template only available for PKP companies?
- "PPh 21" template only for companies with employees?
- Industry-specific templates based on business_type?

**Options:**
A. Strict validation - hide/disable incompatible templates
B. Soft validation - show warnings but allow usage
C. No validation - show all templates always

**Recommendation needed:** Option A for tax templates (PKP/non-PKP), Option B for others

---

#### Q3: Template Categories/Tags?
**Question:** How to organize templates when there are dozens?

**Options:**
A. Flat list with category field (current design)
B. Hierarchical categories (Category > Subcategory)
C. Tag system (multiple tags per template)
D. Both categories and tags

**Considerations:**
- User experience (finding the right template quickly)
- Scalability (50+ templates)
- Multi-client bookkeeper workflow (favorites, recents)

**Recommendation needed:** Option D - Category for structure, tags for flexibility

---

#### Q4: Formula Complexity?
**Question:** How complex should amount calculation formulas be in templates?

**Current design:** Supports percentages (11%, 100%)

**Enhancement options:**
A. Simple arithmetic: `amount * 0.11 + 1000`
B. Field references: `transaction.amount * rate.ppn + fixed_fee`
C. Conditional logic: `if amount > 2000000 then amount * 0.02 else 0`
D. Full expression language (like Excel formulas)

**Considerations:**
- Power user needs vs complexity
- Security (formula injection)
- Validation and testing
- Performance

**Recommendation needed:** Start with Option A (simple arithmetic), evaluate need for Option C later

---

#### Q5: Conditional Template Logic?
**Question:** Should templates support automatic line inclusion based on conditions?

**Examples:**
- If transaction amount > Rp 2,000,000, auto-add PPh 23 line
- If customer is non-PKP, skip PPN line
- If payment date differs from invoice date, add interest line

**Options:**
A. No conditional logic - keep templates simple
B. Pre-configured conditions in template definition
C. Rule engine for complex scenarios

**Considerations:**
- User experience (magic vs explicit)
- Debugging when things go wrong
- Audit trail clarity
- Implementation complexity

**Recommendation needed:** Option A for MVP, consider Option B for Phase 2

---

### Business Logic Questions

#### Q6: Business Type Priority? ✓ ANSWERED
**Question:** Which business types should we create default chart of accounts templates for in Phase 1?

**Decision:**
1. IT Services / Consulting
2. Photography / Videography Services
3. Online Seller / Marketplace
4. General Services (Freelancer)

See Decision #6 above.

---

#### Q7: Inventory Tracking? ✓ ANSWERED
**Question:** Should Phase 1 include inventory management features?

**Decision:** Option C - Basic inventory with FIFO/Average costing for online sellers

See Decision #8 above.

---

#### Q8: Payroll Integration?
**Question:** Should the app handle payroll processing, or just record payroll expenses?

**Context:**
- PPh 21 calculation is complex (progressive rates, PTKP)
- Payroll often outsourced by small businesses
- Employee master data required

**Options:**
A. No payroll - users record net salary expenses only
B. Basic payroll - record gross salary, PPh 21 as separate entries
C. Full payroll - calculate PPh 21, generate pay slips

**Recommendation needed:** Option B for Phase 1 (manual PPh 21 entry), Option C for Phase 2

---

#### Q9: Project/Job Costing? ✓ ANSWERED
**Question:** Should transactions be taggable by project/job for cost tracking?

**Decision:** YES - Option B/C hybrid (simple tagging + profitability reports)

**Use cases covered:**
- Photographers billing by event/wedding
- Consultants/developers tracking project costs
- Multi-project expense allocation

See Decision #7 above.

---

### Data & Technical Questions

#### Q10: Fiscal Period Locking?
**Question:** Should the app prevent editing transactions after month/year closing?

**Options:**
A. Hard lock - no edits after closing
B. Soft lock - warning but allow with permission
C. No lock - always allow edits
D. Lock after tax filing only

**Considerations:**
- Audit compliance
- User flexibility (mistakes happen)
- Tax implications
- Reversal entry workflow

**Recommendation needed:** Option D - lock after tax filing, soft lock after month close

---

#### Q11: Transaction Numbering?
**Question:** How should transaction numbers be generated?

**Format options:**
- `TRX-{YYYY}-{seq}` (e.g., TRX-2025-00001)
- `{TYPE}-{YYYY}{MM}-{seq}` (e.g., EXP-202501-00001)
- `{COMPANY_CODE}-{YYYY}-{seq}` (e.g., ABC-2025-00001)
- User-definable format

**Sequence scope:**
- Global per instance (simple)
- Per transaction type
- Per fiscal year

**Recommendation needed:** `TRX-{YYYY}-{seq}` with yearly reset for simplicity

---

#### Q12: Multi-Currency Support?
**Question:** Should the app support multiple currencies?

**Context:**
- Target is 100% Indonesian users
- Some may have foreign transactions (import/export)
- Adds significant complexity (exchange rates, gain/loss)

**Options:**
A. Rupiah only
B. Multi-currency with manual exchange rate entry
C. Multi-currency with automated exchange rate feeds

**Recommendation needed:** Option A for Phase 1, Option B for Phase 2 if demand exists

---

#### Q13: Document Storage?
**Question:** Where and how to store uploaded receipts, invoices, etc.?

**Options:**
A. Database (BLOB storage)
B. File system (server local storage)
C. Cloud storage (S3, GCS, etc.)
D. No document storage (Phase 2 feature)

**Considerations:**
- Cost
- Scalability
- Backup/redundancy
- Performance

**Recommendation needed:** Option C for SaaS scalability

---

### Feature Scope Questions

#### Q14: Fixed Asset Management?
**Question:** Should the app handle fixed asset tracking and depreciation?

**Context:**
- Important for businesses with equipment, vehicles, buildings
- Depreciation calculation (straight-line, declining balance)
- Asset register for audit

**Options:**
A. Not in Phase 1 - record as expense
B. Basic asset register - no auto depreciation
C. Full asset management - auto depreciation posting

**Recommendation needed:** Option B for Phase 1 if targeting businesses with assets

---

#### Q15: Budget Management?
**Question:** Should users be able to create budgets and track actual vs budget?

**Use cases:**
- Monthly expense budgets
- Project budgets
- Annual revenue targets

**Implementation:**
A. No budgeting features
B. Simple budget vs actual reports
C. Budget workflows (approval, alerts, variance analysis)

**Recommendation needed:** Option B for Phase 2 (valuable for analysis focus)

---

#### Q16: Bank Reconciliation (without integration)?
**Question:** Even without bank feeds, should we have bank reconciliation features?

**Context:**
- Users manually enter bank transactions
- Need to match with bank statement
- Identify missing/duplicate entries

**Features:**
- Upload bank statement (CSV/PDF)
- Match with recorded transactions
- Identify discrepancies

**Recommendation needed:** Yes for Phase 1 - valuable even without auto-feed

---

## Research Needed

### Market Research
1. **Primary business types** among target users (prioritize templates)
2. **Pain points** with existing solutions (Accurate, Jurnal, Zahir, etc.)
3. **Willingness to pay** (pricing strategy)
4. **Bookkeeper workflows** (multi-client management features)

### Technical Research
1. **e-Faktur API** capabilities and requirements
2. **e-Bupot integration** documentation
3. **DJP e-Filing** API availability
4. **Indonesian cloud hosting** options (data residency)

### Competitive Analysis
1. Feature comparison with local competitors
2. Pricing models
3. User reviews (what do they love/hate)
4. Integration ecosystem

### Regulatory Research
1. **Data retention** requirements for accounting records:
   - Minimum retention period (10 years?)
   - What documents must be retained:
     - Transaction receipts/invoices
     - Bank statements
     - Tax documents (SPT, bukti potong, faktur pajak)
     - Contracts and agreements
     - Payroll records
   - Format requirements (original, scan, digital-native all acceptable?)
   - Storage location requirements (Indonesia data residency?)
   - Destruction policies after retention period

2. **Tax audit** requirements (what reports needed):
   - Required reports for tax audit
   - Supporting document requirements
   - Access requirements for auditors
   - Audit trail requirements
   - Digital vs physical document acceptance

3. **Electronic signature** requirements for tax documents
4. **Upcoming tax regulation** changes (2025+)

### Payment Integration Research
1. **Administrative complexity** for target market segments:
   - What paperwork/documentation required for payment gateway integration?
   - Registration process complexity for:
     - Freelance photographers/videographers
     - Home-based online sellers
     - Small IT consultants
   - Business entity requirements (PT, CV, individual/NPWP?)
   - Verification process timeline
   - Ongoing compliance/reporting requirements

2. **Cost structure** for payment integrations:
   - **Initial fees:**
     - Registration/setup fees
     - Integration development costs
     - Certification/testing fees
   - **Transaction fees:**
     - Percentage per transaction
     - Fixed fee per transaction
     - Different rates by payment method (CC, e-wallet, VA, QRIS)
   - **Other recurring fees:**
     - Monthly/annual maintenance fees
     - Settlement fees
     - Chargeback fees
     - Minimum transaction volume requirements

3. **Payment gateway options** for Indonesian market:
   - Midtrans
   - Xendit
   - iPaymu
   - Faspay
   - Nicepay
   - Comparison of features, costs, ease of integration

4. **E-wallet direct integration:**
   - GoPay, OVO, DANA, ShopeePay direct APIs
   - vs aggregator (payment gateway) approach
   - Cost comparison

5. **Marketplace payment handling:**
   - How Tokopedia, Shopee, Bukalapak handle settlements
   - Reporting/reconciliation capabilities
   - Integration APIs availability

### Digital Signature & E-Meterai Research

1. **E-Meterai (Electronic Stamp Duty) requirements:**
   - Which documents require e-meterai for target businesses?
     - Invoices above certain amount threshold
     - Contracts/agreements
     - Receipt of payments
     - Other business documents
   - Current threshold amounts (Rp 5 juta+?)
   - Legal validity and requirements
   - Integration methods:
     - API providers (Peruri, third-party)
     - Manual application process
     - Bulk application capabilities

2. **E-Meterai cost structure:**
   - Per-stamp cost (currently Rp 10,000?)
   - API integration fees
   - Volume discounts
   - Reseller/distributor options

3. **Digital signature (Tanda Tangan Elektronik) requirements:**
   - **PSrE (Penyelenggara Sertifikat Elektronik) providers:**
     - PrivyID
     - VIDA
     - Digisign
     - Tilaka
     - Peruri Sign
   - Legal requirements for tax documents:
     - e-Faktur signing
     - e-Bupot signing
     - SPT signing
   - Contract/agreement signing for service businesses

4. **Digital signature cost structure:**
   - Certificate fees (personal vs corporate)
   - Annual renewal costs
   - Per-signature fees (if any)
   - API integration costs
   - Volume-based pricing

5. **Integration complexity:**
   - **E-Meterai:**
     - API documentation availability
     - Integration development effort
     - Verification/validation process
   - **Digital signature:**
     - Certificate management
     - Integration with document generation
     - User experience (signing workflow)
     - Mobile support

6. **Use cases for target market:**
   - **Photographers/videographers:**
     - Contract signing with clients
     - Invoice e-meterai (above threshold)
   - **Online sellers:**
     - Product supply agreements
     - High-value invoices
   - **IT consultants:**
     - Service agreements/contracts
     - Project invoices
     - NDA, SLA signing

7. **Compliance timeline:**
   - When is e-meterai mandatory vs optional?
   - Penalties for non-compliance
   - Transition period considerations
   - Future regulatory changes

### Document Storage & Management Research

1. **Cloud storage providers** for Indonesian market:

   **Self-Hosted Solutions:**
   - MinIO (S3-compatible object storage)
     - Free and open source
     - Deploy on own infrastructure
     - S3 API compatibility (easy migration)
     - Cost: Only infrastructure (VPS/server)
     - Ops overhead: Requires maintenance
   - Ceph
   - SeaweedFS

   **Indonesian Cloud Providers:**
   - Biznet Gio (NEO Object Storage)
     - Jakarta data center
     - Lower latency for Indonesian users
     - Competitive pricing
     - Local support
   - IDCloudHost (Object Storage)
   - Telkom Sigma (Cloud Storage)
   - Qwords (Cloud Object Storage)

   **Global Cloud Providers:**
   - AWS S3 (ap-southeast-3 Jakarta region)
   - Google Cloud Storage (Jakarta region)
   - Alibaba Cloud OSS

   **Cost Comparison Needed:**
   - Storage cost per GB/month
   - Bandwidth/transfer costs (ingress/egress)
   - Request costs (PUT/GET)
   - Data retrieval costs
   - Minimum commitment
   - Data residency compliance
   - SLA and reliability

   **Migration Path:**
   - Phase 1: MinIO (MVP, lowest cost)
   - Phase 2: Indonesian cloud (growth, managed service)
   - Phase 3: AWS/GCP (scale, global expansion)

2. **Storage optimization strategies:**
   - Image compression (quality vs size trade-off)
   - PDF optimization
   - Thumbnail generation
   - CDN for document delivery
   - Tiered storage (hot vs cold storage)
   - Archive strategies for old documents

3. **Document security:**
   - Encryption at rest
   - Encryption in transit
   - Access control (signed URLs, time-limited access)
   - Virus/malware scanning for uploads
   - Backup and redundancy
   - Disaster recovery

4. **File size and format limits:**
   - Maximum file size per upload
   - Maximum total storage per company instance
   - Supported file formats
   - File format validation
   - OCR capabilities for scanned documents

5. **Audit and compliance:**
   - Document access logging
   - Retention policy enforcement
   - Secure deletion/destruction
   - Export capabilities for audit
   - Chain of custody tracking

6. **User experience considerations:**
   - Upload speed and reliability
   - Progress indicators
   - Bulk upload capabilities
   - Mobile photo capture
   - In-app document viewer requirements

---

## Decision Log Template

When decisions are made, document them here:

```
### Decision: [Title]
**Date:** YYYY-MM-DD
**Decided by:** [Name/Team]
**Question:** [What was being decided]
**Options considered:** [List]
**Decision:** [Chosen option]
**Rationale:** [Why]
**Implementation notes:** [How to implement]
**Review date:** [When to revisit if needed]
```

---

## Next Steps

1. **Prioritize open questions** based on Phase 1 requirements
2. **Make MVP decisions** (defer others to later phases)
3. **Validate assumptions** with potential users
4. **Create implementation plan** based on decided scope
5. **Set up tech stack** and development environment
6. **Build proof of concept** for template system
7. **Design UI/UX mockups** for transaction entry flow
