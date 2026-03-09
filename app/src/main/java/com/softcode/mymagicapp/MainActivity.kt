package com.softcode.mymagicapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.softcode.mymagicapp.core.navigation.NavigationApp
import com.softcode.mymagicapp.core.ui.theme.AplicacionMagicTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplicacionMagicTheme {
                NavigationApp()
            }
        }
    }
}