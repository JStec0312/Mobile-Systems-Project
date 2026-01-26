package com.example.petcare.presentation.settings

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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
import kotlin.random.Random

@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SettingsScreen(
        state = state,
        onToggle = viewModel::onToggleNotification
    )
}

@Composable
fun SettingsScreen(
    state: SettingsState,
    onToggle: (notificationCategoryEnum) -> Unit
) {
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // Opcjonalnie: wyślij powiadomienie testowe jako potwierdzenie
                sendTestNotification(context)
            }
        }
    )

    fun checkPermissionAndToggle(category: notificationCategoryEnum, currentlyEnabled: Boolean) {
        // Jeśli wyłączamy, nie pytamy o zgodę
        if (currentlyEnabled) {
            onToggle(category)
            return
        }

        // Jeśli włączamy, sprawdzamy wersję systemu
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                onToggle(category)
            } else {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            // Starszy Android -> po prostu włączamy
            onToggle(category)
        }
    }

    BaseScreen(isLoading = state.isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
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

                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
                    }
                } else if (state.error != null) {
                    Text(text = state.error, color = Color.Red)
                } else {
                    Text(
                        text = "NOTIFICATIONS",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 16.dp, start = 8.dp)
                    )

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.settings) { setting ->
                            NotificationSettingItem(
                                setting = setting,
                                onToggle = {
                                    checkPermissionAndToggle(setting.category, setting.enabled)
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                // Najpierw sprawdź/poproś o zgodę, potem wyślij test
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                                    sendTestNotification(context)
                                } else {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            } else {
                                sendTestNotification(context)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                    ) {
                        Text("TEST & ASK PERMISSION", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationSettingItem(
    setting: NotificationSettings,
    onToggle: () -> Unit
) {
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
                val iconRes = when(setting.category) {
                    notificationCategoryEnum.meds -> R.drawable.paw
                    notificationCategoryEnum.tasks -> R.drawable.task_done
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = when(setting.category) {
                        notificationCategoryEnum.meds -> "Medications"
                        notificationCategoryEnum.tasks -> "Tasks"
                    },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Switch(
                checked = setting.enabled,
                onCheckedChange = { onToggle() },
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
}

private fun sendTestNotification(context: Context) {
    val channelId = "test_channel"
    val notificationId = Random.nextInt()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Test Notifications"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, name, importance)
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.paw_prints)
        .setContentTitle("Hau Hau! 🐶")
        .setContentText("Test powiadomienia działa!")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)

    with(NotificationManagerCompat.from(context)) {
        try {
            notify(notificationId, builder.build())
        } catch (e: SecurityException) {
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    val dummySettings = listOf(
        NotificationSettings(
            id = UUID.randomUUID().toString(),
            userId = "user1",
            category = notificationCategoryEnum.meds,
            updatedAt = Clock.System.now(),
            enabled = true
        ),
        NotificationSettings(
            id = UUID.randomUUID().toString(),
            userId = "user1",
            category = notificationCategoryEnum.tasks,
            updatedAt = Clock.System.now(),
            enabled = false
        )
    )

    PetCareTheme {
        SettingsScreen(
            state = SettingsState(settings = dummySettings),
            onToggle = {}
        )
    }
}