# Faktur & Tagihan

Panduan lengkap untuk pengelolaan faktur (invoice) ke klien, tagihan dari vendor, pelacakan pembayaran, laporan umur piutang/hutang, dan laporan per klien/vendor.

## Faktur (Invoice)

### Konsep

Faktur adalah dokumen penagihan yang dikirim ke klien setelah pekerjaan selesai. Setiap faktur memiliki line item (rincian jasa/barang), tanggal jatuh tempo, dan status yang berubah sesuai alur kerja.

### Alur Kerja Faktur

```
DRAFT → SENT → PARTIAL → PAID
                  ↑          ↑
              (sebagian)  (lunas)
```

| Status | Keterangan |
|--------|------------|
| DRAFT | Baru dibuat, bisa diedit |
| SENT | Sudah dikirim ke klien, menunggu pembayaran |
| PARTIAL | Sebagian dibayar |
| PAID | Lunas |
| OVERDUE | Melewati tanggal jatuh tempo |

### Membuat Faktur

Buka menu **Proyek** > **Faktur** > **Faktur Baru**.

Isi data faktur:
- **Klien:** Pilih dari daftar klien
- **Tanggal Faktur:** Tanggal penerbitan
- **Tanggal Jatuh Tempo:** Batas waktu pembayaran
- **Proyek** (opsional): Kaitkan ke proyek tertentu

Tambahkan line item:
- **Deskripsi:** Keterangan jasa/barang
- **Quantity:** Jumlah
- **Harga Satuan:** Harga per unit
- **Total:** Dihitung otomatis (qty x harga satuan)

Klik **Simpan** untuk menyimpan sebagai DRAFT.

![Faktur Baru](screenshots/10-invoice-created.png)

### Mengirim Faktur

Dari halaman detail faktur, klik **Kirim** untuk mengubah status dari DRAFT ke SENT. Faktur yang sudah dikirim tidak bisa diedit.

![Faktur Terkirim](screenshots/10-invoice-sent.png)

Setelah dikirim, faktur masuk ke laporan umur piutang dan siap menerima pembayaran.

---

## Tagihan Vendor (Bill)

### Konsep

Tagihan (bill) adalah dokumen yang diterima dari vendor untuk pembelian barang atau jasa. Alur kerjanya mirip faktur, tapi dari sisi pengeluaran.

### Alur Kerja Tagihan

```
DRAFT → APPROVED → PARTIAL → PAID
                      ↑          ↑
                  (sebagian)  (lunas)
```

| Status | Keterangan |
|--------|------------|
| DRAFT | Baru dibuat, menunggu approval |
| APPROVED | Disetujui, menunggu pembayaran |
| PARTIAL | Sebagian dibayar |
| PAID | Lunas |
| OVERDUE | Melewati tanggal jatuh tempo |

### Membuat Tagihan

Buka menu **Pembelian** > **Tagihan** > **Tagihan Baru**.

Isi data:
- **Vendor:** Pilih dari daftar vendor
- **Tanggal Tagihan:** Tanggal penerbitan oleh vendor
- **Tanggal Jatuh Tempo:** Batas pembayaran
- **Nomor Referensi Vendor** (opsional): Nomor tagihan dari vendor

Tambahkan line item seperti pada faktur.

### Menyetujui Tagihan

Dari halaman detail tagihan, klik **Setujui** untuk mengubah status dari DRAFT ke APPROVED.

![Tagihan Disetujui](screenshots/10-bill-approved.png)

Tagihan yang disetujui masuk ke laporan umur hutang dan siap menerima pembayaran.

---

## Pelacakan Pembayaran

### Mencatat Pembayaran Faktur

Buka halaman detail faktur yang berstatus SENT, PARTIAL, atau OVERDUE. Klik **Catat Pembayaran** untuk membuka form pembayaran.

Isi data pembayaran:
- **Tanggal Pembayaran:** Tanggal dana diterima
- **Jumlah:** Nominal pembayaran (bisa sebagian)
- **Metode Pembayaran:** Transfer, Cash, Cek, Kartu Kredit, E-Wallet, Lainnya
- **Nomor Referensi:** Nomor bukti transfer/kuitansi
- **Catatan** (opsional): Keterangan tambahan

Klik **Simpan Pembayaran**.

**Pembayaran sebagian (partial):** Jika jumlah pembayaran kurang dari total faktur, status berubah ke PARTIAL. Sisa tagihan (balance due) ditampilkan di halaman detail.

![Pembayaran Sebagian](screenshots/10-invoice-partial-payment.png)

**Pembayaran lunas:** Jika total semua pembayaran sama dengan total faktur, status otomatis berubah ke PAID.

![Faktur Lunas](screenshots/10-invoice-paid.png)

### Mencatat Pembayaran Tagihan

Prosesnya sama dengan faktur. Buka halaman detail tagihan berstatus APPROVED, PARTIAL, atau OVERDUE, lalu catat pembayaran.

![Pembayaran Tagihan](screenshots/10-bill-payment.png)

### Riwayat Pembayaran

Setiap faktur/tagihan menampilkan tabel riwayat pembayaran di halaman detail:
- Tanggal pembayaran
- Jumlah
- Metode pembayaran
- Nomor referensi

### Validasi

- Pembayaran hanya bisa dicatat pada faktur/tagihan dengan status yang tepat
- Jumlah pembayaran tidak boleh melebihi sisa tagihan (overpayment ditolak)
- Total pembayaran + pembayaran baru <= total faktur/tagihan

---

## Laporan Umur Piutang & Hutang

