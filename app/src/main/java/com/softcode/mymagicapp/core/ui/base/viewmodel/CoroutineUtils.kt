package com.softcode.mymagicapp.core.ui.base.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

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

