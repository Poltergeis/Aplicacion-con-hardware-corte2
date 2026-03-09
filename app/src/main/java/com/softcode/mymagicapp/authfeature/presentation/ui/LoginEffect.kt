package com.softcode.mymagicapp.authfeature.presentation.ui

sealed class LoginEffect {
    data object NavigateToCards : LoginEffect()
    data class ShowError(val message: String) : LoginEffect()
}
