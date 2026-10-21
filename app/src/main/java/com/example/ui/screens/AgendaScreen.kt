package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveFolderUpload
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AgendaConfig
import com.example.data.model.IncomingLetter
import com.example.data.model.OutgoingLetter
import com.example.ui.AgendaTab
import com.example.ui.GenerationUiState
import com.example.ui.SecretaryViewModel
import com.example.ui.theme.DujaEmeraldPrimary
import com.example.ui.theme.DujaGoldSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaScreen(
    viewModel: SecretaryViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentTab by viewModel.agendaTab.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val incomingList by viewModel.filteredIncoming.collectAsStateWithLifecycle()
    val outgoingList by viewModel.filteredOutgoing.collectAsStateWithLifecycle()
    val mailExtractionState by viewModel.mailExtractionState.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var showExtractAiDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .testTag("agenda_screen")
        ) {
            // Tabs: Surat Masuk vs Surat Keluar
            PrimaryTabRow(
                selectedTabIndex = if (currentTab == AgendaTab.SURAT_MASUK) 0 else 1,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = {
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(if (currentTab == AgendaTab.SURAT_MASUK) 0 else 1),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                Tab(
                    selected = currentTab == AgendaTab.SURAT_MASUK,
                    onClick = { viewModel.setAgendaTab(AgendaTab.SURAT_MASUK) },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CallReceived, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Surat Masuk (${incomingList.size})", fontWeight = FontWeight.SemiBold)
                        }
                    },
                    modifier = Modifier.testTag("tab_surat_masuk")
                )
                Tab(
                    selected = currentTab == AgendaTab.SURAT_KELUAR,
                    onClick = { viewModel.setAgendaTab(AgendaTab.SURAT_KELUAR) },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CallMade, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Surat Keluar (${outgoingList.size})", fontWeight = FontWeight.SemiBold)
                        }
                    },
                    modifier = Modifier.testTag("tab_surat_keluar")
                )
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Cari nomor, pengirim, tujuan, atau perihal...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Hapus")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("search_agenda_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Action Toolbar (Buttons to Google Form & AI Assistant)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val currentFormUrl = if (currentTab == AgendaTab.SURAT_MASUK) {
                    AgendaConfig.DEFAULT_INCOMING_FORM_URL
                } else {
                    AgendaConfig.DEFAULT_OUTGOING_FORM_URL
                }
                val formLabel = if (currentTab == AgendaTab.SURAT_MASUK) "Isi Form Masuk" else "Isi Form Keluar"

                Button(
                    onClick = {
                        viewModel.openInAppForm(
                            url = currentFormUrl,
                            title = formLabel
                        )
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentTab == AgendaTab.SURAT_MASUK) DujaEmeraldPrimary else DujaGoldSecondary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(formLabel, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }

                if (currentTab == AgendaTab.SURAT_MASUK) {
                    OutlinedButton(
                        onClick = { showExtractAiDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ekstrak AI", fontSize = 12.sp)
                    }
                }
            }

            // Content List
            if (currentTab == AgendaTab.SURAT_MASUK) {
                if (incomingList.isEmpty()) {
                    EmptyState(message = "Belum ada agenda surat masuk yang tercatat.")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(incomingList, key = { it.id }) { item ->
                            IncomingLetterCard(
                                letter = item,
                                onOpenDrive = { link ->
                                    if (link.startsWith("http")) {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                                        context.startActivity(intent)
                                    } else {
                                        Toast.makeText(context, "Tautan arsip: $link", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                onCopy = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Info Surat Masuk", "${item.letterNumber} - ${item.sender}"))
                                    Toast.makeText(context, "Data surat disalin ke clipboard", Toast.LENGTH_SHORT).show()
                                },
                                onDelete = { viewModel.deleteIncomingLetter(item.id) }
                            )
                        }
                    }
                }
            } else {
                if (outgoingList.isEmpty()) {
                    EmptyState(message = "Belum ada agenda surat keluar yang tercatat.")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(outgoingList, key = { it.id }) { item ->
                            OutgoingLetterCard(
                                letter = item,
                                onCopy = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Info Surat Keluar", "${item.letterNumber} - ${item.recipient}"))
                                    Toast.makeText(context, "Nomor & penerima disalin", Toast.LENGTH_SHORT).show()
                                },
                                onDelete = { viewModel.deleteOutgoingLetter(item.id) }
                            )
                        }
                    }
                }
            }
        }

        // Floating Action Button to Add New Entry Manually
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 16.dp)
                .testTag("fab_tambah_agenda"),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Tambah Entri")
        }
    }

    // Dialog Tambah Entri Manual
    if (showAddDialog) {
        AddAgendaDialog(
            isIncoming = currentTab == AgendaTab.SURAT_MASUK,
            onDismiss = { showAddDialog = false },
            onAddIncoming = { number, received, date, sender, subject, forwarded, drive ->
                viewModel.addManualIncomingLetter(number, received, date, sender, subject, forwarded, drive)
                showAddDialog = false
            },
            onAddOutgoing = { number, date, recipient, subject, qty, drive ->
                viewModel.addManualOutgoingLetter(number, date, recipient, subject, qty, drive)
                showAddDialog = false
            },
            suggestedNextNumber = viewModel.generateNextOutgoingNumber()
        )
    }

    // Dialog Ekstraksi Surat AI
    if (showExtractAiDialog) {
        ExtractAiMailDialog(
            uiState = mailExtractionState,
            onDismiss = {
                viewModel.resetMailExtraction()
                showExtractAiDialog = false
            },
            onExtract = { rawText ->
                viewModel.extractMailInfo(rawText)
            }
        )
    }
}

