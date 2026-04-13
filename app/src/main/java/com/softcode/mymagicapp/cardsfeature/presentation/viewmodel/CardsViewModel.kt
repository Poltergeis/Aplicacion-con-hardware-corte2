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
import com.softcode.mymagicapp.core.workers.SyncScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    private val cameraManager: CameraManager,
    @ApplicationContext private val context: Context
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
                dialogDescription = card.description,
                dialogPower = card.power,
                dialogDefense = card.defense,
                dialogRarity = card.rarity
            )
        }
    }

    fun onDialogTitleChanged(value: String) {
        setState { it.copy(dialogTitle = value) }
    }

    fun onDialogDescriptionChanged(value: String) {
        setState { it.copy(dialogDescription = value) }
    }

    fun onDialogPowerChanged(value: Int) {
        setState { it.copy(dialogPower = value) }
    }

    fun onDialogDefenseChanged(value: Int) {
        setState { it.copy(dialogDefense = value) }
    }

    fun onDialogRarityChanged(value: Int) {
        setState { it.copy(dialogRarity = value) }
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

        val updatedCard = card.copy(
            title = title,
            description = description,
            power = state.dialogPower,
            defense = state.dialogDefense,
            rarity = state.dialogRarity
        )
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
                is AuthResult.Success -> {
                    SyncScheduler.cancel(context)
                    sendEffect(CardsEffect.NavigateToLogin)
                }
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
        setState {
            it.copy(
                showAddDialog = true,
                dialogTitle = "",
                dialogDescription = "",
                dialogPower = 1,
                dialogDefense = 1,
                dialogRarity = 1,
                pendingImageUri = null
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
                dialogDescription = "",
                dialogPower = 1,
                dialogDefense = 1,
                dialogRarity = 1,
                pendingImageUri = null
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

        val imageUri = state.pendingImageUri
        val power = state.dialogPower
        val defense = state.dialogDefense
        val rarity = state.dialogRarity
        onDismissDialog()

        runAsync {
            when (val result = addCardUseCase(title, description, imageUri, power, defense, rarity)) {
                is OperationResult.Failure -> sendEffect(CardsEffect.ShowMessage(result.reason))
                is OperationResult.Error -> sendEffect(CardsEffect.ShowMessage(result.error))
                is OperationResult.Success -> Unit
            }
        }
    }

    fun onCameraPermissionDenied() {
        sendEffect(CardsEffect.ShowMessage("Se necesita permiso de cámara para tomar fotos"))
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
