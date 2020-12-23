package com.github.pksokolowski.coroutinesfun.utils

class CyclicIterator<T>(private val data: Collection<T>) : Iterator<T> {
    private var currentIndex = 0

    override fun hasNext() = data.isNotEmpty()

    override fun next(): T {
        require(data.isNotEmpty()) { "Cyclic iterator cannot output an item out of an empty collection" }
        return data.elementAt(currentIndex).also {
            currentIndex = (currentIndex + 1).rem(data.size)
        }
    }
}

fun <T> Collection<T>.toCyclicIterator() = CyclicIterator(this)