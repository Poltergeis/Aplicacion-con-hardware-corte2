package com.softcode.mymagicapp.authfeature.presentation.viewmodel

import com.softcode.mymagicapp.authfeature.presentation.ui.RegisterEffect
import com.softcode.mymagicapp.authfeature.presentation.ui.RegisterUIState
import com.softcode.mymagicapp.core.data.repository.AuthRepository
import com.softcode.mymagicapp.core.data.repository.AuthResult
import com.softcode.mymagicapp.core.ui.base.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel<RegisterUIState, RegisterEffect>(RegisterUIState()) {

    fun onNameChanged(name: String) {
        setState { it.copy(name = name, nameError = null) }
    }

    fun onPasswordChanged(password: String) {
        setState { it.copy(password = password, passwordError = null) }
    }

    fun onRegisterClicked() {
        val state = _uiState.value
        var hasError = false

        if (state.name.isBlank()) {
            setState { it.copy(nameError = "El usuario es obligatorio") }
            hasError = true
        }
        if (state.password.length < 6) {
            setState { it.copy(passwordError = "Mínimo 6 caracteres") }
            hasError = true
        }
        if (hasError) return

        launchWithState(loading = { isLoading -> _uiState.value.copy(isLoading = isLoading) }) {
            when (val result = authRepository.register(state.name.trim(), state.password)) {
                is AuthResult.Success -> sendEffect(RegisterEffect.NavigateToCards)
                is AuthResult.Error -> sendEffect(RegisterEffect.ShowError(result.message))
            }
        }
    }
}
