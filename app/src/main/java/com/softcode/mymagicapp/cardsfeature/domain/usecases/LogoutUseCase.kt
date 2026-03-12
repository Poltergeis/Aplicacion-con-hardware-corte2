package com.softcode.mymagicapp.cardsfeature.domain.usecases

import com.softcode.mymagicapp.core.domain.repository.AuthRepository
import com.softcode.mymagicapp.core.domain.results.AuthResult

class LogoutUseCase constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): AuthResult =
        authRepository.logout()
}