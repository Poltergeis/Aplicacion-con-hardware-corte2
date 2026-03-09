package com.softcode.mymagicapp.core.di

import android.content.Context
import androidx.room.Room
import com.softcode.mymagicapp.core.data.local.AppDatabase
import com.softcode.mymagicapp.core.data.local.dao.CardDao
import com.softcode.mymagicapp.core.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "mymagicapp_database"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()

    @Provides
    fun provideCardDao(database: AppDatabase): CardDao = database.cardDao()
}
