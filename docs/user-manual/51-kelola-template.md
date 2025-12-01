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

| Posisi | Akun | Formula | Hint (opsional) |
|--------|------|---------|-----------------|
| Debit | Beban Transportasi | `amount` | - |
| Kredit | **[ Pilih saat transaksi ]** | `amount` | `Bank/Kas` |

5. Klik **Simpan Template**

**Hasil**: 
- Template siap digunakan
- Saat membuat transaksi, user akan diminta memilih akun sumber (Kas Kecil, Bank BCA, dll)
- Hint "Bank/Kas" membantu user memahami jenis akun yang harus dipilih

**Catatan**: Akun dengan indikator **?** (tanda tanya biru) berarti akun akan dipilih saat input transaksi.

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

| Posisi | Akun | Formula | Hint | Keterangan |
|--------|------|---------|------|------------|
| Debit | **[ Pilih saat transaksi ]** | `amount / 1.11` | `Akun Beban` | DPP |
| Debit | PPN Masukan | `amount * 0.11 / 1.11` | - | PPN |
| Kredit | **[ Pilih saat transaksi ]** | `amount` | `Bank/Kas` | Total bayar |

5. Klik **Simpan Template**

**Keuntungan Dynamic Account**:
- Satu template bisa digunakan untuk berbagai jenis pembelian
- User pilih akun beban yang sesuai (Perlengkapan, Supplies, Aset, dll) saat transaksi
- User pilih akun pembayaran yang digunakan (Bank BCA, Kas, dll)

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

| Posisi | Akun | Formula | Hint | Keterangan |
|--------|------|---------|------|------------|
| Debit | **[ Pilih saat transaksi ]** | `amount` | `Akun Beban` | Nilai bruto |
| Kredit | **[ Pilih saat transaksi ]** | `amount * 0.98` | `Bank/Kas` | Nett (setelah potong) |
| Kredit | Hutang PPh 23 | `amount * 0.02` | - | PPh dipotong |

5. Klik **Simpan Template**

**Penggunaan**:
- Saat transaksi, pilih akun beban sesuai jenis jasa (Jasa Profesional, Jasa Konsultan, dll)
- Pilih akun bank yang digunakan untuk pembayaran

## Skenario 5: Template dengan PPh Kondisional (Threshold)

**Situasi**: PPh 23 hanya dipotong jika nilai di atas Rp 2.000.000.

**Langkah-langkah**:

1. Buat template seperti Skenario 4
2. Ubah formula PPh menjadi kondisional:

| Posisi | Akun | Formula | Hint |
|--------|------|---------|------|
| Debit | **[ Pilih saat transaksi ]** | `amount` | `Akun Beban` |
| Kredit | **[ Pilih saat transaksi ]** | `amount > 2000000 ? amount * 0.98 : amount` | `Bank/Kas` |
| Kredit | Hutang PPh 23 | `amount > 2000000 ? amount * 0.02 : 0` | - |

**Format Formula Kondisional**:
```
kondisi ? nilai_jika_benar : nilai_jika_salah
```

**Contoh**:
- Jika amount = 3.000.000 → PPh = 60.000 (2%)
- Jika amount = 1.500.000 → PPh = 0

## Skenario 6: Akun Dinamis vs Akun Tetap

**Situasi**: Anda perlu memahami kapan menggunakan akun tetap dan kapan menggunakan akun dinamis.

### Akun Tetap

**Kapan Digunakan**:
- Akun yang selalu sama untuk setiap transaksi
- Contoh: PPN Masukan, Hutang PPh 23, Pendapatan Jasa Konsultasi

**Cara Setting**:
- Pilih akun dari dropdown
- Kode akun akan ditampilkan di preview

**Tampilan**:
- Detail template menampilkan kode dan nama akun
- Tidak ada indikator khusus

### Akun Dinamis (?)

**Kapan Digunakan**:
- Akun yang bervariasi tergantung transaksi
- Contoh: Bank yang digunakan, Jenis beban, Akun kas

**Cara Setting**:
1. Pilih **[ Pilih saat transaksi ]** dari dropdown akun
2. Opsional: Isi field **Hint** yang muncul
   - Contoh hint: "Bank/Kas", "Akun Beban", "Akun Aset"
3. Indikator **?** biru akan muncul di sebelah baris

**Keuntungan**:
- **Fleksibel** - Satu template untuk berbagai akun
- **Efisien** - Tidak perlu buat template terpisah untuk setiap bank
- **User-friendly** - Hint membantu user memilih akun yang tepat

