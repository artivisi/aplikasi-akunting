# Industri Manufaktur

Panduan lengkap untuk bisnis manufaktur sederhana (Home Industry, Bakery, Coffee Shop, Catering).

## Karakteristik Industri Manufaktur

### Ciri Khas

- **Proses Produksi** - Mengolah bahan baku menjadi barang jadi
- **Bill of Materials (BOM)** - Resep/formula produksi
- **Production Orders** - Perintah produksi dengan tracking cost
- **Multi-stage Inventory** - Bahan baku, WIP, dan barang jadi
- **COGM (Cost of Goods Manufactured)** - Harga pokok produksi
- **Component Consumption** - Konsumsi komponen saat produksi

### Alur Bisnis Tipikal

```
Supplier → Raw Materials → Production → Finished Goods → Customer
```

**Contoh Kasus: Kedai Kopi Nusantara**

Kedai kopi & pastry yang memproduksi:
- Roti & pastry (Croissant, Roti Bakar Coklat)
- Menggunakan bahan baku: Tepung, Butter, Telur, Coklat, dll
- Produksi batch sesuai BOM
- Jual hasil produksi ke konsumen

---

## Manajemen Produk & Kategori

### Kategori Produk

Buka menu **Inventori** > **Kategori Produk**.

![Daftar Kategori Produk](screenshots/coffee/product-category-list.png)

**Kategori Tipikal Manufaktur:**

| Kategori | Jenis | Contoh |
|----------|-------|--------|
| Bahan Baku Kopi | Raw Material | Biji Kopi, Susu, Gula Aren |
| Bahan Baku Roti | Raw Material | Tepung, Butter, Telur |
| Produk Jadi - Roti | Finished Goods | Croissant, Roti Bakar |

### Daftar Produk

Buka menu **Inventori** > **Produk**.

![Daftar Produk](screenshots/coffee/product-list.png)

Produk dibagi menjadi:
1. **Bahan Baku** - Input untuk produksi (Tepung, Butter, Telur, Coklat)
2. **Barang Jadi** - Output produksi (Croissant, Roti Bakar Coklat)

### Menambah Produk Baru

1. Klik **Produk Baru**

2. Isi data produk:
   - **Kode:** SKU unik (contoh: TEPUNG-TERIGU)
   - **Nama:** Nama produk
   - **Kategori:** Pilih kategori (Bahan Baku / Produk Jadi)
   - **Satuan:** pcs, kg, liter, gram
   - **Metode Costing:**
     - FIFO (First In First Out)
     - WEIGHTED_AVERAGE (Rata-rata Tertimbang)
   - **Track Inventory:** ✓ (aktifkan untuk tracking stok)
   - **Minimum Stock:** Alert stok rendah
   - **Harga Jual:** Untuk barang jadi
   - **Akun:**
     - Inventory Account (1.1.20 - Persediaan Bahan Baku / 1.1.21 - Persediaan Barang Jadi)
     - COGS Account (5.1.01 - HPP Bahan Baku / 5.2.01 - HPP Barang Jadi)
     - Sales Account (4.1.02 - Pendapatan Penjualan)

3. Klik **Simpan**

**Perbedaan Bahan Baku vs Barang Jadi:**

| Aspek | Bahan Baku | Barang Jadi |
|-------|------------|-------------|
| Inventory Account | 1.1.20 Persediaan Bahan Baku | 1.1.21 Persediaan Barang Jadi |
| COGS Account | 5.1.01 HPP Bahan Baku | 5.2.01 HPP Barang Jadi |
| Harga Jual | 0 (tidak dijual langsung) | Sesuai harga pasar |
| Digunakan dalam | BOM sebagai komponen | BOM sebagai output |

---

## Bill of Materials (BOM)

### Konsep BOM

BOM adalah **resep/formula produksi** yang mendefinisikan:
- Barang jadi yang dihasilkan
- Komponen (bahan baku) yang dibutuhkan
- Kuantitas setiap komponen
- Output quantity per batch

