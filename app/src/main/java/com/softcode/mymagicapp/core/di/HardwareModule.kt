package com.softcode.mymagicapp.core.di

import com.softcode.mymagicapp.core.hardware.adapters.CameraManagerImpl
import com.softcode.mymagicapp.core.hardware.domain.CameraManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HardwareModule {
    @Binds
    @Singleton
    abstract fun bindCameraManager(
        impl: CameraManagerImpl
    ): CameraManager
}