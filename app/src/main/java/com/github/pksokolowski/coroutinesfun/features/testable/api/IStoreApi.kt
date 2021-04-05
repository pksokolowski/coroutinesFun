package com.github.pksokolowski.coroutinesfun.features.testable.api

import com.github.pksokolowski.coroutinesfun.features.testable.model.Category
import com.github.pksokolowski.coroutinesfun.features.testable.model.Item

interface IStoreApi {
    suspend fun getAllItems(): List<Item>
    suspend fun getCategories(): List<Category>
    suspend fun getCategoryVersion(categoryId: Long): Long?
    suspend fun getItemsFromCategory(categoryId: Long): List<Item>
}