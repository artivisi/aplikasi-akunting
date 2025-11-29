# Capacity Planning: Artivisi

Capacity planning for PT Artivisi Intermedia - IT services company (consulting, training, development, remittance).

## Business Profile

| Metric | Value |
|--------|-------|
| Business Type | IT Services (B2B) |
| Revenue Streams | Training, Consulting, Development, Remittance |
| Active Clients | ≤20 |
| Concurrent Projects | ≤20 |
| Employees | <10 |
| Bank Accounts | 5-6 (BCA, BNI Ops, BNI Escrow, BSI, CIMB) |

## Transaction Volume Estimates

### Monthly Transactions

| Transaction Type | Volume/Month | Notes |
|------------------|--------------|-------|
| Income (Invoices) | 10-15 | Training + consulting projects |
| Expenses | 30-50 | Cloud, software, utilities, supplies |
| Payroll | 8-10 | Monthly salary entries |
| Tax Payments | 4-6 | PPh 21, 23, PPN, etc. |
| Bank Transfers | 10-20 | Inter-account, client payments |
| Escrow (Remittance) | 50-150 | If remittance active |
| **Total** | **120-250** | |

### Annual Projections

| Year | Transactions | Journal Entries | Documents |
|------|--------------|-----------------|-----------|
| Year 1 | 1,800 | 5,400 | 900 |
| Year 3 | 2,400 | 7,200 | 1,200 |
| Year 5 | 3,000 | 9,000 | 1,500 |
| Year 10 | 4,000 | 12,000 | 2,500 |

## Storage Requirements

### Database Size

| Table | Rows/Year | Row Size | Annual Growth |
|-------|-----------|----------|---------------|
| journal_entries | 6,000 | 500 bytes | 3 MB |
| journal_entry_lines | 18,000 | 200 bytes | 3.5 MB |
| transactions | 2,000 | 1 KB | 2 MB |
| documents | 1,000 | 500 bytes | 500 KB |
| projects | 30 | 2 KB | 60 KB |
| invoices | 150 | 1 KB | 150 KB |
| **Total DB Growth** | | | **~10 MB/year** |

### 10-Year Database Projection

```
Year 1:   40 MB (initial + first year)
Year 5:   80 MB
Year 10:  130 MB
```

### Document Storage

| Document Type | Count/Year | Avg Size | Annual Growth |
|---------------|------------|----------|---------------|
| Receipt images | 750 | 200 KB | 150 MB |
| Invoice PDFs | 150 | 100 KB | 15 MB |
| Tax documents | 30 | 500 KB | 15 MB |
| Reports | 50 | 200 KB | 10 MB |
| **Total Docs** | | | **~200 MB/year** |

### 10-Year Storage Projection

```
Database:  130 MB
Documents: 2 GB
Backups:   5 GB (daily rotation, weekly/monthly archives)
Logs:      1 GB (rotated)
-----------------------
Total:     ~10 GB
```

## Infrastructure Sizing

### Minimum (Production - Artivisi Scale)

| Resource | Spec | Notes |
|----------|------|-------|
| CPU | 1 vCPU | Sufficient for <10 users |
| RAM | 2 GB | JVM 1GB + PostgreSQL 512MB + OS |
| Disk | 20 GB SSD | 10 years data + backups |
| Network | 100 Mbps | Standard bandwidth |

### Recommended (Production with Headroom)

| Resource | Spec | Notes |
|----------|------|-------|
| CPU | 2 vCPU | Handle peak loads |
| RAM | 4 GB | Comfortable margins |
| Disk | 40 GB SSD | Room for growth |
| Network | 1 Gbps | Fast document uploads |

### Memory Allocation (Minimum 2GB)

```
JVM Heap:     1 GB (-Xmx1024m)
PostgreSQL:   512 MB (shared_buffers=128MB)
OS + Buffer:  512 MB
-----------------------
Total:        2 GB
```

## Concurrent Users

| Metric | Value |
|--------|-------|
| Total Users | 3-5 (admin, accountant, owner) |
| Peak Concurrent | 2-3 |
| Sessions | Stateless (HTMX) |
| Request Rate | 5-20 req/min (peak) |

## VPS Provider Price Comparison

### Target Spec: 2 vCPU, 4GB RAM, 40-50GB SSD

#### Indonesian Providers

