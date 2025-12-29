package com.example.petcare.presentation.medication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.petcare.presentation.common.BaseScreen
import com.example.petcare.presentation.theme.PetCareTheme

// Model danych
data class FakeMedication(
    val name: String,
    val description: String,
    val isActive: Boolean
)

@Composable
fun MedicationHistoryScreen(
    onAddMedicationClick: () -> Unit = {},
    // Parametr do testowania pustej listy w Preview (domyślnie false, czyli pokazuje dane)
    forceEmptyState: Boolean = false
) {
    val scrollState = rememberScrollState()

    // Logika do testowania: jeśli forceEmptyState = true, lista jest pusta.
    val medicationList = if (forceEmptyState) emptyList() else listOf(
        FakeMedication("Heartgard Plus", "Heartworm Prevention", true),
        FakeMedication("Rimadyl", "Anti-inflammatory", true),
        FakeMedication("Apoquel", "Allergy Relief", true),
        FakeMedication("Antibiotics", "Infection Treatment", false)
    )

    BaseScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            // 1. SUMMARY CARDS
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

                    // Wyświetlanie licznika (0 jeśli lista pusta)
                    val activeCount = medicationList.count { it.isActive }
                    Text(
                        text = "$activeCount medications active",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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

                    if (medicationList.isEmpty()) {
                        // Jeśli nie ma leków, nie ma też dawek
                        Text(
                            text = "-",
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    } else {
                        UpcomingDoseRow(medName = "Heartgard Plus", date = "2024-02-15")
                        Spacer(modifier = Modifier.height(8.dp))
                        UpcomingDoseRow(medName = "Rimadyl", date = "2024-01-21")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. PRZYCISK ADD
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

            // 3. LISTA WSZYSTKICH LEKÓW
            Row(modifier = Modifier.fillMaxWidth().padding(start = 8.dp, bottom = 8.dp)) {
                Text(
                    text = "ALL MEDICATIONS",
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.SemiBold, // Zgodne z Dashboardem
                    fontSize = 18.sp
                )
            }

            // --- TUTAJ JEST ZMIANA (Obsługa pustej listy) ---
            if (medicationList.isEmpty()) {
                // Pusty stan - Biały kafel z komunikatem (Styl jak w Dashboard)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp), // Stała wysokość, żeby ładnie wyglądało
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No medications added yet",
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold, // Zgodne z Dashboardem
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // Lista leków
                medicationList.forEach { med ->
                    MedicationItem(med = med)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            // ------------------------------------------------

            Spacer(modifier = Modifier.height(24.dp))

            // 4. EXPORT (Pokazujemy tylko jeśli są dane, albo zawsze - zależy od decyzji. Zostawiam zawsze)
            Button(
                onClick = { /* TODO */ },
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

// --- POMOCNICZE ---

@Composable
fun UpcomingDoseRow(medName: String, date: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$medName: ",
            color = MaterialTheme.colorScheme.tertiary,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )
        Text(
            text = date,
            color = Color.Red.copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp
        )
    }
}

@Composable
fun MedicationItem(med: FakeMedication) {
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
                Text(
                    text = med.description,
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }
            StatusChip(isActive = med.isActive)
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

// PODGLĄD 1: Z DANYMI
@Preview(showBackground = true, name = "With Data")
@Composable
fun MedicationHistoryPreview() {
    PetCareTheme {
        MedicationHistoryScreen(forceEmptyState = false)
    }
}

// PODGLĄD 2: PUSTA LISTA (Sprawdź czy wygląda jak w Dashboard!)
@Preview(showBackground = true, name = "Empty State")
@Composable
fun MedicationHistoryEmptyPreview() {
    PetCareTheme {
        MedicationHistoryScreen(forceEmptyState = true)
    }
}