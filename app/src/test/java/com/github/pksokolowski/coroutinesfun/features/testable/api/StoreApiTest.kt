package com.github.pksokolowski.coroutinesfun.features.testable.api

import com.github.pksokolowski.coroutinesfun.features.testable.api.responses.CategoryDto
import com.github.pksokolowski.coroutinesfun.features.testable.model.Item
import com.github.pksokolowski.coroutinesfun.testutils.MainCoroutineRule
import junit.framework.TestCase
import org.junit.Rule
import java.math.BigDecimal

class StoreApiTest : TestCase() {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    val sut = StoreApi(coroutineRule.testDispatcher)

    fun testGetCategories() = coroutineRule.runBlockingTest {
        val categories = sut.getCategories()
        assertEquals(hardcodedCategories, categories)
    }

    fun testGetItemsByCategory() = coroutineRule.runBlockingTest {
        val items = sut.getItemsFromCategory(3)
        val expected = listOf(
            Item(3, 3, "hair dryer", "", "", BigDecimal("50.99")),
            Item(4, 3, "wrench", "", "", BigDecimal("3.99")),
        )
        assertEquals(expected, items)
    }

    private val hardcodedCategories = listOf(
        CategoryDto(1, "Furniture", 1),
        CategoryDto(2, "Literature", 4),
        CategoryDto(3, "Home utils", 6),
        CategoryDto(4, "Sports", 1),
    )

    private val items = listOf(
        Item(1, 1, "chair", "", "", BigDecimal("12.99")),
        Item(2, 2, "book", "", "", BigDecimal("4.99")),
        Item(3, 3, "hair dryer", "", "", BigDecimal("50.99")),
        Item(4, 3, "wrench", "", "", BigDecimal("3.99")),
        Item(5, 4, "ball", "", "", BigDecimal("0.99")),
    )
}