**Contoh: BOM Croissant**

Output: 24 pcs Croissant

Komponen:
- Tepung Terigu: 3.00 kg
- Butter: 1.20 kg
- Telur: 24.00 butir
- Ragi: 50.00 gram
- Garam: 30.00 gram

### Melihat Daftar BOM

Buka menu **Inventori** > **Bill of Materials**.

![Daftar BOM](screenshots/coffee/bom-list.png)

### Detail BOM

Klik pada BOM untuk melihat detail:

![Detail BOM Croissant](screenshots/coffee/bom-detail-croissant.png)

Informasi yang ditampilkan:
- **Kode BOM:** BOM-CRS
- **Produk Output:** Croissant
- **Output Quantity:** 24 pcs per batch
- **Daftar Komponen:** Tabel dengan nama komponen, quantity, dan satuan

### Menambah BOM Baru

1. Klik **BOM Baru**

2. **Tab Informasi Dasar:**
   - Kode BOM (unik)
   - Nama BOM (deskriptif)
   - Produk Jadi (pilih dari dropdown)
   - Output Quantity (berapa unit yang dihasilkan per batch)
   - Status (Aktif/Nonaktif)

3. **Tab Komponen:**
   - Klik **Tambah Komponen**
   - Pilih produk (bahan baku)
   - Isi quantity yang dibutuhkan
   - Ulangi untuk semua komponen

4. Klik **Simpan**

**Contoh: BOM Roti Bakar Coklat**

Output: 20 pcs

Komponen:
- Tepung Terigu: 2.50 kg
- Butter: 1.00 kg
- Telur: 20.00 butir
- Ragi: 40.00 gram
- Garam: 25.00 gram
- Coklat Blok: 0.80 kg

### Detail BOM Croissant

![Detail BOM Croissant](screenshots/coffee/production-bom-crs.png)

### Detail BOM Roti Bakar Coklat

![Detail BOM Roti Bakar Coklat](screenshots/coffee/production-bom-rbc.png)

---

## Production Order

### Konsep Production Order

Production Order adalah **perintah produksi** untuk membuat barang jadi sesuai BOM.

**Workflow:**
```
DRAFT → IN_PROGRESS → COMPLETED
```

| Status | Arti | Aksi |
|--------|------|------|
| DRAFT | Order belum dimulai | Edit, Start, Delete |
| IN_PROGRESS | Produksi sedang berjalan | Complete, Cancel |
| COMPLETED | Produksi selesai | View only (sudah ada transaksi inventory) |

### Melihat Daftar Production Order

Buka menu **Inventori** > **Production Orders**.

![Daftar Production Order](screenshots/coffee/production-order-list.png)

### Detail Production Order

Klik pada order untuk melihat detail:

![Detail Production Order](screenshots/coffee/production-order-detail-croissant.png)

Informasi yang ditampilkan:
- **No. Order:** PROD-001
- **Tanggal Order:** Kapan order dibuat
- **BOM:** BOM yang digunakan
- **Produk Jadi:** Croissant
- **Jumlah Produksi:** 24 pcs
- **Status:** COMPLETED
- **Ringkasan Biaya:** (tampil setelah COMPLETED)
  - Total Biaya Komponen: Rp 106.920
  - Jumlah Produksi: 24 pcs
  - Harga Pokok per Unit: Rp 4.455

**Tabel Komponen yang Dibutuhkan:**
- Menampilkan semua komponen dari BOM
- Kebutuhan per BOM
- Total kebutuhan (dikalikan quantity order)

### Membuat Production Order Baru

1. Klik **Production Order Baru**

2. Isi data:
   - **No. Order:** Otomatis (PROD-XXX)
   - **Tanggal Order:** Tanggal pembuatan order
   - **BOM:** Pilih BOM yang akan diproduksi
   - **Quantity:** Berapa batch yang akan diproduksi
   - **Target Selesai:** (opsional) Target completion date
   - **Catatan:** (opsional) Informasi tambahan

3. Klik **Simpan** → Status: DRAFT

