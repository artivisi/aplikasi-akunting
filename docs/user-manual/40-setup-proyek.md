# Setup Proyek

## Kapan Anda Membutuhkan Ini

Gunakan panduan ini ketika Anda:
- Mendapat kontrak proyek baru dari klien
- Ingin melacak pendapatan dan biaya per proyek
- Perlu mengatur milestone dan termin pembayaran
- Ingin menganalisis profitabilitas per proyek

## Konsep yang Perlu Dipahami

### Apa Itu Manajemen Proyek di Aplikasi Ini?

Fitur proyek memungkinkan Anda:
- **Tracking pendapatan** - Berapa yang sudah diterima dari proyek
- **Tracking biaya** - Berapa yang sudah dikeluarkan untuk proyek
- **Analisis profit** - Apakah proyek untung atau rugi
- **Progress monitoring** - Seberapa jauh proyek sudah berjalan

### Komponen Proyek

| Komponen | Fungsi |
|----------|--------|
| **Informasi Dasar** | Nama, klien, nilai kontrak, budget |
| **Milestone** | Tahapan pekerjaan dengan bobot progress |
| **Termin Pembayaran** | Jadwal pembayaran dari klien |
| **Transaksi Terkait** | Semua transaksi yang dihubungkan ke proyek |

### Status Proyek

| Status | Arti |
|--------|------|
| **Active** | Proyek sedang berjalan |
| **Completed** | Proyek selesai, masih tampil di laporan |
| **Archived** | Proyek diarsipkan, tidak tampil di dropdown |

## Skenario 1: Buat Proyek Baru

**Situasi**: Anda mendapat kontrak proyek pengembangan website senilai Rp 50.000.000.

**Langkah-langkah**:

1. Klik menu **Proyek** di sidebar
2. Klik tombol **Proyek Baru**
3. Isi informasi dasar:
   - **Kode**: `PRJ-2025-001` (atau auto-generate)
   - **Nama**: `Website E-commerce PT ABC`
   - **Klien**: Pilih PT ABC dari dropdown
   - **Nilai Kontrak**: `50000000`
   - **Budget**: `35000000` (estimasi biaya internal)
   - **Tanggal Mulai**: 1 Desember 2025
   - **Tanggal Selesai**: 28 Februari 2026
   - **Deskripsi**: `Pengembangan website e-commerce dengan fitur payment gateway`
4. Klik **Simpan**

**Hasil**: Proyek baru dibuat dengan status Active.

## Skenario 2: Tambah Milestone

**Situasi**: Anda ingin membagi proyek menjadi 4 tahapan dengan bobot masing-masing.

**Langkah-langkah**:

1. Buka detail proyek yang baru dibuat
2. Scroll ke bagian **Milestone**
3. Klik **Tambah Milestone**
4. Untuk milestone pertama:
   - **Nama**: `Analisis & Desain`
   - **Bobot**: `20` (20% dari total proyek)
   - **Target**: 15 Desember 2025
5. Klik **Simpan**
6. Ulangi untuk milestone lainnya:

| No | Nama | Bobot | Target |
|----|------|-------|--------|
| 1 | Analisis & Desain | 20% | 15 Des 2025 |
| 2 | Pengembangan Frontend | 30% | 15 Jan 2026 |
| 3 | Pengembangan Backend | 30% | 31 Jan 2026 |
| 4 | Testing & Deployment | 20% | 28 Feb 2026 |

**Catatan**: Total bobot harus 100%.

## Skenario 3: Setup Termin Pembayaran

**Situasi**: Klien setuju membayar dalam 3 termin: DP 30%, setelah milestone 2 selesai 40%, sisanya di akhir.

**Langkah-langkah**:

1. Buka detail proyek
2. Scroll ke bagian **Termin Pembayaran**
3. Klik **Tambah Termin**

**Termin 1 - Down Payment**:
- **Nama**: `Down Payment`
- **Persentase**: `30` (30% dari kontrak = Rp 15.000.000)
- **Trigger**: `on_signing` (saat kontrak ditandatangani)
4. Klik **Simpan**

**Termin 2 - Progress Payment**:
- **Nama**: `Progress Payment`
- **Persentase**: `40` (40% = Rp 20.000.000)
- **Trigger**: `on_milestone`
- **Milestone**: Pilih `Pengembangan Frontend`
5. Klik **Simpan**

**Termin 3 - Final Payment**:
- **Nama**: `Final Payment`
- **Persentase**: `30` (30% = Rp 15.000.000)
- **Trigger**: `on_completion` (saat proyek selesai)
6. Klik **Simpan**

**Ringkasan Termin**:

| Termin | Persentase | Jumlah | Trigger |
|--------|------------|--------|---------|
| DP | 30% | Rp 15.000.000 | Saat kontrak |
| Progress | 40% | Rp 20.000.000 | Milestone 2 selesai |
| Final | 30% | Rp 15.000.000 | Proyek selesai |

## Skenario 4: Proyek dengan Klien Baru

**Situasi**: Anda mendapat proyek dari klien yang belum terdaftar.

**Langkah-langkah**:

1. Buat klien terlebih dahulu:
   - Klik menu **Klien** di sidebar
   - Klik **Klien Baru**
   - Isi data klien (nama, alamat, NPWP, dll)
   - Klik **Simpan**
2. Kembali ke menu **Proyek**
3. Buat proyek baru dan pilih klien yang baru dibuat

Lihat [Kelola Klien](52-kelola-klien.md) untuk detail manajemen klien.

## Skenario 5: Duplikat Proyek yang Serupa

**Situasi**: Anda sering mengerjakan proyek dengan struktur serupa (misalnya website development).

**Langkah-langkah**:

1. Buka detail proyek yang ingin dijadikan template
2. Klik tombol **Duplikat**
3. Ubah informasi yang berbeda:
   - Kode proyek baru
   - Nama proyek
   - Klien
   - Nilai kontrak
   - Tanggal
4. Milestone dan termin akan terkopi dengan persentase yang sama
5. Sesuaikan jika diperlukan
6. Klik **Simpan**

## Skenario 6: Edit Proyek yang Sudah Ada

**Situasi**: Ada perubahan nilai kontrak atau timeline proyek.

**Langkah-langkah**:

1. Buka detail proyek
2. Klik tombol **Edit**
3. Ubah informasi yang diperlukan:
   - Nilai kontrak (jika ada addendum)
   - Budget (jika estimasi berubah)
   - Tanggal selesai (jika timeline bergeser)
4. Klik **Simpan Perubahan**

**Catatan**: Perubahan nilai kontrak akan mempengaruhi perhitungan nilai termin pembayaran.

## Tips

1. **Budget realistis** - Estimasi budget dengan buffer 10-20% untuk contingency
2. **Milestone terukur** - Buat milestone dengan deliverable yang jelas
3. **Bobot proporsional** - Sesuaikan bobot dengan effort yang diperlukan
4. **Termin seimbang** - Hindari termin terlalu besar di akhir (risiko cash flow)

## Troubleshooting

| Masalah | Solusi |
|---------|--------|
| Klien tidak muncul | Buat klien terlebih dahulu di menu Klien |
| Total bobot tidak 100% | Sesuaikan bobot milestone |
| Total termin tidak 100% | Sesuaikan persentase termin |

## Lihat Juga

- [Tracking Proyek](41-tracking-proyek.md) - Update progress dan monitor biaya
- [Invoice & Penagihan](42-invoice-penagihan.md) - Buat invoice untuk termin
- [Kelola Klien](52-kelola-klien.md) - Kelola data klien
- [Analisis Profitabilitas](43-analisis-profitabilitas.md) - Laporan profit per proyek
