# Asisten AI Sekretaris Pondok Pesantren Badrud Duja

Aplikasi Android terpadu yang dirancang khusus untuk mendukung tugas kesekretariatan **Pondok Pesantren Badrud Duja (Desa Gaji, Kec. Guntur, Kab. Demak)**:
1. **Generator Surat Resmi Pesantren berbasis AI (Gemini 3.5 Flash)** dengan format penomoran resmi lembaga (`.../PPBD/...` dan `.../YBDG/PBD/...`), kop surat syar'i, dan titimangsa khas Gaji/Demak.
2. **Buku Rekap Hasil Pengisian Agenda Surat Masuk & Keluar** yang memuat data riil persuratan pesantren, tautan langsung ke berkas arsip Google Drive, pencarian cepat, serta form input baru.
3. **Akses Langsung ke Google Formulir Resmi** (Formulir Masuk & Keluar) baik via *In-App Sheet* maupun *Browser Eksternal*.
4. **Notulensi Musyawarah Asatidz & Arsip Digital Dokumen** dengan pratinjau lembar kertas formal.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> Penyesuaian arsitektur berdasarkan lampiran data hasil pengisian resmi pengguna:

- **Rekapitulasi Data Riil Terintegrasi (Baru)**:
  - Aplikasi langsung dibekali data awal (*seed data*) dari jawaban formulir nyata yang Anda lampirkan:
    - **Data Surat Masuk**: Pengesahan PC IPNU & IPPNU Demak untuk PK Pesantren Badrud Duja, lengkap dengan link arsip Google Drive.
    - **Data Surat Keluar**: 13+ riwayat surat resmi keluar (Pemberitahuan administrasi wali santri, perizinan santri MTs/MA Sultan Fatah & MA Hidayatul Mubtadi'in, permohonan pembukaan rekening Bank Jateng).
  - Pengguna dapat melihat daftar data ini dalam bentuk tabel kartu berdesain rapi, mencari dokumen seketika, dan membuka dokumen arsip di Google Drive dengan satu sentuhan.
- **Tautan Resmi Google Formulir**:
  - 📥 **Agenda Surat Masuk**:  
    `https://docs.google.com/forms/d/e/1FAIpQLSdKiztzyRtPBLXbFPcIJGnZe0OHiIKvIqgm0eUDT--asxdz8A/viewform?usp=dialog`
  - 📤 **Agenda Surat Keluar**:  
    `https://docs.google.com/forms/d/e/1FAIpQLSdAVJPEkfSxW-K4mdqECCKKkWDVsCqjaVGYasfrN6Wjl2RUqg/viewform?usp=dialog`
- **Format Otentik Penomoran Surat Badrud Duja**:
  - AI disetel mengenali pola penomoran resmi pesantren:  
    `[No]/PPBD/[BulanRomawi]/[Tahun]` dan `[No]/YBDG/PBD/[Klasifikasi]/[Tahun]` dengan titimangsa default `Gaji, Demak`.

---

## 1. Overview & Core Concept

- **Tujuan Aplikasi**: Memusatkan dan mendigitalisasi seluruh administrasi persuratan dan rekaman agenda Pondok Pesantren Badrud Duja dalam satu genggaman sekretaris: menulis draf surat kilat dengan AI, mengecek riwayat surat keluar/masuk beserta lampirannya di Drive, serta mengisi formulir agenda baru.
- **Sasaran Pengguna**: Sekretaris Pesantren & Pengurus Bagian Tata Usaha (TU) PP Badrud Duja, Desa Gaji, Kec. Guntur, Kab. Demak.
- **Nilai Utama**:
  1. *Transparansi & Tertib Administrasi*: Semua nomor surat yang pernah keluar tercatat rapi sehingga tidak terjadi nomor urut ganda saat mengirim surat ke sekolah mitra (MTs/MA Sultan Fatah) maupun instansi luar.
  2. *Akses Kilat Berkas*: Tautan Google Drive pada agenda surat masuk langsung bisa dibuka untuk membaca lampiran SK/surat fisik yang telah dipindai.
  3. *AI Cerdas Sesuai Tradisi*: Menghasilkan draf surat dengan gaya bahasa santun, doa pembuka/penutup syar'i, dan struktur hierarki pesantren.

---

## 2. User Experience & Visual Design

### Alur Kerja Pengguna (Key User Flows)

```
┌────────────────────────────────────────────────────────────────────────┐
│                   DASHBOARD UTAMA KESEKRETARIATAN                     │
│  • Kartu Statistik: Jumlah Surat Masuk & Keluar Terdaftar             │
│  • Tombol Cepat: [📥 Form Masuk] [📤 Form Keluar] [✨ Buat Surat AI]  │
└──────────────┬────────────────────────┬──────────────────────┬─────────┘
               │                        │                      │
               ▼                        ▼                      ▼
┌──────────────────────────┐ ┌────────────────────┐ ┌────────────────────┐
│   BUKU REKAP AGENDA      │ │ GENERATOR SURAT AI │ │ NOTULENSI RAPAT AI │
│   (MASUK & KELUAR)       │ │                    │ │                    │
│ • Tab Surat Masuk:       │ │ • Template Badrud  │ │ • Judul Musyawarah │
│   Daftar pengirim, nomor,│ │   Duja (YBDG/PPBD) │ │ • Catatan Asatidz  │
│   perihal & link Drive   │ │ • Generate Gemini  │ │ • Rangkuman AI     │
│ • Tab Surat Keluar:      │ │ • Pratinjau Kertas │ │ • Ekspor & Bagikan │
│   Nomor, tujuan, tanggal │ │ • Salin ke Form    │ │                    │
│ • Form Tambah Entri Baru │ │   Keluar 1-Klik    │ │                    │
│ • Buka Google Form       │ └────────────────────┘ └────────────────────┘
└──────────────────────────┘            │                      │
               │                        └──────────┬───────────┘
               ▼                                   ▼
┌────────────────────────────────────────────────────────────────────────┐
│                      ARSIP DOKUMEN DIGITAL (ROOM)                      │
│ • Pratinjau Lembar Surat Formal A4 dengan Kop Resmi PP Badrud Duja     │
│ • Salin Teks / Bagikan Surat via WhatsApp ke Pengurus atau Mitra       │
└────────────────────────────────────────────────────────────────────────┘
```

### Rincian Fitur Buku Rekap Agenda (Data Riil)

1. **Tab Agenda Surat Masuk**:
   - Menampilkan kartu surat masuk:
     - *Badge Nomor Surat* (misal: `002/PC/SP/7455/XIV/VIII/2026`)
     - *Pengirim* (misal: Pimpinan Cabang IPPNU Kab. Demak)
     - *Perihal* (Surat Pengesahan Pimpinan Cabang IPPNU)
     - *Diteruskan Kepada* (Pengurus PK IPPNU PP Badrud Duja)
     - *Tautan Google Drive* aktif yang membuka dokumen arsip digital
   - Tombol **"Isi Google Form Surat Masuk"** untuk mencatat surat masuk baru.
2. **Tab Agenda Surat Keluar**:
   - Menampilkan kartu surat keluar:
     - *Badge Nomor Registrasi* (misal: `001/YBDG/PBD/XIII/2026`, `0130-0143/PPBD/XII/2026`)
     - *Tujuan* (Wali Kelas MTs/MA Sultan Fatah, Wali Santri, Bank Jateng)
     - *Titimangsa* (Gaji / Demak)
     - *Perihal & Jumlah Berkas* (Perizinan santri, kekurangan administrasi)
   - Tombol **"Isi Google Form Surat Keluar"** untuk mencatat surat keluar baru.
3. **Pencarian & Filter Cepat**:
   - Kotak pencarian interaktif untuk menemukan surat berdasarkan nomor, perihal, atau nama instansi.

---

## 3. Key Product Decisions & Trade-Offs

- **Penggabungan Data Offline & Online**:
  - Data respons awal dari spreadsheet pengguna dimasukkan ke dalam basis data lokal (Room) agar sekretaris bisa membuka riwayat surat masuk dan keluar secara instan meski koneksi internet sedang lambat atau offline.
  - Untuk pencatatan baru, disediakan dua opsi: langsung mengisi ke Google Form resmi (agar masuk ke spreadsheet induk) atau mencatat draf cepat di aplikasi.
- **Model AI**: `gemini-3.5-flash` via Retrofit REST API.
  - Menyediakan *context prompt* khusus sejarah dan struktur kepengurusan Pesantren Badrud Duja Gaji Demak.
- **Tema Visual**:
  - *Duja Emerald Green* (`#0E5A36`) dan *Islamic Gold* (`#C5A059`) pada kartu dan bilah aplikasi, berpadu dengan latar belakang putih kertas resmi.

---

## 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────────────────────┐
│                        ANTARMUKA (Compose UI)                          │
│                                                                        │
│  ├── DashboardScreen (Statistik & Menu Cepat)                          │
│  ├── AgendaScreen (Tab Rekap Surat Masuk & Keluar + Akses Google Form) │
│  ├── AgendaDetailDialog / Sheet (Detail Data & Buka Link Drive)        │
│  ├── LetterGeneratorScreen (AI Drafter Format PPBD/YBDG)               │
│  ├── MeetingMinutesScreen (Notulensi Musyawarah Asatidz)               │
│  └── DocumentArchiveScreen (Pratinjau Kertas Surat Resmi & Ekspor)     │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                     VIEWMODEL LAYER (Kotlin)                           │
│  SecretaryViewModel:                                                   │
│  - incomingLetters: StateFlow<List<IncomingLetter>> (Data Masuk)       │
│  - outgoingLetters: StateFlow<List<OutgoingLetter>> (Data Keluar)      │
│  - letterDraftState: StateFlow<GenerationState> (Surat AI)             │
│  - meetingDraftState: StateFlow<GenerationState> (Notulensi AI)        │
│  - searchQuery & activeTab                                             │
└───────────────────┬─────────────────────────────────┬──────────────────┘
                    │                                 │
                    ▼                                 ▼
┌──────────────────────────────────────┐  ┌──────────────────────────────┐
│       AI GENERATION SERVICE          │  │       ROOM PERSISTENCE       │
│  GeminiApiService (Retrofit)         │  │  AppDatabase:                │
│  - Model: gemini-3.5-flash           │  │  • IncomingLetterDao         │
│  - System Prompt Syar'i Badrud Duja  │  │  • OutgoingLetterDao         │
│                                      │  │  • SecretaryDocumentDao      │
└──────────────────────────────────────┘  └──────────────────────────────┘
```

### Entitas Data Riil

1. **`IncomingLetterEntity`** (Surat Masuk):
   - `id`: Int (PrimaryKey autoGenerate)
   - `timestamp`: String
   - `receivedDate`: String (TANGGAL TERIMA)
   - `letterNumber`: String (NOMOR SURAT)
   - `letterDate`: String (TANGGAL SURAT DIBUAT)
   - `sender`: String (PENGIRIM)
   - `subject`: String (PERIHAL)
   - `forwardedTo`: String (DITERUSKAN KEPADA)
   - `archiveLink`: String (KODE ARSIP / LINK DRIVE)

2. **`OutgoingLetterEntity`** (Surat Keluar):
   - `id`: Int (PrimaryKey autoGenerate)
   - `timestamp`: String
   - `letterNumber`: String (NOMOR SURAT)
   - `letterDate`: String (TANGGAL SURAT)
   - `recipient`: String (DIKIRIM KEPADA)
   - `subject`: String (PERIHAL)
   - `quantity`: String (JUMLAH KELUAR)
   - `archiveLink`: String (KODE ARSIP/LINK SURAT)

3. **`SecretaryDocumentEntity`** (Draf Surat Resmi & Notulensi Hasil AI).
