package com.softcode.mymagicapp.cardsfeature.presentation.ui

sealed class CardsEffect {
    data object NavigateToLogin : CardsEffect()
    data class ShowMessage(val message: String) : CardsEffect()
}
