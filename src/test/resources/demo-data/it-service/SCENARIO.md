# IT Service Demo Scenario: PT Solusi Digital Nusantara

## Company Profile

| Field | Value |
|-------|-------|
| Company | PT Solusi Digital Nusantara |
| Industry | IT Consulting & Development |
| NPWP | 01.234.567.8-201.000 |
| PKP | Yes (since 2019-01-01) |
| Fiscal Year | January - December |
| Currency | IDR |
| Address | Jl. Sudirman No. 123, Jakarta Selatan |

## Employees (5 permanent, all hired before 2025)

| ID | Name | Position | PTKP | TER Category | Monthly Gross |
|----|------|----------|------|-------------|--------------|
| EMP-D001 | Ahmad Fauzi | CTO | K_2 | B | 15,000,000 |
| EMP-D002 | Sari Wulandari | Project Manager | TK_0 | A | 15,000,000 |
| EMP-D003 | Riko Pratama | Senior Developer | K_1 | B | 15,000,000 |
| EMP-D004 | Maya Anggraini | Business Analyst | TK_0 | A | 15,000,000 |
| EMP-D005 | Dian Kusuma | QA Lead | K_0 | A | 15,000,000 |

## Clients & Projects

| Code | Client | Type | Project |
|------|--------|------|---------|
| MANDIRI | PT Bank Mandiri Tbk | BUMN | PRJ-MND-01: Core Banking Modernization |
| TELKOM | PT Telkom Indonesia Tbk | BUMN | PRJ-TLK-01: Network Monitoring System |
| PLN | PT PLN (Persero) | BUMN | PRJ-PLN-01: Smart Grid Analytics |
| GRAB | PT Grab Indonesia | Swasta | PRJ-GRB-01: Driver Onboarding Platform |
| KOMINFO | Kementerian Kominfo | Pemerintah | PRJ-KOM-01: e-Government Portal |

## Fixed Assets

| Code | Name | Category | Purchase Date | Cost | Useful Life | Monthly Depreciation |
|------|------|----------|---------------|------|-------------|---------------------|
| AST-LPT-001 | Laptop HP EliteBook 860 | KOMPUTER | 2025-01-15 | 25,000,000 | 48 months | 520,833 |
| AST-SRV-001 | Server Dell PowerEdge R750 | KOMPUTER | 2025-03-20 | 45,000,000 | 48 months | 937,500 |

## Monthly Payroll Calculation (per employee, at 15,000,000 gross)

### BPJS Deductions

| Component | Employee | Company | Base/Cap |
|-----------|----------|---------|----------|
| BPJS Kesehatan | 120,000 (1%) | 480,000 (4%) | Cap: 12,000,000 |
| BPJS JHT | 300,000 (2%) | 555,000 (3.7%) | No cap |
| BPJS JP | 100,423 (1%) | 200,846 (2%) | Cap: 10,042,300 |
| BPJS JKK (class 1) | — | 36,000 (0.24%) | No cap |
| BPJS JKM | — | 45,000 (0.3%) | No cap |
| **Subtotal** | **520,423** | **1,316,846** | |

### PPh 21 TER (Jan-Nov)

| Employee | PTKP | TER Rate | Monthly PPh 21 |
|----------|------|----------|---------------|
| EMP-D001 (K_2) | Category B | ~4% | ~600,000 |
| EMP-D002 (TK_0) | Category A | ~4.5% | ~675,000 |
| EMP-D003 (K_1) | Category B | ~4% | ~600,000 |
| EMP-D004 (TK_0) | Category A | ~4.5% | ~675,000 |
| EMP-D005 (K_0) | Category A | ~4.5% | ~675,000 |

Note: Exact TER amounts depend on the app's bracket lookup. The verification test logs the actual computed amounts.

### Monthly Payroll Totals (5 employees, Jan-Nov)

| Item | Amount |
|------|--------|
| Total Gross | 75,000,000 |
| Total Employee BPJS | 2,602,115 (5 × 520,423) |
| Total PPh 21 | ~3,225,000 - 3,600,000 (varies by exact TER lookup) |
| Total Employee Deductions | ~5,827,115 - 6,202,115 |
| Total Net Pay | ~68,797,885 - 69,172,885 |
| Total Company BPJS | 6,584,230 (5 × 1,316,846) |

### December Reconciliation

In December, PPh 21 is recalculated using annual progressive rates (5% on first 60M, 15% on 60-250M of PKP) and adjusted for Jan-Nov TER withholdings. December PPh 21 is typically higher than monthly TER.

