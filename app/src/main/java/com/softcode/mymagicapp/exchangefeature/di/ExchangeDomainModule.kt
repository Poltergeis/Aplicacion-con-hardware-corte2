package com.softcode.mymagicapp.exchangefeature.di

import com.softcode.mymagicapp.core.domain.repository.AuthRepository
import com.softcode.mymagicapp.core.domain.repository.CardRepository
import com.softcode.mymagicapp.core.domain.repository.ExchangeRepository
import com.softcode.mymagicapp.exchangefeature.domain.usecases.CreateExchangeUseCase
import com.softcode.mymagicapp.exchangefeature.domain.usecases.GetExchangesUseCase
import com.softcode.mymagicapp.exchangefeature.domain.usecases.GetUserCardsUseCase
import com.softcode.mymagicapp.exchangefeature.domain.usecases.GetUsersUseCase
import com.softcode.mymagicapp.exchangefeature.domain.usecases.RespondExchangeUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ExchangeDomainModule {

    @Provides
    @Singleton
    fun provideGetExchangesUseCase(
        exchangeRepository: ExchangeRepository,
        authRepository: AuthRepository
    ): GetExchangesUseCase {
        return GetExchangesUseCase(exchangeRepository, authRepository)
    }

    @Provides
    @Singleton
    fun provideCreateExchangeUseCase(
        exchangeRepository: ExchangeRepository,
        authRepository: AuthRepository
    ): CreateExchangeUseCase {
        return CreateExchangeUseCase(exchangeRepository, authRepository)
    }

    @Provides
    @Singleton
    fun provideRespondExchangeUseCase(
        exchangeRepository: ExchangeRepository
    ): RespondExchangeUseCase {
        return RespondExchangeUseCase(exchangeRepository)
    }

    @Provides
    @Singleton
    fun provideGetUsersUseCase(
        exchangeRepository: ExchangeRepository
    ): GetUsersUseCase {
        return GetUsersUseCase(exchangeRepository)
    }

    @Provides
    @Singleton
    fun provideGetUserCardsUseCase(
        cardRepository: CardRepository
    ): GetUserCardsUseCase {
        return GetUserCardsUseCase(cardRepository)
    }
}
