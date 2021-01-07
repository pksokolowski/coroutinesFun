package com.github.pksokolowski.coroutinesfun.features.standalones

import androidx.lifecycle.ViewModel
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.lang.IllegalStateException
import kotlin.random.Random

class StandAlonesViewModel @ViewModelInject constructor(

) : ViewModel() {

    private val _output = MutableSharedFlow<String>()
    val output: SharedFlow<String> = _output

    private fun output(content: String) {
        viewModelScope.launch {
            _output.emit(content)
        }
    }

    fun runSomeFunCoroutines() {
        suspend fun findSmallestPrime(): Int {
            delay(1500)
            return 2
        }

        suspend fun writeAPoem(): String {
            delay(500)
            return "Lorem ipsum dolor sit amet"
        }

        viewModelScope.launch {
            val primeDeferred = async { findSmallestPrime() }
            val poemDeferred = async { writeAPoem() }

            output("Started work")

            val prime = primeDeferred.await()
            val poem = poemDeferred.await()

            output(
                """
                got both:
                a poem  = $poem
                and a prime  = $prime
            """.trimIndent()
            )
        }
    }

    fun runHandleExceptions(shouldFail: Boolean) {
        fun getUserNameById(id: Long): String {
            if (shouldFail) throw IllegalStateException("ShouldFail was set to true!")
            return "Stefan$id"
        }

        val handler = CoroutineExceptionHandler { coroutineContext, throwable ->
            output("Exception thrown during fake network request: ${throwable.localizedMessage}")
        }

        viewModelScope.launch(Dispatchers.IO + handler) {
            val userName = getUserNameById(1500)
            output("Username = $userName")
        }
    }

    fun withTimeoutSample() {
        output("display subsequent numbers in 1..10 with 100ms delays\nand a 1000 ms timeout\n")
        val numbersFlow = flow {
            for (i in 1..10) {
                delay(100)
                emit(i)
            }
        }
        viewModelScope.launch() {
            withTimeout(600) {
                numbersFlow.collect { output("got number $it") }
            }
        }
    }

    fun transformSample() {
        output("using transform operator on a flow of (1,2,3)\n")
        val flow = listOf(1, 2, 3).asFlow()
            .map {
                delay(100)
                it
            }

        flow
            .flowOn(Dispatchers.Main)
            .transform {
                emit(it.toFloat() - 0.5)
                emit(it)
            }
            .onEach { output(it.toString()) }
            .launchIn(viewModelScope)
            .invokeOnCompletion {
                output("\n you can see new elements were inserted in-between the items")
            }
    }

    fun bufferSample(bufferOn: Boolean) {
        output("buffer accumulates prior values while operators down the chain are free to operate on the older items\n")
        val quickWork = flow {
            for (i in 1..10) {
                delay(200)
                emit(i)
            }
        }

        suspend fun timeConsumingMultiplication(number: Int): Int {
            delay(300)
            return number * 2
        }

        val startTime = System.currentTimeMillis()
        fun showDuration() =
            (System.currentTimeMillis() - startTime).let { output("\ntook : $it ms") }

        if (bufferOn) {
            quickWork
                .buffer()
                .map { timeConsumingMultiplication(it) }
                .onEach { output("received item: $it") }
                .launchIn(viewModelScope)
                .invokeOnCompletion { showDuration() }
        } else {
            quickWork
                //.buffer()
                .map { timeConsumingMultiplication(it) }
                .onEach { output("received item: $it") }
                .launchIn(viewModelScope)
                .invokeOnCompletion { showDuration() }
        }
    }

    fun produceChannelSample() {
        output("create a channel and receive items from it.")

        fun CoroutineScope.produceNumbers() = produce<Int> {
            repeat(5) {
                send(it)
            }
        }

        viewModelScope.launch {
            produceNumbers()
                .consumeEach {
                    output("Consumed from channel: $it")
                }
        }
    }

    /**
     * Notice that the generation of tasks to handle takes approx. 20ms each, and processing
     * each task takes 200 ms, therefore the optimal workers count appears to be 10 for this case,
     * because 200 / 20 = 10.
     */
    fun fanOutSample(workersCount: Int) {
        output("distribute work evenly across multiple coroutines, num of workers: $workersCount")

        val startTime = System.currentTimeMillis()

        fun CoroutineScope.produceNumbers() = produce {
            repeat(5) {
                delay(20)
                send(it)
            }
        }

        fun CoroutineScope.launchProcessor(channel: ReceiveChannel<Int>) =
            launch(Dispatchers.Default) {
                channel.consumeEach { item ->
                    delay(200)
                    val timePassedSoFar = System.currentTimeMillis() - startTime
                    output("processed item: $item; ($timePassedSoFar ms passed since start.)")
                }
            }

        viewModelScope.launch {
            val channel = produceNumbers()
            repeat(workersCount) { launchProcessor(channel) }
        }
    }

    fun sharedResourceAccessSample(useMutex: Boolean) {
        output("accessing shared state from many coroutines. Use mutex = $useMutex")

        val mutex = Mutex()
        // for similar trivial cases, in real-life consider an atomic collection instead of a mutex
        var sharedList = mutableListOf<Int>()

        val iterations = 1000
        val itemsPerIteration = 10

        val handler = CoroutineExceptionHandler { coroutineContext, throwable ->
            output("Exception handled: $throwable")
        }

        viewModelScope.launch(Dispatchers.Default + handler) {
            (1..iterations).map {
                async {
                    if (useMutex) {
                        repeat(itemsPerIteration) { mutex.withLock { sharedList.add(it) } }
                    } else {
                        repeat(itemsPerIteration) { sharedList.add(it) }
                    }
                }
            }.awaitAll()

            output("${sharedList.size} out of ${iterations * itemsPerIteration} arrived")
        }
    }

    fun combineLatestSample() {
        output("combines latest emissions from two flows")

        val flowA = listOf(true, false, false, false, true).emit(300)
        val flowB = listOf(false, true).emit(200)

        combine(flowA, flowB) { a, b ->
            a && b
        }
            .flowOn(Dispatchers.Default)
            .onEach { bothTrue ->
                output(if (bothTrue) "both are true!" else "at least one is false")
            }
            .launchIn(viewModelScope)
    }

    fun handleErrorWithDefaultSample() {
        output("trying an operation which fails, then uses a default value instead for a second operation in chain\n")
        listOf(2).asFlow()
            .map { it / 0 }
            .catch {
                output("first operation failed, using default value instead")
                emit(1)
            }
            .map { it * 2 }
            .onEach { output("result is $it") }
            .launchIn(viewModelScope)
    }

    fun handleErrorOnErrorSwitchToAlternativeSolution(input: Int) {
        suspend fun fastProbabilisticAlgorithm(number: Int): Int {
            delay(100)
            if (Random.nextBoolean()) throw ArithmeticException("Failed to perform the operation")
            return number * 2
        }

        suspend fun slowDeterministicAlgorithm(number: Int): Int {
            delay(1000)
            return number * 2
        }

        flow { emit(input) }
            .map {
                output("Trying a fast, probabilistic algorithm fist")
                fastProbabilisticAlgorithm(it)
            }
            .catch {
                output("Failure, falling back to slow, deterministic solution")
                emit(slowDeterministicAlgorithm(input))
            }
            .onEach {
                output("result is $it")
            }
            .launchIn(viewModelScope)
    }

    fun handleErrorsRetry() {
        output("Tries an iffy connection, if it faile, retries a couple of times\n")

        suspend fun downloadResourceOverIffyConnection(): String {
            delay(100)
            if (Random.nextBoolean()) throw ArithmeticException("Failed to perform the operation")
            return "<some successfully retrieved content>"
        }

        flow { emit(Unit) }
            .map { downloadResourceOverIffyConnection() }
            .retry(4)
            .catch { output("tried 5 times, but failed anyway") }
            .onEach { output("Got: $it") }
            .launchIn(viewModelScope)
    }

    fun <T> List<T>.emit(delay: Long): Flow<T> = flow {
        forEach {
            delay(delay)
            emit(it)
        }
    }
}