package com.github.pksokolowski.coroutinesfun.features.testable.api

import com.github.pksokolowski.coroutinesfun.features.testable.model.Category
import com.github.pksokolowski.coroutinesfun.features.testable.model.Item

interface IItemsApi {
    suspend fun getItems(): List<Item>
    suspend fun getCategories(): List<Category>
    suspend fun getLastItemsUpdateStamp(): Long
}