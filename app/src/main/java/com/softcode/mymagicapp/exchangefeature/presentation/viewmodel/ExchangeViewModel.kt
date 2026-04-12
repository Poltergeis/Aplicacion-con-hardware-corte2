package com.softcode.mymagicapp.exchangefeature.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.softcode.mymagicapp.cardsfeature.domain.usecases.GetCardsUseCase
import com.softcode.mymagicapp.core.domain.entities.CardEntity
import com.softcode.mymagicapp.core.domain.repository.AuthRepository
import com.softcode.mymagicapp.core.domain.results.OperationResult
import com.softcode.mymagicapp.core.network.UserModel
import com.softcode.mymagicapp.core.ui.base.viewmodel.BaseViewModel
import com.softcode.mymagicapp.core.ui.base.viewmodel.runAsync
import com.softcode.mymagicapp.exchangefeature.domain.usecases.CreateExchangeUseCase
import com.softcode.mymagicapp.exchangefeature.domain.usecases.GetExchangesUseCase
import com.softcode.mymagicapp.exchangefeature.domain.usecases.GetUserCardsUseCase
import com.softcode.mymagicapp.exchangefeature.domain.usecases.GetUsersUseCase
import com.softcode.mymagicapp.exchangefeature.domain.usecases.RespondExchangeUseCase
import com.softcode.mymagicapp.exchangefeature.presentation.ui.ExchangeEffect
import com.softcode.mymagicapp.exchangefeature.presentation.ui.ExchangeFilterTab
import com.softcode.mymagicapp.exchangefeature.presentation.ui.ExchangeStep
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
    private val getUsersUseCase: GetUsersUseCase,
    private val getUserCardsUseCase: GetUserCardsUseCase,
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

    fun onRefresh() = loadExchanges()

    fun onFilterTabChanged(tab: ExchangeFilterTab) {
        setState { it.copy(filterTab = tab) }
    }

    // ── Create Dialog — Stepper ──────────────────────────────────────────────

    fun onShowCreateDialog() {
        setState {
            it.copy(
                showCreateDialog = true,
                createStep = ExchangeStep.SELECT_USER,
                userSearchQuery = "",
                availableUsers = emptyList(),
                selectedUser = null,
                myCardSearchQuery = "",
                selectedMyCard = null,
                receiverCards = emptyList(),
                selectedReceiverCard = null
            )
        }
        loadUsers()
    }

    fun onDismissCreateDialog() {
        setState { it.copy(showCreateDialog = false) }
    }

    fun onStepBack() {
        val prev = when (_uiState.value.createStep) {
            ExchangeStep.SELECT_MY_CARD -> ExchangeStep.SELECT_USER
            ExchangeStep.SELECT_RECEIVER_CARD -> ExchangeStep.SELECT_MY_CARD
            ExchangeStep.SELECT_USER -> ExchangeStep.SELECT_USER
        }
        setState { it.copy(createStep = prev) }
    }

    // ── Step 1: User selection ───────────────────────────────────────────────

    private fun loadUsers() {
        runAsync {
            setState { it.copy(isLoadingUsers = true) }
            when (val result = getUsersUseCase()) {
                is OperationResult.Success -> setState {
                    it.copy(availableUsers = result.data, isLoadingUsers = false)
                }
                is OperationResult.Failure -> {
                    setState { it.copy(isLoadingUsers = false) }
                    sendEffect(ExchangeEffect.ShowMessage(result.reason))
                }
                is OperationResult.Error -> {
                    setState { it.copy(isLoadingUsers = false) }
                    sendEffect(ExchangeEffect.ShowMessage(result.error))
                }
            }
        }
    }

    fun onUserSearchChanged(query: String) {
        setState { it.copy(userSearchQuery = query) }
    }

    fun onUserSelected(user: UserModel) {
        if (user.id == _uiState.value.currentUserId) {
            sendEffect(ExchangeEffect.ShowMessage("No puedes intercambiar contigo mismo"))
            return
        }
        setState { it.copy(selectedUser = user, createStep = ExchangeStep.SELECT_MY_CARD) }
    }

    // ── Step 2: My card selection ────────────────────────────────────────────

    fun onMyCardSearchChanged(query: String) {
        setState { it.copy(myCardSearchQuery = query) }
    }

    fun onMyCardSelected(card: CardEntity) {
        setState { it.copy(selectedMyCard = card, createStep = ExchangeStep.SELECT_RECEIVER_CARD) }
        loadReceiverCards()
    }

    // ── Step 3: Receiver card selection ─────────────────────────────────────

    private fun loadReceiverCards() {
        val userId = _uiState.value.selectedUser?.id ?: return
        runAsync {
            setState { it.copy(isLoadingReceiverCards = true) }
            when (val result = getUserCardsUseCase(userId)) {
                is OperationResult.Success -> setState {
                    it.copy(receiverCards = result.data, isLoadingReceiverCards = false)
                }
                is OperationResult.Failure -> {
                    setState { it.copy(isLoadingReceiverCards = false) }
                    sendEffect(ExchangeEffect.ShowMessage(result.reason))
                }
                is OperationResult.Error -> {
                    setState { it.copy(isLoadingReceiverCards = false) }
                    sendEffect(ExchangeEffect.ShowMessage(result.error))
                }
            }
        }
    }

    fun onReceiverCardSelected(card: CardEntity) {
        setState { it.copy(selectedReceiverCard = card) }
    }

    fun onConfirmCreate() {
        val state = _uiState.value
        val receiver = state.selectedUser ?: return
        val myCard = state.selectedMyCard ?: return
        val receiverCard = state.selectedReceiverCard ?: run {
            sendEffect(ExchangeEffect.ShowMessage("Selecciona una carta del receptor"))
            return
        }

        onDismissCreateDialog()

        runAsync {
            when (val result = createExchangeUseCase(receiver.id, myCard.id, receiverCard.id)) {
                is OperationResult.Failure -> sendEffect(ExchangeEffect.ShowMessage(result.reason))
                is OperationResult.Error -> sendEffect(ExchangeEffect.ShowMessage(result.error))
                is OperationResult.Success -> {
                    sendEffect(ExchangeEffect.ShowMessage("Intercambio propuesto a ${receiver.username}"))
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
