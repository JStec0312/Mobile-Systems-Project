package com.example.petcare.presentation.task_details

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.petcare.R
import com.example.petcare.common.taskPriorityEnum
import com.example.petcare.common.taskStatusEnum
import com.example.petcare.common.taskTypeEnum
import com.example.petcare.domain.model.Task
import com.example.petcare.presentation.common.BaseScreen
import com.example.petcare.presentation.theme.PetCareTheme
import kotlinx.coroutines.MainScope
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaInstant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@Composable
fun TaskDetailsRoute(
    viewModel: TaskDetailsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    TaskDetailsScreen(
        state = state,
        onBackClick = onNavigateBack
    )
}

@Composable
fun TaskDetailsScreen(
    state: TaskDetailsState,
    onBackClick: () -> Unit,
) {
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
            } else if (state.task != null) {
                val task = state.task
                val date = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy - HH:mm").withZone(ZoneId.systemDefault())
                val formattedDate = date.format(task.date.toJavaInstant()).replaceFirstChar { it.uppercase() }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(horizontal = 36.dp, vertical = 60.dp)
                        .fillMaxSize()
                ) {
                    Text(
                        text = task.title,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(bottom = 8.dp, top = 16.dp)
                    )
                    if(task.type != taskTypeEnum.other) {
                        Text(
                            text = task.type?.name?.uppercase() ?: "TASK",
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.clock),
                            contentDescription = "Date",
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = formattedDate,
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFEBE6FF))
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.repeat),
                            contentDescription = "Repeated?",
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = if(task.seriesId != null) "Repeated" else "Not repeated",
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFEBE6FF))
                    Spacer(modifier = Modifier.height(16.dp))
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
                            text = task.status.name.replaceFirstChar { it.uppercase() },
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFEBE6FF))
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.notes),
                            contentDescription = "Repeated?",
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = if(task.notes.isNullOrBlank()) "No notes" else task.notes,
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFEBE6FF))
                    Spacer(modifier = Modifier.height(16.dp))
                }
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
fun TaskDetailsPreview() {
    PetCareTheme {
        val mockTask = Task(
            id = "1",
            petId = "pet_1",
            title = "Morning Walk",
            type = taskTypeEnum.walk,
            notes = "Don't forget to bring water and treats!",
            priority = taskPriorityEnum.high,
            status = taskStatusEnum.planned,
            createdAt = LocalDate(2025, 1, 1),
            date = Clock.System.now(),
            seriesId = "series_123"
        )

        TaskDetailsScreen(
            state = TaskDetailsState(
                task = mockTask,
                isLoading = false,
                error = null
            ),
            onBackClick = {}
        )
    }
}