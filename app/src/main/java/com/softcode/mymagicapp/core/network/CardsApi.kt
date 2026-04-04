package com.softcode.mymagicapp.core.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CardsApi {
    @POST("login")
    suspend fun login(@Body body: LoginRequest): Response<AuthResponse>

    @POST("register")
    suspend fun register(@Body body: RegisterRequest): Response<AuthResponse>

    @GET("cards")
    suspend fun getCards(): Response<List<CardResponse>>

    @POST("cards")
    suspend fun postCard(@Body card: CardRequest): Response<CardResponse>

    @PUT("cards/{id}")
    suspend fun updateCard(@Path("id") id: Long, @Body card: CardRequest): Response<MessageResponse>

    @DELETE("cards/{id}")
    suspend fun deleteCard(@Path("id") id: Long): Response<MessageResponse>
}
