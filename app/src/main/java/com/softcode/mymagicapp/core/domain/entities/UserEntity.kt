package com.softcode.mymagicapp.core.domain.entities

data class UserEntity(
    val id: Long,
    val username: String,
    val password: String?
)