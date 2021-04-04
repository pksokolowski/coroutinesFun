package com.github.pksokolowski.coroutinesfun.features.testable.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "category_id")
    val category_id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String = "",

    @ColumnInfo(name = "price")
    val price: BigDecimal = BigDecimal.ZERO,

    @ColumnInfo(name = "items_list_version")
    val itemsListVersion: Long,
)