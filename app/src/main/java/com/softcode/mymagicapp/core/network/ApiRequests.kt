package com.softcode.mymagicapp.core.network

data class LoginRequest(val username: String, val password: String)
data class RegisterRequest(val username: String, val password: String)
data class CardRequest(val title: String, val description: String)
