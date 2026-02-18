-- Update journal template AI metadata for production (akunting.artivisi.id)
-- Run directly on production database (NOT as Flyway migration)
-- Usage: psql -U akunting -d akunting -f update-template-metadata.sql

BEGIN;

-- ============================================
-- INCOME templates
-- ============================================

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat pendapatan dari layanan konsultasi IT atau software development. Cocok untuk non-PKP atau transaksi tanpa PPN. Amount = nilai invoice penuh.',
  keywords = ARRAY['konsultasi','consulting','development','programming','software','jasa','service','proyek','project','implementasi','integration'],
  example_merchants = ARRAY['PT Client A','Startup B','Government Agency','Kementerian','Pemda','Bank BRI','Telkom'],
  typical_amount_min = 5000000, typical_amount_max = 500000000, merchant_patterns = NULL
WHERE template_name = 'Pendapatan Jasa Konsultasi' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat pendapatan konsultasi IT dari perusahaan PKP dengan PPN Keluaran 11%. Amount = DPP + PPN. Sistem otomatis memisahkan DPP dan PPN.',
  keywords = ARRAY['konsultasi','consulting','ppn','vat','pkp','faktur pajak','development','jasa','service'],
  example_merchants = ARRAY['PT Client PKP','Bank Mandiri','Telkom Indonesia','Pertamina','PLN'],
  typical_amount_min = 5000000, typical_amount_max = 1000000000, merchant_patterns = NULL
WHERE template_name = 'Pendapatan Jasa Konsultasi dengan PPN' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat pendapatan dari jasa pelatihan atau training IT. Cocok untuk non-PKP atau tanpa PPN. Amount = nilai invoice penuh.',
  keywords = ARRAY['training','pelatihan','kursus','workshop','seminar','bootcamp','sertifikasi','certification'],
  example_merchants = ARRAY['PT Client A','Kementerian Kominfo','Universitas Indonesia','BRI','Telkom','Pertamina'],
  typical_amount_min = 1000000, typical_amount_max = 500000000, merchant_patterns = NULL
WHERE template_name = 'Pendapatan Jasa Training' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat pendapatan training IT dari perusahaan PKP dengan PPN Keluaran 11%. Amount = DPP + PPN.',
  keywords = ARRAY['training','pelatihan','ppn','vat','pkp','faktur pajak','workshop','seminar'],
  example_merchants = ARRAY['PT Client PKP','BUMN','Kementerian','Bank','Telkom'],
  typical_amount_min = 1000000, typical_amount_max = 500000000, merchant_patterns = NULL
WHERE template_name = 'Pendapatan Jasa Training dengan PPN' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat pendapatan dari jasa software development/programming. Cocok untuk proyek development yang bukan konsultasi murni.',
  keywords = ARRAY['development','software','programming','coding','aplikasi','app','web','mobile','backend','frontend','fullstack'],
  example_merchants = ARRAY['PT Client A','Startup B','Government Agency','Kementerian'],
  typical_amount_min = 5000000, typical_amount_max = 1000000000, merchant_patterns = NULL
WHERE template_name = 'Pendapatan Software Development' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini ketika klien memotong PPh 23 sebesar 2% dari nilai jasa. Amount = nilai bruto. Kas masuk = 98%. Kredit pajak PPh 23 = 2%.',
  keywords = ARRAY['pendapatan','income','pph 23','withholding tax','potong pajak','bukti potong','kredit pajak'],
  example_merchants = ARRAY['PT Client Korporat','BUMN','Kementerian','Bank','Asuransi'],
  typical_amount_min = 5000000, typical_amount_max = 500000000, merchant_patterns = NULL
WHERE template_name = 'Pendapatan Jasa dengan PPh 23' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk pendapatan jasa PKP dimana klien memotong PPh 23 (2% dari DPP) dan ada PPN 11%. Amount = DPP + PPN. Kas masuk = DPP + PPN - PPh23.',
  keywords = ARRAY['pendapatan','income','ppn','pph 23','vat','withholding','pkp','faktur','bukti potong'],
  example_merchants = ARRAY['BUMN','Kementerian','Bank BUMN','Pertamina','PLN','Telkom','Bank Mandiri'],
  typical_amount_min = 10000000, typical_amount_max = 1000000000, merchant_patterns = NULL
WHERE template_name = 'Pendapatan Jasa dengan PPN dan PPh 23' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk pendapatan jasa ke BUMN/Pemerintah (kode Faktur Pajak 03). PPN dipungut oleh pembeli (wapu). Amount = DPP + PPN.',
  keywords = ARRAY['pendapatan','bumn','pemerintah','wapu','ppn dipungut','faktur pajak 03','pemungut'],
  example_merchants = ARRAY['BUMN','Kementerian','Lembaga Pemerintah','BPJS','PLN','Pertamina'],
  typical_amount_min = 10000000, typical_amount_max = 1000000000, merchant_patterns = NULL
WHERE template_name = 'Pendapatan Jasa dengan PPh dan PPN Dipungut BUMN' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mengakui pendapatan dari uang muka yang sudah diterima ketika milestone proyek selesai. Memindahkan saldo dari pendapatan diterima dimuka ke pendapatan jasa.',
  keywords = ARRAY['pengakuan','revenue recognition','milestone','proyek selesai','handover','deliverable','uang muka'],
  example_merchants = NULL,
  typical_amount_min = 5000000, typical_amount_max = 500000000, merchant_patterns = NULL
