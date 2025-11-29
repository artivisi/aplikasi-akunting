# ADR-004: Feature Scope

## Status
Accepted

## Context
Need to define which features are in scope for the accounting application, balancing user needs against implementation complexity.

## Decisions

### 4.1 Project/Job Costing
**Decision:** Include project tracking with milestones, payment terms, and profitability reports.

**Rationale:**
- Critical for photographers (per-event profitability)
- Important for consultants (per-project tracking)
- Competitive differentiator

**Features:**
| Feature | Purpose |
|---------|---------|
| Milestones | Track % complete per deliverable |
| Payment Terms | Define payment schedule (DP, per milestone, final) |
| Invoices | Generate from payment terms |
| Cost Overrun Detection | Compare % spent vs % complete |

**Cost Overrun Formula:**
```
Progress % = Σ (milestone.completion × milestone.actual_progress)
Spent % = actual_costs / budget × 100
Alert if Spent % > Progress % + threshold
```

### 4.2 Payroll Integration
**Decision:** Full payroll with salary component templates, PPh 21, and BPJS calculations.

**Components:**
- Gaji Pokok
- Tunjangan (transport, makan, komunikasi)
- BPJS Kesehatan (4% company + 1% employee)
- BPJS Ketenagakerjaan (JKK, JKM, JHT, JP)
- PPh 21 (progressive rates + PTKP)

**Auto-generate journal entries for:**
- Salary expense
- BPJS payable (company + employee portions)
- PPh 21 payable
- Net salary payable

**Rationale:**
- High value-add for target users without tax background
- PPh 21 and BPJS calculations are too complex for most users
- Differentiator from generic accounting apps

### 4.3 Inventory Management
**Decision:** Basic inventory with FIFO/Average costing for online sellers.

**Scope:**
- Stock quantity tracking
- Simple buy/sell model (no production)
- COGS calculation for profit analysis

**Not Included:**
- Manufacturing/production
- Bill of Materials
- Complex assembly

**Rationale:**
- Online sellers need basic stock tracking
- Target market is simple resellers, not manufacturers

### 4.4 Fixed Asset Management
**Decision:** Basic asset register with auto-journaling via templates + scheduled batch.

**Features:**
- Asset master data: name, category, purchase date, cost, useful life
- Track status: Active, Disposed, Fully Depreciated
- Monthly depreciation batch job generates entries

**Depreciation Methods:**
- Straight-line (Garis Lurus)
- Declining balance (Saldo Menurun)

**Rationale:**
- Photographers have significant equipment
- Reuses existing journal template infrastructure

### 4.5 Budget Management
**Decision:** Simple budget vs actual reports (no approval workflows).

**Features:**
- Set budget per account per period
- Budget vs actual comparison
- Variance highlighting

**Not Included:**
- Approval workflows
- Real-time alerts
- Multi-version scenarios

### 4.6 Bank Reconciliation
**Decision:** Manual CSV upload with configurable bank parsers.

**Supported Banks (Phase 1):**
- BCA, BNI, BSI, CIMB Niaga
- Generic CSV (user maps columns)

**Reconciliation Workflow:**
1. User downloads CSV from bank app
2. Upload and select bank
3. Parser extracts: date, description, amount
4. Match against recorded transactions
5. Show matched/unmatched for review

**No PDF support** - extraction unreliable (~68% accuracy).

### 4.7 Marketplace Reconciliation
**Decision:** Configurable parser for marketplace settlement reports.

**Supported Marketplaces:**
- Tokopedia, Shopee, Bukalapak, Lazada
- Generic CSV

**Fields Extracted:**
- Order ID, dates, gross sales, marketplace fee, shipping, net settlement

**Rationale:**
- Online sellers are a target market segment
- Same configurable parser architecture as bank reconciliation

### 4.8 Business Analysis & Alerts
**Decision:** Dashboard KPIs in Phase 1; trend analysis and smart alerts in Phase 2.

**Phase 1:**
- Dashboard KPIs (revenue, expenses, profit, cash)
- Project/Client profitability

**Phase 2:**
- Trend charts (12-month)
- Smart alerts: project cost overrun, overdue receivables, expense spike, cash low

### 4.9 Amortization Schedules
**Decision:** Automated period-end adjustments for prepaid/unearned items.

**Scope:**
| Type | Auto? |
|------|-------|
| Prepaid Expense (Beban Dibayar Dimuka) | Yes |
| Unearned Revenue (Pendapatan Diterima Dimuka) | Yes |
| Intangible Asset | Yes |
| Fixed Asset Depreciation | No (Phase 5) |

**Implementation:**
- User creates schedule manually
- Monthly batch job generates journal entries
- Last period absorbs rounding difference

### 4.10 Deferred Features
**Decision:** Following features deferred to later phases or custom projects.

| Feature | Status | Reason |
|---------|--------|--------|
| Bank Integration (API) | Deferred | Regulatory/security overhead |
| Multi-Currency | Phase 2 | Most users IDR-only |
| E-wallet Integration | Custom | Not universal need |
| Payment Gateway | Custom | Per-client integration |

## Consequences
- Feature set optimized for service businesses and simple online sellers
- Complex manufacturing/production use cases not supported
- Payroll becomes a key differentiator
- Project profitability analysis helps prevent losses

## References
See [Historical Discussion](../archive/decisions-historical.md) for detailed Q&A and alternatives considered.
