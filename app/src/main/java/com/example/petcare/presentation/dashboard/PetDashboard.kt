package com.example.petcare.presentation.dashboard

import com.example.petcare.presentation.theme.PetCareTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.petcare.common.speciesEnum
import com.example.petcare.common.taskStatusEnum
import com.example.petcare.common.taskTypeEnum
import com.example.petcare.presentation.common.BaseScreen
import com.example.petcare.R
import com.example.petcare.common.sexEnum
import com.example.petcare.common.taskPriorityEnum
import com.example.petcare.domain.model.Pet
import com.example.petcare.domain.model.Task
import kotlinx.datetime.LocalDate

@Composable
fun PetDashboardRoute(
    viewModel: PetDashboardViewModel = hiltViewModel(),
    onNavigateToTasks: () -> Unit,
    onNavigateToMedicationHistory: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToWalk: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    PetDashboardScreen(
        state = state,
        onTaskDone = viewModel::onTaskDone,
        onViewAllTasks = onNavigateToTasks,
        onMedicationHistory = onNavigateToMedicationHistory,
        onChatWithVet = onNavigateToChat,
        onWalkClick = onNavigateToWalk
    )
}
@Composable
fun PetDashboardScreen(
    state: PetDashboardState,
    onTaskDone: () -> Unit,
    onViewAllTasks: () -> Unit,
    onMedicationHistory: () -> Unit,
    onChatWithVet: () -> Unit,
    onWalkClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    BaseScreen {
        if (state.pet == null && state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Good Morning!",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "How is ${state.pet?.name} doing today?",
                                color = Color.White,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { /*TODO*/ },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text(
                                    text = "Generate pet share code",
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        if (state.pet?.avatarThumbUrl != null) {
                            AsyncImage(
                                model = state.pet.avatarThumbUrl,
                                contentDescription = state.pet.name,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .border(
                                        2.dp,
                                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f),
                                        CircleShape
                                    ),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(id = if (state.pet?.species == speciesEnum.dog) R.drawable.dog_pp_white else R.drawable.cat_pp_white),
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .border(
                                        2.dp,
                                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f),
                                        CircleShape
                                    ),
                                contentScale = ContentScale.Crop,
                                contentDescription = ""
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth().padding(start = 8.dp, bottom = 0.dp)) {
                    Text(
                        text = "Today's tasks",
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 0.dp),
                    shape = RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomEnd = 0.dp,
                        bottomStart = 0.dp
                    ),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    if (state.tasks.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No tasks planned for today",
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.tasks) { task ->
                                TaskItem(
                                    title = task.title,
                                    type = task.type ?: taskTypeEnum.other,
                                    status = task.status,
                                    onCheckClick = { onTaskDone() }
                                )
                            }
                        }
                    }
                }
                Button(
                    onClick = onViewAllTasks,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(start = 8.dp, end = 8.dp, bottom = 8.dp, top = 0.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomEnd = 12.dp,
                        bottomStart = 12.dp
                    )
                ) {
                    Text(
                        text = "VIEW ALL TASKS",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "TIPS",
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "tu beda jakies tipy o pogodzie czy o czyms tam ale to potem",
                                color = MaterialTheme.colorScheme.secondary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onMedicationHistory,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "MEDICATION HISTORY",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onChatWithVet,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.chat),
                            contentDescription = "Chat with vet",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Chat with ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Text(
                            text = "VetAI",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .shadow(8.dp, CircleShape)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiary)
                        .clickable { onWalkClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "WALK",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}


    @Composable
fun TaskItem(
    title: String,
    type: taskTypeEnum,
    status: taskStatusEnum,
    onCheckClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (status == taskStatusEnum.done) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.secondary)
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
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = type.toString(),
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .clickable { onCheckClick() },
                contentAlignment = Alignment.Center
            ) {
                if (status == taskStatusEnum.done) {
                    Image(
                        painter = painterResource(id = R.drawable.task_done),
                        contentDescription = "Task done",
                        modifier = Modifier.size(20.dp)
                    )
                }
                else {
                    Image(
                        painter = painterResource(id = R.drawable.task_notdone),
                        contentDescription = "Task not done",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PetDashboardScreenPreview() {
    PetCareTheme {
        val samplePet = Pet(
            id = "1",
            ownerUserId = "user1",
            name = "Aslan",
            species = speciesEnum.dog,
            breed = "Golden Retriever",
            sex = sexEnum.male,
            birthDate = LocalDate(2021, 5, 20),
            avatarThumbUrl = null,
            createdAt = LocalDate(2024, 1, 1)
        )

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
                date = TODO()
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
                date = TODO()
            )
        )

        PetDashboardScreen(
            state = PetDashboardState(
                pet = samplePet,
                tasks = sampleTasks,
                isLoading = false
            ),
            onTaskDone = {},
            onViewAllTasks = {},
            onMedicationHistory = {},
            onChatWithVet = {},
            onWalkClick = {}
        )
    }
}

@Preview
@Composable
fun PetDashboardScreenPreview2() {
    PetCareTheme {
        val samplePet = Pet(
            id = "1",
            ownerUserId = "user1",
            name = "Aslan",
            species = speciesEnum.dog,
            breed = "Golden Retriever",
            sex = sexEnum.male,
            birthDate = LocalDate(2021, 5, 20),
            avatarThumbUrl = null,
            createdAt = LocalDate(2024, 1, 1)
        )

        PetDashboardScreen(
            state = PetDashboardState(
                pet = samplePet,
                tasks = emptyList(),
                isLoading = false
            ),
            onTaskDone = {},
            onViewAllTasks = {},
            onMedicationHistory = {},
            onChatWithVet = {},
            onWalkClick = {}
        )
    }
}