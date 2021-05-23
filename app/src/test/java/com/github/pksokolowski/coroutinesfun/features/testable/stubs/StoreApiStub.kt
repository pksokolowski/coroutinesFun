package com.github.pksokolowski.coroutinesfun.features.testable.stubs

import com.github.pksokolowski.coroutinesfun.features.testable.api.IStoreApi
import com.github.pksokolowski.coroutinesfun.features.testable.api.responses.CategoryDto
import com.github.pksokolowski.coroutinesfun.features.testable.model.Item
import java.math.BigDecimal

class StoreApiStub(
    val hardcodedCategories: MutableList<CategoryDto>,
    val items: MutableList<Item>
) : IStoreApi {
    override suspend fun getAllItems(): List<Item> {
        return items
    }

    override suspend fun getCategories(): List<CategoryDto> {
        return hardcodedCategories
    }

    override suspend fun getCategoryVersion(categoryId: Long): Long? {
        return 12
    }

    override suspend fun getItemsFromCategory(categoryId: Long): List<Item> {
        return items.filter { it.category_id == categoryId }
    }
}