package com.softcode.mymagicapp.core.domain.results

sealed interface AuthResult {
    class Success() : AuthResult
    class Error(val message: String): AuthResult
}