package com.softcode.mymagicapp.cardsfeature.domain.usecases

import com.softcode.mymagicapp.core.domain.entities.CardEntity
import com.softcode.mymagicapp.core.domain.repository.AuthRepository
import com.softcode.mymagicapp.core.domain.repository.CardRepository
import com.softcode.mymagicapp.core.domain.results.OperationResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest

class GetCardsUseCase(
    private val cardRepository: CardRepository,
    private val authRepository: AuthRepository
) {
    /** Reactive stream from Room — emits immediately then on every local change. */
    val cards: Flow<List<CardEntity>> = authRepository.user
        .filterNotNull()
        .flatMapLatest { user -> cardRepository.getCardsFlow(user.id) }

    /** Syncs from the server into Room (SSOT). */
    suspend operator fun invoke(): OperationResult<Unit, String, String> {
        val userId = authRepository.user.value?.id
            ?: return OperationResult.Failure("No hay sesión activa")
        return cardRepository.syncCards(userId)
    }
}
