package com.github.pksokolowski.coroutinesfun.features.testable.db

import com.github.pksokolowski.coroutinesfun.features.testable.api.responses.CategoryDto
import com.github.pksokolowski.coroutinesfun.features.testable.model.Category
import com.github.pksokolowski.coroutinesfun.features.testable.model.Item
import com.github.pksokolowski.coroutinesfun.features.testable.stubs.CategoriesDaoStub
import com.github.pksokolowski.coroutinesfun.features.testable.stubs.ItemsDaoStub
import com.github.pksokolowski.coroutinesfun.features.testable.stubs.StoreApiStub
import com.github.pksokolowski.coroutinesfun.testutils.MainCoroutineRule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

class StoreRepositoryTest {

    @Test
    fun `cache is filled with first ever request to get categories`() =
        coroutineRule.runBlockingTest {
            val cats = sut.getCategories()
            val expectedCats = listOf(
                Category(1, "Furniture", 1, -1),
                Category(2, "Literature", 4, -1),
                Category(3, "Home utils", 6, -1),
                Category(4, "Sports", 1, -1),
            )
            assertEquals(expectedCats, cats)
        }

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val storeApi = StoreApiStub(
        mutableListOf(
            CategoryDto(1, "Furniture", 1),
            CategoryDto(2, "Literature", 4),
            CategoryDto(3, "Home utils", 6),
            CategoryDto(4, "Sports", 1),
        ),
        mutableListOf(
            Item(1, 1, "chair", "", "", BigDecimal("12.99")),
            Item(2, 2, "book", "", "", BigDecimal("4.99")),
            Item(3, 3, "hair dryer", "", "", BigDecimal("50.99")),
            Item(4, 3, "wrench", "", "", BigDecimal("3.99")),
            Item(5, 4, "ball", "", "", BigDecimal("0.99")),
        )
    )

    private val itemsDao = ItemsDaoStub()
    private val categoriesDao = CategoriesDaoStub()

    private val sut = StoreRepository(
        storeApi,
        itemsDao,
        categoriesDao,
        coroutineRule.testDispatcher
    )

}