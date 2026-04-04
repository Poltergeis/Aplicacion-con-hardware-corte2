package com.softcode.mymagicapp.cardsfeature.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.softcode.mymagicapp.cardsfeature.domain.usecases.AddCardUseCase
import com.softcode.mymagicapp.cardsfeature.domain.usecases.DeleteCardUseCase
import com.softcode.mymagicapp.cardsfeature.domain.usecases.GetCardsUseCase
import com.softcode.mymagicapp.cardsfeature.domain.usecases.LogoutUseCase
import com.softcode.mymagicapp.cardsfeature.domain.usecases.UpdateCardUseCase
import com.softcode.mymagicapp.cardsfeature.presentation.viewmodel.CardsViewModel
import com.softcode.mymagicapp.core.di.AppContainer

class CardsModule(private val appContainer: AppContainer) {

    val cardsViewModelFactory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            CardsViewModel(
                GetCardsUseCase(appContainer.cardRepository, appContainer.authRepository),
                AddCardUseCase(appContainer.cardRepository, appContainer.authRepository),
                UpdateCardUseCase(appContainer.cardRepository),
                DeleteCardUseCase(appContainer.cardRepository),
                LogoutUseCase(appContainer.authRepository),
                appContainer.authRepository
            ) as T
    }
}
