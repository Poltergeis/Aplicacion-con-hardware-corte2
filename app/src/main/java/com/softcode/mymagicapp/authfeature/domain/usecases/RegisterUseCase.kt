package com.softcode.mymagicapp.authfeature.domain.usecases

import com.softcode.mymagicapp.core.domain.repository.AuthRepository

class RegisterUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(username: String, password: String) =
        repository.register(username, password)
}