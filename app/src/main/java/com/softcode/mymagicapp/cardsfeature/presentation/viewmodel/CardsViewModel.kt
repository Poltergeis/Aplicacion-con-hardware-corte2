package com.softcode.mymagicapp.cardsfeature.presentation.viewmodel

import android.content.Context
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
import com.softcode.mymagicapp.core.hardware.domain.CameraManager
import com.softcode.mymagicapp.core.ui.base.viewmodel.BaseViewModel
import com.softcode.mymagicapp.core.ui.base.viewmodel.runAsync
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardsViewModel @Inject constructor(
    private val getCardsUseCase: GetCardsUseCase,
    private val addCardUseCase: AddCardUseCase,
    private val updateCardUseCase: UpdateCardUseCase,
    private val deleteCardUseCase: DeleteCardUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val authRepository: AuthRepository,
    private val cameraManager: CameraManager
) : BaseViewModel<CardsUIState, CardsEffect>(CardsUIState()) {

    init {
        observeCards()
        observeUser()
        loadCards()
    }

    private fun observeCards() {
        viewModelScope.launch {
            getCardsUseCase.cards.collect { cards ->
                setState { it.copy(cards = cards) }
            }
        }
    }

    private fun observeUser() {
        viewModelScope.launch {
            authRepository.user.collect { user ->
                setState { it.copy(userName = user?.username ?: "") }
            }
        }
    }

    private fun loadCards() {
        launchWithState(
            loading = { isLoading -> _uiState.value.copy(isLoading = isLoading) }
        ) {
            when (val result = getCardsUseCase()) {
                is OperationResult.Failure -> sendEffect(CardsEffect.ShowMessage(result.reason))
                is OperationResult.Error -> sendEffect(CardsEffect.ShowMessage(result.error))
                is OperationResult.Success -> Unit
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
                is OperationResult.Failure -> sendEffect(CardsEffect.ShowMessage(result.reason))
                is OperationResult.Error -> sendEffect(CardsEffect.ShowMessage(result.error))
                is OperationResult.Success -> Unit
            }
        }
    }

    fun onDeleteCard(card: CardEntity) {
        runAsync {
            when (val result = deleteCardUseCase(card)) {
                is OperationResult.Failure -> sendEffect(CardsEffect.ShowMessage(result.reason))
                is OperationResult.Error -> sendEffect(CardsEffect.ShowMessage(result.error))
                is OperationResult.Success -> Unit
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

    fun onTakePicture(context: Context) {
        runAsync {
            val uri = cameraManager.takePicture(context)
            setState { it.copy(pendingImageUri = uri) }
        }
    }

    fun onShowAddDialog() {
        setState { it.copy(showAddDialog = true, dialogTitle = "", dialogDescription = "", pendingImageUri = null) }
    }

    fun onDismissDialog() {
        setState {
            it.copy(
                showAddDialog = false,
                showEditDialog = false,
                editingCard = null,
                dialogTitle = "",
                dialogDescription = "",
                pendingImageUri = null
            )
        }
    }

    fun onConfirmAdd(context: Context) {
        val state = _uiState.value
        val title = state.dialogTitle.trim()
        val description = state.dialogDescription.trim()

        if (title.isBlank()) {
            sendEffect(CardsEffect.ShowMessage("El título no puede estar vacío"))
            return
        }

        val imageUri = state.pendingImageUri
        onDismissDialog()

        runAsync {
            when (val result = addCardUseCase(title, description, imageUri, context)) {
                is OperationResult.Failure -> sendEffect(CardsEffect.ShowMessage(result.reason))
                is OperationResult.Error -> sendEffect(CardsEffect.ShowMessage(result.error))
                is OperationResult.Success -> Unit
            }
        }
    }
}
