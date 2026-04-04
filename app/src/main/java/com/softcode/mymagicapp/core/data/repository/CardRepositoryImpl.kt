package com.softcode.mymagicapp.core.data.repository

import com.softcode.mymagicapp.core.domain.entities.CardEntity
import com.softcode.mymagicapp.core.domain.repository.CardRepository
import com.softcode.mymagicapp.core.domain.results.OperationResult
import com.softcode.mymagicapp.core.network.CardRequest
import com.softcode.mymagicapp.core.network.CardsApi
import com.softcode.mymagicapp.core.network.toDomain

class CardRepositoryImpl(
    private val api: CardsApi
) : CardRepository {

    override suspend fun getCards(userId: Long): OperationResult<List<CardEntity>, String, String> {
        return try {
            val response = api.getCards()
            when {
                response.isSuccessful -> {
                    val body = response.body()
                    if (body != null) {
                        OperationResult.Success(body.map { it.toDomain() })
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

    override suspend fun addCard(
        userId: Long,
        title: String,
        description: String
    ): OperationResult<Unit, String, String> {
        return try {
            val response = api.postCard(CardRequest(title, description))
            when {
                response.isSuccessful -> OperationResult.Success(Unit)
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
            val response = api.updateCard(card.id, CardRequest(card.title, card.description))
            when {
                response.isSuccessful -> OperationResult.Success(Unit)
                response.code() in 400..499 ->
                    OperationResult.Failure(response.errorBody()?.string() ?: "Error de negocio")
                else ->
                    OperationResult.Error("Error del servidor: ${response.code()}")
            }
        } catch (e: Exception) {
            OperationResult.Error(e.message ?: "Error desconocido")
        }
    }

    override suspend fun deleteCard(card: CardEntity): OperationResult<Unit, String, String> {
        return try {
            val response = api.deleteCard(card.id)
            when {
                response.isSuccessful -> OperationResult.Success(Unit)
                response.code() in 400..499 ->
                    OperationResult.Failure(response.errorBody()?.string() ?: "Error de negocio")
                else ->
                    OperationResult.Error("Error del servidor: ${response.code()}")
            }
        } catch (e: Exception) {
            OperationResult.Error(e.message ?: "Error desconocido")
        }
    }
}
