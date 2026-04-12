package com.softcode.mymagicapp.core.network

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface CardsApi {
    @POST("login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

    @POST("register")
    suspend fun register(@Body body: RegisterRequest): Response<LoginResponse>

    @POST("validate/token")
    suspend fun validateCurrentToken(@Body body: VerifyLoggedUserRequest): Response<LoginResponse>

    @GET("cards")
    suspend fun getCards(): Response<List<CardModel>>

    @POST("cards")
    suspend fun postCard(@Body card: CardModel): Response<CardModel>

    @PUT("cards")
    suspend fun updateCard(@Body card: CardModel): Response<ApiResponse>

    @HTTP(method = "DELETE", path = "cards", hasBody = true)
    suspend fun deleteCard(@Body card: CardModel): Response<ApiResponse>

    @Multipart
    @POST("cards/image")
    suspend fun uploadImage(@Part image: MultipartBody.Part): Response<ImageUploadResponse>

    // ── FCM ──────────────────────────────────────────────────────────────────
    @POST("fcm-token")
    suspend fun updateFcmToken(@Body body: UpdateFcmTokenRequest): Response<ApiResponse>

    // ── Users ────────────────────────────────────────────────────────────────
    @GET("users")
    suspend fun getUsers(@Query("search") search: String = ""): Response<List<UserModel>>

    @GET("cards/user/{userId}")
    suspend fun getCardsByUser(@retrofit2.http.Path("userId") userId: Long): Response<List<CardModel>>

    // ── Exchanges ────────────────────────────────────────────────────────────
    @GET("exchanges")
    suspend fun getExchanges(): Response<List<ExchangeModel>>

    @POST("exchanges")
    suspend fun createExchange(@Body body: CreateExchangeRequest): Response<ExchangeModel>

    @PUT("exchanges")
    suspend fun respondExchange(@Body body: RespondExchangeRequest): Response<ApiResponse>
}