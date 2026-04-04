package com.softcode.mymagicapp.cardsfeature.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.softcode.mymagicapp.cardsfeature.domain.usecases.AddCardUseCase
import com.softcode.mymagicapp.cardsfeature.domain.usecases.DeleteCardUseCase
import com.softcode.mymagicapp.cardsfeature.domain.usecases.GetCardsUseCase
import com.softcode.mymagicapp.cardsfeature.domain.usecases.LogoutUseCase
import com.softcode.mymagicapp.cardsfeature.domain.usecases.UpdateCardUseCase
import com.softcode.mymagicapp.cardsfeature.presentation.ui.CardsEffect
import com.softcode.mymagicapp.cardsfeature.presentation.ui.CardsUIState
import com.softcode.mymagicapp.core.domain.entities.CardEntity
import com.softcode.mymagicapp.core.domain.repository.AuthRepository
import com.softcode.mymagicapp.core.domain.results.AuthResult
import com.softcode.mymagicapp.core.domain.results.OperationResult
import com.softcode.mymagicapp.core.ui.base.viewmodel.BaseViewModel
import com.softcode.mymagicapp.core.ui.base.viewmodel.runAsync
import kotlinx.coroutines.launch

class CardsViewModel(
    private val getCardsUseCase: GetCardsUseCase,
    private val addCardUseCase: AddCardUseCase,
    private val updateCardUseCase: UpdateCardUseCase,
    private val deleteCardUseCase: DeleteCardUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val authRepository: AuthRepository
) : BaseViewModel<CardsUIState, CardsEffect>(CardsUIState()) {

    init {
        observeUser()
        loadCards()
    }

    private fun observeUser() {
        viewModelScope.launch {
            authRepository.user.collect { user ->
                setState { it.copy(userName = user?.username ?: "") }
            }
        }
    }

    fun loadCards() {
        launchWithState(
            loading = { isLoading -> _uiState.value.copy(isLoading = isLoading) }
        ) {
            when (val result = getCardsUseCase()) {
                is OperationResult.Success -> setState { it.copy(cards = result.data) }
                is OperationResult.Failure -> sendEffect(CardsEffect.ShowMessage(result.reason))
                is OperationResult.Error -> sendEffect(CardsEffect.ShowMessage(result.error))
            }
        }
    }

    fun onShowEditDialog(card: CardEntity) {
        setState {
            it.copy(
                showEditDialog = true,
                editingCard = card,
                dialogTitle = card.title,
                dialogDescription = card.description
            )
        }
    }

    fun onDialogTitleChanged(value: String) {
        setState { it.copy(dialogTitle = value) }
    }

    fun onDialogDescriptionChanged(value: String) {
        setState { it.copy(dialogDescription = value) }
    }

    fun onConfirmEdit() {
        val state = _uiState.value
        val card = state.editingCard ?: return
        val title = state.dialogTitle.trim()
        val description = state.dialogDescription.trim()

        if (title.isBlank()) {
            sendEffect(CardsEffect.ShowMessage("El título no puede estar vacío"))
            return
        }

        val updatedCard = card.copy(title = title, description = description)
        onDismissDialog()

        runAsync {
            when (val result = updateCardUseCase(updatedCard)) {
                is OperationResult.Success -> loadCards()
                is OperationResult.Failure -> sendEffect(CardsEffect.ShowMessage(result.reason))
                is OperationResult.Error -> sendEffect(CardsEffect.ShowMessage(result.error))
            }
        }
    }

    fun onDeleteCard(card: CardEntity) {
        runAsync {
            when (val result = deleteCardUseCase(card)) {
                is OperationResult.Success -> loadCards()
                is OperationResult.Failure -> sendEffect(CardsEffect.ShowMessage(result.reason))
                is OperationResult.Error -> sendEffect(CardsEffect.ShowMessage(result.error))
            }
        }
    }

    fun onLogout() {
        runAsync {
            when (logoutUseCase()) {
                is AuthResult.Success -> sendEffect(CardsEffect.NavigateToLogin)
                is AuthResult.Error -> sendEffect(CardsEffect.ShowMessage("Error al cerrar sesión"))
            }
        }
    }

    fun onShowAddDialog() {
        setState { it.copy(showAddDialog = true, dialogTitle = "", dialogDescription = "") }
    }

    fun onDismissDialog() {
        setState {
            it.copy(
                showAddDialog = false,
                showEditDialog = false,
                editingCard = null,
                dialogTitle = "",
                dialogDescription = ""
            )
        }
    }

    fun onConfirmAdd() {
        val state = _uiState.value
        val title = state.dialogTitle.trim()
        val description = state.dialogDescription.trim()

        if (title.isBlank()) {
            sendEffect(CardsEffect.ShowMessage("El título no puede estar vacío"))
            return
        }

        onDismissDialog()

        runAsync {
            when (val result = addCardUseCase(title, description)) {
                is OperationResult.Success -> loadCards()
                is OperationResult.Failure -> sendEffect(CardsEffect.ShowMessage(result.reason))
                is OperationResult.Error -> sendEffect(CardsEffect.ShowMessage(result.error))
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        setState { it.copy(searchQuery = query) }
    }

    fun onToggleFavorite(cardId: Long) {
        setState { state ->
            val newFavorites = if (cardId in state.favoriteCardIds) {
                state.favoriteCardIds - cardId
            } else {
                state.favoriteCardIds + cardId
            }
            state.copy(favoriteCardIds = newFavorites)
        }
    }

    fun onToggleShowOnlyFavorites() {
        setState { it.copy(showOnlyFavorites = !it.showOnlyFavorites) }
    }
}
