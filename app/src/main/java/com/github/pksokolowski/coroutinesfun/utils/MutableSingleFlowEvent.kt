package com.github.pksokolowski.coroutinesfun.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
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

fun <T> Fragment.observe(flow: Flow<T>, block: (T) -> Unit) =
    lifecycleScope.observe(flow, block)

fun <T> AppCompatActivity.observe(flow: Flow<T>, block: (T) -> Unit) =
    lifecycleScope.observe(flow, block)