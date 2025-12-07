# Kalender Pajak

## Kapan Anda Membutuhkan Ini

Gunakan panduan ini ketika Anda:
- Ingin melihat deadline pajak untuk bulan tertentu
- Perlu tracking kewajiban pajak mana yang sudah selesai
- Ingin memastikan tidak ada pajak yang terlambat
- Mencari overview status pajak selama satu tahun

## Konsep yang Perlu Dipahami

### Kewajiban Pajak Bulanan

| Kewajiban | Batas Waktu | Keterangan |
|-----------|-------------|------------|
| Setor PPh 21 | Tanggal 10 | Pajak penghasilan karyawan |
| Setor PPh 23 | Tanggal 10 | Pajak atas jasa |
| Setor PPh 4(2) | Tanggal 10 | Pajak final (sewa, konstruksi) |
| Setor PPh 25 | Tanggal 15 | Angsuran pajak tahunan |
| Setor PPN | Tanggal 15 | Pajak pertambahan nilai |
| Lapor SPT PPh 21/23 | Tanggal 20 | Pelaporan SPT Masa |
| Lapor SPT PPN | Akhir bulan | Pelaporan SPT Masa PPN |

### Status Kewajiban

| Status | Warna | Arti |
|--------|-------|------|
| **Selesai** | Hijau | Sudah disetor/dilaporkan |
| **Mendekati** | Kuning | Deadline dalam 7 hari |
| **Terlambat** | Merah | Melewati deadline |
| **Belum** | Abu-abu | Belum jatuh tempo |

## Skenario 1: Lihat Checklist Pajak Bulan Ini

**Situasi**: Awal bulan, Anda ingin melihat kewajiban pajak untuk masa pajak bulan lalu.

**Langkah-langkah**:

1. Klik menu **Kalender Pajak** di sidebar

![Kalender Pajak](screenshots/tax-calendar.png)

2. Pilih periode:
   - **Tahun**: 2025
   - **Bulan**: Oktober (masa pajak)
3. Lihat checklist dengan status masing-masing:
   - Hijau = Sudah selesai
   - Merah = Terlambat
   - Abu-abu = Belum selesai

**Informasi yang Ditampilkan**:

```
CHECKLIST PAJAK - Oktober 2025

☑ Setor PPh 21         Batas: 10 Nov 2025    [SELESAI]
☑ Setor PPh 23         Batas: 10 Nov 2025    [SELESAI]
☐ Setor PPh 4(2)       Batas: 10 Nov 2025    [BELUM]
☐ Setor PPh 25         Batas: 15 Nov 2025    [MENDEKATI]
☑ Setor PPN            Batas: 15 Nov 2025    [SELESAI]
☐ Lapor SPT PPh 21/23  Batas: 20 Nov 2025    [BELUM]
☐ Lapor SPT PPN        Batas: 30 Nov 2025    [BELUM]
```

## Skenario 2: Tandai Kewajiban Selesai

**Situasi**: Anda sudah menyetor PPh 21 dan ingin menandai di sistem.

**Langkah-langkah**:

1. Buka **Kalender Pajak** untuk masa pajak yang sesuai

![Kalender Pajak](screenshots/tax-calendar.png)

2. Cari item "Setor PPh 21"
3. Klik tombol **Tandai Selesai**
4. Isi informasi:
   - **Tanggal Selesai**: Tanggal penyetoran/pelaporan
   - **Nomor Referensi**: NTPN / Nomor tanda terima
   - **Catatan**: (opsional) Informasi tambahan
5. Klik **Simpan**

**Setelah Ditandai**:
- Status berubah menjadi hijau (SELESAI)
- Tanggal dan referensi tercatat
- Progress checklist terupdate

## Skenario 3: Batalkan Status Selesai

**Situasi**: Anda salah menandai kewajiban sebagai selesai.

**Langkah-langkah**:

1. Buka **Kalender Pajak** untuk masa pajak yang sesuai

![Kalender Pajak](screenshots/tax-calendar.png)

2. Cari item yang ingin dibatalkan
3. Klik tombol **Batalkan**
4. Konfirmasi pembatalan

