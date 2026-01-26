package com.example.petcare.presentation.edit_task

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.petcare.R
import com.example.petcare.common.taskTypeEnum
import com.example.petcare.presentation.add_task.AddTaskState
import com.example.petcare.presentation.add_task.RecurrenceType
import com.example.petcare.presentation.common.BaseScreen
import com.example.petcare.presentation.common.EditPetTextField
import com.example.petcare.presentation.common.PetTextField
import com.example.petcare.presentation.theme.PetCareTheme
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import java.util.Calendar

@Composable
fun EditTaskRoute(
    viewModel: EditTaskViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(state.error) {
        state.error?.let { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            viewModel.onErrorShown()
        }
    }

    LaunchedEffect(state.isSuccessful) {
        if(state.isSuccessful) {
            Toast.makeText(context, "Task updated successfully", Toast.LENGTH_SHORT).show()
            viewModel.onSuccessShown()
            onNavigateBack()
        }
    }

    if(state.showConfirmDialog) {
        val taskSeries = state.seriesId
        val isRecurring = !taskSeries.isNullOrBlank()
        if(isRecurring) {
            AlertDialog(
                onDismissRequest = { viewModel.onDismissDialog() },
                title = { Text("Edit Task") },
                text = { Text("This is a repeating task. Do you want to edit only this occurrence or the entire series? Warning: this action cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = { viewModel.onConfirmSave(editWholeSeries = true) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("Entire series")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { viewModel.onConfirmSave(editWholeSeries = false) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("Only this task")
                    }
                }
            )
        } else {
            AlertDialog(
                onDismissRequest = { viewModel.onDismissDialog() },
                title = { Text("Edit Task") },
                text = { Text("Are you sure you want to edit this task? This action cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.onConfirmSave(false)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("Edit")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { viewModel.onDismissDialog() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB5A5BB))
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }

    EditTaskScreen(
        state = state,
        onTitleChange = viewModel::onTitleChange,
        onTypeChange = viewModel::onTypeChange,
        onTimeChange = viewModel::onTimeChange,
        onNotesChange = viewModel::onNotesChange,
        onSaveClick = viewModel::onSaveClick,
        onBackClick = onNavigateBack
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreen(
    state: EditTaskState,
    onTitleChange: (String) -> Unit,
    onTypeChange: (taskTypeEnum) -> Unit,
    onTimeChange: (LocalTime) -> Unit,
    onNotesChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    var typeExpanded by remember { mutableStateOf(false) }

    val taskTypes = taskTypeEnum.values().toList()

    val calendar = Calendar.getInstance()

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            onTimeChange(LocalTime(hourOfDay, minute))
        },
        state.selectedTime?.hour ?: calendar.get(Calendar.HOUR_OF_DAY),
        state.selectedTime?.minute ?: calendar.get(Calendar.MINUTE),
        true
    )

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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(80.dp))
                EditPetTextField(
                    value = state.title,
                    onValueChange = onTitleChange,
                    label = "Title",
                    placeholder = state.title
                )
                Spacer(modifier = Modifier.height(16.dp))

                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = !typeExpanded },
                    modifier = Modifier.width(280.dp)
                ) {
                    OutlinedTextField(
                        value = state.type?.name?.lowercase()?.replaceFirstChar { it.uppercase() }
                            ?: "",
                        onValueChange = {},
                        readOnly = true,
                        placeholder ={ Text(state.type?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Select type")},
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        label = { Text("Type") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.secondary,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color(0xFFFFFFFF),
                            unfocusedContainerColor = Color(0xFFFFFFFF),
                            focusedLabelColor = MaterialTheme.colorScheme.secondary,
                            unfocusedLabelColor = Color(0xFFBDADD5),
                            focusedTextColor = MaterialTheme.colorScheme.secondary,
                            unfocusedTextColor = MaterialTheme.colorScheme.secondary,
                            unfocusedTrailingIconColor = MaterialTheme.colorScheme.secondary,
                            focusedTrailingIconColor = MaterialTheme.colorScheme.secondary,
                            disabledContainerColor = Color.White,
                            disabledBorderColor = Color.Transparent,
                            disabledTextColor = MaterialTheme.colorScheme.secondary,
                            disabledLabelColor = Color(0xFFBDADD5),
                            disabledPlaceholderColor = Color(0xFFBDADD5)
                        ),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded)
                        },
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        taskTypes.forEach { type ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        type.name.lowercase().replaceFirstChar { it.uppercase() })
                                },
                                onClick = {
                                    onTypeChange(type)
                                    typeExpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = state.selectedTime?.let {
                            "${it.hour}:${
                                it.minute.toString().padStart(2, '0')
                            }"
                        } ?: "",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .clickable { timePickerDialog.show() },
                        label = { Text("Time") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.secondary,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color(0xFFFFFFFF),
                            unfocusedContainerColor = Color(0xFFFFFFFF),
                            focusedLabelColor = MaterialTheme.colorScheme.secondary,
                            unfocusedLabelColor = Color(0xFFBDADD5),
                            focusedTextColor = MaterialTheme.colorScheme.secondary,
                            unfocusedTextColor = MaterialTheme.colorScheme.secondary,
                            unfocusedTrailingIconColor = MaterialTheme.colorScheme.secondary,
                            focusedTrailingIconColor = MaterialTheme.colorScheme.secondary,
                            disabledContainerColor = Color.White,
                            disabledBorderColor = Color.Transparent,
                            disabledTextColor = MaterialTheme.colorScheme.secondary,
                            disabledLabelColor = Color(0xFFBDADD5),
                            disabledPlaceholderColor = Color(0xFFBDADD5)
                        ),
                        placeholder = {
                            Text(
                                text = state.selectedTime?.let {
                                    "${it.hour}:${it.minute.toString().padStart(2, '0')}"
                                } ?: "HH:MM",
                                color = Color(0xFFBDADD5)
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { timePickerDialog.show() }) {
                                Image(
                                    painter = painterResource(id = R.drawable.pen),
                                    contentDescription = "Select date",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        singleLine = true
                    )
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .width(280.dp)
                        .height(150.dp)
                ) {
                    OutlinedTextField(
                        value = state.notes,
                        onValueChange = onNotesChange,
                        singleLine = false,
                        maxLines = 3,
                        modifier = Modifier.fillMaxSize(),
                        label = { Text("Notes") },
                        placeholder = { Text(state.notes) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.secondary,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color(0xFFFFFFFF),
                            unfocusedContainerColor = Color(0xFFFFFFFF),
                            focusedLabelColor = MaterialTheme.colorScheme.secondary,
                            unfocusedLabelColor = Color(0xFFBDADD5),
                            focusedTextColor = MaterialTheme.colorScheme.secondary,
                            unfocusedTextColor = MaterialTheme.colorScheme.secondary,
                            unfocusedTrailingIconColor = MaterialTheme.colorScheme.secondary,
                            focusedTrailingIconColor = MaterialTheme.colorScheme.secondary,
                            disabledContainerColor = Color.White,
                            disabledBorderColor = Color.Transparent,
                            disabledTextColor = MaterialTheme.colorScheme.secondary,
                            disabledLabelColor = Color(0xFFBDADD5),
                            disabledPlaceholderColor = Color(0xFFBDADD5)
                        ),
                        trailingIcon = {
                            Spacer(modifier = Modifier.size(24.dp))
                        }
                    )
                    IconButton(
                        onClick = {},
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 8.dp, end = 0.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.pen),
                            contentDescription = "Edit notes",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onSaveClick,
                    modifier = Modifier
                        .height(76.dp)
                        .width(300.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "SAVE CHANGES",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 28.sp
                    )
                }

                Spacer(modifier = Modifier.height(100.dp))
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

@Composable
fun DayCircle(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(if (isSelected) MaterialTheme.colorScheme.secondary else Color.Transparent)
            .border(1.dp, if (isSelected) Color.Transparent else MaterialTheme.colorScheme.secondary, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun EditTaskScreenPreview() {
    val sampleState = EditTaskState(
        title = "Morning Walk",
        type = taskTypeEnum.walk,
        notes = "Remember to bring treats! aa b ggggg hfdsdh hfg",
        selectedDate = LocalDate(2025, 5, 20),
        selectedTime = LocalTime(8, 30)
    )
    PetCareTheme {
        EditTaskScreen(
            state = sampleState,
            onTitleChange = {},
            onTypeChange = {},
            onTimeChange = {},
            onNotesChange = {},
            onSaveClick = {},
            onBackClick = {}
        )
    }
}