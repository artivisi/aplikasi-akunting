# Laporan Harian

## Kapan Anda Membutuhkan Ini

Gunakan panduan ini ketika Anda:
- Ingin mengecek transaksi yang sudah dicatat hari ini
- Ingin melihat saldo kas dan bank terkini
- Ingin memverifikasi apakah ada transaksi yang belum diposting
- Ingin melihat mutasi akun tertentu
- Ingin memastikan saldo di aplikasi cocok dengan saldo bank

## Konsep Sederhana

**Laporan harian = Cek kondisi keuangan bisnis Anda hari ini.**

Seperti melihat saldo di ATM atau mobile banking, laporan harian membantu Anda menjawab pertanyaan:
- Berapa uang yang saya punya sekarang?
- Transaksi apa saja yang sudah dicatat hari ini?
- Apakah saldo di aplikasi cocok dengan saldo di bank?

Pengecekan rutin membantu Anda mendeteksi kesalahan lebih awal sebelum menjadi masalah besar.

---

## Skenario 1: Cek Kondisi Keuangan (Dashboard)

**Situasi**: Pagi hari, Anda ingin melihat ringkasan kondisi keuangan bisnis secara cepat.

### Langkah 1: Buka Dashboard

Setelah login, Anda akan langsung melihat halaman **Dashboard**. Atau klik menu **Dashboard** di sidebar kiri.

![Dashboard Utama](screenshots/dashboard.png)

### Langkah 2: Pahami Kartu-Kartu KPI

Di Dashboard, ada beberapa kartu yang menampilkan angka penting:

| Kartu | Artinya | Contoh |
|-------|---------|--------|
| **Kas & Bank** | Total saldo semua rekening dan kas saat ini | Rp 50.000.000 |
| **Piutang** | Uang yang belum dibayar klien kepada Anda | Rp 15.000.000 |
| **Hutang** | Uang yang harus Anda bayar ke vendor | Rp 8.000.000 |
| **Pendapatan** | Total pemasukan bulan ini | Rp 25.000.000 |
| **Beban** | Total pengeluaran bulan ini | Rp 15.000.000 |
| **Laba Bersih** | Pendapatan - Beban = Keuntungan | Rp 10.000.000 |

> **Tips Membaca Dashboard**:
> - Angka **hijau** biasanya berarti bagus (naik dari periode sebelumnya)
> - Angka **merah** berarti perlu perhatian (turun dari periode sebelumnya)
> - Klik pada kartu untuk melihat detail lebih lanjut

### Langkah 3: Lihat Periode Lain (Opsional)

Di bagian atas Dashboard, ada pemilih periode. Klik untuk melihat kondisi bulan lalu atau periode tertentu.

---

## Skenario 2: Cek Semua Transaksi Hari Ini

**Situasi**: Akhir hari kerja, Anda ingin memastikan semua transaksi sudah tercatat dengan benar.

### Langkah 1: Buka Menu Transaksi

Di sidebar kiri, klik menu **Transaksi**. Anda akan melihat daftar semua transaksi.

![Daftar Transaksi](screenshots/transactions-list.png)

### Langkah 2: Filter Tanggal Hari Ini

Di bagian atas halaman, ada filter untuk menyaring transaksi:

1. Di field **Dari Tanggal**, pilih tanggal hari ini
2. Di field **Sampai Tanggal**, pilih tanggal hari ini
3. Klik tombol **Tampilkan** atau tekan Enter

### Langkah 3: Review Daftar Transaksi

Periksa daftar transaksi yang muncul:

| Kolom | Informasi |
|-------|-----------|
| **Tanggal** | Kapan transaksi terjadi |
| **No. Transaksi** | Nomor unik transaksi (TRX-2025-xxxx) |
| **Keterangan** | Deskripsi transaksi |
| **Jumlah** | Nilai transaksi dalam rupiah |
| **Status** | Draft (belum final) atau Posted (sudah final) |

