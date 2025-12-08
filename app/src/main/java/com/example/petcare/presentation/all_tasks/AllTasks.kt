package com.example.petcare.presentation.all_tasks

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    onAddTaskClick: () -> Unit,
    onNavigateToTaskDetails: (String) -> Unit,
    onNavigateToEditTask: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }

    if(showDeleteDialog && taskToDelete != null) {
        val task = taskToDelete!!
        val isRecurring = !task.seriesId.isNullOrBlank()
        if(isRecurring) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Remove Task") },
                text = { Text("This is a repeating task. Do you want to delete only this occurrence or the entire series? Warning: this action cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.onDeleteConfirmed(task, deleteWholeSeries = true)
                            showDeleteDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFd15b5b))
                    ) {
                        Text("Delete entire series")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            viewModel.onDeleteConfirmed(task, deleteWholeSeries = false)
                            showDeleteDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFd15b5b))
                    ) {
                        Text("Delete only this task")
                    }
                }
            )
        } else {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Task") },
                text = { Text("Are you sure you want to delete this task? This action cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.onDeleteConfirmed(task, deleteWholeSeries = false)
                            showDeleteDialog = false
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
                }
            )
        }
    }

    AllTasksScreen(
        tasks = state.tasksForSelectedDate,
        dateText = state.dateText,
        dayOfWeek = state.dayOfWeek,
        isLoading = state.isLoading,
        onPrevClick = viewModel::onPrevDayClick,
        onNextClick = viewModel::onNextDayClick,
        onAddTaskClick = onAddTaskClick,
        onTaskDone = viewModel::onTaskDone,
        onTaskCancelled = viewModel::onTaskCancelled,
        onRemoveTask = { task ->
            taskToDelete = task
            showDeleteDialog = true
        },
        onEditTask = { task ->
            onNavigateToEditTask(task.id)
        },
        onViewDetails = { task ->
            onNavigateToTaskDetails(task.id)
        }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
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
    onTaskCancelled: (Task) -> Unit,
    onRemoveTask: (Task) -> Unit,
    onEditTask: (Task) -> Unit,
    onViewDetails: (Task) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedTaskForMenu by remember { mutableStateOf<Task?>(null) }

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
                                            onLongClick = {
                                                selectedTaskForMenu = task
                                                showBottomSheet = true
                                            }
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
        if(showBottomSheet && selectedTaskForMenu != null) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                TaskActionMenu(
                    task = selectedTaskForMenu!!,
                    onRemove = {
                        onRemoveTask(it)
                        showBottomSheet = false
                    },
                    onEdit = {
                        onEditTask(it)
                        showBottomSheet = false
                    },
                    onDetails = {
                        onViewDetails(it)
                        showBottomSheet = false
                    }
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskItem(
    task: Task,
    onCheckClick: () -> Unit,
    onLongClick: () -> Unit
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
            .height(70.dp)
            .combinedClickable(
                onClick = {},
                onLongClick = onLongClick
            ),
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

@Composable
fun TaskActionMenu(
    task: Task,
    onRemove: (Task) -> Unit,
    onEdit: (Task) -> Unit,
    onDetails: (Task) -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = task.title,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        MenuOptionItem(
            icon = R.drawable.details,
            text = "View Details",
            onClick = { onDetails(task)}
        )
        HorizontalDivider(color = Color(0xFFEBE6FF))
        MenuOptionItem(
            icon = R.drawable.edit_darker,
            text = "Edit task",
            onClick = {onEdit(task)}
        )
        HorizontalDivider(color = Color(0xFFEBE6FF))
        MenuOptionItem(
            icon = R.drawable.remove,
            text = "Remove task",
            onClick = {onRemove(task)}
        )
    }
}


@Composable
fun MenuOptionItem(
    icon: Int,
    text: String,
    onClick: () -> Unit,
    textColor: Color = MaterialTheme.colorScheme.secondary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = text,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, color = textColor, fontSize = 18.sp)
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
            onTaskCancelled = {},
            onRemoveTask = {},
            onEditTask = {},
            onViewDetails = {}
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
            onTaskCancelled = {},
            onRemoveTask = {},
            onEditTask = {},
            onViewDetails = {}
        )
    }
}
