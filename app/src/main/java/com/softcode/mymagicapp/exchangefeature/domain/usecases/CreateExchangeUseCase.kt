package com.softcode.mymagicapp.exchangefeature.domain.usecases

import com.softcode.mymagicapp.core.domain.repository.AuthRepository
import com.softcode.mymagicapp.core.domain.repository.ExchangeRepository
import com.softcode.mymagicapp.core.domain.results.OperationResult

class CreateExchangeUseCase(
    private val exchangeRepository: ExchangeRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        receiverId: Long,
        proposerCardId: Long,
        receiverCardId: Long
    ): OperationResult<Unit, String, String> {
        authRepository.user.value?.id
            ?: return OperationResult.Failure("No hay sesion activa")

        return exchangeRepository.createExchange(
            receiverId = receiverId,
            proposerCardId = proposerCardId,
            receiverCardId = receiverCardId
        )
    }
}
