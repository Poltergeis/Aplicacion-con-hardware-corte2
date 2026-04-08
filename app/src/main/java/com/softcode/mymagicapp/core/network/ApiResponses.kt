package com.softcode.mymagicapp.core.network

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse(
    val message: String?
)

@Serializable
data class LoginResponse(
    val id: Long?,
    val username: String?,
    val token: String?,
    val message: String?
)

@Serializable
data class CardModel(
    val id: Long = 0,
    val userId: Long,
    val title: String,
    val description: String,
    val createdAt: Long = System.currentTimeMillis(),
    val imageUrl: String
)

@Serializable
data class ImageUploadResponse(
    val imageUrl: String
)

@Serializable
data class ExchangeModel(
    val id: Long = 0,
    val proposerId: Long,
    val receiverId: Long,
    val proposerCardId: Long,
    val receiverCardId: Long,
    val proposerUsername: String = "",
    val receiverUsername: String = "",
    val proposerCardTitle: String = "",
    val receiverCardTitle: String = "",
    val status: String = "PENDING",
    val createdAt: Long = System.currentTimeMillis()
)