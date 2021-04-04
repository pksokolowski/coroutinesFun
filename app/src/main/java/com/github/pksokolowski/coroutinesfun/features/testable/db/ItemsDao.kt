package com.github.pksokolowski.coroutinesfun.features.testable.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.github.pksokolowski.coroutinesfun.features.testable.model.Item

@Dao
interface ItemsDao {
    @Query("SELECT * FROM items ORDER BY id ASC")
    suspend fun getAllItems(): List<Item>

    @Query("SELECT * FROM items WHERE category_id = :category_id ORDER BY id ASC")
    suspend fun getItemsByCategory(category_id: Long): List<Item>

    @Insert
    suspend fun insertItem(item: Item): Long

    @Update
    suspend fun updateItem(item: Item)
}