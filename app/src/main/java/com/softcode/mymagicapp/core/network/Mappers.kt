package com.softcode.mymagicapp.core.network

import com.softcode.mymagicapp.core.data.local.entity.CardEntity as RoomCardEntity
import com.softcode.mymagicapp.core.domain.entities.CardEntity as DomainCardEntity
import com.softcode.mymagicapp.core.data.local.entity.ExchangeEntity as RoomExchangeEntity
import com.softcode.mymagicapp.core.domain.entities.ExchangeEntity as DomainExchangeEntity
import com.softcode.mymagicapp.core.domain.entities.UserEntity

// ── Network → Domain (Users) ─────────────────────────────────────────────────
fun UserModel.toDomain() = UserEntity(id = id, username = username, password = null)

// ── Network → Room ──────────────────────────────────────────────────────────
fun CardModel.toRoomEntity() = RoomCardEntity(
    id = id,
    userId = userId,
    title = title,
    description = description,
    createdAt = createdAt,
    imageUrl = imageUrl,
    power = power,
    defense = defense,
    rarity = rarity,
    latitude = latitude,
    longitude = longitude
)

// ── Room → Domain ────────────────────────────────────────────────────────────
fun RoomCardEntity.toDomain() = DomainCardEntity(
    id = id,
    userId = userId,
    title = title,
    description = description,
    createdAt = createdAt,
    imageUrl = imageUrl,
    power = power,
    defense = defense,
    rarity = rarity,
    latitude = latitude,
    longitude = longitude
)

// ── Domain → Room ────────────────────────────────────────────────────────────
fun DomainCardEntity.toRoomEntity() = RoomCardEntity(
    id = id,
    userId = userId,
    title = title,
    description = description,
    createdAt = createdAt,
    imageUrl = imageUrl,
    power = power,
    defense = defense,
    rarity = rarity,
    latitude = latitude,
    longitude = longitude
)

// ── Domain → Network ─────────────────────────────────────────────────────────
fun DomainCardEntity.toModel() = CardModel(
    id = id,
    userId = userId,
    title = title,
    description = description,
    createdAt = createdAt,
    imageUrl = imageUrl,
    power = power,
    defense = defense,
    rarity = rarity,
    latitude = latitude,
    longitude = longitude
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
