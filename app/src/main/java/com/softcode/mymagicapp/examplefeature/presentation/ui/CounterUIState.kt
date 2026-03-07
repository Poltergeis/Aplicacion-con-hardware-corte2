package com.softcode.mymagicapp.examplefeature.presentation.ui

//ya no tienes que hacer StateFlows, solo haz esta clase y modifica el estado compartido de
//BaseViewModel
data class CounterUIState(
    val counter: Int = 0
)