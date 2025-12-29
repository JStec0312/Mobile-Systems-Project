package com.example.petcare.presentation.medication

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.petcare.domain.model.Medication
import com.example.petcare.presentation.common.BaseScreen
import com.example.petcare.presentation.theme.PetCareTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun MedicationHistoryRoute(
    viewModel: MedicationViewModel = hiltViewModel(),
    onAddMedicationClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadMedications()
    }

    // Obsługa Exportu (Tekst/CSV)
    LaunchedEffect(true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is MedicationUiEvent.ShareReport -> {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, event.reportContent)
                        putExtra(Intent.EXTRA_SUBJECT, "Pet Medication History")
                        type = "text/plain" // Bezpieczny typ, otwiera notatniki/maile
                    }
                    val shareIntent = Intent.createChooser(sendIntent, "Export History via")
                    context.startActivity(shareIntent)
                }
            }
        }
    }

    MedicationHistoryScreen(
        state = state,
        onAddMedicationClick = onAddMedicationClick,
        onExportClick = viewModel::onExportClick
    )
}

@Composable
fun MedicationHistoryScreen(
    state: MedicationState,
    onAddMedicationClick: () -> Unit,
    onExportClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    BaseScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            // Karta: ACTIVE MEDICATIONS
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ACTIVE MEDICATIONS",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val activeCount = state.medications.count { it.active }
                    Text(
                        text = "$activeCount medications active",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Karta: UPCOMING DOSES
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "UPCOMING DOSES",
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (state.upcomingDoses.isEmpty()) {
                        Text("-", color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold)
                    } else {
                        state.upcomingDoses.forEach { dose ->
                            val timeStr = "${dose.time.hour.toString().padStart(2,'0')}:${dose.time.minute.toString().padStart(2,'0')}"
                            val info = "${dose.medicationName}: ${dose.dayLabel} $timeStr"

                            Text(
                                text = info,
                                color = MaterialTheme.colorScheme.secondary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ADD BUTTON
            Button(
                onClick = onAddMedicationClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ADD NEW MEDICATION",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // LIST HEADER
            Row(modifier = Modifier.fillMaxWidth().padding(start = 8.dp, bottom = 8.dp)) {
                Text(
                    text = "ALL MEDICATIONS",
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
            }

            // LIST CONTENT
            if (state.isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
            } else if (state.medications.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No medications added yet",
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                state.medications.forEach { med ->
                    MedicationItem(med = med)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // EXPORT BUTTON
            Button(
                onClick = onExportClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = MaterialTheme.colorScheme.secondary
                ),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "EXPORT HISTORY",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun MedicationItem(med: Medication) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = med.name,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                val details = listOfNotNull(med.form, med.dose)
                    .filter { it.isNotBlank() }
                    .joinToString(", ")

                if (details.isNotEmpty()) {
                    Text(
                        text = details,
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }
            }
            StatusChip(isActive = med.active)
        }
    }
}

@Composable
fun StatusChip(isActive: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(8.dp).clip(CircleShape).background(if (isActive) Color.Green else Color.Gray)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = if (isActive) "ACTIVE" else "COMPLETED",
            color = if (isActive) MaterialTheme.colorScheme.secondary else Color.Gray,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MedicationHistoryPreview() {
    val sampleMed = Medication(
        id = "1", petId = "1", name = "Apap", form = "Tablet", dose = "1 tab",
        notes = "", active = true,
        createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
        from = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
        to = null, reccurenceString = "", times = listOf(LocalTime(8, 0))
    )
    val sampleDose = UpcomingDoseUiModel("Apap", LocalTime(8, 0), "Today")

    PetCareTheme {
        MedicationHistoryScreen(
            state = MedicationState(
                medications = listOf(sampleMed),
                upcomingDoses = listOf(sampleDose)
            ),
            onAddMedicationClick = {},
            onExportClick = {}
        )
    }
}