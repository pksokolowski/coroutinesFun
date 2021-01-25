package com.github.pksokolowski.coroutinesfun.utils

import androidx.lifecycle.LifecycleCoroutineScope
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

fun <T> LifecycleCoroutineScope.observe(flow: Flow<T>, block: (T) -> Unit) = launchWhenStarted {
    flow.collect { item ->
        block(item)
    }
}