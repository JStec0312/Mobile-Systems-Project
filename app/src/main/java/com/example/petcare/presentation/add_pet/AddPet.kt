package com.example.petcare.presentation.add_pet

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import kotlinx.datetime.Instant
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType.Companion.PrimaryNotEditable
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import android.net.Uri
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.petcare.R
import com.example.petcare.common.sexEnum
import com.example.petcare.common.speciesEnum
import com.example.petcare.presentation.common.BaseScreen
import com.example.petcare.presentation.common.PetTextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.petcare.domain.model.Pet
import com.example.petcare.presentation.my_pets.getAgeText
import com.example.petcare.presentation.theme.PetCareTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun AddPetRoute(
    viewModel: AddPetViewModel = hiltViewModel(),
    onNavigationToMyPets: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.onAvatarChange(uri)
    }

    if (state.isSuccessful) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "Pet added successfully", Toast.LENGTH_SHORT).show()
            onNavigationToMyPets()
        }
    }
    if (state.error != null) {
        LaunchedEffect(state.error) {
            Toast.makeText(context, state.error, Toast.LENGTH_LONG).show()
        }
    }

    AddPetScreen(
        state = state,
        onModeChange = viewModel::onModeChange,
        onNameChange = viewModel::onNameChange,
        onSpeciesChange = viewModel::onSpeciesChange,
        onBreedChange = viewModel::onBreedChange,
        onDateSelected = viewModel::onBirthDateChange,
        onSexChange = viewModel::onSexChange,
        onPhotoClick = {imagePickerLauncher.launch("image/*")},
        onSaveClick = viewModel::onAddNewPet,
        onIdChange = viewModel::onIdChange,
        onSearchClick = viewModel::onSearchClick,
        onAddFoundPetClick = viewModel::onAddFoundPetClick,
        onBackFromConfirmation = viewModel::onBackFromConfirmation
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPetScreen(
    state: AddPetState,
    onModeChange: (AddPetMode) -> Unit,
    onNameChange: (String) -> Unit,
    onSpeciesChange: (speciesEnum) -> Unit,
    onBreedChange: (String) -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onSexChange: (sexEnum) -> Unit,
    onPhotoClick: () -> Unit,
    onSaveClick: () -> Unit,
    onIdChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onAddFoundPetClick: () -> Unit,
    onBackFromConfirmation: () -> Unit
) {
    val scrollState = rememberScrollState()

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableYear(year: Int): Boolean {
                val currentYear = Clock.System.now()
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .year
                return year <= currentYear
            }
        }
    )

    if(showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedMillis = datePickerState.selectedDateMillis
                        if (selectedMillis != null) {
                            val date = Instant.fromEpochMilliseconds(selectedMillis)
                                .toLocalDateTime(TimeZone.UTC).date
                            onDateSelected(date)
                        }
                        showDatePicker = false
                    }
                ) { Text("OK", color = Color.Black)}
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {Text("Cancel", color = Color.Black) }
            }
        ) {
            DatePicker(state=datePickerState)
        }
    }

    BaseScreen {
        if (state.foundPet != null) {
            PetConfirmationScreen(
                pet = state.foundPet,
                onAddClick = onAddFoundPetClick,
                onBackClick = onBackFromConfirmation
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
                            .background(if (state.currentMode == AddPetMode.CREATE_NEW) MaterialTheme.colorScheme.tertiary else Color.Transparent)
                            .clickable { onModeChange(AddPetMode.CREATE_NEW) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Create new pet",
                            color = if (state.currentMode == AddPetMode.CREATE_NEW) Color.White else MaterialTheme.colorScheme.tertiary,
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
                            .background(if (state.currentMode == AddPetMode.ADD_BY_ID) MaterialTheme.colorScheme.tertiary else Color.Transparent)
                            .clickable { onModeChange(AddPetMode.ADD_BY_ID) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Add pet by ID",
                            color = if (state.currentMode == AddPetMode.ADD_BY_ID) Color.White else MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                if (state.currentMode == AddPetMode.CREATE_NEW) {
                    CreateNewPetContent(
                        state = state,
                        onNameChange = onNameChange,
                        onSpeciesChange = onSpeciesChange,
                        onBreedChange = onBreedChange,
                        onDateClick = {showDatePicker = true},
                        onSexChange = onSexChange,
                        onPhotoClick = onPhotoClick,
                        onSaveClick = onSaveClick
                    )
                } else {
                    AddByIdContent(
                        state = state,
                        onIdChange = onIdChange,
                        onSearchClick = onSearchClick
                    )
                }
            }
        }
    }
}

@Composable
fun PetConfirmationScreen(
    pet: Pet,
    onAddClick: () -> Unit,
    onBackClick: () -> Unit
) {
    BaseScreen {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            contentAlignment = Alignment.Center
        ) {
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.height(155.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    if (pet.avatarThumbUrl != null) {
                        AsyncImage(
                            model = pet.avatarThumbUrl,
                            contentDescription = pet.name,
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(if (pet.species == speciesEnum.dog) R.drawable.dog_pp else R.drawable.cat_pp),
                            contentDescription = null,
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = pet.name.uppercase(),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        text = pet.birthDate.getAgeText(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = pet.breed ?: "Uknown mixed breed",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    Spacer(modifier = Modifier.height(80.dp))

                    Button(
                        onClick = onAddClick,
                        modifier = Modifier
                            .height(76.dp)
                            .width(300.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    ) {
                        Text(
                            text = "ADD",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 28.sp,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    TextButton(onClick = onBackClick) {
                        Image(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = "Go back",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "BACK",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                    Image(
                        painter = painterResource(id = R.drawable.petcare_logo_purple),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .width(200.dp)
                            .padding(bottom = 16.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNewPetContent(
        state: AddPetState,
        onNameChange: (String) -> Unit,
        onSpeciesChange: (speciesEnum) -> Unit,
        onBreedChange: (String) -> Unit,
        onDateClick: () -> Unit,
        onSexChange: (sexEnum) -> Unit,
        onPhotoClick: () -> Unit,
        onSaveClick: () -> Unit
    ) {
        var expanded by remember { mutableStateOf(false) }
        val options = listOf("Male", "Female", "Unknown")

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.BottomEnd) {
                if(state.avatarThumbUrl != null) {
                    AsyncImage(
                        model = state.avatarThumbUrl,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(if(state.species == speciesEnum.dog) R.drawable.dog_pp else R.drawable.cat_pp),
                        contentDescription = "Avatar placeholder",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                IconButton(
                    onClick = onPhotoClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.camera),
                        contentDescription = "Change photo",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            PetTextField(
                value = state.name,
                onValueChange = onNameChange,
                label = "Pet name"
            )
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy((-65).dp)
            ) {
                Image(
                    painter = painterResource(id = if (state.species == speciesEnum.dog) R.drawable.dog_clicked else R.drawable.dog_unclicked),
                    contentDescription = "Dog",
                    modifier = Modifier
                        .weight(1f)
                        .height(140.dp)
                        .clickable { onSpeciesChange(speciesEnum.dog) },
                    contentScale = ContentScale.Fit
                )
                Image(
                    painter = painterResource(id = if (state.species == speciesEnum.cat) R.drawable.cat_clicked else R.drawable.cat_unclicked),
                    contentDescription = "Cat",
                    modifier = Modifier
                        .weight(1f)
                        .height(140.dp)
                        .clickable { onSpeciesChange(speciesEnum.cat) },
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            PetTextField(
                value = state.breed,
                onValueChange = onBreedChange,
                label = "Breed"
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.width(280.dp)
            ) {
                OutlinedTextField(
                    value = state.sex.name.replaceFirstChar { it.uppercase() },
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(PrimaryNotEditable, true),
                    label = {Text("Sex")},
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.secondary,
                        unfocusedBorderColor = Color.Transparent,
                        unfocusedTextColor = Color(0xFFBDADD5),
                        focusedContainerColor = Color(0xFFFFFFFF),
                        unfocusedContainerColor = Color(0xFFFFFFFF),
                        focusedLabelColor = MaterialTheme.colorScheme.secondary,
                        unfocusedLabelColor = Color(0xFFBDADD5),
                        focusedTextColor = MaterialTheme.colorScheme.secondary,
                        unfocusedTrailingIconColor = MaterialTheme.colorScheme.secondary,
                        focusedTrailingIconColor = MaterialTheme.colorScheme.secondary
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
                    options.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                val selectedEnum = when (selectionOption) {
                                    "Male" -> sexEnum.male
                                    "Female" -> sexEnum.female
                                    "Unknown" -> sexEnum.unknown
                                    else -> sexEnum.unknown
                                }
                                onSexChange(selectedEnum)
                                expanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.birthDate?.toString() ?: "",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .clickable { onDateClick() },
                label = {Text("Date of birth")},
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.secondary,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color(0xFFFFFFFF),
                    unfocusedContainerColor = Color(0xFFFFFFFF),
                    focusedLabelColor = MaterialTheme.colorScheme.secondary,
                    unfocusedLabelColor = Color(0xFFBDADD5),
                    focusedTextColor = MaterialTheme.colorScheme.secondary,
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
                    IconButton(onClick = onDateClick) {
                        Image(
                            painter = painterResource(id = R.drawable.calendar),
                            contentDescription = "Select date",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                enabled = false,
                singleLine = true
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
                    text = "ADD PET",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 28.sp
                )
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
}

@Composable
fun AddByIdContent(
    state: AddPetState,
    onIdChange: (String) -> Unit,
    onSearchClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        PetTextField(
            value = state.petIdToAdd,
            onValueChange = onIdChange,
            label = "Pet ID"
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onSearchClick,
            modifier = Modifier
                .height(76.dp)
                .width(300.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                text = "SEARCH",
                fontSize = 28.sp,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Preview
@Composable
fun AddPetScreenPreview() {
    PetCareTheme {
        AddPetScreen(
            state = AddPetState(),
            onModeChange = {},
            onNameChange = {},
            onSpeciesChange = {},
            onBreedChange = {},
            onDateSelected = {},
            onSexChange = {},
            onPhotoClick = {},
            onSaveClick = {},
            onIdChange = {},
            onSearchClick = {},
            onAddFoundPetClick = {},
            onBackFromConfirmation = {}
        )
    }
}

@Preview
@Composable
fun AddPetScreenPreview2() {
    PetCareTheme {
        AddPetScreen(
            state = AddPetState(currentMode = AddPetMode.ADD_BY_ID),
            onModeChange = {},
            onNameChange = {},
            onSpeciesChange = {},
            onBreedChange = {},
            onDateSelected = {},
            onSexChange = {},
            onPhotoClick = {},
            onSaveClick = {},
            onIdChange = {},
            onSearchClick = {},
            onAddFoundPetClick = {},
            onBackFromConfirmation = {}
        )
    }
}

@Preview
@Composable
fun AddPetScreenPreview3() {
    PetCareTheme {
        AddPetScreen(
            state = AddPetState(currentMode = AddPetMode.ADD_BY_ID, foundPet = Pet(
                id = "123",
                ownerUserId = "user1",
                name = "Minnie",
                species = speciesEnum.dog,
                breed = "French Bulldog",
                sex = sexEnum.female,
                birthDate = LocalDate(2018, 5, 12),
                avatarThumbUrl = null,
                createdAt = LocalDate(2024, 1, 1)
            )
            ),
            onModeChange = {},
            onNameChange = {},
            onSpeciesChange = {},
            onBreedChange = {},
            onDateSelected = {},
            onSexChange = {},
            onPhotoClick = {},
            onSaveClick = {},
            onIdChange = {},
            onSearchClick = {},
            onAddFoundPetClick = {},
            onBackFromConfirmation = {}
        )
    }
}