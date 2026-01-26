package com.example.petcare.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.petcare.presentation.loading_screen.LoadingScreen
import com.example.petcare.presentation.main.MainContainer
import com.example.petcare.presentation.sign_in.SignInRoute
import com.example.petcare.presentation.sign_up.SignUpRoute
import com.example.petcare.presentation.theme.PetCareTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PetCareTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

object Routes{
    const val LOADING_SCREEN = "loading_screen"
    const val SIGN_UP_SCREEN = "sign_up_screen"
    const val SIGN_IN_SCREEN = "sign_in_screen"
    const val MAIN_APP = "main_app"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Routes.LOADING_SCREEN
    ) {
        composable(route = Routes.LOADING_SCREEN) {
            LoadingScreen(
                onTimeout = {
                    navController.navigate(Routes.SIGN_IN_SCREEN) {
                        popUpTo(Routes.LOADING_SCREEN) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(route = Routes.SIGN_UP_SCREEN) {
            SignUpRoute(
                onNavigateToSignIn = {
                    navController.navigate(Routes.SIGN_IN_SCREEN)
                }
            )
        }
        composable(route = Routes.SIGN_IN_SCREEN) {
            SignInRoute(
                onNavigateToSignUp = {
                    navController.navigate(Routes.SIGN_UP_SCREEN)
                },
                onNavigateToMyPets = {
                    navController.navigate(Routes.MAIN_APP) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(route = Routes.MAIN_APP) {
            MainContainer(
                onNavigateToLogin = {
                    navController.navigate(Routes.SIGN_IN_SCREEN) {
                        popUpTo(Routes.MAIN_APP) { inclusive = true }
                    }
                }
            )
        }
    }
}

