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

    @Test
    fun `cached items are used when up to date`() =
        coroutineRule.runBlockingTest {

            storeApi.items = mutableListOf()
            storeApi.categories = mutableListOf(CategoryDto(1, "things", 2))
            categoriesDao.categories = mutableListOf(Category(1, "things", 2, 2))
            itemsDao.items = mutableListOf(Item(1, 1, "thing", "haha", "none", 10.toBigDecimal()))

            val items = sut.getItemsByCategory(1)
            val expectedItems = listOf(
                Item(1, 1, "thing", "haha", "none", 10.toBigDecimal())
            )
            assertEquals(expectedItems, items)
        }

    @Test
    fun `new items are fetched when cache is not up to date`() =
        coroutineRule.runBlockingTest {

            storeApi.items = mutableListOf(
                Item(2, 1, "new thing", "hehe", "none", 20.toBigDecimal())
            )
            storeApi.categories = mutableListOf(CategoryDto(1, "things", 3))
            categoriesDao.categories = mutableListOf(Category(1, "things", 2, 2))
            itemsDao.items = mutableListOf(Item(1, 1, "thing", "haha", "none", 10.toBigDecimal()))

            val items = sut.getItemsByCategory(1)
            val expectedItems = listOf(
                Item(2, 1, "new thing", "hehe", "none", 20.toBigDecimal())
            )
            assertEquals(expectedItems, items)
        }

    @Test
    fun `subsequent request, after cache refresh, uses cache`() =
        coroutineRule.runBlockingTest {

            storeApi.items = mutableListOf(
                Item(2, 1, "new thing", "hehe", "none", 20.toBigDecimal())
            )
            storeApi.categories = mutableListOf(CategoryDto(1, "things", 3))
            categoriesDao.categories = mutableListOf(Category(1, "things", 3, 2))
            itemsDao.items = mutableListOf(Item(1, 1, "thing", "haha", "none", 10.toBigDecimal()))

            sut.getItemsByCategory(1)

            // the subsequent requests
            // first we remove the item from api to be able to detect if it was used trivially.
            storeApi.items.clear()

            val items = sut.getItemsByCategory(1)
            val expectedItems = listOf(
                Item(2, 1, "new thing", "hehe", "none", 20.toBigDecimal())
            )
            assertEquals(expectedItems, items)
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