## Post Gaji Bulanan Journal Entry (per month)

| Account | Debit | Credit | Variable |
|---------|-------|--------|----------|
| 5.1.01 Beban Gaji | 75,000,000 | | grossSalary |
| 5.1.02 Beban BPJS Kesehatan | (company kes) | | companyBpjsKes |
| 5.1.03 Beban BPJS Ketenagakerjaan | (company tk) | | companyBpjsTk |
| 2.1.10 Hutang Gaji | | (net pay) | netPay |
| 2.1.13 Hutang BPJS | | (total BPJS) | totalBpjs |
| 2.1.20 Hutang PPh 21 | | (total PPh 21) | pph21 |

## Monthly Transaction Flow

For each month January-December 2025, the following operations are executed sequentially:

### Phase 1: Revenue Transactions (from CSV)

Income transactions use these templates depending on client type:

| Template | Formula | Client Type |
|----------|---------|-------------|
| Pendapatan Jasa + PPN | Bank(D)=amt×1.11, Pendapatan(C)=amt, HutangPPN(C)=amt×0.11 | Swasta PKP (GRAB) |
| Pendapatan Jasa + PPN + PPh 23 | Bank(D)=amt×1.09, KreditPPh23(D)=amt×0.02, Pendapatan(C)=amt, HutangPPN(C)=amt×0.11 | Swasta Besar (MANDIRI, TELKOM) |
| Pendapatan Jasa BUMN (FP 03) | Bank(D)=amt×0.98, KreditPPh23(D)=amt×0.02, Pendapatan(C)=amt | BUMN/Pemerintah (PLN, KOMINFO) |

### Phase 2: Expense Transactions (from CSV)

| Template | Account Debit | Amount/Month |
|----------|--------------|-------------|
| Bayar Beban Cloud & Server | 5.1.20 | 5,500,000 |
| Bayar Beban Software & Lisensi | 5.1.21 | 3,300,000 (quarterly) |
| Bayar Beban Sewa | 5.1.05 | 15,000,000 |
| Bayar Beban Telekomunikasi | 5.1.06 | 2,500,000 |
| Bayar Beban Operasional | 5.1.99 | 2,500,000 - 3,500,000 |
| Beban Admin Bank | 5.2.01 | 15,000 |

All expense templates: Beban(D)=amount, Bank(C)=amount.

### Phase 3: Payroll (auto by loader)

1. **Create payroll run** via `/payroll/new` form (period=YYYY-MM, baseSalary=15000000, jkkRiskClass=1)
2. **Auto-calculate** — app computes BPJS + PPh 21 TER for all 5 employees
3. **Approve** via `#btn-approve`
4. **Post to journal** via `#btn-post` — creates "Post Gaji Bulanan" transaction with journal entries

### Phase 4: Pay Salary & BPJS (auto by loader)

5. **Bayar Hutang Gaji** — amount = payrollRun.totalNetPay
   - 2.1.10 Hutang Gaji (D) = netPay
   - 1.1.02 Bank BCA (C) = netPay
6. **Bayar Hutang BPJS** — amount = total employee BPJS + total company BPJS
   - 2.1.13 Hutang BPJS (D) = totalBpjs
   - 1.1.02 Bank BCA (C) = totalBpjs

### Phase 5: PPh 21 Deposit (auto by loader, next month)

7. **Setor PPh 21** — amount = payrollRun.totalPph21 (deposited on 10th of following month)
   - 2.1.20 Hutang PPh 21 (D) = pph21
   - 1.1.02 Bank BCA (C) = pph21

### Phase 6: Depreciation (auto by loader)

8. Navigate to `/assets/depreciation`, post all unposted entries
   - 5.1.12 Beban Penyusutan (D) = monthly depreciation
   - 1.2.02 Akum. Penyusutan (C) = monthly depreciation

### Phase 7: Close Period (auto by loader)

9. Navigate to fiscal period detail, click "Tutup Bulan"

## Income Schedule (2025)