WHERE template_name = 'Pengakuan Pendapatan Proyek' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat pencairan deposito berjangka beserta bunga. Variabel: pokok (nilai deposito) dan bunga (pendapatan bunga).',
  keywords = ARRAY['deposito','deposit','cairkan','pencairan','bunga','interest','jatuh tempo','maturity'],
  example_merchants = ARRAY['BCA','Mandiri','BNI','BRI','Bank Muamalat','BSI'],
  typical_amount_min = 10000000, typical_amount_max = 10000000000, merchant_patterns = ARRAY['.*bank.*']
WHERE template_name = 'Cairkan Deposito' AND is_current_version = true;

-- ============================================
-- RECEIPT templates
-- ============================================

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat penerimaan uang muka (down payment) proyek dari klien. Dana masuk ke bank tetapi belum diakui sebagai pendapatan.',
  keywords = ARRAY['dp','down payment','uang muka','advance','deposit','tanda jadi','panjar'],
  example_merchants = ARRAY['PT Client A','Startup B','Government Agency','Kementerian'],
  typical_amount_min = 5000000, typical_amount_max = 500000000, merchant_patterns = NULL
WHERE template_name = 'Terima DP Proyek' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat penerimaan pembayaran piutang dari klien. Mengurangi saldo piutang usaha dan menambah saldo bank.',
  keywords = ARRAY['pelunasan','pembayaran','piutang','receivable','tagihan','invoice','collection','bayar'],
  example_merchants = ARRAY['PT Client A','Bank Transfer','Virtual Account'],
  typical_amount_min = 1000000, typical_amount_max = 1000000000, merchant_patterns = NULL
WHERE template_name = 'Terima Pelunasan Piutang' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat penerimaan reimbursement dari klien atas biaya yang sudah dibayarkan perusahaan (cloud/travel/dll).',
  keywords = ARRAY['reimbursement','reimburse','klien','ganti biaya','penggantian','client reimburse'],
  example_merchants = ARRAY['PT Client A','Startup B','Bank Transfer'],
  typical_amount_min = 50000, typical_amount_max = 100000000, merchant_patterns = NULL
WHERE template_name = 'Terima Reimbursement dari Klien' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat bunga bank yang masuk ke dana non-halal (sesuai prinsip syariah). Dana ini akan disalurkan sebagai sedekah/donasi.',
  keywords = ARRAY['bunga bank','interest','non-halal','riba','syariah','sharia'],
  example_merchants = ARRAY['BCA','Mandiri','BNI','BRI'],
  typical_amount_min = 1000, typical_amount_max = 10000000, merchant_patterns = ARRAY['.*bank.*']
WHERE template_name = 'Terima Bunga Bank (Non-Halal)' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat setoran modal dari pemilik atau pemegang saham ke perusahaan.',
  keywords = ARRAY['modal','capital','setoran','investasi pemilik','equity','pemegang saham'],
  example_merchants = NULL,
  typical_amount_min = 1000000, typical_amount_max = 10000000000, merchant_patterns = NULL
WHERE template_name = 'Setoran Modal' AND is_current_version = true;

-- ============================================
-- EXPENSE templates
-- ============================================

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat akrual beban cloud yang belum ditagih di akhir bulan. Mendebit beban cloud dan mengkredit hutang.',
  keywords = ARRAY['akrual','accrual','cloud','server','aws','gcp','azure','akhir bulan','month end'],
  example_merchants = ARRAY['Amazon Web Services','Google Cloud','Microsoft Azure','DigitalOcean'],
  typical_amount_min = 50000, typical_amount_max = 50000000, merchant_patterns = ARRAY['.*aws.*','.*google cloud.*','.*azure.*']
WHERE template_name = 'Accrual Beban Cloud' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat akrual bonus karyawan di akhir bulan/tahun yang belum dibayarkan. Mendebit beban bonus dan mengkredit hutang bonus.',
  keywords = ARRAY['akrual','accrual','bonus','karyawan','insentif','akhir tahun','year end'],
  example_merchants = NULL,
  typical_amount_min = 1000000, typical_amount_max = 200000000, merchant_patterns = NULL
WHERE template_name = 'Accrual Bonus Karyawan' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat akrual gaji di akhir bulan ketika gaji belum dibayarkan. Mendebit beban gaji dan mengkredit hutang gaji.',
  keywords = ARRAY['akrual','accrual','gaji','salary','akhir bulan','month end','closing'],
  example_merchants = NULL,
  typical_amount_min = 3000000, typical_amount_max = 200000000, merchant_patterns = NULL
WHERE template_name = 'Accrual Gaji Bulan Berjalan' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat amortisasi aset tak berwujud secara bulanan (software/lisensi/paten yang dikapitalisasi).',
  keywords = ARRAY['amortisasi','amortization','aset tak berwujud','intangible','software','lisensi','paten','goodwill'],
  example_merchants = NULL,
  typical_amount_min = 10000, typical_amount_max = 50000000, merchant_patterns = NULL
WHERE template_name = 'Amortisasi Aset Tak Berwujud' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat pembayaran BBM langsung dari kas/bank perusahaan (bukan reimbursement karyawan).',
  keywords = ARRAY['bbm','bensin','solar','pertamax','fuel','spbu','gas','kendaraan'],
  example_merchants = ARRAY['SPBU Pertamina','Shell','BP','Vivo','Total','SPBU'],
  typical_amount_min = 50000, typical_amount_max = 2000000, merchant_patterns = ARRAY['.*spbu.*','.*pertamina.*','.*shell.*','.*vivo.*']
