# User Manual Creation Guideline

## Finalized Structure

```
1. Setup Awal & Administrasi
   - Tampilan aplikasi baru (bare bones, no seed)
   - Import seed data industri
   - Master data umum (COA, Klien, Supplier)
   - User management & roles
   - Telegram integration

2. Tutorial Dasar Akuntansi ⭐ (Crown Jewel)
   - Konsep dasar akuntansi
   - Siklus akuntansi
   - Transaksi harian (pendapatan, pengeluaran, transfer)
   - Jurnal dan buku besar
   - Penyesuaian (amortisasi, koreksi)
   - Tutup buku dan laporan keuangan

3. Aset Tetap
   - Konsep depresiasi
   - Kategori aset & masa manfaat (regulasi Indonesia)
   - Pencatatan pembelian aset
   - Jadwal depresiasi otomatis

4. Perpajakan
   - Jenis pajak di Indonesia (PPh 21/23/25/4(2), PPN)
   - Transaksi PPN (keluaran, masukan)
   - Transaksi PPh (pemotongan, penyetoran)
   - Periode fiskal & pelaporan
   - Referensi regulasi pajak

5. Penggajian
   - Setup komponen gaji
   - BPJS (Kesehatan, Ketenagakerjaan)
   - PPh 21 karyawan (refer to section 4)
   - Proses penggajian bulanan
   - Bukti potong 1721-A1
   - Layanan mandiri karyawan
   - Catatan tax deductibility

6. Pengantar Industri
   - Jenis industri di dunia
   - Industri yang didukung aplikasi
   - Perbedaan praktik akuntansi per industri

7. Industri Jasa (Service)
   - Karakteristik industri jasa
   - Client & Project Management
   - Template transaksi jasa
   - Invoice & penagihan berbasis milestone
   - Laporan profitabilitas proyek/klien
   - Komponen gaji, aset, pajak khas jasa

8. Industri Dagang (Trading/Seller)
   - Karakteristik industri dagang
   - Manajemen produk & kategori
   - Metode penilaian persediaan (FIFO, rata-rata)
   - Transaksi pembelian & penjualan
   - HPP dan margin
   - Laporan persediaan & profitabilitas produk
   - Komponen gaji, aset, pajak khas dagang

9. Industri Manufaktur (Manufacturing) [TBD]
   - Karakteristik manufaktur
   - Bill of Materials (BOM)
   - Production Order workflow
   - Konsumsi komponen & penerimaan barang jadi
   - Kalkulasi biaya produksi
   - Laporan produksi
   - Komponen gaji, aset, pajak khas manufaktur

10. Industri Pendidikan (Campus/Education) [TBD]
    - Karakteristik institusi pendidikan
    - Manajemen mahasiswa & tagihan SPP
    - Penerimaan pembayaran & cicilan
    - Beasiswa & potongan
    - Laporan piutang per mahasiswa
    - Laporan pendapatan per program studi

11. Keamanan & Kepatuhan Data
    - Enkripsi dokumen dan PII
    - Audit log keamanan
    - Kebijakan data (GDPR/UU PDP)
    - Ekspor data subjek (DSAR)

12. Lampiran
    - Glosarium
    - Referensi Template per Industri
    - Referensi Jadwal Amortisasi & Depresiasi
    - Referensi Akun per Industri
```

## Content Guidelines

### Section Structure

All sections will follow this structure:

1. **Concept explanation** - Universal, can be exercised manually (pen & paper) or Excel spreadsheet
2. **Step-by-step instruction** - Detailed with screenshots to execute the concept in the app
3. **Expected result** - What the user expects to see, with menu direction and screenshot

### Screenshot Requirements

- All screenshots must reflect the case study being explained (description and amount must correspond)
- Screenshots must come from functional tests, not standalone screenshot generator
- If a specific scenario is not covered by existing tests, add it as a functional test first
- Screenshot filename convention: `{section}-{subsection}-{description}.png`

### Language

- All content in Bahasa Indonesia
- Technical terms may use English with Indonesian explanation in parentheses
- Use consistent terminology throughout (refer to Glosarium)

## Implementation Status

| Section | Functional Tests | Screenshots | Content |
|---------|------------------|-------------|---------|
| 1. Setup Awal | ✅ Service, Seller | ⏳ | ⏳ |
| 2. Tutorial Akuntansi | ✅ Service | ⏳ | ⏳ |
| 3. Aset Tetap | ✅ V913 data | ⏳ | ⏳ |
| 4. Perpajakan | ✅ Service | ⏳ | ⏳ |
| 5. Penggajian | ✅ Service | ⏳ | ⏳ |
| 6. Pengantar Industri | N/A | N/A | ⏳ |
| 7. Industri Jasa | ✅ ServiceAccountingTest, etc. | ⏳ | ⏳ |
| 8. Industri Dagang | ✅ SellerInventoryTest, etc. | ⏳ | ⏳ |
| 9. Industri Manufaktur | ❌ TBD (Phase 4) | ❌ | ❌ |
| 10. Industri Pendidikan | ❌ TBD (Phase 5) | ❌ | ❌ |
| 11. Keamanan | ✅ SecurityRegressionTest | ⏳ | ⏳ |
| 12. Lampiran | N/A | N/A | ⏳ |

