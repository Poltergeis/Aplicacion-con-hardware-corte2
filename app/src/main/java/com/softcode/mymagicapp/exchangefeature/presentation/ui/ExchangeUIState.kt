package com.softcode.mymagicapp.exchangefeature.presentation.ui

import com.softcode.mymagicapp.core.domain.entities.CardEntity
import com.softcode.mymagicapp.core.domain.entities.ExchangeEntity

data class ExchangeUIState(
    val exchanges: List<ExchangeEntity> = emptyList(),
    val myCards: List<CardEntity> = emptyList(),
    val isLoading: Boolean = true,
    val showCreateDialog: Boolean = false,
    val receiverIdText: String = "",
    val selectedMyCard: CardEntity? = null,
    val receiverCardIdText: String = "",
    val currentUserId: Long = 0,
    val filterTab: ExchangeFilterTab = ExchangeFilterTab.ALL
) {
    val filteredExchanges: List<ExchangeEntity>
        get() = when (filterTab) {
            ExchangeFilterTab.ALL -> exchanges
            ExchangeFilterTab.SENT -> exchanges.filter { it.proposerId == currentUserId }
            ExchangeFilterTab.RECEIVED -> exchanges.filter { it.receiverId == currentUserId }
            ExchangeFilterTab.PENDING -> exchanges.filter { it.status == ExchangeEntity.STATUS_PENDING }
        }
}

enum class ExchangeFilterTab(val label: String) {
    ALL("Todos"),
    SENT("Enviados"),
    RECEIVED("Recibidos"),
    PENDING("Pendientes")
}
