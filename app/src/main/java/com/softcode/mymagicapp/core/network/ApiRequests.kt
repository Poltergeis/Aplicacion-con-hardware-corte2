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

@Serializable
data class CreateExchangeRequest(
    val receiverId: Long,
    val proposerCardId: Long,
    val receiverCardId: Long
)

@Serializable
data class RespondExchangeRequest(
    val exchangeId: Long,
    val status: String
)

@Serializable
data class UpdateFcmTokenRequest(
    val token: String
)