package com.softcode.mymagicapp.core.ui.base.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

//nunca uses runBlocking para las funciones suspendidas porfavor ( ;_;)
//dentro de los viewmodels puedes llamar a `runAsync` y dentro manejar las funciones
//suspendidas que vendrian siendo los casos de uso y los repositorios de hardware
fun ViewModel.runAsync(
    onError: (Throwable) -> Unit = {},
    block: suspend () -> Unit
) {
    viewModelScope.launch {
        try {
            block()
        } catch (e: Exception) {
            onError(e)
        }
    }
}

