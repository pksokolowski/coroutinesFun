package com.github.pksokolowski.coroutinesfun.features.testable.stubs

import com.github.pksokolowski.coroutinesfun.features.testable.db.ItemsDao
import com.github.pksokolowski.coroutinesfun.features.testable.model.Item

class ItemsDaoStub(val items: MutableList<Item> = mutableListOf()) : ItemsDao {
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

    private fun Item.withId(newId: Long) =
        Item(newId, category_id, name, description, icon_address, price)
}