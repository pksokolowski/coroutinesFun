package com.github.pksokolowski.coroutinesfun.features.testable.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String = "",

    @ColumnInfo(name = "category_version")
    val categoryVersion: Long = 0,

    @ColumnInfo(name = "cached_version")
    val cachedVersion: Long = 0,
)