WHERE template_name = 'Bayar BBM Langsung' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat pembayaran iuran BPJS Kesehatan dan BPJS Ketenagakerjaan. Template DETAILED dengan variabel bpjsKesehatan dan bpjsTenagakerja.',
  keywords = ARRAY['bpjs','bpjs kesehatan','bpjs ketenagakerjaan','jamsostek','insurance','asuransi'],
  example_merchants = ARRAY['BPJS Kesehatan','BPJS Ketenagakerjaan'],
  typical_amount_min = 100000, typical_amount_max = 20000000, merchant_patterns = ARRAY['.*bpjs.*']
WHERE template_name = 'Bayar BPJS' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat pembayaran layanan cloud computing untuk aplikasi/infrastruktur perusahaan sendiri. Termasuk AWS/GCP/Azure/DigitalOcean.',
  keywords = ARRAY['cloud','server','hosting','aws','gcp','azure','digitalocean','heroku','vercel','vps','infrastructure','saas'],
  example_merchants = ARRAY['Amazon Web Services','Google Cloud','Microsoft Azure','DigitalOcean','Heroku','Vercel','Linode','Cloudflare','Netlify'],
  typical_amount_min = 50000, typical_amount_max = 50000000, merchant_patterns = ARRAY['.*aws.*','.*amazon web.*','.*google cloud.*','.*azure.*','.*digitalocean.*','.*heroku.*','.*vercel.*','.*linode.*','.*cloudflare.*']
WHERE template_name = 'Bayar Beban Cloud & Server' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat pembayaran gaji karyawan secara langsung. Cocok untuk pembayaran gaji freelancer atau karyawan kontrak.',
  keywords = ARRAY['gaji','salary','upah','honor','freelancer','kontrak','payroll'],
  example_merchants = ARRAY['Transfer Gaji','BCA','Mandiri','BNI','BRI'],
  typical_amount_min = 3000000, typical_amount_max = 50000000, merchant_patterns = NULL
WHERE template_name = 'Bayar Beban Gaji' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat pembayaran tagihan listrik bulanan ke PLN. Termasuk token listrik prabayar.',
  keywords = ARRAY['listrik','electricity','pln','token','utility','utilitas'],
  example_merchants = ARRAY['PLN','PLN Mobile','Tokopedia PLN','Bukalapak Token Listrik'],
  typical_amount_min = 50000, typical_amount_max = 5000000, merchant_patterns = ARRAY['.*pln.*','.*listrik.*','.*electricity.*']
WHERE template_name = 'Bayar Beban Listrik' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat pembayaran sewa kantor atau coworking space. Untuk sewa dengan PPh 4(2) gunakan template Pembayaran Sewa dengan PPh 4(2).',
  keywords = ARRAY['sewa','rent','kantor','office','coworking','space','gedung','ruangan'],
  example_merchants = ARRAY['WeWork','CoHive','GoWork','Pemilik Gedung','Management Building'],
  typical_amount_min = 1000000, typical_amount_max = 50000000, merchant_patterns = NULL
WHERE template_name = 'Bayar Beban Sewa' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat pembayaran lisensi software dan tools. Termasuk IDE/SaaS/subscription tools.',
  keywords = ARRAY['software','lisensi','license','subscription','tools','ide','saas','github','jetbrains','figma','slack','jira','confluence'],
  example_merchants = ARRAY['JetBrains','GitHub','GitLab','Atlassian','Figma','Slack','Notion','Zoom','Microsoft 365','Adobe','Google Workspace'],
  typical_amount_min = 50000, typical_amount_max = 20000000, merchant_patterns = ARRAY['.*jetbrains.*','.*github.*','.*gitlab.*','.*atlassian.*','.*figma.*','.*slack.*','.*notion.*','.*adobe.*']
WHERE template_name = 'Bayar Beban Software & Lisensi' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat pembayaran tagihan telepon dan internet kantor.',
  keywords = ARRAY['telepon','telephone','internet','telekomunikasi','telkom','indihome','biznet','wifi','broadband','vpn'],
  example_merchants = ARRAY['Telkom Indonesia','IndiHome','Biznet','MyRepublic','First Media','XL Axiata','Telkomsel'],
  typical_amount_min = 100000, typical_amount_max = 5000000, merchant_patterns = ARRAY['.*telkom.*','.*indihome.*','.*biznet.*','.*myrepublic.*','.*first media.*']
WHERE template_name = 'Bayar Beban Telekomunikasi' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat pembayaran bonus karyawan (bonus proyek atau tahunan). Langsung dari kas/bank.',
  keywords = ARRAY['bonus','insentif','incentive','karyawan','proyek','tahunan','reward'],
  example_merchants = ARRAY['Transfer Bonus','BCA','Mandiri','BNI','BRI'],
  typical_amount_min = 500000, typical_amount_max = 100000000, merchant_patterns = NULL
WHERE template_name = 'Bayar Bonus Karyawan' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat pembayaran layanan cloud untuk klien yang akan direimbursement. Dicatat sebagai piutang reimbursement.',
  keywords = ARRAY['cloud','klien','reimbursable','piutang','aws','gcp','azure','client cloud'],
  example_merchants = ARRAY['Amazon Web Services','Google Cloud','Microsoft Azure','DigitalOcean'],
  typical_amount_min = 50000, typical_amount_max = 50000000, merchant_patterns = ARRAY['.*aws.*','.*google cloud.*','.*azure.*']
