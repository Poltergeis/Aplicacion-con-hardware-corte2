package com.softcode.mymagicapp.authfeature.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.softcode.mymagicapp.authfeature.domain.usecases.LoadLoggedUserUseCase
import com.softcode.mymagicapp.authfeature.domain.usecases.LoginUseCase
import com.softcode.mymagicapp.authfeature.domain.usecases.RegisterUseCase
import com.softcode.mymagicapp.authfeature.presentation.viewmodel.LoginViewModel
import com.softcode.mymagicapp.authfeature.presentation.viewmodel.RegisterViewModel
import com.softcode.mymagicapp.core.di.AppContainer

class AuthModule(private val appContainer: AppContainer) {

    val loginViewModelFactory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            LoginViewModel(
                LoginUseCase(appContainer.authRepository),
                LoadLoggedUserUseCase(appContainer.authRepository)
            ) as T
    }

    val registerViewModelFactory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            RegisterViewModel(RegisterUseCase(appContainer.authRepository)) as T
    }
}
