package com.softcode.mymagicapp.core.network

import com.softcode.mymagicapp.core.data.local.entity.CardEntity as RoomCardEntity
import com.softcode.mymagicapp.core.domain.entities.CardEntity as DomainCardEntity

// ── Network → Room ──────────────────────────────────────────────────────────
fun CardModel.toRoomEntity() = RoomCardEntity(
    id = id,
    userId = userId,
    title = title,
    description = description,
    createdAt = createdAt,
    imageUrl = imageUrl
)

// ── Room → Domain ────────────────────────────────────────────────────────────
fun RoomCardEntity.toDomain() = DomainCardEntity(
    id = id,
    userId = userId,
    title = title,
    description = description,
    createdAt = createdAt,
    imageUrl = imageUrl
)

// ── Domain → Room ────────────────────────────────────────────────────────────
fun DomainCardEntity.toRoomEntity() = RoomCardEntity(
    id = id,
    userId = userId,
    title = title,
    description = description,
    createdAt = createdAt,
    imageUrl = imageUrl
)

// ── Domain → Network ─────────────────────────────────────────────────────────
fun DomainCardEntity.toModel() = CardModel(
    id = id,
    userId = userId,
    title = title,
    description = description,
    createdAt = createdAt,
    imageUrl = imageUrl
)
