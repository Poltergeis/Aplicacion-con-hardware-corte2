package com.softcode.mymagicapp.authfeature.presentation.ui

data class RegisterUIState(
    val name: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val nameError: String? = null,
    val passwordError: String? = null
)
