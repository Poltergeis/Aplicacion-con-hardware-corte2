package com.softcode.mymagicapp.core.ui.base.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

//intenta extender los viewmodels de esta clase en lugar de la base ViewModel
//tiene la utilidad de reducir el codigo de estados, ve el ejemplo CounterViewModel
abstract class BaseViewModel<S, E>(
    initialState: S
) : ViewModel() {

    protected val _uiState = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<E>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val effect = _effect.asSharedFlow()

    protected fun setState(reducer: (S) -> S) {
        _uiState.update(reducer)
    }

    protected fun sendEffect(effect: E) {
        _effect.tryEmit(effect)
    }

    protected fun <T> launchWithState(
        loading: (Boolean) -> S,
        block: suspend () -> T
    ) {
        viewModelScope.launch {
            setState { loading(true) }

            try {
                block()
            } finally {
                setState { loading(false) }
            }
        }
    }
}