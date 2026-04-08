package com.softcode.mymagicapp.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.softcode.mymagicapp.core.data.local.entity.ExchangeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExchangeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExchange(exchange: ExchangeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exchanges: List<ExchangeEntity>)

    @Update
    suspend fun updateExchange(exchange: ExchangeEntity)

    @Query("SELECT * FROM exchanges WHERE proposerId = :userId OR receiverId = :userId ORDER BY createdAt DESC")
    fun getExchangesByUserId(userId: Long): Flow<List<ExchangeEntity>>

    @Query("DELETE FROM exchanges WHERE proposerId = :userId OR receiverId = :userId")
    suspend fun deleteExchangesByUserId(userId: Long)
}
