package com.softcode.mymagicapp.cardsfeature.domain.usecases

import com.softcode.mymagicapp.core.domain.repository.AuthRepository
import com.softcode.mymagicapp.core.domain.repository.CardRepository
import com.softcode.mymagicapp.core.domain.results.OperationResult

class AddCardUseCase(
    private val cardRepository: CardRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        title: String,
        description: String
    ): OperationResult<Unit, String, String> {
        val userId = authRepository.user.value?.id
            ?: return OperationResult.Failure("No hay sesión activa")
        return cardRepository.addCard(userId, title, description)
    }
}
