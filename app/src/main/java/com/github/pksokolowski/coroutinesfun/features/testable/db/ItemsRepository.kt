package com.github.pksokolowski.coroutinesfun.features.testable.db

import com.github.pksokolowski.coroutinesfun.features.testable.model.Item
import kotlinx.coroutines.CoroutineDispatcher

class ItemsRepository(
    private val itemsDao: ItemsDao,
    private val ioDispatcher: CoroutineDispatcher
) : IItemsRepository {
    suspend fun getItems(): List<Item> = itemsDao.getAllItems()
}