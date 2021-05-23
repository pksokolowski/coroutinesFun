package com.github.pksokolowski.coroutinesfun.features.testable.stubs

import com.github.pksokolowski.coroutinesfun.features.testable.api.IStoreApi
import com.github.pksokolowski.coroutinesfun.features.testable.api.responses.CategoryDto
import com.github.pksokolowski.coroutinesfun.features.testable.model.Item

class StoreApiStub(
    var categories: MutableList<CategoryDto>,
    var items: MutableList<Item>
) : IStoreApi {
    override suspend fun getAllItems(): List<Item> {
        return items
    }

    override suspend fun getCategories(): List<CategoryDto> {
        return categories
    }

    override suspend fun getCategoryVersion(categoryId: Long): Long? {
        return categories.find { it.id == categoryId }?.currentVersion
    }

    override suspend fun getItemsFromCategory(categoryId: Long): List<Item> {
        return items.filter { it.category_id == categoryId }
    }
}