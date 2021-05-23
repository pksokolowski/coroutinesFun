package com.github.pksokolowski.coroutinesfun.features.testable.stubs

import com.github.pksokolowski.coroutinesfun.features.testable.db.CategoriesDao
import com.github.pksokolowski.coroutinesfun.features.testable.model.Category

class CategoriesDaoStub(var categories: MutableList<Category> = mutableListOf()) : CategoriesDao {
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

    private fun transformCategoryById(id: Long, transformation: Category.() -> Category) {
        val index = categories.indexOfFirst { it.id == id }
        require(index >= 0) { "category doesn't exist, therefore it can't be transformed" }

        val modified = categories[index].transformation()
        categories.removeAt(index)
        categories.add(index, modified)
    }
}