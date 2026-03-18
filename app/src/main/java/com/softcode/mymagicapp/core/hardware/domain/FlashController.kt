package com.softcode.mymagicapp.core.hardware.domain

import kotlinx.coroutines.flow.StateFlow

interface FlashController {
    val isFlashOn: StateFlow<Boolean>
    fun turnOn()
    fun turnOff()
    fun release()
}
