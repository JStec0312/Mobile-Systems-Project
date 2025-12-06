package com.example.petcare.presentation.all_tasks

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
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
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaInstant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun AllTasksRoute(
    viewModel: AllTasksViewModel = hiltViewModel(),
    onAddTaskClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AllTasksScreen(
        tasks = state.tasksForSelectedDate,
        dateText = state.dateText,
        dayOfWeek = state.dayOfWeek,
        isLoading = state.isLoading,
        onPrevClick = viewModel::onPrevDayClick,
        onNextClick = viewModel::onNextDayClick,
        onAddTaskClick = onAddTaskClick,
        onTaskDone = viewModel::onTaskDone,
        onTaskCancelled = viewModel::onTaskCancelled
    )
}

@Composable
fun AllTasksScreen(
    tasks: List<Task>,
    dateText: String,
    dayOfWeek: String,
    isLoading: Boolean,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit,
    onAddTaskClick: () -> Unit,
    onTaskDone: (Task) -> Unit,
    onTaskCancelled: (Task) -> Unit
) {
    BaseScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            DateSelector(
                dateText = dateText,
                dayOfWeek = dayOfWeek,
                onPrevClick = onPrevClick,
                onNextClick = onNextClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 0.dp
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary, modifier = Modifier.align(Alignment.CenterHorizontally).padding(20.dp))
                        } else if (tasks.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp), // Dajemy minimalną wysokość, żeby tekst nie był ściśnięty
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "NO TASKS FOR THIS DAY",
                                    color = MaterialTheme.colorScheme.tertiary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            }
                        } else {
                            tasks.forEach { task ->
                                val dismissState = rememberSwipeToDismissBoxState (
                                    confirmValueChange = {
                                        if( it == SwipeToDismissBoxValue.EndToStart) {
                                            onTaskCancelled(task)
                                            return@rememberSwipeToDismissBoxState false
                                        }
                                        false
                                    }
                                )
                                SwipeToDismissBox(
                                    state = dismissState,
                                    enableDismissFromEndToStart = true,
                                    enableDismissFromStartToEnd = false
                                    ,
                                    backgroundContent = {
                                        val color = if(dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                                            Color(0xFFd15b5b)
                                        }
                                        else Color.Transparent
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(color, shape = RoundedCornerShape(12.dp))
                                                .padding(end = 24.dp),
                                            contentAlignment = Alignment.CenterEnd
                                        ) {
                                            Image(
                                                painter = painterResource(id = R.drawable.task_cancelled),
                                                contentDescription = "Cancel",
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                    },
                                    content = {
                                        TaskItem(
                                            task = task,
                                            onCheckClick = { onTaskDone(task) },
                                        )
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.height(40.dp))
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .offset(y = 32.dp)
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiary)
                        .clickable{ onAddTaskClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.add),
                        contentDescription = "Add task",
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun DateSelector(
    dateText: String,
    dayOfWeek: String,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevClick) {
                Image(
                    painter = painterResource(id = R.drawable.left),
                    contentDescription = "Previous day",
                    modifier = Modifier.size(34.dp),
                    contentScale = ContentScale.Fit
                )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(dateText, color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
            Text(dayOfWeek, color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Medium, fontSize = 16.sp)
        }
        IconButton(onClick = onNextClick) {
            Image(
                painter = painterResource(id = R.drawable.right),
                contentDescription = "Next day",
                modifier = Modifier.size(34.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onCheckClick: () -> Unit
) {
    val isDone = task.status == taskStatusEnum.done
    val isCalncelled = task.status == taskStatusEnum.cancelled
    val isSkipped = task.status == taskStatusEnum.skipped

    val title = task.title
    val type = task.type ?: taskTypeEnum.other
    val date = task.date

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (isDone) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.secondary)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    style = TextStyle(textDecoration = if(isCalncelled) TextDecoration.LineThrough else null)
                )
                val formatter = DateTimeFormatter.ofPattern("HH:mm")
                    .withZone(ZoneId.systemDefault())
                val timeString = formatter.format(date.toJavaInstant())
                val typeString = type.name.replaceFirstChar { it.uppercase() }
                Text(
                    text = "$timeString - $typeString",
                    color = Color.White,
                    fontSize = 13.sp,
                    style = TextStyle(textDecoration = if(isCalncelled) TextDecoration.LineThrough else null)
                )
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { onCheckClick() },
                contentAlignment = Alignment.Center,
            ) {
                if (isDone) {
                    Image(
                        painter = painterResource(id = R.drawable.task_done),
                        contentDescription = "Task done",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else if(isCalncelled) {
                    Image(
                        painter = painterResource(id = R.drawable.task_cancelled),
                        contentDescription = "Task cancelled",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else {
                    Image(
                        painter = painterResource(id = R.drawable.task_notdone),
                        contentDescription = "Task not done",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}



@Preview
@Composable
fun AllTasksPreview() {
    PetCareTheme {
        val sampleTasks = listOf(
            Task(
                id = "1",
                petId = "1",
                title = "Morning Walk",
                status = taskStatusEnum.done,
                type = taskTypeEnum.walk,
                notes = "",
                priority = taskPriorityEnum.high,
                createdAt = LocalDate(2024, 1, 1),
                date = Instant.parse("2025-11-26T08:00:00Z")
            ),
            Task(
                id = "2",
                petId = "1",
                title = "Dinner",
                status = taskStatusEnum.planned,
                notes = "No meat",
                type = taskTypeEnum.feeding,
                priority = taskPriorityEnum.normal,
                createdAt = LocalDate(2024, 1, 1),
                date = Instant.parse("2025-11-26T14:30:00Z")
            ),
            Task(
                id = "2",
                petId = "1",
                title = "Dinner",
                status = taskStatusEnum.cancelled,
                notes = "No meat",
                type = taskTypeEnum.feeding,
                priority = taskPriorityEnum.normal,
                createdAt = LocalDate(2024, 1, 1),
                date = Instant.parse("2025-11-26T14:30:00Z")
            )
        )

        AllTasksScreen(
            dateText = "JUNE 4TH 2025",
            dayOfWeek = "Wednesday",
            tasks = sampleTasks,
            isLoading = false,
            onNextClick = {},
            onPrevClick = {},
            onAddTaskClick = {},
            onTaskDone = {},
            onTaskCancelled = {}
        )
    }
}

@Preview
@Composable
fun TaskPreview2() {
    PetCareTheme {

        AllTasksScreen(
            dateText = "JUNE 4TH 2025",
            dayOfWeek = "Wednesday",
            tasks = emptyList(),
            isLoading = false,
            onNextClick = {},
            onPrevClick = {},
            onAddTaskClick = {},
            onTaskDone = {},
            onTaskCancelled = {}
        )
    }
}
