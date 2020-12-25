package com.github.pksokolowski.coroutinesfun.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.pksokolowski.coroutinesfun.db.dao.AnimalsDao
import com.github.pksokolowski.coroutinesfun.db.dto.AnimalDto

@Database(
    entities = [
        AnimalDto::class,
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun animalsDao(): AnimalsDao
}