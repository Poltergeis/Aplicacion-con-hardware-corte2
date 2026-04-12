package com.softcode.mymagicapp.exchangefeature.domain.usecases

import com.softcode.mymagicapp.core.network.CardsApi
import com.softcode.mymagicapp.core.network.UserModel
import com.softcode.mymagicapp.core.domain.results.OperationResult
import javax.inject.Inject

class GetUsersUseCase @Inject constructor(
    private val api: CardsApi
) {
    suspend operator fun invoke(search: String = ""): OperationResult<List<UserModel>, String, String> {
        return try {
            val response = api.getUsers(search)
            when {
                response.isSuccessful -> {
                    val body = response.body()
                    if (body != null) OperationResult.Success(body)
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
