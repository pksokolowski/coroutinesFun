package com.github.pksokolowski.coroutinesfun.features.testable.api

import com.github.pksokolowski.coroutinesfun.features.testable.model.Category
import com.github.pksokolowski.coroutinesfun.features.testable.model.Item
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.math.BigDecimal

class StoreApi(
    private val networkDispatcher: CoroutineDispatcher
) : IStoreApi {
    private val categories = listOf(
        Category(1, "Furniture", 1),
        Category(2, "Literature", 4),
        Category(3, "Home utils", 6),
        Category(4, "Sports", 1),
    )

    private val items = listOf(
        Item(1, 1, "chair", "", "", BigDecimal("12.99")),
        Item(2, 2, "book", "", "", BigDecimal("4.99")),
        Item(3, 3, "hair dryer", "", "", BigDecimal("50.99")),
        Item(4, 3, "wrench", "", "", BigDecimal("3.99")),
        Item(5, 4, "ball", "", "", BigDecimal("0.99")),
    )

    override suspend fun getAllItems() = withContext(networkDispatcher) {
        delay(2500)
        items
    }

    override suspend fun getItemsFromCategory(categoryId: Long) = withContext(networkDispatcher) {
        delay(2000)
        items.filter { it.category_id == categoryId }
    }

    override suspend fun getCategories(): List<Category> {
        delay(120)
        return categories
    }

    override suspend fun getCategoryVersion(categoryId: Long): Long? {
        delay(80)
        return categories.find { it.id == categoryId }?.categoryVersion
    }
}