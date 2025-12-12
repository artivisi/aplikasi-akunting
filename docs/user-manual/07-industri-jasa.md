# Industri Jasa

Panduan lengkap untuk perusahaan jasa (IT Services, Konsultan, Agency) dengan status PKP.

## Karakteristik Industri Jasa

### Ciri Khas

- **Produk tidak berwujud** - Menjual keahlian dan waktu
- **Project-based** - Pendapatan dari proyek dengan milestone
- **Time & Material** - Biaya berdasarkan jam kerja
- **Retainer** - Pendapatan berulang bulanan
- **Tidak ada inventory** - Tidak ada persediaan barang

### Alur Bisnis Tipikal

```
Klien → Proposal → Kontrak → Proyek → Milestone → Invoice → Pembayaran
```

---

## Client Management

### Melihat Daftar Klien

Buka menu **Klien** > **Daftar Klien**.

![Daftar Klien](screenshots/service/clients-list.png)

### Detail Klien

Klik klien untuk melihat:
- Informasi kontak
- Daftar proyek
- History invoice
- Total revenue dari klien

### Menambah Klien Baru

1. Klik **Klien Baru**

2. Isi:
   - Kode klien (unik)
   - Nama perusahaan
   - NPWP
   - Alamat
   - Contact person
   - Email, telepon
3. Klik **Simpan**

---

## Project Management

### Melihat Daftar Proyek

Buka menu **Proyek** > **Daftar Proyek**.

![Daftar Proyek](screenshots/service/projects-list.png)

### Detail Proyek

Klik pada proyek untuk melihat detail. Informasi yang ditampilkan:
- Status proyek
- Progress milestone
- Total nilai kontrak
- Pendapatan yang sudah diakui
- Invoice yang sudah diterbitkan

### Menambah Proyek Baru

1. Klik **Proyek Baru**

2. Isi:
   - Kode proyek
   - Nama proyek
   - Klien (pilih dari dropdown)
   - Nilai kontrak
   - Tanggal mulai & target selesai
   - Deskripsi
3. Tab **Milestone** - Tambah milestone:
   - Nama milestone
   - Bobot (%)
   - Target tanggal
4. Klik **Simpan**

### Workflow Proyek

```
DRAFT → ACTIVE → COMPLETED
```

| Status | Arti |
|--------|------|
| DRAFT | Proyek belum dimulai |
| ACTIVE | Proyek sedang berjalan |
| COMPLETED | Proyek selesai |

### Update Progress Milestone

1. Buka detail proyek
2. Klik milestone
3. Update status:
   - Tanggal aktual selesai
   - Catatan
4. Klik **Simpan**

Saat milestone selesai, pendapatan dapat diakui proporsional sesuai bobot.

---

## Template Transaksi Jasa

### Template Standar

| Template | Fungsi |
|----------|--------|
| Pendapatan Jasa + PPN | Pendapatan dengan PPN 11% |
| Pendapatan Jasa tanpa PPN | Pendapatan tanpa PPN |
| Terima DP Proyek | DP masuk Pendapatan Diterima Dimuka |
| Pengakuan Pendapatan | Recognize revenue dari DDM |
| Beban Operasional | Pengeluaran operasional |

### Melihat Template

Buka menu **Pengaturan** > **Template**.

![Daftar Template](screenshots/service/templates-list.png)

### Detail Template

![Detail Template](screenshots/service/templates-detail.png)

---

## Transaksi Harian: Contoh Praktis

### Transaksi 1: Setoran Modal Awal

**Konteks:** PT ArtiVisi Intermedia baru didirikan, pemilik menyetor modal Rp 500 juta.

