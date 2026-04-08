package com.softcode.mymagicapp.exchangefeature.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.softcode.mymagicapp.cardsfeature.domain.usecases.GetCardsUseCase
import com.softcode.mymagicapp.core.domain.repository.AuthRepository
import com.softcode.mymagicapp.core.domain.results.OperationResult
import com.softcode.mymagicapp.core.ui.base.viewmodel.BaseViewModel
import com.softcode.mymagicapp.core.ui.base.viewmodel.runAsync
import com.softcode.mymagicapp.exchangefeature.domain.usecases.CreateExchangeUseCase
import com.softcode.mymagicapp.exchangefeature.domain.usecases.GetExchangesUseCase
import com.softcode.mymagicapp.exchangefeature.domain.usecases.RespondExchangeUseCase
import com.softcode.mymagicapp.exchangefeature.presentation.ui.ExchangeEffect
import com.softcode.mymagicapp.exchangefeature.presentation.ui.ExchangeFilterTab
import com.softcode.mymagicapp.exchangefeature.presentation.ui.ExchangeUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExchangeViewModel @Inject constructor(
    private val getExchangesUseCase: GetExchangesUseCase,
    private val createExchangeUseCase: CreateExchangeUseCase,
    private val respondExchangeUseCase: RespondExchangeUseCase,
    private val getCardsUseCase: GetCardsUseCase,
    private val authRepository: AuthRepository
) : BaseViewModel<ExchangeUIState, ExchangeEffect>(ExchangeUIState()) {

    init {
        observeUser()
        observeExchanges()
        observeMyCards()
        loadExchanges()
    }

    private fun observeUser() {
        viewModelScope.launch {
            authRepository.user.collect { user ->
                setState { it.copy(currentUserId = user?.id ?: 0) }
            }
        }
    }

    private fun observeExchanges() {
        viewModelScope.launch {
            getExchangesUseCase.exchanges.collect { exchanges ->
                setState { it.copy(exchanges = exchanges) }
            }
        }
    }

    private fun observeMyCards() {
        viewModelScope.launch {
            getCardsUseCase.cards.collect { cards ->
                setState { it.copy(myCards = cards) }
            }
        }
    }

    private fun loadExchanges() {
        launchWithState(
            loading = { isLoading -> _uiState.value.copy(isLoading = isLoading) }
        ) {
            when (val result = getExchangesUseCase()) {
                is OperationResult.Failure -> sendEffect(ExchangeEffect.ShowMessage(result.reason))
                is OperationResult.Error -> sendEffect(ExchangeEffect.ShowMessage(result.error))
                is OperationResult.Success -> Unit
            }
        }
    }

    fun onRefresh() {
        loadExchanges()
    }

    fun onFilterTabChanged(tab: ExchangeFilterTab) {
        setState { it.copy(filterTab = tab) }
    }

    // ── Create Dialog ────────────────────────────────────────────────────────

    fun onShowCreateDialog() {
        setState {
            it.copy(
                showCreateDialog = true,
                receiverIdText = "",
                selectedMyCard = null,
                receiverCardIdText = ""
            )
        }
    }

    fun onDismissCreateDialog() {
        setState {
            it.copy(
                showCreateDialog = false,
                receiverIdText = "",
                selectedMyCard = null,
                receiverCardIdText = ""
            )
        }
    }

    fun onReceiverIdChanged(value: String) {
        setState { it.copy(receiverIdText = value) }
    }

    fun onReceiverCardIdChanged(value: String) {
        setState { it.copy(receiverCardIdText = value) }
    }

    fun onMyCardSelected(card: com.softcode.mymagicapp.core.domain.entities.CardEntity) {
        setState { it.copy(selectedMyCard = card) }
    }

    fun onConfirmCreate() {
        val state = _uiState.value
        val receiverId = state.receiverIdText.trim().toLongOrNull()
        val receiverCardId = state.receiverCardIdText.trim().toLongOrNull()
        val myCard = state.selectedMyCard

        if (receiverId == null) {
            sendEffect(ExchangeEffect.ShowMessage("ID de usuario receptor invalido"))
            return
        }
        if (myCard == null) {
            sendEffect(ExchangeEffect.ShowMessage("Selecciona una carta tuya para ofrecer"))
            return
        }
        if (receiverCardId == null) {
            sendEffect(ExchangeEffect.ShowMessage("ID de carta del receptor invalido"))
            return
        }
        if (receiverId == state.currentUserId) {
            sendEffect(ExchangeEffect.ShowMessage("No puedes intercambiar contigo mismo"))
            return
        }

        onDismissCreateDialog()

        runAsync {
            when (val result = createExchangeUseCase(receiverId, myCard.id, receiverCardId)) {
                is OperationResult.Failure -> sendEffect(ExchangeEffect.ShowMessage(result.reason))
                is OperationResult.Error -> sendEffect(ExchangeEffect.ShowMessage(result.error))
                is OperationResult.Success -> {
                    sendEffect(ExchangeEffect.ShowMessage("Intercambio propuesto exitosamente"))
                    sendEffect(ExchangeEffect.ExchangeCreated)
                    loadExchanges()
                }
            }
        }
    }

    // ── Respond ──────────────────────────────────────────────────────────────

    fun onAcceptExchange(exchangeId: Long) {
        runAsync {
            when (val result = respondExchangeUseCase(exchangeId, accept = true)) {
                is OperationResult.Failure -> sendEffect(ExchangeEffect.ShowMessage(result.reason))
                is OperationResult.Error -> sendEffect(ExchangeEffect.ShowMessage(result.error))
                is OperationResult.Success -> {
                    sendEffect(ExchangeEffect.ShowMessage("Intercambio aceptado"))
                    loadExchanges()
                }
            }
        }
    }

    fun onRejectExchange(exchangeId: Long) {
        runAsync {
            when (val result = respondExchangeUseCase(exchangeId, accept = false)) {
                is OperationResult.Failure -> sendEffect(ExchangeEffect.ShowMessage(result.reason))
                is OperationResult.Error -> sendEffect(ExchangeEffect.ShowMessage(result.error))
                is OperationResult.Success -> {
                    sendEffect(ExchangeEffect.ShowMessage("Intercambio rechazado"))
                    loadExchanges()
                }
            }
        }
    }
}