### Workflow Produksi

#### 1. Start Production

Dari halaman detail order (status DRAFT):

1. Klik **Mulai Produksi**
2. Konfirmasi: "Pastikan semua komponen tersedia"
3. Status berubah: DRAFT → IN_PROGRESS

**Tidak ada transaksi inventory pada tahap ini.**

#### 2. Complete Production

Dari halaman detail order (status IN_PROGRESS):

1. Klik **Selesaikan Produksi**
2. Konfirmasi: "Komponen akan dikurangi dari stok dan produk jadi akan ditambahkan"
3. Status berubah: IN_PROGRESS → COMPLETED

**Transaksi Inventory Otomatis:**

a. **PRODUCTION_OUT** - Konsumsi komponen:
```
- Tepung Terigu: -3.00 kg @ Rp 12.000 = -Rp 36.000
- Butter: -1.20 kg @ Rp 45.000 = -Rp 54.000
- Telur: -24 butir @ Rp 1.500 = -Rp 36.000
- Ragi: -50 gram @ Rp 30 = -Rp 1.500
- Garam: -30 gram @ Rp 14 = -Rp 420
Total: Rp 127.920
```

b. **PRODUCTION_IN** - Penerimaan barang jadi:
```
+ Croissant: 24 pcs @ Rp 4.455 = Rp 106.920
```

**Unit Cost Calculation:**
```
Unit Cost = Total Component Cost / Output Quantity
Unit Cost = Rp 106.920 / 24 pcs = Rp 4.455/pcs
```

**Jurnal Otomatis:**
```
Dr. 1.1.21 Persediaan Barang Jadi - Roti    Rp 106.920
    Cr. 1.1.20 Persediaan Bahan Baku - Roti    Rp 106.920
```

---

## Transaksi Inventory

### Daftar Transaksi

Buka menu **Inventori** > **Transaksi**.

![Daftar Transaksi Inventory](screenshots/coffee/inventory-transactions-list.png)

**Tipe Transaksi:**

| Tipe | Arti | Direction | Digunakan Untuk |
|------|------|-----------|-----------------|
| PURCHASE | Pembelian | IN (+) | Beli bahan baku dari supplier |
| SALE | Penjualan | OUT (-) | Jual barang jadi ke customer |
| PRODUCTION_IN | Produksi Masuk | IN (+) | Terima barang jadi dari produksi |
| PRODUCTION_OUT | Produksi Keluar | OUT (-) | Konsumsi komponen untuk produksi |
| ADJUSTMENT_IN | Penyesuaian Masuk | IN (+) | Koreksi stok (selisih fisik) |
| ADJUSTMENT_OUT | Penyesuaian Keluar | OUT (-) | Koreksi stok (kerusakan, expired) |

### Filter Transaksi

Gunakan filter untuk mempersempit pencarian:
- **Produk:** Pilih produk tertentu
- **Tipe Transaksi:** Pilih tipe (PURCHASE, SALE, PRODUCTION_IN, dll)
- **Periode:** Tanggal mulai - tanggal akhir

### Pembelian Bahan Baku

**Contoh: Pembelian Tepung Terigu**

Menu **Inventori** > **Transaksi** > **Pembelian Baru**

Data:
- **Tanggal:** 1 Januari 2024
- **Supplier:** Bogasari
- **Produk:** Tepung Terigu (TEPUNG-TERIGU)
- **Quantity:** 50 kg
- **Harga Beli:** Rp 12.000/kg
- **Total:** Rp 600.000
- **Referensi:** PO-001

**Jurnal Otomatis:**
```
Dr. 1.1.20 Persediaan Bahan Baku - Roti  Rp 600.000
    Cr. 1.1.01 Bank BCA                      Rp 600.000
```

**Impact:**
- Stok Tepung Terigu: 0 → 50 kg
- Nilai persediaan: +Rp 600.000
- Kas: -Rp 600.000

### Penjualan Barang Jadi

**Contoh: Penjualan Croissant**

