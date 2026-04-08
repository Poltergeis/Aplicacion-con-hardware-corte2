package com.softcode.mymagicapp.core.domain.entities

data class ExchangeEntity(
    val id: Long,
    val proposerId: Long,
    val receiverId: Long,
    val proposerCardId: Long,
    val receiverCardId: Long,
    val proposerUsername: String = "",
    val receiverUsername: String = "",
    val proposerCardTitle: String = "",
    val receiverCardTitle: String = "",
    val status: String = STATUS_PENDING,
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val STATUS_PENDING = "PENDING"
        const val STATUS_ACCEPTED = "ACCEPTED"
        const val STATUS_REJECTED = "REJECTED"
    }
}
