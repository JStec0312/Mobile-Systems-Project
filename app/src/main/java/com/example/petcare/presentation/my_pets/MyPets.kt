package com.example.petcare.presentation.my_pets

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.petcare.R
import com.example.petcare.common.sexEnum
import com.example.petcare.common.speciesEnum
import com.example.petcare.domain.model.Pet
import com.example.petcare.presentation.common.BaseScreen
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import coil.compose.AsyncImage


@Composable
fun MyPetsRoute(
    viewModel: MyPetsViewModel = hiltViewModel(),
    onNavigateToPetDetails: (String) -> Unit,
    onNavigateToAddPet: () -> Unit,
    onNavigateToEditPet: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    MyPetsScreen(
        state = state,
        onPetClick = onNavigateToPetDetails,
        onAddPetClick = onNavigateToAddPet,
        onSearchQueryChange = { query ->
            viewModel.onSearchQueryChange(query)
        },
        onEditPetClick = onNavigateToEditPet
    )
}

@Composable
fun MyPetsScreen(
    state: MyPetsState,
    onPetClick: (String) -> Unit,
    onAddPetClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onEditPetClick: (String) -> Unit
) {
    BaseScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween

            ) {
                OutlinedTextField(
                    value = "",
                    onValueChange = onSearchQueryChange,
                    placeholder = {
                        Text(
                            "Search for pets...",
                            color = MaterialTheme.colorScheme.secondary
                        )
                    },
                    leadingIcon = null,
                    trailingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.search),
                            contentDescription = "Search Icon",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    modifier = Modifier
                        .weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF605397),
                        unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(
                    onClick = onAddPetClick,
                    modifier = Modifier
                        .size(56.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.plus),
                        contentDescription = "Add pet",
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF605397))
                }
            } else if (state.pets.isEmpty()) {
                Spacer(modifier = Modifier.size(24.dp))
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "NO PETS FOUND YET",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF605397)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Click + to add your first pet!",
                            fontSize = 16.sp,
                            color = Color(0xFF605397)
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(state.pets) { pet ->
                        PetCard(
                            pet = pet,
                            onClick = { onPetClick(pet.id) },
                            onEditClick = { onEditPetClick(pet.id) }
                            )
                    }
                }
            }
        }
    }
}

@Composable
fun PetCard(
    pet: Pet,
    onClick: () -> Unit,
    onEditClick: () -> Unit
) {
    val ageText = pet.birthDate.getAgeText()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if(pet.avatarThumbUrl != null) {
                AsyncImage(
                    model = pet.avatarThumbUrl,
                    contentDescription = pet.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    placeholder = painterResource(
                        id = if (pet.species == speciesEnum.dog) R.drawable.dog_pp else R.drawable.cat_pp
                    )
                )
            }
            else {
                if(pet.species == speciesEnum.dog) {
                    Image(
                        painter = painterResource(id = R.drawable.dog_pp),
                        contentDescription = "dog profile picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    )
                }
                else {
                    Image(
                        painter = painterResource(id = R.drawable.cat_pp),
                        contentDescription = "cat profile picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = pet.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text ="${pet.breed}",
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Text(
                    text = ageText,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End,
                modifier = Modifier.fillMaxHeight()
            ) {
                IconButton(onClick = {}) {
                    Image(
                        painter = painterResource(id = R.drawable.heart),
                        contentDescription = "Favourite",

                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = onEditClick) {
                    Image(
                        painter = painterResource(R.drawable.edit),
                        contentDescription = "Edit",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

fun LocalDate.getAgeText() : String {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val years = today.year - this.year
    return if(years > 0) {
        "$years years old"
    } else {
        val months = today.monthNumber - this.monthNumber
        if(months > 0) "$months months old" else "< 1 month old"
    }
}

@Preview
@Composable
fun MyPetsScreenEmptyPreview() {
    MyPetsScreen(
        state = MyPetsState(pets = emptyList()),
        onPetClick = {},
        onAddPetClick = {},
        onSearchQueryChange = {},
        onEditPetClick = {}
    )
}

@Preview
@Composable
fun MyPetsScreenPreview() {
    val samplePets = listOf(
        Pet("1", "u1", "Minnie", speciesEnum.dog, "French Bulldog", sexEnum.female, LocalDate(2018, 5, 12), null, LocalDate.parse("2024-01-01")),
        Pet("2", "u1", "Aslan", speciesEnum.cat, "Rhodesian Ridgeback", sexEnum.male, LocalDate(2023, 9, 10), null, LocalDate.parse("2024-01-01")),
        Pet("3", "u1", "Blues", speciesEnum.dog, "Cocker Spaniel", sexEnum.male, LocalDate(2013, 1, 1), null, LocalDate.parse("2024-01-01"))
    )

    MyPetsScreen(
        state = MyPetsState(pets = samplePets),
        onPetClick = {},
        onAddPetClick = {},
        onSearchQueryChange = {},
        onEditPetClick = {}
    )
}
