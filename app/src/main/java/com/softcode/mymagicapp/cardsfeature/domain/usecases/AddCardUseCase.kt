package com.softcode.mymagicapp.cardsfeature.domain.usecases

import android.net.Uri
import com.softcode.mymagicapp.cardsfeature.domain.ImageUploader
import com.softcode.mymagicapp.core.domain.repository.AuthRepository
import com.softcode.mymagicapp.core.domain.repository.CardRepository
import com.softcode.mymagicapp.core.domain.results.OperationResult
import com.softcode.mymagicapp.core.hardware.domain.LocationProvider

class AddCardUseCase(
    private val cardRepository: CardRepository,
    private val authRepository: AuthRepository,
    private val imageUploader: ImageUploader,
    private val locationProvider: LocationProvider
) {
    suspend operator fun invoke(
        title: String,
        description: String,
        imageUri: Uri?,
        power: Int = 1,
        defense: Int = 1,
        rarity: Int = 1,
        latitude: Double? = null,
        longitude: Double? = null
    ): OperationResult<Unit, String, String> {
        val userId = authRepository.user.value?.id
            ?: return OperationResult.Failure("No hay sesión activa")

        val imageUrl = if (imageUri != null) {
            when (val result = imageUploader.upload(imageUri)) {
                is OperationResult.Success -> result.data
                is OperationResult.Failure -> return OperationResult.Failure(result.reason)
                is OperationResult.Error -> return OperationResult.Error(result.error)
            }
        } else ""

        // Capture GPS location — null if permission not granted or unavailable
        val location = locationProvider.getCurrentLocation()

        return cardRepository.addCard(
            userId, title, description, imageUrl,
            power, defense, rarity,
            latitude = location?.latitude ?: latitude,
            longitude = location?.longitude ?: longitude
        )
    }
}
