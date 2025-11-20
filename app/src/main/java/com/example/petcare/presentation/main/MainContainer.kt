package com.example.petcare.presentation.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.petcare.R
import com.example.petcare.presentation.my_pets.MyPetsRoute
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContainer() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val topBarTitle = when (currentRoute) {
        "my_pets" -> "MY PETS"
        else -> ""
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
                    fun DrawerItem(label: String, icon: Int, route: String) {
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
                                navController.navigate(route)
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
                    DrawerItem("My Profile", R.drawable.profile, "profile")
                    DrawerItem("Calendar", R.drawable.calendar, "calendar")
                    DrawerItem("Settings", R.drawable.settings, "settings")
                    DrawerItem("Help", R.drawable.info, "help")
                    DrawerItem("About", R.drawable.about, "about")

                    Spacer(modifier = Modifier.weight(1f))

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                painter = painterResource(id = R.drawable.petcare_logo_purple),
                                contentDescription = "Logo",
                                modifier = Modifier.size(150.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {Text(topBarTitle, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.secondary, fontSize = 36.sp, modifier = Modifier.padding(top = 16.dp))},
                    navigationIcon = {
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
                navController = navController,
                startDestination = "my_pets",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("my_pets") {
                    MyPetsRoute(
                        onNavigateToPetDetails = { petId ->
                            println("Kliknieto szczegoly zwierzaka o ID: $petId")
                        },
                        onNavigateToAddPet = {
                            println("Kliknięto przycisk Dodaj Zwierze")
                        },
                        onNavigateToEditPet = {
                            println("Kliknięto przycisk Edytuj Zwierze")
                        }
                    )
                }
                composable("dashboard") {
                    Text("DASHBOARD")
                }
            }
        }
    }
}