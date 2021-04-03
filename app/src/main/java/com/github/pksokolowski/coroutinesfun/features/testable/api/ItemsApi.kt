package com.github.pksokolowski.coroutinesfun.features.testable.api

import com.github.pksokolowski.coroutinesfun.features.testable.model.Item
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.math.BigDecimal

class ItemsApi(
    private val networkDispatcher: CoroutineDispatcher
) : IItemsApi {
    override suspend fun getItems() = withContext(networkDispatcher) {
        delay(500)
        listOf(
            Item(1, "chair", BigDecimal("12.99")),
            Item(2, "book", BigDecimal("4.99")),
            Item(3, "hair dryer", BigDecimal("50.99")),
            Item(4, "wrench", BigDecimal("3.99")),
            Item(5, "ball", BigDecimal("0.99")),
        )
    }
}