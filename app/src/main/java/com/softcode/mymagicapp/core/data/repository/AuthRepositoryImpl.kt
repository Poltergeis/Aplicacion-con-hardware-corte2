package com.softcode.mymagicapp.core.data.repository

import com.softcode.mymagicapp.core.data.local.dao.UserDao
import com.softcode.mymagicapp.core.data.local.entity.UserEntity as RoomUserEntity
import com.softcode.mymagicapp.core.data.session.SessionManager
import com.softcode.mymagicapp.core.domain.entities.UserEntity
import com.softcode.mymagicapp.core.domain.repository.AuthRepository
import com.softcode.mymagicapp.core.domain.results.AuthResult
import com.softcode.mymagicapp.core.network.CardsApi
import com.softcode.mymagicapp.core.network.LoginRequest
import com.softcode.mymagicapp.core.network.RegisterRequest
import com.softcode.mymagicapp.core.network.VerifyLoggedUserRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val sessionManager: SessionManager,
    private val api: CardsApi,
    private val userDao: UserDao
) : AuthRepository {

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    override val user = _currentUser.asStateFlow()

    override suspend fun login(username: String, password: String): AuthResult {
        return try {
            val response = api.login(LoginRequest(username, password))
            if (response.isSuccessful) {
                val body = response.body()
                val token = body?.token
                val name = body?.username
                val id = body?.id
                if ((token != null && name != null) && id != null) {
                    sessionManager.saveSession(name, token)
                    userDao.upsertUser(RoomUserEntity(id = id, name = name, passwordHash = ""))
                    _currentUser.value = UserEntity(id, name, token)
                    AuthResult.Success()
                } else {
                    AuthResult.Error("Invalid server response")
                }
            } else {
                val message = response.errorBody()?.string() ?: "Unknown error"
                AuthResult.Error(message)
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun register(username: String, password: String): AuthResult {
        return try {
            val response = api.register(RegisterRequest(username, password))
            if (response.isSuccessful) {
                val body = response.body()
                val token = body?.token
                val name = body?.username
                val id = body?.id
                if ((token != null && name != null) && id != null) {
                    sessionManager.saveSession(name, token)
                    userDao.upsertUser(RoomUserEntity(id = id, name = name, passwordHash = ""))
                    _currentUser.value = UserEntity(id, name, token)
                    AuthResult.Success()
                } else {
                    AuthResult.Error("Invalid server response")
                }
            } else {
                val message = response.errorBody()?.string() ?: "Unknown error"
                AuthResult.Error(message)
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun logout(): AuthResult {
        return try {
            sessionManager.clearSession()
            _currentUser.value = null
            AuthResult.Success()
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun restoreSession(): AuthResult {
        return try {
            val session = sessionManager.sessionData.firstOrNull()
            if (session != null) {
                val response = api.validateCurrentToken(VerifyLoggedUserRequest(session.token))
                if (response.isSuccessful) {
                    val body = response.body()!!
                    userDao.upsertUser(RoomUserEntity(id = body.id!!, name = body.username!!, passwordHash = ""))
                    _currentUser.value = UserEntity(body.id!!, body.username!!, null)
                    AuthResult.Success()
                } else {
                    sessionManager.clearSession()
                    AuthResult.Error("Session expired")
                }
            } else {
                AuthResult.Error("No active session")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Unknown error")
        }
    }
}