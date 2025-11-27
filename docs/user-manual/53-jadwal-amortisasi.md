# Jadwal Amortisasi

## Kapan Anda Membutuhkan Ini

Gunakan panduan ini ketika Anda:
- Membayar sewa kantor atau asuransi untuk periode panjang
- Menerima pembayaran dimuka dari klien (retainer)
- Memiliki aset tak berwujud yang perlu diamortisasi
- Ingin mengalokasikan biaya/pendapatan secara merata ke beberapa periode

## Konsep yang Perlu Dipahami

### Apa Itu Amortisasi?

Amortisasi adalah proses mengalokasikan biaya atau pendapatan secara merata ke beberapa periode akuntansi.

**Kenapa Perlu Amortisasi?**

Tanpa amortisasi:
```
Januari: Bayar sewa Rp 24.000.000 (12 bulan)
         Beban Sewa: Rp 24.000.000 (semua di Januari)
Februari-Desember: Beban Sewa: Rp 0
```

Dengan amortisasi:
```
Januari: Bayar sewa Rp 24.000.000
         Sewa Dibayar Dimuka: Rp 24.000.000
Setiap bulan: Beban Sewa: Rp 2.000.000
```

**Hasil**: Laporan laba rugi lebih akurat karena beban diakui sesuai periode manfaatnya.

### Tipe Jadwal Amortisasi

| Tipe | Nama Indonesia | Contoh | Pola Jurnal |
|------|----------------|--------|-------------|
| `prepaid_expense` | Beban Dibayar Dimuka | Sewa, asuransi | Dr. Beban / Cr. Dibayar Dimuka |
| `unearned_revenue` | Pendapatan Diterima Dimuka | Retainer, DP | Dr. Diterima Dimuka / Cr. Pendapatan |
| `intangible_asset` | Aset Tak Berwujud | Website, software | Dr. Beban Amortisasi / Cr. Akum. Amortisasi |

### Status Jadwal

| Status | Arti |
|--------|------|
| **Active** | Jadwal berjalan, masih ada entri yang akan diproses |
| **Completed** | Semua periode sudah selesai |
| **Cancelled** | Jadwal dibatalkan |

## Skenario 1: Sewa Kantor Dibayar Dimuka

**Situasi**: Anda membayar sewa kantor Rp 24.000.000 untuk 12 bulan (Jan-Des 2025).

**Langkah 1: Catat Pembayaran**

1. Klik menu **Transaksi**
2. Buat transaksi dengan template **Bayar Sewa Dimuka**
3. Isi:
   - Jumlah: `24000000`
   - Keterangan: `Sewa kantor Jan-Des 2025`
4. Posting transaksi

**Langkah 2: Buat Jadwal Amortisasi**

1. Klik menu **Amortisasi** di sidebar
2. Klik tombol **Jadwal Baru**
3. Pilih **Tipe Jadwal**: `Beban Dibayar Dimuka`
4. Isi informasi:
   - **Nama**: `Sewa Kantor 2025`
   - **Keterangan**: `Sewa gedung kantor Jl. Sudirman`
   - **Akun Sumber**: `Sewa Dibayar Dimuka` (1.1.30)
   - **Akun Tujuan**: `Beban Sewa` (5.1.02)
   - **Jumlah Total**: `24000000`
   - **Tanggal Mulai**: 1 Januari 2025
   - **Tanggal Selesai**: 31 Desember 2025
   - **Frekuensi**: Bulanan
5. Review perhitungan:
   - **Jumlah per Periode**: Rp 2.000.000
   - **Jumlah Periode**: 12
6. Pilih opsi posting:
   - **Auto-Post**: Centang jika ingin otomatis
   - **Tanggal Posting**: 1 (tanggal 1 setiap bulan)
7. Klik **Simpan Jadwal**

**Hasil**: Setiap bulan akan tercipta jurnal:
```
Debit  : Beban Sewa            Rp 2.000.000
Kredit : Sewa Dibayar Dimuka   Rp 2.000.000
```

## Skenario 2: Asuransi Tahunan

**Situasi**: Membayar premi asuransi kantor Rp 6.000.000 untuk 12 bulan.

**Langkah-langkah**:

1. Catat pembayaran ke akun **Asuransi Dibayar Dimuka**
2. Buat jadwal amortisasi:
   - **Tipe**: Beban Dibayar Dimuka
   - **Nama**: `Asuransi Kantor 2025`
   - **Akun Sumber**: Asuransi Dibayar Dimuka
   - **Akun Tujuan**: Beban Asuransi
   - **Jumlah**: 6.000.000
   - **Periode**: 12 bulan
3. **Jumlah per Periode**: Rp 500.000

## Skenario 3: Pendapatan Diterima Dimuka (Retainer)

**Situasi**: Klien membayar retainer Rp 12.000.000 untuk maintenance 6 bulan.

**Langkah 1: Catat Penerimaan**

1. Buat transaksi **Terima DP Proyek** atau **Terima Retainer**
2. Jurnal:
   ```
   Debit  : Bank BCA                     Rp 12.000.000
   Kredit : Pendapatan Diterima Dimuka   Rp 12.000.000
   ```

**Langkah 2: Buat Jadwal Amortisasi**

