package com.github.pksokolowski.coroutinesfun.utils

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@FlowPreview
@ExperimentalCoroutinesApi
class MutableSingleFlowEvent<T> : AbstractFlow<T>() {
    private val mutex = ReentrantLock()
    private val callbacks = mutableListOf<(T) -> Unit>()

    private val events = callbackFlow {
        val callback = { item: T ->
            offer(item)
            Unit
        }
        mutex.withLock { callbacks.add(callback) }
        awaitClose { mutex.withLock { callbacks.remove(callback) } }
    }

    fun send(item: T) {
        mutex.withLock {
            callbacks.forEach { it(item) }
        }
    }

    override suspend fun collectSafely(collector: FlowCollector<T>) =
        collector.emitAll(events)
}

@FlowPreview
@ExperimentalCoroutinesApi
fun <T> MutableSingleFlowEvent<T>.asFlow() = this as Flow<T>