| Provider | Plan | Spec | Price/Month | Notes |
|----------|------|------|-------------|-------|
| [IDCloudHost](https://idcloudhost.com/cloud-vps/) | Cloud VPS | 2 vCPU, 4GB, 40GB NVMe | ~Rp 200,000 | Pay-as-you-grow |
| [Biznet Gio](https://www.biznetgio.com/en/product/neo-lite) | NEO Lite | 2 vCPU, 4GB, 60GB SSD | ~Rp 200,000 | Free 10 Gbps bandwidth |
| [DewaVPS](https://www.dewavps.com/) | Cloud VPS | 2 vCPU, 4GB, 40GB NVMe | ~Rp 250,000 | AMD EPYC Genoa |

#### Global Providers

| Provider | Plan | Spec | Price/Month | Notes |
|----------|------|------|-------------|-------|
| [DigitalOcean](https://www.digitalocean.com/pricing/droplets) | Basic Droplet | 2 vCPU, 4GB, 80GB SSD | $24 (~Rp 380,000) | Singapore DC |
| [Vultr](https://www.vultr.com/pricing/) | Cloud Compute | 2 vCPU, 4GB, 80GB SSD | $24 (~Rp 380,000) | Singapore DC |
| [Linode/Akamai](https://www.linode.com/pricing/) | Shared CPU | 2 vCPU, 4GB, 80GB SSD | $24 (~Rp 380,000) | Singapore DC |

#### Budget Option: 1 vCPU, 2GB RAM (Sufficient for Artivisi)

| Provider | Plan | Spec | Price/Month |
|----------|------|------|-------------|
| IDCloudHost | Cloud VPS | 1 vCPU, 2GB, 20GB | ~Rp 100,000 |
| Biznet Gio | NEO Lite S | 1 vCPU, 2GB, 60GB | ~Rp 100,000 |
| DigitalOcean | Basic | 1 vCPU, 2GB, 50GB | $12 (~Rp 190,000) |
| Vultr | Cloud Compute | 1 vCPU, 2GB, 55GB | $12 (~Rp 190,000) |

### Recommendation

For Artivisi's scale (<10 employees, ≤20 clients):

1. **Best Value**: Biznet Gio NEO Lite (Rp 100-200k/month)
   - Indonesian DC for low latency
   - Free unlimited bandwidth
   - Good for data residency compliance

2. **Alternative**: IDCloudHost (Rp 100-200k/month)
   - Indonesian provider
   - Flexible hourly billing
   - NVMe storage

3. **Global Option**: DigitalOcean/Vultr Singapore ($12-24/month)
   - Better documentation
   - More predictable performance
   - ~30-50ms latency from Jakarta

## Total Monthly Cost

| Item | Budget Option | Recommended |
|------|---------------|-------------|
| VPS (Indonesian) | Rp 100,000 | Rp 200,000 |
| Domain (.co.id) | Rp 15,000 | Rp 15,000 |
| SSL | Free (Let's Encrypt) | Free |
| Backup Storage | Rp 25,000 | Rp 50,000 |
| **Total** | **Rp 140,000** | **Rp 265,000** |

## Backup Requirements

### Backup Schedule

| Type | Frequency | Retention | Storage |
|------|-----------|-----------|---------|
| Database (full) | Daily | 30 days | 500 MB |
| Documents | Daily incremental | 30 days | 2 GB |
| Weekly archive | Weekly | 12 weeks | 1.5 GB |
| Monthly archive | Monthly | 10 years | 1 GB |

### Recovery Objectives

| Metric | Target |
|--------|--------|
| RPO (Recovery Point) | 24 hours |
| RTO (Recovery Time) | 4 hours |

## Scaling Triggers

| Metric | Threshold | Action |
|--------|-----------|--------|
| CPU | >70% sustained | Upgrade to 2 vCPU |
| RAM | >80% used | Upgrade to 4 GB |
| Disk | >70% full | Expand storage |
| Concurrent users | >10 | Consider larger instance |

## Summary

For Artivisi's scale (small IT services, <10 employees, ≤20 clients):

- **Minimum VPS**: 1 vCPU, 2GB RAM, 20GB SSD
- **Recommended VPS**: 2 vCPU, 4GB RAM, 40GB SSD
- **Monthly Cost**: Rp 140,000 - 265,000
- **10-Year Storage**: ~10 GB total
- **Best Provider**: Biznet Gio or IDCloudHost (Indonesian DC, compliance)

The minimum spec (1 vCPU, 2GB) is sufficient for current operations. Scale up only if business grows significantly.
