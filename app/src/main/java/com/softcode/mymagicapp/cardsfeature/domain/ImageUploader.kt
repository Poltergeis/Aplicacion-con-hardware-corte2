package com.softcode.mymagicapp.cardsfeature.domain

import android.net.Uri
import com.softcode.mymagicapp.core.domain.results.OperationResult

interface ImageUploader {
    suspend fun upload(imageUri: Uri): OperationResult<String, String, String>
}
