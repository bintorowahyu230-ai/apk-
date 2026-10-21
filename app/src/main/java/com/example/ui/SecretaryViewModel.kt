package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AgendaConfig
import com.example.data.model.IncomingLetter
import com.example.data.model.OutgoingLetter
import com.example.data.model.SecretaryDocument
import com.example.data.repository.SecretaryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppScreen {
    DASHBOARD,
    AGENDA,
    BUAT_SURAT,
    NOTULENSI,
    ARSIP,
    FORM_WEBVIEW
}

enum class AgendaTab {
    SURAT_MASUK,
    SURAT_KELUAR
}

sealed interface GenerationUiState {
    object Idle : GenerationUiState
    object Loading : GenerationUiState
    data class Success(val content: String) : GenerationUiState
    data class Error(val message: String) : GenerationUiState
}

class SecretaryViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val repository = SecretaryRepository(database)

    // Current Screen
    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Agenda Tab
    private val _agendaTab = MutableStateFlow(AgendaTab.SURAT_MASUK)
    val agendaTab: StateFlow<AgendaTab> = _agendaTab.asStateFlow()

    // Search Query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Active WebView URL for in-app Google Form
    private val _activeFormUrl = MutableStateFlow<String?>(null)
    val activeFormUrl: StateFlow<String?> = _activeFormUrl.asStateFlow()

    private val _formTitle = MutableStateFlow("")
    val formTitle: StateFlow<String> = _formTitle.asStateFlow()

    // Raw streams from DB
    val rawIncoming = repository.incomingLetters
    val rawOutgoing = repository.outgoingLetters
    val rawDocuments = repository.documents

    // Filtered lists
    val filteredIncoming: StateFlow<List<IncomingLetter>> = combine(rawIncoming, _searchQuery) { list, query ->
        if (query.isBlank()) list
        else list.filter {
            it.letterNumber.contains(query, ignoreCase = true) ||
            it.sender.contains(query, ignoreCase = true) ||
            it.subject.contains(query, ignoreCase = true) ||
            it.forwardedTo.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredOutgoing: StateFlow<List<OutgoingLetter>> = combine(rawOutgoing, _searchQuery) { list, query ->
        if (query.isBlank()) list
        else list.filter {
            it.letterNumber.contains(query, ignoreCase = true) ||
            it.recipient.contains(query, ignoreCase = true) ||
            it.subject.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredDocuments: StateFlow<List<SecretaryDocument>> = combine(rawDocuments, _searchQuery) { list, query ->
        if (query.isBlank()) list
        else list.filter {
            it.title.contains(query, ignoreCase = true) ||
            it.letterNumber.contains(query, ignoreCase = true) ||
            it.content.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // AI Generation States
    private val _letterGenerationState = MutableStateFlow<GenerationUiState>(GenerationUiState.Idle)
    val letterGenerationState: StateFlow<GenerationUiState> = _letterGenerationState.asStateFlow()

    private val _meetingGenerationState = MutableStateFlow<GenerationUiState>(GenerationUiState.Idle)
    val meetingGenerationState: StateFlow<GenerationUiState> = _meetingGenerationState.asStateFlow()

    private val _mailExtractionState = MutableStateFlow<GenerationUiState>(GenerationUiState.Idle)
    val mailExtractionState: StateFlow<GenerationUiState> = _mailExtractionState.asStateFlow()

    // Currently Selected Document for Detailed View
    private val _selectedDocument = MutableStateFlow<SecretaryDocument?>(null)
    val selectedDocument: StateFlow<SecretaryDocument?> = _selectedDocument.asStateFlow()

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun setAgendaTab(tab: AgendaTab) {
        _agendaTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openInAppForm(url: String, title: String) {
        _activeFormUrl.value = url
        _formTitle.value = title
        _currentScreen.value = AppScreen.FORM_WEBVIEW
    }

    fun closeInAppForm() {
        _activeFormUrl.value = null
        _currentScreen.value = AppScreen.AGENDA
    }

    fun selectDocument(doc: SecretaryDocument?) {
        _selectedDocument.value = doc
    }

    // AI Actions
    fun generateLetter(
        category: String,
        recipient: String,
        subject: String,
        keyPoints: String,
        letterNumber: String
    ) {
        viewModelScope.launch {
            _letterGenerationState.value = GenerationUiState.Loading
            val result = repository.generateOfficialLetter(
                category = category,
                recipient = recipient,
                subject = subject,
                keyPoints = keyPoints,
                letterNumberHint = letterNumber
            )
            result.onSuccess { content ->
                _letterGenerationState.value = GenerationUiState.Success(content)
            }.onFailure { err ->
                _letterGenerationState.value = GenerationUiState.Error(err.localizedMessage ?: "Terjadi kesalahan saat memproses surat.")
            }
        }
    }

    fun resetLetterGeneration() {
        _letterGenerationState.value = GenerationUiState.Idle
    }

    fun generateMeetingMinutes(
        title: String,
        date: String,
        location: String,
        attendees: String,
        rawNotes: String
    ) {
        viewModelScope.launch {
            _meetingGenerationState.value = GenerationUiState.Loading
            val result = repository.generateMeetingMinutes(
                meetingTitle = title,
                meetingDate = date,
                location = location,
                attendees = attendees,
                rawNotes = rawNotes
            )
            result.onSuccess { content ->
                _meetingGenerationState.value = GenerationUiState.Success(content)
            }.onFailure { err ->
                _meetingGenerationState.value = GenerationUiState.Error(err.localizedMessage ?: "Terjadi kesalahan saat memproses notulensi.")
            }
        }
    }

    fun resetMeetingGeneration() {
        _meetingGenerationState.value = GenerationUiState.Idle
    }

    fun extractMailInfo(rawText: String) {
        viewModelScope.launch {
            _mailExtractionState.value = GenerationUiState.Loading
            val result = repository.extractIncomingMailInfo(rawText)
            result.onSuccess { content ->
                _mailExtractionState.value = GenerationUiState.Success(content)
            }.onFailure { err ->
                _mailExtractionState.value = GenerationUiState.Error(err.localizedMessage ?: "Gagal mengekstrak surat.")
            }
        }
    }

    fun resetMailExtraction() {
        _mailExtractionState.value = GenerationUiState.Idle
    }

    // Document Management
    fun saveGeneratedLetterToArchive(
        title: String,
        letterNumber: String,
        recipient: String,
        content: String,
        docType: String = "SURAT_RESMI"
    ) {
        viewModelScope.launch {
            val todayStr = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date())
            val doc = SecretaryDocument(
                docType = docType,
                title = title,
                letterNumber = letterNumber,
                recipient = recipient,
                content = content,
                dateMasehi = "Gaji, $todayStr",
                dateHijri = getEstimatedHijriDate()
            )
            repository.saveDocument(doc)
            _selectedDocument.value = doc
            _currentScreen.value = AppScreen.ARSIP
        }
    }

    fun addManualIncomingLetter(
        letterNumber: String,
        receivedDate: String,
        letterDate: String,
        sender: String,
        subject: String,
        forwardedTo: String,
        driveLink: String
    ) {
        viewModelScope.launch {
            val ts = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
            repository.addIncomingLetter(
                IncomingLetter(
                    timestamp = ts,
                    receivedDate = receivedDate.ifBlank { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()) },
                    letterNumber = letterNumber,
                    letterDate = letterDate,
                    sender = sender,
                    subject = subject,
                    forwardedTo = forwardedTo,
                    archiveLink = driveLink
                )
            )
        }
    }

    fun addManualOutgoingLetter(
        letterNumber: String,
        letterDate: String,
        recipient: String,
        subject: String,
        quantity: String,
        driveLink: String
    ) {
        viewModelScope.launch {
            val ts = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
            repository.addOutgoingLetter(
                OutgoingLetter(
                    timestamp = ts,
                    letterNumber = letterNumber,
                    letterDate = letterDate.ifBlank { "Gaji, " + SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date()) },
                    recipient = recipient,
                    subject = subject,
                    quantity = quantity.ifBlank { "1 surat" },
                    archiveLink = driveLink.ifBlank { "lihat" }
                )
            )
        }
    }

    fun deleteIncomingLetter(id: Int) {
        viewModelScope.launch {
            repository.deleteIncomingLetter(id)
        }
    }

    fun deleteOutgoingLetter(id: Int) {
        viewModelScope.launch {
            repository.deleteOutgoingLetter(id)
        }
    }

    fun deleteDocument(id: Int) {
        viewModelScope.launch {
            repository.deleteDocument(id)
            if (_selectedDocument.value?.id == id) {
                _selectedDocument.value = null
            }
        }
    }

    fun generateNextOutgoingNumber(): String {
        val count = (filteredOutgoing.value.size) + 1
        val numFormatted = String.format("%03d", count)
        val monthRoman = getRomanMonth()
        val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())
        return "$numFormatted/YBDG/PBD/$monthRoman/$year"
    }

    private fun getRomanMonth(): String {
        val month = SimpleDateFormat("M", Locale.getDefault()).format(Date()).toIntOrNull() ?: 10
        return when (month) {
            1 -> "I"; 2 -> "II"; 3 -> "III"; 4 -> "IV"; 5 -> "V"; 6 -> "VI"
            7 -> "VII"; 8 -> "VIII"; 9 -> "IX"; 10 -> "X"; 11 -> "XI"; else -> "XII"
        }
    }

    private fun getEstimatedHijriDate(): String {
        // Formatted Hijri representation for pesantren documents
        return "Rabi'ul Akhir 1448 H"
    }
}
