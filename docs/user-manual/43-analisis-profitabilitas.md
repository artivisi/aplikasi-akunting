# Analisis Profitabilitas

## Kapan Anda Membutuhkan Ini

Gunakan panduan ini ketika Anda:
- Ingin mengetahui proyek mana yang paling menguntungkan
- Perlu menganalisis profit margin per klien
- Ingin mengevaluasi performa finansial proyek
- Perlu data untuk pricing proyek serupa di masa depan

## Konsep yang Perlu Dipahami

### Apa Itu Profitabilitas Proyek?

Profitabilitas proyek mengukur seberapa besar laba yang dihasilkan dari proyek dibandingkan dengan pendapatannya.

```
Profit = Pendapatan - Biaya
Profit Margin = (Profit / Pendapatan) × 100%
```

**Contoh**:
```
Pendapatan Proyek:  Rp 50.000.000
Biaya Proyek:       Rp 32.000.000
─────────────────────────────────
Profit:             Rp 18.000.000
Profit Margin:      36%
```

### Metrik Profitabilitas

| Metrik | Formula | Arti |
|--------|---------|------|
| **Gross Profit** | Pendapatan - Biaya Langsung | Laba kotor proyek |
| **Profit Margin** | Profit / Pendapatan × 100% | Efisiensi menghasilkan laba |
| **ROI** | Profit / Biaya × 100% | Return atas investasi biaya |

## Skenario 1: Lihat Profitabilitas Satu Proyek

**Situasi**: Anda ingin menganalisis profit dari proyek yang sudah selesai.

**Langkah-langkah**:

1. Klik menu **Proyek** di sidebar
2. Klik proyek yang ingin dianalisis
3. Scroll ke bagian **Ringkasan Finansial**:

```
RINGKASAN FINANSIAL
Proyek: PRJ-2025-001 - Website E-commerce PT ABC

PENDAPATAN
─────────────────────────────────────────
Nilai Kontrak              Rp 50.000.000
Pendapatan Diterima        Rp 50.000.000
Pendapatan Lainnya         Rp          0
─────────────────────────────────────────
Total Pendapatan           Rp 50.000.000

BIAYA
─────────────────────────────────────────
Biaya Freelancer           Rp 15.000.000
Biaya Hosting & Domain     Rp  2.000.000
Biaya Tools & Software     Rp  3.000.000
Biaya Lain-lain            Rp  1.500.000
─────────────────────────────────────────
Total Biaya                Rp 21.500.000

PROFITABILITAS
─────────────────────────────────────────
Gross Profit               Rp 28.500.000
Profit Margin              57%
ROI                        133%

Budget Awal                Rp 35.000.000
Variance                   Rp 13.500.000 (hemat)
```

## Skenario 2: Bandingkan Profitabilitas Beberapa Proyek

**Situasi**: Anda ingin membandingkan performa finansial beberapa proyek.

**Langkah-langkah**:

1. Klik menu **Laporan** di sidebar
2. Pilih **Laporan Profitabilitas**
3. Pilih filter:
   - **Periode**: Tahun 2025
   - **Status**: Completed (atau All)
4. Klik **Tampilkan**

**Hasil Laporan**:

```
LAPORAN PROFITABILITAS PROYEK
Periode: 2025

Kode        Nama                     Pendapatan      Biaya         Profit      Margin
─────────────────────────────────────────────────────────────────────────────────────
PRJ-001     Website E-commerce       50.000.000     21.500.000    28.500.000    57%
PRJ-002     Mobile App ABC           80.000.000     55.000.000    25.000.000    31%
PRJ-003     Sistem Inventory         35.000.000     20.000.000    15.000.000    43%
PRJ-004     Website Company Profile  15.000.000      8.000.000     7.000.000    47%
─────────────────────────────────────────────────────────────────────────────────────
TOTAL                               180.000.000    104.500.000    75.500.000    42%
```

**Insight dari Data**:
- PRJ-001 paling profitable (margin 57%)
- PRJ-002 revenue tertinggi tapi margin terendah (31%)
- Average margin: 42%

## Skenario 3: Analisis Profitabilitas per Klien

**Situasi**: Anda ingin mengetahui klien mana yang paling menguntungkan.

**Langkah-langkah**:

1. Klik menu **Laporan** di sidebar
2. Pilih **Profitabilitas per Klien**
3. Pilih periode
4. Klik **Tampilkan**

**Hasil Laporan**:

```
PROFITABILITAS PER KLIEN
Periode: 2025

Klien           Jumlah      Pendapatan      Biaya         Profit      Margin
                Proyek
────────────────────────────────────────────────────────────────────────────
PT ABC            3         145.000.000    81.500.000    63.500.000    44%
CV Maju Jaya      2          45.000.000    28.000.000    17.000.000    38%
PT XYZ            1          25.000.000    18.000.000     7.000.000    28%
────────────────────────────────────────────────────────────────────────────
TOTAL             6         215.000.000   127.500.000    87.500.000    41%
```

