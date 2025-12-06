# Pendahuluan

## Selamat Datang

Aplikasi Akunting adalah sistem pencatatan keuangan yang dirancang khusus untuk usaha kecil dan menengah (UKM) di Indonesia. Aplikasi ini mendukung standar akuntansi Indonesia (PSAK) dan kepatuhan perpajakan.

## Cara Menggunakan Panduan Ini

Panduan ini disusun berdasarkan **tugas yang ingin Anda selesaikan**, bukan berdasarkan menu aplikasi. Cari bagian yang sesuai dengan kebutuhan Anda:

| Anda Ingin... | Baca Bagian |
|---------------|-------------|
| Mencatat pembayaran dari klien | [Mencatat Pendapatan](10-mencatat-pendapatan.md) |
| Mencatat bayar listrik, vendor, gaji | [Mencatat Pengeluaran](11-mencatat-pengeluaran.md) |
| Pindahkan uang antar rekening | [Transfer Antar Akun](12-transfer-antar-akun.md) |
| Kirim struk via Telegram | [Telegram Receipt](13-telegram-receipt.md) |
| Cetak laporan keuangan bulanan | [Laporan Bulanan](21-laporan-bulanan.md) |
| Hitung dan lapor PPN | [Transaksi PPN](30-transaksi-ppn.md) & [Laporan Pajak](32-laporan-pajak.md) |
| Kelola proyek dan invoice | [Setup Proyek](40-setup-proyek.md) |
| Jalankan penggajian | [Proses Payroll](64-payroll-processing.md) |
| Kelola stok barang | [Transaksi Inventori](76-transaksi-inventori.md) |
| Kelola user dan akses | [Kelola Pengguna](70-kelola-pengguna.md) |
| Setup awal aplikasi | [Setup Awal](50-setup-awal.md) |

## Struktur Setiap Bab

Setiap bab dalam panduan ini mengikuti struktur yang sama:

1. **Kapan Anda Membutuhkan Ini** - Situasi yang memerlukan fitur ini
2. **Konsep yang Perlu Dipahami** - Penjelasan singkat konsep akuntansi terkait
3. **Skenario** - Langkah demi langkah untuk kasus nyata
4. **Tips** - Best practices dan saran
5. **Lihat Juga** - Link ke bab terkait

## Persyaratan Sistem

- Browser modern (Chrome, Firefox, Safari, Edge)
- Koneksi internet stabil
- Resolusi layar minimal 1024x768

## Konvensi Penulisan

| Konvensi | Arti |
|----------|------|
| **Teks tebal** | Tombol atau menu yang perlu diklik |
| `Teks kode` | Nilai yang perlu dimasukkan |
| > Catatan: | Informasi penting yang perlu diperhatikan |

## Daftar Isi

### Pengantar
- [Pendahuluan](00-pendahuluan.md) - Cara menggunakan panduan ini
- [Konsep Dasar](01-konsep-dasar.md) - Dasar-dasar akuntansi

### Bagian I: Operasi Harian
- [Mencatat Pendapatan](10-mencatat-pendapatan.md) - Penerimaan dari klien
- [Mencatat Pengeluaran](11-mencatat-pengeluaran.md) - Bayar listrik, vendor, gaji
- [Transfer Antar Akun](12-transfer-antar-akun.md) - Pindah dana
- [Telegram Receipt](13-telegram-receipt.md) - Kirim struk via Telegram

### Bagian II: Pelaporan
- [Laporan Harian](20-laporan-harian.md) - Cek transaksi dan saldo
- [Laporan Bulanan](21-laporan-bulanan.md) - Neraca, laba rugi
- [Laporan Tahunan](22-laporan-tahunan.md) - Tutup buku akhir tahun

### Bagian III: Perpajakan
- [Transaksi PPN](30-transaksi-ppn.md) - Penjualan/pembelian dengan PPN
- [Transaksi PPh](31-transaksi-pph.md) - Potong PPh 23, PPh 21
- [Laporan Pajak](32-laporan-pajak.md) - Cetak laporan untuk SPT
- [Kalender Pajak](33-kalender-pajak.md) - Tracking deadline pajak

### Bagian IV: Manajemen Proyek
- [Setup Proyek](40-setup-proyek.md) - Buat proyek baru
- [Tracking Proyek](41-tracking-proyek.md) - Pantau progress dan biaya
- [Invoice & Penagihan](42-invoice-penagihan.md) - Buat dan kirim invoice
- [Analisis Profitabilitas](43-analisis-profitabilitas.md) - Laporan profit per proyek

### Bagian V: Konfigurasi
- [Setup Awal](50-setup-awal.md) - Konfigurasi pertama kali
- [Kelola Template](51-kelola-template.md) - Buat dan edit template jurnal
- [Kelola Klien](52-kelola-klien.md) - Data klien
- [Jadwal Amortisasi](53-jadwal-amortisasi.md) - Beban dibayar dimuka
- [Kelola Periode Fiskal](54-kelola-periode-fiskal.md) - Tutup buku bulanan
- [Setup Telegram Bot](55-setup-telegram.md) - Konfigurasi integrasi Telegram

### Bagian VI: Penggajian
- [Kelola Karyawan](60-kelola-karyawan.md) - Data karyawan dan PTKP
- [Komponen Gaji](61-komponen-gaji.md) - Tunjangan dan potongan
- [Kalkulator BPJS](62-kalkulator-bpjs.md) - Hitung iuran BPJS
- [Kalkulator PPh 21](63-kalkulator-pph21.md) - Hitung pajak penghasilan
- [Proses Payroll](64-payroll-processing.md) - Jalankan penggajian bulanan

### Bagian VII: Pengguna & Keamanan
- [Kelola Pengguna](70-kelola-pengguna.md) - User dan role management
- [Layanan Mandiri](71-layanan-mandiri.md) - Self-service untuk karyawan

### Bagian VIII: Inventori & Produksi
- [Kelola Produk](75-kelola-produk.md) - Master produk dan kategori
- [Transaksi Inventori](76-transaksi-inventori.md) - Pembelian, penjualan, adjustment
- [Kartu Stok](77-kartu-stok.md) - Laporan stok dan valuasi
- [Produksi (BOM)](78-produksi-bom.md) - Bill of Materials dan order produksi
- [Analisis Profitabilitas Produk](79-analisis-profitabilitas-produk.md) - Margin dan HPP

### Bagian IX: Kebijakan Data & Keamanan
- [Kebijakan Data](80-kebijakan-data.md) - GDPR/UU PDP compliance
- [Ekspor Data](81-ekspor-data.md) - Full data export/import
- [Keamanan](82-keamanan.md) - Password, enkripsi, audit log

### Lampiran
- [Glosarium](90-glosarium.md) - Istilah-istilah akuntansi
- [Referensi Akun](91-referensi-akun.md) - Daftar kode akun standar
- [Referensi Template](92-referensi-template.md) - Daftar template bawaan