## File Naming Convention

Markdown files:
```
docs/user-manual/
├── 01-setup-awal.md
├── 02-tutorial-akuntansi.md
├── 03-aset-tetap.md
├── 04-perpajakan.md
├── 05-penggajian.md
├── 06-pengantar-industri.md
├── 07-industri-jasa.md
├── 08-industri-dagang.md
├── 09-industri-manufaktur.md  [TBD]
├── 10-industri-pendidikan.md  [TBD]
├── 11-keamanan-kepatuhan.md
├── 12-lampiran-glosarium.md
├── 12-lampiran-template.md
├── 12-lampiran-amortisasi.md
└── 12-lampiran-akun.md
```

## Migration from Current Structure

Current 43 files (00-92) will be consolidated into the new structure:

| Old File | New Location |
|----------|--------------|
| 00-pendahuluan.md | 01-setup-awal.md |
| 01-konsep-dasar.md | 02-tutorial-akuntansi.md |
| 10-mencatat-pendapatan.md | 02-tutorial-akuntansi.md |
| 11-mencatat-pengeluaran.md | 02-tutorial-akuntansi.md |
| 12-transfer-antar-akun.md | 02-tutorial-akuntansi.md |
| 13-telegram-receipt.md | 01-setup-awal.md (Telegram section) |
| 20-laporan-harian.md | 02-tutorial-akuntansi.md |
| 21-laporan-bulanan.md | 02-tutorial-akuntansi.md |
| 22-laporan-tahunan.md | 02-tutorial-akuntansi.md |
| 23-laporan-penyusutan.md | 03-aset-tetap.md |
| 24-penutupan-tahun-buku.md | 02-tutorial-akuntansi.md |
| 30-transaksi-ppn.md | 04-perpajakan.md |
| 31-transaksi-pph.md | 04-perpajakan.md |
| 32-laporan-pajak.md | 04-perpajakan.md |
| 33-kalender-pajak.md | 04-perpajakan.md |
| 40-setup-proyek.md | 07-industri-jasa.md |
| 41-tracking-proyek.md | 07-industri-jasa.md |
| 42-invoice-penagihan.md | 07-industri-jasa.md |
| 43-analisis-profitabilitas.md | 07-industri-jasa.md |
| 50-setup-awal.md | 01-setup-awal.md |
| 51-kelola-template.md | 02-tutorial-akuntansi.md or 12-lampiran |
| 52-kelola-klien.md | 07-industri-jasa.md |
| 53-jadwal-amortisasi.md | 03-aset-tetap.md |
| 54-kelola-periode-fiskal.md | 04-perpajakan.md |
| 55-setup-telegram.md | 01-setup-awal.md |
| 60-kelola-karyawan.md | 05-penggajian.md |
| 61-komponen-gaji.md | 05-penggajian.md |
| 62-kalkulator-bpjs.md | 05-penggajian.md |
| 63-kalkulator-pph21.md | 05-penggajian.md |
| 64-payroll-processing.md | 05-penggajian.md |
| 70-kelola-pengguna.md | 01-setup-awal.md |
| 71-layanan-mandiri.md | 05-penggajian.md |
| 75-kelola-produk.md | 08-industri-dagang.md |
| 76-transaksi-inventori.md | 08-industri-dagang.md |
| 77-kartu-stok.md | 08-industri-dagang.md |
| 78-produksi-bom.md | 09-industri-manufaktur.md |
| 79-analisis-profitabilitas-produk.md | 08-industri-dagang.md |
| 80-kebijakan-data.md | 11-keamanan-kepatuhan.md |
| 81-ekspor-data.md | 11-keamanan-kepatuhan.md |
| 82-keamanan.md | 11-keamanan-kepatuhan.md |
| 90-glosarium.md | 12-lampiran-glosarium.md |
| 91-referensi-akun.md | 12-lampiran-akun.md |
| 92-referensi-template.md | 12-lampiran-template.md |

## Next Steps

1. Update `UserManualGenerator.java` to reflect new structure
2. Update `ScreenshotCapture.java` page definitions to align with new sections
3. Consolidate markdown files according to migration table
4. Add screenshot capture statements to functional tests
5. Write/revise content for each section
6. Generate and review HTML output
