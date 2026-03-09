package com.softcode.mymagicapp.cardsfeature.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.softcode.mymagicapp.cardsfeature.presentation.ui.CardsEffect
import com.softcode.mymagicapp.cardsfeature.presentation.ui.CardsUIState
import com.softcode.mymagicapp.core.data.local.dao.UserDao
import com.softcode.mymagicapp.core.data.local.entity.CardEntity
import com.softcode.mymagicapp.core.data.repository.AuthRepository
import com.softcode.mymagicapp.core.data.repository.CardRepository
import com.softcode.mymagicapp.core.ui.base.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardsViewModel @Inject constructor(
    private val cardRepository: CardRepository,
    private val authRepository: AuthRepository,
    private val userDao: UserDao
) : BaseViewModel<CardsUIState, CardsEffect>(CardsUIState()) {

    private var currentUserId: Long = -1

    init {
        viewModelScope.launch {
            val userId = authRepository.currentUserId.first()
            if (userId == null) {
                sendEffect(CardsEffect.NavigateToLogin)
                return@launch
            }
            currentUserId = userId

            val user = userDao.getUserById(userId)
            setState { it.copy(userName = user?.name ?: "") }

            cardRepository.getCardsByUser(userId).collect { cards ->
                setState { it.copy(cards = cards, isLoading = false) }
            }
        }
    }

    fun onShowAddDialog() {
        setState { it.copy(showAddDialog = true, dialogTitle = "", dialogDescription = "") }
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

    fun onDialogTitleChanged(title: String) {
        setState { it.copy(dialogTitle = title) }
    }

    fun onDialogDescriptionChanged(description: String) {
        setState { it.copy(dialogDescription = description) }
    }

    fun onConfirmAdd() {
        val state = _uiState.value
        if (state.dialogTitle.isBlank()) return
        viewModelScope.launch {
            cardRepository.addCard(currentUserId, state.dialogTitle.trim(), state.dialogDescription.trim())
            onDismissDialog()
            sendEffect(CardsEffect.ShowMessage("Carta creada"))
        }
    }

    fun onConfirmEdit() {
        val state = _uiState.value
        val card = state.editingCard ?: return
        if (state.dialogTitle.isBlank()) return
        viewModelScope.launch {
            cardRepository.updateCard(
                card.copy(title = state.dialogTitle.trim(), description = state.dialogDescription.trim())
            )
            onDismissDialog()
            sendEffect(CardsEffect.ShowMessage("Carta actualizada"))
        }
    }

    fun onDeleteCard(card: CardEntity) {
        viewModelScope.launch {
            cardRepository.deleteCard(card)
            sendEffect(CardsEffect.ShowMessage("Carta eliminada"))
        }
    }

    fun onLogout() {
        viewModelScope.launch {
            authRepository.logout()
            sendEffect(CardsEffect.NavigateToLogin)
        }
    }
}
