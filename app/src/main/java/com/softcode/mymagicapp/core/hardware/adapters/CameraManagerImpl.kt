package com.softcode.mymagicapp.core.hardware.adapters

import android.content.Context
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.softcode.mymagicapp.core.hardware.domain.CameraManager
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class CameraManagerImpl @Inject constructor() : CameraManager {

    private var pendingUri: Uri? = null
    private var pendingContinuation: CancellableContinuation<Uri?>? = null

    private val launcher: ActivityResultLauncher<Uri>? = null

    override suspend fun takePicture(context: Context): Uri? {
        val activity = context as? ComponentActivity ?: return null

        val imageFile = File(
            context.cacheDir,
            "camera_${System.currentTimeMillis()}.jpg"
        )
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
        pendingUri = uri

        return suspendCancellableCoroutine { continuation ->
            pendingContinuation = continuation
            activity.activityResultRegistry
                .register(
                    "camera_capture_${System.currentTimeMillis()}",
                    ActivityResultContracts.TakePicture()
                ) { success ->
                    pendingContinuation = null
                    continuation.resume(if (success) uri else null)
                }
                .launch(uri)
        }
    }
}