package com.github.pksokolowski.coroutinesfun.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.github.pksokolowski.coroutinesfun.db.dto.AnimalDto
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimalsDao {
    @Query("SELECT * FROM animals WHERE id = :id")
    suspend fun getAnimalById(id: Long): AnimalDto?

    @Query("SELECT id FROM animals ORDER BY id ASC")
    suspend fun getAllKnownAnimalsIds(): List<Long>

    @Query("SELECT id FROM animals ORDER BY id ASC")
    fun getAllKnownAnimalsIdsFlow(): Flow<List<Long>>

    @Insert
    suspend fun insertAnimal(animalDto: AnimalDto): Long
}