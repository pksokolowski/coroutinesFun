package com.github.pksokolowski.coroutinesfun.features.testable.api

import com.github.pksokolowski.coroutinesfun.features.testable.api.responses.CategoryDto
import com.github.pksokolowski.coroutinesfun.features.testable.model.Item

interface IStoreApi {
    suspend fun getAllItems(): List<Item>
    suspend fun getCategories(): List<CategoryDto>
    suspend fun getCategoryVersion(categoryId: Long): Long?
    suspend fun getItemsFromCategory(categoryId: Long): List<Item>
}