package com.example.petcare.presentation.walk_history

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.petcare.R
import com.example.petcare.domain.model.Walk
import com.example.petcare.presentation.common.BaseScreen
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

@Composable
fun WalkHistoryRoute(
    viewModel: WalkHistoryViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    WalkHistoryScreen(
        state = state,
        onNavigateBack = onNavigateBack
    )
}
@Composable
fun WalkHistoryScreen(
    state: WalkHistoryState,
    onNavigateBack: () -> Unit,
) {
    BaseScreen(isLoading = state.isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                if(state.walks.isEmpty() && !state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No walks found", color = MaterialTheme.colorScheme.tertiary)
                    }
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start=16.dp, end=16.dp, bottom=16.dp, top=64.dp)
                ) {
                    itemsIndexed(state.walks) { index, walk ->
                        WalkHistoryItem(walk = walk)
                        if(index < state.walks.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                thickness = 1.dp,
                                color = Color.LightGray.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
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

@Composable
fun WalkHistoryItem(walk: Walk) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
                Text(
                    text = walk.startedAt.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            val distanceKm = (walk.distanceMeters ?: 0) / 1000f
            Text(
                text = "${"%.2f".format(distanceKm)} km",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            val durationMin = (walk.durationSec ?: 0) / 60
            InfoChip(text = "$durationMin min", icon = Icons.Default.Timer)
            Spacer(modifier = Modifier.width(16.dp))
            InfoChip(text = "${walk.steps ?: 0} steps", icon = Icons.Default.DirectionsWalk)
        }
    }
}

@Composable
fun InfoChip(
    text: String,
    icon: ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color.Gray
)
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun WalkHistoryScreenPreview() {
    // 1. Przykładowe dane dla modelu Walk
    val today = kotlinx.datetime.Clock.System.now()
        .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault()).date

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

    // 2. Przykładowy stan
    val mockState = WalkHistoryState(
        walks = mockWalks,
        isLoading = false
    )

    // 3. Wyświetlenie w motywie
    com.example.petcare.presentation.theme.PetCareTheme {
        WalkHistoryScreen(
            state = mockState,
            onNavigateBack = {}
        )
    }
}