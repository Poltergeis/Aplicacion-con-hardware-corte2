package com.softcode.mymagicapp.authfeature.presentation.ui

sealed class RegisterEffect {
    data object NavigateToCards : RegisterEffect()
    data class ShowError(val message: String) : RegisterEffect()
}
