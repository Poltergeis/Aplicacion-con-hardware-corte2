package com.softcode.mymagicapp.core.domain.repository

import com.softcode.mymagicapp.core.domain.entities.UserEntity
import com.softcode.mymagicapp.core.domain.results.AuthResult
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val user: StateFlow<UserEntity?>
    suspend fun login(username: String, password: String): AuthResult
    suspend fun register(username: String, password: String): AuthResult
    suspend fun logout(): AuthResult
    suspend fun restoreSession(): AuthResult
}