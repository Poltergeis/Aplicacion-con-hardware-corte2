package com.softcode.mymagicapp.core.domain.repository

import com.softcode.mymagicapp.core.domain.entities.ExchangeEntity
import com.softcode.mymagicapp.core.domain.entities.UserEntity
import com.softcode.mymagicapp.core.domain.results.OperationResult
import kotlinx.coroutines.flow.Flow

interface ExchangeRepository {
    /** Reactive stream backed by Room — emits immediately from local DB. */
    fun getExchangesFlow(userId: Long): Flow<List<ExchangeEntity>>

    /** Fetch exchanges from the API and write them to Room (SSOT sync). */
    suspend fun syncExchanges(userId: Long): OperationResult<Unit, String, String>

    suspend fun createExchange(
        receiverId: Long,
        proposerCardId: Long,
        receiverCardId: Long
    ): OperationResult<Unit, String, String>

    suspend fun respondExchange(
        exchangeId: Long,
        status: String
    ): OperationResult<Unit, String, String>

    suspend fun getUsers(search: String = ""): OperationResult<List<UserEntity>, String, String>
}
