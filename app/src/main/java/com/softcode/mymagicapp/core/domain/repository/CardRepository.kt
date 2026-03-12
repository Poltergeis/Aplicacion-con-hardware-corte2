package com.softcode.mymagicapp.core.domain.repository

import com.softcode.mymagicapp.core.domain.entities.CardEntity
import com.softcode.mymagicapp.core.domain.results.OperationResult
import kotlinx.coroutines.flow.StateFlow

interface CardRepository {
    val cards: StateFlow<List<CardEntity>>
    suspend fun getCards(): OperationResult<Unit, String, String>
    suspend fun addCard(userId: Long, title: String, description: String, imageUrl: String): OperationResult<Unit, String, String>
    suspend fun updateCard(card: CardEntity): OperationResult<Unit, String, String>
    suspend fun deleteCard(card: CardEntity): OperationResult<Unit, String, String>
}