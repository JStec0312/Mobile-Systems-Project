package com.example.petcare.presentation.settings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.petcare.R
import com.example.petcare.common.notificationCategoryEnum
import com.example.petcare.domain.model.NotificationSettings
import com.example.petcare.presentation.common.BaseScreen
import com.example.petcare.presentation.theme.PetCareTheme
import kotlinx.datetime.Clock
import java.util.UUID

@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SettingsScreen(
        state = state,
        onToggleGlobal = viewModel::toggleGlobalNotifications
    )
}

@Composable
fun SettingsScreen(
    state: SettingsState,
    onToggleGlobal: (Boolean) -> Unit
) {
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                onToggleGlobal(true)
            } else {
                onToggleGlobal(false)
            }
        }
    )

    fun handleToggle(shouldEnable: Boolean) {
        if (!shouldEnable) {
            onToggleGlobal(false)
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                onToggleGlobal(true)
            } else {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            onToggleGlobal(true)
        }
    }

    // Używamy BaseScreen z Twoją obsługą ładowania
    BaseScreen(isLoading = state.isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {

            // TŁO (Łapki w lewym górnym rogu)
            Image(
                painter = painterResource(id = R.drawable.paw_prints),
                contentDescription = "",
                modifier = Modifier
                    .scale(scaleX = -1f, scaleY = -1f)
                    .align(Alignment.TopStart)
                    .offset(x = 120.dp, y = 150.dp)
                    .size(500.dp)
                    .alpha(0.5f)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // NAGŁÓWEK
                Text(
                    text = "PREFERENCES",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp, start = 8.dp)
                )

                // KARTA Z PRZEŁĄCZNIKIEM
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.paw),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = "Notifications",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                    text = "Allow reminders",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Switch(
                            checked = state.areNotificationsEnabled,
                            onCheckedChange = { isChecked ->
                                handleToggle(isChecked)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MaterialTheme.colorScheme.secondary,
                                uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                                uncheckedTrackColor = Color.White,
                                uncheckedBorderColor = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }
                }

                if (state.error != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Error: ${state.error}", color = Color.Red, fontSize = 12.sp)
                }
            }
        }
    }
}

// --- PODGLĄD ---

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    val enabledState = SettingsState(
        areNotificationsEnabled = true,
        isLoading = false,
        settings = listOf(
            NotificationSettings(
                id = UUID.randomUUID().toString(),
                userId = "user1",
                category = notificationCategoryEnum.meds,
                updatedAt = Clock.System.now(),
                enabled = true
            )
        )
    )

    val disabledState = SettingsState(
        areNotificationsEnabled = false,
        isLoading = false,
        settings = listOf()
    )

    PetCareTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Preview: Enabled")
            SettingsScreen(
                state = enabledState,
                onToggleGlobal = {}
            )

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(32.dp))

            Text("Preview: Disabled")
            SettingsScreen(
                state = disabledState,
                onToggleGlobal = {}
            )
        }
    }
}