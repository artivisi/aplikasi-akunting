# Pendahuluan

## Selamat Datang

Aplikasi Akunting adalah sistem pencatatan keuangan yang dirancang khusus untuk usaha kecil dan menengah (UKM) di Indonesia. Anda **tidak perlu latar belakang akuntansi** untuk menggunakan aplikasi ini - panduan ini akan menjelaskan semuanya dengan bahasa sederhana.

**Apa yang bisa Anda lakukan dengan aplikasi ini?**

- Mencatat uang masuk (dari klien, penjualan, dll)
- Mencatat uang keluar (bayar listrik, gaji, beli barang, dll)
- Melihat berapa saldo kas dan bank Anda
- Mencetak laporan keuangan untuk pajak atau investor
- Mengelola proyek dan menghitung keuntungan per proyek

---

## Masuk ke Aplikasi

### Langkah 1: Buka Halaman Login

Buka browser (Chrome, Firefox, atau Safari) dan ketik alamat aplikasi. Anda akan melihat halaman login seperti ini:

![Halaman Login](screenshots/login.png)

### Langkah 2: Masukkan Username dan Password

1. Ketik **username** Anda di kolom pertama
2. Ketik **password** Anda di kolom kedua
3. Klik tombol **Masuk**

> **Lupa password?** Hubungi administrator sistem Anda untuk reset password.

### Langkah 3: Lihat Dashboard

Setelah berhasil login, Anda akan melihat **Dashboard** - halaman utama yang menampilkan ringkasan kondisi keuangan bisnis Anda.

![Dashboard Utama](screenshots/dashboard.png)

**Apa arti angka-angka di Dashboard?**

| Kartu | Artinya | Contoh |
|-------|---------|--------|
| **Kas & Bank** | Total uang tunai + saldo semua rekening bank | Rp 50.000.000 |
| **Pendapatan** | Total uang masuk bulan ini | Rp 25.000.000 |
| **Pengeluaran** | Total uang keluar bulan ini | Rp 15.000.000 |
| **Laba Bersih** | Pendapatan dikurangi Pengeluaran | Rp 10.000.000 |

---

## Mengenal Tampilan Aplikasi

### Sidebar (Menu Samping)

Di sisi kiri layar, ada menu untuk mengakses berbagai fitur:

| Menu | Fungsi | Kapan Digunakan |
|------|--------|-----------------|
| **Dashboard** | Lihat ringkasan keuangan | Setiap buka aplikasi |
| **Transaksi** | Catat uang masuk/keluar | Setiap ada transaksi |
| **Akun** | Kelola daftar akun | Setup awal atau tambah akun baru |
| **Template** | Kelola template transaksi | Setup awal atau buat template baru |
| **Laporan** | Cetak laporan keuangan | Akhir bulan atau saat butuh laporan |

### Tombol-Tombol Penting

| Tombol | Warna | Fungsi |
|--------|-------|--------|
| **+ Baru** atau **Transaksi Baru** | Biru | Membuat data baru |
| **Simpan** | Hijau | Menyimpan sebagai draft |
| **Simpan & Posting** | Biru | Menyimpan dan langsung aktifkan |
| **Batal** | Abu-abu | Kembali tanpa menyimpan |
| **Hapus** | Merah | Menghapus data |

---

## Cara Menggunakan Panduan Ini

Panduan ini disusun berdasarkan **apa yang ingin Anda lakukan**, bukan berdasarkan menu aplikasi. Cari bagian yang sesuai dengan kebutuhan Anda:

### Saya Baru Pertama Kali Pakai

| Langkah | Baca Bagian |
|---------|-------------|
| 1. Setup aplikasi | [Setup Awal](50-setup-awal.md) |
| 2. Pahami konsep dasar | [Konsep Dasar](01-konsep-dasar.md) |
| 3. Coba catat transaksi pertama | [Mencatat Pendapatan](10-mencatat-pendapatan.md) |

### Saya Ingin Melakukan Sesuatu

