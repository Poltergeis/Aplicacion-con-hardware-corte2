package com.softcode.mymagicapp.core.domain.repository

import com.softcode.mymagicapp.core.domain.entities.CardEntity
import com.softcode.mymagicapp.core.domain.results.OperationResult
import kotlinx.coroutines.flow.Flow

interface CardRepository {
    /** Reactive stream backed by Room — emits immediately from local DB. */
    fun getCardsFlow(userId: Long): Flow<List<CardEntity>>

    /** Fetch cards from the API and write them to Room (SSOT sync). */
    suspend fun syncCards(userId: Long): OperationResult<Unit, String, String>

    suspend fun addCard(userId: Long, title: String, description: String, imageUrl: String): OperationResult<Unit, String, String>
    suspend fun updateCard(card: CardEntity): OperationResult<Unit, String, String>
    suspend fun deleteCard(card: CardEntity): OperationResult<Unit, String, String>
}
