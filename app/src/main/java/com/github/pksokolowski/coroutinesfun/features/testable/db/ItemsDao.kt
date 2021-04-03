package com.github.pksokolowski.coroutinesfun.features.testable.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.github.pksokolowski.coroutinesfun.features.testable.model.Item
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemsDao {
    @Query("SELECT * FROM items ORDER BY id ASC")
    fun getAllItems(): Flow<List<Item>>

    @Insert
    suspend fun insertItem(item: Item): Long

    @Update
    suspend fun updateItem(item: Item)
}