# Kelola Template

## Kapan Anda Membutuhkan Ini

Gunakan panduan ini ketika Anda:
- Ingin membuat template untuk transaksi baru
- Perlu mengedit formula di template yang ada
- Ingin menduplikat template untuk variasi
- Perlu menonaktifkan template yang tidak dipakai

## Konsep yang Perlu Dipahami

### Apa Itu Template Jurnal?

Template jurnal adalah pola pencatatan transaksi yang sudah dikonfigurasi. Manfaat:
- **Mempercepat input** - Tidak perlu pilih akun satu per satu
- **Mengurangi kesalahan** - Formula otomatis menghitung PPN, PPh
- **Konsistensi** - Transaksi serupa tercatat dengan cara yang sama

### Kategori Template

| Kategori | Warna | Contoh |
|----------|-------|--------|
| **Pendapatan** | Hijau | Pendapatan Jasa, Pendapatan dengan PPN |
| **Pengeluaran** | Merah | Beban Gaji, Beban Listrik |
| **Pembayaran** | Biru | Bayar Hutang, Bayar Vendor |
| **Penerimaan** | Cyan | Terima Piutang, Terima DP |
| **Transfer** | Ungu | Transfer Bank, Isi Kas Kecil |

### Komponen Template

| Komponen | Fungsi |
|----------|--------|
| **Nama** | Nama yang muncul di daftar template |
| **Kategori** | Pengelompokan template |
| **Klasifikasi Arus Kas** | Operasional, Investasi, atau Pendanaan |
| **Baris Jurnal** | Akun dan formula untuk debit/kredit |

## Skenario 1: Lihat dan Cari Template

**Situasi**: Anda ingin menemukan template yang sesuai untuk transaksi.

**Langkah-langkah**:

1. Klik menu **Template** di sidebar
2. Template ditampilkan dalam bentuk kartu
3. Gunakan fitur pencarian dan filter:
   - **Pencarian**: Ketik nama template
   - **Tab Kategori**: Klik tab Pendapatan/Pengeluaran/dll
   - **Tag**: Filter berdasarkan tag (PPN, PPh, dll)

**Fitur Favorit**:
- Klik ikon bintang untuk menandai template favorit
- Template favorit muncul di bagian atas

## Skenario 2: Buat Template Beban Baru (Sederhana)

**Situasi**: Anda perlu template untuk mencatat biaya parkir.

**Langkah-langkah**:

1. Klik menu **Template** di sidebar
2. Klik tombol **Template Baru**
3. Isi informasi dasar:
   - **Nama**: `Beban Parkir`
   - **Kategori**: `Pengeluaran`
   - **Klasifikasi Arus Kas**: `Operasional`
   - **Tipe Template**: `Sederhana`
4. Konfigurasi baris jurnal:

| Posisi | Akun | Formula |
|--------|------|---------|
| Debit | Beban Transportasi | `amount` |
| Kredit | (Akun Sumber) | `amount` |

5. Klik **Simpan Template**

**Hasil**: Template siap digunakan. Saat membuat transaksi, user tinggal pilih akun sumber (Kas Kecil atau Bank) dan masukkan jumlah.

## Skenario 3: Buat Template dengan PPN

**Situasi**: Anda perlu template untuk pembelian dengan PPN.

**Langkah-langkah**:

1. Klik menu **Template** di sidebar
2. Klik tombol **Template Baru**
3. Isi informasi dasar:
   - **Nama**: `Pembelian Perlengkapan dengan PPN`
   - **Kategori**: `Pengeluaran`
   - **Tag**: `PPN`
4. Konfigurasi baris jurnal:

| Posisi | Akun | Formula | Keterangan |
|--------|------|---------|------------|
| Debit | Perlengkapan Kantor | `amount / 1.11` | DPP |
| Debit | PPN Masukan | `amount * 0.11 / 1.11` | PPN |
| Kredit | (Akun Sumber) | `amount` | Total bayar |

5. Klik **Simpan Template**

**Formula yang Digunakan**:
- `amount / 1.11` = menghitung DPP dari nilai inklusif
- `amount * 0.11 / 1.11` = menghitung PPN dari nilai inklusif
- `amount` = nilai total yang diinput user

## Skenario 4: Buat Template dengan PPh 23

**Situasi**: Anda perlu template untuk bayar vendor jasa dengan pemotongan PPh 23.

**Langkah-langkah**:

1. Klik menu **Template** di sidebar
2. Klik tombol **Template Baru**
3. Isi informasi dasar:
   - **Nama**: `Pembayaran Jasa (PPh 23)`
   - **Kategori**: `Pembayaran`
   - **Tag**: `PPh23`
4. Konfigurasi baris jurnal:

| Posisi | Akun | Formula | Keterangan |
|--------|------|---------|------------|
| Debit | Beban Jasa Profesional | `amount` | Nilai bruto |
| Kredit | (Akun Sumber) | `amount * 0.98` | Nett (setelah potong) |
| Kredit | Hutang PPh 23 | `amount * 0.02` | PPh dipotong |