> **Yang Perlu Dicek**:
> - Apakah semua transaksi hari ini sudah tercatat?
> - Apakah ada transaksi yang masih **Draft**? (harus di-posting)
> - Apakah keterangan dan jumlah sudah benar?

### Langkah 4: Posting Transaksi Draft (Jika Ada)

Jika ada transaksi dengan status **Draft**:
1. Klik pada transaksi tersebut untuk membuka detail
2. Periksa apakah data sudah benar
3. Klik tombol **Posting** untuk memfinalkan

> **Penting**: Transaksi Draft **tidak** mempengaruhi saldo. Hanya transaksi Posted yang dihitung.

---

## Skenario 3: Cek Saldo Bank (Cocokkan dengan Mobile Banking)

**Situasi**: Anda ingin mencocokkan saldo di aplikasi dengan saldo di mobile banking.

### Langkah 1: Buka Menu Buku Besar

Di sidebar kiri, klik menu **Buku Besar**.

![Buku Besar](screenshots/ledger-list.png)

### Langkah 2: Pilih Akun Bank

1. Di dropdown **Akun**, pilih rekening yang ingin dicek (contoh: Bank BCA)
2. Biarkan field tanggal kosong untuk melihat sampai hari ini
3. Klik tombol **Tampilkan**

### Langkah 3: Lihat Saldo Akhir

Di bagian bawah tabel, ada **Saldo Akhir**. Ini adalah saldo menurut aplikasi.

### Langkah 4: Bandingkan dengan Saldo Bank

Buka mobile banking atau cek ATM. Bandingkan angka saldo:

| Kondisi | Artinya | Yang Perlu Dilakukan |
|---------|---------|---------------------|
| **Saldo Cocok** | Pencatatan sudah benar | Tidak perlu tindakan |
| **Aplikasi lebih besar** | Ada pengeluaran yang belum dicatat | Cek mutasi bank, catat pengeluaran yang terlewat |
| **Aplikasi lebih kecil** | Ada pemasukan yang belum dicatat | Cek mutasi bank, catat pemasukan yang terlewat |
| **Selisih kecil (ribuan)** | Kemungkinan biaya admin bank | Catat sebagai Beban Administrasi Bank |

> **Tips Rekonsiliasi**:
> - Lakukan pengecekan minimal seminggu sekali
> - Simpan screenshot saldo bank sebagai dokumentasi
> - Selisih yang tidak terjelaskan bisa dicari di mutasi bank

---

## Skenario 4: Lihat Mutasi (Detail Pergerakan Uang)

**Situasi**: Anda ingin melihat riwayat transaksi yang mempengaruhi akun tertentu.

### Langkah 1: Buka Menu Buku Besar

Di sidebar kiri, klik menu **Buku Besar**.

### Langkah 2: Pilih Akun dan Periode

1. Di dropdown **Akun**, pilih akun yang ingin dilihat (contoh: Bank BCA)
2. Di field **Dari Tanggal**, pilih tanggal awal (contoh: 1 November 2025)
3. Di field **Sampai Tanggal**, pilih tanggal akhir (contoh: 30 November 2025)
4. Klik tombol **Tampilkan**

### Langkah 3: Baca Tabel Mutasi

Tabel mutasi menampilkan:

| Kolom | Artinya | Contoh |
|-------|---------|--------|
| **Tanggal** | Kapan transaksi terjadi | 15 Nov 2025 |
| **Keterangan** | Deskripsi transaksi | Pembayaran dari PT ABC |
| **Debit** | Uang masuk (untuk akun bank/kas) | Rp 5.000.000 |
| **Kredit** | Uang keluar (untuk akun bank/kas) | - |
| **Saldo** | Saldo setelah transaksi ini | Rp 55.000.000 |

> **Cara Membaca**:
> - **Debit** pada akun Bank/Kas = uang **MASUK** (saldo bertambah)
> - **Kredit** pada akun Bank/Kas = uang **KELUAR** (saldo berkurang)
> - **Saldo** adalah saldo berjalan (running balance)

