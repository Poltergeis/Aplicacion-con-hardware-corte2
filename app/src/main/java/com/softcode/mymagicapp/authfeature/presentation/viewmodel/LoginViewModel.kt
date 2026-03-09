package com.softcode.mymagicapp.authfeature.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.softcode.mymagicapp.authfeature.presentation.ui.LoginEffect
import com.softcode.mymagicapp.authfeature.presentation.ui.LoginUIState
import com.softcode.mymagicapp.core.data.repository.AuthRepository
import com.softcode.mymagicapp.core.data.repository.AuthResult
import com.softcode.mymagicapp.core.ui.base.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel<LoginUIState, LoginEffect>(LoginUIState()) {

    init {
        viewModelScope.launch {
            val userId = authRepository.currentUserId.first()
            if (userId != null) {
                sendEffect(LoginEffect.NavigateToCards)
            }
        }
    }

    fun onNameChanged(name: String) {
        setState { it.copy(name = name, nameError = null) }
    }

    fun onPasswordChanged(password: String) {
        setState { it.copy(password = password, passwordError = null) }
    }

    fun onLoginClicked() {
        val state = _uiState.value
        if (state.name.isBlank()) {
            setState { it.copy(nameError = "El usuario es obligatorio") }
            return
        }
        if (state.password.isBlank()) {
            setState { it.copy(passwordError = "La contraseña es obligatoria") }
            return
        }

        launchWithState(loading = { isLoading -> _uiState.value.copy(isLoading = isLoading) }) {
            when (val result = authRepository.login(state.name.trim(), state.password)) {
                is AuthResult.Success -> sendEffect(LoginEffect.NavigateToCards)
                is AuthResult.Error -> sendEffect(LoginEffect.ShowError(result.message))
            }
        }
    }
}
