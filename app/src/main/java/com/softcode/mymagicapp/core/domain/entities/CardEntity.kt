package com.softcode.mymagicapp.core.domain.entities

data class CardEntity(
    val id: Long,
    val userId: Long,
    val title: String,
    val description: String,
    val createdAt: Long,
    val imageUrl: String = "",
    val power: Int = 1,
    val defense: Int = 1,
    val rarity: Int = 1,
    val latitude: Double? = null,
    val longitude: Double? = null
)