package com.github.pksokolowski.coroutinesfun.features.standalones

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

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

@FlowPreview
fun <T, R> Flow<T>.mapConcurrently(block: (T) -> R): Flow<R> =
    this.flatMapMerge(Runtime.getRuntime().availableProcessors()) { item ->
        flow {
            emit(block(item))
        }
    }