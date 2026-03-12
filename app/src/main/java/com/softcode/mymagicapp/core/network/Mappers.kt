package com.softcode.mymagicapp.core.network

import com.softcode.mymagicapp.core.domain.entities.CardEntity

fun CardModel.toEntity() = CardEntity(
    id = id,
    userId = userId,
    title = title,
    description = description,
    createdAt = createdAt
)

fun CardEntity.toModel() = CardModel(
    id = id,
    userId = userId,
    title = title,
    description = description,
    createdAt = createdAt,
    imageUrl = ""
)