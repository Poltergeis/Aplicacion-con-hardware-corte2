package com.softcode.mymagicapp.cardsfeature.di

import com.softcode.mymagicapp.cardsfeature.data.ImageUploaderImpl
import com.softcode.mymagicapp.cardsfeature.domain.ImageUploader
import com.softcode.mymagicapp.cardsfeature.domain.usecases.AddCardUseCase
import com.softcode.mymagicapp.cardsfeature.domain.usecases.DeleteCardUseCase
import com.softcode.mymagicapp.core.domain.usecases.GetCardsUseCase
import com.softcode.mymagicapp.cardsfeature.domain.usecases.LogoutUseCase
import com.softcode.mymagicapp.cardsfeature.domain.usecases.UpdateCardUseCase
import com.softcode.mymagicapp.core.domain.repository.AuthRepository
import com.softcode.mymagicapp.core.domain.repository.CardRepository
import com.softcode.mymagicapp.core.hardware.domain.LocationProvider
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
    fun provideImageUploader(impl: ImageUploaderImpl): ImageUploader = impl

    @Provides
    @Singleton
    fun provideAddCardUseCase(
        cardRepository: CardRepository,
        authRepository: AuthRepository,
        imageUploader: ImageUploader,
        locationProvider: LocationProvider
    ): AddCardUseCase {
        return AddCardUseCase(cardRepository, authRepository, imageUploader, locationProvider)
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