**Penggunaan Saat Transaksi**:
- Dropdown akun akan muncul saat buat transaksi
- User memilih akun spesifik yang digunakan
- Preview jurnal langsung update sesuai pilihan

**Contoh Kasus**:

| Template | Akun Tetap | Akun Dinamis |
|----------|-----------|--------------|
| Beban Listrik | Beban Listrik | Bank yang digunakan |
| Pendapatan Jasa | Pendapatan Jasa | Bank penerima |
| Pembelian + PPN | PPN Masukan | Akun beban, Bank pembayaran |

## Skenario 7: Menggunakan Template dengan Akun Dinamis

**Situasi**: Anda membuat transaksi menggunakan template dengan akun dinamis.

**Langkah-langkah**:

1. Buka menu **Transaksi** → **Transaksi Baru**
2. Pilih template yang memiliki akun dinamis (?)
3. Isi form transaksi:
   - **Tanggal**: Pilih tanggal
   - **Jumlah**: Masukkan nilai
   - **Keterangan**: Isi keterangan
4. **Pilih Akun Dinamis** - Dropdown akan muncul untuk setiap akun dinamis:
   - Dropdown menampilkan hint sebagai placeholder
   - Contoh: "Bank/Kas" → Pilih "Bank BCA" atau "Kas Kecil"
5. Klik **Preview** untuk lihat jurnal yang akan dibuat
6. Klik **Simpan & Posting**

**Contoh Visual**:

```
Template: Beban Parkir
══════════════════════════════════════

Informasi Transaksi
────────────────────
Tanggal: 01 Dec 2025
Jumlah:  Rp 50.000
Keterangan: Parkir Meeting Client

Pilih Akun
────────────────────
□ Akun Sumber (Bank/Kas)
  ├─ Kas Kecil ✓
  ├─ Bank BCA
  └─ Bank Mandiri

Preview Jurnal
────────────────────
Beban Transportasi (Dr)  Rp 50.000
Kas Kecil (Cr)           Rp 50.000
                         ─────────
Total                    Rp 50.000 ✓
```

## Skenario 8: Test Formula dan Preview Template

**Situasi**: Anda ingin memastikan formula sudah benar sebelum menyimpan template.

**Langkah-langkah**:

1. Di form template, setelah memasukkan formula
2. Masukkan nilai preview di kolom **Preview dengan**: `10000000`
3. Kolom **Preview** di setiap baris akan menampilkan hasil perhitungan
4. Klik tombol **Preview** untuk melihat modal lengkap:

```
Input: Rp 10.000.000

Hasil Perhitungan:
─────────────────────────────────
? - Akun Beban (Dr)          Rp 10.000.000
? - Bank/Kas (Cr)            Rp  9.800.000
Hutang PPh 23 (Cr)           Rp    200.000
─────────────────────────────────
Total Debit                  Rp 10.000.000
Total Kredit                 Rp 10.000.000 ✓
Jurnal seimbang
```

**Catatan**: 
- Akun dinamis ditampilkan dengan **?** dan hint-nya
- Preview membantu validasi formula sebelum simpan
- Total harus seimbang (debit = kredit)

5. Jika sudah benar, klik **Simpan Template**

## Skenario 9: Duplikat Template yang Ada

**Situasi**: Anda ingin membuat variasi dari template yang sudah ada.

**Langkah-langkah**:

1. Klik menu **Template** di sidebar
2. Cari template yang ingin diduplikat
3. Klik template untuk buka detail
4. Klik tombol **Duplikat**
5. Ubah nama: `Beban Jasa Konsultan (PPh 23)` → `Beban Jasa Desain (PPh 23)`
6. Ubah akun beban jika perlu
7. Klik **Simpan Template**

## Skenario 10: Edit Template yang Ada

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

## Skenario 11: Nonaktifkan Template

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

## FAQ

### Berapa banyak akun dinamis yang bisa ada dalam satu template?

Tidak ada batasan. Anda bisa membuat template dengan semua akun dinamis, semua akun tetap, atau kombinasi keduanya.

### Apakah akun dinamis mempengaruhi performa?

Tidak. Akun dinamis hanya mempengaruhi tampilan form transaksi, tidak mempengaruhi performa sistem.

### Bisa tidak mengubah akun tetap menjadi dinamis di template yang sudah ada?

Bisa. Edit template, ubah akun dari pilihan spesifik menjadi **[ Pilih saat transaksi ]**, dan tambahkan hint jika perlu.

### Apakah transaksi lama terpengaruh jika template diubah?

Tidak. Perubahan template hanya berlaku untuk transaksi baru. Transaksi yang sudah dibuat tetap menggunakan konfigurasi template saat transaksi dibuat.

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
