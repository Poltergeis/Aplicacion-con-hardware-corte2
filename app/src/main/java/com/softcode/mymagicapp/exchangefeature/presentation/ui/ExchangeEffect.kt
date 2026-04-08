package com.softcode.mymagicapp.exchangefeature.presentation.ui

sealed class ExchangeEffect {
    data class ShowMessage(val message: String) : ExchangeEffect()
    data object ExchangeCreated : ExchangeEffect()
}
