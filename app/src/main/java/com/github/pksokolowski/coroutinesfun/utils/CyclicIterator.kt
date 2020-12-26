package com.github.pksokolowski.coroutinesfun.utils

import kotlin.math.min

interface CyclicIterator<T> {
    fun hasNext(): Boolean
    fun next(): T
}

class MutableCyclicIterator<T>(private var data: Collection<T>) : Iterator<T>, CyclicIterator<T> {
    private var currentIndex = -1

    override fun hasNext() = data.isNotEmpty()

    override fun next(): T {
        require(data.isNotEmpty()) { "Cyclic iterator cannot output an item out of an empty collection" }
        currentIndex = (currentIndex + 1).rem(data.size)
        return data.elementAt(currentIndex)
    }

    fun replaceData(newData: Collection<T>) {
        data = newData
        currentIndex = min(currentIndex, data.size - 1)
    }
}

fun <T> Collection<T>.toCyclicIterator(): CyclicIterator<T> = MutableCyclicIterator(this)

fun <T> Collection<T>.toMutableCyclicIterator() = MutableCyclicIterator(this)