Menu **Inventori** > **Transaksi** > **Penjualan Baru**

Data:
- **Tanggal:** 10 Januari 2024
- **Customer:** Walk-in customer
- **Produk:** Croissant (CROISSANT)
- **Quantity:** 15 pcs
- **Harga Jual:** Rp 25.000/pcs
- **Total:** Rp 375.000
- **Referensi:** INV-001

**Sistem hitung HPP otomatis (FIFO/Weighted Average):**
- 15 pcs @ Rp 4.455 = Rp 66.825

**Jurnal Otomatis:**
```
Dr. 1.1.01 Bank BCA                          Rp 375.000
    Cr. 4.1.02 Pendapatan Penjualan Roti         Rp 375.000

Dr. 5.2.01 HPP Barang Jadi - Roti            Rp 66.825
    Cr. 1.1.21 Persediaan Barang Jadi - Roti    Rp 66.825
```

**Margin Analysis:**
- Revenue: Rp 375.000
- COGS: Rp 66.825
- Gross Profit: Rp 308.175
- Margin: 82.18%

---

## Stok Barang

### Melihat Saldo Stok

Buka menu **Inventori** > **Stok Barang**.

![Daftar Stok](screenshots/coffee/inventory-stock-list.png)

Informasi yang ditampilkan:
- **Kode Produk:** SKU
- **Nama Produk:** Nama produk
- **Kategori:** Kategori produk
- **Stok:** Quantity tersedia
- **Biaya Rata-rata:** Average cost per unit
- **Total Nilai:** Quantity × Average Cost
- **Status:** Normal / Stok Rendah (jika di bawah minimum)

**Contoh Stok Setelah Produksi & Penjualan:**

| Produk | Stok | Avg Cost | Total Nilai |
|--------|------|----------|-------------|
| Croissant | 9 pcs | Rp 4.455 | Rp 40.095 |
| Roti Bakar Coklat | 8 pcs | Rp 5.123 | Rp 40.984 |
| Tepung Terigu | 44.50 kg | Rp 12.000 | Rp 534.000 |
| Butter | 46.60 kg | Rp 45.000 | Rp 2.097.000 |

### Detail Stok per Produk

Klik pada produk untuk melihat:
- Informasi produk
- Saldo stok saat ini
- History transaksi
- FIFO layers (jika menggunakan FIFO)

---

## Laporan Produksi & Inventory

### Laporan Saldo Stok

Menu **Inventori** > **Laporan** > **Saldo Stok**

![Laporan Saldo Stok](screenshots/coffee/report-stock-balance.png)

Menampilkan snapshot stok semua produk pada tanggal tertentu.

Kolom:
- Produk
- Kategori
- Quantity
- Unit Cost
- Total Value

Export: PDF / Excel

### Laporan Penilaian Persediaan

Menu **Inventori** > **Laporan** > **Penilaian Persediaan**

![Laporan Penilaian Persediaan](screenshots/coffee/report-inventory-valuation.png)

Menampilkan valuasi persediaan:
- **FIFO:** Breakdown per layer dengan tanggal, quantity, unit cost
- **Weighted Average:** Breakdown per transaksi dengan running average

Filter:
- Periode tanggal
- Kategori produk
- Metode costing

### Laporan Profitabilitas Produk

Menu **Inventori** > **Laporan** > **Profitabilitas Produk**

![Laporan Profitabilitas Produk](screenshots/coffee/report-product-profitability.png)

Analisis margin per produk:

| Produk | Units Sold | Revenue | COGS | Gross Profit | Margin % |
|--------|------------|---------|------|--------------|----------|
| Croissant | 15 pcs | Rp 375.000 | Rp 66.825 | Rp 308.175 | 82.18% |
| Roti Bakar Coklat | 12 pcs | Rp 240.000 | Rp 61.476 | Rp 178.524 | 74.39% |

**Insight:**
- Produk mana yang paling profitable
- Evaluasi harga jual vs cost
- Keputusan produk mix

---

