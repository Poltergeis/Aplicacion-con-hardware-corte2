package com.softcode.mymagicapp.comparefeature.presentation.ui

import com.softcode.mymagicapp.core.domain.entities.CardEntity

data class CompareUIState(
    val allCards: List<CardEntity> = emptyList(),
    val selectedCards: List<CardEntity> = emptyList(),
    val isLoading: Boolean = true,
    val showSelectDialog: Boolean = false,
    val searchQuery: String = ""
) {
    val availableCards: List<CardEntity>
        get() {
            val selectedIds = selectedCards.map { it.id }.toSet()
            var result = allCards.filter { it.id !in selectedIds }
            if (searchQuery.isNotBlank()) {
                val query = searchQuery.lowercase()
                result = result.filter {
                    it.title.lowercase().contains(query) ||
                        it.description.lowercase().contains(query)
                }
            }
            return result
        }

    val canCompare: Boolean get() = selectedCards.size >= 2
}
