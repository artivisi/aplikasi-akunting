# Kelola Periode Fiskal

## Kapan Anda Membutuhkan Ini

Gunakan panduan ini ketika Anda:
- Ingin membuat periode akuntansi baru untuk tahun fiskal
- Perlu menutup buku bulanan setelah semua transaksi selesai
- Menandai bahwa SPT sudah dilaporkan untuk periode tertentu
- Perlu membuka kembali periode yang sudah ditutup untuk koreksi

## Konsep yang Perlu Dipahami

### Status Periode Fiskal

| Status | Arti | Transaksi |
|--------|------|-----------|
| **OPEN** | Periode aktif, transaksi bisa diinput | Boleh |
| **CLOSED** | Buku bulanan sudah ditutup | Tidak boleh |
| **TAX_FILED** | SPT sudah dilaporkan | Tidak boleh |

### Alur Status

```
OPEN → CLOSED → TAX_FILED
  ↑_______↓
   (reopen)
```

Periode bisa dibuka kembali (reopen) dari CLOSED ke OPEN, tapi periode yang sudah TAX_FILED tidak bisa diubah.

## Skenario 1: Generate Periode untuk Satu Tahun

**Situasi**: Awal tahun, Anda ingin menyiapkan semua periode fiskal untuk tahun baru.

**Langkah-langkah**:

1. Klik menu **Periode Fiskal** di sidebar
2. Klik tombol **Generate Tahun**
3. Pilih tahun yang ingin dibuat (contoh: 2025)
4. Klik **Generate**
5. Sistem akan membuat 12 periode (Januari - Desember)

**Hasil**:
- 12 periode baru dengan status OPEN
- Setiap periode memiliki tanggal awal dan akhir yang benar
- Periode siap digunakan untuk pencatatan transaksi

## Skenario 2: Buat Periode Tunggal

**Situasi**: Anda hanya perlu membuat satu periode tertentu.

**Langkah-langkah**:

1. Klik menu **Periode Fiskal** di sidebar
2. Klik tombol **+ Tambah Periode**
3. Pilih:
   - **Tahun**: 2025
   - **Bulan**: November
4. Klik **Simpan**

**Catatan**: Sistem akan menolak jika periode sudah ada.

## Skenario 3: Tutup Buku Bulanan

**Situasi**: Akhir bulan, semua transaksi sudah diinput dan Anda ingin mengunci periode.

**Langkah-langkah**:

1. Klik menu **Periode Fiskal** di sidebar
2. Filter berdasarkan tahun jika perlu
3. Klik periode yang ingin ditutup (contoh: November 2025)
4. Di halaman detail, periksa:
   - Pastikan semua transaksi sudah diposting
   - Cek neraca saldo balance
5. Klik tombol **Tutup Bulan**
6. (Opsional) Tambahkan catatan penutupan
7. Klik **Konfirmasi**

**Setelah Ditutup**:
- Status berubah menjadi CLOSED
- Transaksi baru tidak bisa diinput untuk periode ini
- Tanggal penutupan tercatat di sistem

## Skenario 4: Tandai SPT Sudah Dilaporkan

**Situasi**: Anda sudah melaporkan SPT Masa untuk periode tertentu.

**Langkah-langkah**:

1. Klik menu **Periode Fiskal** di sidebar
2. Klik periode yang sudah ditutup (status: CLOSED)
3. Klik tombol **Tandai SPT Dilaporkan**
4. (Opsional) Masukkan catatan:
   - Nomor tanda terima SPT
   - Tanggal pelaporan
5. Klik **Konfirmasi**

**Setelah Ditandai**:
- Status berubah menjadi TAX_FILED
- Periode terkunci permanen
- Tidak bisa dibuka kembali

## Skenario 5: Buka Kembali Periode

**Situasi**: Ada koreksi yang perlu dilakukan pada periode yang sudah ditutup.

**Langkah-langkah**:

1. Klik menu **Periode Fiskal** di sidebar
2. Klik periode dengan status CLOSED
3. Klik tombol **Buka Kembali**
4. Masukkan alasan pembukaan kembali (wajib)
5. Klik **Konfirmasi**

**Catatan Penting**:
- Hanya periode dengan status CLOSED yang bisa dibuka kembali
- Periode TAX_FILED tidak bisa dibuka kembali
- Alasan pembukaan tercatat untuk audit trail

## Skenario 6: Filter dan Cari Periode

**Situasi**: Anda ingin mencari periode tertentu dari daftar yang panjang.

**Langkah-langkah**:

1. Klik menu **Periode Fiskal** di sidebar
2. Gunakan filter:
   - **Tahun**: Pilih tahun spesifik
   - **Status**: OPEN / CLOSED / TAX_FILED
3. Daftar periode akan difilter sesuai kriteria

## Skenario 7: Review Detail Periode

**Situasi**: Anda ingin melihat informasi lengkap tentang suatu periode.

**Langkah-langkah**:

1. Klik menu **Periode Fiskal** di sidebar
2. Klik periode yang ingin dilihat
3. Di halaman detail, Anda dapat melihat:
   - Nama periode (contoh: November 2025)
   - Tanggal awal dan akhir
   - Status saat ini
   - Tanggal penutupan (jika sudah ditutup)
   - Catatan penutupan
   - Tanggal pelaporan SPT (jika sudah dilaporkan)

## Tips

1. **Tutup secara berurutan** - Tutup periode dari bulan terlama ke terbaru
2. **Validasi dulu** - Cek laporan keuangan sebelum menutup periode
3. **Catat alasan** - Selalu isi catatan saat menutup atau membuka kembali periode
4. **Jangan terburu-buru** - Tutup periode setelah yakin semua transaksi lengkap
5. **Backup sebelum tutup** - Lakukan backup sebelum menutup periode penting

## Troubleshooting

| Masalah | Solusi |
|---------|--------|
| Tidak bisa menutup periode | Pastikan periode sebelumnya sudah ditutup |
| Tidak bisa membuka kembali | Periode sudah TAX_FILED, tidak bisa diubah |
| Periode sudah ada | Gunakan periode yang sudah dibuat, tidak perlu buat baru |
| Transaksi ditolak | Cek status periode, mungkin sudah CLOSED |

## Lihat Juga

- [Laporan Tahunan](22-laporan-tahunan.md) - Proses tutup buku akhir tahun
- [Kalender Pajak](33-kalender-pajak.md) - Tracking deadline pajak bulanan
- [Laporan Pajak](32-laporan-pajak.md) - Persiapan SPT Masa
