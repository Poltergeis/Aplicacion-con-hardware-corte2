package com.softcode.mymagicapp.core.domain.entities

data class CardEntity(
    val id: Long,
    val userId: Long,
    val title: String,
    val description: String,
    val createdAt: Long,
    val imageUrl: String = ""
)