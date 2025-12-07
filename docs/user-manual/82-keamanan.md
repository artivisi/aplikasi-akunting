# Fitur Keamanan

Dokumen ini menjelaskan fitur keamanan yang diimplementasikan dalam aplikasi untuk melindungi data dan sistem Anda.

## Otentikasi dan Otorisasi

### Kebijakan Password

Password harus memenuhi persyaratan kompleksitas:

| Persyaratan | Nilai |
|-------------|-------|
| Panjang minimal | 12 karakter |
| Huruf besar | Minimal 1 |
| Huruf kecil | Minimal 1 |
| Angka | Minimal 1 |
| Karakter spesial | Minimal 1 |

### Penguncian Akun

Sistem melindungi dari serangan brute force:

| Parameter | Nilai |
|-----------|-------|
| Percobaan gagal maksimal | 5 kali |
| Durasi penguncian | 30 menit |
| Reset otomatis | Ya, setelah durasi berakhir |

### Session Management

| Parameter | Nilai |
|-----------|-------|
| Session timeout | 15 menit idle |
| Secure cookie | Ya (HTTPS only) |
| HttpOnly cookie | Ya (tidak dapat diakses JavaScript) |
| SameSite | Strict (mencegah CSRF) |

### Rate Limiting

Endpoint login dilindungi dengan rate limiting untuk mencegah serangan:

| Parameter | Nilai |
|-----------|-------|
| Request per menit | 10 |
| Cooldown | 1 menit |

## Enkripsi

### Enkripsi Data Tersimpan (At Rest)

Data sensitif dienkripsi menggunakan AES-256-GCM sebelum disimpan ke database:

| Field | Entitas | Status |
|-------|---------|--------|
| NIK KTP | Employee | Terenkripsi |
| NPWP | Employee | Terenkripsi |
| Nomor Rekening | Employee, CompanyBankAccount | Terenkripsi |
| BPJS Kesehatan | Employee | Terenkripsi |
| BPJS Ketenagakerjaan | Employee | Terenkripsi |

Dokumen yang diunggah juga dienkripsi dengan AES-256-GCM sebelum disimpan ke disk.

### Enkripsi Data Transit (In Transit)

| Jalur Komunikasi | Protokol |
|------------------|----------|
| Browser ke Aplikasi | HTTPS (TLS 1.2/1.3) |
| Aplikasi ke Database | PostgreSQL SSL |
| Backup ke Cloud | HTTPS + GPG |

### Konfigurasi TLS

| Parameter | Nilai |
|-----------|-------|
| Protokol | TLS 1.2, TLS 1.3 |
| Cipher suites | ECDHE, AES-GCM, CHACHA20 |
| HSTS | max-age=63072000 (2 tahun) |

## Header Keamanan HTTP

Aplikasi mengirimkan header keamanan berikut:

| Header | Nilai | Fungsi |
|--------|-------|--------|
| Content-Security-Policy | Default-src 'self' | Mencegah XSS |
| X-Frame-Options | DENY | Mencegah clickjacking |
| X-Content-Type-Options | nosniff | Mencegah MIME sniffing |
| Strict-Transport-Security | max-age=63072000 | Enforce HTTPS |
| X-XSS-Protection | 1; mode=block | XSS filter |

## Audit Log

Semua aktivitas keamanan dicatat dalam log audit:

### Event yang Dicatat

| Event | Keterangan |
|-------|------------|
| LOGIN_SUCCESS | Login berhasil |
| LOGIN_FAILURE | Login gagal |
| LOGOUT | Logout |
| PASSWORD_CHANGE | Perubahan password |
| USER_CREATE | Pembuatan pengguna |
| USER_UPDATE | Update data pengguna |
| USER_DELETE | Penghapusan pengguna |
| DATA_EXPORT | Ekspor data (DSAR) |
| SETTINGS_CHANGE | Perubahan pengaturan |

### Informasi yang Dicatat

Setiap event mencatat:
- Timestamp (waktu kejadian)
- Username (pelaku)
- IP Address (alamat IP)
- User Agent (browser/client)
- Detail event

### Akses Log Audit

Administrator dapat mengakses log melalui **Pengaturan > Log Audit**:

![Halaman Log Audit](screenshots/settings-audit-logs.png)

- Filter berdasarkan tipe event
- Filter berdasarkan username
- Filter berdasarkan rentang tanggal

### Retensi Log

Log audit disimpan selama 2 tahun dan dirotasi secara otomatis.

## Perlindungan Data dalam Memori

Sistem JVM dikonfigurasi untuk melindungi data di memori:

| Setting | Nilai | Fungsi |
|---------|-------|--------|
| DisableAttachMechanism | Ya | Mencegah debugger attach |
| UseZGC | Ya | Memory management yang lebih aman |
| LimitCORE | 0 | Disable core dumps |
| ProtectSystem | strict | Readonly filesystem |
| PrivateTmp | Ya | Isolated temp directory |
| NoNewPrivileges | Ya | Mencegah privilege escalation |

## Kepatuhan

### OWASP Top 10 (2021)

Aplikasi mengatasi risiko keamanan OWASP Top 10:

| Risk | Mitigasi |
|------|----------|
| A01: Broken Access Control | RBAC, session management |
| A02: Cryptographic Failures | AES-256-GCM, TLS 1.2+ |
| A03: Injection | Parameterized queries, input validation |
| A04: Insecure Design | Security by design |
| A05: Security Misconfiguration | Security headers, secure defaults |
| A06: Vulnerable Components | Dependency scanning |
| A07: Authentication Failures | Password policy, lockout |
| A08: Data Integrity Failures | Input validation, CSRF protection |
| A09: Security Logging | Comprehensive audit logging |
| A10: SSRF | URL validation |

### GDPR dan UU PDP

Aplikasi mendukung kepatuhan terhadap regulasi perlindungan data:

| Hak | Implementasi |
|-----|--------------|
| Right to Access (Art. 15) | Data export via DSAR |
| Right to Rectification (Art. 16) | Profile editing |
| Right to Erasure (Art. 17) | Data anonymization |
| Data Portability (Art. 20) | Data export |
| Storage Limitation | 10-year retention policy |
| Security (Art. 32) | Encryption, access control |

## Panduan Administrator

### Monitoring Keamanan

1. **Log Audit**: Periksa log audit secara berkala untuk aktivitas mencurigakan
2. **Failed Logins**: Monitor login gagal yang berulang dari IP yang sama
3. **Data Export**: Lacak siapa yang mengekspor data dan kapan

### Respons Insiden

Jika terjadi insiden keamanan:

1. Identifikasi scope insiden melalui log audit
2. Nonaktifkan akun yang terkompromi
3. Reset password pengguna terdampak
4. Dokumentasikan dan laporkan sesuai kebijakan

### Backup dan Recovery

Backup dienkripsi dengan:
- AES-256 untuk enkripsi simetris
- GPG untuk enkripsi asimetris
- Checksum SHA-256 untuk verifikasi integritas

Lokasi backup:
- Lokal: 7 hari terakhir
- Backblaze B2: 4 minggu terakhir
- Google Drive: 12 bulan arsip
