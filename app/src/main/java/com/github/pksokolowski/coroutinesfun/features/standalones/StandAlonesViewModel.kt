package com.github.pksokolowski.coroutinesfun.features.standalones

import androidx.lifecycle.ViewModel
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.lang.IllegalStateException

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
}