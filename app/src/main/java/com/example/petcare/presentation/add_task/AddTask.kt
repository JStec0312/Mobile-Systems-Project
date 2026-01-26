package com.example.petcare.presentation.add_task

import android.app.TimePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.LocalTextStyle
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
import com.example.petcare.R
import com.example.petcare.common.taskTypeEnum
import com.example.petcare.domain.model.Pet
import com.example.petcare.presentation.common.BaseScreen
import com.example.petcare.presentation.common.PetTextField
import com.example.petcare.presentation.theme.PetCareTheme
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import java.util.Calendar
import kotlin.text.lowercase

@Composable
fun AddTaskRoute(
    viewModel: AddTaskViewModel = hiltViewModel(),
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
            Toast.makeText(context, "Task added successfully", Toast.LENGTH_SHORT).show()
            viewModel.onSuccessShown()
            onNavigateBack()
        }
    }

    AddTaskScreen(
        state = state,
        onTitleChange = viewModel::onTitleChange,
        onTypeChange = viewModel::onTypeChange,
        onDateChange = viewModel::onDateChange,
        onTimeChange = viewModel::onTimeChange,
        onNotesChange = viewModel::onNotesChange,
        onRecurrenceToggled = viewModel::onRecurrenceToggled,
        onRecurrenceTypeChange = viewModel::onRecurrenceTypeChange,
        onIntervalChange = viewModel::onIntervalChange,
        onDaySelected = viewModel::onDaySelected,
        onSaveClick = viewModel::onSaveClick,
        onBackClick = onNavigateBack,
        onPetSelected = viewModel::onPetSelected
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    state: AddTaskState,
    onTitleChange: (String) -> Unit,
    onTypeChange: (taskTypeEnum) -> Unit,
    onDateChange: (LocalDate) -> Unit,
    onTimeChange: (LocalTime) -> Unit,
    onNotesChange: (String) -> Unit,
    onRecurrenceToggled: (Boolean) -> Unit,
    onRecurrenceTypeChange: (RecurrenceType) -> Unit,
    onIntervalChange: (String) -> Unit,
    onDaySelected: (DayOfWeek) -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit,
    onPetSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var typeExpanded by remember { mutableStateOf(false) }
    var recurrenceExpanded by remember { mutableStateOf(false) }

    val taskTypes = taskTypeEnum.values().toList()
    val recurrenceOptions = RecurrenceType.values().toList()

    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            onDateChange(LocalDate(year, month + 1, dayOfMonth))
        },
        state.selectedDate?.year ?: calendar.get(Calendar.YEAR),
        state.selectedDate?.monthNumber?.minus(1) ?: calendar.get(Calendar.MONTH),
        state.selectedDate?.dayOfMonth ?: calendar.get(Calendar.DAY_OF_MONTH)
    )

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
                    .verticalScroll(scrollState)
                    .padding(bottom = 32.dp)
                    .fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(80.dp))
                if(state.isPetSelectionEnabled) {
                    PetSelector(
                        availablePets = state.availablePets,
                        selectedPetId = state.selectedPetId,
                        onPetSelected = onPetSelected
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                PetTextField(
                    value = state.title,
                    onValueChange = onTitleChange,
                    label = "Title"
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
                        placeholder = { Text("Select type") },
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

                Row(
                    modifier = Modifier.width(280.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedTextField(
                        value = state.selectedDate.toString(),
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { datePickerDialog.show() },
                        label = { Text("Start date") },
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
                                text = "YYYY-MM-DD",
                                color = Color(0xFFBDADD5)
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { datePickerDialog.show() }) {
                                Image(
                                    painter = painterResource(id = R.drawable.calendar),
                                    contentDescription = "Select date",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedTextField(
                        value = state.selectedTime?.let {
                            "${it.hour}:${
                                it.minute.toString().padStart(2, '0')
                            }"
                        } ?: "",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .weight(0.7f)
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
                                text = "HH:MM",
                                color = Color(0xFFBDADD5)
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { timePickerDialog.show() }) {
                                Image(
                                    painter = painterResource(id = R.drawable.clock),
                                    contentDescription = "Select date",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        },
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.width(280.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Repeat task?",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Switch(
                                checked = state.isRecurring,
                                onCheckedChange = onRecurrenceToggled,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = MaterialTheme.colorScheme.secondary,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color(0xFFBDADD5),
                                    uncheckedBorderColor = Color.Transparent
                                )
                            )
                        }
                        if (state.isRecurring) {
                            Spacer(modifier = Modifier.height(10.dp))

                            ExposedDropdownMenuBox(
                                expanded = recurrenceExpanded,
                                onExpandedChange = { recurrenceExpanded = !recurrenceExpanded },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = state.recurrenceType.name.lowercase()
                                        .replaceFirstChar { it.uppercase() },
                                    onValueChange = {},
                                    readOnly = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    label = { Text("Frequency") },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.secondary,
                                        unfocusedBorderColor = Color(0xFFBDADD5),
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
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = recurrenceExpanded
                                        )
                                    }
                                )
                                ExposedDropdownMenu(
                                    expanded = recurrenceExpanded,
                                    onDismissRequest = { recurrenceExpanded = false },
                                    modifier = Modifier.background(Color.White)
                                ) {
                                    recurrenceOptions.forEach { option ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    option.name.lowercase()
                                                        .replaceFirstChar { it.uppercase() })
                                            },
                                            onClick = {
                                                onRecurrenceTypeChange(option)
                                                recurrenceExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            if (state.recurrenceType == RecurrenceType.WEEKLY) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Repeat on:",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    val days = listOf(
                                        DayOfWeek.MONDAY,
                                        DayOfWeek.TUESDAY,
                                        DayOfWeek.WEDNESDAY,
                                        DayOfWeek.THURSDAY,
                                        DayOfWeek.FRIDAY,
                                        DayOfWeek.SATURDAY,
                                        DayOfWeek.SUNDAY
                                    )
                                    days.forEach { day ->
                                        val isSelected = state.selectedDaysOfWeek.contains(day)
                                        DayCircle(
                                            text = day.name.take(1),
                                            isSelected = isSelected,
                                            onClick = { onDaySelected(day) }
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val isCustomInterval = state.repeatInterval > 1
                                Checkbox(
                                    checked = isCustomInterval,
                                    onCheckedChange = { isChecked ->
                                        if (isChecked) {
                                            onIntervalChange(if (state.repeatInterval == 1) "2" else state.repeatInterval.toString())
                                        } else {
                                            onIntervalChange("1")
                                        }
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = MaterialTheme.colorScheme.secondary,
                                        uncheckedColor = Color(0xFFBDADD5)
                                    )
                                )
                                Text(
                                    "Every",
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                TextField(
                                    value = if (isCustomInterval) state.repeatInterval.toString() else "",
                                    onValueChange = { onIntervalChange(it) },
                                    enabled = isCustomInterval,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    textStyle = LocalTextStyle.current.copy(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCustomInterval) MaterialTheme.colorScheme.secondary else Color(
                                            0xFFBDADD5
                                        ),
                                        textAlign = TextAlign.Center
                                    ),
                                    modifier = Modifier
                                        .width(50.dp)
                                        .clickable { if (!isCustomInterval) onIntervalChange("2") },
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        disabledContainerColor = Color.Transparent,
                                        focusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                                        unfocusedIndicatorColor = if (isCustomInterval) MaterialTheme.colorScheme.secondary else Color(
                                            0xFFBDADD5
                                        ),
                                        disabledIndicatorColor = Color.Transparent,
                                        focusedTextColor = MaterialTheme.colorScheme.secondary,
                                        unfocusedTextColor = MaterialTheme.colorScheme.secondary,
                                        disabledTextColor = MaterialTheme.colorScheme.secondary,
                                        cursorColor = MaterialTheme.colorScheme.secondary
                                    ),
                                )
                                val unit = when (state.recurrenceType) {
                                    RecurrenceType.DAILY -> "days"
                                    RecurrenceType.WEEKLY -> "weeks"
                                    RecurrenceType.MONTHLY -> "months"
                                }
                                Text(
                                    " $unit",
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = state.notes,
                    onValueChange = onNotesChange,
                    singleLine = false,
                    maxLines = 3,
                    modifier = Modifier.height(150.dp).width(280.dp),
                    label = { Text("Notes") },
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
                )

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
                        text = "ADD TASK",
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetSelector(
    availablePets: List<Pet>,
    selectedPetId: String?,
    onPetSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedPetName = availablePets.find { it.id == selectedPetId }?.name ?: "Select pet"

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.width(280.dp)
    ) {
        OutlinedTextField(
            value = selectedPetName,
            onValueChange = {},
            readOnly = true,
            placeholder = { Text("Select pet") },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            label = { Text("For who?") },
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
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            availablePets.forEach { pet ->
                DropdownMenuItem(
                    text = {
                        Text(
                            pet.name.lowercase().replaceFirstChar { it.uppercase() })
                    },
                    onClick = {
                        onPetSelected(pet.id)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
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
fun AddTaskPreview_Normal() {
        val state = AddTaskState(
            title = "Buy food",
            selectedDate = LocalDate(2025, 6, 15),
            selectedTime = LocalTime(14, 30),
            isRecurring = false,
            notes = ""
        )

        com.example.petcare.presentation.theme.PetCareTheme {
            AddTaskScreen(
                state = state,
                onTitleChange = {},
                onTypeChange = {},
                onDateChange = {},
                onTimeChange = {},
                onNotesChange = {},
                onRecurrenceToggled = {},
                onRecurrenceTypeChange = {},
                onIntervalChange = {},
                onDaySelected = {},
                onSaveClick = {},
                onBackClick = {},
                onPetSelected = {}
            )
        }

}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, heightDp = 1000)
@Composable
fun AddTaskPreview_Recurring() {
    val state = AddTaskState(
        title = "Morning Walk",
        selectedDate = LocalDate(2025, 6, 15),
        selectedTime = LocalTime(7, 0),
        isRecurring = true,
        recurrenceType = RecurrenceType.WEEKLY,
        repeatInterval = 2,
        selectedDaysOfWeek = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
        notes = "Don't forget treats"
    )

    com.example.petcare.presentation.theme.PetCareTheme {
        AddTaskScreen(
            state = state,
            onTitleChange = {},
            onTypeChange = {},
            onDateChange = {},
            onTimeChange = {},
            onNotesChange = {},
            onRecurrenceToggled = {},
            onRecurrenceTypeChange = {},
            onIntervalChange = {},
            onDaySelected = {},
            onSaveClick = {},
            onBackClick = {},
            onPetSelected = {}
        )
    }
}
