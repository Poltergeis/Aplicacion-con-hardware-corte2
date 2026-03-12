package com.softcode.mymagicapp.authfeature.domain.usecases

import com.softcode.mymagicapp.core.domain.results.AuthResult
import com.softcode.mymagicapp.core.domain.repository.AuthRepository

class LoadLoggedUserUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): AuthResult {
        val currentUser = repository.user.value
        if(currentUser != null) {
            return AuthResult.Success()
        } else {
            return AuthResult.Error("no user is logged yet")
        }
    }
}