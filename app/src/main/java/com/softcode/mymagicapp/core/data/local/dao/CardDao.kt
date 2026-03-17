package com.softcode.mymagicapp.core.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.softcode.mymagicapp.core.data.local.entity.CardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: CardEntity): Long

    @Update
    suspend fun updateCard(card: CardEntity)

    @Delete
    suspend fun deleteCard(card: CardEntity)

    @Query("SELECT * FROM cards WHERE userId = :userId ORDER BY createdAt DESC")
    fun getCardsByUserId(userId: Long): Flow<List<CardEntity>>

    @Query("DELETE FROM cards WHERE userId = :userId")
    suspend fun deleteCardsByUserId(userId: Long)
}