WHERE template_name = 'Bayar Cloud Klien (Reimbursable)' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat pembayaran Pajak Bumi dan Bangunan (PBB) tahunan untuk kantor.',
  keywords = ARRAY['pbb','pajak bumi','pajak bangunan','property tax','sppt'],
  example_merchants = ARRAY['DJP','Kas Negara','Bank Persepsi','Tokopedia PBB'],
  typical_amount_min = 100000, typical_amount_max = 50000000, merchant_patterns = NULL
WHERE template_name = 'Bayar PBB' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat pembayaran parkir dari kas kecil perusahaan.',
  keywords = ARRAY['parkir','parking','kas kecil','petty cash'],
  example_merchants = ARRAY['Parking','SPBU','Mall','Gedung'],
  typical_amount_min = 2000, typical_amount_max = 100000, merchant_patterns = ARRAY['.*parkir.*','.*parking.*']
WHERE template_name = 'Bayar Parkir dari Kas' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat pembayaran kegiatan team building atau outing karyawan.',
  keywords = ARRAY['team building','outing','rekreasi','gathering','bonding','wisata','liburan karyawan'],
  example_merchants = ARRAY['Hotel','Resort','Villa','Restoran','Tiket.com','Traveloka'],
  typical_amount_min = 500000, typical_amount_max = 50000000, merchant_patterns = NULL
WHERE template_name = 'Bayar Team Building / Outing' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat reimbursement biaya kesehatan karyawan (medical reimbursement/tunjangan kesehatan).',
  keywords = ARRAY['kesehatan','medical','tunjangan','reimbursement','dokter','rumah sakit','klinik','apotek','obat'],
  example_merchants = ARRAY['Rumah Sakit','Klinik','Apotek','Dokter','Halodoc','Alodokter'],
  typical_amount_min = 10000, typical_amount_max = 10000000, merchant_patterns = ARRAY['.*rumah sakit.*','.*klinik.*','.*apotek.*','.*hospital.*']
WHERE template_name = 'Bayar Tunjangan Kesehatan' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat pembelian barang atau jasa yang dikenai PPN. Amount = DPP + PPN. Sistem otomatis memisahkan DPP dan PPN Masukan 11%.',
  keywords = ARRAY['pembelian','purchase','ppn masukan','vat input','beli','faktur pajak masukan'],
  example_merchants = ARRAY['Vendor IT','Supplier','Toko Komputer','Distributor'],
  typical_amount_min = 100000, typical_amount_max = 500000000, merchant_patterns = NULL
WHERE template_name = 'Pembelian dengan PPN' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini ketika membayar jasa vendor dan memotong PPh 23 (2%). Amount = nilai bruto. Vendor menerima 98%.',
  keywords = ARRAY['pph 23','withholding','potong pajak','jasa vendor','outsource','subcontractor','freelancer'],
  example_merchants = ARRAY['Vendor IT','Konsultan','Freelancer','PT Subcontractor'],
  typical_amount_min = 1000000, typical_amount_max = 200000000, merchant_patterns = NULL
WHERE template_name = 'Pembayaran Jasa dengan PPh 23' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini ketika membayar sewa tanah atau bangunan dan memotong PPh Final 4(2) sebesar 10%. Amount = nilai bruto sewa.',
  keywords = ARRAY['sewa','rent','pph final','pph 4(2)','tanah','bangunan','gedung','kantor','properti'],
  example_merchants = ARRAY['Pemilik Gedung','Manajemen Properti','PT Properti','Landlord'],
  typical_amount_min = 1000000, typical_amount_max = 100000000, merchant_patterns = NULL
WHERE template_name = 'Pembayaran Sewa dengan PPh 4(2)' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini ketika karyawan membayar pengeluaran perusahaan dengan uang pribadi. Mencatat beban dan hutang reimbursement.',
  keywords = ARRAY['reimburse','reimbursement','ganti','klaim','claim','karyawan bayar','uang pribadi','pengeluaran karyawan'],
  example_merchants = NULL,
  typical_amount_min = 10000, typical_amount_max = 10000000, merchant_patterns = NULL
WHERE template_name = 'Karyawan Reimburse Pengeluaran' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini ketika karyawan mengisi BBM kendaraan kantor dengan uang pribadi. Dicatat sebagai hutang reimbursement BBM.',
  keywords = ARRAY['bbm','bensin','solar','pertamax','fuel','spbu','karyawan bayar','reimburse bbm'],
  example_merchants = ARRAY['SPBU Pertamina','Shell','BP','Vivo'],
  typical_amount_min = 50000, typical_amount_max = 1000000, merchant_patterns = ARRAY['.*spbu.*','.*pertamina.*','.*shell.*']
WHERE template_name = 'Isi BBM Kendaraan (Karyawan Bayar)' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat pembelian alat tulis kantor (ATK).',
  keywords = ARRAY['atk','alat tulis','stationery','kertas','tinta','printer','pulpen','spidol','office supplies'],
  example_merchants = ARRAY['Gramedia','Toko ATK','Tokopedia','Shopee','Indomaret','Alfamart'],
  typical_amount_min = 10000, typical_amount_max = 2000000, merchant_patterns = NULL
