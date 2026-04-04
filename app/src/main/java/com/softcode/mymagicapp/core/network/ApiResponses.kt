package com.softcode.mymagicapp.core.network

data class AuthResponse(
    val id: Long,
    val username: String,
    val token: String,
    val message: String?
)

data class CardResponse(
    val id: Long,
    val userId: Long,
    val title: String,
    val description: String,
    val createdAt: Long
)

data class MessageResponse(val message: String?)