### Konsep Aging

Laporan umur (aging report) mengelompokkan faktur/tagihan yang belum lunas berdasarkan berapa lama sudah jatuh tempo. Berguna untuk memantau risiko piutang tak tertagih dan prioritas pembayaran.

### Bucket Aging

| Bucket | Keterangan |
|--------|------------|
| Belum Jatuh Tempo | Belum melewati due date |
| 1-30 hari | Terlambat 1-30 hari |
| 31-60 hari | Terlambat 31-60 hari |
| 61-90 hari | Terlambat 61-90 hari |
| > 90 hari | Terlambat lebih dari 90 hari |

### Umur Piutang (Receivables Aging)

Buka menu **Laporan** > **Umur Piutang**.

![Laporan Umur Piutang](screenshots/10-aging-receivables-unpaid.png)

Menampilkan:
- **Ringkasan per bucket:** Total piutang per kategori umur
- **Tabel per klien:** Rincian saldo per klien di setiap bucket
- **Filter tanggal:** Pilih tanggal acuan (as-of date)

Setelah pembayaran sebagian, saldo di aging report berkurang sesuai jumlah yang sudah dibayar.

![Aging Setelah Pembayaran Sebagian](screenshots/10-aging-receivables-partial.png)

Setelah lunas, klien hilang dari laporan aging.

![Aging Setelah Lunas](screenshots/10-aging-receivables-cleared.png)

### Umur Hutang (Payables Aging)

Buka menu **Laporan** > **Umur Hutang**. Format sama dengan umur piutang, tapi untuk tagihan vendor.

![Laporan Umur Hutang](screenshots/10-aging-payables-unpaid.png)

---

## Laporan Klien & Vendor

### Konsep Statement

Laporan per klien/vendor (statement) menampilkan riwayat transaksi secara kronologis dengan saldo berjalan (running balance). Berguna untuk rekonsiliasi dengan klien/vendor dan verifikasi posisi piutang/hutang.

### Laporan Klien

Akses dari halaman detail klien: klik **Lihat Laporan**, atau langsung ke **Laporan** > **Laporan Klien**.

![Laporan Klien](screenshots/10-client-statement.png)

Informasi yang ditampilkan:
- **Periode:** Filter tanggal mulai dan akhir
- **Saldo Awal:** Total piutang sebelum periode
- **Tabel Transaksi:** Setiap baris menampilkan:
  - Tanggal
  - Tipe (Invoice atau Pembayaran)
  - Nomor referensi
  - Keterangan
  - Jumlah invoice / jumlah pembayaran
  - Saldo berjalan
- **Saldo Akhir:** Posisi piutang akhir periode
- **Cetak:** Buka versi cetak (print-friendly, A4 landscape)

### Laporan Vendor

Format sama dengan laporan klien, tapi menampilkan tagihan (bill) dan pembayaran ke vendor.

![Laporan Vendor](screenshots/10-vendor-statement.png)

Akses dari halaman detail vendor: klik **Lihat Laporan**.

---

## Contoh Alur Lengkap

Walk-through siklus penagihan dari awal sampai lunas.

### Langkah 1: Buat Faktur

Buat faktur untuk klien PT Telkom dengan 2 line item:
- Jasa Pengembangan Aplikasi: 1 x Rp 10.000.000
- Jasa Maintenance Bulanan: 5 x Rp 1.000.000
- **Total: Rp 15.000.000**

### Langkah 2: Kirim Faktur

Kirim faktur. Status berubah ke SENT. Faktur muncul di laporan umur piutang pada bucket "Belum Jatuh Tempo".

### Langkah 3: Terima Pembayaran Sebagian

Klien membayar Rp 5.000.000 via transfer. Status berubah ke PARTIAL. Sisa tagihan: Rp 10.000.000.

Di laporan aging, nominal berubah dari Rp 15.000.000 menjadi Rp 10.000.000.

### Langkah 4: Terima Pembayaran Final

Klien membayar sisa Rp 10.000.000. Status berubah ke PAID. Faktur hilang dari laporan aging.

### Langkah 5: Periksa Laporan Klien

Buka laporan klien PT Telkom. Terlihat:
1. Invoice Rp 15.000.000 → saldo naik
2. Pembayaran Rp 5.000.000 → saldo turun
3. Pembayaran Rp 10.000.000 → saldo menjadi 0

---

## Tips

1. **Kirim faktur segera** — Jangan biarkan faktur berlama-lama di status DRAFT. Semakin cepat dikirim, semakin cepat dibayar.
2. **Pantau aging mingguan** — Periksa laporan umur piutang minimal seminggu sekali. Piutang > 60 hari perlu tindakan aktif.
3. **Catat pembayaran saat diterima** — Jangan tunda pencatatan pembayaran agar saldo selalu akurat.
4. **Gunakan statement untuk rekonsiliasi** — Kirim statement ke klien secara berkala untuk memastikan kedua pihak sepakat tentang saldo.
5. **Setujui tagihan vendor tepat waktu** — Tagihan yang disetujui masuk ke aging, membantu perencanaan arus kas.

---

## Lihat Juga

- [Industri Jasa](07-industri-jasa.md) — Konteks invoice dalam bisnis jasa IT
- [Rekonsiliasi Bank](14-rekonsiliasi-bank.md) — Cocokkan pembayaran dengan mutasi bank
- [Tutorial Akuntansi](02-tutorial-akuntansi.md) — Jurnal piutang dan hutang
- [Peringatan](15-peringatan.md) — Alert otomatis untuk piutang overdue
