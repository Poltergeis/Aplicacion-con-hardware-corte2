package com.softcode.mymagicapp.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exchanges")
data class ExchangeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
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
