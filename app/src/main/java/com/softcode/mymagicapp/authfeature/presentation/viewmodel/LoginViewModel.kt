package com.softcode.mymagicapp.authfeature.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.softcode.mymagicapp.authfeature.domain.usecases.LoadLoggedUserUseCase
import com.softcode.mymagicapp.authfeature.domain.usecases.LoginUseCase
import com.softcode.mymagicapp.authfeature.presentation.ui.LoginEffect
import com.softcode.mymagicapp.authfeature.presentation.ui.LoginUIState
import com.softcode.mymagicapp.core.domain.results.AuthResult
import com.softcode.mymagicapp.core.ui.base.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val loadLoggedUserUseCase: LoadLoggedUserUseCase
) : BaseViewModel<LoginUIState, LoginEffect>(LoginUIState()) {

    init {
        viewModelScope.launch {
            val result = loadLoggedUserUseCase()
            if(result is AuthResult.Success) {
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
            when(val result = loginUseCase(state.name.trim(), state.password)) {
                is AuthResult.Success -> sendEffect(LoginEffect.NavigateToCards)
                is AuthResult.Error -> sendEffect(LoginEffect.ShowError(result.message))
            }
        }
    }
}
