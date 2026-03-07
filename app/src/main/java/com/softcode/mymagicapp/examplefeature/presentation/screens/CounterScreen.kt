package com.softcode.mymagicapp.examplefeature.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.softcode.mymagicapp.examplefeature.presentation.ui.CounterEffect
import com.softcode.mymagicapp.examplefeature.presentation.viewmodel.CounterViewModel

//te recomiendo usar innerPadding recogido desde Scaffold para agregar margenes y no consumir toda la pantalla
//esto evita que la vista abarque por encima de la camara por ejemplo
@Composable
fun CounterScreen(viewModel: CounterViewModel = hiltViewModel(), innerPadding: PaddingValues) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier.padding(innerPadding)
    ) {

        Text(text = "Count: ${state.counter}")

        Button(onClick = { viewModel.increment() }) {
            Text("Increment")
        }

        Button(onClick = { viewModel.decrement() }) {
            Text("Decrement")
        }

        Button(onClick = { viewModel.reset() }) {
            Text("Reset")
        }

        LaunchedEffect(Unit) {
            viewModel.effect.collect { effect ->
                when(effect) {
                    is CounterEffect.ShowMessage -> {
                        Toast.makeText(
                            context,
                            effect.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}