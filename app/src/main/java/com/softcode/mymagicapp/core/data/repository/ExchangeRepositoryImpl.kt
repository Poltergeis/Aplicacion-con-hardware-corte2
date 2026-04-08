package com.softcode.mymagicapp.core.data.repository

import com.softcode.mymagicapp.core.data.local.dao.ExchangeDao
import com.softcode.mymagicapp.core.domain.entities.ExchangeEntity
import com.softcode.mymagicapp.core.domain.repository.ExchangeRepository
import com.softcode.mymagicapp.core.domain.results.OperationResult
import com.softcode.mymagicapp.core.network.CardsApi
import com.softcode.mymagicapp.core.network.CreateExchangeRequest
import com.softcode.mymagicapp.core.network.RespondExchangeRequest
import com.softcode.mymagicapp.core.network.toDomain
import com.softcode.mymagicapp.core.network.toRoomEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExchangeRepositoryImpl @Inject constructor(
    private val api: CardsApi,
    private val exchangeDao: ExchangeDao
) : ExchangeRepository {

    override fun getExchangesFlow(userId: Long): Flow<List<ExchangeEntity>> =
        exchangeDao.getExchangesByUserId(userId).map { list -> list.map { it.toDomain() } }

    override suspend fun syncExchanges(userId: Long): OperationResult<Unit, String, String> {
        return try {
            val response = api.getExchanges()
            when {
                response.isSuccessful -> {
                    val body = response.body()
                    if (body != null) {
                        exchangeDao.deleteExchangesByUserId(userId)
                        exchangeDao.insertAll(body.map { it.toRoomEntity() })
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

    override suspend fun createExchange(
        receiverId: Long,
        proposerCardId: Long,
        receiverCardId: Long
    ): OperationResult<Unit, String, String> {
        val request = CreateExchangeRequest(
            receiverId = receiverId,
            proposerCardId = proposerCardId,
            receiverCardId = receiverCardId
        )
        return try {
            val response = api.createExchange(request)
            when {
                response.isSuccessful -> {
                    val created = response.body()
                    if (created != null) {
                        exchangeDao.insertExchange(created.toRoomEntity())
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

    override suspend fun respondExchange(
        exchangeId: Long,
        status: String
    ): OperationResult<Unit, String, String> {
        val request = RespondExchangeRequest(
            exchangeId = exchangeId,
            status = status
        )
        return try {
            val response = api.respondExchange(request)
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
