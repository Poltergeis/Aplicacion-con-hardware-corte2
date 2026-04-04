package com.softcode.mymagicapp.core.domain.repository

import com.softcode.mymagicapp.core.domain.entities.CardEntity
import com.softcode.mymagicapp.core.domain.results.OperationResult

interface CardRepository {
    suspend fun getCards(userId: Long): OperationResult<List<CardEntity>, String, String>
    suspend fun addCard(userId: Long, title: String, description: String): OperationResult<Unit, String, String>
    suspend fun updateCard(card: CardEntity): OperationResult<Unit, String, String>
    suspend fun deleteCard(card: CardEntity): OperationResult<Unit, String, String>
}
