package com.softcode.mymagicapp.core.di

import com.softcode.mymagicapp.core.hardware.adapters.CameraManagerImpl
import com.softcode.mymagicapp.core.hardware.adapters.FlashControllerImpl
import com.softcode.mymagicapp.core.hardware.domain.CameraManager
import com.softcode.mymagicapp.core.hardware.domain.FlashController
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HardwareModule {

    @Binds
    @Singleton
    abstract fun bindCameraManager(impl: CameraManagerImpl): CameraManager

    @Binds
    @Singleton
    abstract fun bindFlashController(impl: FlashControllerImpl): FlashController
}