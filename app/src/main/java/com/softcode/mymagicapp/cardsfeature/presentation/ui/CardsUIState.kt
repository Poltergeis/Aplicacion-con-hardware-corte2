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
    val pendingImageUri: Uri? = null,
    val searchQuery: String = "",
    val favoriteCardIds: Set<Long> = emptySet(),
    val showOnlyFavorites: Boolean = false
) {
    val filteredCards: List<CardEntity>
        get() {
            var result = cards
            if (showOnlyFavorites) {
                result = result.filter { it.id in favoriteCardIds }
            }
            if (searchQuery.isNotBlank()) {
                val query = searchQuery.lowercase()
                result = result.filter {
                    it.title.lowercase().contains(query) ||
                        it.description.lowercase().contains(query)
                }
            }
            return result
        }
}
