package com.softcode.mymagicapp.core.data.repository

import com.softcode.mymagicapp.core.data.local.dao.UserDao
import com.softcode.mymagicapp.core.data.local.entity.UserEntity
import com.softcode.mymagicapp.core.data.session.SessionManager
import com.softcode.mymagicapp.core.utils.PasswordUtils
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

sealed class AuthResult {
    data class Success(val userId: Long) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

@Singleton
class AuthRepository @Inject constructor(
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) {
    val currentUserId: Flow<Long?> = sessionManager.userId

    suspend fun register(name: String, password: String): AuthResult {
        val existing = userDao.getUserByName(name)
        if (existing != null) {
            return AuthResult.Error("El usuario ya existe")
        }
        val user = UserEntity(
            name = name,
            passwordHash = PasswordUtils.hash(password)
        )
        val id = userDao.insertUser(user)
        sessionManager.saveSession(id)
        return AuthResult.Success(id)
    }

    suspend fun login(name: String, password: String): AuthResult {
        val user = userDao.getUserByName(name)
            ?: return AuthResult.Error("Usuario no encontrado")
        if (!PasswordUtils.verify(password, user.passwordHash)) {
            return AuthResult.Error("Contraseña incorrecta")
        }
        sessionManager.saveSession(user.id)
        return AuthResult.Success(user.id)
    }

    suspend fun logout() {
        sessionManager.clearSession()
    }
}
