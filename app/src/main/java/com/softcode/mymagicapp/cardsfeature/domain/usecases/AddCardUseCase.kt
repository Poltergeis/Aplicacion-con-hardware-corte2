package com.softcode.mymagicapp.cardsfeature.domain.usecases

import android.content.Context
import android.net.Uri
import com.softcode.mymagicapp.cardsfeature.data.UploadCardImageUseCase
import com.softcode.mymagicapp.core.domain.repository.AuthRepository
import com.softcode.mymagicapp.core.domain.repository.CardRepository
import com.softcode.mymagicapp.core.domain.results.OperationResult

class AddCardUseCase (
    private val cardRepository: CardRepository,
    private val authRepository: AuthRepository,
    private val uploadCardImageUseCase: UploadCardImageUseCase
) {
    suspend operator fun invoke(
        title: String,
        description: String,
        imageUri: Uri?,
        context: Context
    ): OperationResult<Unit, String, String> {
        val userId = authRepository.user.value?.id
            ?: return OperationResult.Failure("No hay sesión activa")

        val imageUrl = if (imageUri != null) {
            when (val result = uploadCardImageUseCase(imageUri, context)) {
                is OperationResult.Success -> result.data
                is OperationResult.Failure -> return OperationResult.Failure(result.reason)
                is OperationResult.Error -> return OperationResult.Error(result.error)
            }
        } else ""

        return cardRepository.addCard(userId, title, description, imageUrl)
    }
}