package com.github.pksokolowski.coroutinesfun.features.standalones

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.transform

fun <T> Flow<T>.filterDoubleTap(periodMillis: Long): Flow<Unit> {
    require(periodMillis > 0) { "Double tap acceptance period should be positive" }
    return flow {
        var lastStamp = -1L
        collect {
            val now = System.currentTimeMillis()
            val sinceLast = now - lastStamp
            lastStamp = now
            if (sinceLast <= periodMillis) {
                emit(Unit)
                lastStamp = -1
            }
        }
    }
}

/**
 * An alternative implementation of the same [filterDoubleTap] operator, here using
 * transform {} instead of an explicit flow { collect { } } combo.
 */
fun <T> Flow<T>.filterDoubleTapAlternative(periodMillis: Long): Flow<Unit> {
    require(periodMillis > 0) { "Double tap acceptance period should be positive" }
    var lastStamp = -1L
    return transform {
        val now = System.currentTimeMillis()
        val sinceLast = now - lastStamp
        lastStamp = now
        if (sinceLast <= periodMillis) {
            emit(Unit)
            lastStamp = -1
        }
    }
}