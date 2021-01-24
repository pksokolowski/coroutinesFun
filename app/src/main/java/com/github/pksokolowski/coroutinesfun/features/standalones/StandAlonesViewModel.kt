package com.github.pksokolowski.coroutinesfun.features.standalones

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resumeWithException
import kotlin.random.Random
import kotlin.system.measureTimeMillis

@ExperimentalCoroutinesApi
@FlowPreview
class StandAlonesViewModel @ViewModelInject constructor(
    private val backgroundWorkUseCase: BackgroundWorkUseCase,
    private val stressSingleFlowEventUseCase: StressSingleFlowEventUseCase,
) : ViewModel() {

    private val _output = MutableSharedFlow<String>()
    val output: SharedFlow<String> = _output

    private var samplesScope = CoroutineScope(Dispatchers.Main.immediate)
    private var computationScope = CoroutineScope(Dispatchers.Default)

    private fun output(content: String) {
        viewModelScope.launch {
            _output.emit(content)
        }
    }

    fun cancelSampleJobs() {
        samplesScope.coroutineContext.cancelChildren()
        computationScope.coroutineContext.cancelChildren()
    }

    override fun onCleared() {
        super.onCleared()
        // cancelling the utility scopes, this is for the samples to have a "different" scope when
        // needed to try interactions, communication or backpressure management differences if any.
        samplesScope.cancel()
        computationScope.cancel()
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

        samplesScope.launch {
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

        samplesScope.launch(Dispatchers.IO + handler) {
            val userName = getUserNameById(1500)
            output("Username = $userName")
        }
    }

    fun handleCancellation() {
        output(
            """
            start a long running operation and cancel it mid-way
            After the work is done <some important cleanup> is required to be run, think re-enabling 
            some button etc.
            
        """.trimIndent()
        )

        fun someImportantAction() {
            output("<some important cleanup>")
        }

        @Suppress("BlockingMethodInNonBlockingContext")
        suspend fun doWork() = withContext(Dispatchers.Default) {
            repeat(6) {
                // cooperative cancellation, note that isActive check can be done manually for more flexibility
                ensureActive()

                output("still running work...")
                Thread.sleep(1000)
            }
        }

        // changing dispatcher to Main, from samplesScope's Main.immediate, this is because we're
        // already on the Main thread here (samplesScope) so Main.immediate would skip the dispatch
        // and just run the code without adding it to the queue and moving on to the next launch
        // below
        val job = samplesScope.launch(Dispatchers.Main) {
            try {
                doWork()
                output("work finished!")
            } catch (e: CancellationException) {
                output("cancellation exception caught!")
            } finally {
                someImportantAction()
            }
        }

        samplesScope.launch(Dispatchers.Main) {
            delay(2000)
            output("Cancelling the coroutine.")
            job.cancel()
        }
    }

    fun builtInCooperation() {
        output("showcase of delay(), as any built-in suspend fun in coroutines, cooperate on cancellation\n")

        suspend fun getRandomNumber() = withContext(Dispatchers.Default) {
            repeat(10) {
                // notice that there is no isActive check or ensureActive() call
                // just a delay() call
                delay(1000)
                output("still working...")
            }
            4
        }

        val job = samplesScope.launch(Dispatchers.Main) {
            try {
                val number = getRandomNumber()
            } catch (e: CancellationException) {
                output("cancellation exception captured")
            }
        }

        samplesScope.launch(Dispatchers.Main) {
            delay(2000)
            output("cancelling...")
            job.cancel()
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
        samplesScope.launch() {
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
            .launchIn(samplesScope)
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
                .launchIn(samplesScope)
                .invokeOnCompletion { showDuration() }
        } else {
            quickWork
                //.buffer()
                .map { timeConsumingMultiplication(it) }
                .onEach { output("received item: $it") }
                .launchIn(samplesScope)
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

        samplesScope.launch {
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

        samplesScope.launch {
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

        samplesScope.launch(Dispatchers.Default + handler) {
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
            .launchIn(samplesScope)
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
            .launchIn(samplesScope)
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
            .launchIn(samplesScope)
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
            .launchIn(samplesScope)
    }

    fun backPressure() {
        val source = Channel<Int>(3)

        samplesScope.launch {
            for (i in 1..10) {
                delay(100)
                source.send(i)
                output("produced: $i")
            }
        }

        samplesScope.launch {
            source
                .consumeEach {
                    delay(1000)
                    output("handled: $it")
                }
        }
    }

    fun conflateSample() {
        output(
            """
            with a fast emitting source, when stream is intended to just show the latest 
            result in the UI, conflate() operator comes in handy.
            
        """.trimIndent()
        )

        (1..100).asFlow()
            .map {
                delay(10)
                it
            }
            .conflate()
            .onEach {
                delay(1000)
                output("Displaying $it")
            }
            .launchIn(samplesScope)

    }

    private inline fun <T, R> Flow<T>.customOperatorRun(crossinline transform: suspend T.() -> R): Flow<R> =
        transform { value -> return@transform emit(value.transform()) }

    fun customOperatorSimple() {
        (1..10).asFlow()
            .customOperatorRun { toString() }
            .onEach {
                output(it)
            }
            .launchIn(samplesScope)
    }

    fun customOperatorDoubleClick() {
        output("emiting 'click' events faster and faster\n")
        (10 downTo 0).asFlow()
            .onEach {
                output("click!")
                delay(it * 100L)
            }
            .filterDoubleTap(300)
            .onEach { output("Actuated!") }
            .launchIn(samplesScope)
    }

    fun lateToSharedFlow() {
        output("An eagerly started SharedFlow is subscribed to after emissions are over, replay = 2\n")
        (1..10).asFlow()
            .onEach {
                output("Produced $it in hurry!")
            }
            .shareIn(viewModelScope, SharingStarted.Eagerly, replay = 2)
            .onEach {
                output("Consuming $it deliberately...")
                delay(1000)
            }
            .launchIn(samplesScope)
    }

    fun sharedFlowFromAnotherCoroutineScope(useSameCoroutineInstead: Boolean = false) {
        output("shared flow from one scope observer by slow subscriber on another...\n")
        if (useSameCoroutineInstead) output("--- using the same scope for comparison ---\n")
        val scopeToEmitFrom =
            if (useSameCoroutineInstead) samplesScope else computationScope
        (1..10).asFlow()
            .onEach {
                output("Produced $it in hurry!")
            }
            .shareIn(scopeToEmitFrom, SharingStarted.Lazily)
            .onEach {
                output("Consuming $it deliberately...")
                delay(1000)
            }
            .launchIn(samplesScope)
    }

    fun secondSubscriberOfSharedFlow() {
        output("two subscribers are observing one fast sharedFlow all in one scope\n")
        val sharedOne = (1..10).asFlow()
            .onEach {
                output("Produced $it in hurry!")
            }
            .shareIn(samplesScope, SharingStarted.Eagerly, replay = 2)

        sharedOne
            .onEach {
                output("First consumer got $it")
                delay(1000)
            }
            .launchIn(samplesScope)

        sharedOne
            .onEach {
                output("Second consumer got $it")
                delay(1000)
            }
            .launchIn(samplesScope)
    }

    fun sharedWithoutBackpressure() {
        output("creating a sharedFlow with buffer of 1 and DROP_OLDEST policy, then observing slowly 1..10")
        val sharedFlow = (1..10).asFlow()
            .onEach { output("produced $it") }
            .conflate()
            .shareIn(samplesScope, SharingStarted.Lazily, 1)

        sharedFlow
            .onEach {
                delay(1000)
                output("Consumed $it")
            }
            .takeWhile {
                // converting the flow to a completable one
                // notice it is after the onEach, so 10 is first consumed there
                // and here takeWhile is provided with a lambda returning false for 10
                // given the false, takeWhile completes the flow. Needless to say
                // that the sharedFlow upstream stays intact.
                // Overall this works because we know we will get the 10 and don't care to
                // receive any further values, if any appear upstream.
                it < 10
            }
            .launchIn(samplesScope)
            .invokeOnCompletion {
                output("starting a late subscriber...")
                sharedFlow
                    .onEach {
                        delay(500)
                        output("LateOne consumed $it")
                    }
                    .launchIn(samplesScope)
            }

    }

    fun <T> List<T>.emit(delay: Long): Flow<T> = flow {
        forEach {
            delay(delay)
            emit(it)
        }
    }

    fun flatMapMerge() {
        data class Author(val id: Long, val name: String)
        data class Post(val id: Long, val authorId: Long, val content: String)

        val dataSource = hashMapOf<Author, List<Post>>(
            Author(1, "Stefan") to listOf(Post(1, 1, "kopkop"), Post(1, 2, "abc!")),
            Author(2, "Marian") to listOf(Post(3, 2, "Lorem ipsum...")),
        )

        fun getAuthors() = flow {
            val authors = dataSource.keys
            authors.forEach {
                delay(Random.nextLong(100))
                emit(it)
            }
        }

        fun getPosts(author: Author) = flow {
            val posts = dataSource[author]
            posts?.forEach {
                delay(Random.nextLong(100))
                emit(it)
            }
        }

        getAuthors()
            .flowOn(Dispatchers.IO)
            .flatMapMerge { author ->
                getPosts(author)
            }
            .onEach { post ->
                output("got post: $post")
            }
            .launchIn(samplesScope)
    }

    fun executionTime() {
        val channel = Channel<Long>()

        samplesScope.launch {
            val timeTaken = measureTimeMillis {
                output("started a long running operation...")
                delay(3000)
                output("finished the long running operation.")
            }
            channel.offer(timeTaken)
        }

        samplesScope.launch {
            channel.consumeEach { duration ->
                output("New operation duration captured! It took: $duration")
            }
        }

        // note: can also consume it as a flow, like so:
//        channel.consumeAsFlow()
//            .onEach { duration ->
//                output("(as flow) New operation duration captured! It took: $duration")
//            }
//            .launchIn(samplesScope)
    }

    fun runBackgroundWork() {
        samplesScope.launch {
            output("Viewmodel uses a use-case for some background work, rather than dealing with background thread itself")
            val result = backgroundWorkUseCase.computeSomething(1)
            output("result is $result")
        }
    }

    fun mainImmediate() {
        output("showcase of the difference between dispatchers main and main.immediate. ")
        output("Basically, if already on the main thread (which is the case here) main.immediate will make the code run right away, without dispatch, like if there was no coroutine launch\n\n")

        samplesScope.launch(Dispatchers.Main) {
            output("running code in the first coroutine, on main dispatcher\n")
        }

        samplesScope.launch(Dispatchers.Main.immediate) {
            output("running code in the second coroutine, on main.immediate dispatcher.\n")
        }
    }

    fun oneShotToSuspend() {
        output("turning a one-shot api to a suspending function\n")

        fun oneShot(onSuccess: (String) -> Unit, onError: (throwable: Throwable) -> Unit) {
            output("running...")
            Thread.sleep(3000)
            onSuccess("abc")
        }

        suspend fun oneShotSuspending() = withContext(Dispatchers.Default) {
            suspendCancellableCoroutine<String> { continuation ->
                oneShot(
                    onSuccess = { result ->
                        continuation.resume(result,
                            onCancellation = { output("cancelled!") }
                        )
                    },
                    onError = { throwable ->
                        continuation.resumeWithException(throwable)
                    }
                )
            }
        }

        samplesScope.launch {
            val resource = oneShotSuspending()
            output("result = $resource")
        }
    }

    fun nonCancellable() {
        output("running some work that should best be 'atomic' through cancellation\n")
        // parallel lists simulate two systems, like remote api and local cache for example
        val cache = mutableListOf<Int>()
        val api = mutableListOf<Int>()

        suspend fun addNumber(number: Int) = withContext(Dispatchers.Default) {
            output("---starting new addNumber procedure---")
            delay(2000)

            output("beginning to write $number to db and cache...")
            withContext(NonCancellable) {
                api.add(number)
                // the below delay normally cooperates on cancellation, but with the NonCancellable
                // job, it won't interfere. This makes it a bit safer, though in real life still
                // probably a task for another api, like WorkManager.
                delay(2000)
                cache.add(number)
            }

            output("finished work, returning")
            Unit
        }

        val addNumbersJob = samplesScope.launch(Dispatchers.Main) {
            try {
                addNumber(1)
                addNumber(2)
                addNumber(3)
            } catch (e: CancellationException) {
                output("cancellation captured")
            } finally {
                output("\nData saved:\nApi = $api\nCache = $cache")
            }
        }

        samplesScope.launch(Dispatchers.Main) {
            delay(6500)
            addNumbersJob.cancel()
        }

    }

    fun stressSingleFlowEvent() {
        output("Beginning stress test...")
        samplesScope.launch {
            stressSingleFlowEventUseCase.commence(1000, 10, ::output)
        }
    }
}