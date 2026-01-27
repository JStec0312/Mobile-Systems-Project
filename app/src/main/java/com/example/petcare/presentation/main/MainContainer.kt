package com.example.petcare.presentation.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.petcare.R
import com.example.petcare.presentation.about.AboutScreen
import com.example.petcare.presentation.add_pet.AddPetRoute
import com.example.petcare.presentation.add_task.AddTaskRoute
import com.example.petcare.presentation.ai_chat.AIChatRoute
import com.example.petcare.presentation.all_tasks.AllTasksRoute
import com.example.petcare.presentation.all_tasks.AllTasksViewModel
import com.example.petcare.presentation.calendar.CalendarRoute
import com.example.petcare.presentation.dashboard.PetDashboardRoute
import com.example.petcare.presentation.edit_pet.EditPetRoute
import com.example.petcare.presentation.edit_task.EditTaskRoute
import com.example.petcare.presentation.help.HelpScreen
import com.example.petcare.presentation.medication.MedicationHistoryRoute
import com.example.petcare.presentation.medication_details.MedicationDetailsRoute
import com.example.petcare.presentation.my_pets.MyPetsRoute
import com.example.petcare.presentation.my_pets.MyPetsViewModel
import com.example.petcare.presentation.settings.SettingsRoute
import com.example.petcare.presentation.task_details.TaskDetailsRoute
import com.example.petcare.presentation.walk.WalkRoute
import com.example.petcare.presentation.walk_history.WalkHistoryRoute
import com.example.petcare.presentation.walk_stats.WalkStatsRoute
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContainer(
    viewModel: MainViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit
) {
    val mainNavController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.logoutEvent.collect {
            scope.launch { drawerState.close() }
            onNavigateToLogin()
        }
    }

    val navBackStackEntry by mainNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val topBarTitle = when {
        currentRoute == "my_pets" -> "MY PETS"
        currentRoute == "add_pet" -> "NEW PET"
        currentRoute?.startsWith("edit_pet") == true  -> "EDIT PET"
        currentRoute?.startsWith("dashboard") == true -> "DASHBOARD"
        currentRoute?.startsWith("walk") == true -> "WALK TRACKER"
        currentRoute?.startsWith("all_tasks") == true -> "TASKS"
        currentRoute == "help" -> "HELP"
        currentRoute == "about" -> "ABOUT"
        currentRoute?.startsWith("add_task") == true -> "NEW TASK"
        currentRoute?.startsWith("task_details") == true -> "TASK DETAILS"
        currentRoute?.startsWith("edit_task") == true -> "EDIT TASK"
        currentRoute == "calendar" -> "CALENDAR"
        currentRoute == "medication_history" -> "MEDICATION"
        currentRoute == "add_medication" -> "ADD MEDICATION"
        currentRoute == "ai_chat" -> "VET AI"
        currentRoute == "settings" -> "SETTINGS"
        currentRoute?.startsWith("walk_stats") == true -> "WALK STATS"
        currentRoute?.startsWith("walk_history") == true -> "WALK HISTORY"
        else -> ""
    }

    val isCloseableScreen = when {
        currentRoute == "medication_history" -> true
        currentRoute == "ai_chat" -> true
        currentRoute?.startsWith("all_tasks") == true -> true
        currentRoute == "privacy_policy" -> true
        else -> false
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet (
                drawerContainerColor = Color.White,
                drawerContentColor = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.width(300.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                ) {
                    IconButton(
                        onClick = { scope.launch { drawerState.close() }},
                        modifier = Modifier.size(48.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.menu),
                            contentDescription = "Close Menu",
                            modifier = Modifier.size(38.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))

                    @Composable
                    fun DrawerItem(label: String, icon: Int, route: String, onClickOverride: (() -> Unit)? = null) {
                        NavigationDrawerItem(
                            label = {
                                Text(
                                    text = label,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            icon = {
                                Image(
                                    painter = painterResource(id = icon),
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp)
                                )
                            },
                            selected = currentRoute == route,
                            onClick = {
                                if(onClickOverride != null) {onClickOverride()}
                                else {
                                    mainNavController.navigate(route)
                                }
                                scope.launch { drawerState.close() }
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                                unselectedContainerColor = Color.Transparent
                            ),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        HorizontalDivider(color = Color(0xFFEBE6FF))
                    }
                    DrawerItem("My Pets", R.drawable.paw, "my_pets")
                    DrawerItem("Calendar", R.drawable.calendar, "calendar")
                    DrawerItem("Settings", R.drawable.settings, "settings")
                    DrawerItem("Help", R.drawable.info, "help")
                    DrawerItem("About", R.drawable.about, "about")
                    DrawerItem(
                        label ="Log out",
                        icon = R.drawable.logout,
                        route = "logout",
                        onClickOverride = {viewModel.onLogout()}
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Image(
                            painter = painterResource(id = R.drawable.petcare_logo_purple),
                            contentDescription = "Logo",
                            modifier = Modifier.size(150.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        // ZMIANA: Zamiast Text używamy AutoResizedText
                        AutoResizedText(
                            text = topBarTitle,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(top = 16.dp, start = 8.dp, end = 8.dp)
                        )
                    },
                    navigationIcon = {
                        if (isCloseableScreen) {
                            IconButton(
                                onClick = { mainNavController.popBackStack() },
                                modifier = Modifier
                                    .size(58.dp)
                                    .padding(start = 14.dp, top = 20.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.cross),
                                    contentDescription = "Close",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        } else {
                            IconButton(
                                onClick = { scope.launch { drawerState.open() } },
                                modifier = Modifier
                                    .size(58.dp)
                                    .padding(start = 14.dp, top = 20.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.menu),
                                    contentDescription = "Open Menu"
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier.height(100.dp)
                )
            }
        ) {
                innerPadding ->
            NavHost(
                navController = mainNavController,
                startDestination = "my_pets",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("my_pets") {
                    val savedStateHandle = mainNavController.currentBackStackEntry?.savedStateHandle
                    val shouldRefresh by savedStateHandle?.getLiveData<Boolean>("should_refresh_pets")
                        ?.observeAsState(initial = false) ?: remember { mutableStateOf(false) }
                    val myPetsViewModel: MyPetsViewModel = hiltViewModel()

                    LaunchedEffect(shouldRefresh) {
                        if(shouldRefresh) myPetsViewModel.getPets()
                    }

                    MyPetsRoute(
                        onNavigateToPetDetails = { petId -> mainNavController.navigate("dashboard/$petId") },
                        onNavigateToAddPet = { mainNavController.navigate("add_pet") },
                        onNavigateToEditPet = { petId -> mainNavController.navigate("edit_pet/$petId") }
                    )
                }
                composable("add_pet") {
                    AddPetRoute(
                        onNavigationToMyPets = {
                            mainNavController.previousBackStackEntry?.savedStateHandle?.set("should_refresh_pets", true)
                            mainNavController.popBackStack()
                        }
                    )
                }
                composable("edit_pet/{petId}", arguments = listOf(navArgument("petId") {type = NavType.StringType})) {
                    EditPetRoute(
                        onNavigateBack = {
                            mainNavController.previousBackStackEntry?.savedStateHandle?.set("should_refresh_pets", true)
                            mainNavController.popBackStack()
                        }
                    )
                }
                composable("dashboard/{petId}", arguments = listOf(navArgument("petId") {type = NavType.StringType})) { backStackEntry ->
                    val petId = backStackEntry.arguments?.getString("petId") ?: ""
                    PetDashboardRoute(
                        onNavigateToTasks = { mainNavController.navigate("all_tasks/$petId") },
                        onNavigateToMedicationHistory = { mainNavController.navigate("medication_history") },
                        onNavigateToChat = { mainNavController.navigate("ai_chat") },
                        onNavigateToWalk = { mainNavController.navigate("walk/$petId") }
                    )
                }
                composable("walk/{petId}", arguments = listOf(navArgument("petId") {type = NavType.StringType})) { backStackEntry ->
                    val petId = backStackEntry.arguments?.getString("petId") ?: ""
                    WalkRoute(
                        onNavigateToStats = { mainNavController.navigate("walk_stats/$petId") },
                        onStopClick = { mainNavController.popBackStack() }
                    )
                }
                composable("all_tasks/{petId}") { backStackEntry ->
                    val petId = backStackEntry.arguments?.getString("petId") ?: ""
                    val savedStateHandle = mainNavController.currentBackStackEntry?.savedStateHandle
                    val shouldRefresh by savedStateHandle?.getLiveData<Boolean>("should_refresh_tasks")
                        ?.observeAsState(initial = false) ?: remember { mutableStateOf(false) }
                    val allTasksViewModel: AllTasksViewModel = hiltViewModel()

                    LaunchedEffect(shouldRefresh) {
                        if(shouldRefresh) {
                            allTasksViewModel.loadTasks()
                            savedStateHandle?.set("should_refresh_tasks", false)
                        }
                    }

                    AllTasksRoute(
                        viewModel = allTasksViewModel,
                        onAddTaskClick = { mainNavController.navigate("add_task/$petId") },
                        onNavigateToTaskDetails = { taskId -> mainNavController.navigate("task_details/$taskId") },
                        onNavigateToEditTask = { taskId -> mainNavController.navigate("edit_task/$petId/$taskId") }
                    )
                }
                composable("add_task/{petId}", arguments = listOf(navArgument("petId") {type = NavType.StringType})) {
                    AddTaskRoute(
                        onNavigateBack = {
                            mainNavController.previousBackStackEntry?.savedStateHandle?.set("should_refresh_tasks", true)
                            mainNavController.popBackStack()
                        }
                    )
                }
                composable("task_details/{taskId}", arguments = listOf(navArgument("taskId") {type = NavType.StringType})) {
                    TaskDetailsRoute(onNavigateBack = { mainNavController.popBackStack() })
                }
                composable("edit_task/{petId}/{taskId}", arguments = listOf(navArgument("taskId") {type = NavType.StringType}, navArgument("petId") {type = NavType.StringType})) {
                    EditTaskRoute(
                        onNavigateBack = {
                            mainNavController.previousBackStackEntry?.savedStateHandle?.set("should_refresh_tasks", true)
                            mainNavController.popBackStack()
                        }
                    )
                }
                composable("calendar") {
                    CalendarRoute(
                        onNavigateToAddTask = { mainNavController.navigate("add_task/no_id") },
                        onNavigateToTaskDetails = { taskId -> mainNavController.navigate("task_details/$taskId") },
                        onNavigateToEditTask = { task -> mainNavController.navigate("edit_task/${task.petId}/${task.id}") }
                    )
                }
                composable("profile") { Text("PROFILE") }
                composable("dashboard") { Text("DASHBOARD") }
                composable("help") { HelpScreen() }
                composable("about") { AboutScreen(onPrivacyPolicyClick = {
                    mainNavController.navigate("privacy_policy")
                }) }
                composable("medication_history") {
                    MedicationHistoryRoute(
                        onAddMedicationClick = { mainNavController.navigate("add_medication") },
                        onNavigateToDetails = { medicationId -> mainNavController.navigate("medication_details/$medicationId") },
                        onNavigateToEdit = { medicationId -> mainNavController.navigate("medication_edit/$medicationId") }
                    )
                }
                composable("add_medication") {
                    com.example.petcare.presentation.add_medication.AddMedicationRoute(onNavigateBack = { mainNavController.popBackStack() })
                }
                composable("medication_details/{medicationId}", arguments = listOf(navArgument("medicationId") { type = NavType.StringType })) {
                    MedicationDetailsRoute(onNavigateBack = { mainNavController.popBackStack() })
                }
                composable("medication_edit/{medicationId}", arguments = listOf(navArgument("medicationId") { type = NavType.StringType })) {
                    com.example.petcare.presentation.edit_medication.EditMedicationRoute(onNavigateBack = { mainNavController.popBackStack() })
                }
                composable("ai_chat") {
                    AIChatRoute()
                }
                composable("settings") {
                    SettingsRoute()
                }
                composable("walk_stats/{petId}", arguments = listOf(navArgument("petId") {type = NavType.StringType})) { navBackStackEntry ->
                    val petId = navBackStackEntry.arguments?.getString("petId") ?: ""
                    WalkStatsRoute(onNavigateToHistory = { mainNavController.navigate("walk_history/$petId")})
                }
                composable("walk_history/{petId}", arguments = listOf(navArgument("petId") {type = NavType.StringType})) {
                    WalkHistoryRoute(onNavigateBack = { mainNavController.popBackStack() })
                }
                composable("privacy_policy") {
                    com.example.petcare.presentation.about.PrivacyPolicyScreen(
                        onNavigateBack = {
                            mainNavController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AutoResizedText(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    targetFontSize: TextUnit = 36.sp
) {
    var textSize by remember(text) { mutableStateOf(targetFontSize) }
    var readyToDraw by remember(text) { mutableStateOf(false) }

    Text(
        text = text,
        color = color,
        fontSize = textSize,
        fontWeight = FontWeight.Black,
        maxLines = 1,
        softWrap = false,
        textAlign = TextAlign.Center,
        overflow = TextOverflow.Clip,
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.didOverflowWidth) {
                // Jeśli tekst się nie mieści (wylewa poza szerokość), zmniejszamy czcionkę o 10%
                textSize *= 0.9f
            } else {
                readyToDraw = true
            }
        },
        modifier = modifier.drawWithContent {
            if (readyToDraw) {
                drawContent()
            }
        }
    )
}