package com.github.pksokolowski.coroutinesfun.repository

import com.github.pksokolowski.coroutinesfun.model.Animal

interface AnimalsRepository {
    suspend fun getAnimalById(id: Long): Animal?
    suspend fun getAllKnownAnimalsIds(): List<Long>
    suspend fun saveAnimal(animal: Animal)
}