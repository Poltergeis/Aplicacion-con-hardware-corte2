package com.softcode.mymagicapp.exchangefeature.domain.usecases

import com.softcode.mymagicapp.core.domain.entities.ExchangeEntity
import com.softcode.mymagicapp.core.domain.repository.AuthRepository
import com.softcode.mymagicapp.core.domain.repository.ExchangeRepository
import com.softcode.mymagicapp.core.domain.results.OperationResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest

class GetExchangesUseCase(
    private val exchangeRepository: ExchangeRepository,
    private val authRepository: AuthRepository
) {
    /** Reactive stream from Room — emits immediately then on every local change. */
    val exchanges: Flow<List<ExchangeEntity>> = authRepository.user
        .filterNotNull()
        .flatMapLatest { user -> exchangeRepository.getExchangesFlow(user.id) }

    /** Syncs from the server into Room (SSOT). */
    suspend operator fun invoke(): OperationResult<Unit, String, String> {
        val userId = authRepository.user.value?.id
            ?: return OperationResult.Failure("No hay sesion activa")
        return exchangeRepository.syncExchanges(userId)
    }
}
