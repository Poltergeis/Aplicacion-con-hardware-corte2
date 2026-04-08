package com.softcode.mymagicapp.exchangefeature.domain.usecases

import com.softcode.mymagicapp.core.domain.repository.ExchangeRepository
import com.softcode.mymagicapp.core.domain.results.OperationResult

class RespondExchangeUseCase(
    private val exchangeRepository: ExchangeRepository
) {
    suspend operator fun invoke(
        exchangeId: Long,
        accept: Boolean
    ): OperationResult<Unit, String, String> {
        val status = if (accept) "ACCEPTED" else "REJECTED"
        return exchangeRepository.respondExchange(exchangeId, status)
    }
}
