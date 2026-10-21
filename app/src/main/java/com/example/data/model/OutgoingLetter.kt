package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "outgoing_letters")
data class OutgoingLetter(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timestamp: String = "",
    val letterNumber: String = "",       // NOMOR SURAT (e.g. 001/YBDG/PBD/XIII/2026)
    val letterDate: String = "",         // TANGGAL SURAT (e.g. Demak, 17 Agustus 2026)
    val recipient: String = "",          // DIKIRIM KEPADA
    val subject: String = "",            // PERIHAL
    val quantity: String = "",           // JUMLAH KELUAR (e.g. 1 surat 1 lampiran)
    val archiveLink: String = "",        // KODE ARSIP / LINK SURAT
    val notes: String = ""
)