| Month | Client | Template | Amount | PPN 11% | PPh 23 2% |
|-------|--------|----------|--------|---------|-----------|
| Jan | MANDIRI | +PPN+PPh23 | 150,000,000 | 16,500,000 | 3,000,000 |
| Jan | PLN | BUMN FP03 | 200,000,000 | (dipungut) | 4,000,000 |
| Feb | GRAB | +PPN | 80,000,000 | 8,800,000 | — |
| Feb | TELKOM | +PPN+PPh23 | 120,000,000 | 13,200,000 | 2,400,000 |
| Mar | KOMINFO | BUMN FP03 | 180,000,000 | (dipungut) | 3,600,000 |
| Apr | MANDIRI | +PPN+PPh23 | 100,000,000 | 11,000,000 | 2,000,000 |
| May | TELKOM | +PPN+PPh23 | 130,000,000 | 14,300,000 | 2,600,000 |
| Jun | PLN | BUMN FP03 | 250,000,000 | (dipungut) | 5,000,000 |
| Jul | — | +PPN | 90,000,000 | 9,900,000 | — |
| Aug | MANDIRI | +PPN+PPh23 | 160,000,000 | 17,600,000 | 3,200,000 |
| Sep | KOMINFO | BUMN FP03 | 220,000,000 | (dipungut) | 4,400,000 |
| Oct | TELKOM | +PPN+PPh23 | 140,000,000 | 15,400,000 | 2,800,000 |
| Nov | — | +PPN | 110,000,000 | 12,100,000 | — |
| Dec | MANDIRI | +PPN+PPh23 | 180,000,000 | 19,800,000 | 3,600,000 |
| **Total** | | | **2,110,000,000** | **138,600,000** | **36,600,000** |

Notes:
- PPN 11% = amount × 11% (DPP Nilai Lain per PMK 131/2024: DPP = amount × 11/12, PPN = DPP × 12% = amount × 11%)
- BUMN (FP03): PPN dipungut oleh pembeli, seller doesn't record PPN. PPh 23 still applies.
- Kredit Pajak PPh 23 = 2% of invoice amount (received as debit to 1.1.26)

## Annual Summary (Expected End-of-Year Balances)

### Revenue
- Total Pendapatan Jasa: 2,110,000,000

### PPN
- PPN Keluaran (from +PPN and +PPN+PPh23 templates): 138,600,000
- PPN deposits needed (Setor PPN): depends on PPN Masukan offset

### PPh
- Kredit Pajak PPh 23: 36,600,000
- PPh 21 dari payroll: ~38,700,000 - 43,200,000 (11 months TER + December reconciliation)

### Operating Expenses (annual)
- Beban Gaji: 900,000,000 (75M × 12)
- Beban BPJS Kesehatan: ~28,800,000
- Beban BPJS Ketenagakerjaan: ~50,184,000
- Beban Sewa: 180,000,000
- Beban Cloud & Server: 66,000,000
- Beban Telekomunikasi: 30,000,000
- Beban Software & Lisensi: 9,900,000
- Beban Operasional: 35,100,000
- Beban Admin Bank: 180,000
- Beban Penyusutan: ~14,062,500 (laptop 9 months + server 7 months)

### Balance Sheet (Expected)
- Bank BCA: should be positive (revenue > expenses + payroll)
- Modal Disetor: 500,000,000
- Hutang Gaji: should be 0 (paid each month)
- Hutang BPJS: should be 0 (paid each month)
- Hutang PPN: 138,600,000 - deposits (if no Setor PPN, full amount outstanding)
- Hutang PPh 21: should be 0 (deposited next month; December PPh 21 deposited in Jan 2026)
- Kredit Pajak PPh 23: 36,600,000

## Verification Checkpoints

### After January 2025
- Transactions: 9 manual + 1 payroll + 2 post-payroll = 12
- Bank BCA: 500M (modal) + 150M×1.09 (Mandiri) + 200M×0.98 (PLN) - expenses - payroll payments
- Hutang PPN: 16,500,000 (from Mandiri invoice)
- Hutang Gaji: 0 (paid same month)
- Hutang BPJS: 0 (paid same month)

### After June 2025 (mid-year)
- 6 months of payroll posted
- 6 months of depreciation (laptop from Feb, server from Apr)
- All periods Jan-Jun closed

### After December 2025 (year-end)
- 12 months of payroll, including December reconciliation
- All 2025 periods closed
- Trial balance: debit total = credit total
- All liability accounts (Hutang Gaji, Hutang BPJS) should be zero
- Hutang PPh 21: December PPh 21 outstanding (deposited in Jan 2026)
- Hutang PPN: accumulated if no Setor PPN

## Known Limitations
- All employees receive the same baseSalary (15M) — the payroll UI doesn't support per-employee salary
- Employee salary components from seed data are empty
- Closing journal entry not included (would require Jurnal Manual template)
- PPN deposits (Setor PPN) not auto-generated yet
