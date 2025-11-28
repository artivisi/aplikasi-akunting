# Laporan Bulanan

## Kapan Anda Membutuhkan Ini

Gunakan panduan ini ketika Anda:
- Ingin melihat ringkasan keuangan bulanan
- Perlu membuat laporan untuk manajemen atau investor
- Menyiapkan data untuk laporan pajak bulanan
- Menganalisis performa bisnis bulan ini vs bulan lalu

## Konsep yang Perlu Dipahami

Ada empat laporan keuangan utama:

| Laporan | Fungsi | Tipe |
|---------|--------|------|
| **Neraca Saldo** | Daftar saldo semua akun | Titik waktu |
| **Neraca** | Posisi keuangan (aset, kewajiban, ekuitas) | Titik waktu |
| **Laba Rugi** | Pendapatan vs beban | Periode |
| **Arus Kas** | Aliran kas masuk dan keluar | Periode |

**Titik waktu** = kondisi pada tanggal tertentu
**Periode** = akumulasi selama rentang waktu

## Skenario 1: Cetak Neraca Saldo Akhir Bulan

**Situasi**: Akhir bulan, Anda ingin memvalidasi bahwa pembukuan balance (debit = kredit).

**Langkah-langkah**:

1. Klik menu **Laporan** di sidebar
2. Pilih **Neraca Saldo**
3. Pilih **Tanggal**: Tanggal akhir bulan (contoh: 30 November 2025)
4. Klik **Tampilkan**
5. Periksa laporan:
   - **Total Debit** harus sama dengan **Total Kredit**
   - Jika berbeda, ada kesalahan yang perlu ditelusuri

**Memahami Neraca Saldo**:

| Kolom | Arti |
|-------|------|
| Kode Akun | Nomor akun di bagan akun |
| Nama Akun | Nama lengkap akun |
| Debit | Total saldo debit akun |
| Kredit | Total saldo kredit akun |

**Cara Ekspor**:

1. Klik tombol **Ekspor PDF** untuk dokumentasi resmi
2. Klik tombol **Ekspor Excel** untuk analisis lebih lanjut

## Skenario 2: Cetak Laporan Laba Rugi Bulanan

**Situasi**: Anda ingin mengetahui apakah bisnis untung atau rugi bulan ini.

**Langkah-langkah**:

1. Klik menu **Laporan** di sidebar
2. Pilih **Laba Rugi**
3. Pilih periode:
   - **Tanggal Awal**: 1 November 2025
   - **Tanggal Akhir**: 30 November 2025
4. Klik **Tampilkan**
5. Review laporan:
   - **Pendapatan**: Total penerimaan dari operasional
   - **Beban**: Total pengeluaran operasional
   - **Laba/Rugi Bersih**: Pendapatan - Beban

**Memahami Laba Rugi**:

```
Pendapatan Jasa               Rp 50.000.000
Pendapatan Lainnya            Rp  2.000.000
─────────────────────────────────────────
Total Pendapatan              Rp 52.000.000

Beban Gaji                    Rp 15.000.000
Beban Sewa                    Rp  5.000.000
Beban Utilitas                Rp  2.000.000
Beban Operasional Lain        Rp  3.000.000
─────────────────────────────────────────
Total Beban                   Rp 25.000.000

LABA BERSIH                   Rp 27.000.000
```

## Skenario 3: Cetak Neraca (Balance Sheet)

**Situasi**: Anda ingin mengetahui posisi keuangan perusahaan saat ini.

**Langkah-langkah**:

1. Klik menu **Laporan** di sidebar
2. Pilih **Neraca**
3. Pilih **Tanggal**: Tanggal akhir bulan
4. Klik **Tampilkan**
5. Periksa persamaan akuntansi:
   ```
   Aset = Kewajiban + Ekuitas
   ```

**Struktur Neraca**:

```
ASET
─────────────────────────────────────────
Aset Lancar
  Kas & Bank                  Rp 80.000.000
  Piutang Usaha               Rp 25.000.000
  Beban Dibayar Dimuka        Rp 10.000.000

Aset Tetap
  Peralatan                   Rp 50.000.000
  Akum. Penyusutan           (Rp 10.000.000)

Total Aset                   Rp 155.000.000

KEWAJIBAN
─────────────────────────────────────────
Kewajiban Lancar
  Hutang Usaha                Rp 15.000.000
  Hutang Pajak                Rp  5.000.000

Total Kewajiban               Rp 20.000.000

EKUITAS
─────────────────────────────────────────
Modal                        Rp 100.000.000
Laba Ditahan                  Rp  8.000.000
Laba Tahun Berjalan          Rp 27.000.000

Total Ekuitas                Rp 135.000.000

TOTAL KEWAJIBAN + EKUITAS    Rp 155.000.000
```

## Skenario 4: Cetak Laporan Arus Kas

**Situasi**: Anda ingin mengetahui dari mana uang masuk dan ke mana uang keluar selama bulan ini.

**Langkah-langkah**:

1. Klik menu **Laporan** di sidebar
2. Pilih **Arus Kas**
3. Pilih periode:
   - **Tanggal Awal**: 1 November 2025
   - **Tanggal Akhir**: 30 November 2025
