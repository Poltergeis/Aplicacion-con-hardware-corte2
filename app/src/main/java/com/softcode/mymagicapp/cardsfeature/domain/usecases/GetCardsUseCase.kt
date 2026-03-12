package com.softcode.mymagicapp.cardsfeature.domain.usecases

import com.softcode.mymagicapp.core.domain.entities.CardEntity
import com.softcode.mymagicapp.core.domain.repository.CardRepository
import com.softcode.mymagicapp.core.domain.results.OperationResult
import kotlinx.coroutines.flow.StateFlow

class GetCardsUseCase constructor(
    private val cardRepository: CardRepository
) {
    val cards: StateFlow<List<CardEntity>> = cardRepository.cards

    suspend operator fun invoke(): OperationResult<Unit, String, String> =
        cardRepository.getCards()
}