Lihat walkthrough lengkap di [Tutorial Akuntansi - Setoran Modal](02-tutorial-akuntansi.md#contoh-lengkap-setoran-modal).

**Screenshot:**

![Form Setoran Modal](screenshots/service/01-setoran-modal-awal-20-form.png)
![Detail Setoran Modal](screenshots/service/01-setoran-modal-awal-20-result.png)

---

### Transaksi 2: Pendapatan Konsultasi dengan PPN

**Konteks:** Invoice #INV-2024-001 untuk proyek Core Banking di Bank Mandiri, Milestone 1 selesai.

**Detail:**
- DPP: Rp 176.756.757
- PPN 11%: Rp 19.443.243
- Total: Rp 196.200.000

Lihat walkthrough lengkap di [Tutorial Akuntansi - Pendapatan PPN](02-tutorial-akuntansi.md#contoh-lengkap-pendapatan-dengan-ppn).

**Screenshot:**

![Form Pendapatan Konsultasi](screenshots/service/02-konsultasi-core-banking-milest-form.png)
![Detail Pendapatan Konsultasi](screenshots/service/02-konsultasi-core-banking-milest-result.png)

---

### Transaksi 3: Bayar Lisensi Software

**Konteks:** Subscription tahunan JetBrains IntelliJ IDEA untuk tim developer.

![Form Bayar Software](screenshots/service/03-jetbrains-intellij-license-2-form.png)

**Detail:**
- **Tanggal:** 15 Januari 2024
- **Jumlah:** Rp 3.330.000
- **Keterangan:** JetBrains IntelliJ License 2024
- **Referensi:** JB-2024-001

**Jurnal:**
```
Dr. 5.1.21 Beban Software & Lisensi    Rp 3.330.000
    Cr. 1.1.02 Bank BCA                    Rp 3.330.000
```

![Detail Transaksi](screenshots/service/03-jetbrains-intellij-license-2-result.png)

**Catatan:** Lisensi 1 tahun, bisa diamortisasi jika perusahaan ingin alokasi bulanan.

---

### Transaksi 4: Bayar Cloud Services

**Konteks:** Tagihan AWS untuk server aplikasi klien.

![Form Bayar Cloud](screenshots/service/04-aws-cloud-services-ja-form.png)

**Detail:**
- **Provider:** AWS
- **Periode:** Januari 2024
- **Amount:** Rp 5.550.000
- **Referensi:** AWS-2024-001

**Jurnal:**
```
Dr. 5.1.20 Beban Cloud & Server        Rp 5.550.000
    Cr. 1.1.02 Bank BCA                    Rp 5.550.000
```

![Detail Transaksi](screenshots/service/04-aws-cloud-services-ja-result.png)

---

### Transaksi 5: Pendapatan Training

**Konteks:** Training IT Security untuk karyawan PT Semen Indonesia, pembayaran penuh.

![Form Pendapatan Training](screenshots/service/05-it-security-training-full-p-form.png)

**Detail:**
- DPP: Rp 147.297.297
- PPN 11%: Rp 16.202.703
- Total: Rp 163.500.000
- **Referensi:** INV-2024-002

**Jurnal:**
```
Dr. 1.1.02 Bank BCA                     Rp 163.500.000
    Cr. 4.1.01 Pendapatan Jasa Training     Rp 147.297.297
    Cr. 2.2.01 Hutang PPN                   Rp  16.202.703
```

![Detail Transaksi](screenshots/service/05-it-security-training-full-p-result.png)

---

## Invoice dan Penagihan

### Melihat Daftar Invoice

Buka menu **Invoice** > **Daftar Invoice** untuk melihat semua invoice yang telah dibuat.

### Membuat Invoice

1. Klik **Invoice Baru**
2. Pilih klien
3. Pilih proyek (opsional)
4. Isi item invoice:
   - Deskripsi
   - Quantity
   - Harga satuan
5. Sistem menghitung:
   - Subtotal
   - PPN (jika PKP)
   - Total
6. Klik **Simpan**

### Workflow Invoice

```
DRAFT → SENT → PAID
```

### Mencatat Pembayaran Invoice

Saat klien membayar:

1. Buka invoice
2. Klik **Terima Pembayaran**
3. Isi:
   - Tanggal terima
   - Jumlah (bisa partial)
   - Rekening penerima
4. Klik **Simpan**

Jurnal yang dibuat:
```
Dr. Bank                    xxx
    Cr. Piutang Usaha           xxx
```

---

## Profitabilitas Proyek

### Laporan Profitabilitas Proyek

Buka menu **Laporan** > **Profitabilitas Proyek** untuk melihat analisis margin per proyek.

Metrik yang ditampilkan:
- Total revenue proyek
- Total cost (gaji, vendor, dll)
- Gross profit
- Profit margin (%)

### Laporan Profitabilitas Klien

Buka menu **Laporan** > **Profitabilitas Klien** untuk melihat analisis revenue dan margin per klien.

Agregasi per klien:
- Total revenue dari klien
- Total cost
- Profit
- Jumlah proyek

---

## Skenario Transaksi Lengkap

Berikut adalah contoh lengkap alur transaksi PT ArtiVisi Intermedia (IT Services) selama 2 bulan operasional.

### Transaksi 1: Setoran Modal Awal

**Tanggal:** 1 Januari 2024
**Template:** Setoran Modal
**Jumlah:** Rp 500.000.000

![Form Setoran Modal](screenshots/service/01-setoran-modal-awal-20-form.png)

**Jurnal Entry:**
```
Dr. 1.1.02 Bank BCA              Rp 500.000.000
    Cr. 3.1.01 Modal Saham           Rp 500.000.000
```

![Hasil Transaksi](screenshots/service/01-setoran-modal-awal-20-result.png)

**Penjelasan:** Pemilik menyetor modal awal Rp 500 juta ke rekening perusahaan. Saldo awal Bank BCA menjadi Rp 500.000.000.

---

### Transaksi 2: Pendapatan Konsultasi dengan PPN

**Tanggal:** 15 Januari 2024
**Template:** Pendapatan Jasa Konsultasi
**Jumlah Total:** Rp 196.200.000
**Referensi:** INV-2024-001

![Form Pendapatan](screenshots/service/02-konsultasi-core-banking-milest-form.png)

**Breakdown PPN:**
- DPP (Dasar Pengenaan Pajak): Rp 196.200.000 / 1.11 = **Rp 176.756.757**
- PPN 11%: Rp 176.756.757 × 11% = **Rp 19.443.243**
- Total: **Rp 196.200.000**

**Jurnal Entry:**
```
Dr. 1.1.02 Bank BCA              Rp 196.200.000
    Cr. 4.1.02 Pendapatan Jasa       Rp 176.756.757
    Cr. 2.1.03 Hutang PPN            Rp  19.443.243
```

![Hasil Transaksi](screenshots/service/02-konsultasi-core-banking-milest-result.png)

**Penjelasan:** Perusahaan menerima pembayaran proyek konsultasi Core Banking Milestone 1. Karena PKP, pendapatan dipecah menjadi DPP dan PPN Keluaran yang akan disetor ke Dirjen Pajak.

**Dampak:** Saldo Bank BCA bertambah menjadi Rp 696.200.000.

---

### Transaksi 3: Bayar Lisensi Software

**Tanggal:** 15 Januari 2024
**Template:** Bayar Beban Software & Lisensi
**Jumlah:** Rp 3.330.000
**Referensi:** JB-2024-001

![Form Beban](screenshots/service/03-jetbrains-intellij-license-2-form.png)

**Jurnal Entry:**
```
Dr. 5.1.21 Beban Software & Lisensi  Rp 3.330.000
    Cr. 1.1.02 Bank BCA                  Rp 3.330.000
```

![Hasil Transaksi](screenshots/service/03-jetbrains-intellij-license-2-result.png)

**Penjelasan:** Pembayaran lisensi JetBrains IntelliJ untuk tim development.

**Dampak:** Saldo Bank BCA berkurang menjadi Rp 692.870.000. Beban operasional bertambah Rp 3.330.000.

---

### Transaksi 4: Bayar Cloud Services

**Tanggal:** 31 Januari 2024
**Template:** Bayar Beban Cloud & Server
**Jumlah:** Rp 5.550.000
**Referensi:** AWS-2024-001

![Form Cloud](screenshots/service/04-aws-cloud-services-ja-form.png)

**Jurnal Entry:**
```
Dr. 5.1.20 Beban Cloud & Server  Rp 5.550.000
    Cr. 1.1.02 Bank BCA              Rp 5.550.000
```

![Hasil Transaksi](screenshots/service/04-aws-cloud-services-ja-result.png)

**Penjelasan:** Pembayaran tagihan AWS untuk infrastruktur cloud bulan Januari.

**Dampak:** Saldo Bank BCA berkurang menjadi Rp 687.320.000. Total beban operasional menjadi Rp 8.880.000.

---

### Transaksi 5: Pendapatan Training

**Tanggal:** 28 Februari 2024
**Template:** Pendapatan Jasa Training
**Jumlah:** Rp 163.500.000
**Referensi:** INV-2024-002

![Form Training](screenshots/service/05-it-security-training-full-p-form.png)

**Jurnal Entry:**
```
Dr. 1.1.02 Bank BCA          Rp 163.500.000
    Cr. 4.1.01 Pendapatan Jasa   Rp 163.500.000
```

![Hasil Transaksi](screenshots/service/05-it-security-training-full-p-result.png)

**Penjelasan:** Pembayaran penuh untuk IT Security Training (non-PKP, tanpa PPN).

**Dampak:** Saldo Bank BCA bertambah menjadi **Rp 850.820.000** (saldo akhir).

---

## Laporan Keuangan

### Daftar Transaksi

![Daftar Transaksi](screenshots/service/transaction-list.png)

Semua 5 transaksi telah diposting dan dapat dilihat di menu **Transaksi** > **Daftar Transaksi**.

### Laporan Laba Rugi (Income Statement)

![Laporan Laba Rugi](screenshots/service/report-income-statement.png)

**Periode:** Januari - Februari 2024

**Pendapatan:**
- Pendapatan Jasa Training: Rp 163.500.000
- Pendapatan Jasa Konsultasi: Rp 176.756.757
- **Total Pendapatan: Rp 340.256.757**

**Beban:**
- Beban Cloud & Server: Rp 5.550.000
- Beban Software & Lisensi: Rp 3.330.000
- **Total Beban: Rp 8.880.000**

**Laba Bersih: Rp 331.376.757**

**Margin Laba: 97,4%** (laba/pendapatan)

**Analisis:** Perusahaan sangat profitable dengan margin 97%. Beban operasional hanya 2,6% dari pendapatan, menunjukkan efisiensi tinggi khas perusahaan jasa IT.

### Neraca (Balance Sheet)

![Neraca](screenshots/service/report-balance-sheet.png)

**Per 28 Februari 2024**

**ASET:**
- Bank BCA: Rp 850.820.000
- **Total Aset: Rp 850.820.000**

**KEWAJIBAN:**
- Hutang PPN: Rp 19.443.243
- **Total Kewajiban: Rp 19.443.243**

**EKUITAS:**
- Modal Saham: Rp 500.000.000
- Laba Ditahan: Rp 331.376.757
- **Total Ekuitas: Rp 831.376.757**

**Total Kewajiban + Ekuitas: Rp 850.820.000** ✓ (Balanced)

**Analisis:** Struktur keuangan sehat dengan hutang minimal (hanya PPN yang akan disetor). Ekuitas mencapai 97,7% dari total aset.

### Neraca Saldo (Trial Balance)

![Neraca Saldo](screenshots/service/report-trial-balance.png)

**Per 28 Februari 2024**

Menampilkan semua akun dengan saldo debit dan kredit. Total Debit = Total Kredit ✓

**Kegunaan:** Memverifikasi keseimbangan pembukuan sebelum membuat laporan keuangan final.

### Laporan Arus Kas (Cash Flow)

![Laporan Arus Kas](screenshots/service/report-cash-flow.png)

**Periode:** Januari - Februari 2024

**Arus Kas dari Aktivitas Operasi:**
- Penerimaan dari pelanggan: Rp 359.700.000
- Pembayaran beban operasional: (Rp 8.880.000)
- **Kas Bersih dari Operasi: Rp 350.820.000**

**Arus Kas dari Aktivitas Investasi:** Rp 0

**Arus Kas dari Aktivitas Pendanaan:**
- Setoran modal: Rp 500.000.000
- **Kas Bersih dari Pendanaan: Rp 500.000.000**

**Kenaikan Kas Bersih: Rp 850.820.000**

**Analisis:** Arus kas positif sangat kuat. Perusahaan mampu mendanai operasional dari revenue dan tidak memerlukan pendanaan eksternal selain modal awal.

### Daftar Jurnal

![Daftar Jurnal](screenshots/service/journal-list.png)

Menampilkan semua jurnal entry yang dibuat dari transaksi yang telah diposting. Setiap transaksi menghasilkan jurnal entry dengan detail debit dan kredit yang seimbang.

---

## Tips Industri Jasa

1. **Track time** - Catat jam kerja per proyek untuk analisis cost
2. **Milestone jelas** - Definisikan deliverable yang terukur
3. **Invoice tepat waktu** - Jangan tunda penagihan
4. **Review profitabilitas** - Analisis per proyek secara berkala
5. **Manage cashflow** - Monitor piutang dan aging

---

## Lihat Juga

- [Pengantar Industri](06-pengantar-industri.md) - Perbandingan industri
- [Perpajakan](04-perpajakan.md) - PPN untuk jasa
- [Tutorial Akuntansi](02-tutorial-akuntansi.md) - Jurnal dasar