## Contoh Skenario Lengkap: Produksi Croissant

### Langkah 1: Pembelian Bahan Baku

Beli semua komponen yang dibutuhkan:

1. Tepung Terigu: 50 kg @ Rp 12.000 = Rp 600.000
2. Butter: 50 kg @ Rp 45.000 = Rp 2.250.000
3. Telur: 100 butir @ Rp 1.500 = Rp 150.000
4. Ragi: 500 gram @ Rp 30 = Rp 15.000
5. Garam: 1000 gram @ Rp 14 = Rp 14.000

**Total Investasi:** Rp 3.029.000

### Langkah 2: Buat BOM

BOM: BOM-CRS - Croissant

Output: 24 pcs

Komponen:
- Tepung: 3.00 kg
- Butter: 1.20 kg
- Telur: 24 butir
- Ragi: 50 gram
- Garam: 30 gram

### Langkah 3: Buat Production Order

Order: PROD-001

- BOM: BOM-CRS
- Quantity: 24 pcs (1 batch)
- Status: DRAFT

### Langkah 4: Mulai Produksi

Klik **Mulai Produksi** → Status: IN_PROGRESS

(Tidak ada transaksi inventory pada tahap ini)

### Langkah 5: Selesaikan Produksi

Klik **Selesaikan Produksi** → Status: COMPLETED

**Transaksi Otomatis:**

PRODUCTION_OUT (konsumsi):
- Tepung: -3.00 kg
- Butter: -1.20 kg
- Telur: -24 butir
- Ragi: -50 gram
- Garam: -30 gram

PRODUCTION_IN (hasil):
- Croissant: +24 pcs @ Rp 4.455

**Biaya Produksi:**
- Total Component Cost: Rp 106.920
- Unit Cost: Rp 4.455/pcs

### Langkah 6: Jual Barang Jadi

Jual 15 pcs Croissant @ Rp 25.000 = Rp 375.000

**Auto-COGS:**
- HPP: 15 × Rp 4.455 = Rp 66.825
- Gross Profit: Rp 308.175
- Margin: 82.18%

**Sisa Stok:**
- Croissant: 24 - 15 = 9 pcs @ Rp 4.455

---

## Best Practices Manufaktur

### 1. Setup BOM yang Akurat

- **Ukur komponen dengan presisi** - Pastikan quantity di BOM sesuai dengan realita produksi
- **Update BOM jika ada perubahan resep** - Jangan biarkan BOM kadaluarsa
- **Dokumentasi** - Tambahkan catatan/deskripsi untuk BOM yang kompleks

### 2. Production Order Workflow

- **Pastikan stok cukup sebelum start production** - Cek stok komponen terlebih dahulu
- **Complete production tepat waktu** - Jangan biarkan order IN_PROGRESS terlalu lama
- **Track tanggal completion** - Untuk analisis lead time produksi

### 3. Inventory Management

- **Pembelian bahan baku** - Beli dalam batch optimal (tidak terlalu banyak/sedikit)
- **Monitor stok minimum** - Set minimum stock untuk alert stok rendah
- **Stock opname berkala** - Lakukan adjustment untuk koreksi selisih fisik vs sistem
- **FIFO untuk barang yang mudah rusak** - Susu, telur, produk segar
- **Weighted Average untuk barang tahan lama** - Tepung, gula, garam

### 4. Costing & Profitability

- **Review unit cost secara berkala** - Cek apakah harga beli komponen naik
- **Evaluasi margin** - Pastikan margin cukup untuk cover overhead
- **Analisis produk profitability** - Fokus pada produk dengan margin tinggi
- **Adjust harga jual jika perlu** - Jika cost naik, pertimbangkan naikkan harga

### 5. Laporan & Monitoring

- **Cek laporan stok** - Weekly/monthly untuk monitor inventory value
- **Laporan profitabilitas** - Monthly untuk review product mix
- **Track production volume** - Berapa batch per bulan
- **Monitor waste/scrap** - Jika ada komponen terbuang, catat sebagai adjustment

