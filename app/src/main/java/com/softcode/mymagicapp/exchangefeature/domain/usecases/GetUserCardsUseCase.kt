package com.softcode.mymagicapp.exchangefeature.domain.usecases

import com.softcode.mymagicapp.core.domain.entities.CardEntity
import com.softcode.mymagicapp.core.domain.results.OperationResult
import com.softcode.mymagicapp.core.network.CardsApi
import com.softcode.mymagicapp.core.network.toDomain
import com.softcode.mymagicapp.core.network.toRoomEntity
import javax.inject.Inject

class GetUserCardsUseCase @Inject constructor(
    private val api: CardsApi
) {
    suspend operator fun invoke(userId: Long): OperationResult<List<CardEntity>, String, String> {
        return try {
            val response = api.getCardsByUser(userId)
            when {
                response.isSuccessful -> {
                    val body = response.body()
                    if (body != null) OperationResult.Success(body.map { it.toRoomEntity().toDomain() })
                    else OperationResult.Failure("Respuesta vacía")
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
}