WHERE template_name = 'Pembelian ATK' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat pembelian aset tetap (komputer/laptop/server/peralatan). Variabel: assetCost. Aset akan didepresiasi.',
  keywords = ARRAY['aset tetap','fixed asset','komputer','laptop','server','peralatan','equipment','furniture','mesin'],
  example_merchants = ARRAY['Tokopedia','Shopee','Bhinneka','DataScript','Enterkomputer','IKEA','Informa'],
  typical_amount_min = 1000000, typical_amount_max = 200000000, merchant_patterns = NULL
WHERE template_name = 'Pembelian Aset Tetap' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat pembelian konsumsi kantor (snack/kopi/makan siang/air minum untuk karyawan).',
  keywords = ARRAY['konsumsi','makan','snack','kopi','coffee','air minum','catering','lunch','kantor'],
  example_merchants = ARRAY['Starbucks','KFC','McDonalds','Kopi Kenangan','Fore Coffee','GrabFood','GoFood','Indomaret','Alfamart'],
  typical_amount_min = 10000, typical_amount_max = 5000000, merchant_patterns = ARRAY['.*starbucks.*','.*kfc.*','.*mcdonald.*','.*grabfood.*','.*gofood.*']
WHERE template_name = 'Pembelian Konsumsi Kantor' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat pembelian peralatan kantor (komputer/laptop/monitor/keyboard/mouse). Dicatat sebagai aset tetap.',
  keywords = ARRAY['peralatan','equipment','komputer','laptop','monitor','keyboard','mouse','printer','scanner','projector'],
  example_merchants = ARRAY['Bhinneka','DataScript','Enterkomputer','Tokopedia','Shopee','iBox','Digimap'],
  typical_amount_min = 100000, typical_amount_max = 100000000, merchant_patterns = NULL
WHERE template_name = 'Pembelian Peralatan' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat pembelian perlengkapan kantor (supplies habis pakai seperti tisu/sabun/alat kebersihan).',
  keywords = ARRAY['perlengkapan','supplies','kantor','tisu','sabun','alat kebersihan','dispenser','hand sanitizer'],
  example_merchants = ARRAY['Indomaret','Alfamart','Tokopedia','Shopee','ACE Hardware'],
  typical_amount_min = 10000, typical_amount_max = 5000000, merchant_patterns = NULL
WHERE template_name = 'Pembelian Perlengkapan Kantor' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat pembayaran sewa dan biaya operasional kantor umum.',
  keywords = ARRAY['operasional','operational','kantor','office','biaya kantor','overhead'],
  example_merchants = NULL,
  typical_amount_min = 10000, typical_amount_max = 50000000, merchant_patterns = NULL
WHERE template_name = 'Biaya Operasional Kantor' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat pembayaran denda pajak (STP/Surat Tagihan Pajak).',
  keywords = ARRAY['denda','penalty','pajak','stp','surat tagihan','telat bayar','telat lapor'],
  example_merchants = ARRAY['DJP','Kas Negara','Bank Persepsi'],
  typical_amount_min = 10000, typical_amount_max = 50000000, merchant_patterns = NULL
WHERE template_name = 'Denda Pajak' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat penempatan deposito berjangka di bank.',
  keywords = ARRAY['deposito','deposit','berjangka','time deposit','penempatan','investasi'],
  example_merchants = ARRAY['BCA','Mandiri','BNI','BRI','Bank Muamalat','BSI'],
  typical_amount_min = 10000000, typical_amount_max = 10000000000, merchant_patterns = ARRAY['.*bank.*']
WHERE template_name = 'Beli Deposito' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat pembelian koin emas dinar atau perak dirham sebagai investasi.',
  keywords = ARRAY['dinar','dirham','emas','perak','gold','silver','koin','investasi','logam mulia'],
  example_merchants = ARRAY['Wakala Induk Nusantara','Gerai Dinar','Dinar First'],
  typical_amount_min = 100000, typical_amount_max = 1000000000, merchant_patterns = ARRAY['.*dinar.*','.*dirham.*','.*wakala.*']
WHERE template_name = 'Beli Dinar/Dirham' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat pembelian emas batangan (Antam/UBS/dll) sebagai investasi.',
  keywords = ARRAY['logam mulia','emas','gold','antam','ubs','batangan','investasi','precious metal'],
  example_merchants = ARRAY['Antam','Pegadaian','Tokopedia Emas','Bukalapak Emas','Treasury'],
  typical_amount_min = 500000, typical_amount_max = 5000000000, merchant_patterns = ARRAY['.*antam.*','.*pegadaian.*','.*emas.*']
WHERE template_name = 'Beli Logam Mulia' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat penjualan dinar/dirham dengan laba. Variabel: hasilPenjualan dan nilaiPerolehan.',
  keywords = ARRAY['jual','dinar','dirham','laba','profit','gain','emas','perak'],
  example_merchants = ARRAY['Wakala Induk Nusantara','Gerai Dinar','Pembeli'],
  typical_amount_min = 100000, typical_amount_max = 1000000000, merchant_patterns = NULL
WHERE template_name = 'Jual Dinar/Dirham (Laba)' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat penjualan dinar/dirham dengan rugi. Variabel: hasilPenjualan dan nilaiPerolehan.',
  keywords = ARRAY['jual','dinar','dirham','rugi','loss','emas','perak'],
  example_merchants = ARRAY['Wakala Induk Nusantara','Gerai Dinar','Pembeli'],
  typical_amount_min = 100000, typical_amount_max = 1000000000, merchant_patterns = NULL
