package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.IncomingLetter
import com.example.data.model.OutgoingLetter
import com.example.data.model.SecretaryDocument
import com.example.data.remote.GeminiApiClient
import kotlinx.coroutines.flow.Flow

class SecretaryRepository(private val database: AppDatabase) {
    private val incomingDao = database.incomingLetterDao()
    private val outgoingDao = database.outgoingLetterDao()
    private val documentDao = database.secretaryDocumentDao()

    val incomingLetters: Flow<List<IncomingLetter>> = incomingDao.getAll()
    val outgoingLetters: Flow<List<OutgoingLetter>> = outgoingDao.getAll()
    val documents: Flow<List<SecretaryDocument>> = documentDao.getAll()

    suspend fun addIncomingLetter(letter: IncomingLetter): Long = incomingDao.insert(letter)
    suspend fun deleteIncomingLetter(id: Int) = incomingDao.deleteById(id)

    suspend fun addOutgoingLetter(letter: OutgoingLetter): Long = outgoingDao.insert(letter)
    suspend fun deleteOutgoingLetter(id: Int) = outgoingDao.deleteById(id)

    suspend fun saveDocument(doc: SecretaryDocument): Long = documentDao.insert(doc)
    suspend fun deleteDocument(id: Int) = documentDao.deleteById(id)
    suspend fun getDocumentById(id: Int) = documentDao.getById(id)

    suspend fun generateOfficialLetter(
        category: String,
        recipient: String,
        subject: String,
        keyPoints: String,
        letterNumberHint: String
    ): Result<String> {
        val systemPrompt = """
            Anda adalah Asisten AI Sekretaris Resmi Pondok Pesantren Badrud Duja (Yayasan Badrud Duja Gaji / YBDG), beralamat di Desa Gaji, Kecamatan Guntur, Kabupaten Demak, Jawa Tengah.
            
            Tugas Anda adalah menyusun naskah surat resmi berbahasa Indonesia yang sangat baku, santun, dan sesuai kaidah tata naskah dinas kepesantrenan Nahdlatul Ulama / Pesantren Salaf-Modern di Indonesia.
            
            Aturan Wajib Struktur Surat:
            1. Paling atas sertakan Basmalah dalam tulisan Arab: بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ
            2. Nomor Surat: Gunakan kode standar lembaga Badrud Duja (misal format: $letterNumberHint atau .../PPBD/.../2026 atau .../YBDG/PBD/.../2026).
            3. Lampiran dan Perihal yang jelas.
            4. Tujuan Surat: "Kepada Yth. $recipient di Tempat"
            5. Salam Pembuka Syar'i: "Assalamu’alaikum Warahmatullahi Wabarakatuh"
            6. Kalimat Tahmid & Sholawat pembuka yang khidmat.
            7. Isi Surat: Menguraikan poin-poin yang diminta secara lugas, berwibawa, dan sopan.
            8. Kalimat penutup doa jazakumullah khair.
            9. Salam Penutup Syar'i:
               "Wallahul Muwaffiq Ila Aqwamith Thariq"
               "Wassalamu’alaikum Warahmatullahi Wabarakatuh"
            10. Titimangsa di kanan bawah: "Gaji, [Tanggal Hari Ini]" dengan kalender Hijriah dan Masehi.
            11. Blok tanda tangan resmi:
                PENGURUS PONDOK PESANTREN BADRUD DUJA
                Pengasuh Pesantren / Sekretaris Pesantren.
                
            Jangan tampilkan markdown pembungkus codeblock (seperti ```markdown), tampilkan teks surat langsung yang siap dicetak dan dibagikan.
        """.trimIndent()

        val userPrompt = """
            Susunkan naskah surat resmi pesantren:
            - Kategori Surat: $category
            - Nomor Surat: $letterNumberHint
            - Tujuan Penerima: $recipient
            - Perihal: $subject
            - Poin-Poin Utama / Keterangan Tambahan:
            $keyPoints
        """.trimIndent()

        return GeminiApiClient.askGemini(userPrompt, systemPrompt)
    }

    suspend fun generateMeetingMinutes(
        meetingTitle: String,
        meetingDate: String,
        location: String,
        attendees: String,
        rawNotes: String
    ): Result<String> {
        val systemPrompt = """
            Anda adalah Asisten Notulensi Rapat Pondok Pesantren Badrud Duja Desa Gaji, Kec. Guntur, Kab. Demak.
            Susunkan ringkasan hasil musyawarah / rapat dewan asatidz dan pengurus harian secara rapi, sistematis, dan jelas.
            
            Format Notulensi:
            NOTULENSI MUSYAWARAH PONDOK PESANTREN BADRUD DUJA
            - Agenda / Judul Rapat:
            - Hari / Tanggal:
            - Waktu & Tempat:
            - Pimpinan Musyawarah:
            - Peserta Hadir:
            
            I. POKOK-POKOK PEMBAHASAN
            II. HASIL KEPUTUSAN MUSYAWARAH
            III. DISTRIBUSI TUGAS & TINDAK LANJUT (Action Items & Penanggung Jawab)
            
            Tampilkan dalam format teks rapi dan terstruktur tanpa pembungkus codeblock markdown.
        """.trimIndent()

        val userPrompt = """
            Buatkan notulensi rapat resmi:
            - Judul Agenda: $meetingTitle
            - Hari/Tanggal: $meetingDate
            - Tempat: $location
            - Peserta: $attendees
            - Catatan Mentah Poin Pembahasan:
            $rawNotes
        """.trimIndent()

        return GeminiApiClient.askGemini(userPrompt, systemPrompt)
    }

    suspend fun extractIncomingMailInfo(rawText: String): Result<String> {
        val systemPrompt = """
            Anda adalah Asisten Administrasi Persuratan Pondok Pesantren Badrud Duja.
            Dari teks surat masuk yang diberikan, ekstraklah data penting untuk mempermudah sekretaris mengisi buku agenda:
            1. Nomor Surat
            2. Tanggal Surat
            3. Instansi / Nama Pengirim
            4. Perihal Surat
            5. Ringkasan Singkat Inti Surat
            6. Rekomendasi Disposisi / Diteruskan Kepada (misal: Pengasuh, Pengurus Harian, Keamanan, atau Madrasah Diniyah)
            
            Tuliskan ringkas dan jelas dalam butir-butir agar mudah disalin satu-persatu.
        """.trimIndent()

        return GeminiApiClient.askGemini("Ekstrak data dari naskah surat masuk berikut:\n\n$rawText", systemPrompt)
    }
}
