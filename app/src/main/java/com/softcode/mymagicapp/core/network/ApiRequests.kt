package com.softcode.mymagicapp.core.network

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val username: String,
    val password: String
)

@Serializable
data class VerifyLoggedUserRequest(
    val token: String
)