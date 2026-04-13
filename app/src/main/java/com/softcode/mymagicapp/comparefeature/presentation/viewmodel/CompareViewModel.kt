package com.softcode.mymagicapp.comparefeature.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.softcode.mymagicapp.core.domain.usecases.GetCardsUseCase
import com.softcode.mymagicapp.comparefeature.presentation.ui.CompareEffect
import com.softcode.mymagicapp.comparefeature.presentation.ui.CompareUIState
import com.softcode.mymagicapp.core.domain.entities.CardEntity
import com.softcode.mymagicapp.core.domain.results.OperationResult
import com.softcode.mymagicapp.core.ui.base.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompareViewModel @Inject constructor(
    private val getCardsUseCase: GetCardsUseCase
) : BaseViewModel<CompareUIState, CompareEffect>(CompareUIState()) {

    init {
        observeCards()
        loadCards()
    }

    private fun observeCards() {
        viewModelScope.launch {
            getCardsUseCase.cards.collect { cards ->
                setState { it.copy(allCards = cards) }
            }
        }
    }

    private fun loadCards() {
        launchWithState(
            loading = { isLoading -> _uiState.value.copy(isLoading = isLoading) }
        ) {
            when (val result = getCardsUseCase()) {
                is OperationResult.Failure -> sendEffect(CompareEffect.ShowMessage(result.reason))
                is OperationResult.Error -> sendEffect(CompareEffect.ShowMessage(result.error))
                is OperationResult.Success -> Unit
            }
        }
    }

    fun onShowSelectDialog() {
        setState { it.copy(showSelectDialog = true, searchQuery = "") }
    }

    fun onDismissSelectDialog() {
        setState { it.copy(showSelectDialog = false, searchQuery = "") }
    }

    fun onSearchQueryChanged(query: String) {
        setState { it.copy(searchQuery = query) }
    }

    fun onAddCardToCompare(card: CardEntity) {
        setState { state ->
            if (state.selectedCards.any { it.id == card.id }) {
                return@setState state
            }
            state.copy(
                selectedCards = state.selectedCards + card,
                showSelectDialog = false,
                searchQuery = ""
            )
        }
    }

    fun onRemoveCardFromCompare(card: CardEntity) {
        setState { state ->
            state.copy(selectedCards = state.selectedCards.filter { it.id != card.id })
        }
    }

    fun onClearAll() {
        setState { it.copy(selectedCards = emptyList()) }
    }
}
