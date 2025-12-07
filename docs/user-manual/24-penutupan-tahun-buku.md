# Penutupan Tahun Buku

Penutupan Tahun Buku (Fiscal Year Closing) adalah proses akhir tahun untuk menutup akun pendapatan dan beban, serta memindahkan laba/rugi ke akun Laba Ditahan.

## Mengakses Penutupan

1. Buka menu **Laporan**
2. Pada bagian **Aset Tetap & Penutupan**, klik **Penutupan Tahun Buku**

![Halaman Penutupan Tahun Buku](screenshots/reports-fiscal-closing.png)

## Proses Penutupan

Sistem akan membuat 3 jurnal penutup secara otomatis:

### 1. Tutup Pendapatan
```
Dr. Pendapatan Jasa Training     xxx
Dr. Pendapatan Jasa Konsultasi   xxx
    Cr. Laba Berjalan                xxx
```

### 2. Tutup Beban
```
Dr. Laba Berjalan                xxx
    Cr. Beban Gaji                   xxx
    Cr. Beban Listrik                xxx
    Cr. Beban Penyusutan             xxx
```

### 3. Transfer ke Laba Ditahan

Jika **Laba**:
```
Dr. Laba Berjalan               xxx
    Cr. Laba Ditahan                xxx
```

Jika **Rugi**:
```
Dr. Laba Ditahan                xxx
    Cr. Laba Berjalan               xxx
```

## Cara Menggunakan

### Preview

Sebelum eksekusi, sistem menampilkan:
- Total Pendapatan tahun tersebut
- Total Beban tahun tersebut
- Laba/Rugi Bersih
- Preview jurnal yang akan dibuat

![Preview Jurnal Penutupan](screenshots/reports-fiscal-closing.png)

### Eksekusi Penutupan

1. Pilih tahun yang akan ditutup
2. Review preview jurnal penutup
3. Klik **Eksekusi Penutupan**
4. Konfirmasi dengan klik OK

![Konfirmasi Eksekusi Penutupan](screenshots/reports-fiscal-closing.png)

### Status

- **Belum ditutup**: Tahun masih terbuka, bisa dieksekusi
- **Sudah ditutup**: Jurnal penutup sudah dibuat

## Pembatalan Penutupan

Jika terjadi kesalahan, penutupan bisa dibatalkan:

1. Klik **Batalkan Penutupan**

![Pembatalan Penutupan](screenshots/reports-fiscal-closing.png)

2. Isi alasan pembatalan
3. Klik **Batalkan Penutupan**

Jurnal penutup akan di-void dan status tahun kembali terbuka.

## Kapan Melakukan Penutupan

Lakukan penutupan setelah:
- Semua transaksi tahun tersebut sudah di-input

![Verifikasi Transaksi](screenshots/transactions-list.png)

- Penyesuaian akhir tahun sudah dilakukan
- Penyusutan aset sudah dihitung

![Laporan Penyusutan](screenshots/reports-depreciation.png)

- Laporan keuangan sudah final

![Laporan Laba Rugi](screenshots/reports-income-statement.png)

## Akun yang Digunakan

| Kode | Nama | Keterangan |
|------|------|------------|
| 3.2.02 | Laba Berjalan | Penampung sementara laba/rugi |
| 3.2.01 | Laba Ditahan | Akumulasi laba tahun-tahun sebelumnya |

## Tips

- Lakukan penutupan setelah laporan keuangan sudah fix
- Backup data sebelum melakukan penutupan
- Setelah penutupan, saldo pendapatan dan beban menjadi nol untuk tahun berikutnya
