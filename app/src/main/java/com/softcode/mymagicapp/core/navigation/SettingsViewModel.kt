package com.softcode.mymagicapp.core.navigation

import androidx.lifecycle.ViewModel
import com.softcode.mymagicapp.core.hardware.domain.FlashController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val flashController: FlashController
) : ViewModel() {

    val isFlashOn: StateFlow<Boolean> = flashController.isFlashOn

    fun setFlash(enabled: Boolean) {
        if (enabled) flashController.turnOn() else flashController.turnOff()
    }

    fun turnOffFlash() = flashController.turnOff()

    override fun onCleared() {
        super.onCleared()
        flashController.release()
    }
}
