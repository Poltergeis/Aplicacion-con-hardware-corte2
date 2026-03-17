package com.softcode.mymagicapp.cardsfeature.di

import com.softcode.mymagicapp.cardsfeature.data.UploadCardImageUseCase
import com.softcode.mymagicapp.cardsfeature.domain.usecases.AddCardUseCase
import com.softcode.mymagicapp.cardsfeature.domain.usecases.DeleteCardUseCase
import com.softcode.mymagicapp.cardsfeature.domain.usecases.GetCardsUseCase
import com.softcode.mymagicapp.cardsfeature.domain.usecases.LogoutUseCase
import com.softcode.mymagicapp.cardsfeature.domain.usecases.UpdateCardUseCase
import com.softcode.mymagicapp.core.domain.repository.AuthRepository
import com.softcode.mymagicapp.core.domain.repository.CardRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {
    @Provides
    @Singleton
    fun provideAddCardUseCase(cardRepository: CardRepository, authRepository: AuthRepository, uploadCardImageUseCase: UploadCardImageUseCase): AddCardUseCase {
        return AddCardUseCase(cardRepository, authRepository, uploadCardImageUseCase)
    }

    @Provides
    @Singleton
    fun provideDeleteCardUseCase(repository: CardRepository): DeleteCardUseCase {
        return DeleteCardUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetCardsUseCase(repository: CardRepository, authRepository: AuthRepository): GetCardsUseCase {
        return GetCardsUseCase(repository, authRepository)
    }

    @Provides
    @Singleton
    fun provideLogoutUseCase(repository: AuthRepository): LogoutUseCase {
        return LogoutUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideUpdateCardUseCase(repository: CardRepository): UpdateCardUseCase {
        return UpdateCardUseCase(repository)
    }
}