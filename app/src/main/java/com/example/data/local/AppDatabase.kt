package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.IncomingLetter
import com.example.data.model.OutgoingLetter
import com.example.data.model.SecretaryDocument
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [IncomingLetter::class, OutgoingLetter::class, SecretaryDocument::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun incomingLetterDao(): IncomingLetterDao
    abstract fun outgoingLetterDao(): OutgoingLetterDao
    abstract fun secretaryDocumentDao(): SecretaryDocumentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sekretaris_duja.db"
                ).addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateInitialData(database)
                }
            }
        }

        private suspend fun populateInitialData(database: AppDatabase) {
            val incomingDao = database.incomingLetterDao()
            val outgoingDao = database.outgoingLetterDao()
            val documentDao = database.secretaryDocumentDao()

            // Seed real responses for Surat Masuk from PP Badrud Duja
            val initialIncoming = listOf(
                IncomingLetter(
                    timestamp = "08/09/2026 6:56:40",
                    receivedDate = "06/09/2026",
                    letterNumber = "002/PC/SP/7455/XIV/VIII/2026",
                    letterDate = "23 Agustus 2026 M",
                    sender = "Pimpinan cabang Ikatan pelajar Putri Nahdlatul Ulama Kabupaten Demak",
                    subject = "SURAT PENGESAHAN PIMPINAN CABANG IKATAN PELAJAR PUTRI NAHDLATUL ULAMA",
                    forwardedTo = "Pengurus Pimpinan Komisariat IPPNU PP Badrud Duja",
                    archiveLink = "https://drive.google.com/open?id=1A8r9-m8ohKgIjHRmRFG4wWQLXlhEPOW7"
                ),
                IncomingLetter(
                    timestamp = "08/09/2026 7:06:15",
                    receivedDate = "06/09/2026",
                    letterNumber = "002/PC/SP/XXVII/7354/VIII/26",
                    letterDate = "17 Agustus 2026 M",
                    sender = "Pimpinan Ikatan Pelajar Nahdlatul Ulama Kabupaten Demak",
                    subject = "SURAT PENGESAHAN",
                    forwardedTo = "PENGURUS PIMPINAN KOMISARIAT IKATAN PELAJAR NAHDLATUL ULAMA PESANTREN BADRUD DUJA DESA GAJI KECAMATAN GUNTUR",
                    archiveLink = "https://drive.google.com/open?id=1oB1U33l5HO8C90NJfJYWoFmT9qljUfUu"
                )
            )
            incomingDao.insertAll(initialIncoming)

            // Seed real responses for Surat Keluar from PP Badrud Duja
            val initialOutgoing = listOf(
                OutgoingLetter(
                    timestamp = "07/08/2026",
                    letterNumber = "0130-0143/PPBD/XII/2026",
                    letterDate = "Gaji, 07 Agustus 2026",
                    recipient = "Bapak/Ibuk Wali santri putra",
                    subject = "Pemberitahuan Kekurangan administrasi",
                    quantity = "14 Surat",
                    archiveLink = "lihat"
                ),
                OutgoingLetter(
                    timestamp = "10/08/2026",
                    letterNumber = "0144-0152/PPBD/XII/2026",
                    letterDate = "Gaji, 10 Agustus 2026",
                    recipient = "Bapak/ibuk Wali santri putri",
                    subject = "Pemberitahuan Kekurangan administrasi",
                    quantity = "9 surat",
                    archiveLink = "lihat"
                ),
                OutgoingLetter(
                    timestamp = "11/09/2026 0:13:38",
                    letterNumber = "001/YBDG/PBD/XIII/2026",
                    letterDate = "Demak, 17 Agustus 2026",
                    recipient = "Wali Kelas, Kelas VII MTs Sultan Fatah",
                    subject = "Perizinan",
                    quantity = "1 surat 1 lampiran",
                    archiveLink = "lihat"
                ),
                OutgoingLetter(
                    timestamp = "11/09/2026 0:17:28",
                    letterNumber = "002/YBDG/PBD/XIII/2026",
                    letterDate = "Demak, 17 Agustus 2026",
                    recipient = "Wali Kelas, Kelas VIII MTs Sultan Fatah",
                    subject = "Perizinan",
                    quantity = "1 surat 1 lampiran",
                    archiveLink = "lihat"
                ),
                OutgoingLetter(
                    timestamp = "11/09/2026 0:22:42",
                    letterNumber = "003/YBDG/PBD/XIII/2026",
                    letterDate = "Demak, 17 Agustus 2026",
                    recipient = "Wali Kelas, Kelas IX MTs Sultan Fatah",
                    subject = "Perizinan",
                    quantity = "1 surat 1 lampiran",
                    archiveLink = "lihat"
                ),
                OutgoingLetter(
                    timestamp = "11/09/2026 0:29:40",
                    letterNumber = "004/YBDG/PBD/XIII/2026",
                    letterDate = "Demak, 17 Agustus 2026",
                    recipient = "Wali Kelas, Kelas X, XI, XII MA Sultan Fatah",
                    subject = "Perizinan",
                    quantity = "1 surat 1 lampiran",
                    archiveLink = "lihat"
                ),
                OutgoingLetter(
                    timestamp = "11/09/2026 0:41:17",
                    letterNumber = "005/YBDG/PBD/XIII/2026",
                    letterDate = "Demak, 15 Agustus 2026",
                    recipient = "Kepala Sekolah MA Hidayatul Mubtadi’in",
                    subject = "Perizinan",
                    quantity = "1 surat 1 lampiran",
                    archiveLink = "lihat"
                ),
                OutgoingLetter(
                    timestamp = "11/09/2026 0:47:37",
                    letterNumber = "006/YBDG/PBD/XIII/2026",
                    letterDate = "Demak, 17 Agustus 2026",
                    recipient = "Wali Kelas, Kelas VII MTs Sultan Fatah",
                    subject = "Perizinan",
                    quantity = "1 surat 1 lampiran",
                    archiveLink = "lihat"
                ),
                OutgoingLetter(
                    timestamp = "11/09/2026 0:53:14",
                    letterNumber = "007/YBDG/PBD/XIII/2026",
                    letterDate = "Demak, 17 Agustus 2026",
                    recipient = "Wali Kelas, Kelas VIII MTs Sultan Fatah",
                    subject = "Perizinan",
                    quantity = "1 surat 1 lampiran",
                    archiveLink = "lihat"
                ),
                OutgoingLetter(
                    timestamp = "11/09/2026 0:56:10",
                    letterNumber = "008/YBDG/PBD/XIII/2026",
                    letterDate = "Demak, 17 Agustus 2026",
                    recipient = "Wali Kelas, Kelas IX MTs Sultan Fatah",
                    subject = "Perizinan",
                    quantity = "1 surat 1 lampiran",
                    archiveLink = "lihat"
                ),
                OutgoingLetter(
                    timestamp = "11/09/2026 0:59:41",
                    letterNumber = "009/YBDG/PBD/XIII/2026",
                    letterDate = "Demak, 17 Agustus 2026",
                    recipient = "Wali Kelas, Kelas X, XI, XII MA Sultan Fatah",
                    subject = "Perizinan",
                    quantity = "1 surat 1 lampiran",
                    archiveLink = "lihat"
                ),
                OutgoingLetter(
                    timestamp = "11/09/2026 1:06:20",
                    letterNumber = "010/YBDG/PBD/VII/2026",
                    letterDate = "Gaji, 27 Agustus 2026",
                    recipient = "BANK JATENG",
                    subject = "Permohonan Pembukaan Rekening",
                    quantity = "1 surat",
                    archiveLink = "lihat"
                ),
                OutgoingLetter(
                    timestamp = "11/09/2026 1:08:40",
                    letterNumber = "0153-0164/PPBD/IX/2026",
                    letterDate = "Gaji, 10 September 2026",
                    recipient = "Bapak/Ibu Wali Santri Putra",
                    subject = "Pemberitahuan Kekurangan Administrasi",
                    quantity = "12 surat",
                    archiveLink = "lihat"
                )
            )
            outgoingDao.insertAll(initialOutgoing)

            // Seed an authentic initial official letter & notulensi
            documentDao.insert(
                SecretaryDocument(
                    docType = "SURAT_RESMI",
                    title = "Surat Permohonan Dispensasi Kegiatan Santri",
                    letterNumber = "011/YBDG/PBD/XIII/2026",
                    recipient = "Kepala MTs & MA Sultan Fatah",
                    content = """
                    بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ
                    
                    Nomor   : 011/YBDG/PBD/XIII/2026
                    Lampiran: 1 (satu) Berkas Daftar Santri
                    Perihal : Permohonan Dispensasi Belajar Santri
                    
                    Kepada Yth.
                    Bapak/Ibu Kepala MTs & MA Sultan Fatah
                    di Tempat
                    
                    Assalamu’alaikum Warahmatullahi Wabarakatuh
                    
                    Segala puji bagi Allah SWT yang senantiasa melimpahkan taufiq dan hidayah-Nya kepada kita semua. Sholawat serta salam semoga tercurah kepada junjungan kita Nabi Agung Muhammad SAW, keluarga, dan para sahabatnya.
                    
                    Sehubungan dengan diadakannya kegiatan Ziarah dan Khotmil Qur’an Pondok Pesantren Badrud Duja Desa Gaji Kecamatan Guntur Kabupaten Demak, dengan ini kami selaku Pengurus memohon dispensasi ketidakhadiran kegiatan belajar mengajar bagi para santri kami yang bersekolah di MTs & MA Sultan Fatah pada:
                    
                    Hari / Tanggal : Kamis, 22 Oktober 2026
                    Waktu          : Pukul 07.30 WIB - Selesai
                    Tempat         : Kompleks Makam Auliya & Pesantren Badrud Duja
                    
                    Demikian surat permohonan ini kami sampaikan. Atas perhatian, kebijaksanaan, dan kerjasama Bapak/Ibu pimpinan madrasah, kami haturkan jazakumullah khairan katsiran.
                    
                    Wallahul Muwaffiq Ila Aqwamith Thariq
                    Wassalamu’alaikum Warahmatullahi Wabarakatuh
                    
                    Gaji, 20 Oktober 2026
                    PENGURUS PONDOK PESANTREN BADRUD DUJA
                    
                    Sekretaris Pesantren,
                    Wahyu Bintoro
                    """.trimIndent(),
                    dateHijri = "09 Rabiul Akhir 1448 H",
                    dateMasehi = "Gaji, 20 Oktober 2026"
                )
            )

            documentDao.insert(
                SecretaryDocument(
                    docType = "NOTULENSI",
                    title = "Notulensi Musyawarah Pengurus & Dewan Asatidz: Persiapan Ujian Semester",
                    letterNumber = "NOT/PPBD/X/2026",
                    recipient = "Majelis Asatidz & Pengurus Harian",
                    content = """
                    NOTULENSI MUSYAWARAH ASATIDZ PONDOK PESANTREN BADRUD DUJA
                    
                    Hari / Tanggal  : Ahad, 18 Oktober 2026
                    Waktu           : 20.00 WIB - 22.30 WIB
                    Tempat          : Musholla Utama PP Badrud Duja Gaji
                    Pimpinan Rapat  : Pengasuh Pondok Pesantren Badrud Duja
                    Notulis         : Sekretaris Pesantren (Wahyu Bintoro)
                    Peserta Hadir   : 16 Asatidz & Pengurus Harian
                    
                    POKOK PEMBAHASAN:
                    1. Evaluasi kedisiplinan dan absensi pengajian kitab kuning santri putra dan putri.
                    2. Jadwal Imtihan/Ujian Semester Ganjil Madrasah Diniyah Badrud Duja.
                    3. Penertiban perizinan keluar santri ke sekolah formal (MTs/MA Sultan Fatah).
                    
                    HASIL KEPUTUSAN MUSYAWARAH:
                    1. Imtihan Diniyah akan dimulai pada tanggal 10 November 2026.
                    2. Setiap santri yang mengajukan izin keluar wajib membawa kartu izin resmi bertanda tangan sekretaris pondok.
                    3. Surat pemberitahuan kekurangan administrasi telah dikirimkan bertahap kepada seluruh wali santri.
                    
                    TINDAK LANJUT / TUGAS PENGURUS:
                    - Bagian Kurikulum: Menyusun naskah soal imtihan paling lambat 1 November 2026.
                    - Sekretaris: Mencetak format rekap absensi dan kartu ujian santri.
                    - Bagian Keamanan: Memperketat pos jaga gerbang santri.
                    """.trimIndent(),
                    dateHijri = "07 Rabiul Akhir 1448 H",
                    dateMasehi = "Gaji, 18 Oktober 2026"
                )
            )
        }
    }
}
