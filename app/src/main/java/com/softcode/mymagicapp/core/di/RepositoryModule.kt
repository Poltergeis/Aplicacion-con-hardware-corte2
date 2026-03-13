package com.softcode.mymagicapp.core.di

import com.softcode.mymagicapp.core.data.repository.AuthRepositoryImpl
import com.softcode.mymagicapp.core.data.repository.CardRepositoryImpl
import com.softcode.mymagicapp.core.domain.repository.AuthRepository
import com.softcode.mymagicapp.core.domain.repository.CardRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindCardRepository(
        impl: CardRepositoryImpl
    ): CardRepository
}