**Catatan**: Status akan kembali ke belum selesai.

## Skenario 4: Lihat Overview Tahunan

**Situasi**: Anda ingin melihat status pajak selama satu tahun penuh.

**Langkah-langkah**:

1. Klik menu **Kalender Pajak** di sidebar
2. Klik tab **Tahunan** atau link **Lihat Tahunan**
3. Pilih tahun (contoh: 2025)

![Kalender Pajak Tahunan](screenshots/tax-calendar-yearly.png)

4. Lihat grid 12 bulan dengan status:

**Tampilan Overview**:

```
            Jan  Feb  Mar  Apr  Mei  Jun  Jul  Agu  Sep  Okt  Nov  Des
PPh 21      ✓    ✓    ✓    ✓    ✓    ✓    ✓    ✓    ✓    ○    -    -
PPh 23      ✓    ✓    ✓    ✓    ✓    ✓    ✓    ✓    ✓    ○    -    -
PPh 4(2)    ✓    ✓    ✓    ✓    ✓    ✓    ✓    ✓    ✓    -    -    -
PPh 25      ✓    ✓    ✓    ✓    ✓    ✓    ✓    ✓    ✓    ○    -    -
PPN         ✓    ✓    ✓    ✓    ✓    ✓    ✓    ✓    ✓    ✓    -    -
SPT PPh     ✓    ✓    ✓    ✓    ✓    ✓    ✓    ✓    ✓    -    -    -
SPT PPN     ✓    ✓    ✓    ✓    ✓    ✓    ✓    ✓    ✓    -    -    -

Keterangan: ✓ Selesai | ○ Mendekati/Terlambat | - Belum
```

## Skenario 5: Cek Dashboard Widget

**Situasi**: Anda ingin quick view status pajak dari dashboard.

**Langkah-langkah**:

1. Buka **Dashboard** (halaman utama)

![Dashboard](screenshots/dashboard.png)

2. Lihat widget **Kalender Pajak** yang menampilkan:
   - Jumlah deadline yang terlambat (merah)
   - Jumlah deadline yang mendekati (kuning)
3. Klik widget untuk membuka detail

**Widget menampilkan**:

```
KALENDER PAJAK
─────────────────────
⚠ 2 Terlambat
⏰ 3 Mendekati (7 hari)

[Lihat Detail →]
```

## Skenario 6: Lihat Deadline Terdekat

**Situasi**: Anda ingin fokus pada deadline yang paling urgent.

**Langkah-langkah**:

1. Klik menu **Kalender Pajak** di sidebar

![Kalender Pajak](screenshots/tax-calendar.png)

2. Klik tab **Mendatang**
3. Lihat daftar deadline yang diurutkan berdasarkan tanggal:
   - **Terlambat**: Yang sudah melewati deadline
   - **Mendekati**: Yang jatuh tempo dalam 7 hari
   - **Akan Datang**: Yang jatuh tempo lebih dari 7 hari

## Tips

1. **Cek rutin** - Buka kalender pajak setiap awal bulan
2. **Tandai segera** - Update status begitu selesai setor/lapor
3. **Simpan referensi** - Selalu catat NTPN/nomor tanda terima
4. **Dashboard** - Manfaatkan widget untuk quick check harian
5. **Review tahunan** - Cek overview tahunan untuk memastikan lengkap

## Troubleshooting

| Masalah | Solusi |
|---------|--------|
| Deadline tidak muncul | Cek apakah tahun dan bulan sudah benar |
| Status tidak update | Refresh halaman atau cek koneksi |
| Tidak bisa tandai selesai | Pastikan tanggal selesai tidak kosong |
| Widget tidak muncul | Widget ada di halaman Dashboard utama |

## Lihat Juga

- [Laporan Pajak](32-laporan-pajak.md) - Cetak laporan untuk persiapan SPT
- [Transaksi PPN](30-transaksi-ppn.md) - Catat transaksi dengan PPN
- [Transaksi PPh](31-transaksi-pph.md) - Catat pemotongan dan penyetoran PPh
- [Kelola Periode Fiskal](54-kelola-periode-fiskal.md) - Tutup buku dan tandai SPT dilaporkan
