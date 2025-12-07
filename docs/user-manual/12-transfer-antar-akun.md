# Transfer Antar Akun

## Kapan Anda Membutuhkan Ini

Gunakan panduan ini ketika Anda:
- Memindahkan uang dari rekening bank ke rekening lain
- Mengisi kas kecil dari rekening bank
- Menyetor uang tunai ke bank
- Transfer antar rekening bank yang berbeda

## Konsep Sederhana

**Transfer = Pindah uang dari satu tempat ke tempat lain.**

Bayangkan Anda punya dua dompet: dompet A dan dompet B. Jika Anda pindahkan uang Rp 100.000 dari dompet A ke dompet B:
- Dompet A **berkurang** Rp 100.000
- Dompet B **bertambah** Rp 100.000
- **Total uang Anda tetap sama** (tidak bertambah, tidak berkurang)

Dalam aplikasi, "dompet" ini disebut **akun**. Contoh akun:
- Kas Kecil (uang tunai di kantor)
- Bank BCA (rekening BCA)
- Bank Mandiri (rekening Mandiri)

---

## Skenario 1: Isi Kas Kecil dari Bank (Paling Umum)

**Situasi**: Anda menarik uang Rp 2.000.000 dari ATM Bank BCA untuk mengisi kas kecil kantor.

### Langkah 1: Buka Menu Transaksi

Di sidebar kiri, klik menu **Transaksi**. Anda akan melihat daftar semua transaksi yang pernah dicatat.

![Daftar Transaksi](screenshots/transactions-list.png)

### Langkah 2: Klik Tombol "Transaksi Baru"

Di bagian atas halaman, klik tombol biru **Transaksi Baru**. Form pencatatan transaksi akan muncul.

![Form Transaksi Baru](screenshots/transactions-form.png)

### Langkah 3: Pilih Template "Transfer Kas"

Di bagian atas form, ada dropdown **Template**. Klik dropdown tersebut dan pilih **Transfer Kas**.

> **Template ini khusus untuk pindah uang**, bukan untuk pengeluaran atau pemasukan.

### Langkah 4: Isi Form Transaksi

Isi field-field berikut:

| Field | Apa yang Diisi | Contoh |
|-------|----------------|--------|
| **Tanggal** | Tanggal Anda menarik uang | `20 November 2025` |
| **Jumlah** | Berapa rupiah yang dipindahkan (tanpa titik/koma) | `2000000` |
| **Dari Akun** | Dari mana uang diambil | `Bank BCA` |
| **Ke Akun** | Ke mana uang dipindahkan | `Kas Kecil` |
| **Keterangan** | Catatan untuk referensi | `Isi kas kecil dari ATM BCA` |
| **No. Referensi** | Nomor struk ATM (opsional) | `ATM-20251120-001` |

> **Cara Memilih Akun**:
> - **Dari Akun** = Sumber uang (rekening/kas yang BERKURANG)
> - **Ke Akun** = Tujuan uang (rekening/kas yang BERTAMBAH)

### Langkah 5: Periksa Preview Jurnal

Di bagian bawah form, ada **Preview Jurnal**:

```
Debit  : Kas Kecil   Rp 2.000.000  (saldo kas bertambah)
Kredit : Bank BCA    Rp 2.000.000  (saldo bank berkurang)
```

> **Cara Membaca Preview**:
> - **Debit Kas Kecil** = Uang di kas kecil bertambah Rp 2 juta
> - **Kredit Bank BCA** = Uang di rekening BCA berkurang Rp 2 juta
> - Total: Rp 0 (tidak ada uang masuk atau keluar dari bisnis)

### Langkah 6: Simpan dan Posting

Klik tombol **Simpan & Posting** untuk menyimpan transaksi.

### Langkah 7: Verifikasi

Cek Dashboard atau Buku Besar untuk memastikan:
- Saldo Bank BCA sudah berkurang Rp 2.000.000
- Saldo Kas Kecil sudah bertambah Rp 2.000.000
- **Total Kas & Bank** tetap sama

---

## Skenario 2: Setor Tunai ke Bank

**Situasi**: Anda menyetor uang tunai Rp 5.000.000 dari kas ke rekening Bank Mandiri.

### Langkah-langkah

1. Klik menu **Transaksi** di sidebar
2. Klik tombol **Transaksi Baru**
3. Pilih template **Transfer Kas**
4. Isi form:
   - **Tanggal**: Tanggal setoran
   - **Jumlah**: `5000000`
   - **Dari Akun**: Kas Kecil (karena uang tunai yang disetor)
   - **Ke Akun**: Bank Mandiri (rekening tujuan setoran)
   - **Keterangan**: `Setor tunai ke Bank Mandiri`
   - **No. Referensi**: Nomor slip setoran
5. Periksa **Preview Jurnal**:
   ```
   Debit  : Bank Mandiri   Rp 5.000.000  (saldo bank bertambah)
   Kredit : Kas Kecil      Rp 5.000.000  (uang tunai berkurang)
   ```
6. Klik **Simpan & Posting**

> **Ingat**: "Dari Akun" adalah sumber uang (yang berkurang), "Ke Akun" adalah tujuan (yang bertambah).

