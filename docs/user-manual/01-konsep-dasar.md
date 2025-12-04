# Konsep Dasar Akuntansi

## Kapan Anda Membutuhkan Ini

Baca bab ini jika Anda:
- Baru pertama kali menggunakan aplikasi akuntansi
- Ingin memahami istilah-istilah yang digunakan di aplikasi
- Perlu memahami mengapa transaksi dicatat dengan cara tertentu

## Apa Itu Akuntansi?

Akuntansi adalah proses mencatat, mengklasifikasi, dan melaporkan transaksi keuangan bisnis. Tujuannya:
- Mengetahui kondisi keuangan bisnis
- Membuat keputusan bisnis yang tepat
- Memenuhi kewajiban pelaporan pajak

## Persamaan Dasar Akuntansi

```
Aset = Kewajiban + Ekuitas
```

| Komponen | Pengertian | Contoh |
|----------|------------|--------|
| **Aset** | Apa yang dimiliki bisnis | Kas, piutang, peralatan |
| **Kewajiban** | Apa yang dihutang bisnis | Hutang vendor, hutang pajak |
| **Ekuitas** | Modal pemilik | Modal awal, laba ditahan |

Setiap transaksi harus menjaga keseimbangan persamaan ini.

## Debit dan Kredit

Setiap transaksi dicatat dalam dua sisi: **debit** dan **kredit**. Total debit harus sama dengan total kredit.

| Jenis Akun | Bertambah | Berkurang |
|------------|-----------|-----------|
| Aset | Debit | Kredit |
| Kewajiban | Kredit | Debit |
| Ekuitas | Kredit | Debit |
| Pendapatan | Kredit | Debit |
| Beban | Debit | Kredit |

### Contoh: Terima Pembayaran dari Klien

Anda menerima Rp 10.000.000 dari klien untuk jasa konsultasi.

| Akun | Debit | Kredit |
|------|-------|--------|
| Kas/Bank | 10.000.000 | |
| Pendapatan Jasa | | 10.000.000 |

- Kas (aset) bertambah → debit
- Pendapatan bertambah → kredit

### Contoh: Bayar Listrik

Anda membayar tagihan listrik Rp 500.000.

| Akun | Debit | Kredit |
|------|-------|--------|
| Beban Listrik | 500.000 | |
| Kas/Bank | | 500.000 |

- Beban bertambah → debit
- Kas (aset) berkurang → kredit

## Bagan Akun (Chart of Accounts)

Bagan akun adalah daftar semua akun yang digunakan untuk mencatat transaksi. Akun dikelompokkan berdasarkan jenisnya:

| Kode | Jenis | Contoh |
|------|-------|--------|
| 1.x.xx | Aset | Kas, Bank, Piutang, Peralatan |
| 2.x.xx | Kewajiban | Hutang Usaha, Hutang Pajak |
| 3.x.xx | Ekuitas | Modal, Laba Ditahan |
| 4.x.xx | Pendapatan | Pendapatan Jasa, Pendapatan Lain |
| 5.x.xx | Beban | Gaji, Sewa, Listrik, Internet |

## Transaksi dan Jurnal

Aplikasi ini menggunakan pendekatan **transaction-centric** dimana setiap pencatatan dimulai dari transaksi bisnis, bukan dari jurnal.

**Transaksi** adalah kejadian bisnis yang dicatat, misalnya:
- Terima pembayaran dari klien
- Bayar tagihan listrik
- Transfer antar rekening

**Jurnal** adalah catatan debit/kredit yang dihasilkan dari transaksi. Setiap transaksi menghasilkan satu atau lebih baris jurnal.

Keuntungan pendekatan ini:
- Anda tidak perlu memahami debit/kredit untuk mencatat transaksi
- Sistem otomatis membuat jurnal yang benar berdasarkan template
- Semua jurnal selalu terkait dengan transaksi yang jelas

## Status Transaksi

| Status | Arti |
|--------|------|
| **Draft** | Transaksi tersimpan tapi belum mempengaruhi saldo akun |
| **Posted** | Transaksi sudah diposting, mempengaruhi saldo akun |
| **Void** | Transaksi dibatalkan, jurnal reversal otomatis dibuat |

## Periode Akuntansi

Bisnis biasanya menggunakan periode akuntansi:
- **Bulanan** - Untuk laporan rutin dan pajak
- **Tahunan** - Untuk tutup buku dan laporan keuangan akhir tahun

## Tips untuk Pemula

1. **Catat segera** - Catat transaksi sesegera mungkin setelah terjadi
2. **Simpan bukti** - Simpan struk, invoice, dan dokumen pendukung
3. **Gunakan template** - Aplikasi menyediakan template untuk transaksi umum
4. **Review rutin** - Periksa laporan minimal sekali seminggu

## Lihat Juga

- [Setup Awal](50-setup-awal.md) - Konfigurasi bagan akun
- [Kelola Template](51-kelola-template.md) - Memahami template jurnal
- [Glosarium](90-glosarium.md) - Penjelasan istilah lengkap
