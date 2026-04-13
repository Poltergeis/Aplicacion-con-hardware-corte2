package com.softcode.mymagicapp.cardsfeature.data

import android.content.Context
import android.net.Uri
import com.softcode.mymagicapp.cardsfeature.domain.ImageUploader
import com.softcode.mymagicapp.core.domain.results.OperationResult
import com.softcode.mymagicapp.core.network.CardsApi
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageUploaderImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: CardsApi
) : ImageUploader {

    override suspend fun upload(imageUri: Uri): OperationResult<String, String, String> {
        return try {
            val stream = context.contentResolver.openInputStream(imageUri)
                ?: return OperationResult.Failure("No se pudo leer la imagen")

            val bytes = stream.readBytes()
            stream.close()

            val requestBody = bytes.toRequestBody("image/jpeg".toMediaType())
            val part = MultipartBody.Part.createFormData("image", "photo.jpg", requestBody)

            val response = api.uploadImage(part)
            when {
                response.isSuccessful -> {
                    val url = response.body()?.imageUrl
                        ?: return OperationResult.Failure("Respuesta vacía del servidor")
                    OperationResult.Success(url)
                }
                response.code() in 400..499 ->
                    OperationResult.Failure(response.errorBody()?.string() ?: "Error de negocio")
                else -> OperationResult.Error("Error del servidor: ${response.code()}")
            }
        } catch (e: Exception) {
            OperationResult.Error(e.message ?: "Error desconocido")
        }
    }
}
