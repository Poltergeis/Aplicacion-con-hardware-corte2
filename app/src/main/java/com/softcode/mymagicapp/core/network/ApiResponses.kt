package com.softcode.mymagicapp.core.network

import androidx.room.PrimaryKey
import com.softcode.mymagicapp.core.data.local.entity.CardEntity
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
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
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