5. Klik **Simpan Template**

## Skenario 5: Template dengan PPh Kondisional (Threshold)

**Situasi**: PPh 23 hanya dipotong jika nilai di atas Rp 2.000.000.

**Langkah-langkah**:

1. Buat template seperti Skenario 4
2. Ubah formula PPh menjadi kondisional:

| Posisi | Akun | Formula |
|--------|------|---------|
| Debit | Beban Jasa | `amount` |
| Kredit | (Akun Sumber) | `amount > 2000000 ? amount * 0.98 : amount` |
| Kredit | Hutang PPh 23 | `amount > 2000000 ? amount * 0.02 : 0` |

**Format Formula Kondisional**:
```
kondisi ? nilai_jika_benar : nilai_jika_salah
```

**Contoh**:
- Jika amount = 3.000.000 → PPh = 60.000 (2%)
- Jika amount = 1.500.000 → PPh = 0

## Skenario 6: Test Formula Sebelum Simpan

**Situasi**: Anda ingin memastikan formula sudah benar sebelum menyimpan template.

**Langkah-langkah**:

1. Di form template, setelah memasukkan formula
2. Klik tombol **Coba Formula**
3. Masukkan nilai contoh: `10000000`
4. Lihat hasil perhitungan:

```
Input: Rp 10.000.000

Hasil Perhitungan:
─────────────────────────────────
Beban Jasa Profesional (Dr)  Rp 10.000.000
Bank BCA (Cr)                Rp  9.800.000
Hutang PPh 23 (Cr)           Rp    200.000
─────────────────────────────────
Total Debit                  Rp 10.000.000
Total Kredit                 Rp 10.000.000 ✓
```

5. Jika sudah benar, klik **Simpan Template**

## Skenario 7: Duplikat Template yang Ada

**Situasi**: Anda ingin membuat variasi dari template yang sudah ada.

**Langkah-langkah**:

1. Klik menu **Template** di sidebar
2. Cari template yang ingin diduplikat
3. Klik template untuk buka detail
4. Klik tombol **Duplikat**
5. Ubah nama: `Beban Jasa Konsultan (PPh 23)` → `Beban Jasa Desain (PPh 23)`
6. Ubah akun beban jika perlu
7. Klik **Simpan Template**

## Skenario 8: Edit Template yang Ada

**Situasi**: Tarif PPN berubah dari 11% menjadi 12%.

**Langkah-langkah**:

1. Klik menu **Template** di sidebar
2. Cari template dengan PPN
3. Klik template untuk buka detail
4. Klik **Edit**
5. Ubah formula:
   - `amount / 1.11` → `amount / 1.12`
   - `amount * 0.11 / 1.11` → `amount * 0.12 / 1.12`
6. Klik **Simpan Perubahan**

**Catatan**: Perubahan hanya berlaku untuk transaksi baru. Transaksi yang sudah ada tidak berubah.

## Skenario 9: Nonaktifkan Template

**Situasi**: Ada template yang tidak lagi digunakan.

**Langkah-langkah**:

1. Klik menu **Template** di sidebar
2. Cari template yang ingin dinonaktifkan
3. Klik template untuk buka detail
4. Klik **Edit**
5. Nonaktifkan toggle **Status Template**
6. Klik **Simpan**

**Efek**:
- Template tidak muncul saat buat transaksi baru
- Transaksi historis tetap ada

## Referensi Formula

| Formula | Hasil | Contoh (amount=11.100.000) |
|---------|-------|---------------------------|
| `amount` | Nilai input | 11.100.000 |
| `amount / 1.11` | DPP dari inklusif | 10.000.000 |
| `amount * 0.11 / 1.11` | PPN dari inklusif | 1.100.000 |
| `amount * 0.02` | PPh 23 (2%) | 222.000 |
| `amount * 0.98` | Nett setelah PPh 23 | 10.878.000 |
| `1000000` | Nilai tetap | 1.000.000 |

## Tips

1. **Naming convention** - Gunakan nama yang jelas dan konsisten
2. **Tag** - Tambahkan tag untuk memudahkan pencarian
3. **Test dulu** - Selalu test formula sebelum simpan
4. **Dokumentasi** - Isi deskripsi template dengan jelas

## Troubleshooting

| Masalah | Solusi |
|---------|--------|
| Formula error | Cek syntax, pastikan tidak ada typo |
| Total tidak balance | Pastikan total debit = total kredit |
| Template tidak muncul | Cek apakah template aktif |

## Lihat Juga

- [Konsep Dasar](01-konsep-dasar.md) - Memahami debit/kredit
- [Transaksi PPN](30-transaksi-ppn.md) - Template dengan PPN
- [Transaksi PPh](31-transaksi-pph.md) - Template dengan PPh
- [Referensi Template](92-referensi-template.md) - Daftar template bawaan
