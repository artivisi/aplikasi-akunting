# Laporan Harian

## Kapan Anda Membutuhkan Ini

Gunakan panduan ini ketika Anda:
- Ingin mengecek transaksi yang sudah dicatat hari ini
- Ingin melihat saldo kas dan bank terkini
- Ingin memverifikasi apakah ada transaksi yang belum diposting
- Ingin melihat mutasi akun tertentu

## Konsep yang Perlu Dipahami

Laporan harian membantu Anda:
- **Monitoring** - Memastikan semua transaksi tercatat
- **Verifikasi** - Mencocokkan saldo aplikasi dengan saldo aktual
- **Deteksi** - Menemukan kesalahan pencatatan lebih awal

## Skenario 1: Cek Kondisi Keuangan Hari Ini (Dashboard)

**Situasi**: Pagi hari, Anda ingin melihat ringkasan kondisi keuangan bisnis.

**Langkah-langkah**:

1. Buka aplikasi, halaman **Dashboard** akan tampil otomatis
2. Lihat kartu-kartu KPI:
   - **Kas & Bank** - Total saldo kas dan bank saat ini
   - **Piutang** - Total piutang yang belum dibayar
   - **Hutang** - Total hutang yang harus dibayar
   - **Transaksi** - Jumlah transaksi bulan ini
3. Untuk melihat periode lain, gunakan pemilih bulan di atas

**Informasi yang Ditampilkan**:

| Kartu | Arti |
|-------|------|
| Pendapatan | Total pendapatan bulan ini vs bulan lalu |
| Beban | Total pengeluaran bulan ini vs bulan lalu |
| Laba Bersih | Pendapatan - Beban |
| Margin Laba | (Laba / Pendapatan) × 100% |

## Skenario 2: Cek Transaksi Hari Ini

**Situasi**: Akhir hari kerja, Anda ingin memastikan semua transaksi sudah tercatat.

**Langkah-langkah**:

1. Klik menu **Transaksi** di sidebar
2. Di filter **Periode**, pilih tanggal hari ini
3. Klik **Tampilkan** atau tekan Enter
4. Review daftar transaksi yang muncul:
   - Periksa apakah semua transaksi hari ini sudah ada
   - Perhatikan status transaksi (Draft/Posted)
5. Jika ada yang kurang, catat segera

**Tips Verifikasi**:
- Cocokkan dengan struk/bukti yang Anda miliki
- Pastikan tidak ada transaksi yang masih Draft
- Perhatikan total pengeluaran dan pemasukan

## Skenario 3: Cek Saldo Bank

**Situasi**: Anda ingin mencocokkan saldo di aplikasi dengan saldo di mobile banking.

**Langkah-langkah**:

1. Klik menu **Buku Besar** di sidebar
2. Di filter **Akun**, pilih rekening bank (contoh: Bank BCA)
3. Di filter **Periode**, biarkan kosong untuk melihat sampai hari ini
4. Klik **Tampilkan**
5. Lihat **Saldo Akhir** di bagian bawah
6. Bandingkan dengan saldo di mobile banking

**Jika Saldo Tidak Cocok**:

| Selisih | Kemungkinan Penyebab |
|---------|---------------------|
| Aplikasi lebih besar | Ada transaksi pengeluaran yang belum dicatat |
| Aplikasi lebih kecil | Ada penerimaan yang belum dicatat |
| Selisih kecil | Biaya admin bank yang belum dicatat |

## Skenario 4: Lihat Mutasi Akun

**Situasi**: Anda ingin melihat detail transaksi yang mempengaruhi akun tertentu.

**Langkah-langkah**:

1. Klik menu **Buku Besar** di sidebar
2. Pilih **Akun** yang ingin dilihat
3. Pilih **Periode** (tanggal awal - tanggal akhir)
4. Klik **Tampilkan**
5. Tabel mutasi menampilkan:
   - **Tanggal** - Tanggal transaksi
   - **Keterangan** - Deskripsi transaksi
   - **Debit** - Penambahan (untuk akun aset)
   - **Kredit** - Pengurangan (untuk akun aset)
   - **Saldo** - Saldo berjalan setelah transaksi

## Skenario 5: Cek Transaksi Draft yang Belum Diposting

**Situasi**: Anda ingin memastikan tidak ada transaksi yang masih draft.

**Langkah-langkah**:

1. Klik menu **Transaksi** di sidebar
2. Di filter **Status**, pilih **Draft**
3. Klik **Tampilkan**
4. Jika ada transaksi draft:
   - Review apakah sudah benar
   - Klik untuk membuka detail
   - Posting jika sudah yakin, atau hapus jika tidak valid

> Catatan: Transaksi Draft tidak mempengaruhi saldo akun. Pastikan semua transaksi valid sudah diposting.

## Skenario 6: Cari Transaksi Tertentu

**Situasi**: Anda ingin mencari transaksi berdasarkan nomor referensi atau keterangan.

**Langkah-langkah**:

1. Klik menu **Transaksi** di sidebar
2. Di kolom **Pencarian**, ketik kata kunci:
   - Nomor transaksi (TRX-2025-xxxx)
   - Nomor referensi (INV-xxx, PO-xxx)
   - Kata dalam keterangan (nama vendor, nama klien)
3. Tekan Enter atau klik ikon search
4. Hasil pencarian akan ditampilkan

## Tips

1. **Rutinkan pengecekan** - Biasakan cek transaksi di akhir hari kerja
2. **Rekonsiliasi rutin** - Cocokkan saldo bank minimal seminggu sekali
3. **Posting segera** - Jangan biarkan transaksi draft terlalu lama
4. **Simpan bukti** - Lampirkan bukti transaksi untuk audit trail

## Troubleshooting

| Masalah | Solusi |
|---------|--------|
| Saldo tidak update | Pastikan transaksi sudah Posted, bukan Draft |
| Transaksi tidak muncul | Periksa filter periode dan status |
| Dashboard kosong | Pastikan ada transaksi di periode yang dipilih |

## Lihat Juga

- [Mencatat Pengeluaran](11-mencatat-pengeluaran.md) - Catat transaksi yang terlewat
- [Laporan Bulanan](21-laporan-bulanan.md) - Laporan yang lebih komprehensif
- [Transfer Antar Akun](12-transfer-antar-akun.md) - Jika ada selisih karena transfer
