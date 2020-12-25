package com.github.pksokolowski.coroutinesfun.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.github.pksokolowski.coroutinesfun.db.dto.AnimalDto

@Dao
interface AnimalsDao {
    @Query("SELECT * FROM animals WHERE id = :id")
    suspend fun getAnimalById(id: Long): AnimalDto?

    @Insert
    suspend fun insertAnimal(animalDto: AnimalDto): Long
}