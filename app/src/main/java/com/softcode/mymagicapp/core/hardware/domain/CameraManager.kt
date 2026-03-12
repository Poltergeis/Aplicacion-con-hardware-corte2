package com.softcode.mymagicapp.core.hardware.domain

import android.content.Context
import android.net.Uri

interface CameraManager {
    suspend fun takePicture(context: Context): Uri?
}