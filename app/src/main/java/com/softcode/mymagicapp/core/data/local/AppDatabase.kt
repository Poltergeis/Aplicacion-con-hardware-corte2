package com.softcode.mymagicapp.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.softcode.mymagicapp.core.data.local.dao.CardDao
import com.softcode.mymagicapp.core.data.local.dao.UserDao
import com.softcode.mymagicapp.core.data.local.entity.CardEntity
import com.softcode.mymagicapp.core.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class, CardEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun cardDao(): CardDao
}
