package com.softcode.mymagicapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.softcode.mymagicapp.authfeature.di.AuthModule
import com.softcode.mymagicapp.cardsfeature.di.CardsModule
import com.softcode.mymagicapp.core.di.AppContainer
import com.softcode.mymagicapp.core.navigation.NavigationApp
import com.softcode.mymagicapp.core.ui.theme.AplicacionMagicTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appContainer = AppContainer()
        val authModule = AuthModule(appContainer)
        val cardsModule = CardsModule(appContainer)

        setContent {
            AplicacionMagicTheme {
                NavigationApp(
                    authModule = authModule,
                    cardsModule = cardsModule
                )
            }
        }
    }
}
