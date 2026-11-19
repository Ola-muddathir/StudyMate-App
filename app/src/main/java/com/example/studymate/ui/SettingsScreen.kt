
package com.example.studymate.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.studymate.data.PreferencesRepository
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val repository = remember {
        PreferencesRepository(context)
    }

    val themeMode by repository.themeMode.collectAsState(
        initial = "SYSTEM"
    )

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Appearance",
            style = MaterialTheme.typography.titleMedium
        )

        Text("Choose your preferred theme")

        listOf("LIGHT", "DARK", "SYSTEM").forEach { mode ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        scope.launch {
                            repository.saveThemeMode(mode)
                        }
                    }
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    when (mode) {
                        "LIGHT" -> "Light Mode"
                        "DARK" -> "Dark Mode"
                        else -> "System Default"
                    }
                )

                RadioButton(
                    selected = themeMode == mode,
                    onClick = {
                        scope.launch {
                            repository.saveThemeMode(mode)
                        }
                    }
                )
            }
        }
    }
}