WHERE template_name = 'Jual Dinar/Dirham (Rugi)' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat penjualan emas batangan dengan laba. Variabel: hasilPenjualan dan nilaiPerolehan.',
  keywords = ARRAY['jual','logam mulia','emas','gold','laba','profit','gain','antam'],
  example_merchants = ARRAY['Antam','Pegadaian','Tokopedia Emas','Pembeli'],
  typical_amount_min = 500000, typical_amount_max = 5000000000, merchant_patterns = NULL
WHERE template_name = 'Jual Logam Mulia (Laba)' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat penjualan emas batangan dengan rugi. Variabel: hasilPenjualan dan nilaiPerolehan.',
  keywords = ARRAY['jual','logam mulia','emas','gold','rugi','loss','antam'],
  example_merchants = ARRAY['Antam','Pegadaian','Tokopedia Emas','Pembeli'],
  typical_amount_min = 500000, typical_amount_max = 5000000000, merchant_patterns = NULL
WHERE template_name = 'Jual Logam Mulia (Rugi)' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat penyusutan aset tetap bulanan. Variabel: depreciationAmount. Dijalankan otomatis dari modul aset tetap.',
  keywords = ARRAY['penyusutan','depreciation','aset tetap','fixed asset','bulanan','monthly'],
  example_merchants = NULL,
  typical_amount_min = 10000, typical_amount_max = 50000000, merchant_patterns = NULL
WHERE template_name = 'Penyusutan Aset Tetap' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat penyusutan peralatan kantor secara bulanan (jurnal penyesuaian).',
  keywords = ARRAY['penyusutan','depreciation','peralatan','equipment','bulanan','jurnal penyesuaian'],
  example_merchants = NULL,
  typical_amount_min = 10000, typical_amount_max = 20000000, merchant_patterns = NULL
WHERE template_name = 'Penyusutan Peralatan' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat pelepasan atau penjualan aset tetap. Variabel: bookValue, accumulatedDepreciation, assetCost, disposalProceeds, gainLoss.',
  keywords = ARRAY['pelepasan','disposal','aset tetap','jual aset','scrapping','write off'],
  example_merchants = NULL,
  typical_amount_min = 0, typical_amount_max = 500000000, merchant_patterns = NULL
WHERE template_name = 'Pelepasan Aset Tetap' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat penjualan aset tetap dengan laba (harga jual > nilai buku). Variabel: hargaJual, nilaiPerolehan, akumPenyusutan.',
  keywords = ARRAY['jual aset','penjualan aset','laba','profit','gain','fixed asset sale'],
  example_merchants = NULL,
  typical_amount_min = 100000, typical_amount_max = 500000000, merchant_patterns = NULL
WHERE template_name = 'Penjualan Aset Tetap (Laba)' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat penjualan aset tetap dengan rugi (harga jual < nilai buku). Variabel: hargaJual, nilaiPerolehan, akumPenyusutan.',
  keywords = ARRAY['jual aset','penjualan aset','rugi','loss','fixed asset sale'],
  example_merchants = NULL,
  typical_amount_min = 100000, typical_amount_max = 500000000, merchant_patterns = NULL
WHERE template_name = 'Penjualan Aset Tetap (Rugi)' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat pembayaran angsuran PPh 25 (cicilan PPh Badan bulanan).',
  keywords = ARRAY['pph 25','angsuran pajak','cicilan pajak badan','corporate tax installment'],
  example_merchants = ARRAY['DJP','Kas Negara','Bank Persepsi','e-Billing'],
  typical_amount_min = 100000, typical_amount_max = 50000000, merchant_patterns = NULL
WHERE template_name = 'Bayar PPh 25' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat pembayaran PPh 29 (kurang bayar PPh Badan tahunan).',
  keywords = ARRAY['pph 29','pajak badan tahunan','corporate tax','kurang bayar','annual tax'],
  example_merchants = ARRAY['DJP','Kas Negara','Bank Persepsi','e-Billing'],
  typical_amount_min = 100000, typical_amount_max = 500000000, merchant_patterns = NULL
WHERE template_name = 'Bayar PPh 29' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Template sistem untuk posting hasil payroll bulanan. Variabel: grossSalary, companyBpjs, netPay, totalBpjs, pph21.',
  keywords = ARRAY['payroll','gaji','salary','posting gaji','slip gaji'],
  example_merchants = NULL,
  typical_amount_min = 5000000, typical_amount_max = 500000000, merchant_patterns = NULL
WHERE template_name = 'Post Gaji Bulanan' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat biaya administrasi bank bulanan.',
  keywords = ARRAY['admin bank','biaya bank','bank charge','bank fee','administrasi','potongan bank'],
  example_merchants = ARRAY['BCA','Mandiri','BNI','BRI','CIMB','Bank Admin'],
  typical_amount_min = 5000, typical_amount_max = 500000, merchant_patterns = ARRAY['.*bank.*']
WHERE template_name = 'Beban Admin Bank' AND is_current_version = true;

-- ============================================
-- PAYMENT templates
-- ============================================

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk membayar klaim reimbursement karyawan. Mengurangi hutang reimbursement dan kas/bank.',
  keywords = ARRAY['reimburse','reimbursement','ganti','bayar klaim','transfer karyawan'],
  example_merchants = NULL,
  typical_amount_min = 10000, typical_amount_max = 10000000, merchant_patterns = NULL
