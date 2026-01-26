package com.example.petcare.presentation.calendar

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.petcare.R
import com.example.petcare.common.sexEnum
import com.example.petcare.common.speciesEnum
import com.example.petcare.common.taskStatusEnum
import com.example.petcare.common.taskTypeEnum
import com.example.petcare.domain.model.Task
import com.example.petcare.presentation.common.BaseScreen
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaInstant
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.petcare.presentation.dashboard.TaskItem
import com.example.petcare.domain.model.Pet
import java.time.temporal.WeekFields

@Composable
fun CalendarRoute(
    viewModel: CalendarViewModel = hiltViewModel(),
    onNavigateToAddTask: () -> Unit,
    onNavigateToTaskDetails: (String) -> Unit,
    onNavigateToEditTask: (Task) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }

    LaunchedEffect(state.error) {
        state.error?.let { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            viewModel.onErrorShown()
        }
    }
    LaunchedEffect(state.isRemoveSuccess) {
        if (state.isRemoveSuccess) {
            Toast.makeText(context, "Task removed", Toast.LENGTH_SHORT).show()
            viewModel.onSuccessShown()
        }
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }

    if (showDeleteDialog && taskToDelete != null) {
        val task = taskToDelete!!
        val isRecurring = !task.seriesId.isNullOrBlank()
        if (isRecurring) {
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

    CalendarScreen(
        state = state,
        onMonthChange = viewModel::onMonthChange,
        onTaskClick = viewModel::onTaskClick,
        onDayClick = viewModel::onDayClick,
        onAddClick = onNavigateToAddTask,
        onFilterClick = viewModel::onFilterClick,
        onPetFilterToggle = viewModel::onFilteredClick,
        onClearFilter = viewModel::onClearFilter,
        onDismissBottomSheet = viewModel::onDismissBottomSheet,
        onTaskRemove = { task ->
            taskToDelete = task
            showDeleteDialog = true
        },
        onTaskDone = viewModel::onTaskDone,
        onTaskCancelled = viewModel::onTaskCancelled,
        onTaskEdit = {task -> onNavigateToEditTask(task)},
        onTaskDetails = {task -> onNavigateToTaskDetails(task.id)}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    state: CalendarState,
    onMonthChange: (YearMonth) -> Unit,
    onTaskClick: (Task) -> Unit,
    onDayClick: (LocalDate) -> Unit,
    onAddClick: () -> Unit,
    onFilterClick: () -> Unit,
    onPetFilterToggle: (String) -> Unit,
    onClearFilter: () -> Unit,
    onDismissBottomSheet: () -> Unit,
    onTaskRemove: (Task) -> Unit,
    onTaskDone: (Task) -> Unit,
    onTaskCancelled: (Task) -> Unit,
    onTaskEdit: (Task) -> Unit,
    onTaskDetails: (Task) -> Unit
) {
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(24) }
    val endMonth = remember { currentMonth.plusMonths(24) }

    val daysOfWeek = remember { daysOfWeek(firstDayOfWeekFromLocale()) }

    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = state.currentMonth,
        firstDayOfWeek = daysOfWeek.first()
    )

    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(calendarState.firstVisibleMonth) {
        if (calendarState.firstVisibleMonth.yearMonth != state.currentMonth) {
            onMonthChange(calendarState.firstVisibleMonth.yearMonth)
        }
    }

    LaunchedEffect(state.currentMonth) {
        if (calendarState.firstVisibleMonth.yearMonth != state.currentMonth) {
            calendarState.animateScrollToMonth(state.currentMonth)
        }
    }

    BaseScreen(isLoading = state.isLoading) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onAddClick,
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.background,
                    modifier = Modifier.size(50.dp),
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.add),
                        contentDescription = "Add task",
                        modifier = Modifier.size(50.dp),
                        alpha = 0.75f
                    )
                }
            }
        ) { innerPadding ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                CalendarHeader(
                    currentMonth = calendarState.firstVisibleMonth.yearMonth,
                    onPreviousClick = {
                        coroutineScope.launch {
                            calendarState.animateScrollToMonth(calendarState.firstVisibleMonth.yearMonth.minusMonths(1))
                        }
                    },
                    onNextClick = {
                        coroutineScope.launch {
                            calendarState.animateScrollToMonth(
                                calendarState.firstVisibleMonth.yearMonth.plusMonths(
                                    1
                                )
                            )
                        }
                    }
                )
                DaysOfWeekTitle(daysOfWeek = daysOfWeek)
                BoxWithConstraints(
                    modifier = Modifier.weight(1f)
                ) {
                    val weeksInMonth = remember(calendarState.firstVisibleMonth.yearMonth) {
                        getWeeksInMonth(
                            calendarState.firstVisibleMonth.yearMonth,
                            daysOfWeek.first()
                        )
                    }

                    val cellHeight = maxHeight / weeksInMonth
                    HorizontalCalendar(
                        state = calendarState,
                        dayContent = { day ->
                            val date =
                                LocalDate(day.date.year, day.date.monthValue, day.date.dayOfMonth)
                            val tasksForDay = state.tasksByDate[date] ?: emptyList()
                            DayCell(
                                day = day,
                                tasks = tasksForDay,
                                onClick = { onDayClick(date) },
                                onTaskClick = { task -> onTaskClick(task) },
                                cellHeight = cellHeight
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .drawBehind {
                                val strokeWidth = 2.dp.toPx()
                                val color = Color(0xFFebe6ff)

                                drawLine(
                                    color = color,
                                    start = Offset(0f, 0f),
                                    end = Offset(0f, size.width),
                                    strokeWidth = strokeWidth
                                )
                            }
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = onFilterClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.75f)
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .width(280.dp)
                            .height(46.dp)
                    ) {
                        val buttonText = when {
                            state.selectedPetsIds.isEmpty() -> "All pets"
                            state.selectedPetsIds.size == state.allPets.size -> "All pets"
                            else -> state.allPets
                                .filter { it.id in state.selectedPetsIds }
                                .joinToString(", ") { it.name }
                        }
                        Text(
                            text = buttonText,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 18.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
        if(state.activateBottomSheet != null) {
            ModalBottomSheet(
                onDismissRequest = onDismissBottomSheet,
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                when(state.activateBottomSheet) {
                    is CalendarBottomSheetType.DayDetails -> {
                        DayDetailsMenu(
                            date = state.selectedDate,
                            tasks = state.selectedDayTasks,
                            onTaskClick = { task ->
                                onDismissBottomSheet()
                                onTaskClick(task)
                            },
                            onTaskDone = onTaskDone,
                            onTaskCancelled = onTaskCancelled
                        )
                    }
                    is CalendarBottomSheetType.TaskDetails -> {
                        state.selectedTask?.let { task ->
                            TaskDetailsMenu(
                                task = task,
                                onRemove = { taskToRemove ->
                                    onDismissBottomSheet()
                                    onTaskRemove(taskToRemove)
                                },
                                onEdit = { taskToEdit ->
                                    onDismissBottomSheet()
                                    onTaskEdit(taskToEdit)
                                },
                                onDetails = { taskToDetail ->
                                    onDismissBottomSheet()
                                    onTaskDetails(taskToDetail)
                                }
                            )
                        }
                    }
                    is CalendarBottomSheetType.FilterPets -> {
                        FilterPetsMenu(
                            allPets = state.allPets,
                            selectedIds = state.selectedPetsIds,
                            onTogglePet = onPetFilterToggle,
                            onClearFilter = onClearFilter
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarHeader(
    currentMonth: YearMonth,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(top = 4.dp, bottom = 6.dp)
            .height(64.dp)
            .width(350.dp),
        color = MaterialTheme.colorScheme.tertiary,
        shape = RoundedCornerShape(32.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            IconButton(onClick = onPreviousClick) {
                Icon(
                    painter = painterResource(id = R.drawable.left),
                    contentDescription = "Previous month",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = "${currentMonth.month.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH)} ${currentMonth.year}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            IconButton(onClick = onNextClick) {
                Icon(
                    painter = painterResource(id = R.drawable.right),
                    contentDescription = "Previous month",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 0.dp)
            .height(IntrinsicSize.Min)
            .drawBehind {
                val strokeWidth = 2.dp.toPx()
                val color = Color(0xFFebe6ff)

                drawLine(
                    color = color,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = strokeWidth
                )
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        daysOfWeek.forEachIndexed { index, dayOfWeek ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Text(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(vertical = 10.dp),
                    textAlign = TextAlign.Center,
                    text = dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.ENGLISH),
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )

                if (index < daysOfWeek.lastIndex) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight()
                            .width(2.dp)
                            .background(Color(0xFFebe6ff))
                    )
                }
            }
        }
    }
}

@Composable
fun DayCell(
    day: CalendarDay,
    tasks: List<Task>,
    onClick: () -> Unit,
    onTaskClick: (Task) -> Unit,
    cellHeight: Dp
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(cellHeight)
            .drawBehind {
                val strokeWidth = 4.dp.toPx()
                val color = Color(0xFFebe6ff)

                drawLine(
                    color = color,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = strokeWidth
                )

                drawLine(
                    color = color,
                    start = Offset(size.width, 0f),
                    end = Offset(size.width, size.height),
                    strokeWidth = strokeWidth
                )
            }
            .clickable(enabled = day.position == DayPosition.MonthDate) { onClick() }
    ) {
        if (day.position == DayPosition.MonthDate) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 4.dp, start = 2.dp, top = 2.dp, bottom = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = day.date.dayOfMonth.toString(),
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                val taskItemHeight = 18.dp
                val spacerHeight = 2.dp
                val oneTaskTotalHeight = taskItemHeight + spacerHeight

                val dayHeaderHeight = 24.dp
                val containerPadding = 4.dp
                val availableHeightForTasks = cellHeight - dayHeaderHeight - containerPadding

                val maxPossibleTasks = (availableHeightForTasks / oneTaskTotalHeight).toInt()

                val maxVisibleTasks = if (tasks.size > maxPossibleTasks) {
                    if (maxPossibleTasks > 0) maxPossibleTasks - 1 else 0
                } else {
                    tasks.size
                }
                tasks.take(maxVisibleTasks).forEach { task ->
                    TaskChip(task = task, onClick = { onTaskClick(task) })
                    Spacer(modifier = Modifier.height(2.dp))
                }
                if (tasks.size > maxVisibleTasks) {
                    Surface(
                        color = Color(0xFFebe6ff),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(18.dp)
                            .clickable { onClick() }
                    ) {
                        Box(contentAlignment = Alignment.CenterStart) {
                            Text(
                                text = "+${tasks.size - maxVisibleTasks} more",
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskChip(
    task: Task,
    onClick: () -> Unit
) {
    Surface(
        color = Color(0xFFebe6ff),
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(18.dp)
            .clickable { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = task.title,
                fontSize = 10.sp,
                lineHeight = 10.sp,
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

@Composable
fun TaskDetailsMenu(
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

@Composable
fun DayDetailsMenu(
    date: LocalDate?,
    tasks: List<Task>,
    onTaskClick: (Task) -> Unit,
    onTaskDone: (Task) -> Unit,
    onTaskCancelled: (Task) -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Tasks for ${date ?: ""}",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.tertiary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        if(tasks.isEmpty()) {
            Text(
                text = "No tasks for this day",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center
            )
        }
        else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tasks) { task ->
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
                        enableDismissFromStartToEnd = false,
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
                                onClick = { onTaskClick(task) }
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FilterPetsMenu(
    allPets: List<Pet>,
    selectedIds: Set<String>,
    onTogglePet: (String) -> Unit,
    onClearFilter: () -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Filter tasks by pet",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.tertiary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClearFilter() }
                .padding(8.dp)
        ) {
            Checkbox(
                checked = selectedIds.isEmpty(),
                onCheckedChange = {onClearFilter()},
                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.secondary)
            )
            Text(
                text = "All pets",
                modifier = Modifier.padding(8.dp),
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 18.sp
            )
        }
        HorizontalDivider(color = Color(0xFFEBE6FF))
        LazyColumn {
            items(allPets) { pet ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTogglePet(pet.id) }
                        .padding(8.dp)
                ) {
                    Checkbox(
                        checked = pet.id in selectedIds,
                        onCheckedChange = { onTogglePet(pet.id) },
                        colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.secondary)
                    )
                    Text(
                        text = pet.name,
                        modifier = Modifier.padding(8.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 18.sp
                    )
                }
                HorizontalDivider(color = Color(0xFFEBE6FF))
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskItem(
    task: Task,
    onCheckClick: () -> Unit,
    onClick: () -> Unit
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
                onClick = { onClick() },
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

fun getWeeksInMonth(yearMonth: YearMonth, firstDayOfWeek: DayOfWeek): Int {
    val firstDayOfMonth = yearMonth.atDay(1)
    val lastDayOfMonth = yearMonth.atEndOfMonth()

    val weekFields = WeekFields.of(firstDayOfWeek, 1)
    val weekOfFirst = firstDayOfMonth.get(weekFields.weekOfMonth())

    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfWeekVal = firstDayOfMonth.dayOfWeek.value
    val startOffset = (firstDayOfWeekVal - firstDayOfWeek.value + 7) % 7
    val totalDays = daysInMonth + startOffset

    return (totalDays + 6) / 7
}


@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    // 1. Przygotowanie danych testowych
    val currentMonth = YearMonth.now()
    val todayJava = java.time.LocalDate.now()
    val todayKotlin = LocalDate(todayJava.year, todayJava.monthValue, todayJava.dayOfMonth)
    val tomorrowJava = todayJava.plusDays(1)
    val tomorrowKotlin = LocalDate(
        tomorrowJava.year,
        tomorrowJava.monthValue,
        tomorrowJava.dayOfMonth
    )
    // Mockowe zadania
    val task1 = Task(
        id = "1",
        title = "Walk Aslan",
        petId = "pet1",
        date = kotlinx.datetime.Clock.System.now(),
        status = com.example.petcare.common.taskStatusEnum.planned,
        // --- Dodane brakujące pola ---
        type = com.example.petcare.common.taskTypeEnum.walk,
        createdAt = LocalDate(2025, 1, 1),
        notes = "Remember treats",
        priority = com.example.petcare.common.taskPriorityEnum.normal
    )
    val task2 = Task(
        id = "2",
        title = "Vet Visit",
        petId = "pet1",
        date = kotlinx.datetime.Clock.System.now(),
        status = com.example.petcare.common.taskStatusEnum.planned,
        // --- Dodane brakujące pola ---
        type = com.example.petcare.common.taskTypeEnum.vet,
        createdAt = LocalDate(2025, 1, 1),
        notes = "Check vaccination",
        priority = com.example.petcare.common.taskPriorityEnum.high
    )

    // Mockowy stan
    val mockState = CalendarState(
        currentMonth = currentMonth,
        tasksByDate = mapOf(
            todayKotlin to listOf(task1, task2),
            tomorrowKotlin to listOf(task1)
        ),
        allPets = listOf(
            com.example.petcare.domain.model.Pet(
                id = "pet1",
                name = "Aslan",
                ownerUserId = "user1",
                // --- Dodane brakujące pola ---
                breed = "Golden Retriever",
                birthDate = LocalDate(2020, 5, 20),
                avatarThumbUrl = null,
                species = speciesEnum.dog,
                sex = sexEnum.male,
                createdAt = LocalDate(2024, 1, 1)
            ),
            com.example.petcare.domain.model.Pet(
                id = "pet2",
                name = "Bella",
                ownerUserId = "user1",
                // --- Dodane brakujące pola ---
                breed = "Siamese",
                birthDate = LocalDate(2021, 8, 15),
                avatarThumbUrl = null,
                species = speciesEnum.cat,
                sex = sexEnum.female,
                createdAt = LocalDate(2024, 2, 1)
            )
        ),
        selectedPetsIds = emptySet()
    )

    // 2. Wyświetlenie ekranu
    com.example.petcare.presentation.theme.PetCareTheme {
        CalendarScreen(
            state = mockState,
            onMonthChange = {},
            onTaskClick = {},
            onDayClick = {},
            onAddClick = {},
            onFilterClick = {},
            onPetFilterToggle = {},
            onClearFilter = {},
            onDismissBottomSheet = {},
            onTaskRemove = {},
            onTaskDone = {},
            onTaskCancelled = {},
            onTaskEdit = {},
            onTaskDetails = {}
        )
    }
}
