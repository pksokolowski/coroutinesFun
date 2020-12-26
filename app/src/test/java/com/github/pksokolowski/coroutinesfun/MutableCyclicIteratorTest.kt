package com.github.pksokolowski.coroutinesfun

import com.github.pksokolowski.coroutinesfun.utils.toMutableCyclicIterator
import org.junit.Test

import org.junit.Assert.*

class MutableCyclicIteratorTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    fun `given n items collection is replaced with n+1 items collection, the last item is immediately accessible with next() provided currentItem points at the n-th item`() {
        val dataN = listOf(1, 2, 3)
        val dataN1 = listOf(1, 2, 3, 4)
        val iterator = dataN.toMutableCyclicIterator()

        // should return 1
        iterator.next()
        // should return 2
        iterator.next()
        // should return 3
        iterator.next()

        iterator.replaceData(dataN1)
        val retrievedValue = iterator.next()

        assertEquals(4, retrievedValue)
    }

}