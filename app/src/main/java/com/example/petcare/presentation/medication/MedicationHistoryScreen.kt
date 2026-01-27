package com.example.petcare.presentation.medication

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.FileProvider // <--- IMPORT
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.petcare.R
import com.example.petcare.domain.model.Medication
import com.example.petcare.presentation.common.BaseScreen
import com.example.petcare.presentation.theme.PetCareTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import timber.log.Timber

@Composable
fun MedicationHistoryRoute(
    viewModel: MedicationViewModel = hiltViewModel(),
    onAddMedicationClick: () -> Unit,
    onNavigateToDetails: (String) -> Unit,
    onNavigateToEdit: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadMedications()
    }

    LaunchedEffect(true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is MedicationUiEvent.SharePdf -> {
                    try {
                        // Tworzymy URI za pomocą FileProvider (wymagane w Android 7+)
                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.provider", // UWAGA: Musi pasować do AndroidManifest
                            event.file
                        )

                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "application/pdf"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            putExtra(Intent.EXTRA_SUBJECT, "Medication History")
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }

                        val shareIntent = Intent.createChooser(sendIntent, "Share PDF")
                        context.startActivity(shareIntent)

                    } catch (e: Exception) {
                        Timber.d("Error sharing PDF: ${e.message}");
                        Toast.makeText(context, "Error sharing file: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
                is MedicationUiEvent.ShowMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var medicationToDelete by remember { mutableStateOf<Medication?>(null) }

    if (showDeleteDialog && medicationToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Medication") },
            text = { Text("Are you sure you want to delete ${medicationToDelete?.name}? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        medicationToDelete?.let { viewModel.deleteMedication(it.id) }
                        showDeleteDialog = false
                        medicationToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFd15b5b))
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB5A5BB))
                ) {
                    Text("Cancel")
                }
            },
            containerColor = Color.White
        )
    }

    MedicationHistoryScreen(
        state = state,
        onAddMedicationClick = onAddMedicationClick,
        onExportClick = viewModel::onExportClick,
        onMedicationClick = { medId ->
            onNavigateToDetails(medId)
        },
        onEditClick = { medId ->
            onNavigateToEdit(medId)
        },
        onDeleteClick = { medication ->
            medicationToDelete = medication
            showDeleteDialog = true
        }
    )
}

// ... Reszta pliku MedicationHistoryScreen (funkcje Composable) bez zmian ...
// Poniżej wklejam resztę, żeby plik był kompletny, ale zmiany były tylko w Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationHistoryScreen(
    state: MedicationState,
    onAddMedicationClick: () -> Unit,
    onExportClick: () -> Unit,
    onMedicationClick: (String) -> Unit,
    onEditClick: (String) -> Unit,
    onDeleteClick: (Medication) -> Unit
) {
    val scrollState = rememberScrollState()

    var selectedMedication by remember { mutableStateOf<Medication?>(null) }
    val sheetState = rememberModalBottomSheetState()

    if (selectedMedication != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedMedication = null },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            MedicationActionMenu(
                medication = selectedMedication!!,
                onDetails = {
                    selectedMedication?.let { onMedicationClick(it.id) }
                    selectedMedication = null
                },
                onEdit = {
                    selectedMedication?.let { onEditClick(it.id) }
                    selectedMedication = null
                },
                onRemove = {
                    selectedMedication?.let { onDeleteClick(it) }
                    selectedMedication = null
                }
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    BaseScreen(isLoading = state.isLoading) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            // ACTIVE MEDICATIONS
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

            // UPCOMING DOSES
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
                    MedicationItem(
                        med = med,
                        onClick = { selectedMedication = med }
                    )
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
fun MedicationActionMenu(
    medication: Medication,
    onDetails: () -> Unit,
    onEdit: () -> Unit,
    onRemove: () -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = medication.name,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        MenuOptionItem(
            iconRes = R.drawable.details,
            text = "View Details",
            onClick = onDetails
        )
        HorizontalDivider(color = Color(0xFFEBE6FF))

        MenuOptionItem(
            iconRes = R.drawable.edit_darker,
            text = "Edit medication",
            onClick = onEdit
        )
        HorizontalDivider(color = Color(0xFFEBE6FF))

        MenuOptionItem(
            iconRes = R.drawable.remove,
            text = "Remove medication",
            onClick = onRemove,
            isDestructive = true
        )
    }
}

@Composable
fun MenuOptionItem(
    iconRes: Int,
    text: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = text,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun MedicationItem(
    med: Medication,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
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
            onExportClick = {},
            onMedicationClick = {},
            onEditClick = {},
            onDeleteClick = {}
        )
    }
}