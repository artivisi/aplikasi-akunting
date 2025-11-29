# ADR-003: Tax & Compliance

## Status
Accepted

## Context
Indonesian tax regulations are specific and complex. The application must support compliance while remaining usable by non-accountants.

## Decisions

### 3.1 Accounting Standards
**Decision:** SAK EMKM compliance with cash flow statement addition.

**SAK EMKM Requirements:**
- Laporan Posisi Keuangan (Balance Sheet)
- Laporan Laba Rugi (Income Statement)
- Catatan atas Laporan Keuangan / CALK (Notes)
- Historical cost basis
- Accrual accounting

**Additional (Beyond SAK EMKM):**
- Laporan Arus Kas (Cash Flow Statement)
- Direct method with auto-classification based on template used
- `cash_flow_category` field: OPERATING, INVESTING, FINANCING, NON_CASH

**Rationale:**
- Target market is UMKM - SAK EMKM is applicable
- Cash flow provides valuable business insight, differentiator from competitors

### 3.2 Tax Integration Strategy
**Decision:** Export format default; PJAP integration as custom project.

**Core Product:**
- e-Faktur: Export CSV format for upload to DJP e-Faktur app
- e-Bupot: Export format for upload to DJP e-Bupot
- e-Filing: Export format for SPT data
- Tax reports (PPN, PPh 21, PPh 23) in standard format

**Custom Project (upon request):**
- Direct PJAP integration (OnlinePajak, Klikpajak)
- Automated submission

**Rationale:**
- PJAP authorization required for direct integration
- Export format sufficient for most users
- Keep core product simple

### 3.3 Data Retention Policy
**Decision:** 10-year retention, digital documents legally valid.

**Legal Basis:**
- UU KUP Pasal 28 ayat 11: 10-year retention
- UU ITE Pasal 5-6: Digital documents legally valid
- PMK 81/2024: Both physical and digital formats accepted

**Implementation:**
- Mobile receipt photo capture feature
- Auto-generate berita acara (legalization record) on upload
- Store metadata: capture date, hash for integrity, uploader info
- Host in Indonesia for data residency compliance

**Physical Documents Can Be Destroyed After Scanning:**
- Requires legalization record (berita acara)
- Exception: documents with national/company importance

### 3.4 Fiscal Period Locking
**Decision:** Lock after tax filing, soft lock after month close.

**Month close (soft lock):**
- Warning displayed when editing closed period
- Requires elevated permission (admin/owner role)
- Audit trail logged for any modifications

**Tax filing (hard lock):**
- No edits allowed to periods with filed SPT
- Must use reversal/adjustment entries in current period
- If correction needed: file SPT Pembetulan first, then unlock

**Rationale:**
- Implement from start to avoid migration issues later
- Balances data integrity with practical correction needs

### 3.5 Digital Signature & E-Meterai
**Decision:** Custom project upon request (not in core product).

**Rationale:**
- Not all users need certified e-signature
- E-meterai only required for documents > Rp 5 juta threshold
- Integration adds complexity and per-transaction cost

**Core Product:**
- Basic signature image on documents (uploaded scan)
- Document generation ready for manual e-meterai

## Consequences
- Tax compliance is built into daily workflows, not an afterthought
- Users can confidently destroy physical receipts after proper digitization
- Export formats enable compliance without complex integrations
- Historical data is protected from accidental modification

## References
See [Historical Discussion](../archive/decisions-historical.md) for detailed Q&A and alternatives considered.
