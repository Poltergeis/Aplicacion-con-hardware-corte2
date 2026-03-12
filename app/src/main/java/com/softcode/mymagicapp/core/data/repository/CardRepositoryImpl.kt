package com.softcode.mymagicapp.core.data.repository

import com.softcode.mymagicapp.core.domain.entities.CardEntity
import com.softcode.mymagicapp.core.domain.repository.CardRepository
import com.softcode.mymagicapp.core.domain.results.OperationResult
import com.softcode.mymagicapp.core.network.CardModel
import com.softcode.mymagicapp.core.network.CardsApi
import com.softcode.mymagicapp.core.network.toEntity
import com.softcode.mymagicapp.core.network.toModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardRepositoryImpl @Inject constructor(
    private val api: CardsApi
) : CardRepository {
    private val _cards = MutableStateFlow<List<CardEntity>>(emptyList())
    override val cards = _cards.asStateFlow()

    override suspend fun getCards(): OperationResult<Unit, String, String> {
        return try {
            val response = api.getCards()
            when {
                response.isSuccessful -> {
                    val body = response.body()
                    if (body != null) {
                        _cards.value = body.map { it.toEntity() }
                        OperationResult.Success(Unit)
                    } else {
                        OperationResult.Failure("Empty response body")
                    }
                }
                response.code() in 400..499 -> {
                    val error = response.errorBody()?.string()
                    OperationResult.Failure(error ?: "Business error")
                }
                else -> OperationResult.Error("Server error: ${response.code()}")
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
        val previousState = _cards.value

        return try {
            val response = api.postCard(newCard)
            when {
                response.isSuccessful -> {
                    val created = response.body()
                    if (created != null) {
                        _cards.value = previousState + created.toEntity()
                        OperationResult.Success(Unit)
                    } else {
                        OperationResult.Failure("Respuesta vacía del servidor")
                    }
                }
                response.code() in 400..499 -> {
                    OperationResult.Failure(response.errorBody()?.string() ?: "Error de negocio")
                }
                else -> OperationResult.Error("Error del servidor: ${response.code()}")
            }
        } catch (e: Exception) {
            OperationResult.Error(e.message ?: "Error desconocido")
        }
    }

    override suspend fun updateCard(card: CardEntity): OperationResult<Unit, String, String> {
        val previousState = _cards.value
        _cards.value = previousState.map { if (it.id == card.id) card else it }

        return try {
            val response = api.updateCard(card.toModel())
            when {
                response.isSuccessful -> OperationResult.Success(Unit)
                response.code() in 400..499 -> {
                    _cards.value = previousState
                    val error = response.errorBody()?.string()
                    OperationResult.Failure(error ?: "Business error")
                }
                else -> {
                    _cards.value = previousState
                    OperationResult.Error("Server error: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            _cards.value = previousState
            OperationResult.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun deleteCard(card: CardEntity): OperationResult<Unit, String, String> {
        val previousState = _cards.value
        _cards.value = previousState.filter { it.id != card.id }

        return try {
            val response = api.deleteCard(card.toModel())
            when {
                response.isSuccessful -> OperationResult.Success(Unit)
                response.code() in 400..499 -> {
                    _cards.value = previousState
                    val error = response.errorBody()?.string()
                    OperationResult.Failure(error ?: "Business error")
                }
                else -> {
                    _cards.value = previousState
                    OperationResult.Error("Server error: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            _cards.value = previousState
            OperationResult.Error(e.message ?: "Unknown error")
        }
    }
}
