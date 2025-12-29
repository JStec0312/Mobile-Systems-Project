package com.example.petcare.presentation.add_medication

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.petcare.R
import com.example.petcare.presentation.common.BaseScreen
import com.example.petcare.presentation.theme.PetCareTheme
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import java.util.Calendar

enum class MedicationForm {
    TABLET, CAPSULE, SYRUP, INJECTION, DROPS, OINTMENT, OTHER
}

enum class MedRecurrenceType {
    DAILY, WEEKLY, MONTHLY, AS_NEEDED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicationScreen(
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // --- STAN ---
    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }
    var selectedForm by remember { mutableStateOf<MedicationForm?>(null) }

    // Daty
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) } // Nowe: End Date
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }

    // Powtarzalność
    var recurrenceType by remember { mutableStateOf(MedRecurrenceType.DAILY) }
    var repeatInterval by remember { mutableStateOf(1) }

    // Przypomnienia (Nowe, z Mocka)
    var isReminderEnabled by remember { mutableStateOf(true) }

    // Dropdowny
    var formExpanded by remember { mutableStateOf(false) }
    var recurrenceExpanded by remember { mutableStateOf(false) }

    // Kalendarze
    val calendar = Calendar.getInstance()

    // Helper do daty
    fun showDatePicker(onDateSelected: (LocalDate) -> Unit) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth -> onDateSelected(LocalDate(year, month + 1, dayOfMonth)) },
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute -> selectedTime = LocalTime(hourOfDay, minute) },
        calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true
    )

    // STYL PÓL (Identyczny jak w AddTask)
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
    )

    BaseScreen {
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

                // Kontener dla pól (używamy paddingu zamiast sztywnej szerokości, żeby naprawić wygląd)
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)) {

                    // 1. NAME
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Medication Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = customColors,
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 2. FORM
                    ExposedDropdownMenuBox(
                        expanded = formExpanded,
                        onExpandedChange = { formExpanded = !formExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedForm?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "",
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
                                    onClick = { selectedForm = form; formExpanded = false }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3. DOSAGE
                    OutlinedTextField(
                        value = dosage,
                        onValueChange = { dosage = it },
                        label = { Text("Dosage (e.g. 1 tablet)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = customColors,
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 4. FREQUENCY (Przeniesione wyżej, jak w Mocku)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Frequency", fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.height(8.dp))

                            ExposedDropdownMenuBox(
                                expanded = recurrenceExpanded,
                                onExpandedChange = { recurrenceExpanded = !recurrenceExpanded },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = recurrenceType.name.lowercase().replace("as_needed", "as needed").replaceFirstChar { it.uppercase() },
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
                                            onClick = { recurrenceType = option; recurrenceExpanded = false }
                                        )
                                    }
                                }
                            }

                            // Jeśli wybrano "Daily", "Weekly" itp - pokaż interwał
                            if (recurrenceType != MedRecurrenceType.AS_NEEDED) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Every", color = MaterialTheme.colorScheme.secondary, modifier = Modifier.padding(end = 8.dp))
                                    TextField(
                                        value = repeatInterval.toString(),
                                        onValueChange = { repeatInterval = it.filter { c -> c.isDigit() }.toIntOrNull() ?: 1 },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        textStyle = LocalTextStyle.current.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary, textAlign = TextAlign.Center),
                                        modifier = Modifier.width(50.dp),
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = Color.Transparent,
                                            unfocusedContainerColor = Color.Transparent,
                                            focusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                                            unfocusedIndicatorColor = MaterialTheme.colorScheme.secondary
                                        )
                                    )
                                    val unit = when (recurrenceType) {
                                        MedRecurrenceType.DAILY -> "days"
                                        MedRecurrenceType.WEEKLY -> "weeks"
                                        MedRecurrenceType.MONTHLY -> "months"
                                        else -> ""
                                    }
                                    Text(" $unit", color = MaterialTheme.colorScheme.secondary, modifier = Modifier.padding(start = 8.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 5. START DATE & END DATE (Duration z Mocka)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Start Date
                        OutlinedTextField(
                            value = selectedDate?.toString() ?: "",
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.weight(1f).clickable { showDatePicker { selectedDate = it } },
                            label = { Text("Start Date") },
                            colors = customColors,
                            placeholder = { Text("YYYY-MM-DD") },
                            trailingIcon = {
                                IconButton(onClick = { showDatePicker { selectedDate = it } }) {
                                    Image(painter = painterResource(id = R.drawable.calendar), contentDescription = null, modifier = Modifier.size(20.dp))
                                }
                            },
                            singleLine = true
                        )

                        // End Date (Opcjonalne)
                        OutlinedTextField(
                            value = endDate?.toString() ?: "",
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.weight(1f).clickable { showDatePicker { endDate = it } },
                            label = { Text("End Date") }, // W mocku to "Duration / End Date"
                            colors = customColors,
                            placeholder = { Text("Optional") },
                            trailingIcon = {
                                IconButton(onClick = { showDatePicker { endDate = it } }) {
                                    Image(painter = painterResource(id = R.drawable.calendar), contentDescription = null, modifier = Modifier.size(20.dp))
                                }
                            },
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 6. REMINDER SETTINGS (Zgodnie z Mockiem)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Enable reminders", fontSize = 16.sp, color = MaterialTheme.colorScheme.secondary)
                                Switch(
                                    checked = isReminderEnabled,
                                    onCheckedChange = { isReminderEnabled = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = MaterialTheme.colorScheme.secondary,
                                        uncheckedThumbColor = Color.White,
                                        uncheckedTrackColor = Color(0xFFBDADD5),
                                        uncheckedBorderColor = Color.Transparent
                                    )
                                )
                            }

                            // Pokaż wybór godziny tylko jeśli przypomnienia włączone
                            if (isReminderEnabled) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF0F0F0))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Reminder time", color = MaterialTheme.colorScheme.secondary)

                                    Box(
                                        modifier = Modifier
                                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                                            .clickable { timePickerDialog.show() }
                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = selectedTime?.let { "${it.hour}:${it.minute.toString().padStart(2, '0')}" } ?: "Select Time",
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 7. INSTRUCTIONS
                    OutlinedTextField(
                        value = instructions,
                        onValueChange = { instructions = it },
                        singleLine = false,
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        label = { Text("Special Instructions") },
                        colors = customColors,
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // BUTTONS
                Button(
                    onClick = { /* TODO: Save */ },
                    modifier = Modifier.height(60.dp).width(300.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("ADD MEDICATION", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Cancel button (w stylu tekstu, żeby nie przytłaczał)
                Text(
                    text = "CANCEL",
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateBack() }
                )

                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddMedicationPreview() {
    PetCareTheme {
        AddMedicationScreen()
    }
}