package com.github.pksokolowski.coroutinesfun.features.standalones

import com.github.pksokolowski.coroutinesfun.utils.MutableSingleFlowEvent
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class StressSingleFlowEventUseCase @Inject constructor() {
    suspend fun commence(eventsCount: Int, subscribersCount: Int, output: (String) -> Unit): Unit =
        withContext(Dispatchers.Default) {
            val event = MutableSingleFlowEvent<Int>()

            val observers = List(subscribersCount) {
                Observer(it, this, event)
            }

            launch(Dispatchers.Default) {
                repeat(eventsCount) {
                    event.send(it)
                }
                delay(5000)
                val eventsObserved = observers.sumBy { it.getEventsCount() }
                output("Events sent = $eventsCount\nTotal observers = $subscribersCount\nEvents observed = $eventsObserved")
                val perfectDelivery = observers.all { it.getEventsCount() == eventsCount }
                if (perfectDelivery) {
                    output("\nEvery observer got exactly $eventsCount events.")
                } else {
                    output("\n!!! Not every observer got exactly $eventsCount events.")
                }
            }
        }

    private class Observer(
        val id: Int,
        scope: CoroutineScope,
        eventsFlow: Flow<Int>
    ) {
        private val receivedEventsCount = AtomicInteger(0)

        init {
            scope.launch(Dispatchers.Default) {
                eventsFlow.collect {
                    receivedEventsCount.incrementAndGet()
                }
            }
        }

        fun getEventsCount() = receivedEventsCount.get()
    }
}