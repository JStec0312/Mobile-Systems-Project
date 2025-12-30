package com.example.petcare.presentation.medication_details

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.petcare.R
import com.example.petcare.domain.model.Medication
import com.example.petcare.presentation.common.BaseScreen
import com.example.petcare.presentation.theme.PetCareTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun MedicationDetailsRoute(
    viewModel: MedicationDetailsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    MedicationDetailsScreen(
        state = state,
        onBackClick = onNavigateBack
    )
}

@Composable
fun MedicationDetailsScreen(
    state: MedicationDetailsState,
    onBackClick: () -> Unit,
) {
    BaseScreen {
        Box(modifier = Modifier.fillMaxSize()) {
            // Tło z łapkami
            Image(
                painter = painterResource(id = R.drawable.paw_prints),
                contentDescription = "",
                modifier = Modifier
                    .scale(scaleX = -1f, scaleY = -1f)
                    .align(Alignment.TopStart)
                    .offset(x = 120.dp, y = 150.dp)
                    .size(500.dp)
            )

            if (state.isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (state.error != null) {
                Text(
                    text = state.error,
                    color = Color(0xFFd15b5b),
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (state.medication != null) {
                val med = state.medication

                // Formatowanie czasu
                val timesString = if (med.times.isNotEmpty()) {
                    med.times.joinToString(", ") {
                        "${it.hour.toString().padStart(2, '0')}:${it.minute.toString().padStart(2, '0')}"
                    }
                } else {
                    "No time set"
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(horizontal = 36.dp, vertical = 60.dp)
                        .fillMaxSize()
                ) {
                    // Tytuł (Nazwa leku)
                    Text(
                        text = med.name,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(bottom = 8.dp, top = 16.dp)
                    )

                    // Podtytuł (Forma i Dawka)
                    val subTitle = listOfNotNull(med.form, med.dose)
                        .filter { it.isNotBlank() }
                        .joinToString(" - ")

                    if (subTitle.isNotBlank()) {
                        Text(
                            text = subTitle.uppercase(),
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Wiersz 1: Harmonogram
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.clock),
                            contentDescription = "Time",
                            modifier = Modifier.size(36.dp)
                        )
                        Column {
                            Text(
                                text = "Times: $timesString",
                                color = MaterialTheme.colorScheme.secondary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            // POPRAWKA TUTAJ: Używamy isNullOrBlank() zamiast isNotBlank() na polu nullable
                            val recurrenceInfo = if (!med.reccurenceString.isNullOrBlank()) "Recurring" else "One time"
                            Text(
                                text = recurrenceInfo,
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFEBE6FF))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Wiersz 2: Daty
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.calendar),
                            contentDescription = "Date",
                            modifier = Modifier.size(36.dp)
                        )
                        Column {
                            Text(
                                text = "From: ${med.from}",
                                color = MaterialTheme.colorScheme.secondary,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "To: ${med.to ?: "Ongoing"}",
                                color = MaterialTheme.colorScheme.secondary,
                                fontSize = 18.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFEBE6FF))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Wiersz 3: Status
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.status),
                            contentDescription = "Status",
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = if (med.active) "Active" else "Completed",
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFEBE6FF))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Wiersz 4: Notatki
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.notes),
                            contentDescription = "Notes",
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = if (med.notes.isNullOrBlank()) "No notes" else med.notes,
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFEBE6FF))
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Przycisk zamykania (X)
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.cross),
                        contentDescription = "Back",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MedicationDetailsPreview() {
    val mockMed = Medication(
        id = "1",
        petId = "pet1",
        name = "Apap",
        form = "Tablet",
        dose = "1 tab",
        notes = "Take with food",
        active = true,
        createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
        from = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
        to = null,
        reccurenceString = "DAILY",
        times = listOf(LocalTime(8, 0), LocalTime(20, 0))
    )

    PetCareTheme {
        MedicationDetailsScreen(
            state = MedicationDetailsState(medication = mockMed),
            onBackClick = {}
        )
    }
}