WHERE template_name = 'Bayar Reimbursement ke Karyawan' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat pembayaran reimburse BBM ke karyawan yang sudah bayar duluan.',
  keywords = ARRAY['reimburse','bbm','bensin','karyawan','ganti bbm','fuel reimburse'],
  example_merchants = NULL,
  typical_amount_min = 50000, typical_amount_max = 1000000, merchant_patterns = NULL
WHERE template_name = 'Bayar Reimburse BBM ke Karyawan' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat penyetoran iuran BPJS setelah payroll diposting.',
  keywords = ARRAY['bpjs','iuran bpjs','setor bpjs','bpjs kesehatan','bpjs ketenagakerjaan'],
  example_merchants = ARRAY['BPJS Kesehatan','BPJS Ketenagakerjaan'],
  typical_amount_min = 100000, typical_amount_max = 20000000, merchant_patterns = ARRAY['.*bpjs.*']
WHERE template_name = 'Bayar Hutang BPJS' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat pembayaran bonus dari hutang bonus yang sudah diakrual.',
  keywords = ARRAY['bonus','hutang bonus','bayar bonus','insentif','akrual bonus'],
  example_merchants = NULL,
  typical_amount_min = 500000, typical_amount_max = 100000000, merchant_patterns = NULL
WHERE template_name = 'Bayar Hutang Bonus Karyawan' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat pembayaran gaji bulanan ke karyawan setelah payroll diposting.',
  keywords = ARRAY['bayar gaji','transfer gaji','hutang gaji','salary payment','payroll payment'],
  example_merchants = ARRAY['Transfer Gaji','BCA','Mandiri','BNI','BRI'],
  typical_amount_min = 3000000, typical_amount_max = 200000000, merchant_patterns = NULL
WHERE template_name = 'Bayar Hutang Gaji' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat penyetoran PPh 21 karyawan ke kas negara dari kas/bank.',
  keywords = ARRAY['pph 21','pajak karyawan','payroll tax','setor pajak','tax payment'],
  example_merchants = ARRAY['DJP','Kas Negara','Bank Persepsi','e-Billing'],
  typical_amount_min = 50000, typical_amount_max = 50000000, merchant_patterns = NULL
WHERE template_name = 'Bayar PPh 21' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat penyetoran PPh 21 dari hutang PPh 21 ke kas negara.',
  keywords = ARRAY['pph 21','setor pajak','hutang pph 21','tax payment'],
  example_merchants = ARRAY['DJP','Kas Negara','Bank Persepsi','e-Billing'],
  typical_amount_min = 50000, typical_amount_max = 50000000, merchant_patterns = NULL
WHERE template_name = 'Setor PPh 21' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat penyetoran PPh 23 yang telah dipotong dari vendor ke kas negara.',
  keywords = ARRAY['pph 23','withholding tax','setor pajak','potong pajak','tax payment'],
  example_merchants = ARRAY['DJP','Kas Negara','Bank Persepsi','e-Billing'],
  typical_amount_min = 50000, typical_amount_max = 50000000, merchant_patterns = NULL
WHERE template_name = 'Setor PPh 23' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat penyetoran PPh 4(2) Final ke kas negara.',
  keywords = ARRAY['pph 4(2)','pph final','pajak sewa','setor pajak','tax payment'],
  example_merchants = ARRAY['DJP','Kas Negara','Bank Persepsi','e-Billing'],
  typical_amount_min = 50000, typical_amount_max = 20000000, merchant_patterns = NULL
WHERE template_name = 'Setor PPh 4(2)' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat penyetoran PPN kurang bayar (PPN Keluaran - PPN Masukan) ke kas negara.',
  keywords = ARRAY['ppn','vat','pajak pertambahan nilai','setor ppn','tax payment'],
  example_merchants = ARRAY['DJP','Kas Negara','Bank Persepsi','e-Billing'],
  typical_amount_min = 100000, typical_amount_max = 200000000, merchant_patterns = NULL
WHERE template_name = 'Setor PPN' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat pembayaran BPJS Kesehatan.',
  keywords = ARRAY['bpjs kesehatan','bpjs','asuransi kesehatan','health insurance'],
  example_merchants = ARRAY['BPJS Kesehatan'],
  typical_amount_min = 50000, typical_amount_max = 10000000, merchant_patterns = ARRAY['.*bpjs.*kesehatan.*']
WHERE template_name = 'Pembayaran BPJS Kesehatan' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat pembayaran BPJS Tenaga Kerja (Ketenagakerjaan).',
  keywords = ARRAY['bpjs tk','bpjs ketenagakerjaan','jamsostek','tenaga kerja','jht','jkk','jkm','jp'],
  example_merchants = ARRAY['BPJS Ketenagakerjaan'],
  typical_amount_min = 50000, typical_amount_max = 10000000, merchant_patterns = ARRAY['.*bpjs.*tenaga.*']
WHERE template_name = 'Pembayaran BPJS TK' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat pembayaran zakat perusahaan (zakat penghasilan/maal).',
  keywords = ARRAY['zakat','zakat maal','zakat penghasilan','sedekah','infak','baznas'],
  example_merchants = ARRAY['BAZNAS','Dompet Dhuafa','Rumah Zakat','LAZ'],
  typical_amount_min = 100000, typical_amount_max = 100000000, merchant_patterns = ARRAY['.*baznas.*','.*dompet dhuafa.*','.*rumah zakat.*']