1. Klik menu **Amortisasi**
2. Klik **Jadwal Baru**
3. Pilih **Tipe**: `Pendapatan Diterima Dimuka`
4. Isi:
   - **Nama**: `Retainer Maintenance PT ABC`
   - **Akun Sumber**: Pendapatan Diterima Dimuka
   - **Akun Tujuan**: Pendapatan Jasa
   - **Jumlah**: 12.000.000
   - **Periode**: 6 bulan
5. **Jumlah per Periode**: Rp 2.000.000

**Jurnal Bulanan**:
```
Debit  : Pendapatan Diterima Dimuka   Rp 2.000.000
Kredit : Pendapatan Jasa              Rp 2.000.000
```

## Skenario 4: Amortisasi Aset Tak Berwujud

**Situasi**: Anda mengembangkan website perusahaan senilai Rp 50.000.000, diamortisasi 5 tahun.

**Langkah-langkah**:

1. Catat pembuatan website ke akun **Aset Tak Berwujud - Website**
2. Buat jadwal amortisasi:
   - **Tipe**: Aset Tak Berwujud
   - **Nama**: `Website Perusahaan`
   - **Akun Sumber**: Akumulasi Amortisasi Website
   - **Akun Tujuan**: Beban Amortisasi
   - **Jumlah**: 50.000.000
   - **Periode**: 60 bulan (5 tahun)
3. **Jumlah per Periode**: Rp 833.333

**Jurnal Bulanan**:
```
Debit  : Beban Amortisasi                    Rp 833.333
Kredit : Akumulasi Amortisasi Website        Rp 833.333
```

## Skenario 5: Monitor Jadwal Amortisasi

**Situasi**: Anda ingin melihat status semua jadwal amortisasi.

**Langkah-langkah**:

1. Klik menu **Amortisasi** di sidebar
2. Lihat daftar jadwal:
   - **Status**: Active, Completed, Cancelled
   - **Progress**: Bar yang menunjukkan periode terselesaikan
   - **Sisa**: Jumlah yang belum diamortisasi
3. Filter berdasarkan tipe atau status jika perlu

## Skenario 6: Posting Manual Entri Amortisasi

**Situasi**: Anda tidak mengaktifkan auto-post dan perlu posting manual.

**Langkah-langkah**:

1. Klik menu **Amortisasi**
2. Klik jadwal yang bersangkutan
3. Di daftar entri, lihat status masing-masing:
   - **Pending**: Belum waktunya
   - **Draft**: Sudah jatuh tempo, menunggu posting
   - **Posted**: Sudah diposting
4. Untuk entri **Draft**, klik tombol **Post**
5. Konfirmasi posting

## Skenario 7: Lihat Detail Entri Amortisasi

**Situasi**: Anda ingin melihat entri mana yang sudah diproses.

**Langkah-langkah**:

1. Klik menu **Amortisasi**
2. Klik jadwal yang bersangkutan
3. Scroll ke bagian **Daftar Entri**:

```
DAFTAR ENTRI AMORTISASI
Sewa Kantor 2025

No   Periode         Jumlah      Status    Jurnal
1    Jan 2025        2.000.000   Posted    JRN-001
2    Feb 2025        2.000.000   Posted    JRN-025
3    Mar 2025        2.000.000   Posted    JRN-048
4    Apr 2025        2.000.000   Draft     -
5    May 2025        2.000.000   Pending   -
...
12   Dec 2025        2.000.000   Pending   -

Total: 24.000.000
Sudah Posted: 6.000.000
Sisa: 18.000.000
```

4. Klik nomor jurnal untuk melihat detail transaksi

## Skenario 8: Batalkan Jadwal Amortisasi

**Situasi**: Kontrak sewa dibatalkan di tengah periode.

**Langkah-langkah**:

1. Klik menu **Amortisasi**
2. Klik jadwal yang akan dibatalkan
3. Klik tombol **Batalkan**
4. Konfirmasi pembatalan
5. Status berubah menjadi **Cancelled**

**Catatan**:
- Entri yang sudah Posted tidak akan di-reverse otomatis
- Sisa saldo di akun sumber perlu di-adjust manual jika diperlukan

## Penanganan Pembulatan

Sistem menangani pembulatan otomatis:
```
Total: Rp 100.000 untuk 3 periode

Periode 1: Rp 33.333
Periode 2: Rp 33.333
Periode 3: Rp 33.334 (menyerap selisih)
```

## Tips

1. **Nama deskriptif** - Sertakan tahun atau periode dalam nama jadwal
2. **Auto-post untuk rutin** - Aktifkan untuk item yang tidak perlu review
3. **Manual untuk special** - Nonaktifkan auto-post jika perlu validasi
4. **Review bulanan** - Cek di awal bulan apakah entri terproses

## Troubleshooting

| Masalah | Solusi |
|---------|--------|
| Entri tidak terposting | Cek apakah tanggal sudah lewat dan auto-post aktif |
| Jumlah tidak sesuai | Cek periode mulai dan selesai |
| Akun tidak muncul | Pastikan akun yang dipilih aktif |

## Lihat Juga

- [Mencatat Pengeluaran](11-mencatat-pengeluaran.md) - Catat pembayaran dimuka
- [Mencatat Pendapatan](10-mencatat-pendapatan.md) - Catat penerimaan dimuka
- [Laporan Bulanan](21-laporan-bulanan.md) - Verifikasi beban yang diamortisasi
