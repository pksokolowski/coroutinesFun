package com.github.pksokolowski.coroutinesfun

import kotlinx.coroutines.*
import org.junit.Assert.assertEquals
import org.junit.Test

private suspend fun funUnderTest(input: Int) = withContext(Dispatchers.Default) {
    delay(100)
    input * 2
}

@ExperimentalCoroutinesApi
class SuspendingFunctionTest {
    @Test
    fun `sample test of a suspend fun`() {
        runBlocking {
            val result = funUnderTest(2)
            assertEquals(4, result)
        }
    }

}