| Anda Ingin... | Baca Bagian |
|---------------|-------------|
| Catat uang masuk dari klien | [Mencatat Pendapatan](10-mencatat-pendapatan.md) |
| Catat bayar listrik, gaji, atau belanja | [Mencatat Pengeluaran](11-mencatat-pengeluaran.md) |
| Pindahkan uang antar rekening | [Transfer Antar Akun](12-transfer-antar-akun.md) |
| Kirim foto struk via Telegram | [Telegram Receipt](13-telegram-receipt.md) |
| Lihat berapa saldo saya | [Laporan Harian](20-laporan-harian.md) |
| Cetak laporan bulanan | [Laporan Bulanan](21-laporan-bulanan.md) |
| Hitung dan lapor pajak PPN | [Transaksi PPN](30-transaksi-ppn.md) |
| Kelola proyek dan invoice | [Setup Proyek](40-setup-proyek.md) |
| Jalankan penggajian | [Proses Payroll](64-payroll-processing.md) |
| Kelola stok barang | [Transaksi Inventori](76-transaksi-inventori.md) |
| Tambah user baru | [Kelola Pengguna](70-kelola-pengguna.md) |

---

## Istilah Penting untuk Pemula

Jangan khawatir jika Anda tidak paham istilah akuntansi. Berikut penjelasan sederhana:

| Istilah | Artinya dalam Bahasa Sehari-hari |
|---------|----------------------------------|
| **Transaksi** | Setiap kali uang masuk atau keluar |
| **Posting** | Menyimpan transaksi secara permanen (tidak bisa diedit) |
| **Draft** | Transaksi yang masih bisa diedit (belum final) |
| **Akun** | Kategori untuk mengelompokkan uang (misal: Kas, Bank BCA, Listrik) |
| **Template** | Format siap pakai untuk transaksi yang sering dilakukan |
| **Jurnal** | Catatan rinci transaksi (mana yang bertambah, mana yang berkurang) |
| **Debit** | Kolom kiri dalam jurnal (biasanya = uang bertambah untuk kas/bank) |
| **Kredit** | Kolom kanan dalam jurnal (biasanya = uang berkurang untuk kas/bank) |
| **Neraca** | Laporan yang menunjukkan apa yang Anda miliki vs apa yang Anda hutang |
| **Laba Rugi** | Laporan yang menunjukkan keuntungan atau kerugian |

---

## Struktur Setiap Bab

Setiap bab dalam panduan ini mengikuti struktur yang sama:

1. **Kapan Anda Membutuhkan Ini** - Situasi yang memerlukan fitur ini
2. **Konsep yang Perlu Dipahami** - Penjelasan sederhana (bisa dilewati jika sudah paham)
3. **Skenario** - Langkah demi langkah dengan contoh nyata
4. **Tips** - Saran praktis
5. **Lihat Juga** - Link ke bab terkait

---

## Persyaratan Sistem

- Browser modern (Chrome, Firefox, Safari, Edge)
- Koneksi internet stabil
- Resolusi layar minimal 1024x768 (laptop/komputer)

---

## Konvensi Penulisan

| Format | Arti |
|--------|------|
| **Teks tebal** | Tombol atau menu yang perlu diklik |
| `Teks dalam kotak` | Nilai yang perlu diketik persis seperti itu |
| > Catatan: | Informasi penting yang perlu diperhatikan |
| Tabel | Data referensi atau perbandingan |

---

## Butuh Bantuan?

- **Lupa cara melakukan sesuatu?** Gunakan Daftar Isi di bawah
- **Menemukan error?** Cek bagian Troubleshooting di setiap bab
- **Butuh bantuan teknis?** Hubungi administrator sistem Anda

---

## Daftar Isi Lengkap

Panduan ini disusun dari fitur dasar ke fitur lanjutan:

### Bagian I: Pengantar
- [Pendahuluan](00-pendahuluan.md) - Cara menggunakan panduan ini (Anda di sini)
- [Konsep Dasar](01-konsep-dasar.md) - Dasar-dasar akuntansi dalam bahasa sederhana

