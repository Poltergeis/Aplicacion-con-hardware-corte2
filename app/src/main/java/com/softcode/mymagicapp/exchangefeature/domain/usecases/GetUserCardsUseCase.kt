package com.softcode.mymagicapp.exchangefeature.domain.usecases

import com.softcode.mymagicapp.core.domain.entities.CardEntity
import com.softcode.mymagicapp.core.domain.repository.CardRepository
import com.softcode.mymagicapp.core.domain.results.OperationResult

class GetUserCardsUseCase(
    private val cardRepository: CardRepository
) {
    suspend operator fun invoke(userId: Long): OperationResult<List<CardEntity>, String, String> {
        return cardRepository.getCardsByUser(userId)
    }
}
