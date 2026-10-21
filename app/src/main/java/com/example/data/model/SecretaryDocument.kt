package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "secretary_documents")
data class SecretaryDocument(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val docType: String,             // SURAT_RESMI, NOTULENSI, MAKLUMAT, SURAT_TUGAS, SURAT_IZIN
    val title: String,               // Perihal / Agenda
    val letterNumber: String = "",   // e.g. 015/PPBD/X/2026
    val recipient: String = "",      // Ditujukan kepada
    val content: String,             // Isi surat / naskah lengkap
    val dateHijri: String = "",      // e.g. 10 Rabiul Awwal 1448 H
    val dateMasehi: String = "",     // e.g. Gaji, 21 Oktober 2026
    val signer: String = "Pengasuh & Sekretaris PP Badrud Duja",
    val createdAt: Long = System.currentTimeMillis()
)
