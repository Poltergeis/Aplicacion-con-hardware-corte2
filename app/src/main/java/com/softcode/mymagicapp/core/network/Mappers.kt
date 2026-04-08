package com.softcode.mymagicapp.core.network

import com.softcode.mymagicapp.core.data.local.entity.CardEntity as RoomCardEntity
import com.softcode.mymagicapp.core.domain.entities.CardEntity as DomainCardEntity
import com.softcode.mymagicapp.core.data.local.entity.ExchangeEntity as RoomExchangeEntity
import com.softcode.mymagicapp.core.domain.entities.ExchangeEntity as DomainExchangeEntity

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

// ══ Exchange Mappers ═════════════════════════════════════════════════════════

// ── Network → Room ──────────────────────────────────────────────────────────
fun ExchangeModel.toRoomEntity() = RoomExchangeEntity(
    id = id,
    proposerId = proposerId,
    receiverId = receiverId,
    proposerCardId = proposerCardId,
    receiverCardId = receiverCardId,
    proposerUsername = proposerUsername,
    receiverUsername = receiverUsername,
    proposerCardTitle = proposerCardTitle,
    receiverCardTitle = receiverCardTitle,
    status = status,
    createdAt = createdAt
)

// ── Room → Domain ────────────────────────────────────────────────────────────
fun RoomExchangeEntity.toDomain() = DomainExchangeEntity(
    id = id,
    proposerId = proposerId,
    receiverId = receiverId,
    proposerCardId = proposerCardId,
    receiverCardId = receiverCardId,
    proposerUsername = proposerUsername,
    receiverUsername = receiverUsername,
    proposerCardTitle = proposerCardTitle,
    receiverCardTitle = receiverCardTitle,
    status = status,
    createdAt = createdAt
)

// ── Domain → Room ────────────────────────────────────────────────────────────
fun DomainExchangeEntity.toRoomEntity() = RoomExchangeEntity(
    id = id,
    proposerId = proposerId,
    receiverId = receiverId,
    proposerCardId = proposerCardId,
    receiverCardId = receiverCardId,
    proposerUsername = proposerUsername,
    receiverUsername = receiverUsername,
    proposerCardTitle = proposerCardTitle,
    receiverCardTitle = receiverCardTitle,
    status = status,
    createdAt = createdAt
)
