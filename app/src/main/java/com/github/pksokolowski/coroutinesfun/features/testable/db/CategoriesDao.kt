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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(Categories: Category)

    @Query("DELETE FROM categories")
    fun nukeTable()

    @Query("UPDATE categories SET category_version = :currentVersion WHERE id = :id ")
    fun updateCurrentVersion(id: Long, currentVersion: Long)

    @Query("UPDATE categories SET cached_version = :cachedVersion WHERE id = :id ")
    fun updateCachedVersion(id: Long, cachedVersion: Long)

    @Query("UPDATE categories SET cached_version = :cachedVersion WHERE id = :id ")
    fun updateExceptCachedVersion(id: Long, cachedVersion: Long)

    @Query("DELETE FROM categories WHERE id = :id ")
    fun delete(id: Long)
}