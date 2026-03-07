package com.softcode.mymagicapp.examplefeature.presentation.ui

//esto es para eventos, es decir, algo que no necesita persistir,
//ejemplo: prueba el boton de reset de mi ejemplo
sealed class CounterEffect {
    data class ShowMessage(val message: String) : CounterEffect()
}