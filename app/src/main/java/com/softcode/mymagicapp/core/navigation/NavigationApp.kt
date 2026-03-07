package com.softcode.mymagicapp.core.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.softcode.mymagicapp.examplefeature.presentation.screens.CounterScreen

//añade las rutas aqui, y nunca les pases el NavController directamente, en su lugar haz esto:
//MiComposable(ToHome = { navController.navigate(Home) })
@Composable
fun NavigationApp(){
    val navController = rememberNavController()
    NavHost(navController, startDestination = example) {
        composable<example> {
            Scaffold(
                modifier = Modifier.fillMaxSize()
            ) {innerPadding ->
                CounterScreen(innerPadding = innerPadding)
            }
        }
    }
}