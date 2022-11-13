package com.github.pksokolowski.coroutinesfun.playground

import kotlinx.coroutines.*
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

@ExperimentalCoroutinesApi
class ExceptionsHandlingToyTest {
    @Test
    fun `handling exceptions with a try-catch works when try block is within, not around, a launch()`() {
        runBlocking {
            launch {
                try {
                    throw RuntimeException("Didn't pykło!")
                } catch (e: Exception) {
                }
            }
        }
    }

    @Test
    fun `handling exceptions with a try-catch fails to catch when try block is around a launch()`() {
        try {
            runBlocking {
                try {
                    launch {
                        throw RuntimeException("Didn't pykło!")
                    }
                } catch (e: Exception) {
                }
            }

        } catch (e: Exception) {
            return
        }

        throw RuntimeException("Test failed, expected exception to be caught in the outer try-catch and the return to be called there")
    }

    @Test
    fun `coroutineHandler DOES NOT prevent uncaught exceptions from crashing the app`() {
        val handler = CoroutineExceptionHandler { _, _ ->
            print("This won't even get to run, actually")
        }

        whenBlockIsRan {
            coroutineScope {
                launch(handler) {
                    throw RuntimeException("Didn't pykło!")
                }
            }
        }.thenAppCrashes()
    }

    @Test
    fun `A coroutineExceptionHandler will work when supervisorScope is applied around the coroutine`() {
        val handler = CoroutineExceptionHandler { _, _ ->
            print("This will get to run, though it does not really handle the exception, you still need supervisorScope")
        }

        whenBlockIsRan {
            supervisorScope {
                launch(handler) {
                    throw RuntimeException("Didn't pykło!")
                }
            }
        }.thenAppDoesNotCrash()
    }

    @Test
    fun `supervisor scope alone, without any coroutineExceptionHandler is enough to prevent crashing the app when an exception is thrown within a coroutine`() {
        whenBlockIsRan {
            supervisorScope {
                launch {
                    throw RuntimeException("Didn't pykło!")
                }
            }
        }.thenAppDoesNotCrash()
    }

    @Test
    fun `supervisor scope alone will not suffice for async, unlike it would for launch, the wpp will still crash`() {
        whenBlockIsRan {
            supervisorScope {
                val deferred = async<Int> {
                    throw RuntimeException("Didn't pykło!")
                }
                deferred.await()
            }
        }.thenAppCrashes()
    }

    @Test
    fun `supervisor scope with a coroutineExceptionHandler will not suffice for async, the wpp will still crash`() {
        val handler = CoroutineExceptionHandler { _, _ ->
            print("This will not even run")
        }

        whenBlockIsRan {
            supervisorScope {
                val deferred = async<Int>(handler) {
                    throw RuntimeException("Didn't pykło!")
                }
                deferred.await()
            }
        }.thenAppCrashes()
    }
}

private fun whenBlockIsRan(block: suspend CoroutineScope.() -> Unit): Exception? = try {
    runBlocking {
        block()
    }
    null
} catch (e: Exception) {
    e
}

private fun Exception?.thenAppCrashes() {
    assertNotNull(this)
}

private fun Exception?.thenAppDoesNotCrash() {
    assertNull(this)
}