package com.example.petcare.presentation.walk_stats

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.petcare.R
import com.example.petcare.domain.model.Walk
import com.example.petcare.presentation.add_pet.AddPetMode
import com.example.petcare.presentation.common.BaseScreen
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale

@Composable
fun WalkStatsRoute(
    viewModel: WalkStatsViewModel = hiltViewModel(),
    onNavigateToHistory: (String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    WalkStatsScreen(
        state = state,
        onHistoryClick = {
            state.pet?.id?.let { id -> onNavigateToHistory(id)}
        },
        onToggleViewMode = viewModel::toggleViewMode,
        onNavigateTime = viewModel::navigateTime
    )
}
@Composable
fun WalkStatsScreen(
    state: WalkStatsState,
    onHistoryClick: () -> Unit,
    onToggleViewMode: () -> Unit,
    onNavigateTime: (Boolean) -> Unit
) {
    val scrollState = rememberScrollState()

    BaseScreen(isLoading = state.isLoading) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onHistoryClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(imageVector = Icons.Default.History, contentDescription = "History", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("WALK HISTORY", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(Color.White),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(4.dp)
                        .clip(RoundedCornerShape(25.dp))
                        .background(if (state.isMonthly) MaterialTheme.colorScheme.tertiary else Color.Transparent)
                        .clickable { onToggleViewMode() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Monthly",
                        color = if (state.isMonthly) Color.White else MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(4.dp)
                        .clip(RoundedCornerShape(25.dp))
                        .background(if (!state.isMonthly) MaterialTheme.colorScheme.tertiary else Color.Transparent)
                        .clickable { onToggleViewMode() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Weekly",
                        color = if (!state.isMonthly) Color.White else MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }
            }
            DateNavigator(state = state, onNavigate = onNavigateTime)
            StatPanel(
                title = "Distance",
                totalValue = "${"%.2f".format(state.totalDistance)} km",
                averageValue = "${"%.2f".format(state.avgDistance)} km",
                maxValue = "${"%.2f".format(state.maxDistance)} km",
                walks = state.walks,
                maxNumericValue = state.maxDistance.toFloat(),
                isMonthly = state.isMonthly,
                selectedDate = state.selectedDate,
                dataMapper = { (it.distanceMeters ?: 0f).toFloat() / 1000f }
            )
            Spacer(modifier = Modifier.height(2.dp))
            StatPanel(
                title = "Steps",
                totalValue = "${state.totalSteps}",
                averageValue = "${state.avgSteps}",
                maxValue = "${state.maxSteps}",
                walks = state.walks,
                maxNumericValue = state.maxSteps.toFloat(),
                isMonthly = state.isMonthly,
                selectedDate = state.selectedDate,
                dataMapper = { (it.steps ?: 0).toFloat() }
            )
            Spacer(modifier = Modifier.height(2.dp))
            StatPanel(
                title = "Duration",
                totalValue = "${state.totalDuration} min",
                averageValue = "${state.avgDuration} min",
                maxValue = "${state.maxDuration} min",
                walks = state.walks,
                maxNumericValue = state.maxDuration.toFloat(),
                isMonthly = state.isMonthly,
                selectedDate = state.selectedDate,
                dataMapper = { (it.durationSec ?: 0).toFloat() / 60f }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DateNavigator(
    state: WalkStatsState,
    onNavigate: (Boolean) -> Unit
) {
    val periodText = getFormattedPeriod(state.selectedDate, state.isMonthly)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onNavigate(false) }) {
            Image(
                painter = painterResource(id = R.drawable.left),
                contentDescription = "Previous",
                modifier = Modifier.size(30.dp),
                contentScale = ContentScale.Fit
            )
        }
        Text(
            text = periodText,
            color = MaterialTheme.colorScheme.tertiary,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        IconButton(onClick = { onNavigate(true) }) {
            Image(
                painter = painterResource(id = R.drawable.right),
                contentDescription = "Next day",
                modifier = Modifier.size(30.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun StatPanel(
    title: String,
    totalValue: String,
    averageValue: String,
    maxValue: String,
    walks: List<Walk>,
    maxNumericValue: Float,
    isMonthly: Boolean,
    selectedDate: LocalDate,
    dataMapper: (Walk) -> Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title.uppercase(),
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.tertiary,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatInfoItem(label = "Total", value = totalValue)
                StatInfoItem(label = "Average", value = averageValue)
                StatInfoItem(label = "Max", value = maxValue)
            }
            Spacer(modifier = Modifier.height(24.dp))
            StatBarChart(
                walks = walks,
                maxNumericValue = maxNumericValue,
                isMonthly = isMonthly,
                selectedDate = selectedDate,
                dataMapper = dataMapper
            )
        }
    }
}

@Composable
fun StatInfoItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Black
        )
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun StatBarChart(
    walks: List<Walk>,
    maxNumericValue: Float,
    isMonthly: Boolean,
    selectedDate: LocalDate,
    dataMapper: (Walk) -> Float
) {
    val barColor = MaterialTheme.colorScheme.secondary

    val numberOfBars = if(isMonthly) {
        val javaDate = java.time.LocalDate.of(selectedDate.year, selectedDate.monthNumber, 1)
        javaDate.lengthOfMonth()
    } else 7

    Row(
        modifier = Modifier
            .height(120.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(if(isMonthly) 2.dp else 8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        repeat(numberOfBars) { index ->
            val dayOffset = index.toLong()
            val valueForDay = walks.filter { walk ->
                val javaWalkDate = java.time.LocalDate.of(selectedDate.year, selectedDate.monthNumber, selectedDate.dayOfMonth)
                if (isMonthly) {
                    javaWalkDate.dayOfMonth == (index + 1)
                } else {
                    val startOfWeek = java.time.LocalDate.of(
                        selectedDate.year,
                        selectedDate.monthNumber,
                        selectedDate.dayOfMonth
                    )
                        .with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))
                    javaWalkDate == startOfWeek.plusDays(dayOffset)
                }
            }.sumOf { dataMapper(it).toDouble() }.toFloat()
            val ratio = if(maxNumericValue > 0) valueForDay / maxNumericValue else 0f
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(ratio.coerceAtLeast(0.05f))
                    .background(
                        color = if(valueForDay > 0) barColor else barColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                    )
            )
        }
    }
}


fun getFormattedPeriod(date: LocalDate, isMonthly: Boolean): String {
    val javaDate = java.time.LocalDate.of(date.year, date.monthNumber, date.dayOfMonth)
    return if (isMonthly) {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)
        javaDate.format(formatter).uppercase()
    } else {
        val start = javaDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val end = start.plusDays(6)
        val monthFormatter = DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH)
        if(start.month == end.month) {
            "${start.dayOfMonth} - ${end.dayOfMonth} ${start.format(monthFormatter).uppercase()}"
        } else {
            "${start.dayOfMonth} ${start.format(monthFormatter).uppercase()} - ${end.dayOfMonth} ${end.format(monthFormatter).uppercase()}"
        }
    }
}


@androidx.compose.ui.tooling.preview.Preview(showBackground = true)@Composable
fun WalkStatsScreenPreview() {
    // 1. Przygotowanie dzisiejszej daty
    val today = kotlinx.datetime.Clock.System.now()
        .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date

    // 2. Przygotowanie przykładowych spacerów pasujących do Twojego modelu
    val mockWalks = listOf(
        Walk(
            id = "1",
            petId = "pet1",
            startedAt = today,
            endedAt = today,
            durationSec = 1800, // 30 min
            distanceMeters = 2500,
            steps = 3200,
            pending = false,
            createdAt = today
        ),
        Walk(
            id = "2",
            petId = "pet1",
            startedAt = today.minus(1, kotlinx.datetime.DateTimeUnit.DAY),
            endedAt = today.minus(1, kotlinx.datetime.DateTimeUnit.DAY),
            durationSec = 1200, // 20 min
            distanceMeters = 1500,
            steps = 2100,
            pending = false,
            createdAt = today
        ),
        Walk(
            id = "3",
            petId = "pet1",
            startedAt = today.minus(2, kotlinx.datetime.DateTimeUnit.DAY),
            endedAt = today.minus(2, kotlinx.datetime.DateTimeUnit.DAY),
            durationSec = 2400, // 40 min
            distanceMeters = 4000,
            steps = 5500,
            pending = false,
            createdAt = today
        )
    )

    // 3. Przygotowanie mockowego stanu
    val mockState = WalkStatsState(
        pet = null,
        walks = mockWalks,
        selectedDate = today,
        isMonthly = false,
        isLoading = false
    )

    // 4. Wyświetlenie podglądu
    com.example.petcare.presentation.theme.PetCareTheme {
        // Dodajemy Box z tłem, żeby dobrze widzieć białe karty StatPanel
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            WalkStatsScreen(
                state = mockState,
                onHistoryClick = {},
                onToggleViewMode = {},
                onNavigateTime = {}
            )
        }
    }
}