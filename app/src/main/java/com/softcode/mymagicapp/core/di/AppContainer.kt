package com.softcode.mymagicapp.core.di

import com.softcode.mymagicapp.core.data.repository.AuthRepositoryImpl
import com.softcode.mymagicapp.core.data.repository.CardRepositoryImpl
import com.softcode.mymagicapp.core.domain.repository.AuthRepository
import com.softcode.mymagicapp.core.domain.repository.CardRepository
import com.softcode.mymagicapp.core.network.AuthInterceptor
import com.softcode.mymagicapp.core.network.CardsApi
import com.softcode.mymagicapp.core.network.TokenHolder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AppContainer {

    private val tokenHolder = TokenHolder()

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(tokenHolder))
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.121.56.4:3000/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: CardsApi = retrofit.create(CardsApi::class.java)

    val authRepository: AuthRepository = AuthRepositoryImpl(api, tokenHolder)
    val cardRepository: CardRepository = CardRepositoryImpl(api)
}
