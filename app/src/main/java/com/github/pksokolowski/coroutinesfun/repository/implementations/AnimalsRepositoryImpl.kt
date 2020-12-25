package com.github.pksokolowski.coroutinesfun.repository.implementations

import com.github.pksokolowski.coroutinesfun.db.dao.AnimalsDao
import com.github.pksokolowski.coroutinesfun.db.dto.AnimalDto
import com.github.pksokolowski.coroutinesfun.model.Animal
import com.github.pksokolowski.coroutinesfun.repository.AnimalsRepository

class AnimalsRepositoryImpl(
    private val animalsDao: AnimalsDao
) : AnimalsRepository {
    override suspend fun getAnimalById(id: Long) = animalsDao.getAnimalById(id).run {
        if (this == null) return@run null
        Animal(name, description)
    }

    override suspend fun saveAnimal(animal: Animal) {
        with(animal) {
            val dto = AnimalDto(0, name, description)
            animalsDao.insertAnimal(dto)
        }
    }

    override suspend fun getAllKnownAnimalsIds() = animalsDao.getAllKnownAnimalsIds()
}