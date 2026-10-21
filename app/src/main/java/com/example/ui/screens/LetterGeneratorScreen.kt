package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.GenerationUiState
import com.example.ui.SecretaryViewModel
import com.example.ui.theme.DujaEmeraldPrimary
import com.example.ui.theme.DujaGoldSecondary

data class LetterPreset(
    val title: String,
    val defaultRecipient: String,
    val defaultSubject: String,
    val defaultPoints: String
)

private val presets = listOf(
    LetterPreset(
        title = "Dispensasi MTs/MA Sultan Fatah",
        defaultRecipient = "Kepala MTs & MA Sultan Fatah",
        defaultSubject = "Permohonan Dispensasi Belajar Santri",
        defaultPoints = "Memohon izin dispensasi tidak mengikuti KBM untuk santri karena mengikuti kegiatan Khotmil Qur'an dan ziarah tahunan pondok pesantren."
    ),
    LetterPreset(
        title = "Pemberitahuan Wali Santri",
        defaultRecipient = "Bapak/Ibu Wali Santri Putra & Putri",
        defaultSubject = "Pemberitahuan Administrasi & Jadwal Imtihan",
        defaultPoints = "Menyampaikan jadwal imtihan akhir semester, rincian kekurangan syahriah/administrasi pondok, dan jadwal kepulangan libur semester ganjil."
    ),
    LetterPreset(
        title = "Permohonan Bank Jateng",
        defaultRecipient = "Pimpinan Cabang PT Bank Pembangunan Daerah Jawa Tengah (Bank Jateng)",
        defaultSubject = "Permohonan Pembukaan Rekening Pondok",
        defaultPoints = "Mengajukan permohonan pembukaan rekening giro/tabungan atas nama Yayasan Badrud Duja Gaji untuk operasional santri."
    ),
    LetterPreset(
        title = "Izin Pulang Santri",
        defaultRecipient = "Wali Kelas & Bagian Keamanan Pesantren",
        defaultSubject = "Surat Keterangan Izin Pulang Santri",
        defaultPoints = "Santri atas nama Ahmad Fauzi izin pulang karena keperluan keluarga selama 3 hari terhitung mulai besok."
    ),
    LetterPreset(
        title = "Undangan Rapat Asatidz",
        defaultRecipient = "Dewan Asatidz & Pengurus Pesantren",
        defaultSubject = "Undangan Musyawarah Evaluasi Pembelajaran Diniyah",
        defaultPoints = "Mengundang seluruh asatidz untuk musyawarah penentuan jadwal imtihan dan evaluasi kurikulum kitab kuning hari Sabtu malam Ahad di Aula Pondok."
    )
)

@Composable
fun LetterGeneratorScreen(
    viewModel: SecretaryViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val letterUiState by viewModel.letterGenerationState.collectAsStateWithLifecycle()

    var selectedPresetTitle by remember { mutableStateOf(presets[0].title) }
    var letterNumber by remember { mutableStateOf(viewModel.generateNextOutgoingNumber()) }
    var recipient by remember { mutableStateOf(presets[0].defaultRecipient) }
    var subject by remember { mutableStateOf(presets[0].defaultSubject) }
    var keyPoints by remember { mutableStateOf(presets[0].defaultPoints) }
    var generatedLetterContent by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("letter_generator_screen")
    ) {
        // Header
        Text(
            text = "Generator Surat Resmi Syar'i",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Draf otomatis berstandar Pondok Pesantren Badrud Duja (YBDG / PPBD)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Preset Chips
        Text(
            text = "Pilih Template Cepat:",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            presets.forEach { preset ->
                FilterChip(
                    selected = selectedPresetTitle == preset.title,
                    onClick = {
                        selectedPresetTitle = preset.title
                        recipient = preset.defaultRecipient
                        subject = preset.defaultSubject
                        keyPoints = preset.defaultPoints
                    },
                    label = { Text(preset.title, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Input Form
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(14.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Nomor Surat with auto generator
                OutlinedTextField(
                    value = letterNumber,
                    onValueChange = { letterNumber = it },
                    label = { Text("Nomor Registrasi Surat") },
                    trailingIcon = {
                        IconButton(onClick = { letterNumber = viewModel.generateNextOutgoingNumber() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Perbarui Nomor")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_nomor_surat"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = recipient,
                    onValueChange = { recipient = it },
                    label = { Text("Tujuan / Penerima Surat") },
                    placeholder = { Text("contoh: Kepala MTs Sultan Fatah") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_penerima_surat"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Perihal Surat") },
                    placeholder = { Text("contoh: Permohonan Dispensasi Belajar Santri") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_perihal_surat"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = keyPoints,
                    onValueChange = { keyPoints = it },
                    label = { Text("Poin-Poin Pokok Inti Surat") },
                    placeholder = { Text("Tuliskan garis besar apa yang ingin disampaikan...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("input_poin_surat"),
                    maxLines = 5
                )

                Button(
                    onClick = {
                        viewModel.generateLetter(
                            category = selectedPresetTitle,
                            recipient = recipient,
                            subject = subject,
                            keyPoints = keyPoints,
                            letterNumber = letterNumber
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_generate_letter"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    enabled = recipient.isNotBlank() && subject.isNotBlank() && letterUiState !is GenerationUiState.Loading
                ) {
                    if (letterUiState is GenerationUiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Gemini AI menyusun surat...")
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Susun Naskah Surat AI")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Result Section
        when (val state = letterUiState) {
            is GenerationUiState.Success -> {
                generatedLetterContent = state.content
                LetterResultCard(
                    content = generatedLetterContent,
                    onContentChange = { generatedLetterContent = it },
                    onSaveToArchive = {
                        viewModel.saveGeneratedLetterToArchive(
                            title = subject,
                            letterNumber = letterNumber,
                            recipient = recipient,
                            content = generatedLetterContent
                        )
                        Toast.makeText(context, "Surat berhasil disimpan ke Arsip Dokumen", Toast.LENGTH_SHORT).show()
                    },
                    onRegisterOutgoing = {
                        viewModel.addManualOutgoingLetter(
                            letterNumber = letterNumber,
                            letterDate = "Gaji, Demak",
                            recipient = recipient,
                            subject = subject,
                            quantity = "1 surat",
                            driveLink = "lihat"
                        )
                        Toast.makeText(context, "Nomor surat telah didaftarkan ke Buku Agenda Surat Keluar", Toast.LENGTH_LONG).show()
                    },
                    onCopy = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Surat PP Badrud Duja", generatedLetterContent))
                        Toast.makeText(context, "Teks surat disalin ke clipboard", Toast.LENGTH_SHORT).show()
                    },
                    onShare = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, generatedLetterContent)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Bagikan Surat Resmi"))
                    }
                )
            }
            is GenerationUiState.Error -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Gagal Menyusun Surat",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
            else -> {}
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun LetterResultCard(
    content: String,
    onContentChange: (String) -> Unit,
    onSaveToArchive: () -> Unit,
    onRegisterOutgoing: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFCFCF9) // Parchment paper style
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pratinjau Naskah Surat Resmi",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = DujaEmeraldPrimary
                )
                Text(
                    text = "Bisa diedit langsung",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Editable Text Area
            OutlinedTextField(
                value = content,
                onValueChange = onContentChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp),
                textStyle = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Serif,
                    lineHeight = 20.sp
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onSaveToArchive,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Simpan Arsip", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onRegisterOutgoing,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.PostAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Catat Agenda", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCopy,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Salin Teks", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onShare,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Bagikan", fontSize = 12.sp)
                }
            }
        }
    }
}
