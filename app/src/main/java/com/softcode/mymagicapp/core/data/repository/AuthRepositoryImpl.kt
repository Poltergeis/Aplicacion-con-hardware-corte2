package com.softcode.mymagicapp.core.data.repository

import com.softcode.mymagicapp.core.domain.entities.UserEntity
import com.softcode.mymagicapp.core.domain.repository.AuthRepository
import com.softcode.mymagicapp.core.domain.results.AuthResult
import com.softcode.mymagicapp.core.network.CardsApi
import com.softcode.mymagicapp.core.network.LoginRequest
import com.softcode.mymagicapp.core.network.RegisterRequest
import com.softcode.mymagicapp.core.network.TokenHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepositoryImpl(
    private val api: CardsApi,
    private val tokenHolder: TokenHolder
) : AuthRepository {

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    override val user = _currentUser.asStateFlow()

    override suspend fun login(username: String, password: String): AuthResult {
        return try {
            val response = api.login(LoginRequest(username, password))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    tokenHolder.token = body.token
                    _currentUser.value = UserEntity(body.id, body.username, body.token)
                    AuthResult.Success()
                } else {
                    AuthResult.Error("Respuesta inválida del servidor")
                }
            } else {
                AuthResult.Error(response.errorBody()?.string() ?: "Error desconocido")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Error desconocido")
        }
    }

    override suspend fun register(username: String, password: String): AuthResult {
        return try {
            val response = api.register(RegisterRequest(username, password))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    tokenHolder.token = body.token
                    _currentUser.value = UserEntity(body.id, body.username, body.token)
                    AuthResult.Success()
                } else {
                    AuthResult.Error("Respuesta inválida del servidor")
                }
            } else {
                AuthResult.Error(response.errorBody()?.string() ?: "Error desconocido")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Error desconocido")
        }
    }

    override suspend fun logout(): AuthResult {
        return try {
            tokenHolder.token = null
            _currentUser.value = null
            AuthResult.Success()
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Error desconocido")
        }
    }
}