4. Klik **Tampilkan**
5. Review laporan yang terbagi dalam tiga bagian:
   - **Aktivitas Operasi**: Kas dari kegiatan utama bisnis
   - **Aktivitas Investasi**: Kas dari pembelian/penjualan aset
   - **Aktivitas Pendanaan**: Kas dari modal/pinjaman

**Memahami Arus Kas**:

```
ARUS KAS DARI AKTIVITAS OPERASI
─────────────────────────────────────────
Penerimaan Jasa                Rp 50.000.000
Pembayaran Gaji               (Rp 15.000.000)
Pembayaran Sewa               (Rp  5.000.000)
Pembayaran Operasional        (Rp  5.000.000)
─────────────────────────────────────────
Arus Kas Bersih dari Operasi   Rp 25.000.000

ARUS KAS DARI AKTIVITAS INVESTASI
─────────────────────────────────────────
Pembelian Peralatan           (Rp 10.000.000)
─────────────────────────────────────────
Arus Kas Bersih dari Investasi (Rp 10.000.000)

ARUS KAS DARI AKTIVITAS PENDANAAN
─────────────────────────────────────────
Setoran Modal                  Rp 20.000.000
─────────────────────────────────────────
Arus Kas Bersih dari Pendanaan Rp 20.000.000

RINGKASAN
─────────────────────────────────────────
Kenaikan Bersih Kas            Rp 35.000.000
Saldo Kas Awal                 Rp 45.000.000
SALDO KAS AKHIR                Rp 80.000.000
```

**Rekonsiliasi dengan Neraca**:

Laporan arus kas menampilkan rekonsiliasi saldo kas akhir dengan total saldo akun kas/bank di neraca. Keduanya harus sama.

**Cara Ekspor**:

1. Klik tombol **PDF** untuk dokumentasi resmi
2. Klik tombol **Excel** untuk analisis lebih lanjut
3. Klik tombol **Cetak** untuk print langsung

## Skenario 5: Bandingkan Performa Bulan Ini vs Bulan Lalu

**Situasi**: Anda ingin menganalisis tren performa bisnis.

**Langkah-langkah**:

1. Cetak Laba Rugi bulan ini (lihat Skenario 2)
2. Ekspor ke Excel
3. Cetak Laba Rugi bulan lalu dengan cara yang sama
4. Ekspor ke Excel
5. Buat perbandingan di Excel:
   - Selisih nominal
   - Persentase perubahan
   - Tren naik/turun

**Alternatif - Gunakan Dashboard**:

1. Buka **Dashboard**
2. Lihat indikator perubahan di setiap kartu KPI
3. Warna hijau = naik, merah = turun
4. Persentase perubahan ditampilkan otomatis

## Skenario 6: Siapkan Laporan untuk Manajemen

**Situasi**: Anda perlu menyiapkan laporan bulanan untuk meeting dengan direktur.

**Langkah-langkah**:

1. Cetak keempat laporan untuk periode yang sama:
   - Neraca Saldo → untuk validasi
   - Laba Rugi → untuk performa
   - Neraca → untuk posisi keuangan
   - Arus Kas → untuk aliran kas
2. Ekspor semua ke PDF
3. Siapkan ringkasan:
   - Highlight pendapatan dan laba
   - Jelaskan perubahan signifikan dari bulan lalu
   - Identifikasi tren yang perlu diperhatikan
   - Jelaskan posisi kas dan pergerakannya

## Skenario 7: Persiapan Laporan Pajak Bulanan

**Situasi**: Akhir bulan, Anda perlu data untuk SPT Masa PPN dan PPh.

**Langkah-langkah**:

1. Untuk PPN: Lihat [Laporan Pajak](32-laporan-pajak.md)
2. Untuk PPh: Lihat [Laporan Pajak](32-laporan-pajak.md)
3. Untuk rekonsiliasi, cetak juga:
   - Neraca Saldo → cek saldo akun hutang pajak
   - Buku Besar → detail mutasi akun pajak

## Tips

1. **Jadwalkan rutin** - Cetak laporan di tanggal yang sama setiap bulan
2. **Validasi dulu** - Cek Neraca Saldo balance sebelum cetak laporan lain
3. **Arsipkan** - Simpan PDF laporan bulanan untuk dokumentasi
4. **Analisis tren** - Bandingkan dengan periode sebelumnya
5. **Closing** - Pastikan semua transaksi bulan tersebut sudah posted

## Troubleshooting

| Masalah | Solusi |
|---------|--------|
| Neraca tidak balance | Telusuri akun dengan saldo tidak wajar |
| Laba berbeda dari ekspektasi | Periksa apakah ada transaksi yang tidak diposting |
| Data tidak lengkap | Pastikan periode yang dipilih sudah benar |

## Lihat Juga

- [Laporan Harian](20-laporan-harian.md) - Monitoring transaksi harian
- [Laporan Tahunan](22-laporan-tahunan.md) - Tutup buku akhir tahun
- [Laporan Pajak](32-laporan-pajak.md) - Laporan untuk pelaporan pajak
