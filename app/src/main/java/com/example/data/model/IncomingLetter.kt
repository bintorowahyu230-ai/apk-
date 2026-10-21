package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "incoming_letters")
data class IncomingLetter(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timestamp: String = "",
    val receivedDate: String = "",       // TANGGAL TERIMA
    val letterNumber: String = "",       // NOMOR SURAT
    val letterDate: String = "",         // TANGGAL SURAT DIBUAT
    val sender: String = "",             // PENGIRIM
    val subject: String = "",            // PERIHAL
    val forwardedTo: String = "",        // DITERUSKAN KEPADA
    val archiveLink: String = "",        // KODE ARSIP / LINK GOOGLE DRIVE
    val notes: String = ""
)