---

## Troubleshooting

### Stok Komponen Tidak Cukup

**Problem:** Saat complete production, muncul error "Insufficient stock"

**Solusi:**
1. Cek stok komponen di **Inventori > Stok Barang**
2. Beli komponen yang kurang via **Transaksi > Pembelian**
3. Atau kurangi quantity production order

### Unit Cost Tidak Sesuai Ekspektasi

**Problem:** Unit cost terlalu tinggi/rendah

**Penyebab:**
- Harga beli komponen berubah
- Ada waste/scrap yang tidak tercatat
- BOM quantity tidak akurat

**Solusi:**
1. Review harga beli komponen di transaksi pembelian
2. Update BOM jika ada perubahan resep
3. Catat waste sebagai adjustment (PRODUCTION_OUT tambahan)

### FIFO vs Weighted Average

**Kapan pakai FIFO:**
- Barang yang mudah rusak (susu, telur, produk segar)
- Harga komponen sering berubah
- Perlu tracking detail per batch pembelian

**Kapan pakai Weighted Average:**
- Barang tahan lama (tepung, gula, garam)
- Harga relatif stabil
- Lebih simple untuk inventory management

---

## Integrasi dengan Modul Lain

### Perpajakan (Modul 4)

- **PPN atas pembelian bahan baku** - Jika supplier PKP
- **PPN atas penjualan barang jadi** - Jika bisnis sudah PKP
- **PPh 23 atas jasa produksi** - Jika menggunakan jasa maklon

Lihat **04-perpajakan.md** untuk detail.

### Penggajian (Modul 5)

- **Gaji karyawan produksi** - Baker, chef, operator
- **Overhead cost** - Alokasi gaji ke production cost (advanced)

Lihat **05-penggajian.md** untuk detail.

### Aset Tetap (Modul 3)

- **Mesin produksi** - Oven, mixer, dll
- **Depresiasi** - Alokasi ke production overhead (advanced)

Lihat **03-aset-tetap.md** untuk detail.

---

## Referensi

### Akun COA untuk Manufaktur

| Kode | Nama Akun | Fungsi |
|------|-----------|--------|
| 1.1.20 | Persediaan Bahan Baku | Inventory bahan baku |
| 1.1.21 | Persediaan Barang Jadi | Inventory finished goods |
| 1.1.22 | Persediaan WIP | Work in Process (advanced) |
| 4.1.02 | Pendapatan Penjualan | Revenue dari penjualan |
| 5.1.01 | HPP Bahan Baku | COGS bahan baku |
| 5.2.01 | HPP Barang Jadi | COGS finished goods |
| 5.3.01 | Biaya Produksi | Direct labor & overhead |

Lihat **12-lampiran-akun.md** untuk daftar lengkap.

### Template Transaksi

| Template | Fungsi |
|----------|--------|
| Pembelian Bahan Baku | Purchase raw materials |
| Transfer Produksi | PRODUCTION_OUT & PRODUCTION_IN |
| Penjualan + COGS | Sale dengan auto-COGS |
| Stock Adjustment | Koreksi stok (kerusakan, selisih) |

Lihat **12-lampiran-template.md** untuk detail.

---

## Kesimpulan

Modul manufaktur aplikasi ini cocok untuk:
- ✅ Home industry (kue, roti, katering)
- ✅ Bakery & coffee shop
- ✅ Small-scale manufacturing
- ✅ Simple BOM-based production

Fitur utama:
- Bill of Materials (BOM) management
- Production Order workflow dengan cost tracking
- Auto-COGS calculation
- FIFO & Weighted Average costing
- Production & profitability reports

**Next Steps:**
1. Setup kategori produk (bahan baku vs barang jadi)
2. Input master produk
3. Buat BOM untuk setiap produk jadi
4. Beli bahan baku
5. Mulai produksi via production order
6. Jual barang jadi dengan auto-COGS

Untuk pertanyaan lebih lanjut, lihat modul terkait di manual ini atau hubungi support.