---

## Skenario 3: Transfer Antar Bank

**Situasi**: Transfer Rp 10.000.000 dari Bank BCA ke Bank Mandiri untuk operasional cabang.

### Langkah-langkah

1. Klik menu **Transaksi** di sidebar
2. Klik tombol **Transaksi Baru**
3. Pilih template **Transfer Kas**
4. Isi form:
   - **Tanggal**: Tanggal transfer
   - **Jumlah**: `10000000`
   - **Dari Akun**: Bank BCA
   - **Ke Akun**: Bank Mandiri
   - **Keterangan**: `Transfer operasional ke rekening Mandiri`
   - **No. Referensi**: Nomor referensi transfer dari mobile banking
5. Klik **Simpan & Posting**

> **Tips**: Simpan screenshot bukti transfer dari mobile banking sebagai lampiran.

---

## Skenario 4: Transfer dengan Biaya Admin Bank

**Situasi**: Transfer Rp 10.000.000 dari Bank BCA ke Bank Mandiri, dikenakan biaya transfer Rp 6.500.

**Mengapa harus dicatat terpisah?** Biaya admin adalah pengeluaran (beban), bukan sekadar pemindahan uang. Jadi perlu 2 transaksi:

### Transaksi 1: Transfer Pokok (Rp 10.000.000)

1. Buat transaksi **Transfer Kas**
2. Jumlah: `10000000`
3. Dari: Bank BCA, Ke: Bank Mandiri
4. Keterangan: `Transfer operasional`
5. Simpan & Posting

### Transaksi 2: Biaya Admin (Rp 6.500)

1. Buat transaksi baru
2. Pilih template **Beban Administrasi Bank**
3. Jumlah: `6500`
4. Akun Sumber: Bank BCA
5. Keterangan: `Biaya transfer antar bank`
6. Simpan & Posting

**Hasil di Buku Besar**:
| Akun | Debit | Kredit | Keterangan |
|------|-------|--------|------------|
| Bank Mandiri | Rp 10.000.000 | - | Saldo bertambah |
| Bank BCA | - | Rp 10.006.500 | Saldo berkurang (transfer + biaya) |
| Beban Admin Bank | Rp 6.500 | - | Biaya tercatat sebagai pengeluaran |

---

## Skenario 5: Pemindahan ke Deposito

**Situasi**: Menempatkan Rp 50.000.000 dari rekening giro ke deposito untuk mendapat bunga.

### Langkah-langkah

1. Klik menu **Transaksi** di sidebar
2. Klik tombol **Transaksi Baru**
3. Pilih template **Transfer Kas**
4. Isi form:
   - **Tanggal**: Tanggal penempatan deposito
   - **Jumlah**: `50000000`
   - **Dari Akun**: Bank BCA (Giro)
   - **Ke Akun**: Deposito BCA
   - **Keterangan**: `Penempatan deposito 3 bulan @5% p.a.`
   - **No. Referensi**: Nomor bilyet deposito
5. Klik **Simpan & Posting**

> **Catatan**: Pastikan akun "Deposito BCA" sudah ada di Bagan Akun. Jika belum, tambahkan dulu di menu **Akun**.

---

## Tips Praktis

1. **Catat tanggal yang tepat** - Gunakan tanggal saat uang benar-benar berpindah, bukan tanggal Anda mencatat.

2. **Simpan referensi** - Catat nomor struk ATM, slip setoran, atau referensi transfer untuk memudahkan rekonsiliasi.

3. **Pisahkan biaya admin** - Biaya administrasi bank adalah pengeluaran (beban), bukan transfer. Catat terpisah.

4. **Verifikasi segera** - Setelah mencatat transfer, cek saldo di aplikasi vs saldo aktual di mobile banking/buku tabungan.

5. **Untuk jumlah besar** - Cek dua kali sebelum posting. Kesalahan angka bisa membingungkan saat rekonsiliasi.

---

## Troubleshooting

| Masalah | Penyebab | Solusi |
|---------|----------|--------|
| Saldo tidak berubah | Transaksi masih Draft | Buka transaksi, klik **Posting** |
| Akun tidak muncul di dropdown | Akun tidak aktif atau bukan tipe Kas/Bank | Cek di menu **Akun**, pastikan aktif dan tipe benar |
| Saldo aplikasi tidak cocok dengan bank | Ada transaksi yang belum dicatat | Cek mutasi bank, catat yang terlewat |
| Selisih kecil (ribuan) | Biaya admin yang belum dicatat | Buat transaksi Beban Administrasi Bank |
| Transfer terbalik (Dari/Ke salah) | Salah pilih akun | Void transaksi, buat ulang dengan benar |

---

## Lihat Juga

- [Mencatat Pengeluaran](11-mencatat-pengeluaran.md) - Untuk mencatat biaya admin bank
- [Laporan Harian](20-laporan-harian.md) - Cek saldo dan mutasi akun
- [Setup Awal](50-setup-awal.md) - Menambah akun bank baru
- [Referensi Akun](91-referensi-akun.md) - Daftar akun kas dan bank
