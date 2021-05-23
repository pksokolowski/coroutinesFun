package com.github.pksokolowski.coroutinesfun.features.testable.db

import com.github.pksokolowski.coroutinesfun.features.testable.api.IStoreApi
import com.github.pksokolowski.coroutinesfun.features.testable.api.responses.CategoryDto
import com.github.pksokolowski.coroutinesfun.features.testable.model.Category
import com.github.pksokolowski.coroutinesfun.features.testable.model.Item
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class StoreRepository(
    private val storeApi: IStoreApi,
    private val itemsDao: ItemsDao,
    private val categoriesDao: CategoriesDao,
    private val ioDispatcher: CoroutineDispatcher
) : IStoreRepository {
    suspend fun getItems(): List<Item> = itemsDao.getAllItems()

    suspend fun getItemsByCategory(categoryId: Long) = withContext(ioDispatcher) {
        val category = categoriesDao.getCategoryById(categoryId)
        if (category == null || category.cachedVersion != category.categoryVersion) {
            // update cache
            val freshItems = storeApi.getItemsFromCategory(categoryId)
            itemsDao.removeItemsFromCategory(categoryId)
            itemsDao.insertItems(freshItems)
        }
        val items = itemsDao.getItemsByCategory(categoryId)
    }

    suspend fun getCategories() = withContext(ioDispatcher) {
        val freshCats = storeApi.getCategories()
            .map {
                it.toCategory()
            }

        val cachedCats = categoriesDao.getAllCategories()
            .associateBy { it.id }

        val updated = freshCats
            .map {
                val cached = cachedCats[it.id] ?: return@map it
                cached.withCachedVersionRetained(it)
            }

        categoriesDao.nukeTable()
        categoriesDao.insert(updated)

        updated
    }

    private fun CategoryDto.toCategory(): Category {
        return Category(id, name, currentVersion)
    }
}