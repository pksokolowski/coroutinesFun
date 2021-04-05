package com.github.pksokolowski.coroutinesfun.features.testable.db

import com.github.pksokolowski.coroutinesfun.features.testable.api.IStoreApi
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

    }

    private suspend fun getFreshCategoryInfo(categoryId: Long) = withContext(ioDispatcher) {
        val cached = categoriesDao.getCategoryById(categoryId)
        val fromApi = storeApi.getCategories()
    }

    private suspend fun refreshCategoriesCache() = withContext(ioDispatcher) {

    }
}