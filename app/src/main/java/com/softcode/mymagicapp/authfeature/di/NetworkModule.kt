package com.softcode.mymagicapp.authfeature.di

import com.softcode.mymagicapp.authfeature.domain.usecases.LoadLoggedUserUseCase
import com.softcode.mymagicapp.authfeature.domain.usecases.LoginUseCase
import com.softcode.mymagicapp.authfeature.domain.usecases.RegisterUseCase
import com.softcode.mymagicapp.core.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideLoginUseCase(repository: AuthRepository): LoginUseCase {
        return LoginUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideRegisterUseCase(repository: AuthRepository): RegisterUseCase {
        return RegisterUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideLoadLoggedUserUseCase(repository: AuthRepository): LoadLoggedUserUseCase {
        return LoadLoggedUserUseCase(repository)
    }
}