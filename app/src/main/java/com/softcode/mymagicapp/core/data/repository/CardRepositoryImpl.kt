package com.softcode.mymagicapp.core.data.repository

import com.softcode.mymagicapp.core.data.local.dao.CardDao
import com.softcode.mymagicapp.core.domain.entities.CardEntity
import com.softcode.mymagicapp.core.domain.repository.CardRepository
import com.softcode.mymagicapp.core.domain.results.OperationResult
import com.softcode.mymagicapp.core.network.CardModel
import com.softcode.mymagicapp.core.network.CardsApi
import com.softcode.mymagicapp.core.network.toDomain
import com.softcode.mymagicapp.core.network.toModel
import com.softcode.mymagicapp.core.network.toRoomEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardRepositoryImpl @Inject constructor(
    private val api: CardsApi,
    private val cardDao: CardDao
) : CardRepository {

    override fun getCardsFlow(userId: Long): Flow<List<CardEntity>> =
        cardDao.getCardsByUserId(userId).map { list -> list.map { it.toDomain() } }

    override suspend fun syncCards(userId: Long): OperationResult<Unit, String, String> {
        return try {
            val response = api.getCards()
            when {
                response.isSuccessful -> {
                    val body = response.body()
                    if (body != null) {
                        // Server is the source of truth: replace all local cards for this user
                        cardDao.deleteCardsByUserId(userId)
                        body.forEach { cardDao.insertCard(it.toRoomEntity()) }
                        OperationResult.Success(Unit)
                    } else {
                        OperationResult.Failure("Empty response body")
                    }
                }
                response.code() in 400..499 ->
                    OperationResult.Failure(response.errorBody()?.string() ?: "Business error")
                else ->
                    OperationResult.Error("Server error: ${response.code()}")
            }
        } catch (e: Exception) {
            OperationResult.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun addCard(
        userId: Long,
        title: String,
        description: String,
        imageUrl: String
    ): OperationResult<Unit, String, String> {
        val newCard = CardModel(
            userId = userId,
            title = title,
            description = description,
            createdAt = System.currentTimeMillis(),
            imageUrl = imageUrl
        )
        return try {
            val response = api.postCard(newCard)
            when {
                response.isSuccessful -> {
                    val created = response.body()
                    if (created != null) {
                        cardDao.insertCard(created.toRoomEntity())
                        OperationResult.Success(Unit)
                    } else {
                        OperationResult.Failure("Respuesta vacía del servidor")
                    }
                }
                response.code() in 400..499 ->
                    OperationResult.Failure(response.errorBody()?.string() ?: "Error de negocio")
                else ->
                    OperationResult.Error("Error del servidor: ${response.code()}")
            }
        } catch (e: Exception) {
            OperationResult.Error(e.message ?: "Error desconocido")
        }
    }

    override suspend fun updateCard(card: CardEntity): OperationResult<Unit, String, String> {
        return try {
            val response = api.updateCard(card.toModel())
            when {
                response.isSuccessful -> {
                    cardDao.updateCard(card.toRoomEntity())
                    OperationResult.Success(Unit)
                }
                response.code() in 400..499 ->
                    OperationResult.Failure(response.errorBody()?.string() ?: "Business error")
                else ->
                    OperationResult.Error("Server error: ${response.code()}")
            }
        } catch (e: Exception) {
            OperationResult.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun deleteCard(card: CardEntity): OperationResult<Unit, String, String> {
        return try {
            val response = api.deleteCard(card.toModel())
            when {
                response.isSuccessful -> {
                    cardDao.deleteCard(card.toRoomEntity())
                    OperationResult.Success(Unit)
                }
                response.code() in 400..499 ->
                    OperationResult.Failure(response.errorBody()?.string() ?: "Business error")
                else ->
                    OperationResult.Error("Server error: ${response.code()}")
            }
        } catch (e: Exception) {
            OperationResult.Error(e.message ?: "Unknown error")
        }
    }
}
