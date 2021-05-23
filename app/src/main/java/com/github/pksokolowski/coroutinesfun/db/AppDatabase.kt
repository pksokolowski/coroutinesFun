package com.github.pksokolowski.coroutinesfun.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.github.pksokolowski.coroutinesfun.db.dao.AnimalsDao
import com.github.pksokolowski.coroutinesfun.db.dao.PrimeCandidateDao
import com.github.pksokolowski.coroutinesfun.db.dto.AnimalDto
import com.github.pksokolowski.coroutinesfun.db.dto.PrimeCandidateDto
import com.github.pksokolowski.coroutinesfun.features.testable.db.CategoriesDao
import com.github.pksokolowski.coroutinesfun.features.testable.db.ItemsDao
import com.github.pksokolowski.coroutinesfun.features.testable.model.Category
import com.github.pksokolowski.coroutinesfun.features.testable.model.Item

@Database(
    entities = [
        AnimalDto::class,
        PrimeCandidateDto::class,
        Item::class,
        Category::class
    ],
    version = 4,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun animalsDao(): AnimalsDao
    abstract fun primeCandidatesDao(): PrimeCandidateDao
    abstract fun itemsDao(): ItemsDao
    abstract fun categoriesDao(): CategoriesDao
}