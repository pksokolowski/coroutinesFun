package com.github.pksokolowski.coroutinesfun.features.testable.api

import com.github.pksokolowski.coroutinesfun.features.testable.model.Item

interface IItemsApi {
    suspend fun getItems(): List<Item>
}