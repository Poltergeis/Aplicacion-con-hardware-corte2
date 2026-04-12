package com.softcode.mymagicapp.exchangefeature.presentation.ui

import com.softcode.mymagicapp.core.domain.entities.CardEntity
import com.softcode.mymagicapp.core.domain.entities.ExchangeEntity
import com.softcode.mymagicapp.core.network.UserModel

enum class ExchangeStep { SELECT_USER, SELECT_MY_CARD, SELECT_RECEIVER_CARD }

data class ExchangeUIState(
    val exchanges: List<ExchangeEntity> = emptyList(),
    val myCards: List<CardEntity> = emptyList(),
    val isLoading: Boolean = true,
    val currentUserId: Long = 0,
    val filterTab: ExchangeFilterTab = ExchangeFilterTab.ALL,

    // ── Create dialog stepper ────────────────────────────────────────────────
    val showCreateDialog: Boolean = false,
    val createStep: ExchangeStep = ExchangeStep.SELECT_USER,
    // Step 1 – user selection
    val userSearchQuery: String = "",
    val availableUsers: List<UserModel> = emptyList(),
    val isLoadingUsers: Boolean = false,
    val selectedUser: UserModel? = null,
    // Step 2 – my card selection
    val myCardSearchQuery: String = "",
    val selectedMyCard: CardEntity? = null,
    // Step 3 – receiver card selection
    val receiverCards: List<CardEntity> = emptyList(),
    val isLoadingReceiverCards: Boolean = false,
    val selectedReceiverCard: CardEntity? = null
) {
    val filteredExchanges: List<ExchangeEntity>
        get() = when (filterTab) {
            ExchangeFilterTab.ALL -> exchanges
            ExchangeFilterTab.SENT -> exchanges.filter { it.proposerId == currentUserId }
            ExchangeFilterTab.RECEIVED -> exchanges.filter { it.receiverId == currentUserId }
            ExchangeFilterTab.PENDING -> exchanges.filter { it.status == ExchangeEntity.STATUS_PENDING }
        }

    val filteredUsers: List<UserModel>
        get() = if (userSearchQuery.isBlank()) availableUsers
                else availableUsers.filter { it.username.contains(userSearchQuery, ignoreCase = true) }

    val filteredMyCards: List<CardEntity>
        get() = if (myCardSearchQuery.isBlank()) myCards
                else myCards.filter { it.title.contains(myCardSearchQuery, ignoreCase = true) }
}

enum class ExchangeFilterTab(val label: String) {
    ALL("Todos"),
    SENT("Enviados"),
    RECEIVED("Recibidos"),
    PENDING("Pendientes")
}
