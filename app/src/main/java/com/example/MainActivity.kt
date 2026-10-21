package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppScreen
import com.example.ui.SecretaryViewModel
import com.example.ui.screens.AgendaScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DocumentArchiveScreen
import com.example.ui.screens.InAppFormWebView
import com.example.ui.screens.LetterGeneratorScreen
import com.example.ui.screens.MeetingMinutesScreen
import com.example.ui.theme.DujaEmeraldPrimary
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainContent()
            }
        }
    }
}

@Composable
fun MainContent(
    viewModel: SecretaryViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val activeFormUrl by viewModel.activeFormUrl.collectAsStateWithLifecycle()
    val formTitle by viewModel.formTitle.collectAsStateWithLifecycle()

    // Handle system back navigation to return to Dashboard from secondary screens
    if (currentScreen != AppScreen.DASHBOARD && currentScreen != AppScreen.FORM_WEBVIEW) {
        BackHandler {
            viewModel.navigateTo(AppScreen.DASHBOARD)
        }
    }

    if (currentScreen == AppScreen.FORM_WEBVIEW && activeFormUrl != null) {
        InAppFormWebView(
            url = activeFormUrl!!,
            title = formTitle,
            onBack = { viewModel.closeInAppForm() }
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.DASHBOARD,
                        onClick = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                        icon = {
                            Icon(
                                if (currentScreen == AppScreen.DASHBOARD) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "Beranda"
                            )
                        },
                        label = { Text("Beranda") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_beranda")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.AGENDA,
                        onClick = { viewModel.navigateTo(AppScreen.AGENDA) },
                        icon = {
                            Icon(
                                if (currentScreen == AppScreen.AGENDA) Icons.Filled.Book else Icons.Outlined.Book,
                                contentDescription = "Agenda"
                            )
                        },
                        label = { Text("Agenda") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_agenda")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.BUAT_SURAT,
                        onClick = { viewModel.navigateTo(AppScreen.BUAT_SURAT) },
                        icon = {
                            Icon(
                                if (currentScreen == AppScreen.BUAT_SURAT) Icons.Filled.Description else Icons.Outlined.Description,
                                contentDescription = "Surat AI"
                            )
                        },
                        label = { Text("Surat AI") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_surat_ai")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.NOTULENSI,
                        onClick = { viewModel.navigateTo(AppScreen.NOTULENSI) },
                        icon = {
                            Icon(
                                if (currentScreen == AppScreen.NOTULENSI) Icons.AutoMirrored.Filled.Assignment else Icons.AutoMirrored.Outlined.Assignment,
                                contentDescription = "Notulensi"
                            )
                        },
                        label = { Text("Notulensi") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_notulensi")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.ARSIP,
                        onClick = { viewModel.navigateTo(AppScreen.ARSIP) },
                        icon = {
                            Icon(
                                if (currentScreen == AppScreen.ARSIP) Icons.Filled.Folder else Icons.Outlined.Folder,
                                contentDescription = "Arsip"
                            )
                        },
                        label = { Text("Arsip") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_arsip")
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentScreen) {
                    AppScreen.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                    AppScreen.AGENDA -> AgendaScreen(viewModel = viewModel)
                    AppScreen.BUAT_SURAT -> LetterGeneratorScreen(viewModel = viewModel)
                    AppScreen.NOTULENSI -> MeetingMinutesScreen(viewModel = viewModel)
                    AppScreen.ARSIP -> DocumentArchiveScreen(viewModel = viewModel)
                    AppScreen.FORM_WEBVIEW -> {} // Handled above
                }
            }
        }
    }
}
