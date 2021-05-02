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
        val category = getFreshCategoryInfo(categoryId)
        val items = itemsDao.getItemsByCategory(categoryId)
    }

    private suspend fun getFreshCategoryInfo(categoryId: Long) = withContext(ioDispatcher) {
        val cached = categoriesDao.getCategoryById(categoryId)

        val currentVersion = storeApi.getCategoryVersion(categoryId) ?: run {
            categoriesDao.delete(categoryId)
            return@withContext null
        }

        if (currentVersion == cached?.cachedVersion) return@withContext cached

        val categoryInfo = storeApi.getCategories()
            .firstOrNull { it.id == categoryId }?.toCategory()
            ?: cached
            ?: return@withContext null

        categoriesDao.insert(categoryInfo)

        categoryInfo
    }

    private suspend fun getCategories() = withContext(ioDispatcher) {
        val cached = categoriesDao.getAllCategories()
        val fromApi = storeApi.getCategories()
        if (cached == fromApi) return@withContext fromApi

        categoriesDao.nukeTable()
        categoriesDao.insert(cached)
        fromApi
    }

    private fun CategoryDto.toCategory(): Category {
        return Category(id, name, currentVersion, 0)
    }
}