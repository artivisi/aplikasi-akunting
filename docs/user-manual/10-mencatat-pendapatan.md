# Mencatat Pendapatan

## Kapan Anda Membutuhkan Ini

Gunakan panduan ini setiap kali Anda **menerima uang**, misalnya:
- Klien membayar invoice atau tagihan
- Menerima pembayaran dari penjualan barang/jasa
- Menerima bunga dari bank
- Menerima pelunasan piutang dari klien yang sebelumnya berhutang

## Konsep Sederhana

**Pendapatan = Uang yang masuk ke bisnis Anda.**

Setiap kali Anda menerima uang, ada dua hal yang terjadi:
1. Saldo bank/kas Anda **bertambah** (uang masuk ke rekening)
2. Pendapatan bisnis Anda **bertambah** (keuntungan bertambah)

Aplikasi akan mencatat kedua hal ini secara otomatis ketika Anda mengisi form transaksi.

---

## Skenario 1: Terima Pembayaran dari Klien (Paling Umum)

**Situasi**: Klien Anda (misalnya PT ABC) membayar invoice jasa konsultasi sebesar Rp 5.000.000 via transfer ke rekening BCA Anda.

### Langkah 1: Buka Menu Transaksi

Di sidebar kiri, klik menu **Transaksi**. Anda akan melihat daftar semua transaksi yang pernah dicatat.

![Daftar Transaksi](screenshots/transactions-list.png)

> **Tip**: Di halaman ini Anda bisa melihat transaksi sebelumnya, mencari transaksi tertentu, atau filter berdasarkan tanggal.

### Langkah 2: Klik Tombol "Transaksi Baru"

Di bagian atas halaman, cari dan klik tombol biru **Transaksi Baru**. Form pencatatan transaksi akan muncul.

![Form Transaksi Baru](screenshots/transactions-form.png)

### Langkah 3: Pilih Template "Pendapatan Jasa"

Di bagian atas form, ada dropdown **Template**. Klik dropdown tersebut dan pilih **Pendapatan Jasa**.

> **Apa itu Template?** Template adalah format siap pakai yang sudah diatur sebelumnya. Dengan memilih template, Anda tidak perlu mengisi semua field secara manual - aplikasi akan otomatis memilih akun yang tepat.

### Langkah 4: Isi Form Transaksi

Isi field-field berikut:

| Field | Apa yang Diisi | Contoh |
|-------|----------------|--------|
| **Tanggal** | Tanggal uang masuk ke rekening Anda | `15 November 2025` |
| **Jumlah** | Berapa rupiah yang diterima (tanpa titik/koma) | `5000000` |
| **Akun Sumber** | Rekening mana yang menerima uang | `Bank BCA` |
| **Keterangan** | Catatan untuk referensi nanti | `Pembayaran invoice INV-2025-001 dari PT ABC` |
| **No. Referensi** | Nomor invoice atau bukti transfer | `INV-2025-001` |

> **Penting**: Untuk jumlah, ketik angka saja tanpa titik atau koma. Jadi Rp 5.000.000 diketik sebagai `5000000`.

### Langkah 5: Periksa Preview Jurnal

Di bagian bawah form, ada **Preview Jurnal** yang menunjukkan apa yang akan dicatat:

```
Debit  : Bank BCA           Rp 5.000.000  (saldo bank bertambah)
Kredit : Pendapatan Jasa    Rp 5.000.000  (pendapatan bertambah)
```

> **Cara Membaca Preview**:
> - **Debit Bank BCA** = Uang di rekening BCA Anda bertambah Rp 5 juta
> - **Kredit Pendapatan Jasa** = Pendapatan bisnis Anda bertambah Rp 5 juta

Pastikan angka dan akun sudah benar sebelum lanjut.

### Langkah 6: Simpan dan Posting

Klik tombol **Simpan & Posting** untuk menyimpan transaksi.

> **Perbedaan Simpan vs Simpan & Posting**:
> - **Simpan** = Menyimpan sebagai draft (bisa diedit nanti)
> - **Simpan & Posting** = Menyimpan dan langsung mencatat ke buku besar (tidak bisa diedit)

### Langkah 7: Verifikasi Transaksi Berhasil

Setelah berhasil, Anda akan melihat halaman detail transaksi.

![Detail Transaksi](screenshots/transactions-detail.png)

**Cara mengecek transaksi sudah benar:**
- Status menunjukkan **Posted** (bukan Draft)
- Jumlah sesuai dengan yang Anda masukkan
- Jurnal menunjukkan akun yang benar

---

## Skenario 2: Terima Pembayaran dengan PPN

**Situasi**: Klien membayar Rp 11.100.000 yang sudah termasuk PPN 11%.

**Perhitungan**:
- Total diterima: Rp 11.100.000
- PPN (11%): Rp 1.100.000
- Pendapatan bersih: Rp 10.000.000

### Langkah-langkah

1. Klik menu **Transaksi** di sidebar
2. Klik tombol **Transaksi Baru**
3. Pilih template **Pendapatan Jasa dengan PPN**
4. Isi form:
   - **Tanggal**: Tanggal uang diterima
   - **Jumlah**: `11100000` (ketik jumlah total yang diterima)
   - **Akun Sumber**: Bank BCA
   - **Keterangan**: `Pembayaran invoice INV-2025-002 dari PT XYZ - termasuk PPN`