### Bagian II: Setup & Konfigurasi
- [Setup Awal](50-setup-awal.md) - Langkah pertama setelah install
- [Kelola Periode Fiskal](54-kelola-periode-fiskal.md) - Mengatur tahun buku
- [Kelola Template](51-kelola-template.md) - Membuat template transaksi
- [Kelola Klien](52-kelola-klien.md) - Menambah data klien
- [Jadwal Amortisasi](53-jadwal-amortisasi.md) - Untuk sewa/asuransi dibayar dimuka
- [Setup Telegram Bot](55-setup-telegram.md) - Kirim struk via Telegram

### Bagian III: Operasi Harian
- [Mencatat Pendapatan](10-mencatat-pendapatan.md) - Uang masuk
- [Mencatat Pengeluaran](11-mencatat-pengeluaran.md) - Uang keluar
- [Transfer Antar Akun](12-transfer-antar-akun.md) - Pindah uang
- [Telegram Receipt](13-telegram-receipt.md) - Kirim struk via HP

### Bagian IV: Pelaporan
- [Laporan Harian](20-laporan-harian.md) - Cek transaksi dan saldo
- [Laporan Bulanan](21-laporan-bulanan.md) - Neraca dan Laba Rugi
- [Laporan Tahunan](22-laporan-tahunan.md) - Tutup buku akhir tahun
- [Laporan Penyusutan](23-laporan-penyusutan.md) - Penyusutan aset
- [Penutupan Tahun Buku](24-penutupan-tahun-buku.md) - Proses tutup buku

### Bagian V: Perpajakan
- [Transaksi PPN](30-transaksi-ppn.md) - Pajak Pertambahan Nilai
- [Transaksi PPh](31-transaksi-pph.md) - Pajak Penghasilan
- [Laporan Pajak](32-laporan-pajak.md) - Cetak laporan untuk SPT
- [Kalender Pajak](33-kalender-pajak.md) - Reminder deadline pajak

### Bagian VI: Manajemen Proyek
- [Setup Proyek](40-setup-proyek.md) - Buat proyek baru
- [Tracking Proyek](41-tracking-proyek.md) - Pantau progress
- [Invoice & Penagihan](42-invoice-penagihan.md) - Buat invoice
- [Analisis Profitabilitas](43-analisis-profitabilitas.md) - Hitung profit per proyek

### Bagian VII: Penggajian
- [Kelola Karyawan](60-kelola-karyawan.md) - Data karyawan
- [Komponen Gaji](61-komponen-gaji.md) - Tunjangan dan potongan
- [Kalkulator BPJS](62-kalkulator-bpjs.md) - Hitung iuran BPJS
- [Kalkulator PPh 21](63-kalkulator-pph21.md) - Hitung pajak gaji
- [Proses Payroll](64-payroll-processing.md) - Jalankan penggajian

### Bagian VIII: Inventori & Produksi
- [Kelola Produk](75-kelola-produk.md) - Master produk
- [Transaksi Inventori](76-transaksi-inventori.md) - Beli, jual, adjustment
- [Kartu Stok](77-kartu-stok.md) - Laporan stok
- [Produksi (BOM)](78-produksi-bom.md) - Bill of Materials
- [Analisis Profitabilitas Produk](79-analisis-profitabilitas-produk.md) - Margin produk

### Bagian IX: Administrasi & Keamanan
- [Kelola Pengguna](70-kelola-pengguna.md) - Tambah user baru
- [Layanan Mandiri](71-layanan-mandiri.md) - Self-service karyawan
- [Kebijakan Data](80-kebijakan-data.md) - Privasi data
- [Ekspor Data](81-ekspor-data.md) - Backup data
- [Keamanan](82-keamanan.md) - Password dan audit

### Lampiran
- [Glosarium](90-glosarium.md) - Daftar istilah
- [Referensi Akun](91-referensi-akun.md) - Daftar kode akun
- [Referensi Template](92-referensi-template.md) - Daftar template bawaan