@Composable
private fun IncomingLetterCard(
    letter: IncomingLetter,
    onOpenDrive: (String) -> Unit,
    onCopy: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = letter.letterNumber.trim(),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Row {
                    IconButton(onClick = onCopy, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Salin", modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus", modifier = Modifier.size(18.dp), tint = Color.Red.copy(alpha = 0.7f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = letter.subject.trim(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Pengirim: ${letter.sender.trim()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (letter.forwardedTo.isNotBlank()) {
                Text(
                    text = "Diteruskan ke: ${letter.forwardedTo.trim()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Diterima: ${letter.receivedDate} • Surat: ${letter.letterDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    fontSize = 11.sp
                )

                if (letter.archiveLink.startsWith("http")) {
                    OutlinedButton(
                        onClick = { onOpenDrive(letter.archiveLink) },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(Icons.Default.DriveFolderUpload, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Buka Drive", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun OutgoingLetterCard(
    letter: OutgoingLetter,
    onCopy: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = letter.letterNumber.trim(),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Row {
                    IconButton(onClick = onCopy, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Salin", modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus", modifier = Modifier.size(18.dp), tint = Color.Red.copy(alpha = 0.7f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = letter.subject.trim(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Tujuan: ${letter.recipient.trim()}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${letter.letterDate} • Jumlah: ${letter.quantity}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun EmptyState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.InsertDriveFile,
                contentDescription = null,
                modifier = Modifier.size(54.dp),
                tint = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun AddAgendaDialog(
    isIncoming: Boolean,
    onDismiss: () -> Unit,
    onAddIncoming: (number: String, received: String, date: String, sender: String, subject: String, forwarded: String, drive: String) -> Unit,
    onAddOutgoing: (number: String, date: String, recipient: String, subject: String, qty: String, drive: String) -> Unit,
    suggestedNextNumber: String
) {
    var letterNumber by remember { mutableStateOf(if (!isIncoming) suggestedNextNumber else "") }
    var letterDate by remember { mutableStateOf("") }
    var receivedDate by remember { mutableStateOf("") }
    var partyName by remember { mutableStateOf("") } // Sender or Recipient
    var subject by remember { mutableStateOf("") }
    var extraField by remember { mutableStateOf("") } // Forwarded to or Quantity
    var driveLink by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (isIncoming) "Tambah Agenda Surat Masuk" else "Tambah Agenda Surat Keluar")
        },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    OutlinedTextField(
                        value = letterNumber,
                        onValueChange = { letterNumber = it },
                        label = { Text("Nomor Surat") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (isIncoming) {
                    item {
                        OutlinedTextField(
                            value = receivedDate,
                            onValueChange = { receivedDate = it },
                            label = { Text("Tanggal Diterima (cth: 21/10/2026)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = letterDate,
                        onValueChange = { letterDate = it },
                        label = { Text("Tanggal Surat (cth: Gaji, 20 Oktober 2026)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = partyName,
                        onValueChange = { partyName = it },
                        label = { Text(if (isIncoming) "Instansi / Pengirim" else "Dikirim Kepada / Tujuan") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("Perihal Surat") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = extraField,
                        onValueChange = { extraField = it },
                        label = { Text(if (isIncoming) "Diteruskan Kepada (Disposisi)" else "Jumlah Keluar (cth: 1 berkas)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = driveLink,
                        onValueChange = { driveLink = it },
                        label = { Text("Link Google Drive (Opsional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isIncoming) {
                        onAddIncoming(letterNumber, receivedDate, letterDate, partyName, subject, extraField, driveLink)
                    } else {
                        onAddOutgoing(letterNumber, letterDate, partyName, subject, extraField, driveLink)
                    }
                },
                enabled = letterNumber.isNotBlank() && partyName.isNotBlank() && subject.isNotBlank()
            ) {
                Text("Simpan ke Buku Agenda")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
private fun ExtractAiMailDialog(
    uiState: GenerationUiState,
    onDismiss: () -> Unit,
    onExtract: (String) -> Unit
) {
    val context = LocalContext.current
    var rawText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Asisten Ekstraksi Surat Masuk")
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Tempel teks atau ketik garis besar surat fisik yang diterima. AI akan memilah Nomor, Pengirim, Tanggal, dan Perihal agar mudah disalin ke Google Form.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = rawText,
                    onValueChange = { rawText = it },
                    placeholder = { Text("Contoh: Dari Kemenag Demak nomor B-102/Kmg.11.21/2026 tanggal 19 Oktober perihal Undangan Upacara Hari Santri Nasional...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5
                )

                when (uiState) {
                    is GenerationUiState.Loading -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Gemini AI menganalisis surat...", fontSize = 13.sp)
                        }
                    }
                    is GenerationUiState.Success -> {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                        ) {
                            LazyColumn(modifier = Modifier.padding(10.dp)) {
                                item {
                                    Text(
                                        text = uiState.content,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Hasil Ekstraksi Surat", uiState.content))
                                Toast.makeText(context, "Hasil analisis disalin ke clipboard", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Salin Hasil Analisis")
                        }
                    }
                    is GenerationUiState.Error -> {
                        Text(
                            text = uiState.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    GenerationUiState.Idle -> {}
                }
            }
        },
        confirmButton = {
            if (uiState !is GenerationUiState.Success) {
                Button(
                    onClick = { onExtract(rawText) },
                    enabled = rawText.isNotBlank() && uiState !is GenerationUiState.Loading
                ) {
                    Text("Ekstrak Data")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup")
            }
        }
    )
}
