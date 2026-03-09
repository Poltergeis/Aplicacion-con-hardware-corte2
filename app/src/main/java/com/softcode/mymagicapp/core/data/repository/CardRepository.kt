package com.softcode.mymagicapp.core.data.repository

import com.softcode.mymagicapp.core.data.local.dao.CardDao
import com.softcode.mymagicapp.core.data.local.entity.CardEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardRepository @Inject constructor(
    private val cardDao: CardDao
) {
    fun getCardsByUser(userId: Long): Flow<List<CardEntity>> {
        return cardDao.getCardsByUserId(userId)
    }

    suspend fun addCard(userId: Long, title: String, description: String): Long {
        val card = CardEntity(userId = userId, title = title, description = description)
        return cardDao.insertCard(card)
    }

    suspend fun updateCard(card: CardEntity) {
        cardDao.updateCard(card)
    }

    suspend fun deleteCard(card: CardEntity) {
        cardDao.deleteCard(card)
    }
}