WHERE template_name = 'Pembayaran Zakat' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat penarikan dana perusahaan oleh pemilik untuk keperluan pribadi (prive/drawing).',
  keywords = ARRAY['prive','drawing','pengambilan pribadi','tarik dana pemilik','owner withdrawal'],
  example_merchants = NULL,
  typical_amount_min = 100000, typical_amount_max = 1000000000, merchant_patterns = NULL
WHERE template_name = 'Prive / Pengambilan Pribadi' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat penyaluran dana non-halal (bunga bank) sebagai sedekah/donasi sesuai prinsip syariah.',
  keywords = ARRAY['non-halal','sedekah','donasi','salurkan','riba','bunga bank','syariah'],
  example_merchants = ARRAY['Panti Asuhan','Yayasan','Dompet Dhuafa','Rumah Zakat'],
  typical_amount_min = 1000, typical_amount_max = 10000000, merchant_patterns = NULL
WHERE template_name = 'Salurkan Dana Non-Halal' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat sumbangan perusahaan atau kegiatan CSR (termasuk THR untuk pihak eksternal).',
  keywords = ARRAY['sumbangan','donasi','csr','thr','sosial','amal','charity','corporate social responsibility'],
  example_merchants = ARRAY['Yayasan','Panti Asuhan','Masjid','Sekolah'],
  typical_amount_min = 50000, typical_amount_max = 50000000, merchant_patterns = NULL
WHERE template_name = 'Beban Sumbangan dan CSR' AND is_current_version = true;

-- ============================================
-- TRANSFER templates
-- ============================================

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat pencatatan beban sewa kantor yang sudah terpakai (amortisasi sewa dibayar dimuka).',
  keywords = ARRAY['amortisasi','sewa','rent','prepaid','sewa dibayar dimuka','monthly amortization'],
  example_merchants = NULL,
  typical_amount_min = 100000, typical_amount_max = 50000000, merchant_patterns = NULL
WHERE template_name = 'Amortisasi Sewa Kantor' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat transfer dana antar rekening bank perusahaan sendiri.',
  keywords = ARRAY['transfer','pindah buku','antar bank','antar rekening','overbooking'],
  example_merchants = NULL,
  typical_amount_min = 100000, typical_amount_max = 10000000000, merchant_patterns = NULL
WHERE template_name = 'Transfer Antar Bank' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat transfer dari rekening bank ke kas kecil untuk kebutuhan operasional harian.',
  keywords = ARRAY['kas kecil','petty cash','pengisian kas','isi kas','tarik tunai'],
  example_merchants = NULL,
  typical_amount_min = 100000, typical_amount_max = 10000000, merchant_patterns = NULL
WHERE template_name = 'Pengisian Kas Kecil' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Template sistem untuk menginput saldo awal neraca pada awal tahun buku. Hanya digunakan sekali saat setup awal.',
  keywords = ARRAY['saldo awal','opening balance','beginning balance','tahun buku','fiscal year'],
  example_merchants = NULL,
  typical_amount_min = 0, typical_amount_max = 100000000000, merchant_patterns = NULL
WHERE template_name = 'Saldo Awal Tahun' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk koreksi pencatatan PPN yang sebelumnya dicatat sebagai biaya.',
  keywords = ARRAY['koreksi','ppn','vat','reklasifikasi','penyesuaian','correction'],
  example_merchants = NULL,
  typical_amount_min = 10000, typical_amount_max = 100000000, merchant_patterns = NULL
WHERE template_name = 'Koreksi Pencatatan PPn' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk koreksi pencatatan piutang (reklasifikasi biaya kuliah yang dicatat sebagai piutang karyawan).',
  keywords = ARRAY['koreksi','piutang','reklasifikasi','penyesuaian','correction','biaya kuliah'],
  example_merchants = NULL,
  typical_amount_min = 100000, typical_amount_max = 50000000, merchant_patterns = NULL
WHERE template_name = 'Koreksi Pencatatan Piutang' AND is_current_version = true;

-- ============================================
-- Escrow templates
-- ============================================

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat penerimaan dana user ke rekening escrow untuk transfer remittance.',
  keywords = ARRAY['escrow','deposit','user','remittance','dana masuk','titipan'],
  example_merchants = NULL,
  typical_amount_min = 100000, typical_amount_max = 1000000000, merchant_patterns = NULL
WHERE template_name = 'Escrow: Terima Dana User' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat settlement dana dari escrow ke penerima. Fee diakui sebagai pendapatan.',
  keywords = ARRAY['escrow','settlement','penerima','remittance','transfer keluar','fee'],
  example_merchants = NULL,
  typical_amount_min = 100000, typical_amount_max = 1000000000, merchant_patterns = NULL
WHERE template_name = 'Escrow: Settlement ke Penerima' AND is_current_version = true;

UPDATE journal_templates SET
  semantic_description = 'Gunakan template ini untuk mencatat transfer accumulated fee dari rekening escrow ke rekening operasional perusahaan.',
  keywords = ARRAY['escrow','fee','transfer','operasional','accumulated fee'],
  example_merchants = NULL,
  typical_amount_min = 10000, typical_amount_max = 100000000, merchant_patterns = NULL
WHERE template_name = 'Escrow: Transfer Fee ke Operasional' AND is_current_version = true;

-- Verify: count templates with metadata populated
SELECT COUNT(*) AS templates_with_metadata
FROM journal_templates
WHERE semantic_description IS NOT NULL
  AND is_current_version = true;

SELECT COUNT(*) AS total_templates
FROM journal_templates
WHERE is_current_version = true;

COMMIT;
