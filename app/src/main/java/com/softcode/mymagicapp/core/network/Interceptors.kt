package com.softcode.mymagicapp.core.network

import okhttp3.Interceptor
import okhttp3.Response

class TokenHolder {
    @Volatile var token: String? = null
}

class AuthInterceptor(private val tokenHolder: TokenHolder) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token = tokenHolder.token
        return if (token != null) {
            chain.proceed(
                request.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            )
        } else {
            chain.proceed(request)
        }
    }
}
