# PT Artivisi Intermedia

Client-specific configuration for PT Artivisi Intermedia.

## Business Profile

| Attribute | Value |
|-----------|-------|
| Business Type | IT Services (B2B) |
| Services | Training, Consulting, Development |
| Employees | <10 |
| Location | Jakarta, Indonesia |

## Files

| File | Description |
|------|-------------|
| `capacity-planning.md` | Infrastructure sizing and cost estimates |

## Seed Data

Seed data for this client is stored in encrypted backup (not in version control).

To deploy:
1. Restore seed data ZIP from encrypted backup
2. Import via Settings > Import Data

## Deployment

Domain: `akunting.artivisi.id`

See:
- `capacity-planning.md` for VPS sizing
- `docs/03-operations-guide.md` for deployment steps

---

## Template for New Clients

To add a new client, create a folder structure:

```
clients/<client-name>/
├── README.md              # Copy this template
├── capacity-planning.md   # Copy from artivisi as reference
└── seed-data/             # Client-specific seed data (optional)
    └── ...                # Or use industry-seed packages
```

For seed data, clients can either:
1. Use an `industry-seed/` package as starting point
2. Create custom seed data in `seed-data/` folder
3. Store sensitive data in encrypted backup (recommended for production)
