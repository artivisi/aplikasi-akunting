# ADR-005: Infrastructure

## Status
Accepted

## Context
Infrastructure decisions for deployment, storage, and operations of the accounting application.

## Decisions

### 5.1 Document Storage
**Decision:** Dual implementation selectable by config - Local FS (MVP) + S3-compatible (Production).

**Implementations:**
1. **Local Filesystem (MVP/development)**
   - Profile: `storage.type=local`
   - Simple, zero external dependencies

2. **S3-Compatible Storage (Production)**
   - Profile: `storage.type=s3`
   - Works with: MinIO, AWS S3, GCP Cloud Storage

**Storage Optimization:**
- Image compression: 80% quality on upload
- Thumbnail generation for preview
- PDF optimization on upload
- Max per upload: 10 MB
- Supported formats: JPG, PNG, PDF, Excel, CSV

**Security:**
- ClamAV virus scanning on upload
- Reject infected files with error message

### 5.2 Cloud Hosting
**Decision:** Local Indonesian providers or DigitalOcean; avoid big cloud unless requested.

**Preferred Providers:**
- Indonesian: IDCloudHost, Biznet Gio, Dewaweb
- Global (budget): DigitalOcean
- Avoid: AWS, GCP (unless client specifically requests)

**Rationale:**
- Cost control for mid-range pricing strategy
- Data residency compliance (Indonesia)
- Big cloud overkill for target market

**Deployment:**
- Single VPS per instance (MVP)
- Docker Compose
- Can co-locate multiple instances for cost efficiency

### 5.3 Multi-Currency
**Decision:** Rupiah only for Phase 1.

**When Multi-Currency NOT Needed:**
- Pay foreign services via IDR credit card
- Receive foreign payments via PayPal auto-convert
- All bank accounts are in IDR

**When Multi-Currency IS Needed (Phase 2):**
- Own USD/foreign currency bank account
- Invoice in USD with payment to USD account

**Rationale:**
- Most target users (photographers, online sellers) use IDR only
- Simplifies Phase 1 significantly

### 5.4 Transaction Numbering
**Decision:** Per transaction type with yearly reset - `{TYPE}-{YYYY}-{seq}`

**Format Examples:**
- SAL-2025-00001 (Sales)
- PUR-2025-00001 (Purchase)
- EXP-2025-00001 (Expense)
- JNL-2025-00001 (General Journal)

**Implementation:**
- Separate sequence per transaction type
- Sequence resets each fiscal year
- Width configurable (default 5 digits)
- Faktur Pajak follows DJP rules (separate system)

**Rationale:**
- Easy to identify transaction type from number
- Organized for audit and reporting

### 5.5 Business Model
**Decision:** Monthly subscription, mid-range pricing.

**Target Market:**
- IT Services / Consulting
- Photography / Videography
- Online Seller / Marketplace

**Pricing:**
- Model: Monthly subscription
- Range: Rp 200k - 500k/month
- Goal: Cover hosting expenses per instance

**Bookkeeper Support:**
- Separate credentials per client
- No shared dashboard across clients
- Complete data isolation per instance

### 5.6 DevSecOps Tools
**Decision:** Standard toolchain for development, testing, and security.

| Purpose | Tool |
|---------|------|
| Local Development | Docker Compose |
| Infrastructure as Code | Ansible, Pulumi |
| Functional Testing | Playwright |
| Performance Testing | K6 |
| Security Scanning | SonarQube, OWASP tools, Trivy |

## Consequences
- Deployments are simple and cost-effective
- Storage can scale from local dev to production without code changes
- Indonesian hosting ensures data residency compliance
- Transaction numbers are human-readable and audit-friendly

## References
See [Historical Discussion](../archive/decisions-historical.md) for detailed Q&A and alternatives considered.