**Insight**:
- PT ABC klien paling profitable (44% margin, 3 proyek)
- PT XYZ margin rendah (28%) - perlu evaluasi

## Skenario 4: Identifikasi Proyek Merugi

**Situasi**: Anda ingin menemukan proyek dengan margin di bawah target (misalnya 30%).

**Langkah-langkah**:

1. Buka **Laporan Profitabilitas**
2. Urutkan berdasarkan **Margin** (ascending)
3. Identifikasi proyek dengan margin < 30%
4. Untuk setiap proyek bermasalah:
   - Buka detail proyek
   - Analisis breakdown biaya
   - Identifikasi penyebab margin rendah

**Penyebab Umum Margin Rendah**:
| Penyebab | Indikator | Solusi |
|----------|-----------|--------|
| Scope creep | Biaya > budget | Kelola change request dengan ketat |
| Underpricing | Nilai kontrak terlalu rendah | Revisi pricing untuk proyek serupa |
| Inefficiency | Biaya tenaga kerja tinggi | Improve process |
| Unexpected cost | Banyak biaya tidak terduga | Tambah contingency di pricing |

## Skenario 5: Proyeksi Profitabilitas Proyek Berjalan

**Situasi**: Anda ingin memprediksi profit proyek yang sedang berjalan.

**Langkah-langkah**:

1. Buka detail proyek yang sedang berjalan
2. Lihat bagian **Proyeksi Profitabilitas**:

```
PROYEKSI PROFITABILITAS
Proyek: PRJ-2025-005 (In Progress - 60%)

Pendapatan
─────────────────────────────────────────
Nilai Kontrak              Rp 100.000.000
Sudah Diterima             Rp  60.000.000

Biaya
─────────────────────────────────────────
Budget                     Rp  70.000.000
Biaya Aktual (s.d. saat ini) Rp  48.000.000
% Budget Terpakai          69%
Progress                   60%

Proyeksi
─────────────────────────────────────────
Proyeksi Total Biaya       Rp  80.000.000 ⚠️
Proyeksi Profit            Rp  20.000.000
Proyeksi Margin            20%

⚠️ WARNING: Proyeksi biaya melebihi budget
Rekomendasi: Review dan optimasi biaya sisa proyek
```

## Skenario 6: Export Data untuk Analisis Lanjutan

**Situasi**: Anda perlu data mentah untuk analisis di Excel atau presentasi.

**Langkah-langkah**:

1. Buka **Laporan Profitabilitas** yang diinginkan
2. Klik tombol **Ekspor Excel**
3. File Excel akan terunduh dengan data:
   - Summary per proyek
   - Detail transaksi
   - Breakdown biaya

**Analisis yang Bisa Dilakukan di Excel**:
- Trend profit margin per bulan
- Pareto analysis (proyek/klien yang berkontribusi 80% profit)
- Korelasi nilai kontrak dengan margin

## Skenario 7: Tentukan Pricing Proyek Baru

**Situasi**: Anda mau menentukan harga untuk proyek serupa berdasarkan data historis.

**Langkah-langkah**:

1. Buka **Laporan Profitabilitas**
2. Filter proyek dengan tipe serupa
3. Analisis:
   - Average cost untuk proyek sejenis
   - Target margin yang diinginkan (misal 40%)
4. Hitung harga:
   ```
   Estimasi Biaya: Rp 25.000.000
   Target Margin: 40%

   Harga = Biaya / (1 - Margin)
   Harga = 25.000.000 / 0.6
   Harga = Rp 41.667.000 (bulatkan ke Rp 42.000.000)
   ```

## Tips

1. **Review berkala** - Analisis profitabilitas minimal setiap quarter
2. **Benchmark** - Tetapkan target margin minimum (misalnya 30%)
3. **Lessons learned** - Dokumentasi insight dari proyek yang berhasil/gagal
4. **Pricing database** - Bangun database harga berdasarkan data historis

## Troubleshooting

| Masalah | Solusi |
|---------|--------|
| Biaya tidak muncul | Pastikan transaksi sudah dihubungkan ke proyek |
| Pendapatan tidak lengkap | Cek apakah semua pembayaran sudah dicatat |
| Data tidak konsisten | Pastikan semua transaksi sudah diposting |

## Lihat Juga

- [Setup Proyek](40-setup-proyek.md) - Setting budget dan nilai kontrak
- [Tracking Proyek](41-tracking-proyek.md) - Catat biaya proyek
- [Laporan Bulanan](21-laporan-bulanan.md) - Laporan keuangan overall
