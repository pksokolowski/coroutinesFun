package com.github.pksokolowski.coroutinesfun.utils

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

@FlowPreview
@ExperimentalCoroutinesApi
class MutableSingleFlowEvent<T> : AbstractFlow<T>() {
    private val callbacks = mutableListOf<(T) -> Unit>()

    private val events = callbackFlow {
        val callback = { item: T ->
            offer(item)
            Unit
        }
        callbacks.add(callback)
        awaitClose { callbacks.remove(callback) }
    }

    fun send(item: T) {
        callbacks.forEach { it(item) }
    }

    override suspend fun collectSafely(collector: FlowCollector<T>) =
        collector.emitAll(events)
}

@FlowPreview
@ExperimentalCoroutinesApi
fun <T> MutableSingleFlowEvent<T>.asFlow() = this as Flow<T>