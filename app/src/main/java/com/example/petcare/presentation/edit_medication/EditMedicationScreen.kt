package com.example.petcare.presentation.edit_medication

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.petcare.R
import com.example.petcare.presentation.add_medication.MedRecurrenceType
import com.example.petcare.presentation.add_medication.MedicationForm
import com.example.petcare.presentation.common.BaseScreen
import com.example.petcare.presentation.theme.PetCareTheme
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.Calendar

@Composable
fun EditMedicationRoute(
    viewModel: EditMedicationViewModel = hiltViewModel(),
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
            Toast.makeText(context, "Medication updated successfully", Toast.LENGTH_SHORT).show()
            viewModel.onSuccessShown()
            onNavigateBack()
        }
    }

    EditMedicationScreen(
        state = state,
        onNameChange = viewModel::onNameChange,
        onFormChange = viewModel::onFormChange,
        onDoseChange = viewModel::onDoseChange,
        onNotesChange = viewModel::onNotesChange,
        onStartDateChange = viewModel::onStartDateChange,
        onEndDateChange = viewModel::onEndDateChange,
        // NAPRAWA: Używamy poprawnych nazw funkcji z VM
        onReminderTimeChange = viewModel::onReminderTimeChange,
        onReminderEnabledChange = viewModel::onReminderEnabledChange,
        onRecurrenceToggled = viewModel::onRecurrenceToggled,
        onRecurrenceTypeChange = viewModel::onRecurrenceTypeChange,
        onIntervalChange = viewModel::onIntervalChange,
        onDaySelected = viewModel::onDaySelected,
        onSaveClick = viewModel::onSaveClick,
        onBackClick = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMedicationScreen(
    state: EditMedicationState,
    onNameChange: (String) -> Unit,
    onFormChange: (MedicationForm) -> Unit,
    onDoseChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onStartDateChange: (LocalDate) -> Unit,
    onEndDateChange: (LocalDate?) -> Unit,
    // Poprawione parametry
    onReminderTimeChange: (LocalTime) -> Unit,
    onReminderEnabledChange: (Boolean) -> Unit,
    onRecurrenceToggled: (Boolean) -> Unit,
    onRecurrenceTypeChange: (MedRecurrenceType) -> Unit,
    onIntervalChange: (String) -> Unit,
    onDaySelected: (DayOfWeek) -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var formExpanded by remember { mutableStateOf(false) }
    var recurrenceExpanded by remember { mutableStateOf(false) }

    // Date Picker
    var showDatePicker by remember { mutableStateOf(false) }
    var isSelectingStartDate by remember { mutableStateOf(true) }
    val datePickerState = rememberDatePickerState()

    // Time Picker
    var showTimePicker by remember { mutableStateOf(false) }
    val calendar = Calendar.getInstance()
    val timePickerState = rememberTimePickerState(
        initialHour = state.reminderTime?.hour ?: calendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = state.reminderTime?.minute ?: calendar.get(Calendar.MINUTE),
        is24Hour = true
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedMillis = datePickerState.selectedDateMillis
                        if (selectedMillis != null) {
                            val date = Instant.fromEpochMilliseconds(selectedMillis)
                                .toLocalDateTime(TimeZone.currentSystemDefault()).date
                            if (isSelectingStartDate) onStartDateChange(date) else onEndDateChange(date)
                        }
                        showDatePicker = false
                    }
                ) { Text("OK", color = Color.Black) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel", color = Color.Black) }
            }
        ) { DatePicker(state = datePickerState) }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    onReminderTimeChange(LocalTime(timePickerState.hour, timePickerState.minute))
                    showTimePicker = false
                }) { Text("OK", color = Color.Black) }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel", color = Color.Black) }
            },
            text = { Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { TimePicker(state = timePickerState) } },
            containerColor = Color.White
        )
    }

    val customColors = OutlinedTextFieldDefaults.colors(
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
    )

    BaseScreen(isLoading = state.isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.paw_prints),
                contentDescription = "",
                modifier = Modifier.scale(scaleX = -1f, scaleY = -1f).align(Alignment.TopStart).offset(x = 120.dp, y = 150.dp).size(500.dp)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 32.dp).fillMaxSize().verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(80.dp))

                // Name
                OutlinedTextField(
                    value = state.name,
                    onValueChange = onNameChange,
                    label = { Text("Medication Name") },
                    modifier = Modifier.width(280.dp),
                    colors = customColors,
                    trailingIcon = { Icon(painterResource(id = R.drawable.pen), "Edit", Modifier.size(20.dp), tint = MaterialTheme.colorScheme.secondary) },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Form
                ExposedDropdownMenuBox(
                    expanded = formExpanded,
                    onExpandedChange = { formExpanded = !formExpanded },
                    modifier = Modifier.width(280.dp)
                ) {
                    OutlinedTextField(
                        value = state.form?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "",
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Select form") },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        label = { Text("Form") },
                        colors = customColors,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = formExpanded) },
                    )
                    ExposedDropdownMenu(
                        expanded = formExpanded,
                        onDismissRequest = { formExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        MedicationForm.values().forEach { form ->
                            DropdownMenuItem(
                                text = { Text(form.name.lowercase().replaceFirstChar { it.uppercase() }) },
                                onClick = { onFormChange(form); formExpanded = false },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Dose
                OutlinedTextField(
                    value = state.dose,
                    onValueChange = onDoseChange,
                    label = { Text("Dosage") },
                    modifier = Modifier.width(280.dp),
                    colors = customColors,
                    trailingIcon = { Icon(painterResource(id = R.drawable.pen), "Edit", Modifier.size(20.dp), tint = MaterialTheme.colorScheme.secondary) },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Recurrence Section
                Card(
                    modifier = Modifier.width(280.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Repeat medication?", fontSize = 16.sp, color = MaterialTheme.colorScheme.secondary)
                            Switch(
                                checked = state.isRecurring,
                                onCheckedChange = onRecurrenceToggled,
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MaterialTheme.colorScheme.secondary)
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
                                    value = state.recurrenceType.name.lowercase().replace("as_needed", "as needed").replaceFirstChar { it.uppercase() },
                                    onValueChange = {},
                                    readOnly = true,
                                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                                    colors = customColors,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = recurrenceExpanded) }
                                )
                                ExposedDropdownMenu(
                                    expanded = recurrenceExpanded,
                                    onDismissRequest = { recurrenceExpanded = false },
                                    modifier = Modifier.background(Color.White)
                                ) {
                                    MedRecurrenceType.values().forEach { option ->
                                        DropdownMenuItem(
                                            text = { Text(option.name.lowercase().replace("as_needed", "as needed").replaceFirstChar { it.uppercase() }) },
                                            onClick = { onRecurrenceTypeChange(option); recurrenceExpanded = false }
                                        )
                                    }
                                }
                            }

                            if (state.recurrenceType != MedRecurrenceType.AS_NEEDED) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Every", color = MaterialTheme.colorScheme.secondary, modifier = Modifier.padding(end = 8.dp))
                                    TextField(
                                        value = state.repeatInterval.toString(),
                                        onValueChange = onIntervalChange,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        modifier = Modifier.width(50.dp),
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = Color.Transparent,
                                            unfocusedContainerColor = Color.Transparent,
                                            focusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                                            unfocusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                                            focusedTextColor = MaterialTheme.colorScheme.secondary,
                                            unfocusedTextColor = MaterialTheme.colorScheme.secondary
                                        )
                                    )
                                    val unit = when (state.recurrenceType) {
                                        MedRecurrenceType.DAILY -> "days"
                                        MedRecurrenceType.WEEKLY -> "weeks"
                                        MedRecurrenceType.MONTHLY -> "months"
                                        else -> ""
                                    }
                                    Text(" $unit", color = MaterialTheme.colorScheme.secondary, modifier = Modifier.padding(start = 8.dp))
                                }

                                if (state.recurrenceType == MedRecurrenceType.WEEKLY) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        val days = listOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
                                        days.forEach { day ->
                                            val isSelected = state.selectedDays.contains(day)
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(CircleShape)
                                                    .clickable { onDaySelected(day) }
                                                    .background(if(isSelected) MaterialTheme.colorScheme.secondary else Color.Transparent)
                                                    .border(1.dp, if(isSelected) Color.Transparent else MaterialTheme.colorScheme.secondary, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = day.name.take(1),
                                                    color = if(isSelected) Color.White else MaterialTheme.colorScheme.secondary,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // DATES
                Row(modifier = Modifier.width(280.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = state.startDate.toString(),
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.weight(1f).clickable { isSelectingStartDate = true; showDatePicker = true },
                        label = { Text("Start") },
                        colors = customColors,
                        placeholder = { Text("YYYY-MM-DD", color = Color(0xFFBDADD5)) },
                        trailingIcon = { IconButton(onClick = { isSelectingStartDate = true; showDatePicker = true }) { Image(painterResource(id = R.drawable.calendar), null, Modifier.size(24.dp)) } },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = state.endDate?.toString() ?: "",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.weight(1f).clickable { isSelectingStartDate = false; showDatePicker = true },
                        label = { Text("End") },
                        placeholder = { Text("Optional") },
                        colors = customColors,
                        trailingIcon = { IconButton(onClick = { isSelectingStartDate = false; showDatePicker = true }) { Image(painterResource(id = R.drawable.calendar), null, Modifier.size(24.dp)) } },
                        singleLine = true
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // REMINDERS
                Card(modifier = Modifier.width(280.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Enable reminders", fontSize = 16.sp, color = MaterialTheme.colorScheme.secondary)
                            Switch(
                                checked = state.isReminderEnabled,
                                onCheckedChange = onReminderEnabledChange,
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MaterialTheme.colorScheme.secondary)
                            )
                        }
                        if (state.isReminderEnabled) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Reminder time", color = MaterialTheme.colorScheme.secondary)
                                Box(
                                    modifier = Modifier.background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp)).clickable { showTimePicker = true }.padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(text = state.reminderTime?.let { "${it.hour}:${it.minute.toString().padStart(2, '0')}" } ?: "Select Time", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // NOTES
                Box(modifier = Modifier.width(280.dp).height(150.dp)) {
                    OutlinedTextField(
                        value = state.notes,
                        onValueChange = onNotesChange,
                        singleLine = false,
                        maxLines = 5,
                        modifier = Modifier.fillMaxSize(),
                        label = { Text("Notes") },
                        colors = customColors,
                        trailingIcon = { Spacer(modifier = Modifier.size(24.dp)) }
                    )
                    IconButton(
                        onClick = {},
                        modifier = Modifier.align(Alignment.TopEnd).padding(top = 8.dp, end = 0.dp)
                    ) {
                        Icon(painterResource(id = R.drawable.pen), "Edit notes", Modifier.size(20.dp), tint = MaterialTheme.colorScheme.secondary)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onSaveClick,
                    modifier = Modifier.height(76.dp).width(300.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(color = Color.White)
                    } else {
                        Text("SAVE CHANGES", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp)
                    }
                }
                Spacer(modifier = Modifier.height(60.dp))
            }

            IconButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
            ) {
                Image(painterResource(id = R.drawable.cross), "Back", Modifier.size(24.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditMedicationPreview() {
    PetCareTheme {
        EditMedicationScreen(
            state = EditMedicationState(
                name = "Apap",
                form = MedicationForm.TABLET,
                dose = "1 tab",
                notes = "Take with water",
                isRecurring = true,
                recurrenceType = MedRecurrenceType.DAILY
            ),
            onNameChange = {}, onFormChange = {}, onDoseChange = {}, onNotesChange = {},
            onStartDateChange = {}, onEndDateChange = {},
            onReminderTimeChange = {}, onReminderEnabledChange = {},
            onRecurrenceToggled = {}, onRecurrenceTypeChange = {}, onIntervalChange = {}, onDaySelected = {},
            onSaveClick = {}, onBackClick = {}
        )
    }
}