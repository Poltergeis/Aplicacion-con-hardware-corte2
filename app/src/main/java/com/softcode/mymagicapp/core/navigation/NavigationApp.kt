package com.softcode.mymagicapp.core.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.softcode.mymagicapp.authfeature.di.AuthModule
import com.softcode.mymagicapp.authfeature.presentation.screens.LoginScreen
import com.softcode.mymagicapp.authfeature.presentation.screens.RegisterScreen
import com.softcode.mymagicapp.cardsfeature.di.CardsModule
import com.softcode.mymagicapp.cardsfeature.presentation.screens.CardsScreen
import com.softcode.mymagicapp.cardsfeature.presentation.viewmodel.CardsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationApp(
    authModule: AuthModule,
    cardsModule: CardsModule
) {
    val cardsViewModel: CardsViewModel = viewModel(factory = cardsModule.cardsViewModelFactory)

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDest = navBackStackEntry?.destination

    val isAuthRoute = currentDest?.hasRoute<LoginRoute>() == true ||
            currentDest?.hasRoute<RegisterRoute>() == true
    val isCardsRoute = currentDest?.hasRoute<CardsRoute>() == true

    val cardsState by cardsViewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (!isAuthRoute) {
                TopAppBar(
                    title = {
                        if (isCardsRoute) {
                            Text(
                                text = "Hola, ${cardsState.userName}",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    },
                    actions = {
                        if (isCardsRoute) {
                            IconButton(onClick = cardsViewModel::onLogout) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ExitToApp,
                                    contentDescription = "Cerrar sesión"
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = LoginRoute,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable<LoginRoute> {
                LoginScreen(
                    factory = authModule.loginViewModelFactory,
                    onNavigateToRegister = { navController.navigate(RegisterRoute) },
                    onLoginSuccess = {
                        navController.navigate(CardsRoute) {
                            popUpTo(LoginRoute) { inclusive = true }
                        }
                    }
                )
            }

            composable<RegisterRoute> {
                RegisterScreen(
                    factory = authModule.registerViewModelFactory,
                    onNavigateToLogin = { navController.popBackStack() },
                    onRegisterSuccess = {
                        navController.navigate(CardsRoute) {
                            popUpTo(LoginRoute) { inclusive = true }
                        }
                    }
                )
            }

            composable<CardsRoute> {
                CardsScreen(
                    viewModel = cardsViewModel,
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
