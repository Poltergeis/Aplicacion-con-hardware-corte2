package com.softcode.mymagicapp.core.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "exchanges",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["proposerId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["receiverId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["proposerId"]), Index(value = ["receiverId"])]
)
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
