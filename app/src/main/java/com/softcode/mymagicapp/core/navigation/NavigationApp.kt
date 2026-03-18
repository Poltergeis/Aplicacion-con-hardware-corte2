package com.softcode.mymagicapp.core.navigation

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.view.WindowManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.softcode.mymagicapp.authfeature.presentation.screens.LoginScreen
import com.softcode.mymagicapp.authfeature.presentation.screens.RegisterScreen
import com.softcode.mymagicapp.cardsfeature.presentation.screens.CardsScreen
import com.softcode.mymagicapp.cardsfeature.presentation.viewmodel.CardsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationApp(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    navSharedViewModel: NavigationSharedViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDest = navBackStackEntry?.destination

    val isAuthRoute = currentDest?.hasRoute<LoginRoute>() == true ||
            currentDest?.hasRoute<RegisterRoute>() == true
    val isCardsRoute = currentDest?.hasRoute<CardsRoute>() == true

    val userName by navSharedViewModel.userName.collectAsState()
    val isFlashOn by settingsViewModel.isFlashOn.collectAsState()
    var showSettingsDialog by remember { mutableStateOf(false) }

    // Callback registrado desde el bloque composable<CardsRoute>
    var onLogoutAction: (() -> Unit)? by remember { mutableStateOf(null) }

    // Cierra el diálogo y apaga el flash al navegar a rutas de autenticación
    LaunchedEffect(isAuthRoute) {
        if (isAuthRoute) {
            settingsViewModel.turnOffFlash()
            showSettingsDialog = false
        }
    }

    // Gestión del brillo de pantalla para CardsRoute
    val view = LocalView.current
    DisposableEffect(isCardsRoute) {
        val window = (view.context as Activity).window
        if (isCardsRoute) {
            val params = window.attributes
            params.screenBrightness = 1.0f
            window.attributes = params
        }
        onDispose {
            val params = window.attributes
            params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
            window.attributes = params
        }
    }

    // Apagar el flash al pasar la app a segundo plano
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                settingsViewModel.turnOffFlash()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Lanzador de permiso de cámara para la linterna
    val context = LocalContext.current
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) settingsViewModel.setFlash(true)
    }

    fun requestFlashOn() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            settingsViewModel.setFlash(true)
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (!isAuthRoute) {
                TopAppBar(
                    title = {
                        if (isCardsRoute) {
                            Text(
                                text = "Hola, $userName",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    },
                    actions = {
                        if (isCardsRoute) {
                            IconButton(onClick = { onLogoutAction?.invoke() }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ExitToApp,
                                    contentDescription = "Cerrar sesión"
                                )
                            }
                        }
                        IconButton(onClick = { showSettingsDialog = true }) {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = "Configuración"
                            )
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
                    onNavigateToLogin = { navController.popBackStack() },
                    onRegisterSuccess = {
                        navController.navigate(CardsRoute) {
                            popUpTo(LoginRoute) { inclusive = true }
                        }
                    }
                )
            }

            composable<CardsRoute> {
                val cardsViewModel: CardsViewModel = hiltViewModel()

                DisposableEffect(cardsViewModel) {
                    onLogoutAction = cardsViewModel::onLogout
                    onDispose { onLogoutAction = null }
                }

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

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = {
                settingsViewModel.turnOffFlash()
                showSettingsDialog = false
            },
            title = { Text("Configuración") },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Linterna")
                    Switch(
                        checked = isFlashOn,
                        onCheckedChange = { enabled ->
                            if (enabled) requestFlashOn() else settingsViewModel.setFlash(false)
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        settingsViewModel.turnOffFlash()
                        showSettingsDialog = false
                    }
                ) {
                    Text("Cerrar")
                }
            }
        )
    }
}
