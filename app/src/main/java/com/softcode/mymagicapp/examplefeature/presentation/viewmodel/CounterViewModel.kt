package com.softcode.mymagicapp.examplefeature.presentation.viewmodel

import com.softcode.mymagicapp.core.ui.base.viewmodel.BaseViewModel
import com.softcode.mymagicapp.examplefeature.presentation.ui.CounterEffect
import com.softcode.mymagicapp.examplefeature.presentation.ui.CounterUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

//intenta usar BaseViewModel, se ahorra codigo en manejo de estados, y tambien tiene los efectos
@HiltViewModel
class CounterViewModel @Inject constructor() :
    BaseViewModel<CounterUIState, CounterEffect>(CounterUIState()) {
    fun increment() {
        //esto viene de BaseViewModel
        setState { state ->
            state.copy(counter = state.counter + 1)
        }
    }

    fun decrement() {
        //esto viene de BaseViewModel
        setState { state ->
            state.copy(counter = state.counter - 1)
        }
    }

    fun reset() {
        //esto viene de BaseViewModel
        setState { state -> state.copy(counter = 0) }
        //esto viene de BaseViewModel
        sendEffect(CounterEffect.ShowMessage("contador reiniciado"))
    }
}