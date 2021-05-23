package com.github.pksokolowski.coroutinesfun.features.testable.db

import com.github.pksokolowski.coroutinesfun.features.testable.api.IStoreApi
import com.github.pksokolowski.coroutinesfun.features.testable.api.responses.CategoryDto
import com.github.pksokolowski.coroutinesfun.features.testable.model.Category
import com.github.pksokolowski.coroutinesfun.features.testable.model.Item
import com.github.pksokolowski.coroutinesfun.testutils.MainCoroutineRule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

class StoreRepositoryTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val sut = StoreRepository(
        storeApi,
        itemsDao,
        categoriesDao,
        coroutineRule.testDispatcher
    )

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

    companion object {
        private val categoriesDao = object : CategoriesDao {
            override suspend fun getCategoryById(id: Long): Category? {
                return categories.find { it.id == id }
            }

            override suspend fun getAllCategories(): List<Category> {
                return categories
            }

            override suspend fun insert(newCategories: List<Category>) {
                categories.addAll(newCategories)
            }

            override suspend fun insert(newCategory: Category) {
                categories.add(newCategory)
            }

            override fun nukeTable() {
                categories.clear()
            }

            override fun updateCurrentVersion(id: Long, currentVersion: Long) {
                transformCategoryById(id) {
                    withCurrentVersion(currentVersion)
                }
            }

            override fun updateCachedVersion(id: Long, cachedVersion: Long) {
                transformCategoryById(id) {
                    withCachedVersion(cachedVersion)
                }
            }

            override fun delete(id: Long) {
                val index = categories.indexOfFirst { it.id == id }
                require(index >= 0) { "category doesn't exist, therefore it can't be updated" }

                categories.removeAt(index)
            }

            val categories = mutableListOf<Category>()

            private fun transformCategoryById(id: Long, transformation: Category.() -> Category) {
                val index = categories.indexOfFirst { it.id == id }
                require(index >= 0) { "category doesn't exist, therefore it can't be transformed" }

                val modified = categories[index].transformation()
                categories.removeAt(index)
                categories.add(index, modified)
            }
        }

        private val itemsDao = object : ItemsDao {
            override suspend fun getAllItems(): List<Item> {
                return items
            }

            override suspend fun getItemsByCategory(category_id: Long): List<Item> {
                return items.filter { it.category_id == category_id }
            }

            override fun removeItemsFromCategory(categoryId: Long) {
                items.removeIf { it.category_id == categoryId }
            }

            override suspend fun insertItems(newItems: List<Item>) {
                var nextId = newItems.maxByOrNull { it.id }?.id ?: 0 + 1
                val newCatItems = newItems
                    .map {
                        it.withId(nextId++)
                    }
                items.addAll(newCatItems)
            }

            override suspend fun updateItem(item: Item) {
                val index = items.indexOfFirst { it.id == item.id }
                require(index >= 0) { "item doesn't exist" }

                items.removeAt(index)
                items.add(index, item)
            }

            val items = mutableListOf<Item>()

            fun Item.withId(newId: Long) =
                Item(newId, category_id, name, description, icon_address, price)
        }

        private val storeApi = object : IStoreApi {
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

            val hardcodedCategories = listOf(
                CategoryDto(1, "Furniture", 1),
                CategoryDto(2, "Literature", 4),
                CategoryDto(3, "Home utils", 6),
                CategoryDto(4, "Sports", 1),
            )

            val items = listOf(
                Item(1, 1, "chair", "", "", BigDecimal("12.99")),
                Item(2, 2, "book", "", "", BigDecimal("4.99")),
                Item(3, 3, "hair dryer", "", "", BigDecimal("50.99")),
                Item(4, 3, "wrench", "", "", BigDecimal("3.99")),
                Item(5, 4, "ball", "", "", BigDecimal("0.99")),
            )
        }
    }
}