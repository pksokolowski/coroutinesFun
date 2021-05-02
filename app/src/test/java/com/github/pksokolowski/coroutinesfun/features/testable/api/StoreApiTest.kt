package com.github.pksokolowski.coroutinesfun.features.testable.api

import com.github.pksokolowski.coroutinesfun.features.testable.api.responses.CategoryDto
import com.github.pksokolowski.coroutinesfun.testutils.MainCoroutineRule
import junit.framework.TestCase
import org.junit.Rule

class StoreApiTest : TestCase() {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    val sut = StoreApi(coroutineRule.testDispatcher)

    fun testGetCategories() = coroutineRule.runBlockingTest {
        val categories = sut.getCategories()
        assertEquals(hardcodedCategories, categories)
    }

    val hardcodedCategories = listOf(
        CategoryDto(1, "Furniture", 1),
        CategoryDto(2, "Literature", 4),
        CategoryDto(3, "Home utils", 6),
        CategoryDto(4, "Sports", 1),
    )
}