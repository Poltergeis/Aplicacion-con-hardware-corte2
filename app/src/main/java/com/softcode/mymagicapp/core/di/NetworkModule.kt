package com.softcode.mymagicapp.core.di

import com.softcode.mymagicapp.core.data.repository.AuthRepositoryImpl
import com.softcode.mymagicapp.core.data.repository.CardRepositoryImpl
import com.softcode.mymagicapp.core.data.session.SessionManager
import com.softcode.mymagicapp.core.domain.repository.CardRepository
import com.softcode.mymagicapp.core.network.CardsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofitClient(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(client)
            .baseUrl("")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(api: CardsApi, sessionManager: SessionManager): AuthRepositoryImpl {
        return AuthRepositoryImpl(sessionManager, api)
    }

    @Provides
    @Singleton
    fun provideCardsRepository(api: CardsApi): CardRepositoryImpl {
        return CardRepositoryImpl(api)
    }
}