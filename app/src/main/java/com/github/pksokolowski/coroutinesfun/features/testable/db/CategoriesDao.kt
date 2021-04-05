package com.github.pksokolowski.coroutinesfun.features.testable.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.pksokolowski.coroutinesfun.features.testable.model.Category

@Dao
interface CategoriesDao {
    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: Long): Category?

    @Query("SELECT * FROM categories ORDER BY id ASC")
    suspend fun getAllCategories(): List<Category>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(Categories: List<Category>)

    @Query("DELETE FROM categories")
    fun nukeTable()
}