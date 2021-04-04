package com.github.pksokolowski.coroutinesfun.features.testable.api

import com.github.pksokolowski.coroutinesfun.features.testable.model.Category
import com.github.pksokolowski.coroutinesfun.features.testable.model.Item
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.math.BigDecimal

class ItemsApi(
    private val networkDispatcher: CoroutineDispatcher
) : IItemsApi {
    override suspend fun getItems() = withContext(networkDispatcher) {
        delay(2500)
        listOf(
            Item(1, 1, "chair", BigDecimal("12.99"), 5),
            Item(2, 2, "book", BigDecimal("4.99"), 5),
            Item(3, 3, "hair dryer", BigDecimal("50.99"), 5),
            Item(4, 3, "wrench", BigDecimal("3.99"), 5),
            Item(5, 4, "ball", BigDecimal("0.99"), 5),
        )
    }

    override suspend fun getCategories(): List<Category> {
        delay(100)
        return listOf(
            Category(1, "Furniture"),
            Category(2, "Literature"),
            Category(3, "Home utils"),
            Category(4, "Sports"),
        )
    }

    override suspend fun getLastItemsUpdateStamp(): Long {
        delay(100)
        return 5
    }
}