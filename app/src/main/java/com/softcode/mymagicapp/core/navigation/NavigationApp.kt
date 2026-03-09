package com.softcode.mymagicapp.core.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.softcode.mymagicapp.authfeature.presentation.screens.LoginScreen
import com.softcode.mymagicapp.authfeature.presentation.screens.RegisterScreen
import com.softcode.mymagicapp.cardsfeature.presentation.screens.CardsScreen

@Composable
fun NavigationApp() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = LoginRoute) {

        composable<LoginRoute> {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                LoginScreen(
                    innerPadding = innerPadding,
                    onNavigateToRegister = {
                        navController.navigate(RegisterRoute)
                    },
                    onLoginSuccess = {
                        navController.navigate(CardsRoute) {
                            popUpTo(LoginRoute) { inclusive = true }
                        }
                    }
                )
            }
        }

        composable<RegisterRoute> {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                RegisterScreen(
                    innerPadding = innerPadding,
                    onNavigateToLogin = {
                        navController.popBackStack()
                    },
                    onRegisterSuccess = {
                        navController.navigate(CardsRoute) {
                            popUpTo(LoginRoute) { inclusive = true }
                        }
                    }
                )
            }
        }

        composable<CardsRoute> {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                CardsScreen(
                    innerPadding = innerPadding,
                    onLogout = {
                        navController.navigate(LoginRoute) {
                            popUpTo(CardsRoute) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