---

## Skenario 5: Cari Transaksi Draft yang Belum Diposting

**Situasi**: Anda ingin memastikan tidak ada transaksi yang terlupakan (masih draft).

### Langkah 1: Buka Menu Transaksi

Di sidebar kiri, klik menu **Transaksi**.

### Langkah 2: Filter Status Draft

1. Di dropdown **Status**, pilih **Draft**
2. Klik tombol **Tampilkan**

### Langkah 3: Review dan Tindak Lanjuti

Jika ada transaksi draft:

| Kondisi Transaksi | Tindakan |
|-------------------|----------|
| Data sudah benar | Klik transaksi, lalu klik **Posting** |
| Data perlu koreksi | Klik transaksi, edit data, lalu klik **Posting** |
| Transaksi tidak valid | Klik transaksi, lalu klik **Hapus** |

> **Penting**: Jangan biarkan transaksi draft terlalu lama. Posting segera setelah data dikonfirmasi.

---

## Skenario 6: Cari Transaksi Tertentu

**Situasi**: Anda ingin mencari transaksi berdasarkan kata kunci (nama klien, nomor referensi, dll).

### Langkah 1: Buka Menu Transaksi

Di sidebar kiri, klik menu **Transaksi**.

### Langkah 2: Gunakan Kolom Pencarian

1. Di kolom **Pencarian** (biasanya di kanan atas), ketik kata kunci:
   - Nomor transaksi: `TRX-2025-0001`
   - Nomor referensi: `INV-001` atau `PO-2025-001`
   - Nama klien: `PT ABC`
   - Kata dalam keterangan: `listrik`, `gaji`
2. Tekan **Enter** atau klik ikon search

### Langkah 3: Review Hasil Pencarian

Hasil pencarian akan menampilkan transaksi yang cocok dengan kata kunci.

> **Tips Pencarian**:
> - Gunakan kata kunci yang spesifik
> - Jika tidak ketemu, coba kata kunci yang berbeda
> - Periksa juga filter tanggal (mungkin transaksi di luar periode filter)

---

## Tips Praktis

1. **Rutin setiap hari** - Biasakan cek transaksi di akhir hari kerja (15 menit saja cukup)

2. **Rekonsiliasi mingguan** - Cocokkan saldo aplikasi dengan saldo bank setiap akhir pekan

3. **Posting langsung** - Jangan tunda posting transaksi. Semakin cepat diposting, semakin akurat datanya.

4. **Simpan bukti** - Screenshot saldo bank, foto struk - sebagai dokumentasi jika ada selisih

5. **Perhatikan pola** - Jika selisih terus terjadi, mungkin ada jenis transaksi yang rutin terlewat (misal: biaya admin bank bulanan)

---

## Troubleshooting

| Masalah | Penyebab | Solusi |
|---------|----------|--------|
| Saldo tidak update | Transaksi masih Draft | Posting semua transaksi draft |
| Transaksi tidak muncul | Filter tanggal atau status tidak sesuai | Reset filter, perluas rentang tanggal |
| Dashboard kosong | Tidak ada transaksi di periode yang dipilih | Pilih periode yang ada transaksinya |
| Selisih dengan bank | Transaksi belum dicatat | Cek mutasi bank, catat yang terlewat |
| Tidak bisa posting | Ada field yang belum diisi | Lengkapi semua field wajib |

---

## Lihat Juga

- [Mencatat Pendapatan](10-mencatat-pendapatan.md) - Catat pemasukan yang terlewat
- [Mencatat Pengeluaran](11-mencatat-pengeluaran.md) - Catat pengeluaran yang terlewat
- [Transfer Antar Akun](12-transfer-antar-akun.md) - Jika ada selisih karena transfer
- [Laporan Bulanan](21-laporan-bulanan.md) - Laporan yang lebih komprehensif
