package com.softcode.mymagicapp.cardsfeature.domain.usecases

import com.softcode.mymagicapp.core.domain.entities.CardEntity
import com.softcode.mymagicapp.core.domain.repository.CardRepository
import com.softcode.mymagicapp.core.domain.results.OperationResult

class DeleteCardUseCase constructor(
    private val cardRepository: CardRepository
) {
    suspend operator fun invoke(card: CardEntity): OperationResult<Unit, String, String> =
        cardRepository.deleteCard(card)
}