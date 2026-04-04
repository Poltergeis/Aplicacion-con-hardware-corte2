package com.softcode.mymagicapp.core.network

import com.softcode.mymagicapp.core.domain.entities.CardEntity

fun CardResponse.toDomain() = CardEntity(
    id = id,
    userId = userId,
    title = title,
    description = description,
    createdAt = createdAt
)

fun CardEntity.toRequest() = CardRequest(
    title = title,
    description = description
)
