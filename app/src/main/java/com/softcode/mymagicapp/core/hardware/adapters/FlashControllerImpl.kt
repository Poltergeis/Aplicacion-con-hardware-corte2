package com.softcode.mymagicapp.core.hardware.adapters

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import com.softcode.mymagicapp.core.hardware.domain.FlashController
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlashControllerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : FlashController {

    private val cameraManager =
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    private val _isFlashOn = MutableStateFlow(false)
    override val isFlashOn: StateFlow<Boolean> = _isFlashOn.asStateFlow()

    private val torchCameraId: String? by lazy {
        cameraManager.cameraIdList.firstOrNull { id ->
            cameraManager.getCameraCharacteristics(id)
                .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        }
    }

    override fun turnOn() {
        torchCameraId?.let {
            try {
                cameraManager.setTorchMode(it, true)
                _isFlashOn.value = true
            } catch (_: Exception) { }
        }
    }

    override fun turnOff() {
        torchCameraId?.let {
            try {
                cameraManager.setTorchMode(it, false)
                _isFlashOn.value = false
            } catch (_: Exception) { }
        }
    }

    override fun release() = turnOff()
}
