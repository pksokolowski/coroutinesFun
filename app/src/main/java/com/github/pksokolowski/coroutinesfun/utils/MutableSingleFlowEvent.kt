package com.github.pksokolowski.coroutinesfun.utils

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@FlowPreview
class MutableSingleFlowEvent<T> : AbstractFlow<T>() {
    private val scope = CoroutineScope(Dispatchers.Main)
    private val sharedFlow = MutableSharedFlow<T>()

    fun send(item: T) {
        scope.launch {
            sharedFlow.emit(item)
        }
    }

    override suspend fun collectSafely(collector: FlowCollector<T>) {
        collector.emitAll(sharedFlow)
    }
}

@FlowPreview
@ExperimentalCoroutinesApi
fun <T> MutableSingleFlowEvent<T>.asFlow() = this as Flow<T>