5. Periksa **Preview Jurnal**:
   ```
   Debit  : Bank BCA           Rp 11.100.000  (uang yang diterima)
   Kredit : Hutang PPN         Rp  1.100.000  (PPN yang harus disetor ke negara)
   Kredit : Pendapatan Jasa    Rp 10.000.000  (pendapatan bersih)
   ```
6. Klik **Simpan & Posting**

> **Catatan tentang PPN**: PPN yang Anda terima dari klien bukan milik Anda - itu adalah titipan yang harus Anda setor ke negara. Makanya dicatat sebagai "Hutang PPN".

---

## Skenario 3: Terima DP (Uang Muka) Proyek

**Situasi**: Klien membayar DP 30% untuk proyek senilai Rp 50.000.000. Jadi Anda menerima Rp 15.000.000.

> **Penting**: DP belum boleh dicatat sebagai pendapatan karena pekerjaan belum selesai. DP dicatat sebagai "uang muka yang harus dikerjakan".

### Langkah-langkah

1. Klik menu **Transaksi** di sidebar
2. Klik tombol **Transaksi Baru**
3. Pilih template **Terima DP Proyek**
4. Isi form:
   - **Tanggal**: Tanggal uang diterima
   - **Jumlah**: `15000000`
   - **Akun Sumber**: Bank BCA
   - **Proyek**: Pilih proyek terkait dari dropdown
   - **Keterangan**: `DP 30% Proyek Website PT ABC`
5. Periksa **Preview Jurnal**:
   ```
   Debit  : Bank BCA                    Rp 15.000.000  (uang masuk)
   Kredit : Pendapatan Diterima Dimuka  Rp 15.000.000  (kewajiban)
   ```
6. Klik **Simpan & Posting**

> **Kapan DP diakui sebagai pendapatan?** Saat milestone proyek selesai. Lihat [Tracking Proyek](41-tracking-proyek.md).

---

## Skenario 4: Terima Bunga dari Bank

**Situasi**: Bank memberikan bunga deposito Rp 250.000.

### Langkah-langkah

1. Klik menu **Transaksi** di sidebar
2. Klik tombol **Transaksi Baru**
3. Pilih template **Pendapatan Bunga**
4. Isi form:
   - **Tanggal**: Tanggal bunga dikreditkan ke rekening
   - **Jumlah**: `250000`
   - **Akun Sumber**: Bank BCA (atau rekening yang menerima bunga)
   - **Keterangan**: `Bunga deposito November 2025`
5. Klik **Simpan & Posting**

---

## Skenario 5: Terima Pelunasan dari Klien yang Berhutang

**Situasi**: Klien yang sebelumnya berhutang (Anda sudah mencatat piutang) melunasi hutangnya Rp 8.000.000.

### Langkah-langkah

1. Klik menu **Transaksi** di sidebar
2. Klik tombol **Transaksi Baru**
3. Pilih template **Terima Pelunasan Piutang**
4. Isi form:
   - **Tanggal**: Tanggal uang diterima
   - **Jumlah**: `8000000`
   - **Akun Sumber**: Bank BCA
   - **Keterangan**: `Pelunasan piutang PT ABC`
5. Periksa **Preview Jurnal**:
   ```
   Debit  : Bank BCA        Rp 8.000.000  (uang masuk)
   Kredit : Piutang Usaha   Rp 8.000.000  (piutang berkurang)
   ```
6. Klik **Simpan & Posting**

> **Catatan**: Transaksi ini tidak menambah pendapatan karena pendapatan sudah dicatat saat piutang dibuat.

---

## Tips Praktis

1. **Catat segera** - Jangan menunda mencatat transaksi. Catat segera setelah uang diterima agar tidak lupa.

2. **Selalu isi referensi** - Kolom No. Referensi sangat berguna untuk mencari transaksi nanti. Isi dengan nomor invoice, nomor transfer, atau nomor bukti lainnya.

3. **Pilih proyek jika ada** - Jika pendapatan terkait proyek klien tertentu, pilih proyeknya. Ini berguna untuk menghitung profit per proyek.

4. **Periksa preview sebelum posting** - Setelah posting, transaksi tidak bisa diedit. Pastikan angka dan akun sudah benar.

5. **Cek saldo setelah posting** - Buka Dashboard untuk memastikan saldo bank sudah bertambah.

---

## Troubleshooting

| Masalah | Penyebab | Solusi |
|---------|----------|--------|
| Template tidak muncul di dropdown | Template tidak aktif | Buka menu Template, cari template, dan aktifkan |
| Saldo bank tidak berubah | Transaksi masih Draft | Buka transaksi, klik Posting |
| Salah isi jumlah/akun | Transaksi sudah Posted | Void transaksi, buat transaksi baru yang benar |
| PPN tidak terhitung | Menggunakan template tanpa PPN | Gunakan template dengan PPN |

---

## Lihat Juga

- [Transaksi PPN](30-transaksi-ppn.md) - Detail tentang pendapatan dengan PPN
- [Tracking Proyek](41-tracking-proyek.md) - Menghubungkan pendapatan ke proyek
- [Invoice & Penagihan](42-invoice-penagihan.md) - Membuat invoice untuk klien
- [Laporan Harian](20-laporan-harian.md) - Mengecek transaksi yang sudah dicatat
