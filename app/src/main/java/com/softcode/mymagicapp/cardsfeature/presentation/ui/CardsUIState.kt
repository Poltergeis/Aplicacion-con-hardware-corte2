package com.softcode.mymagicapp.cardsfeature.presentation.ui

import android.net.Uri
import com.softcode.mymagicapp.core.domain.entities.CardEntity

data class CardsUIState(
    val cards: List<CardEntity> = emptyList(),
    val isLoading: Boolean = true,
    val showAddDialog: Boolean = false,
    val showEditDialog: Boolean = false,
    val editingCard: CardEntity? = null,
    val dialogTitle: String = "",
    val dialogDescription: String = "",
    val userName: String = "",
    val pendingImageUri: Uri? = null
)
